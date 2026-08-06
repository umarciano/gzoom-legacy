# Runbook — Import Schede Performance Strategica (CTX_BS) — MODELLO NATIVO

Ordine di esecuzione per creare le schede di Performance Strategica **con i loro N
indicatori**, tramite import di massa. **Modello nativo**: gli indicatori stanno
**direttamente sulla scheda root** (come le schede preesistenti), NON su obiettivi-figli.

> Validato in piattaforma il 2026-07-27 (scheda UOC Cardiologia con 7 indicatori).
> Dettaglio tecnico e cause: `analisi performance organizzativa/6.import-schede-bs-troubleshooting.md`.

---

## A. Prerequisiti dati (devono già esistere nel DB dopo il reset)

| Cosa | Serve per | Come |
|---|---|---|
| Persone (matricole) | (eventuali referenti) | anagrafica |
| Dipartimenti / UOC | Risoluzione `Codice UOC` (`party_parent_role.parent_role_code`) | import UOC |
| Tipo `CTX_BS` con folder `WEFLD_IND` (finalità FIN_VAL) | Tab "Indicatori di valutazione" sulla scheda | di norma già nel baseline; **verificare** (vedi C) |

---

## B. Script SQL di configurazione — ORDINE (dopo il reset)

Un reset del DB **cancella la configurazione custom** (datasource, scoring): va ri-applicata.
Eseguire con: `psql -h localhost -U postgres -d cardarelli -f <file>`

