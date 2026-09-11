-- PARAMETRI INDICATORI COMPOSITE (modale consuntivazione) - DEFINIZIONE MANUALE.
-- Indicatori che genera_parametri_indicatori.py lascia "da definire a mano" (parsing ambiguo:
-- slash TESTUALI P.S./O.B.I. non divisioni, oppure formula verbale senza A/B parseabile).
-- Formule reali dal foglio Obiettivi_UOC (colonna "Formula di calcolo"):
--   A55  = (deospedalizzati dopo consulenza) / (con richiesta consulenza)            -> A/B*100  (rapporto)
--   A58b = (deosp. dopo consulenza urologica) / (con richiesta consulenza urologica) -> A/B*100  (rapporto)
--   ST77 = (lavoratori sottoposti) / (lavoratori previsti)                           -> A/B*100  (rapporto)
--   A111 = (media visite a.26 - media visite a.25) / media visite a.25               -> (A-B)/B*100 (variazione %)
--   A114 = (% accessi PS con ricovero, permanenza T0-T3 >8h, a.val - a.prec) / a.prec -> (A-B)/B*100 (variazione %)
--   A115 = (% accessi OBI intervallo T0-T5 >44h, a.val - a.prec) / a.prec            -> (A-B)/B*100 (variazione %)
--   ST76 = studi multicentrici + trial attivi + studi finanziati + progetti finanziati -> SUM(A) (conteggio, 4 addendi)
-- Modello: gl_fiscal_type PAR_<COD>_<seq> (etichetta) + gl_account_input_calc (factor_calculator = ruolo).
-- Il valore finale (ACTUAL) lo calcola il FE dai parametri; qui si definisce solo COSA chiedere.
-- Idempotente (DELETE preventivi). Da eseguire dopo POST_IMPORT_PARAMETRI_INDICATORI.sql.
SET client_encoding TO 'UTF8';
BEGIN;

