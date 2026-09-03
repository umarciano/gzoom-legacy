/**
 * Verifica di coerenza IMPORT Performance Strategica (CTX_BS).
 *
 * Confronta il file Excel sorgente "Obiettivi_2026.xlsm" (foglio Obiettivi_UOC) con i dati
 * importati nel DB GZoom, usando come chiave il CODICE NEW ("ZZ NUOVO COD"), NON il testo.
 *
 * IMPORTANTE (lezione 2026-08-04): il testo dell'indicatore e' condiviso da piu' codici
 * (es. "Percentuale pratiche trattate" = ST28/ST46/ST46B). Un confronto per TESTO non
 * distingue il codice giusto ed e' cieco allo scambio di codici. La chiave autorevole e'
 * (CdC + ZZ NUOVO COD): l'Excel ha entrambi per riga; il DB ha account_code = codice NEW.
 *
 * Per ogni scheda (UOC) verifica, in ENTRAMBE le direzioni:
 *   - ogni (UOC, codiceNew) atteso dall'Excel e' presente sulla scheda (con PESO coerente e FASCE reali);
 *   - ogni misura presente nel DB e' prevista dall'Excel (codice "di troppo" => assegnazione errata).
 *
 * NON pilota il browser: legge Excel + interroga il DB.
 * Config via env: PGHOST/PGPORT/PGUSER/PGPASSWORD/PGDATABASE + EXCEL_OBIETTIVI.
 */
import { test, expect } from '@playwright/test';
import { Client } from 'pg';
import ExcelJS from 'exceljs';
import * as fs from 'fs';
import * as path from 'path';

const EXCEL_PATH = process.env.EXCEL_OBIETTIVI
  || 'C:\\Users\\l.di.cecio\\Accenture\\SANITA ATC - Internal - Campania\\AORN Cardarelli\\AS_PerformanceContoEconomico\\Progetto\\02_Execution\\PLO VIII - Perfromance Organizzativa GZOOM + Notifiche\\Obiettivi_2026.xlsm';
const SHEET = 'Obiettivi_UOC';

// Alias CdC "sporchi": codice UOC nell'Excel che in anagrafica GZoom corrisponde a un altro codice
// (stessa unita' fisica). Allineato a UOC_ALIAS in genera_import_da_obiettivi.py. Vedi doc 08 §3.3.
const UOC_ALIAS: Record<string, string> = { 'BSEA0121': 'BSEA0120' };

// codici DB che NON provengono dagli Obiettivi (root strategiche legacy / punteggi aggregati):
// esclusi dal controllo "di troppo" per non generare falsi positivi.
const IGNORE_DB_CODE = (c: string) =>
  /^IND_STG_/.test(c) || /^IND\d/.test(c) || c === 'SCORE' || c === 'SCOREKPI' || /^XXX/.test(c);

type ExcelRow = {
  riga: number;
  uocCode: string;      // CdC (es. BAA9904)
  codiceNew: string;    // ZZ NUOVO COD (es. ST46)  -> UPPER per confronto
  cd: string;           // Cd locale (solo display)
  indicatore: string;   // testo (solo display)
  isSiNo: boolean;
  peso: number | null;
  ranges: RangeParsed[];
};
type RangeParsed = { raw: string; factor: number | null; lo: number | null; hi: number | null };

type DbMeasure = { accountCode: string; formula: string | null; uomRangeId: string | null; peso: number | null };
type DbBand = { fromValue: number; thruValue: number; factor: number };

function cellVal(v: any): any {
  if (v == null) return '';
  if (typeof v === 'object') {
    if ('result' in v) return (v as any).result;
    if ('text' in v) return (v as any).text;
    if ('richText' in v) return (v as any).richText.map((t: any) => t.text).join('');
    return '';
  }
  return v;
}
const norm = (v: any) => { const x = cellVal(v); return x == null ? '' : String(x).trim(); };
const up = (v: any) => norm(v).toUpperCase();

