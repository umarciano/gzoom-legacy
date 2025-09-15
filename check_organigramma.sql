-- Verifica la struttura dell'organigramma Cardarelli
-- Query per verificare le relazioni gerarchiche

-- 1. Verifica la radice Company
SELECT 'Company' as check_type, party_id, party_type_id, status_id 
FROM party WHERE party_id = 'Company';

-- 2. Verifica i dipartimenti collegati a Company
SELECT 'Dipartimenti' as check_type, pr.party_id_from, pr.party_id_to, pg.group_name
FROM party_relationship pr
JOIN party_group pg ON pr.party_id_to = pg.party_id
WHERE pr.party_id_from = 'Company' 
  AND pr.party_relationship_type_id = 'GROUP_ROLLUP'
  AND pr.thru_date IS NULL
ORDER BY pg.group_name;

-- 3. Verifica le UOC collegate ai dipartimenti
SELECT 'UOC' as check_type, pr.party_id_from as dipartimento, pr.party_id_to as uoc, pg.group_name
FROM party_relationship pr
JOIN party_group pg ON pr.party_id_to = pg.party_id
WHERE pr.party_id_from IN (
    SELECT party_id_to FROM party_relationship 
    WHERE party_id_from = 'Company' 
      AND party_relationship_type_id = 'GROUP_ROLLUP'
      AND thru_date IS NULL
)
AND pr.party_relationship_type_id = 'GROUP_ROLLUP'
AND pr.thru_date IS NULL
ORDER BY pr.party_id_from, pg.group_name;

-- 4. Conta totale UOC per dipartimento
SELECT dept.party_id_to as dipartimento, 
       dept_pg.group_name as nome_dipartimento,
       COUNT(uoc.party_id_to) as numero_uoc
FROM party_relationship dept
LEFT JOIN party_group dept_pg ON dept.party_id_to = dept_pg.party_id
LEFT JOIN party_relationship uoc ON dept.party_id_to = uoc.party_id_from 
                                 AND uoc.party_relationship_type_id = 'GROUP_ROLLUP'
                                 AND uoc.thru_date IS NULL
WHERE dept.party_id_from = 'Company' 
  AND dept.party_relationship_type_id = 'GROUP_ROLLUP'
  AND dept.thru_date IS NULL
GROUP BY dept.party_id_to, dept_pg.group_name
ORDER BY dept_pg.group_name;
