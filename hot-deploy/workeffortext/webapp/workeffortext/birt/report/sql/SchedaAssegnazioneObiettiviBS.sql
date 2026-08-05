-- Stampa Assegnazione Obiettivi Performance Strategica (CTX_BS) - Raccolta_Requisiti 8.1
-- Query JDBC per il report BIRT SchedaAssegnazioneObiettiviBS.rptdesign.
-- Parametro: ? = workEffortId (la scheda root CTX_BS). Validata su Postgres 2026-08-04.
-- Una riga per indicatore assegnato: campi sintesi + dettaglio (8.1).
-- SI_NO: valore atteso = 'Si', range = 'Esito Si/No' (niente bande numeriche fittizie).
-- Target numerico derivato dalla banda 100% (gestisce anche indicatori inversi, from_value = -999999).

SELECT
  gl.account_code                                   AS codice,
  grt.description                                   AS area,
  gl.description                                    AS descrizione,
  gl.account_name                                   AS indicatore,
  CASE WHEN gl.calc_custom_method_id = 'SI_NO' THEN 'Si/No'
       ELSE gl.calc_custom_method_id END            AS formula,
  gl.source                                         AS fonte,
  wem.kpi_score_weight                              AS peso,
  refpg.group_name                                  AS referente,
  CASE WHEN gl.calc_custom_method_id = 'SI_NO' THEN 'Si'
       ELSE (SELECT CASE WHEN rv.from_value <= -999999 THEN rv.thru_value::text
                         ELSE rv.from_value::text END
             FROM uom_range_values rv
             WHERE rv.uom_range_id = wem.uom_range_id AND rv.range_values_factor = 100
             ORDER BY rv.from_value LIMIT 1)
  END                                               AS valore_atteso,
  CASE WHEN gl.calc_custom_method_id = 'SI_NO' THEN 'Esito Si/No'
       ELSE (SELECT string_agg(
                CASE WHEN rv.from_value <= -999999 THEN '<=' || rv.thru_value
                     WHEN rv.thru_value >= 999999 THEN '>=' || rv.from_value
                     ELSE rv.from_value || '-' || rv.thru_value END
                || ' = ' || rv.range_values_factor || '%', '   ' ORDER BY rv.from_value)
             FROM uom_range_values rv WHERE rv.uom_range_id = wem.uom_range_id)
  END                                               AS range_fasce
FROM work_effort_measure wem
JOIN work_effort we      ON we.work_effort_id = wem.work_effort_id AND we.work_effort_type_id = 'CTX_BS'
JOIN gl_account gl       ON gl.gl_account_id = wem.gl_account_id
LEFT JOIN gl_resource_type grt ON grt.gl_resource_type_id = gl.gl_resource_type_id
LEFT JOIN gl_account_role gar  ON gar.gl_account_id = gl.gl_account_id AND gar.role_type_id = 'WEM_IND_IN_CHARGE'
LEFT JOIN party_group refpg    ON refpg.party_id = gar.party_id
WHERE wem.work_effort_id = ?
  AND (wem.is_invisible IS NULL OR wem.is_invisible <> 'Y')
ORDER BY wem.sequence_id, gl.account_code;
