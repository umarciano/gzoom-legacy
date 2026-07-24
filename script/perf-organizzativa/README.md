# Configurazione Performance Organizzativa CTX_BS — Cardarelli

Script SQL per configurare l'infrastruttura di sistema necessaria al modulo
Performance Organizzativa (CTX_BS) in ambiente Cardarelli.

## Script — ordine di esecuzione obbligatorio

1. **`setup_4fasce_scoring.sql`** — Scala di punteggio a 4 fasce (PERF_4FASCE)
   - Crea `UomRange` + `UomRangeValues` per scoring 0/50/75/100%
   - Prerequisito per tutti gli indicatori CTX_BS con `WESCORE_DIRECTRANGE`

2. **`setup_workflow_stati.sql`** — Workflow 8 stati Cardarelli (WE_STATUS_OR_CARD)
   - Crea `StatusType`, 8 `StatusItem` WEORCARD_*, `StatusValidChange`
   - Crea righe `WorkEffortTypeStatus` per CTX_BS
   - Migra le schede esistenti da `WEPERFST_EXECPEND` → `WEORCARD_TOVALIDATE`

3. **`../profile-permissions/setup_orgperf_dir_uo_profile.sql`** — Profilo Direttore UO
   - Crea `RoleType` ORGDIR_UO, `WorkEffortTypeRole`, `WorkEffortTypeStatus` UPDATE
   - Crea `SecurityGroup` ORGPERF_DIR_UO + permessi
   - **Prerequisito: setup_workflow_stati.sql già eseguito**

## Uso

```bash
# Linux/Mac
./configure_perf_organizzativa.sh

# Windows
.\configure_perf_organizzativa.ps1
```

Con parametri personalizzati:
```bash
./configure_perf_organizzativa.sh localhost 5432 cardarelli postgres
```

## Verifica post-esecuzione

```sql
-- V. setup_4fasce_scoring: deve restituire 1
SELECT COUNT(*) FROM uom_range WHERE uom_range_id = 'PERF_4FASCE';

-- V. setup_workflow_stati: deve restituire 8
SELECT COUNT(*) FROM status_item WHERE status_id LIKE 'WEORCARD_%';

-- V. profilo direttore UO: deve restituire 4
SELECT COUNT(*) FROM security_group_permission WHERE group_id = 'ORGPERF_DIR_UO';
```

## Note

- Tutti gli script sono idempotenti (`ON CONFLICT DO NOTHING`)
- Il profilo Direttore UO è in `../profile-permissions/` (già incluso nell'orchestrazione)
- Le esclusioni menu per ORGPERF_DIR_UO sono TODO in `setup_orgperf_dir_uo_profile.sql`
