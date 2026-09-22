-- =====================================================================
-- PROD FIX schede CTX_BS (accorpato) - da lanciare sul DB di produzione
-- =====================================================================
-- Applica in UNA sola transazione le correzioni sui DATI delle schede CTX_BS:
--   (1) [2026] org_unit_role_type_id: da 'ORGANIZATION_UNIT' (generico) a 'UOC' -> la tendina
--       "Tipo Unita' Resp." nel dettaglio non e' piu' vuota.
--   (2) [2026] source_reference_id: OMOLOGAZIONE del codice scheda a 2026_STG_<UO>, rimuovendo i
--       token 'OB_' e 'PF_' (2026_OB_STG_<UO> e 2026_OB_PF_STG_<UO> -> 2026_STG_<UO>).
--       Il 'PF_' era su sole 3 UO (Servizio Ispettivo, Direzione Sanitaria Aziendale,
--       Farmacia Umaca): incoerenza di naming ereditata dalla sorgente, nessuna differenza
--       funzionale (sono tutte "Obiettivo Performance Strategica") -> uniformate.
--   (3) [2026] etch (Etichetta): da NULL a 'OB_PF_STG_2026' (continuita' col 2025 'OB_PF_STG' + anno).
--   (4) [2025] is_posted: da NULL a 'Y' -> congela in SOLA LETTURA il ciclo chiuso 2025
--       (il gate checkWorkEffortViewFormReadOnly controlla is_posted='Y'). Le 2026 restano 'N'
--       (modificabili): N e NULL sono equivalenti per quel gate, solo 'Y' blocca.
--
-- NON tocca la config import ne' i template: per i FUTURI import vedi
--   - orgTypeCode fisso 'UOC'  -> CONFIG_FIX_ORGTYPE_IMPORT_SCHEDE_BS.sql / SETUP (V003)
--   - codici scheda senza 'OB' -> aggiornare WeRootInterface_BS.xlsx + rigenerare gli script
--                                  generati (POST_IMPORT_FASCE_COMPLETO.sql, ecc.)
--
-- Idempotente e con guardie: se rieseguito non fa danni; se trova incoerenze si ferma.
-- =====================================================================
BEGIN;

-- ---------------------------------------------------------------------
-- (1) Tipo Unita' Responsabile -> UOC
-- ---------------------------------------------------------------------
-- Guardia: non forzare UOC se qualche UO (ancora a ORGANIZATION_UNIT) non ha il PartyRole UOC.
DO $$
DECLARE n_incoerenti int;
BEGIN
  SELECT count(*) INTO n_incoerenti
  FROM work_effort we
  WHERE we.work_effort_type_id = 'CTX_BS'
    AND we.source_reference_id LIKE '2026\_%'
    AND we.org_unit_role_type_id = 'ORGANIZATION_UNIT'
    AND we.org_unit_id IS NOT NULL
    AND NOT EXISTS (SELECT 1 FROM party_role pr
                    WHERE pr.party_id = we.org_unit_id AND pr.role_type_id = 'UOC');
  IF n_incoerenti > 0 THEN
    RAISE EXCEPTION 'STOP (tipo unita''): % schede 2026 con UO senza PartyRole UOC', n_incoerenti;
  END IF;
END $$;

UPDATE work_effort
SET org_unit_role_type_id = 'UOC',
    last_updated_stamp = now(), last_updated_tx_stamp = now()
WHERE work_effort_type_id = 'CTX_BS'
  AND source_reference_id LIKE '2026\_%'
  AND org_unit_role_type_id = 'ORGANIZATION_UNIT';

-- ---------------------------------------------------------------------
-- (2) Codice scheda: omologazione a 2026_STG_<UO> (rimozione 'OB_' e 'PF_')
-- ---------------------------------------------------------------------
-- Guardia: il nuovo codice non deve gia' esistere (collisione con altra scheda).
DO $$
DECLARE n_coll int;
BEGIN
  SELECT count(*) INTO n_coll
  FROM work_effort w
  JOIN work_effort x
    ON x.source_reference_id = regexp_replace(w.source_reference_id, '^2026_OB_(PF_)?STG_', '2026_STG_')
   AND x.work_effort_id <> w.work_effort_id
  WHERE w.work_effort_type_id = 'CTX_BS'
    AND w.source_reference_id LIKE '2026\_OB\_%';
  IF n_coll > 0 THEN
    RAISE EXCEPTION 'STOP (rename codici): % collisioni con codici esistenti', n_coll;
  END IF;
END $$;

UPDATE work_effort
SET source_reference_id = regexp_replace(source_reference_id, '^2026_OB_(PF_)?STG_', '2026_STG_'),
    last_updated_stamp = now(), last_updated_tx_stamp = now()
WHERE work_effort_type_id = 'CTX_BS'
  AND source_reference_id LIKE '2026\_OB\_%';

-- ---------------------------------------------------------------------
-- (3) [2026] Etichetta (etch) -> 'OB_PF_STG_2026'
-- ---------------------------------------------------------------------
UPDATE work_effort
SET etch='OB_PF_STG_2026', last_updated_stamp=now(), last_updated_tx_stamp=now()
WHERE work_effort_type_id='CTX_BS' AND source_reference_id LIKE '2026\_%'
  AND (etch IS NULL OR etch <> 'OB_PF_STG_2026');

-- ---------------------------------------------------------------------
-- (4) [2025] is_posted -> 'Y' (congela il ciclo chiuso in sola lettura)
-- ---------------------------------------------------------------------
-- Tutte le CTX_BS non-2026 sono il ciclo 2025 (verificato: estimated_completion_date anno 2025).
-- N e NULL sono equivalenti per il gate di read-only; solo 'Y' blocca la modifica.
UPDATE work_effort
SET is_posted='Y', last_updated_stamp=now(), last_updated_tx_stamp=now()
WHERE work_effort_type_id='CTX_BS' AND source_reference_id NOT LIKE '2026\_%'
  AND COALESCE(is_posted,'') <> 'Y';

-- ---------------------------------------------------------------------
-- Verifica finale
-- ---------------------------------------------------------------------
SELECT 'tipo_unita_2026' AS check, org_unit_role_type_id AS valore, count(*) AS n
FROM work_effort
WHERE work_effort_type_id='CTX_BS' AND source_reference_id LIKE '2026\_%'
GROUP BY org_unit_role_type_id
UNION ALL
SELECT 'codici_OB_residui_2026', '-', count(*)
FROM work_effort
WHERE work_effort_type_id='CTX_BS' AND source_reference_id LIKE '2026\_OB\_%'
UNION ALL
SELECT 'etch_2026', COALESCE(etch,'(NULL)'), count(*)
FROM work_effort
WHERE work_effort_type_id='CTX_BS' AND source_reference_id LIKE '2026\_%'
GROUP BY etch
UNION ALL
SELECT 'is_posted_2025', COALESCE(is_posted,'(NULL)'), count(*)
FROM work_effort
WHERE work_effort_type_id='CTX_BS' AND source_reference_id NOT LIKE '2026\_%'
GROUP BY is_posted
ORDER BY 1;

COMMIT;
