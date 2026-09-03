#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
GENERATORE dell'import COMPLETO delle fasce Performance Strategica (CTX_BS).

Legge il file sorgente Obiettivi_2026.xlsm (foglio Obiettivi_UOC) e produce uno script SQL
(POST_IMPORT_FASCE_COMPLETO.sql) che, per OGNI coppia (UOC, indicatore), crea una scala
uom_range dedicata (RNG_<UOC>_<CODICE>) con le sue fasce reali e la assegna alla misura
(work_effort_measure) tramite DirectRange.

Motivazione: 43 codici indicatore hanno fasce DIVERSE tra UOC -> serve una scala per-(UOC+codice),
non per-codice. Le fasce nella fonte sono testuali (es. ">= 90% risultato 100%").

L'ARTEFATTO da eseguire e' l'SQL. Questo Python e' solo il generatore (rigenerabile).

Uso:
  python genera_fasce_da_obiettivi.py "<percorso Obiettivi_2026.xlsm>"  [output.sql]
"""
import sys, re, openpyxl

SHEET = "Obiettivi_UOC"
OUT_DEFAULT = "POST_IMPORT_FASCE_COMPLETO.sql"
SENT = 999999

# Correzioni per ERRORI NOTI nella fonte Obiettivi_2026.xlsm che NON si possono correggere all'origine
# (celle con formula). Applicate in lettura. Chiave = (UOC, CODICE); valore = {indice_cella_0based: nuovo_testo}.
# Documentate in analisi performance organizzativa/01-configurazione-obiettivi-e-indicatori.md.
CELL_CORRECTIONS = {
    # E09: la 4a fascia era "< 45%" -> contraddittoria (100% e' gia' "<= 37%", e "<45%" si sovrapporrebbe
    # a tutte le bande). Corretta in "> 45%": indicatore lower-better coerente (<=37 100 / 38-40 75 / 41-45 50 / >45 0).
    ("BSEA14081", "E09"): {3: "> 45% risultato 0%"},
}

def norm(v):
    return "" if v is None else str(v).strip()

def fmt(x):
    """Numero per display: intero senza decimali, decimale con virgola."""
    x = round(float(x), 2)
    return str(int(x)) if x == int(x) else ("%s" % x).replace(".", ",")

def parse_cell(raw):
    """Ritorna (factor, lower_bound, is_lowest, is_highest) o None.
    lower_bound = estremo inferiore numerico della banda; is_lowest/is_highest = banda aperta (< / >=)."""
    s = norm(raw)
    if not s:
        return None
    m = re.search(r"risultato\s*([\d.,]+)\s*%", s, re.I)
    if not m:
        m = re.search(r"([\d.,]+)\s*%\s*$", s)
    if not m:
        return None
    factor = float(m.group(1).replace(",", "."))
    thr = re.split(r"risultato", s, flags=re.I)[0]
    nums = [float(x.replace(",", ".")) for x in re.findall(r"\d+(?:[.,]\d+)?", thr)]
    if not nums:
        return None
    low_open = ("<" in thr) or ("≤" in thr) or ("minore" in thr.lower())   # < / <=  -> banda piu' bassa
    high_open = (">" in thr) or ("≥" in thr) or ("magg" in thr.lower())     # > / >=  -> banda piu' alta
    if len(nums) >= 2:
        lb = min(nums)
    else:
        lb = nums[0]
    # ">X" STRETTO (non ">=" / "≥"): la banda superiore (tipicamente 0%) parte SOPRA X, quindi X deve
    # restare nella banda PRECEDENTE (es. Excel "186-190 -> 50%", ">190 -> 0%" => 190 vale 50%, non 0%).
    # Bumpo lb di +0.01: build_bands calcola thru_precedente = (X+0.01)-0.01 = X e from_banda = X+0.01.
    # NB: il bordo inferiore "<X" NON serve bumparlo: build_bands lo deriva dal lb della banda successiva.
    gt_strict = high_open and (">" in thr) and ("≥" not in thr) and (">=" not in thr) and ("uguale" not in thr.lower())
    if gt_strict:
        lb = round(lb + 0.01, 2)
    # --- descr: stringa "Fascia" FEDELE all'Excel, calcolata dal testo originale (operatore + soglia + %) ---
    # Cosi' il display non deve ricostruire nulla da from/thru (che perdono la soglia esatta e l'operatore).
    pct = "%" if "%" in thr else ""
    le_incl = ("≤" in thr) or ("<=" in thr) or ("minore o ug" in thr.lower())
    ge_incl = ("≥" in thr) or (">=" in thr) or ("magg. o ug" in thr.lower()) or ("uguale" in thr.lower())
    # NB: solo ASCII (<= / >=) nelle stringhe: il DB e' WIN1252 e NON contiene i simboli ≤/≥ (verrebbero
    # storati come mojibake "â‰¤"). "<=" e ">=" sono leggibili e sicuri.
    if low_open:
        descr = ("<= " if le_incl else "< ") + fmt(min(nums)) + pct
    elif high_open:
        descr = (">= " if ge_incl else "> ") + fmt(max(nums) if len(nums) > 1 else nums[0]) + pct
    elif len(nums) >= 2:
        descr = fmt(min(nums)) + pct + " - " + fmt(max(nums)) + pct
    else:
        descr = fmt(nums[0]) + pct
    return {"factor": factor, "lb": lb, "low_open": low_open, "high_open": high_open,
            "gt_strict": gt_strict, "descr": descr, "raw": s}

def build_bands(cells):
    """Da 2-4 celle-fascia costruisce bande contigue [from,thru,factor] con sentinelle e .99.
    Ordina per estremo inferiore; la banda 'low_open' (< ) parte da -SENT, la 'high_open' (>=) arriva a +SENT."""
    bands = [c for c in (parse_cell(x) for x in cells) if c]
    if len(bands) < 2:
        return None, "meno di 2 fasce parse-abili"
    # ordina per lower bound; la banda low_open (<) va comunque per prima
    def sortkey(b):
        return (-1e12 if b["low_open"] else b["lb"])
    bands.sort(key=sortkey)
    n = len(bands)
    out = []
    for i, b in enumerate(bands):
        frm = -SENT if (i == 0) else bands[i]["lb"]
        if i == n - 1:
            thru = SENT
        else:
            nxt = bands[i + 1]
            thru = round(nxt["lb"] - 0.01, 2)
        out.append((frm, thru, b["factor"], b["descr"]))
    # coerenza: factor distinti
    if len({f for _, _, f, _ in out}) != len(out):
        return None, "factor duplicati"
    return out, None

def sqlstr(s):
    return "'" + s.replace("'", "''") + "'"

def main():
    if len(sys.argv) < 2:
        print("Uso: python genera_fasce_da_obiettivi.py <Obiettivi.xlsm> [out.sql]"); sys.exit(1)
    xlsx = sys.argv[1]
    out = sys.argv[2] if len(sys.argv) > 2 else OUT_DEFAULT
    wb = openpyxl.load_workbook(xlsx, read_only=True, data_only=True)
    ws = wb[SHEET]
    rows = [r for r in ws.iter_rows(values_only=True)]
    hdr = [norm(c).lower() for c in rows[0]]
    idx = {h: i for i, h in enumerate(hdr)}
    def g(r, h):
        i = idx.get(h.lower()); return r[i] if (i is not None and i < len(r)) else None

    generated = []   # (uoc, code, bands)
    skipped = []     # (uoc, code, motivo)
    seen = set()
    for r in rows[1:]:
        # Codice indicatore: usare "ZZ NUOVO COD" (nuovo codice, es. ST15) che e' quello usato da
        # gl_account.account_code e dagli id RNG_<UOC>_<CODICE> nel DB. La colonna "Cd" ora contiene il
        # vecchio codice legacy (es. c03) e NON va usata (romperebbe l'aggancio alle WorkEffortMeasure).
        cd = norm(g(r, "zz nuovo cod")).upper() or norm(g(r, "cd")).upper()
        uoc = norm(g(r, "cdc")).upper()
        if not cd or not uoc:
            continue
        formula = norm(g(r, "formula di calcolo"))
        if re.fullmatch(r"si\s*/\s*no", formula.strip(), re.I):
            skipped.append((uoc, cd, "SI_NO (nessuna scala numerica)")); continue
        cells = [g(r, "range1"), g(r, "range2"), g(r, "range3"), g(r, "range4")]
        corr = CELL_CORRECTIONS.get((uoc, cd))
        if corr:
            cells = [corr.get(k, cells[k]) for k in range(4)]
        bands, err = build_bands(cells)
        key = (uoc, cd)
        if err:
            skipped.append((uoc, cd, err)); continue
        if key in seen:
            continue  # gia' generato (righe duplicate stessa UOC+codice)
        seen.add(key)
        generated.append((uoc, cd, bands))

    lines = []
    lines.append("-- ============================================================")
    lines.append("-- IMPORT COMPLETO FASCE Performance Strategica (CTX_BS)")
    lines.append("-- Generato da genera_fasce_da_obiettivi.py leggendo Obiettivi_UOC.")
    lines.append("-- Scala per-(UOC+codice) RNG_<UOC>_<CODICE>, DirectRange. Idempotente.")
    lines.append(f"-- Coppie generate: {len(generated)} | saltate: {len(skipped)}")
    lines.append("-- ============================================================")
    lines.append("BEGIN;")
    for uoc, cd, bands in generated:
        rid = f"RNG_{uoc}_{cd}"
        lines.append(f"\n-- {uoc} / {cd}")
        # NB: NON cancellare l'header uom_range (le work_effort_measure lo referenziano via FK wm_uorn:
        # un DELETE fallirebbe alla ri-esecuzione). Rinfreschiamo solo le fasce (uom_range_values) e
        # garantiamo l'esistenza dell'header con ON CONFLICT DO NOTHING. Cosi' lo script e' ri-eseguibile.
        lines.append(f"DELETE FROM uom_range_values WHERE uom_range_id={sqlstr(rid)};")
        desc = f"Fasce {cd} {uoc}"
        lines.append(
            "INSERT INTO uom_range (uom_id, uom_range_id, description, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) "
            f"VALUES ('OTH_SCO', {sqlstr(rid)}, {sqlstr(desc)}, now(), now(), now(), now()) ON CONFLICT (uom_range_id) DO NOTHING;")
        for seq, (frm, thru, fac, descr) in enumerate(bands):
            vid = f"{rid}_{seq}"
            lines.append(
                "INSERT INTO uom_range_values (uom_range_id, uom_range_values_id, is_positive, from_value, thru_value, range_values_factor, range_values_factor_min, comments, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) "
                f"VALUES ({sqlstr(rid)}, {sqlstr(vid)}, 'Y', {frm}, {thru}, {fac}, {fac}, {sqlstr(descr)}, now(), now(), now(), now());")
        lines.append(
            "UPDATE work_effort_measure wem SET uom_range_id=" + sqlstr(rid) +
            ", we_score_range_enum_id='WESCORE_DIRECTRANGE', we_score_conv_enum_id='WECONVER_NOCONVERSIO' "
            "FROM work_effort we, gl_account gl "
            "WHERE wem.work_effort_id=we.work_effort_id AND wem.gl_account_id=gl.gl_account_id "
            "AND we.work_effort_type_id='CTX_BS' "
            f"AND we.source_reference_id IN ('OB_STG_{uoc}','OB_PF_STG_{uoc}') "
            f"AND upper(gl.account_code)={sqlstr(cd)};")
    lines.append("\nCOMMIT;")
    # report finale
    lines.append("\n-- ---- SALTATE (nessuna scala creata) ----")
    for uoc, cd, mot in skipped:
        lines.append(f"--   {uoc}/{cd}: {mot}")

    with open(out, "w", encoding="utf-8") as fh:
        fh.write("\n".join(lines))
    print(f"OK -> {out}")
    print(f"Coppie (UOC,codice) con fasce generate: {len(generated)}")
    print(f"Saltate: {len(skipped)}  (di cui SI_NO: {sum(1 for _,_,m in skipped if m.startswith('SI_NO'))})")
    # anteprima motivi skip non-SI_NO
    other = [(u,c,m) for u,c,m in skipped if not m.startswith('SI_NO')]
    if other:
        print("Skip non-SI_NO (primi 15):")
        for u,c,m in other[:15]: print(f"   {u}/{c}: {m}")

if __name__ == "__main__":
    main()
