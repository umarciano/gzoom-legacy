#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Genera i file di import allineati al "codice NEW":
  1) templates/WeMeasureInterface_BS.xlsx  (assegnazioni indicatore->scheda)
  2) POST_IMPORT_FASCE_COMPLETO.sql        (fasce reali per-(UOC+indicatore))

Fonte: foglio "Obiettivi_UOC" (assegnazioni per UOC, con Cd LOCALE + testo + Peso + Range1-4)
       + foglio "Obiettivi" (master: testo indicatore -> "codice NEW" globale)
       + templates/WeRootInterface_BS.xlsx (Codice UOC -> Codice Scheda).

Il join Obiettivi_UOC<->master e Obiettivi_UOC<->DB avviene per TESTO indicatore (il Cd e' locale).

Uso: python genera_import_da_obiettivi.py <Obiettivi.xlsm>
"""
import sys, re, openpyxl

BASE = r"C:\GZOOM\GZOOM_CARDARELLI\workspace\gzoom-legacy\script"
WEROOT = BASE + r"\templates\WeRootInterface_BS.xlsx"
CATALOGO = BASE + r"\templates\IndicatoriCatalogo_BS.xlsx"
OUT_MIS = BASE + r"\templates\WeMeasureInterface_BS.xlsx"
OUT_SQL = BASE + r"\POST_IMPORT_FASCE_COMPLETO.sql"
DATA_IN, DATA_FIN = "01/01/2026", "31/12/2026"
SENT = 999999

# Override fasce per codici con range non parse-abili dalla sorgente (che NON va modificata).
# Regola concordata (2026-08-17). bands = lista (from, thru, factor).
FASCE_OVERRIDE = {
    "A66": [(-SENT, 5.99, 0.0), (6.0, SENT, 100.0)],  # >=6 -> 100%, altrimenti 0%
}

# Alias CdC "sporchi": codice UOC nell'Excel che in ANAGRAFICA GZoom corrisponde a un ALTRO codice
# (stessa unita' fisica). Es.: nell'Excel c'e' BSEA0121 ("Centro grandi ustionati - Chirurgia plastica
# ricostruttiva"), ma in piattaforma quell'unita' e' registrata come BSEA0120 (+ BSEA0120C = comparto,
# stessa valutazione organizzativa). Gli indicatori BSEA0121 dell'Excel vanno quindi sulla scheda BSEA0120.
# Vedi doc 08 §3.3. La sorgente Excel NON va modificata: si rimappa qui in generazione.
UOC_ALIAS = {
    "BSEA0121": "BSEA0120",
}

def norm(s): return "" if s is None else str(s).strip()
def ntext(s): return re.sub(r"\s+", " ", re.sub(r"[^a-z0-9]+", " ", norm(s).lower())).strip()

def parse_cell(raw):
    s = norm(raw)
    if not s: return None
    m = re.search(r"risultato\s*([\d.,]+)\s*%", s, re.I) or re.search(r"([\d.,]+)\s*%\s*$", s)
    if not m: return None
    factor = float(m.group(1).replace(",", "."))
    thr = re.split(r"risultato", s, flags=re.I)[0]
    nums = [float(x.replace(",", ".")) for x in re.findall(r"\d+(?:[.,]\d+)?", thr)]
    if not nums: return None
    low_open = ("<" in thr) or ("\u2264" in thr)
    lb = min(nums) if len(nums) >= 2 else nums[0]
    return {"factor": factor, "lb": lb, "low_open": low_open}

def build_bands(cells):
    bands = [c for c in (parse_cell(x) for x in cells) if c]
    if len(bands) < 2: return None
    bands.sort(key=lambda b: (-1e12 if b["low_open"] else b["lb"]))
    # Fondi bande CONTIGUE con lo STESSO factor: alcune sorgenti spezzano una fascia in due righe
    # con lo stesso risultato (es. "= 10-12% risultato 75%" + "= 13-15% risultato 75%" => 10-15% 75%).
    # Senza questa fusione la coerenza "factor distinti" sotto scarterebbe l'indicatore (es. ST59/C33).
    merged = []
    for b in bands:
        if merged and merged[-1]["factor"] == b["factor"]:
            continue
        merged.append(b)
    bands = merged
    n = len(bands); out = []
    for i, b in enumerate(bands):
        frm = -SENT if i == 0 else bands[i]["lb"]
        thru = SENT if i == n - 1 else round(bands[i + 1]["lb"] - 0.01, 2)
        out.append((frm, thru, b["factor"]))
    if len({f for _, _, f in out}) != len(out): return None
    return out

def sqlstr(s): return "'" + str(s).replace("'", "''") + "'"

def main():
    if len(sys.argv) < 2:
        print("Uso: python genera_import_da_obiettivi.py <Obiettivi.xlsm>"); sys.exit(1)
    src = sys.argv[1]
    wb = openpyxl.load_workbook(src, read_only=True, data_only=True)

    # master: testo -> codice NEW ; codice NEW -> formula (per capire SI_NO)
    wsm = wb["Obiettivi"]; rm = [r for r in wsm.iter_rows(values_only=True)]
    Hm = {norm(c).lower(): i for i, c in enumerate(rm[0])}
    def gm(r, h):
        i = Hm.get(h.lower()); return r[i] if (i is not None and i < len(r)) else None
    text2new = {}; new2formula = {}
    for r in rm[1:]:
        cod = norm(gm(r, "codice new")).upper()
        if not cod: continue
        t = ntext(gm(r, "indicatore"))
        if t and t not in text2new: text2new[t] = cod
        new2formula[cod] = norm(gm(r, "formula"))

    # WeRoot: Codice UOC -> [Codice Scheda]
    wbr = openpyxl.load_workbook(WEROOT, read_only=True, data_only=True)
    wr = [r for r in wbr.active.iter_rows(values_only=True)]
    Hr = {norm(c).lower(): i for i, c in enumerate(wr[0])}
    uoc2sched = {}
    for r in wr[1:]:
        sc = norm(r[Hr["codice scheda"]]); uoc = norm(r[Hr["codice uoc"]]).upper()
        if sc and uoc: uoc2sched.setdefault(uoc, []).append(sc)

    # Catalogo: mappa codice UPPER -> casing ESATTO nel catalogo.
    # Il lookup glAccount dell'import misure e' CASE-SENSITIVE: il "Codice Indicatore"
    # nelle misure DEVE avere la stessa casing dell'account_code del catalogo
    # (es. catalogo 'S45a' vs misure 'S45A' => "No glAccount with condition accountCode='S45A'").
    wbcat = openpyxl.load_workbook(CATALOGO, read_only=True, data_only=True)
    rc = [r for r in wbcat.active.iter_rows(values_only=True)]
    Hc = {norm(c).lower(): i for i, c in enumerate(rc[0])}
    icod = Hc.get("codice indicatore", 0)
    cat_case = {}
    for r in rc[1:]:
        c = norm(r[icod])
        if c: cat_case.setdefault(c.upper(), c)

    # Obiettivi_UOC: righe (UOC, testo, peso, range)
    wsu = wb["Obiettivi_UOC"]; ru = [r for r in wsu.iter_rows(values_only=True)]
    Hu = {norm(c).lower(): i for i, c in enumerate(ru[0])}
    def gu(r, h):
        i = Hu.get(h.lower()); return r[i] if (i is not None and i < len(r)) else None

    mis_rows = []; fasce = []; seen = set()
    skip_nocod = []; skip_sched = []; skip_sinono = 0; skip_fasce = 0
    for r in ru[1:]:
        uoc = norm(gu(r, "cdc")).upper()
        uoc = UOC_ALIAS.get(uoc, uoc)   # rimappa CdC "sporchi" al codice org reale in anagrafica
        # codice NEW AUTOREVOLE letto direttamente dalla riga (colonna "ZZ NUOVO COD"),
        # NON abbinato per testo: il testo dell'indicatore e' condiviso da piu' codici
        # (es. "Percentuale pratiche trattate" = ST28/ST46/ST46B) => il match per testo sbagliava.
        cod = norm(gu(r, "zz nuovo cod"))
        if not uoc or not cod: continue
        scheds = uoc2sched.get(uoc)
        if not scheds:
            skip_sched.append((uoc, cod)); continue
        peso = gu(r, "peso"); peso = "" if peso in (None, "") else peso
        # is_sino "intelligente": e' binario (SI_NO) solo se la Formula e' SI/NO E le fasce NON sono
        # graduate. Alcuni indicatori (es. ST69/ST69b/ST79) hanno Formula "SI/NO" ma fasce percentuali
        # con risultati intermedi (100/75/50/0) => NON sono binari, vanno trattati a valore-diretto+fasce.
        _formula_sino = bool(re.fullmatch(r"si\s*/\s*no", norm(gu(r, "formula di calcolo")), re.I))
        _bprev = build_bands([gu(r, "range1"), gu(r, "range2"), gu(r, "range3"), gu(r, "range4")])
        _graded = bool(_bprev) and any(f not in (0, 100) for _, _, f in _bprev)
        is_sino = _formula_sino and not _graded
        for sc in scheds:
            key = (sc, cod.upper())
            if key in seen: continue
            seen.add(key)
            cod_cat = cat_case.get(cod.upper(), cod)  # casing esatta del catalogo (lookup case-sensitive)
            mis_rows.append([sc, cod_cat, cod_cat, peso, DATA_IN, DATA_FIN])
            # fasce (salvo SI_NO)
            if is_sino:
                skip_sinono += 1; continue
            if cod.upper() in FASCE_OVERRIDE:
                bands = FASCE_OVERRIDE[cod.upper()]
            else:
                bands = build_bands([gu(r, "range1"), gu(r, "range2"), gu(r, "range3"), gu(r, "range4")])
            if not bands:
                skip_fasce += 1; continue
            fasce.append((uoc, cod.upper(), bands))

    # --- WeMeasureInterface_BS.xlsx ---
    out = openpyxl.Workbook(); wo = out.active; wo.title = "WeMeasureInterface_BS"
    wo.append(["Codice Scheda", "Codice Obiettivo", "Codice Indicatore", "Peso", "Data Inizio", "Data Fine"])
    for row in mis_rows: wo.append(row)
    out.save(OUT_MIS)

    # --- POST_IMPORT_FASCE_COMPLETO.sql (match per scheda UOC + account_code = codice NEW) ---
    L = ["-- IMPORT FASCE Performance Strategica (CTX_BS) - keyed su codice NEW.",
         "-- Generato da genera_import_da_obiettivi.py. Scala per-(UOC+indicatore) RNG_<UOC>_<CODICE>.",
         f"-- Coppie fasce: {len(fasce)} | SI_NO saltati: {skip_sinono} | senza fasce valide: {skip_fasce}",
         "BEGIN;"]
    for uoc, cod, bands in fasce:
        rid = f"RNG_{uoc}_{cod}"
        L.append(f"\n-- {uoc} / {cod}")
        # NB: NON cancellare uom_range (le misure la referenziano via FK -> il DELETE fallirebbe al re-run).
        # Creare l'header solo se manca (ON CONFLICT); i valori si possono cancellare/ricreare (non FK dalle misure).
        L.append(f"DELETE FROM uom_range_values WHERE uom_range_id={sqlstr(rid)};")
        L.append("INSERT INTO uom_range (uom_id, uom_range_id, description, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) "
                 f"VALUES ('OTH_SCO', {sqlstr(rid)}, {sqlstr('Fasce '+cod+' '+uoc)}, now(), now(), now(), now()) ON CONFLICT (uom_range_id) DO NOTHING;")
        for seq, (frm, thru, fac) in enumerate(bands):
            L.append("INSERT INTO uom_range_values (uom_range_id, uom_range_values_id, is_positive, from_value, thru_value, range_values_factor, range_values_factor_min, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) "
                     f"VALUES ({sqlstr(rid)}, {sqlstr(rid+'_'+str(seq))}, 'Y', {frm}, {thru}, {fac}, {fac}, now(), now(), now(), now());")
        # Match per CODICE SCHEDA REALE letto dal WeRoot (uoc2sched): cosi' e' ANNO-AGNOSTICO
        # (i codici includono l'anno, es. 2026_OB_PF_STG_<UOC>) e tocca SOLO le schede di quell'anno,
        # lasciando intatto lo storico degli anni precedenti.
        sched_in = ",".join(sqlstr(s) for s in uoc2sched.get(uoc, [])) or "''"
        L.append("UPDATE work_effort_measure wem SET uom_range_id=" + sqlstr(rid) +
                 ", we_score_range_enum_id='WESCORE_DIRECTRANGE', we_score_conv_enum_id='WECONVER_NOCONVERSIO' "
                 "FROM work_effort we, gl_account gl "
                 "WHERE wem.work_effort_id=we.work_effort_id AND wem.gl_account_id=gl.gl_account_id "
                 "AND we.work_effort_type_id='CTX_BS' "
                 f"AND we.source_reference_id IN ({sched_in}) "
                 f"AND upper(gl.account_code)={sqlstr(cod)};")
    L.append("\nCOMMIT;")
    with open(OUT_SQL, "w", encoding="utf-8") as fh:
        fh.write("\n".join(L))

    print(f"WeMeasureInterface_BS.xlsx: {len(mis_rows)} righe (assegnazioni distinte) -> {OUT_MIS}")
    print(f"POST_IMPORT_FASCE_COMPLETO.sql: {len(fasce)} scale -> {OUT_SQL}")
    print(f"Saltate: righe senza codice NEW {len(skip_nocod)} | UOC senza scheda WeRoot {len(skip_sched)} | SI_NO {skip_sinono} | senza fasce {skip_fasce}")
    us = sorted({u for u, _ in skip_sched})
    if us: print("  UOC senza scheda in WeRoot:", us)

if __name__ == "__main__":
    main()
