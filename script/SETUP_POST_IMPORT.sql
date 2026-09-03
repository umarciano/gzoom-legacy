-- =====================================================================
-- SETUP POST-IMPORT — Performance Strategica (CTX_BS)
-- =====================================================================
-- Da eseguire DOPO l'import dati (catalogo -> misure+ruoli -> schede).
-- Esegue in ordine, in un colpo solo:
--   1) POST_IMPORT_ASSEGNA_PROFILI.sql       -> membership STRATPERF_REFERENTE/DIR_UO + WEM_PERF_IN_CHARGE
--                                               sulle schede (SENZA, il re-import ricrea le schede e i
--                                               direttori NON vedono la propria scheda: visibilita' rotta)
--   2) POST_IMPORT_FASCE_COMPLETO.sql        -> scale reali RNG_<UOC>_<codiceNEW> + scoring diretto
--   3) POST_IMPORT_PARAMETRI_INDICATORI.sql  -> parametri modale (gl_fiscal_type PAR_* + gl_account_input_calc)
--   4) POST_IMPORT_PARAMETRI_COMPOSITE.sql   -> parametri dei 5 indicatori composite (definizione manuale)
--   5) POST_IMPORT_FIX_RAPPORTI.sql          -> riclassifica i num/den "rapporto" (A/B*100 -> A/B, no x100)
--   6) POST_IMPORT_FIX_PARTYROLE_REFERENTE.sql -> completa party_role.parent_role_type_id dei referenti
--                                                 (altrimenti la lookup UI del referente resta vuota)
--   7) MIGRAZIONE_NOTE_VALIDAZIONE_STRATEGICA.sql -> crea le istanze note per-scheda (Nota Direttore UO /
--                                                    Amministrativo-Sanitario); senza, le note non compaiono
--
-- I primi due file sono generati (rispettivamente da genera_import_da_obiettivi.py e
-- genera_parametri_indicatori.py); il terzo e' scritto a mano. Questo wrapper li richiama con \ir
-- (percorso relativo alla posizione di QUESTO file), quindi funziona da qualsiasi cartella.
--
-- Uso: psql -h localhost -U postgres -d cardarelli -f SETUP_POST_IMPORT.sql

-- Le etichette dei parametri contengono accenti (à è ù); i file sono UTF-8.
SET client_encoding TO 'UTF8';

\echo '== POST-IMPORT 1/7: assegnazione profili + WEM_PERF_IN_CHARGE (visibilita direttori/referenti) =='
\ir POST_IMPORT_ASSEGNA_PROFILI.sql

\echo '== POST-IMPORT 2/7: fasce reali =='
\ir POST_IMPORT_FASCE_COMPLETO.sql

\echo '== POST-IMPORT 3/7: parametri indicatori (modale) =='
\ir POST_IMPORT_PARAMETRI_INDICATORI.sql

\echo '== POST-IMPORT 4/7: parametri indicatori composite (manuale) =='
\ir POST_IMPORT_PARAMETRI_COMPOSITE.sql

\echo '== POST-IMPORT 5/7: riclassifica rapporti (A/B*100 -> A/B) =='
\ir POST_IMPORT_FIX_RAPPORTI.sql

\echo '== POST-IMPORT 6/7: fix party_role referenti (parent_role_type_id) per lookup UI =='
\ir POST_IMPORT_FIX_PARTYROLE_REFERENTE.sql

\echo '== POST-IMPORT 7/7: note di validazione (Nota Direttore UO / San-Amm) per scheda =='
\ir MIGRAZIONE_NOTE_VALIDAZIONE_STRATEGICA.sql

\echo '== POST-IMPORT completato =='
