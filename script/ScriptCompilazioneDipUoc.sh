#!/bin/bash
# Script bash per eseguire lo script Python di compilazione Dipartimenti/UOC
# Compatibile con Linux/macOS

# Colori per output
RED='\033[0;31m'
GREEN='\033[0;32m'
CYAN='\033[0;36m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Directory dello script
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
PYTHON_SCRIPT="$SCRIPT_DIR/script_compilazione_dip_uoc.py"

echo -e "${CYAN}=== Script Compilazione Dipartimenti/UOC ===${NC}"

# Verifica che Python3 sia installato
if ! command -v python3 &> /dev/null; then
    echo -e "${RED}ERRORE: Python3 non trovato. Installare Python3 per continuare.${NC}"
    exit 1
fi

# Verifica che pip sia installato
if ! command -v pip3 &> /dev/null; then
    echo -e "${YELLOW}ATTENZIONE: pip3 non trovato. Tentativo di installazione dipendenze potrebbe fallire.${NC}"
fi

# Verifica e installa le dipendenze se necessario
REQUIREMENTS_FILE="$SCRIPT_DIR/requirements.txt"
if [ -f "$REQUIREMENTS_FILE" ]; then
    echo -e "${CYAN}Verifica dipendenze Python...${NC}"
    pip3 install -q -r "$REQUIREMENTS_FILE"
    if [ $? -ne 0 ]; then
        echo -e "${YELLOW}ATTENZIONE: Alcune dipendenze potrebbero non essere state installate correttamente.${NC}"
    fi
else
    echo -e "${YELLOW}ATTENZIONE: File requirements.txt non trovato.${NC}"
fi

# Esegui lo script Python
echo -e "${CYAN}Esecuzione script Python...${NC}"
python3 "$PYTHON_SCRIPT"

exit $?
