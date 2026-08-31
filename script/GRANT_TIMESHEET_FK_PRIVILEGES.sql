-- =============================================================================
-- GRANT_TIMESHEET_FK_PRIVILEGES.sql
-- Privilegi necessari all'utente GZOOM per le foreign key di TIMESHEET
--
-- Esecuzione locale:
--   psql -h localhost -p 5433 -U <utente_amministratore> -d preprod \
--     -v app_user=postgres -f GRANT_TIMESHEET_FK_PRIVILEGES.sql
--
-- Nota: eseguire come proprietario delle tabelle o superuser PostgreSQL.
-- Il ruolo app_user deve coincidere con entityengine.jdbc-username dell'ambiente.
-- =============================================================================

\if :{?app_user}
\else
\echo 'ERRORE: specificare -v app_user=<utente_gzoom>'
\quit 1
\endif

\echo 'Concessione privilegi FK a :'app_user

BEGIN;

-- Permette al ruolo applicativo di usare lo schema public.
GRANT USAGE ON SCHEMA public TO :"app_user";

-- Necessario se GZOOM deve creare TIMESHEET quando la tabella manca
-- (add-missing-on-start=true nella configurazione Entity Engine).
GRANT CREATE ON SCHEMA public TO :"app_user";

-- Per creare le FK da TIMESHEET servono REFERENCES sulle tabelle target.
GRANT REFERENCES ON TABLE
    public.party,
    public.status_item,
    public.user_login,
    public.work_effort_type_period,
    public.uom
TO :"app_user";

COMMIT;

\echo 'Verifica privilegi REFERENCES:'
SELECT
    :'app_user' AS grantee,
    table_name,
    has_table_privilege(:'app_user', 'public.' || table_name, 'REFERENCES') AS references_ok
FROM (VALUES
    ('party'),
    ('status_item'),
    ('user_login'),
    ('work_effort_type_period'),
    ('uom')
) AS target_tables(table_name);

\echo 'Verifica proprietario TIMESHEET:'
SELECT
    table_schema,
    table_name,
    tableowner
FROM information_schema.tables
WHERE table_schema = 'public'
  AND table_name = 'timesheet';

\echo 'Se REFERENCES_OK e'' true ma l''avvio segnala ancora permesso negato,'
\echo 'l''utente GZOOM non e'' proprietario di TIMESHEET o delle tabelle target.'
\echo 'In quel caso eseguire lo script come proprietario/superuser oppure trasferire'
\echo 'la proprieta'' secondo le regole DBA; non usare GRANT ALL alla cieca.'
