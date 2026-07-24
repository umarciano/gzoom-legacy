# =====================================================================
# CONFIGURE_PERF_ORGANIZZATIVA.PS1
# =====================================================================
# Configura l'infrastruttura Performance Organizzativa CTX_BS.
# Ordine: setup_4fasce_scoring → setup_workflow_stati → setup_orgperf_dir_uo_profile
#
# Uso:
#   .\configure_perf_organizzativa.ps1 [-DbHost <h>] [-DbPort <p>] [-DbName <n>] [-DbUser <u>] [-DbPassword <pw>]
# =====================================================================

[CmdletBinding()]
param(
    [string]$DbHost     = "localhost",
    [int]   $DbPort     = 5432,
    [string]$DbName     = "cardarelli",
    [string]$DbUser     = "postgres",
    [string]$DbPassword = ""
)

$ErrorActionPreference = "Continue"
$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$LogFile   = Join-Path $ScriptDir "configure_perf_organizzativa_$(Get-Date -Format 'yyyyMMdd_HHmmss').log"

$SqlFiles = @(
    (Join-Path $ScriptDir "setup_4fasce_scoring.sql"),
    (Join-Path $ScriptDir "setup_workflow_stati.sql"),
    (Join-Path $ScriptDir "..\profile-permissions\setup_orgperf_dir_uo_profile.sql")
)

function Write-Log($level, $msg) {
    $line = "[$(Get-Date -Format 'yyyy-MM-dd HH:mm:ss')] [$level] $msg"
    $line | Tee-Object -FilePath $LogFile -Append | Write-Host
}

Write-Host "====================================================" -ForegroundColor Blue
Write-Host " CONFIGURAZIONE PERFORMANCE ORGANIZZATIVA CTX_BS   " -ForegroundColor Blue
Write-Host "====================================================" -ForegroundColor Blue
Write-Host "  Host: $DbHost | Port: $DbPort | DB: $DbName | User: $DbUser"
Write-Host "  Log: $LogFile"
Write-Host ""
Read-Host "Premere INVIO per continuare (CTRL+C per annullare)"

if ($DbPassword -eq "") {
    $secure = Read-Host "Password per $DbUser" -AsSecureString
    $DbPassword = [Runtime.InteropServices.Marshal]::PtrToStringAuto(
        [Runtime.InteropServices.Marshal]::SecureStringToBSTR($secure))
}
$env:PGPASSWORD = $DbPassword

$success = 0; $errors = 0

foreach ($file in $SqlFiles) {
    $name = Split-Path $file -Leaf
    Write-Host ""
    Write-Host "Esecuzione: $name" -ForegroundColor Yellow
    Write-Log "INFO" "Inizio: $name"

    $output = & psql -h $DbHost -p $DbPort -d $DbName -U $DbUser -f $file 2>&1
    $code   = $LASTEXITCODE
    $output | Out-File -FilePath $LogFile -Append

    if ($code -eq 0) {
        Write-Host "✓ $name" -ForegroundColor Green; Write-Log "SUCCESS" $name; $success++
    } else {
        Write-Host "✗ $name" -ForegroundColor Red; $output | Write-Host; Write-Log "ERROR" "$name (exit $code)"; $errors++
    }
}

$env:PGPASSWORD = ""
Write-Host ""
Write-Host "Successi: $success | Errori: $errors"
if ($errors -eq 0) { Write-Host "✓ Configurazione completata." -ForegroundColor Green; exit 0 }
else               { Write-Host "✗ Completata con errori — vedere $LogFile" -ForegroundColor Red; exit 1 }
