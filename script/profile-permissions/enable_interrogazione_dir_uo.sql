-- =====================================================================
-- Abilita "Consultazione > Interrogazione Schede Strategiche" al Direttore UO
-- =====================================================================
-- security_group_content e' una lista di ESCLUSIONI: per far VEDERE l'Interrogazione al
-- gruppo ORGPERF_DIR_UO basta RIMUOVERE le esclusioni della sezione Consultazione (GP_MENU_00402)
-- e della voce Interrogazione Schede Strategiche (GP_MENU_00104).
-- I profili ORGPERF_DIR_SAN / ORGPERF_DIR_AMM le hanno gia' (creati senza queste esclusioni).
-- Idempotente (DELETE). Lo scoping in Interrogazione e' gestito dal groovy
-- executePerformFindBSWorkEffortRootInqy.groovy (DIR_UO -> solo le proprie UO, tutti gli stati).
-- =====================================================================

DELETE FROM security_group_content
 WHERE group_id='ORGPERF_DIR_UO' AND content_id IN ('GP_MENU_00402','GP_MENU_00104');

-- Verifica: DIR_UO deve passare da 152 a 150 esclusioni (come SAN/AMM)
SELECT group_id, COUNT(*) AS esclusioni_menu
  FROM security_group_content
 WHERE group_id IN ('ORGPERF_DIR_UO','ORGPERF_DIR_SAN','ORGPERF_DIR_AMM')
 GROUP BY group_id ORDER BY group_id;
