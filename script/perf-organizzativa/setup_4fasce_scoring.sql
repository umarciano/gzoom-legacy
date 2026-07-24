-- =============================================================================
-- setup_4fasce_scoring.sql
-- Configurazione scoring a 4 fasce per obiettivi Performance Organizzativa
-- (A soglia, SI/NO, Report) - CTX_BS
--
-- Contesto tecnico:
--   WECONVER_4PERCLIMITS legge 4 soglie da WorkEffortTransaction e restituisce
--   un valore -1..301 che WESCORE_DIRECTRANGE mappa su rangeValuesFactor.
--   La catena completa e':
--     actual < SOGLIA_50  --> converter: -1     --> DirectRange: 0%
--     actual < BUDGET     --> converter: 0-100  --> DirectRange: 50%
--     actual < SOGLIA_100 --> converter: 100-200--> DirectRange: 75%
--     actual >= SOGLIA_100--> converter: 200-301--> DirectRange: 100%
--
-- Pre-requisiti verificati:
--   - content 'WEFLD_ELAB' esiste (content_type_id = 'WEFLD_ELAB')
--   - uom 'OTH_SCO' esiste
--   - enumeration RED, YELLOW, GREEN esistono
--
-- Esecuzione:
--   psql -U postgres -d cardarelli -f V001__perf_organizzativa_4fasce_scoring.sql
-- =============================================================================

BEGIN;

-- ---------------------------------------------------------------------------
-- 1. GlFiscalType — nuovi tipi di rilevazione per le soglie
-- ---------------------------------------------------------------------------

INSERT INTO gl_fiscal_type (
    gl_fiscal_type_id,
    description,
    gl_fiscal_type_enum_id,
    is_financial_used,
    is_account_used,
    is_indicator_used,
    created_stamp,
    created_tx_stamp,
    last_updated_stamp,
    last_updated_tx_stamp
)
VALUES
    (
        'SOGLIA_50',
        'Soglia 50% (banda inferiore)',
        'GLFISCTYPE_TARGET',
        'N', 'N', 'Y',
        NOW(), NOW(), NOW(), NOW()
    ),
    (
        'SOGLIA_100',
        'Soglia 100% (obiettivo pieno)',
        'GLFISCTYPE_TARGET',
        'N', 'N', 'Y',
        NOW(), NOW(), NOW(), NOW()
    )
ON CONFLICT (gl_fiscal_type_id) DO NOTHING;


-- ---------------------------------------------------------------------------
-- 2. UomRange — scala di punteggio discreta a 4 fasce
-- ---------------------------------------------------------------------------

INSERT INTO uom_range (
    uom_range_id,
    uom_id,
    description,
    created_stamp,
    created_tx_stamp,
    last_updated_stamp,
    last_updated_tx_stamp
)
VALUES (
    'PERF_4FASCE',
    'OTH_SCO',
    'Performance 4 Fasce (0/50/75/100%)',
    NOW(), NOW(), NOW(), NOW()
)
ON CONFLICT (uom_range_id) DO NOTHING;


-- ---------------------------------------------------------------------------
-- 3. UomRangeValues — 4 bande per WESCORE_DIRECTRANGE
-- ---------------------------------------------------------------------------

