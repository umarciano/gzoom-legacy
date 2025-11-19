#!/bin/bash

###############################################################################
# SCRIPT COMPLETO: VALIDAZIONE + IMPORT MASSIVO
###############################################################################
# 
# Questo script esegue:
# 1. Validazione dati nel Template_Dipendenti_AORN.xlsx
# 2. Se validazione OK, esegue gli script di import in sequenza
#
###############################################################################

# Colori per output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# Determina la directory dello script
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

echo ""
echo -e "${CYAN}============================================================================${NC}"
echo -e "${CYAN}  PROCESSO COMPLETO: VALIDAZIONE + IMPORT MASSIVO DIPENDENTI${NC}"
echo -e "${CYAN}============================================================================${NC}"
echo ""

# ============================================================================
# FASE 1: VALIDAZIONE DATI
# ============================================================================

echo -e "${YELLOW}FASE 1: Validazione dati Template_Dipendenti_AORN.xlsx${NC}"
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
    echo -e "${RED}ERRORE: Python non trovato sul sistema!${NC}"
    echo "Installare Python 3.x per eseguire questo script."
    exit 1
fi

# Esegui validazione
$PYTHON_CMD "$SCRIPT_DIR/script_validazione_dati.py"
EXIT_CODE=$?

if [ $EXIT_CODE -ne 0 ]; then
    echo ""
    echo -e "${RED}============================================================================${NC}"
    echo -e "${RED}  PROCESSO INTERROTTO: Validazione fallita!${NC}"
    echo -e "${RED}============================================================================${NC}"
    echo ""
    echo -e "${YELLOW}Correggere gli errori nel file Excel prima di procedere.${NC}"
    echo ""
    exit 1
fi

echo ""
echo -e "${GREEN}✓ Validazione completata con successo!${NC}"
echo ""
echo -e "${YELLOW}Premere INVIO per continuare con l'import oppure CTRL+C per annullare...${NC}"
read -r

# ============================================================================
# FASE 2: COMPILAZIONE ANAGRAFICA
# ============================================================================

echo ""
echo -e "${CYAN}============================================================================${NC}"
echo -e "${CYAN}  FASE 2: Compilazione file IMPORT_RISORSE_UMANE.xlsx${NC}"
echo -e "${CYAN}============================================================================${NC}"
echo ""

# Nota: Questi script sono in PowerShell, quindi su Linux/Mac richiedono PowerShell Core
# Oppure devono essere convertiti in script Python/Bash equivalenti
if command -v pwsh &> /dev/null; then
    pwsh -File "$SCRIPT_DIR/ScriptCompilazioneAnagrafica.ps1"
    EXIT_CODE=$?
else
    echo -e "${YELLOW}NOTA: PowerShell Core non trovato.${NC}"
    echo -e "${YELLOW}Gli script di compilazione sono disponibili solo per Windows/PowerShell.${NC}"
    echo -e "${YELLOW}Eseguire manualmente su sistema Windows.${NC}"
    exit 0
fi

if [ $EXIT_CODE -ne 0 ]; then
    echo ""
    echo -e "${RED}ERRORE durante la compilazione dell'anagrafica!${NC}"
    exit 1
fi

# ============================================================================
# FASE 3: COMPILAZIONE DIPARTIMENTI E UOC
# ============================================================================

echo ""
echo -e "${CYAN}============================================================================${NC}"
echo -e "${CYAN}  FASE 3: Compilazione file IMPORT_DIPARTIMENTO_E_UOC.xls${NC}"
echo -e "${CYAN}============================================================================${NC}"
echo ""

pwsh -File "$SCRIPT_DIR/ScriptCompilazioneDipUoc.ps1"
EXIT_CODE=$?

if [ $EXIT_CODE -ne 0 ]; then
    echo ""
    echo -e "${RED}ERRORE durante la compilazione dipartimenti e UOC!${NC}"
    exit 1
fi

# ============================================================================
# FASE 4: COMPILAZIONE SCHEDE
# ============================================================================

echo ""
echo -e "${CYAN}============================================================================${NC}"
echo -e "${CYAN}  FASE 4: Compilazione file IMPORT_SCHEDE.xls${NC}"
echo -e "${CYAN}============================================================================${NC}"
echo ""

pwsh -File "$SCRIPT_DIR/ScriptCompilazioneSchede.ps1"
EXIT_CODE=$?

if [ $EXIT_CODE -ne 0 ]; then
    echo ""
    echo -e "${RED}ERRORE durante la compilazione delle schede!${NC}"
    exit 1
fi

# ============================================================================
# RIEPILOGO FINALE
# ============================================================================

echo ""
echo -e "${GREEN}============================================================================${NC}"
echo -e "${GREEN}  ✓✓✓ PROCESSO COMPLETATO CON SUCCESSO! ✓✓✓${NC}"
echo -e "${GREEN}============================================================================${NC}"
echo ""
echo -e "${CYAN}File generati:${NC}"
echo -e "${GREEN}  ✓ IMPORT_RISORSE_UMANE.xlsx${NC}"
echo -e "${GREEN}  ✓ IMPORT_DIPARTIMENTO_E_UOC.xls${NC}"
echo -e "${GREEN}  ✓ IMPORT_SCHEDE.xls${NC}"
echo ""
echo -e "${GREEN}I file sono pronti per l'import nel sistema.${NC}"
echo ""
echo -e "${YELLOW}PROSSIMI PASSI:${NC}"
echo "1. Verificare manualmente i file generati (opzionale)"
echo "2. Procedere con l'import nel sistema GZOOM"
echo ""
