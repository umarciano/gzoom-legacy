# ============================================================================
# SCRIPT COMPLETO: VALIDAZIONE + IMPORT MASSIVO
# ============================================================================
# 
# Questo script esegue:
# 1. Validazione dati nel Template_Dipendenti_AORN.xlsx
# 2. Se validazione OK, esegue gli script di import in sequenza
#
# ============================================================================

Write-Host ""
Write-Host "============================================================================" -ForegroundColor Cyan
Write-Host "  PROCESSO COMPLETO: VALIDAZIONE + IMPORT MASSIVO DIPENDENTI" -ForegroundColor Cyan
Write-Host "============================================================================" -ForegroundColor Cyan
Write-Host ""

# Determina la directory dello script
$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path

# ============================================================================
# FASE 1: VALIDAZIONE DATI
# ============================================================================

Write-Host "FASE 1: Validazione dati Template_Dipendenti_AORN.xlsx" -ForegroundColor Yellow
Write-Host ""

& "$scriptDir\script_validazione_dati.ps1"

if ($LASTEXITCODE -ne 0) {
    Write-Host ""
    Write-Host "============================================================================" -ForegroundColor Red
    Write-Host "  PROCESSO INTERROTTO: Validazione fallita!" -ForegroundColor Red
    Write-Host "============================================================================" -ForegroundColor Red
    Write-Host ""
    Write-Host "Correggere gli errori nel file Excel prima di procedere." -ForegroundColor Yellow
    Write-Host ""
    exit 1
}

Write-Host ""
Write-Host "✓ Validazione completata con successo!" -ForegroundColor Green
Write-Host ""
Write-Host "Premere un tasto per continuare con l'import oppure CTRL+C per annullare..." -ForegroundColor Yellow
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")

# ============================================================================
# FASE 2: COMPILAZIONE ANAGRAFICA
# ============================================================================

Write-Host ""
Write-Host "============================================================================" -ForegroundColor Cyan
Write-Host "  FASE 2: Compilazione file IMPORT_RISORSE_UMANE.xlsx" -ForegroundColor Cyan
Write-Host "============================================================================" -ForegroundColor Cyan
Write-Host ""

& "$scriptDir\ScriptCompilazioneAnagrafica.ps1"

if ($LASTEXITCODE -ne 0) {
    Write-Host ""
    Write-Host "ERRORE durante la compilazione dell'anagrafica!" -ForegroundColor Red
    exit 1
}

# ============================================================================
# FASE 3: COMPILAZIONE DIPARTIMENTI E UOC
# ============================================================================

Write-Host ""
Write-Host "============================================================================" -ForegroundColor Cyan
Write-Host "  FASE 3: Compilazione file IMPORT_DIPARTIMENTO_E_UOC.xls" -ForegroundColor Cyan
Write-Host "============================================================================" -ForegroundColor Cyan
Write-Host ""

& "$scriptDir\ScriptCompilazioneDipUoc.ps1"

if ($LASTEXITCODE -ne 0) {
    Write-Host ""
    Write-Host "ERRORE durante la compilazione dipartimenti e UOC!" -ForegroundColor Red
    exit 1
}

# ============================================================================
# FASE 4: COMPILAZIONE SCHEDE
# ============================================================================

Write-Host ""
Write-Host "============================================================================" -ForegroundColor Cyan
Write-Host "  FASE 4: Compilazione file IMPORT_SCHEDE.xls" -ForegroundColor Cyan
Write-Host "============================================================================" -ForegroundColor Cyan
Write-Host ""

& "$scriptDir\ScriptCompilazioneSchede.ps1"

if ($LASTEXITCODE -ne 0) {
    Write-Host ""
    Write-Host "ERRORE durante la compilazione delle schede!" -ForegroundColor Red
    exit 1
}

# ============================================================================
# RIEPILOGO FINALE
# ============================================================================

Write-Host ""
Write-Host "============================================================================" -ForegroundColor Green
Write-Host "  ✓✓✓ PROCESSO COMPLETATO CON SUCCESSO! ✓✓✓" -ForegroundColor Green
Write-Host "============================================================================" -ForegroundColor Green
Write-Host ""
Write-Host "File generati:" -ForegroundColor Cyan
Write-Host "  ✓ IMPORT_RISORSE_UMANE.xlsx" -ForegroundColor Green
Write-Host "  ✓ IMPORT_DIPARTIMENTO_E_UOC.xls" -ForegroundColor Green
Write-Host "  ✓ IMPORT_SCHEDE.xls" -ForegroundColor Green
Write-Host ""
Write-Host "I file sono pronti per l'import nel sistema." -ForegroundColor Green
Write-Host ""
Write-Host "PROSSIMI PASSI:" -ForegroundColor Yellow
Write-Host "1. Verificare manualmente i file generati (opzionale)" -ForegroundColor White
Write-Host "2. Procedere con l'import nel sistema GZOOM" -ForegroundColor White
Write-Host ""
