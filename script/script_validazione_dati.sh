#!/bin/bash

###############################################################################
# SCRIPT DI VALIDAZIONE DATI - Template_Dipendenti_AORN.xlsx
###############################################################################
#
# Scopo: Validare la completezza e coerenza dei dati nel file Excel
#        Template_Dipendenti_AORN.xlsx prima dell'import massivo
#
# Questo script è un wrapper che invoca lo script Python
#
###############################################################################

# Determina la directory dello script
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

# Esegui lo script Python
echo "Esecuzione script di validazione..."
echo ""

# Verifica se Python è disponibile
# Su Windows con Git Bash, usa python.exe per invocare il Python di Windows
if command -v python.exe &> /dev/null; then
    PYTHON_CMD="python.exe"
elif command -v python3 &> /dev/null; then
    PYTHON_CMD="python3"
elif command -v python &> /dev/null; then
    PYTHON_CMD="python"
else
    echo "ERRORE: Python non trovato sul sistema!"
    echo "Installare Python 3.x per eseguire questo script."
    exit 1
fi

# Esegui lo script Python di validazione
$PYTHON_CMD "$SCRIPT_DIR/script_validazione_dati.py"
EXIT_CODE=$?

# Restituisci il codice di uscita dello script Python
exit $EXIT_CODE