-- Nuovo metodo di calcolo per la variazione percentuale (A111). SUM(A) esiste gia'.
INSERT INTO custom_method (custom_method_id, custom_method_type_id, custom_method_name, description, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES ('(A-B)/B*100', 'GL_ACC', '(A-B)/B*100', 'Variazione percentuale di A rispetto alla base B: (A-B)/B*100', now(), now(), now(), now())
ON CONFLICT (custom_method_id) DO NOTHING;

-- ============================ A55 (A/B*100) ============================
DELETE FROM gl_account_input_calc WHERE gl_account_id IN (SELECT gl_account_id FROM gl_account WHERE upper(account_code)='A55' AND gl_account_type_id='WECAL');
DELETE FROM gl_fiscal_type WHERE gl_fiscal_type_id IN ('PAR_A55_1','PAR_A55_2');
INSERT INTO gl_fiscal_type (gl_fiscal_type_id, description, gl_fiscal_type_enum_id, is_financial_used, is_account_used, is_indicator_used, created_by_user_login, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES
  ('PAR_A55_1', 'pazienti oncologici deospedalizzati dopo consulenza da P.S./O.B.I.', 'GLFISCTYPE_ACTUAL', 'N', 'N', 'Y', 'admin', now(), now(), now(), now()),
  ('PAR_A55_2', 'pazienti oncologici con richiesta consulenza da P.S./O.B.I.',        'GLFISCTYPE_ACTUAL', 'N', 'N', 'Y', 'admin', now(), now(), now(), now());
INSERT INTO gl_account_input_calc (gl_account_input_calc_id, gl_account_id, input_sequence_num, gl_account_id_ref, factor_calculator, gl_fiscal_type_id, created_by_user_login, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
  SELECT 'IC_A55_1', gl_account_id, '1', NULL, 'A', 'PAR_A55_1', 'admin', now(), now(), now(), now() FROM gl_account WHERE upper(account_code)='A55' AND gl_account_type_id='WECAL';
INSERT INTO gl_account_input_calc (gl_account_input_calc_id, gl_account_id, input_sequence_num, gl_account_id_ref, factor_calculator, gl_fiscal_type_id, created_by_user_login, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
  SELECT 'IC_A55_2', gl_account_id, '2', NULL, 'B', 'PAR_A55_2', 'admin', now(), now(), now(), now() FROM gl_account WHERE upper(account_code)='A55' AND gl_account_type_id='WECAL';

-- ============================ A58b (A/B*100) ============================
DELETE FROM gl_account_input_calc WHERE gl_account_id IN (SELECT gl_account_id FROM gl_account WHERE upper(account_code)='A58B' AND gl_account_type_id='WECAL');
DELETE FROM gl_fiscal_type WHERE gl_fiscal_type_id IN ('PAR_A58B_1','PAR_A58B_2');
INSERT INTO gl_fiscal_type (gl_fiscal_type_id, description, gl_fiscal_type_enum_id, is_financial_used, is_account_used, is_indicator_used, created_by_user_login, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES
  ('PAR_A58B_1', 'pazienti deospedalizzati da P.S./O.B.I. dopo richiesta consulenza urologica', 'GLFISCTYPE_ACTUAL', 'N', 'N', 'Y', 'admin', now(), now(), now(), now()),
  ('PAR_A58B_2', 'pazienti con richiesta consulenza urologica da P.S./O.B.I.',                  'GLFISCTYPE_ACTUAL', 'N', 'N', 'Y', 'admin', now(), now(), now(), now());
INSERT INTO gl_account_input_calc (gl_account_input_calc_id, gl_account_id, input_sequence_num, gl_account_id_ref, factor_calculator, gl_fiscal_type_id, created_by_user_login, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
  SELECT 'IC_A58B_1', gl_account_id, '1', NULL, 'A', 'PAR_A58B_1', 'admin', now(), now(), now(), now() FROM gl_account WHERE upper(account_code)='A58B' AND gl_account_type_id='WECAL';
INSERT INTO gl_account_input_calc (gl_account_input_calc_id, gl_account_id, input_sequence_num, gl_account_id_ref, factor_calculator, gl_fiscal_type_id, created_by_user_login, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
  SELECT 'IC_A58B_2', gl_account_id, '2', NULL, 'B', 'PAR_A58B_2', 'admin', now(), now(), now(), now() FROM gl_account WHERE upper(account_code)='A58B' AND gl_account_type_id='WECAL';

-- ============================ ST77 (A/B*100) ============================
DELETE FROM gl_account_input_calc WHERE gl_account_id IN (SELECT gl_account_id FROM gl_account WHERE upper(account_code)='ST77' AND gl_account_type_id='WECAL');
DELETE FROM gl_fiscal_type WHERE gl_fiscal_type_id IN ('PAR_ST77_1','PAR_ST77_2');
INSERT INTO gl_fiscal_type (gl_fiscal_type_id, description, gl_fiscal_type_enum_id, is_financial_used, is_account_used, is_indicator_used, created_by_user_login, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES
  ('PAR_ST77_1', 'n. lavoratori sottoposti a sorveglianza sanitaria', 'GLFISCTYPE_ACTUAL', 'N', 'N', 'Y', 'admin', now(), now(), now(), now()),
  ('PAR_ST77_2', 'n. lavoratori previsti per sorveglianza sanitaria', 'GLFISCTYPE_ACTUAL', 'N', 'N', 'Y', 'admin', now(), now(), now(), now());
INSERT INTO gl_account_input_calc (gl_account_input_calc_id, gl_account_id, input_sequence_num, gl_account_id_ref, factor_calculator, gl_fiscal_type_id, created_by_user_login, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
  SELECT 'IC_ST77_1', gl_account_id, '1', NULL, 'A', 'PAR_ST77_1', 'admin', now(), now(), now(), now() FROM gl_account WHERE upper(account_code)='ST77' AND gl_account_type_id='WECAL';
INSERT INTO gl_account_input_calc (gl_account_input_calc_id, gl_account_id, input_sequence_num, gl_account_id_ref, factor_calculator, gl_fiscal_type_id, created_by_user_login, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
  SELECT 'IC_ST77_2', gl_account_id, '2', NULL, 'B', 'PAR_ST77_2', 'admin', now(), now(), now(), now() FROM gl_account WHERE upper(account_code)='ST77' AND gl_account_type_id='WECAL';

-- ============================ A111 ((A-B)/B*100, variazione) ============================
UPDATE gl_account SET calc_custom_method_id='(A-B)/B*100', last_updated_stamp=now(), last_updated_tx_stamp=now()
  WHERE upper(account_code)='A111' AND gl_account_type_id='WECAL';
DELETE FROM gl_account_input_calc WHERE gl_account_id IN (SELECT gl_account_id FROM gl_account WHERE upper(account_code)='A111' AND gl_account_type_id='WECAL');
DELETE FROM gl_fiscal_type WHERE gl_fiscal_type_id IN ('PAR_A111_1','PAR_A111_2');
INSERT INTO gl_fiscal_type (gl_fiscal_type_id, description, gl_fiscal_type_enum_id, is_financial_used, is_account_used, is_indicator_used, created_by_user_login, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES
  ('PAR_A111_1', 'media visite mensili ambulatorio EMG/Ecografia anno 2026', 'GLFISCTYPE_ACTUAL', 'N', 'N', 'Y', 'admin', now(), now(), now(), now()),
  ('PAR_A111_2', 'media visite mensili ambulatorio EMG/Ecografia anno 2025', 'GLFISCTYPE_ACTUAL', 'N', 'N', 'Y', 'admin', now(), now(), now(), now());
INSERT INTO gl_account_input_calc (gl_account_input_calc_id, gl_account_id, input_sequence_num, gl_account_id_ref, factor_calculator, gl_fiscal_type_id, created_by_user_login, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
  SELECT 'IC_A111_1', gl_account_id, '1', NULL, 'A', 'PAR_A111_1', 'admin', now(), now(), now(), now() FROM gl_account WHERE upper(account_code)='A111' AND gl_account_type_id='WECAL';
INSERT INTO gl_account_input_calc (gl_account_input_calc_id, gl_account_id, input_sequence_num, gl_account_id_ref, factor_calculator, gl_fiscal_type_id, created_by_user_login, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
  SELECT 'IC_A111_2', gl_account_id, '2', NULL, 'B', 'PAR_A111_2', 'admin', now(), now(), now(), now() FROM gl_account WHERE upper(account_code)='A111' AND gl_account_type_id='WECAL';

-- ============================ ST76 (SUM(A), conteggio 4 addendi) ============================
UPDATE gl_account SET calc_custom_method_id='SUM(A)', last_updated_stamp=now(), last_updated_tx_stamp=now()
  WHERE upper(account_code)='ST76' AND gl_account_type_id='WECAL';
DELETE FROM gl_account_input_calc WHERE gl_account_id IN (SELECT gl_account_id FROM gl_account WHERE upper(account_code)='ST76' AND gl_account_type_id='WECAL');
DELETE FROM gl_fiscal_type WHERE gl_fiscal_type_id IN ('PAR_ST76_1','PAR_ST76_2','PAR_ST76_3','PAR_ST76_4');
INSERT INTO gl_fiscal_type (gl_fiscal_type_id, description, gl_fiscal_type_enum_id, is_financial_used, is_account_used, is_indicator_used, created_by_user_login, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES
  ('PAR_ST76_1', 'n. studi multicentrici attivi (pazienti arruolati e/o follow-up)', 'GLFISCTYPE_ACTUAL', 'N', 'N', 'Y', 'admin', now(), now(), now(), now()),
  ('PAR_ST76_2', 'n. trial clinici attivi (pazienti arruolati e/o follow-up)',       'GLFISCTYPE_ACTUAL', 'N', 'N', 'Y', 'admin', now(), now(), now(), now()),
  ('PAR_ST76_3', 'n. studi di ricerca clinica finanziati (UOC = PI)',                'GLFISCTYPE_ACTUAL', 'N', 'N', 'Y', 'admin', now(), now(), now(), now()),
  ('PAR_ST76_4', 'n. progetti di ricerca clinica finanziati (UOC = PI)',             'GLFISCTYPE_ACTUAL', 'N', 'N', 'Y', 'admin', now(), now(), now(), now());
INSERT INTO gl_account_input_calc (gl_account_input_calc_id, gl_account_id, input_sequence_num, gl_account_id_ref, factor_calculator, gl_fiscal_type_id, created_by_user_login, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
  SELECT 'IC_ST76_1', gl_account_id, '1', NULL, 'A', 'PAR_ST76_1', 'admin', now(), now(), now(), now() FROM gl_account WHERE upper(account_code)='ST76' AND gl_account_type_id='WECAL';
INSERT INTO gl_account_input_calc (gl_account_input_calc_id, gl_account_id, input_sequence_num, gl_account_id_ref, factor_calculator, gl_fiscal_type_id, created_by_user_login, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
  SELECT 'IC_ST76_2', gl_account_id, '2', NULL, 'A', 'PAR_ST76_2', 'admin', now(), now(), now(), now() FROM gl_account WHERE upper(account_code)='ST76' AND gl_account_type_id='WECAL';
INSERT INTO gl_account_input_calc (gl_account_input_calc_id, gl_account_id, input_sequence_num, gl_account_id_ref, factor_calculator, gl_fiscal_type_id, created_by_user_login, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
  SELECT 'IC_ST76_3', gl_account_id, '3', NULL, 'A', 'PAR_ST76_3', 'admin', now(), now(), now(), now() FROM gl_account WHERE upper(account_code)='ST76' AND gl_account_type_id='WECAL';
INSERT INTO gl_account_input_calc (gl_account_input_calc_id, gl_account_id, input_sequence_num, gl_account_id_ref, factor_calculator, gl_fiscal_type_id, created_by_user_login, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
  SELECT 'IC_ST76_4', gl_account_id, '4', NULL, 'A', 'PAR_ST76_4', 'admin', now(), now(), now(), now() FROM gl_account WHERE upper(account_code)='ST76' AND gl_account_type_id='WECAL';

-- ============================ A114 ((A-B)/B*100, variazione % - PS) ============================
-- Formula Obiettivi_09_09 (UOC Pronto Soccorso): A = % accessi PS con permanenza T0-T3 > 8h nell'anno
-- di valutazione; B = stessa % nell'anno precedente. Indicatore = (A-B)/B*100 (come A111).
-- NB: conta SOLO gli accessi seguiti da un Ricovero (confermato dal cliente 2026-09-10; la cella
--     "Formula di calcolo" non lo esplicitava ma la descrizione "Indicatore" della scheda si').
UPDATE gl_account SET calc_custom_method_id='(A-B)/B*100', last_updated_stamp=now(), last_updated_tx_stamp=now()
  WHERE upper(account_code)='A114' AND gl_account_type_id='WECAL';
DELETE FROM gl_account_input_calc WHERE gl_account_id IN (SELECT gl_account_id FROM gl_account WHERE upper(account_code)='A114' AND gl_account_type_id='WECAL');
DELETE FROM gl_fiscal_type WHERE gl_fiscal_type_id IN ('PAR_A114_1','PAR_A114_2');
INSERT INTO gl_fiscal_type (gl_fiscal_type_id, description, gl_fiscal_type_enum_id, is_financial_used, is_account_used, is_indicator_used, created_by_user_login, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES
  ('PAR_A114_1', '% accessi in PS con ricovero e permanenza T0-T3 > 8 ore (anno di valutazione)', 'GLFISCTYPE_ACTUAL', 'N', 'N', 'Y', 'admin', now(), now(), now(), now()),
  ('PAR_A114_2', '% accessi in PS con ricovero e permanenza T0-T3 > 8 ore (anno precedente)',     'GLFISCTYPE_ACTUAL', 'N', 'N', 'Y', 'admin', now(), now(), now(), now());
INSERT INTO gl_account_input_calc (gl_account_input_calc_id, gl_account_id, input_sequence_num, gl_account_id_ref, factor_calculator, gl_fiscal_type_id, created_by_user_login, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
  SELECT 'IC_A114_1', gl_account_id, '1', NULL, 'A', 'PAR_A114_1', 'admin', now(), now(), now(), now() FROM gl_account WHERE upper(account_code)='A114' AND gl_account_type_id='WECAL';
INSERT INTO gl_account_input_calc (gl_account_input_calc_id, gl_account_id, input_sequence_num, gl_account_id_ref, factor_calculator, gl_fiscal_type_id, created_by_user_login, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
  SELECT 'IC_A114_2', gl_account_id, '2', NULL, 'B', 'PAR_A114_2', 'admin', now(), now(), now(), now() FROM gl_account WHERE upper(account_code)='A114' AND gl_account_type_id='WECAL';

-- ============================ A115 ((A-B)/B*100, variazione % - PS OBI) ============================
-- Formula Obiettivi_09_09 (UOC PS OBI): A = % accessi in OBI con intervallo T0-T5 > 44h nell'anno di
-- valutazione; B = stessa % nell'anno precedente. Indicatore = (A-B)/B*100 (come A111).
UPDATE gl_account SET calc_custom_method_id='(A-B)/B*100', last_updated_stamp=now(), last_updated_tx_stamp=now()
  WHERE upper(account_code)='A115' AND gl_account_type_id='WECAL';
DELETE FROM gl_account_input_calc WHERE gl_account_id IN (SELECT gl_account_id FROM gl_account WHERE upper(account_code)='A115' AND gl_account_type_id='WECAL');
DELETE FROM gl_fiscal_type WHERE gl_fiscal_type_id IN ('PAR_A115_1','PAR_A115_2');
INSERT INTO gl_fiscal_type (gl_fiscal_type_id, description, gl_fiscal_type_enum_id, is_financial_used, is_account_used, is_indicator_used, created_by_user_login, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES
  ('PAR_A115_1', '% accessi in OBI con intervallo T0-T5 > 44 ore (anno di valutazione)', 'GLFISCTYPE_ACTUAL', 'N', 'N', 'Y', 'admin', now(), now(), now(), now()),
  ('PAR_A115_2', '% accessi in OBI con intervallo T0-T5 > 44 ore (anno precedente)',     'GLFISCTYPE_ACTUAL', 'N', 'N', 'Y', 'admin', now(), now(), now(), now());
INSERT INTO gl_account_input_calc (gl_account_input_calc_id, gl_account_id, input_sequence_num, gl_account_id_ref, factor_calculator, gl_fiscal_type_id, created_by_user_login, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
  SELECT 'IC_A115_1', gl_account_id, '1', NULL, 'A', 'PAR_A115_1', 'admin', now(), now(), now(), now() FROM gl_account WHERE upper(account_code)='A115' AND gl_account_type_id='WECAL';
INSERT INTO gl_account_input_calc (gl_account_input_calc_id, gl_account_id, input_sequence_num, gl_account_id_ref, factor_calculator, gl_fiscal_type_id, created_by_user_login, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
  SELECT 'IC_A115_2', gl_account_id, '2', NULL, 'B', 'PAR_A115_2', 'admin', now(), now(), now(), now() FROM gl_account WHERE upper(account_code)='A115' AND gl_account_type_id='WECAL';

COMMIT;

-- Verifica
SELECT ga.account_code, ga.calc_custom_method_id,
       string_agg(gft.description, ' | ' ORDER BY gaic.input_sequence_num) AS parametri
FROM gl_account ga
JOIN gl_account_input_calc gaic ON gaic.gl_account_id = ga.gl_account_id
JOIN gl_fiscal_type gft ON gft.gl_fiscal_type_id = gaic.gl_fiscal_type_id
WHERE upper(ga.account_code) IN ('A55','A58B','ST77','A111','A114','A115','ST76') AND ga.gl_account_type_id='WECAL'
GROUP BY ga.account_code, ga.calc_custom_method_id
ORDER BY ga.account_code;

-- =====================================================================
-- PARAMETRI INTERMEDI (PAR_*_INT) per il DOPPIO CICLO
-- =====================================================================
-- Variante "ciclo intermedio" di OGNI parametro (PAR_<cod>_<n>_INT): il referente, nella fase
-- intermedia (scheda WEORCARD_TOACC_INT, indicatori consuntivabileParzialmente='Y'), inserisce i
-- parametri parziali che vengono salvati con glFiscalTypeId=PAR_<cod>_<n>_INT, SEPARATI dai PAR_* del
-- ciclo finale. Senza questi fiscal type, createWeTrans fallisce sull'FK ("PAR_ST13_1_INT not exist")
-- e il salvataggio intermedio va in errore.
-- NB: DEVE stare QUI (post-import), NON in SETUP: i PAR_* vengono creati dagli import parametri
-- (INDICATORI + COMPOSITE, che girano prima di questo blocco); in SETUP i PAR_* non esistono ancora
-- (SELECT vuota -> 0 _INT). Idempotente (ON CONFLICT). Rigenerabile ad ogni post-import.
BEGIN;
INSERT INTO gl_fiscal_type (
    gl_fiscal_type_id, description, gl_fiscal_type_enum_id,
    is_financial_used, is_account_used, is_indicator_used,
    created_stamp, created_tx_stamp, last_updated_stamp, last_updated_tx_stamp)
SELECT gl_fiscal_type_id || '_INT', description || ' (intermedio)', gl_fiscal_type_enum_id,
       is_financial_used, is_account_used, is_indicator_used, NOW(),NOW(),NOW(),NOW()
FROM gl_fiscal_type
WHERE gl_fiscal_type_id LIKE 'PAR%' AND gl_fiscal_type_id NOT LIKE '%\_INT' ESCAPE '\'
ON CONFLICT (gl_fiscal_type_id) DO NOTHING;
COMMIT;

-- Verifica: n. PAR_*_INT deve eguagliare n. PAR_* finali.
SELECT
  (SELECT count(*) FROM gl_fiscal_type WHERE gl_fiscal_type_id LIKE 'PAR%' AND gl_fiscal_type_id NOT LIKE '%\_INT' ESCAPE '\') AS par_finali,
  (SELECT count(*) FROM gl_fiscal_type WHERE gl_fiscal_type_id LIKE 'PAR%\_INT' ESCAPE '\') AS par_intermedi;
