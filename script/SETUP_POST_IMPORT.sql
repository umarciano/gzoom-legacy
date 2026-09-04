-- =====================================================================
-- SETUP POST-IMPORT — Performance Strategica (CTX_BS)
-- =====================================================================
-- Da eseguire DOPO l'import dati (catalogo -> misure+ruoli -> schede).
-- Esegue in ordine, in un colpo solo:
--   1) POST_IMPORT_ASSEGNA_PROFILI.sql       -> membership STRATPERF_REFERENTE/DIR_UO + WEM_PERF_IN_CHARGE
--                                               sulle schede (SENZA, il re-import ricrea le schede e i
--                                               direttori NON vedono la propria scheda: visibilita' rotta)
--   2) POST_IMPORT_FASCE_COMPLETO.sql        -> scale reali RNG_<UOC>_<codiceNEW> + scoring diretto
--   3) POST_IMPORT_FIX_FASCE_MANUALI.sql     -> fasce SOLO-piattaforma, NON nell'Excel (es. E21 target 6,
--                                               dove la VLOOKUP dei Range torna vuota); da tenere aggiornato a mano
--   4) POST_IMPORT_PARAMETRI_INDICATORI.sql  -> parametri modale (gl_fiscal_type PAR_* + gl_account_input_calc)
--   5) POST_IMPORT_PARAMETRI_COMPOSITE.sql   -> parametri dei 5 indicatori composite (definizione manuale)
--   6) POST_IMPORT_FIX_RAPPORTI.sql          -> riclassifica i num/den "rapporto" (A/B*100 -> A/B, no x100)
--   7) POST_IMPORT_FIX_PARTYROLE_REFERENTE.sql -> completa party_role.parent_role_type_id dei referenti
--                                                 (altrimenti la lookup UI del referente resta vuota)
--   8) MIGRAZIONE_NOTE_VALIDAZIONE_STRATEGICA.sql -> crea le istanze note per-scheda (Nota Direttore UO /
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

\echo '== POST-IMPORT 2/8: fasce reali (da Excel) =='
\ir POST_IMPORT_FASCE_COMPLETO.sql

\echo '== POST-IMPORT 3/8: fasce manuali solo-piattaforma (non nell Excel, es. E21) =='
\ir POST_IMPORT_FIX_FASCE_MANUALI.sql

\echo '== POST-IMPORT 4/8: parametri indicatori (modale) =='
\ir POST_IMPORT_PARAMETRI_INDICATORI.sql

\echo '== POST-IMPORT 5/8: parametri indicatori composite (manuale) =='
\ir POST_IMPORT_PARAMETRI_COMPOSITE.sql

\echo '== POST-IMPORT 6/8: riclassifica rapporti (A/B*100 -> A/B) =='
\ir POST_IMPORT_FIX_RAPPORTI.sql

\echo '== POST-IMPORT 7/8: fix party_role referenti (parent_role_type_id) per lookup UI =='
\ir POST_IMPORT_FIX_PARTYROLE_REFERENTE.sql

\echo '== POST-IMPORT 8/8: note di validazione (Nota Direttore UO / San-Amm) per scheda =='
\ir MIGRAZIONE_NOTE_VALIDAZIONE_STRATEGICA.sql

\echo '== POST-IMPORT completato =='
