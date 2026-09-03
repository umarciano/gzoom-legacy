#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
RICONCILIA il catalogo indicatori (IndicatoriCatalogo_BS.xlsx) col master "codice NEW"
del foglio "Obiettivi" di Obiettivi_2026.xlsm.

- Codice indicatore = "codice NEW" (codice globale veritiero, ~376 quasi-biiettivo).
- Mantiene le righe gia' presenti nel catalogo; AGGIUNGE quelle mancanti (codice NEW non presente).
- Le mappe Area(nome->codice) e Referente(nome->codice) sono IMPARATE dagli indicatori gia' in
  comune (data-driven), poi applicate ai nuovi. Tipologia derivata dalla Formula.

Output: IndicatoriCatalogo_BS_reconciled.xlsx  (+ report a console).
Uso: python riconcilia_catalogo_indicatori.py <Obiettivi.xlsm>
"""
import sys, re, collections, openpyxl

CAT = r"C:\GZOOM\GZOOM_CARDARELLI\workspace\gzoom-legacy\script\templates\IndicatoriCatalogo_BS.xlsx"
OUT = r"C:\GZOOM\GZOOM_CARDARELLI\workspace\gzoom-legacy\script\templates\IndicatoriCatalogo_BS_reconciled.xlsx"

def norm(s): return "" if s is None else str(s).strip()
def n2(s): return re.sub(r"\s+", " ", str(s).strip().lower()) if s is not None else ""
def _nopar(s): return re.sub(r"\(.*?\)", "", s).strip()
def _nouoc(s): return re.sub(r"^(uoc|uosd|uos|uo)\s+", "", s).strip()

# La connessione DB dell'import gira in WIN1252/LATIN1: caratteri fuori da quel set
# (es. i simboli matematici) fanno fallire l'INSERT del gl_account. Sanitizziamo qui.
_WIN1252_MAP = {"≤": "<=", "≥": ">=", "≠": "<>",
                "→": "->", "⇒": "=>", " ": " "}

def win1252_safe(s):
    if not isinstance(s, str):
        return s
    out = []
    for ch in s:
        try:
            ch.encode("cp1252"); out.append(ch)
        except UnicodeEncodeError:
            out.append(_WIN1252_MAP.get(ch, "?"))
    return "".join(out)

# Override per codici la cui natura NON e' derivabile dalla sola formula (refusi/eccezioni della
# sorgente, che NON va modificata). Vedi analisi 2026-08-17 (classificazione-indicatori.csv).
TIPO_OVERRIDE = {
    "A04":  "A/B*100",       # e' una percentuale ("Percentuale di pazienti..."), ma nel master manca il '%' nel Target
    "S35":  "",              # conteggio: n. certificazioni rilasciate (fasce =4/=3/=2), non un rapporto
    "E24":  "",              # formula custom 1-(A/B) (riduzione %): nessun tipo standard -> valore diretto (si inserisce la % gia' calcolata)
    "ST76": "SUM(A)",        # composite: somma conteggi
    "A111": "(A-B)/B*100",   # composite: variazione %
    "A52":  "(A-B)/B*100",   # variazione ((DH24-DH25)/DH25), Target % -> non e' un semplice A/B*100
    "A55":  "A/B*100",       # composite (percentuale)
    "A58B": "A/B*100",       # composite (percentuale)
    "ST77": "A/B*100",       # composite (percentuale)
    # Formula "SI/NO" nel master MA fasce percentuali graduate (100/75/50/0) -> NON binari:
    # valore diretto ("") con scala a fasce. Il referente inserisce la % / il conteggio.
    "ST69": "",              # % completamento formazione FSE (100/99-85/84-75/<75)
    "ST69B": "",             # variante di ST69
    "ST79": "",              # conteggio (target 18; >=18/17-16/15-14/<14)
}

def tipologia(cod, formula, target):
    """Tipo indicatore dalla formula + Target del master.
    - Target 'SI'/'NO' => esito SI_NO (autoritativo, anche se la formula contiene '/').
    - num/den: '%' nel Target => percentuale (A/B*100); senza '%' => rapporto assoluto (A/B)."""
    c = norm(cod).upper()
    if c in TIPO_OVERRIDE:
        return TIPO_OVERRIDE[c]
    f = norm(formula).lower()
    t = norm(target)
    if t.upper() in ("SI", "SÌ", "NO"): return "SI_NO"   # Target esito -> SI_NO
    if not f: return ""
    if re.fullmatch(r"si\s*/\s*no", f): return "SI_NO"
    if "/" in f:
        return "A/B*100" if "%" in t else "A/B"
    if "somma" in f or f.startswith("sum") or "sommatoria" in f: return "SUM(A)"
    return ""

def main():
    if len(sys.argv) < 2:
        print("Uso: python riconcilia_catalogo_indicatori.py <Obiettivi.xlsm>"); sys.exit(1)
    src = sys.argv[1]

    # --- master "Obiettivi" ---
    wb = openpyxl.load_workbook(src, read_only=True, data_only=True)
    ws = wb["Obiettivi"]; rows = [r for r in ws.iter_rows(values_only=True)]
    H = {norm(c).lower(): i for i, c in enumerate(rows[0])}
    def g(r, h):
        i = H.get(h.lower()); return r[i] if (i is not None and i < len(r)) else None
    master = {}  # codiceNEW -> dict
    for r in rows[1:]:
        cod = norm(g(r, "codice new")).upper()
        if not cod or cod in master: continue
        master[cod] = {
            "indicatore": norm(g(r, "indicatore")),
            "descr": norm(g(r, "obiettivo")),
            "area_name": norm(g(r, "descr. area")),
            "formula": norm(g(r, "formula")),
            "target": norm(g(r, "target")),
            "fonte": norm(g(r, "fonte")),
            "ref_name": norm(g(r, "referente")),
        }

    # --- mappa UOC autorevole dal foglio "CdC": STRUTTURA/unità -> codice CdC (parent_role_code) ---
    # Serve a risolvere il Referente testuale del master (es. "UOC Ufficio Legale e Avvocatura (ULA)")
    # al codice UOC, ANCHE per UOC non ancora presenti nel catalogo (che la mappa "imparata" sotto NON
    # copriva -> referente lasciato vuoto -> nessun WEM_IND_IN_CHARGE all'import).
    cdc_struct = {}; cdc_unita = {}
    if "CdC" in wb.sheetnames:
        for cr in wb["CdC"].iter_rows(min_row=2, values_only=True):
            code = norm(cr[0]) if len(cr) > 0 else ""
            if not code:
                continue
            st = n2(cr[2]) if len(cr) > 2 else ""
            un = n2(cr[1]) if len(cr) > 1 else ""
            if st:
                cdc_struct.setdefault(st, code); cdc_struct.setdefault(n2(_nopar(st)), code)
            if un:
                cdc_unita.setdefault(un, code); cdc_unita.setdefault(n2(_nouoc(un)), code)

    # --- catalogo esistente ---
    wbc = openpyxl.load_workbook(CAT)
    wsc = wbc.active
    hdr = [norm(c.value) for c in wsc[1]]
    ci = {h.lower(): i for i, h in enumerate(hdr)}
    existing_rows = []
    existing_codes = set()
    for row in wsc.iter_rows(min_row=2, values_only=True):
        if not row or not norm(row[0]): continue
        existing_rows.append(list(row))
        existing_codes.add(norm(row[0]).upper())

    # --- mappe IMPARATE dagli indicatori in comune (master ∩ catalogo) ---
    area_map = collections.Counter(); ref_map = collections.Counter()
    areaByName = {}; refByName = {}
    idx_area = ci.get("area"); idx_ref = ci.get("codice uoc referente")
    for row in existing_rows:
        cod = norm(row[0]).upper()
        if cod in master:
            an = master[cod]["area_name"].lower()
            rn = master[cod]["ref_name"].lower()
            ac = norm(row[idx_area]) if idx_area is not None else ""
            rc = norm(row[idx_ref]) if idx_ref is not None else ""
            if an and ac: areaByName.setdefault(an, collections.Counter())[ac] += 1
            if rn and rc: refByName.setdefault(rn, collections.Counter())[rc] += 1
    # scegli il codice piu' frequente per ciascun nome
    areaByName = {k: v.most_common(1)[0][0] for k, v in areaByName.items()}
    refByName = {k: v.most_common(1)[0][0] for k, v in refByName.items()}

    # Risolutore referente: PRIMA la mappa autorevole CdC (copre le UOC nuove), POI la mappa imparata.
    # I referenti GENERICI ("Capo dipartimento", "Direzione Strategica", "Direzione UOC", ...) e i
    # multi-UOC ("A/B") NON si risolvono qui di proposito: vanno chiariti col cliente (non una UOC univoca).
    def resolve_ref(name):
        x = n2(name)
        if not x:
            return ""
        for cand in (cdc_struct.get(x), cdc_struct.get(n2(_nopar(x))),
                     cdc_unita.get(x), cdc_unita.get(n2(_nouoc(_nopar(x)))), cdc_unita.get(n2(_nouoc(x)))):
            if cand:
                return cand
        return refByName.get(name.lower(), "")

    # --- completa il referente MANCANTE sulle righe GIA' nel catalogo (blank -> risolto via CdC) ---
    filled_existing = 0
    if idx_ref is not None:
        for row in existing_rows:
            cod = norm(row[0]).upper()
            cur = norm(row[idx_ref]) if idx_ref < len(row) else ""
            if cod in master and not cur:
                code = resolve_ref(master[cod]["ref_name"])
                if code:
                    while len(row) <= idx_ref:
                        row.append("")
                    row[idx_ref] = code
                    filled_existing += 1

    # --- costruisci righe per i codice NEW mancanti ---
    missing = [c for c in master if c not in existing_codes]
    new_rows = []
    ref_unresolved = 0; area_unresolved = 0
    for cod in sorted(missing):
        m = master[cod]
        area = areaByName.get(m["area_name"].lower(), "")
        ref = resolve_ref(m["ref_name"])
        if not area and m["area_name"]: area_unresolved += 1
        if not ref and m["ref_name"]: ref_unresolved += 1
        # ordine colonne = hdr: Codice Indicatore, Indicatore, Descrizione sintetica, Tipologia, Area, Codice UOC Referente, Fonte
        rowmap = {
            "codice indicatore": cod,
            "indicatore": m["indicatore"],
            "descrizione sintetica": m["descr"],
            "tipologia": tipologia(cod, m["formula"], m["target"]),
            "area": area,
            "codice uoc referente": ref,
            "fonte": m["fonte"],
        }
        new_rows.append([rowmap.get(h.lower(), "") for h in hdr])

    # --- scrivi output (esistenti + nuovi) ---
    out = openpyxl.Workbook(); wo = out.active; wo.title = wsc.title
    wo.append([win1252_safe(h) for h in hdr])
    for r in existing_rows: wo.append([win1252_safe(v) for v in r])
    for r in new_rows: wo.append([win1252_safe(v) for v in r])
    out.save(OUT)

    print(f"Master codice NEW: {len(master)} | catalogo esistente: {len(existing_codes)} | AGGIUNTI: {len(new_rows)}")
    print(f"Totale catalogo riconciliato: {len(existing_rows)+len(new_rows)} righe -> {OUT}")
    print(f"Referenti MANCANTI completati su righe esistenti (via CdC): {filled_existing}")
    print(f"Nuovi con Area non risolta: {area_unresolved} | Referente non risolto: {ref_unresolved} (generici/multi-UOC: da chiarire col cliente)")
    print(f"Mappe imparate: aree {len(areaByName)}, referenti {len(refByName)}")

if __name__ == "__main__":
    main()