function uocFromSourceRef(sref: string): string {
  // Codici scheda per-anno (es. 2026_OB_PF_STG_BSA9090): strippa PRIMA il prefisso anno, poi lo schema.
  return up(sref).replace(/^\d{4}_/, '').replace(/^OB_PF_STG_/, '').replace(/^OB_STG_/, '');
}

/** Parsa "≥ 90% risultato 100%", "= 89-85% risultato 75%", "< 77% risultato 0%". */
function parseRange(raw: any): RangeParsed | null {
  const s = norm(raw);
  if (!s) return null;
  const fm = s.match(/risultato\s*([\d.,]+)\s*%/i) || s.match(/([\d.,]+)\s*%\s*$/);
  const factor = fm ? parseFloat(fm[1].replace(',', '.')) : null;
  const thr = s.split(/risultato/i)[0];
  const nums = (thr.match(/\d+(?:[.,]\d+)?/g) || []).map((n) => parseFloat(n.replace(',', '.')));
  let lo: number | null = null, hi: number | null = null;
  if (/[\u2265]|>=|magg/i.test(thr)) { lo = nums[0] ?? null; }
  else if (/</.test(thr) || /minore/i.test(thr)) { hi = nums[0] ?? null; }
  else if (nums.length >= 2) { lo = Math.min(nums[0], nums[1]); hi = Math.max(nums[0], nums[1]); }
  else if (nums.length === 1) { lo = hi = nums[0]; }
  return { raw: s, factor, lo, hi };
}

async function readExcel(): Promise<ExcelRow[]> {
  const wb = new ExcelJS.Workbook();
  await wb.xlsx.readFile(EXCEL_PATH);
  const ws = wb.getWorksheet(SHEET);
  if (!ws) throw new Error(`Foglio "${SHEET}" non trovato in ${EXCEL_PATH}`);

  const header = ws.getRow(1);
  const idx: Record<string, number> = {};
  header.eachCell((cell, col) => { idx[norm(cell.value).toLowerCase()] = col; });
  const col = (names: string[]) => { for (const n of names) { const i = idx[n.toLowerCase()]; if (i) return i; } return -1; };
  const cUoc = col(['cdc', '2']);
  const cNew = col(['zz nuovo cod', 'zznuovocod', 'codice new']);
  const cCd = col(['cd']);
  const cInd = col(['indicatore']);
  const cForm = col(['formula di calcolo', 'formuladicalcolo']);
  const cPeso = col(['peso']);
  const cR1 = col(['range1']); const cR2 = col(['range2']); const cR3 = col(['range3']); const cR4 = col(['range4']);
  if (cUoc < 0 || cNew < 0) throw new Error(`Colonne chiave non trovate (CdC=${cUoc}, ZZ NUOVO COD=${cNew}). Header: ${Object.keys(idx).join(', ')}`);

  const rows: ExcelRow[] = [];
  ws.eachRow((row, rn) => {
    if (rn === 1) return;
    const uocRaw = up(row.getCell(cUoc).value);
    const uoc = UOC_ALIAS[uocRaw] || uocRaw;   // rimappa CdC sporchi (BSEA0121 -> BSEA0120)
    const codiceNew = up(row.getCell(cNew).value);
    if (!uoc || !codiceNew) return;
    const ranges = [cR1, cR2, cR3, cR4].filter((c) => c > 0)
      .map((c) => parseRange(row.getCell(c).value)).filter((r): r is RangeParsed => !!r);
    const pesoRaw = cPeso > 0 ? cellVal(row.getCell(cPeso).value) : null;
    const formula = norm(cForm > 0 ? row.getCell(cForm).value : '');
    rows.push({
      riga: rn, uocCode: uoc, codiceNew, cd: up(cCd > 0 ? row.getCell(cCd).value : ''),
      indicatore: norm(cInd > 0 ? row.getCell(cInd).value : ''),
      isSiNo: /^si\s*\/\s*no$/i.test(formula),
      peso: pesoRaw != null && pesoRaw !== '' ? Number(pesoRaw) : null,
      ranges,
    });
  });
  return rows;
}

