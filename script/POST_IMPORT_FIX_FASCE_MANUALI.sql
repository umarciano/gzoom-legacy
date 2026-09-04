-- =====================================================================
-- POST_IMPORT_FIX_FASCE_MANUALI.sql
-- =====================================================================
-- Fasce corrette SOLO in piattaforma, NON presenti/derivabili dall'Excel (foglio Obiettivi_UOC) e quindi
-- NON generate da POST_IMPORT_FASCE_COMPLETO.sql. Tipico: indicatore i cui Range nell'Excel sono una VLOOKUP
-- che torna VUOTA (il Target non matcha nessuna chiave del foglio "Range") -> all'import resta su PERF_4FASCE.
--
-- Da eseguire DOPO POST_IMPORT_FASCE_COMPLETO.sql (agganciato a SETUP_POST_IMPORT.sql). Idempotente.
-- Ogni blocco: (eventuale reclass gl_account) + crea/ripopola la scala RNG_<UOC>_<codice> + ripunta la misura
-- a WESCORE_DIRECTRANGE. Aggiungere qui i nuovi casi man mano che si scoprono.
--
-- Uso standalone: psql -h localhost -U postgres -d cardarelli -v ON_ERROR_STOP=1 -f POST_IMPORT_FIX_FASCE_MANUALI.sql
-- =====================================================================
SET client_encoding TO 'UTF8';
BEGIN;

-- ---------------------------------------------------------------------
-- E21 (BSU7200) "N campagne di trasmissione ai reparti", Target 6.
-- Nell'Excel i Range sono una VLOOKUP che torna vuota (Target "6" != chiave "= 6" del foglio Range) ->
-- nessuna fascia importata. Applichiamo il template standard "target 6": =6->100 / =5->75 / =4->50 / <4->0.
-- ---------------------------------------------------------------------
UPDATE gl_account SET calc_custom_method_id = NULL, last_updated_stamp = now(), last_updated_tx_stamp = now()
 WHERE upper(account_code) = 'E21' AND calc_custom_method_id = 'SI_NO';
DELETE FROM uom_range_values WHERE uom_range_id = 'RNG_BSU7200_E21';
INSERT INTO uom_range (uom_id, uom_range_id, description, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
 VALUES ('OTH_SCO', 'RNG_BSU7200_E21', 'Fasce E21 BSU7200 (target 6)', now(), now(), now(), now())
 ON CONFLICT (uom_range_id) DO NOTHING;
INSERT INTO uom_range_values (uom_range_id, uom_range_values_id, is_positive, from_value, thru_value, range_values_factor, range_values_factor_min, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES
 ('RNG_BSU7200_E21', 'RNG_BSU7200_E21_0', 'Y', -999999, 3.99,   0.0,   0.0, now(), now(), now(), now()),
 ('RNG_BSU7200_E21', 'RNG_BSU7200_E21_1', 'Y', 4.0,     4.99,  50.0,  50.0, now(), now(), now(), now()),
 ('RNG_BSU7200_E21', 'RNG_BSU7200_E21_2', 'Y', 5.0,     5.99,  75.0,  75.0, now(), now(), now(), now()),
 ('RNG_BSU7200_E21', 'RNG_BSU7200_E21_3', 'Y', 6.0,     999999, 100.0, 100.0, now(), now(), now(), now());
UPDATE work_effort_measure wem
   SET uom_range_id = 'RNG_BSU7200_E21', we_score_range_enum_id = 'WESCORE_DIRECTRANGE', we_score_conv_enum_id = 'WECONVER_NOCONVERSIO'
  FROM work_effort we, gl_account gl
 WHERE wem.work_effort_id = we.work_effort_id AND wem.gl_account_id = gl.gl_account_id
   AND we.work_effort_type_id = 'CTX_BS' AND we.source_reference_id = '2026_OB_STG_BSU7200' AND upper(gl.account_code) = 'E21';

-- ---------------------------------------------------------------------
-- E09 (BSEA14081) "n. opposizioni / n. totale segnalazioni" (%), Target <= 37% -> indicatore DECRESCENTE.
-- Nell'Excel il R4 e' una VLOOKUP che fallisce (Target 37% non e' chiave del foglio Range) -> la 4a fascia
-- (> 45% -> 0) NON viene importata: genera_import produce solo 3 bande. Qui completiamo con la 4a banda.
-- Percentuale composita (calc_custom_method_id='A/B*100', invariato): scoring DIRECTRANGE come A55/ST77.
-- ---------------------------------------------------------------------
DELETE FROM uom_range_values WHERE uom_range_id = 'RNG_BSEA14081_E09';
INSERT INTO uom_range (uom_id, uom_range_id, description, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
 VALUES ('OTH_SCO', 'RNG_BSEA14081_E09', 'Fasce E09 BSEA14081 (decrescente, <=37%)', now(), now(), now(), now())
 ON CONFLICT (uom_range_id) DO NOTHING;
INSERT INTO uom_range_values (uom_range_id, uom_range_values_id, is_positive, from_value, thru_value, range_values_factor, range_values_factor_min, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES
 ('RNG_BSEA14081_E09', 'RNG_BSEA14081_E09_0', 'Y', -999999, 37.99,  100.0, 100.0, now(), now(), now(), now()),
 ('RNG_BSEA14081_E09', 'RNG_BSEA14081_E09_1', 'Y', 38.0,    40.99,  75.0,  75.0, now(), now(), now(), now()),
 ('RNG_BSEA14081_E09', 'RNG_BSEA14081_E09_2', 'Y', 41.0,    45.0,   50.0,  50.0, now(), now(), now(), now()),
 ('RNG_BSEA14081_E09', 'RNG_BSEA14081_E09_3', 'Y', 45.01,   999999, 0.0,   0.0, now(), now(), now(), now());
UPDATE work_effort_measure wem
   SET uom_range_id = 'RNG_BSEA14081_E09', we_score_range_enum_id = 'WESCORE_DIRECTRANGE', we_score_conv_enum_id = 'WECONVER_NOCONVERSIO'
  FROM work_effort we, gl_account gl
 WHERE wem.work_effort_id = we.work_effort_id AND wem.gl_account_id = gl.gl_account_id
   AND we.work_effort_type_id = 'CTX_BS' AND we.source_reference_id = '2026_OB_STG_BSEA14081' AND upper(gl.account_code) = 'E09';

COMMIT;

-- ---------- Verifica ----------
SELECT upper(gl.account_code) AS codice, wem.uom_range_id, urv.from_value, urv.thru_value, urv.range_values_factor
FROM work_effort we JOIN work_effort_measure wem ON wem.work_effort_id = we.work_effort_id
JOIN gl_account gl ON gl.gl_account_id = wem.gl_account_id
LEFT JOIN uom_range_values urv ON urv.uom_range_id = wem.uom_range_id
WHERE we.source_reference_id IN ('2026_OB_STG_BSU7200', '2026_OB_STG_BSEA14081') AND upper(gl.account_code) IN ('E21', 'E09')
ORDER BY codice, urv.from_value;