INSERT INTO uom_range_values (
    uom_range_values_id,
    uom_range_id,
    from_value,
    thru_value,
    range_values_factor,
    range_values_factor_min,
    is_positive,
    color_enum_id,
    alert,
    prorate_range,
    comments,
    created_stamp,
    created_tx_stamp,
    last_updated_stamp,
    last_updated_tx_stamp
)
VALUES
    -- Sotto soglia minima (actual < SOGLIA_50): converter restituisce -1
    (
        'P4F_000', 'PERF_4FASCE',
        -1.0, -1.0,
        0.0, 0.0,
        'Y', 'RED', 'N', 'N',
        'Sotto soglia minima (SOGLIA_50) - punteggio 0%',
        NOW(), NOW(), NOW(), NOW()
    ),
    -- Banda 50% (SOGLIA_50 <= actual < BUDGET): converter restituisce 0..99.99
    (
        'P4F_050', 'PERF_4FASCE',
        0.0, 99.99,
        50.0, 50.0,
        'Y', 'RED', 'N', 'N',
        'Banda 50%: actual tra SOGLIA_50 e BUDGET',
        NOW(), NOW(), NOW(), NOW()
    ),
    -- Banda 75% (BUDGET <= actual < SOGLIA_100): converter restituisce 100..199.99
    (
        'P4F_075', 'PERF_4FASCE',
        100.0, 199.99,
        75.0, 75.0,
        'Y', 'YELLOW', 'N', 'N',
        'Banda 75%: actual tra BUDGET e SOGLIA_100',
        NOW(), NOW(), NOW(), NOW()
    ),
    -- Banda 100% (actual >= SOGLIA_100): converter restituisce 200..301
    (
        'P4F_100', 'PERF_4FASCE',
        200.0, 301.0,
        100.0, 100.0,
        'Y', 'GREEN', 'N', 'N',
        'Obiettivo raggiunto (>= SOGLIA_100) - punteggio 100%',
        NOW(), NOW(), NOW(), NOW()
    )
ON CONFLICT (uom_range_values_id) DO NOTHING;


-- ---------------------------------------------------------------------------
-- 4. WorkEffortTypeContent — parametri BeanShell per CTX_OR e CTX_BS
--    Letti da WorkEffortTypeCntParamsEvaluator (contentId = 'WEFLD_ELAB')
--    Sovrascrivono il default di ElaboreteScoreIndicServices (tutto = BUDGET)
-- ---------------------------------------------------------------------------

INSERT INTO work_effort_type_content (
    work_effort_type_id,
    content_id,
    we_type_content_type_id,
    params,
    created_stamp,
    created_tx_stamp,
    last_updated_stamp,
    last_updated_tx_stamp
)
VALUES
    (
        'CTX_OR',
        'WEFLD_ELAB',
        'WEFLD_ELAB',
        'glFiscalTypeIdExcellentLimit = "SOGLIA_100"; glFiscalTypeIdUpperLimit = "SOGLIA_100"; glFiscalTypeIdLowerLimit = "SOGLIA_50";',
        NOW(), NOW(), NOW(), NOW()
    ),
    (
        'CTX_BS',
        'WEFLD_ELAB',
        'WEFLD_ELAB',
        'glFiscalTypeIdExcellentLimit = "SOGLIA_100"; glFiscalTypeIdUpperLimit = "SOGLIA_100"; glFiscalTypeIdLowerLimit = "SOGLIA_50";',
        NOW(), NOW(), NOW(), NOW()
    )
ON CONFLICT (work_effort_type_id, content_id) DO UPDATE
    SET params                = EXCLUDED.params,
        last_updated_stamp    = NOW(),
        last_updated_tx_stamp = NOW();


COMMIT;


-- =============================================================================
-- ROLLBACK (commentato — eseguire solo in caso di ripristino)
-- =============================================================================
--
-- BEGIN;
--
-- -- 4. Rimuove il record BeanShell da CTX_OR
-- DELETE FROM work_effort_type_content
-- WHERE work_effort_type_id = 'CTX_OR'
--   AND content_id          = 'WEFLD_ELAB';
--
-- -- 3. Rimuove le bande della scala PERF_4FASCE
-- DELETE FROM uom_range_values
-- WHERE uom_range_id = 'PERF_4FASCE';
--
-- -- 2. Rimuove la scala
-- DELETE FROM uom_range
-- WHERE uom_range_id = 'PERF_4FASCE';
--
-- -- 1. Rimuove i tipi di rilevazione soglia
-- --    ATTENZIONE: fallisce se esistono WorkEffortTransaction che usano questi tipi.
-- --    Verificare prima con:
-- --      SELECT COUNT(*) FROM acctg_trans_entry WHERE gl_fiscal_type_id IN ('SOGLIA_50','SOGLIA_100');
-- DELETE FROM gl_fiscal_type
-- WHERE gl_fiscal_type_id IN ('SOGLIA_50', 'SOGLIA_100');
--
-- COMMIT;
