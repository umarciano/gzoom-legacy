#!/bin/bash
# =====================================================================
# CONFIGURE_PERF_ORGANIZZATIVA.SH
# =====================================================================
# Configura l'infrastruttura Performance Organizzativa CTX_BS per
# AORN Cardarelli. Eseguire nell'ordine corretto:
#   1. setup_4fasce_scoring.sql
#   2. setup_workflow_stati.sql
#   3. ../profile-permissions/setup_orgperf_dir_uo_profile.sql
#
# Uso:
#   ./configure_perf_organizzativa.sh [DB_HOST] [DB_PORT] [DB_NAME] [DB_USER]
# =====================================================================

RED='\033[0;31m'; GREEN='\033[0;32m'; YELLOW='\033[1;33m'; BLUE='\033[0;34m'; NC='\033[0m'

DB_HOST=${1:-localhost}
DB_PORT=${2:-5432}
DB_NAME=${3:-cardarelli}
DB_USER=${4:-postgres}

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
LOG_FILE="$SCRIPT_DIR/configure_perf_organizzativa_$(date +%Y%m%d_%H%M%S).log"

SQL_FILES=(
    "$SCRIPT_DIR/setup_4fasce_scoring.sql"
    "$SCRIPT_DIR/setup_workflow_stati.sql"
    "$SCRIPT_DIR/../profile-permissions/setup_orgperf_dir_uo_profile.sql"
)

log() { echo "[$(date '+%Y-%m-%d %H:%M:%S')] [$1] $2" | tee -a "$LOG_FILE"; }

if ! command -v psql &> /dev/null; then
    echo -e "${RED}ERRORE: psql non trovato nel PATH${NC}"; exit 1
fi

echo -e "${BLUE}====================================================${NC}"
echo -e "${BLUE} CONFIGURAZIONE PERFORMANCE ORGANIZZATIVA CTX_BS   ${NC}"
echo -e "${BLUE}====================================================${NC}"
echo "  Host: $DB_HOST | Port: $DB_PORT | DB: $DB_NAME | User: $DB_USER"
echo "  Log: $LOG_FILE"
echo ""
echo -e "${YELLOW}Premere INVIO per continuare o CTRL+C per annullare...${NC}"
read

success=0; errors=0

for sql_file in "${SQL_FILES[@]}"; do
    name=$(basename "$sql_file")
    echo ""
    echo -e "${YELLOW}Esecuzione: $name${NC}"
    log "INFO" "Inizio: $name"

    output=$(PGPASSWORD="$PGPASSWORD" psql -h "$DB_HOST" -p "$DB_PORT" -d "$DB_NAME" -U "$DB_USER" -f "$sql_file" 2>&1)
    code=$?
    echo "$output" >> "$LOG_FILE"

    if [ $code -eq 0 ]; then
        echo -e "${GREEN}✓ $name${NC}"; log "SUCCESS" "$name"; ((success++))
    else
        echo -e "${RED}✗ $name${NC}"; echo "$output"; log "ERROR" "$name (exit $code)"; ((errors++))
    fi
done

echo ""
echo -e "${BLUE}====================================================${NC}"
echo "Successi: ${success} | Errori: ${errors}"
[ $errors -eq 0 ] && echo -e "${GREEN}✓ Configurazione completata.${NC}" || echo -e "${RED}✗ Completata con errori — vedere $LOG_FILE${NC}"
[ $errors -eq 0 ] && exit 0 || exit 1