| # | Script | Cosa crea / perché |
|---|---|---|
| **1** | **`SETUP_PERF_ORGANIZZATIVA.sql`** | **Script unico consolidato.** Contiene tutto: scoring 4 fasce (`PERF_4FASCE`, soglie `SOGLIA_50/100`, converter), workflow stati `WEORCARD_*`, datasource import (schede/obiettivi/misure/ruoli/**catalogo**) con mapping corretti (Area→Natura, **Fonte**, **referente-UOC** `WEM_IND_IN_CHARGE`), nature/aree, seed `party_role` referenti (V005/V006), formato card indicatori. Idempotente. |

> Sostituisce i vecchi script separati `perf-organizzativa/setup_4fasce_scoring.sql`,
> `setup_workflow_stati.sql` e `CONFIG_IMPORT_SCHEDE_BS.sql` (**rimosso**), che avevano
> mapping obsoleti (es. `customText01` per l'Area, referente su matricola). Nel modello
> nativo le misure targettano la ROOT (`sourceReferenceId = Codice Scheda`, `CTX_BS`); il
> datasource `IMPORT_OBIETTIVI_BS` resta definito ma **non si usa**.

> **Dopo l'import dati** (sezione D) eseguire un unico script: **`SETUP_POST_IMPORT.sql`**
> (`psql -f SETUP_POST_IMPORT.sql`), che a sua volta richiama in ordine:
> - `POST_IMPORT_FASCE_COMPLETO.sql` — `uom_range` **per-(UOC+indicatore)** `RNG_<UOC>_<codiceNEW>` con
>   le fasce reali + scoring diretto `WESCORE_DIRECTRANGE` sulle misure (gen. `genera_import_da_obiettivi.py`);
> - `POST_IMPORT_PARAMETRI_INDICATORI.sql` — **parametri per indicatore** (`gl_fiscal_type PAR_*` +
>   `gl_account_input_calc`) che il referente inserisce nella modale (num/den o valore diretto, dalle
>   formule di `Obiettivi_UOC`; gen. `genera_parametri_indicatori.py`). 5 composite da definire a mano.

> **RICONCILIAZIONE 2026-08-03**: i template `IndicatoriCatalogo_BS.xlsx` (catalogo) e
> `WeMeasureInterface_BS.xlsx` (misure) sono stati **rigenerati** dal file sorgente
> `Obiettivi_2026.xlsm` allineandoli al codice indicatore globale **"codice NEW"** (foglio
> "Obiettivi" master). Il `Cd` di `Obiettivi_UOC` NON e' un codice globale (e' locale per UOC).
> Generatori: `riconcilia_catalogo_indicatori.py` (catalogo, 417 righe/376 codici) e
> `genera_import_da_obiettivi.py` (misure 581 righe + `POST_IMPORT_FASCE_COMPLETO.sql` 495 scale).
> I file pre-riconciliazione sono in `templates/_archivio_pre_riconciliazione_2026-08-03/` e
> `_archivio_pre_riconciliazione_2026-08-03/`. Fasce SI_NO (83) non create di proposito.

### Datasource creati (descrizione = voce nella tendina UI)

| Datasource | Tracciato | Uso nel modello nativo |
|---|---|---|
| IMPORT_INDICATORI_BS | GL_ACCOUNT_INTERFACE | **catalogo indicatori** (da caricare PER PRIMO) |
| IMPORT_MISURE_BS | WE_MEASURE_INTERFACE | N indicatori agganciati alla scheda root |
| IMPORT_RUOLI_BS | WE_PARTY_INTERFACE | referenti (WEM_PERF_IN_CHARGE) sulla scheda root |
| IMPORT_SCHEDE_BS | WE_ROOT_INTERFACE | schede root (esecuzione = cascata: tira dentro misure e referenti) |
| IMPORT_OBIETTIVI_BS | WE_INTERFACE | NON usato (modello nativo) |

---

## C. Verifiche prerequisito (query, MCP read-only o psql)

```sql
-- scoring 4 fasce presente
SELECT uom_range_id FROM uom_range WHERE uom_range_id='PERF_4FASCE';
-- folder tab indicatori su CTX_BS (deve esistere WEFLD_IND con FIN_VAL)
SELECT content_id, work_effort_purpose_type_id FROM work_effort_type_content
 WHERE work_effort_type_id='CTX_BS' AND content_id='WEFLD_IND';
-- datasource creati
SELECT data_source_id FROM data_source WHERE data_source_id LIKE 'IMPORT_%BS';
```
Se manca `WEFLD_IND` su CTX_BS, aggiungerlo (vedi doc 1 "configurazione-obiettivi-e-indicatori", sez. folder CTX_BS).

---

## D. Import dati (UI) — ORDINE OBBLIGATORIO

> **IMPORTANTE — il catalogo va PER PRIMO.** Se si caricano le misure senza che gli
> indicatori esistano già nel catalogo, l'import misure crea `gl_account` **stub incompleti**
> (`default_uom_id` NULL) e la griglia "Indicatori di valutazione" **NON li mostra**
> (`INNER JOIN UOM` in `queryIndicator.sql.ftl`).

### Passo 1 — Catalogo indicatori (datasource IMPORT_INDICATORI_BS)
Schermata **Contabilità**: *Modello di Governance > Unità Contabili ed Extracontabili >
Interfacciamento Unità e Movimenti*, riga **"Anagrafica Unità Contabili/Extracontabili"**.
- Datasource: *…Indicatori (catalogo) Performance Strategica…*, **Data** (ref_date obbligatoria!), file catalogo, esegui ▶.
- Crea `gl_account` completi: `default_uom_id`=OTH_SCO (da 'Punt.'), accountTypeEnumId=INDICATOR, stato GLACC_ACTIVE, finalità FIN_VAL.
- I codici indicatore nel catalogo DEVONO coincidere con la colonna *Codice Indicatore* del file misure.
- Colonne file catalogo (`IndicatoriCatalogo_BS.xlsx`): Codice Indicatore, Indicatore, Descrizione sintetica, Tipologia, **Area** (codice `AREA_*`→Natura), **Codice UOC Referente** (→`gl_account_role` UOC, ruolo `WEM_IND_IN_CHARGE`), **Fonte** (→`gl_account.source`). NB: il referente dell'**indicatore** è una **UOC** (dal master Obiettivi); è cosa diversa dal **responsabile della scheda** (direttore UO, ruolo `WEM_PERF_IN_CHARGE`, file ruoli).

### Passo 2 — Misure + Referenti (Solo Upload)
Schermata **Interfacciamento Schede** (*Modello di Governance > Unità di Programmazione*).
Spunta **Solo Upload** e carica (esegui ▶) entrambi in staging:
- riga **Interfaccia Misure Obiettivi**: datasource *…Misure…*, file `WeMeasureInterface_BS.xlsx`.
- riga **Interfaccia Ruoli Obiettivi**: datasource *…Ruoli…*, file `WePartyInterface_BS.xlsx`.
Nel modello nativo entrambi targettano la ROOT (config: `sourceReferenceId = Codice Scheda`, `CTX_BS`).
Il file ruoli ha: Codice Scheda, Matricola Referente, Data Inizio, Data Fine (una riga per referente/scheda).

### Passo 3 — Schede root (datasource IMPORT_SCHEDE_BS) — esecuzione
- Togli **Solo Upload**, riga **Interfaccia Schede**: datasource *…Schede Performance Strategica CTX_BS*, file `WeRootInterface_BS.xlsx`, **spunta la checkbox** della riga, esegui ▶.
- Processing **asincrono** (~15-20s): crea le schede e la cascata aggancia alla root le N misure **e i referenti**.

---

## E. Note operative

- **Catalogo prima** (vedi sopra): la trappola più insidiosa.
- Root **auto-parentata** (`work_effort_parent_id = work_effort_id`): è ciò che permette
  alle misure di agganciarsi alla root con `sourceReferenceId = Codice Scheda` + `CTX_BS`.
- `operationType=O` (schede): ogni import **elimina e ricrea** la scheda. In caso di errore
  in cascata la scheda resta cancellata → rilanciare.
- Prima del run completo (tutte le schede): verificare che nessun `source_reference_id`
  in import abbia **duplicati** in work_effort (O fallisce con "Found more than one").
- Tendina datasource UI: è un `local-autocompleter` cache-ato al render. Se una voce
  appena creata non compare, ricaricare la pagina o (se serve) riavviare Tomcat.
- **Referenti**: nel modello nativo il referente (ruolo WEM_PERF_IN_CHARGE, abilitato su CTX_BS
  in `work_effort_type_role`) va sulla **scheda root**. Config `IMPORT_RUOLI_BS` aggiornata
  (`sourceReferenceId = Codice Scheda`, `CTX_BS`). N matricole sulla stessa scheda => N referenti
  sulla root. Chiave assegnazione = (workEffortId, fromDate, roleTypeId, partyId): la stessa
  persona ripetuta non duplica.

---

## F. Verifica esito

```sql
-- N indicatori attaccati alla root di una scheda (es.)
SELECT we.source_reference_id, COUNT(wem.*) AS n_indicatori
FROM work_effort we JOIN work_effort_measure wem ON wem.work_effort_id = we.work_effort_id
WHERE we.work_effort_type_id='CTX_BS' AND we.source_reference_id = '<CODICE_SCHEDA>'
GROUP BY we.source_reference_id;
```
In piattaforma: *Performance Strategica > Consultazione > Interrogazione* → cerca la scheda →
doppio click → tab **"Indicatori di valutazione"** → devono comparire gli N indicatori.
