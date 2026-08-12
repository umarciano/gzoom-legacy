-- =============================================================================
-- MIGRAZIONE_NOTE_VALIDAZIONE_STRATEGICA.sql
-- Crea le note di validazione sulle schede Performance Strategica CTX_BS esistenti
--
-- Esecuzione:
--   psql -h <host> -U postgres -d cardarelli -f MIGRAZIONE_NOTE_VALIDAZIONE_STRATEGICA.sql
-- Dipendenze: SETUP_PERF_STRATEGICA.sql
-- =============================================================================

BEGIN;

-- NoteData: una nota Direttore UO e una nota Direttore Amministrativo/Sanitario per scheda.
INSERT INTO note_data (
  note_id, note_name, note_info, note_name_lang, note_info_lang,
    created_stamp, created_tx_stamp, last_updated_stamp, last_updated_tx_stamp
)
SELECT
    'BSU_' || substr(md5(we.work_effort_id), 1, 16),
    'Note Direttore UO', NULL,
    'Note Direttore UO', NULL,
    NOW(), NOW(), NOW(), NOW()
FROM work_effort we
WHERE we.work_effort_type_id = 'CTX_BS'
  AND EXISTS (
      SELECT 1
      FROM work_effort_type_attr wta
      WHERE wta.work_effort_type_id = 'CTX_BS'
        AND wta.attr_name = 'Note Direttore UO'
  )
  AND NOT EXISTS (
      SELECT 1
      FROM work_effort_note wen
      JOIN note_data nd ON nd.note_id = wen.note_id
      WHERE wen.work_effort_id = we.work_effort_id
        AND nd.note_name = 'Note Direttore UO'
  )
ON CONFLICT (note_id) DO NOTHING;

INSERT INTO note_data (
  note_id, note_name, note_info, note_name_lang, note_info_lang,
    created_stamp, created_tx_stamp, last_updated_stamp, last_updated_tx_stamp
)
SELECT
    'BSD_' || substr(md5(we.work_effort_id), 1, 16),
    'Note Direttore Amministrativo/Sanitario', NULL,
    'Note Direttore Amministrativo/Sanitario', NULL,
    NOW(), NOW(), NOW(), NOW()
FROM work_effort we
WHERE we.work_effort_type_id = 'CTX_BS'
  AND EXISTS (
      SELECT 1
      FROM work_effort_type_attr wta
      WHERE wta.work_effort_type_id = 'CTX_BS'
        AND wta.attr_name = 'Note Direttore Amministrativo/Sanitario'
  )
  AND NOT EXISTS (
      SELECT 1
      FROM work_effort_note wen
      JOIN note_data nd ON nd.note_id = wen.note_id
      WHERE wen.work_effort_id = we.work_effort_id
        AND nd.note_name = 'Note Direttore Amministrativo/Sanitario'
  )
ON CONFLICT (note_id) DO NOTHING;

-- WorkEffortNote: collega le note alla scheda e le rende note principali non pubblicate.
INSERT INTO work_effort_note (
    work_effort_id, note_id, internal_note, is_main, is_html, sequence_id, is_posted,
    created_stamp, created_tx_stamp, last_updated_stamp, last_updated_tx_stamp
)
SELECT
    we.work_effort_id, nd.note_id, 'Y', 'Y', 'N', 3, 'N',
    NOW(), NOW(), NOW(), NOW()
FROM work_effort we
JOIN note_data nd ON nd.note_id = 'BSU_' || substr(md5(we.work_effort_id), 1, 16)
WHERE we.work_effort_type_id = 'CTX_BS'
  AND NOT EXISTS (
      SELECT 1
      FROM work_effort_note wen
      WHERE wen.work_effort_id = we.work_effort_id
        AND wen.note_id = nd.note_id
  )
ON CONFLICT (work_effort_id, note_id) DO NOTHING;

INSERT INTO work_effort_note (
    work_effort_id, note_id, internal_note, is_main, is_html, sequence_id, is_posted,
    created_stamp, created_tx_stamp, last_updated_stamp, last_updated_tx_stamp
)
SELECT
    we.work_effort_id, nd.note_id, 'Y', 'Y', 'N', 4, 'N',
    NOW(), NOW(), NOW(), NOW()
FROM work_effort we
JOIN note_data nd ON nd.note_id = 'BSD_' || substr(md5(we.work_effort_id), 1, 16)
WHERE we.work_effort_type_id = 'CTX_BS'
  AND NOT EXISTS (
      SELECT 1
      FROM work_effort_note wen
      WHERE wen.work_effort_id = we.work_effort_id
        AND wen.note_id = nd.note_id
  )
ON CONFLICT (work_effort_id, note_id) DO NOTHING;

COMMIT;

-- Verifica: per ogni scheda CTX_BS devono risultare entrambe le note.
SELECT we.work_effort_id, nd.note_name, wen.note_id
FROM work_effort we
JOIN work_effort_note wen ON wen.work_effort_id = we.work_effort_id
JOIN note_data nd ON nd.note_id = wen.note_id
WHERE we.work_effort_type_id = 'CTX_BS'
  AND nd.note_name IN ('Note Direttore UO', 'Note Direttore Amministrativo/Sanitario')
ORDER BY we.work_effort_id, nd.note_name;
