#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Genera POST_IMPORT_PARAMETRI_INDICATORI.sql: definisce, per ogni indicatore (codice NEW),
i PARAMETRI che il referente dovra' inserire nella modale di consuntivazione.

Fonte: foglio "Obiettivi_UOC", colonna "Formula di calcolo" (verbale), per codice NEW ("ZZ NUOVO COD").
Regole:
  - formula num/den (un solo "/"): 2 parametri -> A = numeratore (testo prima di "/"),
    B = denominatore (testo dopo "/");
  - valore diretto (nessun "/", non SI/NO): 1 parametro (A) con etichetta = testo formula;
  - SI/NO: nessun parametro numerico (la modale mostra un Si/No);
  - composite (>=2 "/"): NON generate automaticamente (ambigue) -> elencate a fine run per gestione manuale.

Modello nativo (verificato): gl_fiscal_type PAR_<COD>_<seq> (description = etichetta mostrata) +
gl_account_input_calc (gl_account_id = indicatore, gl_fiscal_type_id = PAR_, factor_calculator A/B,
input_sequence_num, gl_account_id_ref = NULL). La modale legge queste righe per sapere cosa chiedere.

Uso: python genera_parametri_indicatori.py <Obiettivi.xlsm>
"""
import sys, re, openpyxl

BASE = r"C:\GZOOM\GZOOM_CARDARELLI\workspace\gzoom-legacy\script"
OUT_SQL = BASE + r"\POST_IMPORT_PARAMETRI_INDICATORI.sql"

def norm(s): return "" if s is None else str(s).strip()
def sqlstr(s): return "'" + str(s).replace("'", "''") + "'"
def sane(cod): return re.sub(r"[^A-Za-z0-9]", "", cod).upper()

def main():
    if len(sys.argv) < 2:
        print("Uso: python genera_parametri_indicatori.py <Obiettivi.xlsm>"); sys.exit(1)
    wb = openpyxl.load_workbook(sys.argv[1], read_only=True, data_only=True)
    ws = wb["Obiettivi_UOC"]; rows = [r for r in ws.iter_rows(values_only=True)]
    H = {norm(c).lower(): i for i, c in enumerate(rows[0])}
    def g(r, h):
        i = H.get(h.lower()); return r[i] if (i is not None and i < len(r)) else None

    # per codice NEW: formula (prima occorrenza)
    per = {}
    for r in rows[1:]:
        cod = norm(g(r, "zz nuovo cod"))
        if not cod or cod in per: continue
        per[cod] = norm(g(r, "formula di calcolo"))

    numden = []      # (cod, num, den)
    diretto = []     # (cod, label)
    sino = 0
    composite = []   # (cod, formula)  -> manuale
    for cod, f in per.items():
        fl = f.lower().replace(" ", "")
        if not f or fl in ("si/no", "sì/no"):
            sino += 1 if f else 0
            continue
        ns = f.count("/")
        if ns == 1:
            a, b = f.split("/")
            numden.append((cod, a.strip(), b.strip()))
        elif ns == 0:
            diretto.append((cod, f))
        else:
            composite.append((cod, f))

    L = ["-- PARAMETRI INDICATORI (modale consuntivazione) - generato da genera_parametri_indicatori.py",
         "-- gl_fiscal_type PAR_<COD>_<seq> (etichetta) + gl_account_input_calc (definizione input per indicatore).",
         f"-- num/den: {len(numden)} indicatori (2 param) | valore diretto: {len(diretto)} (1 param) | SI/NO: {sino} (0) | composite manuali: {len(composite)}",
         "SET client_encoding TO 'UTF8';  -- le etichette contengono accenti (à è ù): il file è UTF-8",
         "BEGIN;", ""]

    def emit(cod, params):
        # params = list di (seq, factor, label)
        c = sane(cod)
        gaid = f"(SELECT gl_account_id FROM gl_account WHERE upper(account_code)={sqlstr(c)} AND gl_account_type_id='WECAL')"
        L.append(f"-- {cod}")
        L.append(f"DELETE FROM gl_account_input_calc WHERE gl_account_id IN (SELECT gl_account_id FROM gl_account WHERE upper(account_code)={sqlstr(c)} AND gl_account_type_id='WECAL');")
        for seq, fac, label in params:
            pid = f"PAR_{c}_{seq}"
            L.append(f"DELETE FROM gl_fiscal_type WHERE gl_fiscal_type_id={sqlstr(pid)};")
            L.append("INSERT INTO gl_fiscal_type (gl_fiscal_type_id, description, gl_fiscal_type_enum_id, is_financial_used, is_account_used, is_indicator_used, created_by_user_login, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) "
                     f"VALUES ({sqlstr(pid)}, {sqlstr(label[:255])}, 'GLFISCTYPE_ACTUAL', 'N', 'N', 'Y', 'admin', now(), now(), now(), now());")
            L.append("INSERT INTO gl_account_input_calc (gl_account_input_calc_id, gl_account_id, input_sequence_num, gl_account_id_ref, factor_calculator, gl_fiscal_type_id, created_by_user_login, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) "
                     f"SELECT {sqlstr('IC_'+c+'_'+str(seq))}, gl_account_id, {sqlstr(str(seq))}, NULL, {sqlstr(fac)}, {sqlstr(pid)}, 'admin', now(), now(), now(), now() "
                     f"FROM gl_account WHERE upper(account_code)={sqlstr(c)} AND gl_account_type_id='WECAL';")
        L.append("")

    for cod, num, den in numden:
        emit(cod, [(1, 'A', num), (2, 'B', den)])
    for cod, label in diretto:
        emit(cod, [(1, 'A', label)])

    L.append("COMMIT;")
    if composite:
        L.append("")
        L.append("-- ============================================================")
        L.append("-- COMPOSITE (>=2 '/') - DA DEFINIRE A MANO (parsing ambiguo):")
        for cod, f in composite:
            L.append(f"--   {cod}: {re.sub(r'[\r\n]+', ' / ', f)}")
    with open(OUT_SQL, "w", encoding="utf-8") as fh:
        fh.write("\n".join(L))

    print(f"Indicatori con formula: {len(per)}")
    print(f"  num/den (2 param): {len(numden)} | valore diretto (1 param): {len(diretto)} | SI/NO (0): {sino} | composite (manuali): {len(composite)}")
    print(f"SQL -> {OUT_SQL}")
    if composite:
        print("Composite da definire a mano:")
        for cod, f in composite: print(f"   {cod}: {f[:80]}")

if __name__ == "__main__":
    main()
