# =====================================================================
# CONFIGURE_PROFILES_AORN.PS1
# =====================================================================
# Script per configurare i profili di sicurezza AORN nel database PostgreSQL
# 
# Profili configurati:
# 1. AORNADMIN - Amministratore di Sistema AORN
# 2. EMPLPERF_VALUTATO - Dipendente valutato
# 3. EMPLPERF_VALUTATORE - Valutatore
#
# Uso:
#   .\configure_profiles_aorn.ps1 [-DbHost <host>] [-DbPort <port>] [-DbName <name>] [-DbUser <user>] [-DbPassword <password>]
#
# Parametri opzionali (valori di default se non specificati):
#   -DbHost      Host del database PostgreSQL (default: localhost)
#   -DbPort      Porta del database PostgreSQL (default: 5432)
#   -DbName      Nome del database (default: ofbiz)
#   -DbUser      Utente del database (default: ofbiz)
#   -DbPassword  Password del database (se non specificata, verrà richiesta)
#
# Esempi:
#   .\configure_profiles_aorn.ps1
#   .\configure_profiles_aorn.ps1 -DbHost "localhost" -DbName "gzoom" -DbUser "postgres"
#   .\configure_profiles_aorn.ps1 -DbHost "192.168.1.100" -DbPort 5433 -DbName "ofbiz" -DbUser "admin" -DbPassword "MyPassword123"
# =====================================================================

[CmdletBinding()]
param(
    [string]$DbHost = "localhost",
    [int]$DbPort = 5432,
    [string]$DbName = "ofbiz",
    [string]$DbUser = "ofbiz",
    [string]$DbPassword = ""
)

# =====================================================================
# CONFIGURAZIONE
# =====================================================================

$ErrorActionPreference = "Continue"

# Directory dello script
$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path

# File di log
$LogFile = Join-Path $ScriptDir "configure_profiles_aorn_$(Get-Date -Format 'yyyyMMdd_HHmmss').log"

# File SQL da eseguire (nell'ordine corretto)
$SqlFiles = @(
    "setup_aornadmin_profile.sql",
    "setup_valutato_profile.sql",
    "setup_valutatore_profile.sql"
)

# =====================================================================
# FUNZIONI
# =====================================================================

# Funzione per stampare messaggi con timestamp
function Write-Log {
    param(
        [string]$Level,
        [string]$Message
    )
    
    $timestamp = Get-Date -Format "yyyy-MM-dd HH:mm:ss"
    $logMessage = "[$timestamp] [$Level] $Message"
    
    # Scrivi su console con colori
    switch ($Level) {
        "ERROR"   { Write-Host $logMessage -ForegroundColor Red }
        "SUCCESS" { Write-Host $logMessage -ForegroundColor Green }
        "WARNING" { Write-Host $logMessage -ForegroundColor Yellow }
        "INFO"    { Write-Host $logMessage -ForegroundColor Cyan }
        default   { Write-Host $logMessage }
    }
    
    # Scrivi su file di log
    Add-Content -Path $LogFile -Value $logMessage
}

# Funzione per stampare header
function Write-Header {
    param([string]$Message)
    
    Write-Host ""
    Write-Host "=====================================================================" -ForegroundColor Blue
    Write-Host $Message -ForegroundColor Blue
    Write-Host "=====================================================================" -ForegroundColor Blue
    Write-Log -Level "INFO" -Message $Message
}

# Funzione per verificare se psql è installato
function Test-PostgreSqlClient {
    $psqlPath = Get-Command psql -ErrorAction SilentlyContinue
    
    if (-not $psqlPath) {
        Write-Log -Level "ERROR" -Message "psql non è installato o non è nel PATH"
        Write-Host ""
        Write-Host "SUGGERIMENTO: Installare PostgreSQL client o aggiungere la directory bin di PostgreSQL al PATH" -ForegroundColor Yellow
        Write-Host "Esempio PATH: C:\Program Files\PostgreSQL\15\bin" -ForegroundColor Yellow
        return $false
    }
    
    Write-Log -Level "INFO" -Message "psql trovato: $($psqlPath.Source)"
    return $true
}