test('Coerenza import Performance Strategica: Excel Obiettivi <-> DB (per CODICE NEW)', async () => {
  const excel = await readExcel();
  console.log(`\nExcel: ${excel.length} righe (UOC + codice NEW) dal foglio ${SHEET}`);

  const db = new Client({
    host: process.env.PGHOST || 'localhost', port: Number(process.env.PGPORT || 5432),
    user: process.env.PGUSER || 'postgres', password: process.env.PGPASSWORD || 'postgres',
    database: process.env.PGDATABASE || 'cardarelli',
  });
  await db.connect();

  const scheHe = await db.query(
    `SELECT work_effort_id, source_reference_id FROM work_effort
      WHERE work_effort_type_id='CTX_BS' AND work_effort_parent_id=work_effort_id
        AND source_reference_id LIKE '2026\\_%'`); // solo schede dell'anno corrente (baseline 2025 esclusa)
  const schedeByUoc = new Map<string, string[]>();
  for (const r of scheHe.rows) {
    const uoc = uocFromSourceRef(r.source_reference_id);
    if (!schedeByUoc.has(uoc)) schedeByUoc.set(uoc, []);
    schedeByUoc.get(uoc)!.push(r.work_effort_id);
  }

  const meas = await db.query(
    `SELECT wem.work_effort_id, gl.account_code, gl.calc_custom_method_id AS formula,
            wem.uom_range_id, wem.kpi_score_weight AS peso
       FROM work_effort_measure wem
       JOIN gl_account gl ON gl.gl_account_id = wem.gl_account_id
       JOIN work_effort we ON we.work_effort_id = wem.work_effort_id AND we.work_effort_type_id='CTX_BS'`);
  // chiave = weId | account_code(UPPER)
  const measByWeCode = new Map<string, DbMeasure>();
  const codesByWe = new Map<string, Set<string>>();
  for (const r of meas.rows) {
    const code = up(r.account_code);
    measByWeCode.set(`${r.work_effort_id}|${code}`, {
      accountCode: code, formula: r.formula, uomRangeId: r.uom_range_id, peso: r.peso == null ? null : Number(r.peso),
    });
    if (!codesByWe.has(r.work_effort_id)) codesByWe.set(r.work_effort_id, new Set());
    codesByWe.get(r.work_effort_id)!.add(code);
  }

  const bands = await db.query(`SELECT uom_range_id, from_value, thru_value, range_values_factor FROM uom_range_values`);
  const bandsById = new Map<string, DbBand[]>();
  for (const r of bands.rows) {
    if (!bandsById.has(r.uom_range_id)) bandsById.set(r.uom_range_id, []);
    bandsById.get(r.uom_range_id)!.push({ fromValue: Number(r.from_value), thruValue: Number(r.thru_value), factor: Number(r.range_values_factor) });
  }
  await db.end();

  const critical: string[] = [];
  const warnings: string[] = [];
  let okCount = 0;

  // insieme atteso per UOC (per il controllo inverso) + traccia dei codici "visti" per weId
  const expectedByUoc = new Map<string, Set<string>>();
  for (const e of excel) {
    if (!expectedByUoc.has(e.uocCode)) expectedByUoc.set(e.uocCode, new Set());
    expectedByUoc.get(e.uocCode)!.add(e.codiceNew);
  }

  // --- DIREZIONE 1: ogni (UOC, codiceNew) atteso deve esserci nel DB ---
  for (const e of excel) {
    const tag = `[riga ${e.riga}] UOC ${e.uocCode} / ${e.codiceNew}${e.cd ? ' (Cd ' + e.cd + ')' : ''}`;
    const weIds = schedeByUoc.get(e.uocCode);
    if (!weIds || weIds.length === 0) {
      critical.push(`${tag}: SCHEDA MANCANTE (nessuna scheda CTX_BS per la UOC ${e.uocCode}).`);
      continue;
    }
    let m: DbMeasure | undefined;
    for (const weId of weIds) { m = measByWeCode.get(`${weId}|${e.codiceNew}`); if (m) break; }
    if (!m) {
      const presenti = weIds.flatMap((w) => [...(codesByWe.get(w) || [])]).filter((c) => !IGNORE_DB_CODE(c));
      critical.push(`${tag}: CODICE NON presente sulla scheda. Codici in DB per la UOC: ${presenti.sort().join(', ') || '(nessuno)'}.`);
      continue;
    }
    // Peso
    if (e.peso != null && m.peso != null && Number(e.peso) !== Number(m.peso)) {
      warnings.push(`${tag}: PESO diverso (excel=${e.peso} vs db=${m.peso}).`);
    }
    // Fasce/target (i SI_NO non hanno scala numerica di proposito)
    const excelFactors = [...new Set(e.ranges.map((r) => r.factor).filter((f): f is number => f != null))].sort((a, b) => a - b);
    const isSiNo = e.isSiNo || (m.formula || '').toUpperCase() === 'SI_NO';
    if (e.ranges.length > 0 && !isSiNo) {
      const rid = m.uomRangeId;
      if (!rid || rid === 'PERF_4FASCE') {
        critical.push(`${tag}: FASCE MANCANTI/GENERICHE (scala "${rid || 'NESSUNA'}").`);
        continue;
      }
      const dbFactors = [...new Set((bandsById.get(rid) || []).map((b) => b.factor))].sort((a, b) => a - b);
      if (JSON.stringify(excelFactors) !== JSON.stringify(dbFactors)) {
        critical.push(`${tag}: PUNTEGGI FASCE diversi (excel=${excelFactors.join('/')} vs db=${dbFactors.join('/')}) su ${rid}.`);
        continue;
      }
    }
    okCount++;
  }

  // --- DIREZIONE 2: ogni codice nel DB deve essere previsto dall'Excel (codice "di troppo") ---
  for (const [uoc, weIds] of schedeByUoc) {
    const expected = expectedByUoc.get(uoc);
    if (!expected) continue; // UOC non presente nell'Excel: gestita altrove
    const seen = new Set<string>();
    for (const weId of weIds) for (const code of (codesByWe.get(weId) || [])) {
      if (IGNORE_DB_CODE(code) || seen.has(code)) continue;
      seen.add(code);
      if (!expected.has(code)) {
        critical.push(`[UOC ${uoc}] CODICE IN DB NON PREVISTO dall'Excel: ${code} (assegnazione errata/residua).`);
      }
    }
  }

  const reportFile = path.join(__dirname, 'report-verifica-import.txt');
  const lines: string[] = [];
  lines.push(`REPORT VERIFICA IMPORT Performance Strategica (per CODICE NEW) - ${new Date().toISOString()}`);
  lines.push(`Excel: ${EXCEL_PATH}`);
  lines.push(`Righe Excel analizzate : ${excel.length}`);
  lines.push(`OK                     : ${okCount}`);
  lines.push(`ERRORI CRITICI         : ${critical.length}`);
  lines.push(`WARNING                : ${warnings.length}`);
  lines.push(`\n===== ERRORI CRITICI (${critical.length}) =====`);
  critical.forEach((x) => lines.push('  X ' + x));
  lines.push(`\n===== WARNING (${warnings.length}) =====`);
  warnings.forEach((x) => lines.push('  ! ' + x));
  fs.writeFileSync(reportFile, lines.join('\n'), 'utf-8');

  console.log(`\n================ REPORT VERIFICA IMPORT (per codice NEW) ================`);
  console.log(`Righe Excel : ${excel.length} | OK: ${okCount} | CRITICI: ${critical.length} | WARNING: ${warnings.length}`);
  if (critical.length) {
    console.log(`\nPrimi ${Math.min(15, critical.length)} errori critici:`);
    critical.slice(0, 15).forEach((x) => console.log('  X ' + x));
  }
  console.log(`\nReport COMPLETO in: ${reportFile}`);
  console.log(`========================================================\n`);

  expect(critical.length, `Trovati ${critical.length} disallineamenti critici. Dettaglio in ${reportFile}`).toBe(0);
});
