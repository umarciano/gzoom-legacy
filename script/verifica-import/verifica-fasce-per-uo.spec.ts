/**
 * Verifica COERENZA FASCE per-UO: Excel Obiettivi_2026 (foglio Obiettivi_UOC) <-> PIATTAFORMA.
 *
 * La piattaforma mostra le fasce di un indicatore leggendo le `uom_range_values` del DB (lo fa la card
 * `WorkEffortMeasureIndicatorCardarelliCard.ftl` -> tabella "Range (fasce)"). Quindi confrontare le
 * `uom_range_values` con le fasce dell'Excel = verificare che a video le fasce siano "come sull'Excel".
 *
 * A differenza di verifica-coerenza-import (che confronta solo i PUNTEGGI/factor), qui si confrontano le
 * BANDE COMPLETE: estremi (from/thru) + punteggio, banda per banda. Piu' peso e tipo (SI_NO). NON il referente
 * (nell'Excel non c'e' la persona, ed e' modificabile da UI).
 *
 * NON pilota il browser: legge Excel + DB. Chiave = (UOC=CdC, codice=ZZ NUOVO COD).
 * Config: PGHOST/PGPORT/PGUSER/PGPASSWORD/PGDATABASE + EXCEL_OBIETTIVI.
 */
import { test, expect } from '@playwright/test';
import { Client } from 'pg';
import ExcelJS from 'exceljs';
import * as fs from 'fs';
import * as path from 'path';

const EXCEL_PATH = process.env.EXCEL_OBIETTIVI
  || 'C:\\Users\\l.di.cecio\\Accenture\\SANITA ATC - Internal - Campania\\AORN Cardarelli\\AS_PerformanceContoEconomico\\Progetto\\02_Execution\\PLO VIII - Perfromance Organizzativa GZOOM + Notifiche\\Obiettivi_2026.xlsm';
const SHEET = 'Obiettivi_UOC';
const SENT = 999999;
const TOL = 0.02;
const UOC_ALIAS: Record<string, string> = { 'BSEA0121': 'BSEA0120' }; // dato sporco anagrafica (vedi doc 08 §3.3)
const IGNORE = (c: string) => /^IND_STG_/.test(c) || /^IND\d/.test(c) || c === 'SCORE' || c === 'SCOREKPI' || /^XXX/.test(c);

type Band = [number, number, number]; // [from, thru, factor]
function cellVal(v: any): any { if (v == null) return ''; if (typeof v === 'object') { if ('result' in v) return v.result; if ('text' in v) return v.text; if ('richText' in v) return v.richText.map((t: any) => t.text).join(''); return ''; } return v; }
const norm = (v: any) => { const x = cellVal(v); return x == null ? '' : String(x).trim(); };
const up = (v: any) => norm(v).toUpperCase();

function parseCell(raw: any): { factor: number; lb: number; lowOpen: boolean } | null {
  const s = norm(raw); if (!s) return null;
  const fm = s.match(/risultato\s*([\d.,]+)\s*%/i) || s.match(/([\d.,]+)\s*%\s*$/);
  if (!fm) return null;
  const factor = parseFloat(fm[1].replace(',', '.'));
  const thr = s.split(/risultato/i)[0];
  const nums = (thr.match(/\d+(?:[.,]\d+)?/g) || []).map((n) => parseFloat(n.replace(',', '.')));
  if (!nums.length) return null;
  const lowOpen = /[<\u2264]/.test(thr);
  const lb = nums.length >= 2 ? Math.min(nums[0], nums[1]) : nums[0];
  return { factor, lb, lowOpen };
}
/** Replica build_bands di genera_import_da_obiettivi.py: bande attese dall'Excel. */
function buildBands(cells: any[]): Band[] | null {
  let bands = cells.map(parseCell).filter((b): b is { factor: number; lb: number; lowOpen: boolean } => !!b);
  if (bands.length < 2) return null;
  bands.sort((a, b) => (a.lowOpen ? -1e12 : a.lb) - (b.lowOpen ? -1e12 : b.lb));
  const merged: typeof bands = [];
  for (const b of bands) { if (merged.length && merged[merged.length - 1].factor === b.factor) continue; merged.push(b); }
  bands = merged;
  const n = bands.length; const out: Band[] = [];
  for (let i = 0; i < n; i++) {
    const frm = i === 0 ? -SENT : bands[i].lb;
    const thru = i === n - 1 ? SENT : Math.round((bands[i + 1].lb - 0.01) * 100) / 100;
    out.push([frm, thru, bands[i].factor]);
  }
  if (new Set(out.map((o) => o[2])).size !== out.length) return null;
  return out;
}
const eq = (a: number, b: number) => Math.abs(a - b) <= TOL;
const sortBands = (b: Band[]) => [...b].sort((a, c) => a[0] - c[0]);
function bandeUguali(ex: Band[], db: Band[]): boolean {
  if (ex.length !== db.length) return false;
  const e = sortBands(ex), d = sortBands(db);   // confronto ordine-indipendente
  for (let i = 0; i < e.length; i++) { for (let k = 0; k < 3; k++) { if (!eq(e[i][k], d[i][k])) return false; } }
  return true;
}
/** Bande MALFORMATE = errore dati (di solito Range invertito/typo nell'Excel, es. "<45%" al posto di ">45%"
 *  su indicatore decrescente): banda con from>thru, oppure bande sovrapposte. Ritorna la descrizione o null. */
