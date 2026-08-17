-- ============================================================================
-- FIX classificazione RAPPORTI (A/B) — Performance Strategica (CTX_BS)
-- Post-import idempotente: alcuni indicatori num/den NON sono percentuali ma RAPPORTI in
-- unita' naturale (Target SENZA '%' nel master): il valore = num/den SENZA x100, confrontato
-- con fasce assolute.
-- NB: dal 2026-08-17 la colonna 'Tipologia' del catalogo (IndicatoriCatalogo_BS.xlsx) e' gia'
-- CORRETTA (i rapporti = 'A/B'), quindi un reimport pulito produce gia' i valori giusti e questo
-- script diventa un NO-OP. Resta come rete di sicurezza (idempotente) per DB importati da un
-- catalogo vecchio. Generatori corretti: riconcilia::tipologia (Target-based) + genera_import (merge bande).
--
-- Da eseguire DOPO l'import del catalogo. Idempotente. C33/A66/A03* non sono su schede CTX_BS.
-- Le fasce di ST59 (era su scala generica errata) sono ora nel canonico POST_IMPORT_FASCE_COMPLETO.sql
-- (RNG_BAA9905_ST59), da eseguire prima/insieme a questo.
-- ============================================================================
BEGIN;

-- RAPPORTI: da 'A/B*100' a 'A/B' (num/den senza x100; fasce gia' in unita' naturale).
-- Presenti su CTX_BS: ST15 (ore/FTE), ST82 (ore/richiesta), ST73/ST73B (365/rotazione), A84.
-- (A03/A03b/A03c sono rapporti ma non assegnati a schede CTX_BS.)
UPDATE gl_account SET calc_custom_method_id = 'A/B', last_updated_stamp = now()
WHERE upper(account_code) IN ('ST15','ST82','ST73','ST73B','A84','A03','A03B','A03C')
  AND calc_custom_method_id = 'A/B*100';

COMMIT;

-- Verifica
SELECT account_code, calc_custom_method_id FROM gl_account
 WHERE upper(account_code) IN ('ST15','ST82','ST73','ST73B','A84') ORDER BY 1;
