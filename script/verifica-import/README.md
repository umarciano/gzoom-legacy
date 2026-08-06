# Verifica coerenza import Performance Strategica (Excel ↔ DB)

Test automatico che confronta il file **Obiettivi_2026.xlsm** (foglio `Obiettivi_UOC`) con i dati
effettivamente importati nel DB GZoom. Serve a evitare di verificare a mano centinaia di schede/indicatori
e a intercettare disallineamenti come il caso **C08** (fasce reali nel file, ma scala generica in piattaforma).

## Cosa verifica (per ogni riga UOC + indicatore dell'Excel)
- la **scheda** della UOC esiste in piattaforma (CTX_BS);
- l'**indicatore** (codice `Cd`) è presente sulla scheda;
- il **Peso** coincide;
- le **fasce/target** sono coerenti: l'indicatore deve avere una **scala reale** per-indicatore (NON la
  generica `PERF_4FASCE`, NON nulla) e i **punteggi delle fasce** (0/50/75/100) devono corrispondere ai
  Range1-4 dell'Excel. Confine della banda 100% confrontato con la soglia (warning se diverge).

Il test **NON** apre il browser: legge l'Excel e interroga il DB (la card indicatore mostra questi stessi dati).

## Setup (una tantum)
Richiede Node.js. Dalla cartella `verifica-import`:
```bash
npm install
```

## Esecuzione
Impostare le variabili d'ambiente (PowerShell):
```powershell
$env:PGHOST="localhost"; $env:PGPORT="5432"; $env:PGUSER="postgres"; $env:PGPASSWORD="<password>"; $env:PGDATABASE="cardarelli"
$env:EXCEL_OBIETTIVI="C:\percorso\Obiettivi_2026.xlsm"   # se diverso dal default nel codice
npx playwright test
```
(Il percorso Excel ha già un default nel codice; sovrascrivilo con `EXCEL_OBIETTIVI` se serve.)

Output: un **REPORT** con conteggi (OK / ERRORI CRITICI / WARNING) e l'elenco dei disallineamenti.
Il test **fallisce** se ci sono errori critici (schede/indicatori mancanti o fasce generiche/incoerenti).

## Assunzioni da confermare al primo run
- Il **codice indicatore** in piattaforma (`gl_account.account_code`) coincide col `Cd` dell'Excel (es. `C08`).
- Il **codice UOC** si ricava da `work_effort.source_reference_id` togliendo il prefisso `OB_STG_` / `OB_PF_STG_`.

Se al primo run risultassero *tutti* "scheda/indicatore mancante", significa che questo mapping va tarato
(prefissi o formato codice diversi): segnalarlo e si aggiusta la query nello spec.

## Flusso d'uso previsto
1. Restore DB + import base (schede/obiettivi/misure/ruoli).
2. `npx playwright test` → il report evidenzia i ~114 indicatori con fasce generiche/mancanti (es. C08).
3. Import **completo delle fasce** (parsing Range1-4 dalla fonte).
4. Ri-eseguire il test → **0 errori critici** = import coerente con l'Excel.