function bandaMalformata(bands: Band[]): string | null {
  const s = sortBands(bands);
  for (const [f, t] of s) { if (f > t + TOL) return `banda invertita [${f}..${t}]`; }
  for (let i = 0; i < s.length - 1; i++) { if (s[i][1] > s[i + 1][0] + TOL) return `bande sovrapposte [..${s[i][1]}] / [${s[i + 1][0]}..]`; }
  return null;
}
const fmt = (b: Band[]) => b.map(([f, t, x]) => `[${f <= -SENT ? '-inf' : f}..${t >= SENT ? '+inf' : t}]=${x}%`).join(' ');

interface ExRow { uoc: string; code: string; ind: string; peso: number | null; isSiNo: boolean; bands: Band[] | null; hasFormula: boolean; }
/** true se un cella Range e' una FORMULA non risolta (es. =VLOOKUP...): il file non ha il valore cache-ato,
 *  quindi le fasce NON sono leggibili dal file (vanno verificate a mano / con Excel che ricalcola). */
function rangeConFormula(cells: any[]): boolean {
  return cells.some((v) =>
    (v && typeof v === 'object' && 'formula' in v && (v.result === undefined || v.result === null || String(v.result).trim() === '')) ||
    (typeof v === 'string' && v.trim().startsWith('='))   // formula salvata come stringa (es. "=IFERROR(VLOOKUP...)")
  );
}
async function readExcel(): Promise<ExRow[]> {
  const wb = new ExcelJS.Workbook(); await wb.xlsx.readFile(EXCEL_PATH);
  const ws = wb.getWorksheet(SHEET); if (!ws) throw new Error(`Foglio ${SHEET} non trovato`);
  const idx: Record<string, number> = {}; ws.getRow(1).eachCell((c, col) => { idx[norm(c.value).toLowerCase()] = col; });
  const col = (names: string[]) => { for (const n of names) { const i = idx[n.toLowerCase()]; if (i) return i; } return -1; };
  const cU = col(['cdc', '2']), cN = col(['zz nuovo cod', 'codice new']), cI = col(['indicatore']);
  const cF = col(['formula di calcolo']), cP = col(['peso']);
  const cR = [col(['range1']), col(['range2']), col(['range3']), col(['range4'])].filter((x) => x > 0);
  const rows: ExRow[] = [];
  ws.eachRow((row, rn) => {
    if (rn === 1) return;
    const uocRaw = up(row.getCell(cU).value); const uoc = UOC_ALIAS[uocRaw] || uocRaw;
    const code = up(row.getCell(cN).value);
    if (!uoc || !code || IGNORE(code)) return;
    const formula = norm(cF > 0 ? row.getCell(cF).value : '');
    const pesoRaw = cP > 0 ? cellVal(row.getCell(cP).value) : null;
    const rangeRaw = cR.map((c) => row.getCell(c).value);
    rows.push({ uoc, code, ind: norm(cI > 0 ? row.getCell(cI).value : ''),
      peso: pesoRaw != null && pesoRaw !== '' ? Number(pesoRaw) : null,
      isSiNo: /^si\s*\/\s*no$/i.test(formula), bands: buildBands(rangeRaw), hasFormula: rangeConFormula(rangeRaw) });
  });
  return rows;
}
const uocFromRef = (s: string) => up(s).replace(/^\d{4}_/, '').replace(/^OB_PF_STG_/, '').replace(/^OB_STG_/, '');

