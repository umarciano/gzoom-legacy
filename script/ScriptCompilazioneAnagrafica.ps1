# Script per la compilazione automatica di IMPORT_RISORSE_UMANE.xls
# Legge i dati dal file Template_Dipendenti_AORN.xlsx (sheet "Template Dipendenti AORN")
# e popola il file IMPORT_RISORSE_UMANE.xls con i dati delle risorse umane

# Verifica e installa il modulo ImportExcel se necessario
if (-not (Get-Module -ListAvailable -Name ImportExcel)) {
    Write-Host "Modulo ImportExcel non trovato. Installazione in corso..." -ForegroundColor Yellow
    Install-Module -Name ImportExcel -Scope CurrentUser -Force
    Write-Host "Modulo ImportExcel installato con successo." -ForegroundColor Green
}

Import-Module ImportExcel

# Percorsi dei file
$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$templateDir = Join-Path $scriptDir "template"
$sourceFile = Join-Path $templateDir "Template_Dipendenti_AORN.xlsx"
$targetFile = Join-Path $templateDir "IMPORT_RISORSE_UMANE.xlsx"

Write-Host "Inizio elaborazione..." -ForegroundColor Cyan

# Verifica esistenza file sorgente
if (-not (Test-Path $sourceFile)) {
    Write-Host "ERRORE: File sorgente $sourceFile non trovato!" -ForegroundColor Red
    exit 1
}

# Leggi i dati dal foglio "Template Dipendenti AORN" del file Template_Dipendenti_AORN.xlsx
Write-Host "Lettura dati dal file $sourceFile (sheet 'Template Dipendenti AORN')..." -ForegroundColor Cyan

try {
    # Importa il foglio "Template Dipendenti AORN"
    $dipendentiData = Import-Excel -Path $sourceFile -WorksheetName "Template Dipendenti AORN"
    
    Write-Host "Trovati $($dipendentiData.Count) righe totali nel file sorgente." -ForegroundColor Green
    
    # Crea un array con tutte le matricole dei valutatori per verificare "Is Evaluation Manager"
    $valutatori = @()
    foreach ($row in $dipendentiData) {
        if ($null -ne $row.'Matricola Referente Valutatore' -and 
            -not [string]::IsNullOrWhiteSpace($row.'Matricola Referente Valutatore')) {
            $matricolaVal = $row.'Matricola Referente Valutatore'.ToString().Trim()
            if ($matricolaVal -and $valutatori -notcontains $matricolaVal) {
                $valutatori += $matricolaVal
            }
        }
    }
    
    Write-Host "Identificati $($valutatori.Count) valutatori unici." -ForegroundColor Green
    
    # Valori di default
    $defaultDate = "01/01/2025"
    $defaultEmploymentAmount = "1"
    $defaultEmploymentOrgRoleType = "ORGANIZATION_UNIT"
    $defaultDataSource = "IMPORT_HR"
    
    # Crea la struttura dati per l'export
    $outputData = @()
    
    # Per ogni dipendente, crea la riga corrispondente
    foreach ($row in $dipendentiData) {
        # Salta righe vuote
        if ($null -eq $row.'Matricola' -or [string]::IsNullOrWhiteSpace($row.'Matricola')) {
            continue
        }
        
        # Estrai i dati con gestione dei valori null
        $matricola = if ($row.'Matricola') { $row.'Matricola'.ToString().Trim() } else { "" }
        $nome = if ($row.'Nome') { $row.'Nome'.ToString().Trim() } else { "" }
        $cognome = if ($row.'Cognome') { $row.'Cognome'.ToString().Trim() } else { "" }
        $codiceFiscale = if ($row.'Codice Fiscale') { $row.'Codice Fiscale'.ToString().Trim() } else { "" }
        $ruoloGZOOM = if ($row.'Ruolo GZOOM') { $row.'Ruolo GZOOM'.ToString().Trim() } else { "" }
        $codiceUOC = if ($row.'Codice UOC') { $row.'Codice UOC'.ToString().Trim() } else { "" }
        $nomeUOC = if ($row.'Nome UOC') { $row.'Nome UOC'.ToString().Trim() } else { "" }
        $matricolaValutatore = if ($row.'Matricola Referente Valutatore') { $row.'Matricola Referente Valutatore'.ToString().Trim() } else { "" }
        $email = if ($row.'Email') { $row.'Email'.ToString().Trim() } else { "" }
        $username = if ($row.'Username') { $row.'Username'.ToString().Trim() } else { "" }
        
        # Determina se questo dipendente è un Evaluation Manager
        # (se la sua matricola è presente nell'elenco dei valutatori)
        $isEvaluationManager = if ($valutatori -contains $matricola) { "Y" } else { "N" }
        
        # Determina il Group Profile ID in base a Is Evaluation Manager
        $groupProfileId = if ($isEvaluationManager -eq "Y") { "EMPLPERF_VALUTATORE" } else { "EMPLPERF_VALUTATO" }
        
        # Crea l'oggetto con tutti i campi mappati
        $outputData += [PSCustomObject]@{
            'Person Code' = $matricola
            'First Name' = $nome
            'Last Name' = $cognome
            'Fiscal Code' = $codiceFiscale
            'Person Role Type' = $ruoloGZOOM
            'Employment Position Type' = $ruoloGZOOM
            'Qualification From Date' = $defaultDate
            'Employment Amount' = $defaultEmploymentAmount
            'Employment Start Date' = $defaultDate
            'Employment End Date' = ''
            'Employment Org Code' = $codiceUOC
            'Employment Org Role Type' = $defaultEmploymentOrgRoleType
            'Employment Org Description' = $nomeUOC
            'Employment Org Comments' = ''
            'Employment Org From Date' = $defaultDate
            'Employment Org End Date' = ''
            'Evaluator Code' = $matricolaValutatore
            'Evaluator From Date' = $defaultDate
            'Allocation Org Code' = ''
            'Allocation Org Role Type' = ''
            'Allocation Org Description' = ''
            'Allocation Org Comments' = ''
            'Allocation Org From Date' = ''
            'Allocation Org End Date' = ''
            'Is Evaluation Manager' = $isEvaluationManager
            'Approver Code' = $matricolaValutatore
            'Email' = $email
            'Mobile Phone' = ''
            'User Login ID' = $username
            'Group Profile ID' = $groupProfileId
            'Work Effort Assignment Code' = ''
            'Work Effort Date' = ''
            'Employment Position Type Date' = $defaultDate
            'Description' = ''
            'Comments' = ''
            'Reference Date' = $defaultDate
            'Data Source' = $defaultDataSource
        }
    }
    
    Write-Host "Totale righe da scrivere: $($outputData.Count)" -ForegroundColor Green
    
    # Esporta i dati nel file di destinazione
    Write-Host "Scrittura dati nel file $targetFile..." -ForegroundColor Cyan
    
    $outputData | Export-Excel -Path $targetFile -WorksheetName "Sheet1" -AutoSize -TableName "RisorseUmane" -ClearSheet
    
    Write-Host "COMPLETATO! File $targetFile generato con successo." -ForegroundColor Green
    Write-Host "Totale righe scritte: $($outputData.Count)" -ForegroundColor Green
    Write-Host "Valutatori identificati: $($valutatori.Count)" -ForegroundColor Green
    
} catch {
    Write-Host "ERRORE durante l'elaborazione: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host $_.Exception.StackTrace -ForegroundColor Red
    exit 1
}
