#!/bin/bash
# =====================================================================
# CONFIGURE_PROFILES_AORN.SH
# =====================================================================
# Script per configurare i profili di sicurezza AORN nel database PostgreSQL
# 
# Profili configurati:
# 1. AORNADMIN - Amministratore di Sistema AORN
# 2. EMPLPERF_VALUTATO - Dipendente valutato
# 3. EMPLPERF_VALUTATORE - Valutatore
#
# Uso:
#   ./configure_profiles_aorn.sh [DB_HOST] [DB_PORT] [DB_NAME] [DB_USER]
#
# Parametri opzionali (valori di default se non specificati):
#   DB_HOST   - Host del database PostgreSQL (default: localhost)
#   DB_PORT   - Porta del database PostgreSQL (default: 5432)
#   DB_NAME   - Nome del database (default: ofbiz)
#   DB_USER   - Utente del database (default: ofbiz)
#
# NOTA: La password verrà richiesta interattivamente per ogni file SQL
#       oppure può essere impostata nella variabile d'ambiente PGPASSWORD
# =====================================================================

# Colori per output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Parametri database (con valori di default)
DB_HOST=${1:-localhost}
DB_PORT=${2:-5432}
DB_NAME=${3:-ofbiz}
DB_USER=${4:-ofbiz}

# Directory dello script
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# File di log
LOG_FILE="$SCRIPT_DIR/configure_profiles_aorn_$(date +%Y%m%d_%H%M%S).log"

# File SQL da eseguire (nell'ordine corretto)
SQL_FILES=(
    "setup_aornadmin_profile.sql"
    "setup_valutato_profile.sql"
    "setup_valutatore_profile.sql"
)

# =====================================================================
# FUNZIONI
# =====================================================================

# Funzione per stampare messaggi con timestamp
log_message() {
    local level=$1
    shift
    local message=$@
    local timestamp=$(date '+%Y-%m-%d %H:%M:%S')
    echo "[$timestamp] [$level] $message" | tee -a "$LOG_FILE"
}

# Funzione per stampare header
print_header() {
    echo -e "${BLUE}=====================================================================${NC}"
    echo -e "${BLUE}$1${NC}"
    echo -e "${BLUE}=====================================================================${NC}"
    log_message "INFO" "$1"
}

# Funzione per verificare se psql è installato
check_psql() {
    if ! command -v psql &> /dev/null; then
        echo -e "${RED}ERRORE: psql non è installato o non è nel PATH${NC}"
        log_message "ERROR" "psql non trovato"
        exit 1
    fi
}

# Funzione per eseguire un file SQL
execute_sql_file() {
    local sql_file=$1
    local sql_path="$SCRIPT_DIR/$sql_file"
    
    if [ ! -f "$sql_path" ]; then
        echo -e "${RED}ERRORE: File non trovato: $sql_path${NC}"
        log_message "ERROR" "File non trovato: $sql_path"
        return 1
    fi
    
    echo -e "${YELLOW}Esecuzione di: $sql_file${NC}"
    log_message "INFO" "Inizio esecuzione: $sql_file"
    
    # Esegui il file SQL e cattura output ed errori
    local output
    local exit_code
    
    output=$(psql -h "$DB_HOST" -p "$DB_PORT" -d "$DB_NAME" -U "$DB_USER" -f "$sql_path" 2>&1)
    exit_code=$?
    
    # Registra l'output nel log
    echo "$output" >> "$LOG_FILE"
    
    if [ $exit_code -eq 0 ]; then
        echo -e "${GREEN}✓ Successo: $sql_file eseguito correttamente${NC}"
        log_message "SUCCESS" "$sql_file eseguito con successo"
        return 0
    else
        echo -e "${RED}✗ Errore nell'esecuzione di: $sql_file${NC}"
        echo -e "${RED}Output:${NC}"
        echo "$output"
        log_message "ERROR" "Errore nell'esecuzione di $sql_file - Exit code: $exit_code"
        return 1
    fi
}

# =====================================================================
# MAIN
# =====================================================================

print_header "CONFIGURAZIONE PROFILI DI SICUREZZA AORN"

echo ""
echo "Parametri di connessione:"
echo "  Host:     $DB_HOST"
echo "  Porta:    $DB_PORT"
echo "  Database: $DB_NAME"
echo "  Utente:   $DB_USER"
echo ""
echo "File di log: $LOG_FILE"
echo ""

log_message "INFO" "Parametri: Host=$DB_HOST, Port=$DB_PORT, Database=$DB_NAME, User=$DB_USER"

# Verifica prerequisiti
check_psql

# Chiedi conferma prima di procedere
echo -e "${YELLOW}Premere INVIO per continuare o CTRL+C per annullare...${NC}"
read

# Contatori
total_files=${#SQL_FILES[@]}
success_count=0
error_count=0

# Esegui ogni file SQL
for sql_file in "${SQL_FILES[@]}"; do
    echo ""
    execute_sql_file "$sql_file"
    
    if [ $? -eq 0 ]; then
        ((success_count++))
    else
        ((error_count++))
    fi
    
    # Pausa tra un file e l'altro
    sleep 1
done

# =====================================================================
# RIEPILOGO FINALE
# =====================================================================

echo ""
print_header "RIEPILOGO ESECUZIONE"

echo ""
echo "File totali:     $total_files"
echo -e "Successi:        ${GREEN}$success_count${NC}"
echo -e "Errori:          ${RED}$error_count${NC}"
echo ""

log_message "INFO" "Riepilogo: Totali=$total_files, Successi=$success_count, Errori=$error_count"

if [ $error_count -eq 0 ]; then
    echo -e "${GREEN}✓ Tutti i profili sono stati configurati correttamente!${NC}"
    log_message "SUCCESS" "Configurazione completata con successo"
    exit 0
else
    echo -e "${RED}✗ Alcuni profili non sono stati configurati correttamente.${NC}"
    echo -e "${YELLOW}Controllare il file di log per dettagli: $LOG_FILE${NC}"
    log_message "WARNING" "Configurazione completata con errori"
    exit 1
fi