# Funzione per eseguire un file SQL
function Invoke-SqlFile {
    param(
        [string]$SqlFile,
        [string]$Host,
        [int]$Port,
        [string]$Database,
        [string]$User,
        [string]$Password
    )
    
    $sqlPath = Join-Path $ScriptDir $SqlFile
    
    if (-not (Test-Path $sqlPath)) {
        Write-Log -Level "ERROR" -Message "File non trovato: $sqlPath"
        return $false
    }
    
    Write-Host ""
    Write-Host "Esecuzione di: $SqlFile" -ForegroundColor Yellow
    Write-Log -Level "INFO" -Message "Inizio esecuzione: $SqlFile"
    
    # Imposta la variabile d'ambiente PGPASSWORD
    $env:PGPASSWORD = $Password
    
    try {
        # Esegui psql e cattura output
        $output = & psql -h $Host -p $Port -d $Database -U $User -f $sqlPath 2>&1
        $exitCode = $LASTEXITCODE
        
        # Registra l'output nel log
        $output | ForEach-Object { Add-Content -Path $LogFile -Value $_ }
        
        if ($exitCode -eq 0) {
            Write-Host "✓ Successo: $SqlFile eseguito correttamente" -ForegroundColor Green
            Write-Log -Level "SUCCESS" -Message "$SqlFile eseguito con successo"
            return $true
        }
        else {
            Write-Host "✗ Errore nell'esecuzione di: $SqlFile" -ForegroundColor Red
            Write-Host "Output:" -ForegroundColor Red
            $output | ForEach-Object { Write-Host $_ -ForegroundColor Red }
            Write-Log -Level "ERROR" -Message "Errore nell'esecuzione di $SqlFile - Exit code: $exitCode"
            return $false
        }
    }
    catch {
        Write-Host "✗ Eccezione durante l'esecuzione di: $SqlFile" -ForegroundColor Red
        Write-Host $_.Exception.Message -ForegroundColor Red
        Write-Log -Level "ERROR" -Message "Eccezione: $($_.Exception.Message)"
        return $false
    }
    finally {
        # Rimuovi la password dalla variabile d'ambiente
        Remove-Item Env:\PGPASSWORD -ErrorAction SilentlyContinue
    }
}

# =====================================================================
# MAIN
# =====================================================================

Write-Header "CONFIGURAZIONE PROFILI DI SICUREZZA AORN"

Write-Host ""
Write-Host "Parametri di connessione:"
Write-Host "  Host:     $DbHost"
Write-Host "  Porta:    $DbPort"
Write-Host "  Database: $DbName"
Write-Host "  Utente:   $DbUser"
Write-Host ""
Write-Host "File di log: $LogFile"
Write-Host ""

Write-Log -Level "INFO" -Message "Parametri: Host=$DbHost, Port=$DbPort, Database=$DbName, User=$DbUser"

# Verifica prerequisiti
if (-not (Test-PostgreSqlClient)) {
    Write-Host ""
    Write-Host "Premere un tasto per uscire..." -ForegroundColor Yellow
    $null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
    exit 1
}

# Richiedi la password se non è stata fornita
if ([string]::IsNullOrEmpty($DbPassword)) {
    $securePassword = Read-Host "Inserire la password per l'utente '$DbUser'" -AsSecureString
    $BSTR = [System.Runtime.InteropServices.Marshal]::SecureStringToBSTR($securePassword)
    $DbPassword = [System.Runtime.InteropServices.Marshal]::PtrToStringAuto($BSTR)
    [System.Runtime.InteropServices.Marshal]::ZeroFreeBSTR($BSTR)
}

# Chiedi conferma prima di procedere
Write-Host ""
Write-Host "Premere INVIO per continuare o CTRL+C per annullare..." -ForegroundColor Yellow
$null = Read-Host

# Contatori
$totalFiles = $SqlFiles.Count
$successCount = 0
$errorCount = 0

# Esegui ogni file SQL
foreach ($sqlFile in $SqlFiles) {
    $result = Invoke-SqlFile -SqlFile $sqlFile -Host $DbHost -Port $DbPort -Database $DbName -User $DbUser -Password $DbPassword
    
    if ($result) {
        $successCount++
    }
    else {
        $errorCount++
    }
    
    # Pausa tra un file e l'altro
    Start-Sleep -Seconds 1
}

# =====================================================================
# RIEPILOGO FINALE
# =====================================================================

Write-Header "RIEPILOGO ESECUZIONE"

Write-Host ""
Write-Host "File totali:     $totalFiles"
Write-Host "Successi:        " -NoNewline
Write-Host $successCount -ForegroundColor Green
Write-Host "Errori:          " -NoNewline
Write-Host $errorCount -ForegroundColor Red
Write-Host ""

Write-Log -Level "INFO" -Message "Riepilogo: Totali=$totalFiles, Successi=$successCount, Errori=$errorCount"

if ($errorCount -eq 0) {
    Write-Host "✓ Tutti i profili sono stati configurati correttamente!" -ForegroundColor Green
    Write-Log -Level "SUCCESS" -Message "Configurazione completata con successo"
    $exitCode = 0
}
else {
    Write-Host "✗ Alcuni profili non sono stati configurati correttamente." -ForegroundColor Red
    Write-Host "Controllare il file di log per dettagli: $LogFile" -ForegroundColor Yellow
    Write-Log -Level "WARNING" -Message "Configurazione completata con errori"
    $exitCode = 1
}

Write-Host ""
Write-Host "Premere un tasto per uscire..." -ForegroundColor Yellow
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")

exit $exitCode