test('Coerenza FASCE per-UO: Excel Obiettivi <-> piattaforma (uom_range_values)', async () => {
  const excel = await readExcel();
  const db = new Client({ host: process.env.PGHOST || 'localhost', port: Number(process.env.PGPORT || 5432),
    user: process.env.PGUSER || 'postgres', password: process.env.PGPASSWORD || 'postgres', database: process.env.PGDATABASE || 'cardarelli' });
  await db.connect();
  // bande DB (cio' che mostra la card) + peso + tipo, per (UOC, codice) delle schede 2026
  const r = await db.query(`
    SELECT we.source_reference_id AS sref, upper(gl.account_code) AS code,
           wem.kpi_score_weight AS peso, gl.calc_custom_method_id AS formula,
           urv.from_value AS frm, urv.thru_value AS thru, urv.range_values_factor AS factor
    FROM work_effort we
    JOIN work_effort_measure wem ON wem.work_effort_id = we.work_effort_id
    JOIN gl_account gl ON gl.gl_account_id = wem.gl_account_id
    LEFT JOIN uom_range_values urv ON urv.uom_range_id = wem.uom_range_id
    WHERE we.work_effort_type_id = 'CTX_BS' AND we.source_reference_id LIKE '2026\\_%'`);
  await db.end();

  interface DbAgg { peso: number | null; formula: string | null; bands: Band[]; }
  const byKey = new Map<string, DbAgg>();
  for (const row of r.rows) {
    const key = `${uocFromRef(row.sref)}|${up(row.code)}`;
    if (IGNORE(up(row.code))) continue;
    let a = byKey.get(key);
    if (!a) { a = { peso: row.peso == null ? null : Number(row.peso), formula: row.formula, bands: [] }; byKey.set(key, a); }
    if (row.frm != null) a.bands.push([Number(row.frm), Number(row.thru), Number(row.factor)]);
  }
  for (const a of byKey.values()) a.bands.sort((x, y) => x[0] - y[0]);

  const critical: string[] = []; const warn: string[] = []; let okFasce = 0;
  for (const e of excel) {
    const tag = `UOC ${e.uoc} / ${e.code}`;
    const a = byKey.get(`${e.uoc}|${e.code}`);
    if (!a) { critical.push(`${tag}: assente in piattaforma (nessuna misura sulla scheda 2026).`); continue; }
    const isSiNo = e.isSiNo || (a.formula || '').toUpperCase() === 'SI_NO';
    // PESO
    if (e.peso != null && a.peso != null && Number(e.peso) !== Number(a.peso)) warn.push(`${tag}: PESO excel=${e.peso} vs piattaforma=${a.peso}`);
    // FASCE (i SI_NO binari non hanno scala)
    if (isSiNo) { okFasce++; continue; }
    if (e.hasFormula) { warn.push(`${tag}: un Range dell'Excel è una FORMULA non risolta (es. VLOOKUP su foglio Range!) → fasce non leggibili dal file. Piattaforma: ${a.bands.length ? fmt(a.bands) : '(nessuna)'}`); continue; }
    if (!e.bands) { warn.push(`${tag}: fasce non parse-abili dall'Excel (Range vuoti/formato) — indicatore con Target ma senza fasce definite. Piattaforma: ${a.bands.length ? (a.bands.length === 4 && a.bands.every((b, i) => b[2] === [0, 50, 75, 100][i]) ? 'scala GENERICA PERF_4FASCE' : fmt(a.bands)) : '(nessuna)'}.`); continue; }
    // 1) ERRORE ALLA FONTE: i Range dell'Excel producono bande incoerenti (tipico: indicatore decrescente
    //    col range "<X%" al posto di ">X%"). Va corretto nell'Excel (in piattaforma potrebbe essere gia' sistemato a mano).
    const malExcel = bandaMalformata(e.bands);
    if (malExcel) {
      critical.push(`${tag}: RANGE ERRATO NELL'EXCEL (${malExcel}) — es. indicatore decrescente col range "<X%" invece di ">X%".\n      Excel      : ${fmt(e.bands)}\n      Piattaforma: ${a.bands.length ? fmt(a.bands) : '(nessuna)'}`);
      continue;
    }
    if (a.bands.length === 0) { critical.push(`${tag}: FASCE MANCANTI in piattaforma (attese: ${fmt(e.bands)}).`); continue; }
    // 2) piattaforma con bande incoerenti (non ancora sistemata)
    const malDb = bandaMalformata(a.bands);
    if (malDb) { critical.push(`${tag}: FASCE MALFORMATE in piattaforma (${malDb}). Bande: ${fmt(a.bands)}`); continue; }
    // 3) divergenza Excel (valido) vs piattaforma
    if (!bandeUguali(e.bands, a.bands)) {
      critical.push(`${tag}: FASCE DIVERSE\n      Excel      : ${fmt(e.bands)}\n      Piattaforma: ${fmt(a.bands)}`);
      continue;
    }
    okFasce++;
  }

  const reportFile = path.join(__dirname, 'report-verifica-fasce.txt');
  const L = [`REPORT COERENZA FASCE Excel<->piattaforma - ${new Date().toISOString()}`, `Excel: ${EXCEL_PATH}`,
    `Righe Excel: ${excel.length} | fasce OK: ${okFasce} | CRITICI: ${critical.length} | WARNING: ${warn.length}`,
    `\n===== CRITICI (${critical.length}) =====`, ...critical.map((x) => '  X ' + x),
    `\n===== WARNING (${warn.length}) =====`, ...warn.map((x) => '  ! ' + x)];
  fs.writeFileSync(reportFile, L.join('\n'), 'utf-8');
  console.log(`\n==== FASCE Excel<->piattaforma | righe ${excel.length} | OK ${okFasce} | CRITICI ${critical.length} | WARN ${warn.length} ====`);
  critical.slice(0, 20).forEach((x) => console.log('  X ' + x));
  console.log(`\nReport completo: ${reportFile}`);

  expect(critical.length, `Trovate ${critical.length} incoerenze CRITICHE nelle fasce. Dettaglio in ${reportFile}`).toBe(0);
});
