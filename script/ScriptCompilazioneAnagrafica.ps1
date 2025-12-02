# Script per la compilazione automatica di IMPORT_RISORSE_UMANE.xls
# Legge i dati dal file Template_Dipendenti_AORN.xlsx (sheet "Template Dipendenti AORN")
# e popola il file IMPORT_RISORSE_UMANE.xls con i dati delle risorse umane

# ============================================================================
# CONFIGURAZIONE - NOMI FILE E CARTELLE
# ============================================================================
$CONFIG = @{
    # Cartelle
    TEMPLATE_DIR = "templates"
    
    # File sorgente
    SOURCE_FILE = "Template_Dipendenti_AORN.xlsx"
    SOURCE_SHEET = "Template Dipendenti AORN"
    
    # File destinazione
    TARGET_FILE = "IMPORT_RISORSE_UMANE.xlsx"
    TARGET_SHEET = "Sheet1"
    
    # Colonne file sorgente
    SRC_COL_MATRICOLA = "Matricola"
    SRC_COL_NOME = "Nome"
    SRC_COL_COGNOME = "Cognome"
    SRC_COL_CF = "Codice Fiscale"
    SRC_COL_RUOLO = "Ruolo GZOOM"
    SRC_COL_CODICE_UOC = "Codice UOC"
    SRC_COL_NOME_UOC = "Nome UOC"
    SRC_COL_MATR_VALUTATORE = "Matricola Referente Valutatore"
    SRC_COL_EMAIL = "Email"
    SRC_COL_USERNAME = "Username"
    
    # Colonne file destinazione
    OUT_COL_PERSON_CODE = "Person Code"
    OUT_COL_FIRST_NAME = "First Name"
    OUT_COL_LAST_NAME = "Last Name"
    OUT_COL_FISCAL_CODE = "Fiscal Code"
    OUT_COL_PERSON_ROLE_TYPE = "Person Role Type"
    OUT_COL_EMPL_POS_TYPE = "Employment Position Type"
    OUT_COL_QUAL_FROM_DATE = "Qualification From Date"
    OUT_COL_EMPL_AMOUNT = "Employment Amount"
    OUT_COL_EMPL_START_DATE = "Employment Start Date"
    OUT_COL_EMPL_END_DATE = "Employment End Date"
    OUT_COL_EMPL_ORG_CODE = "Employment Org Code"
    OUT_COL_EMPL_ORG_ROLE_TYPE = "Employment Org Role Type"
    OUT_COL_EMPL_ORG_DESC = "Employment Org Description"
    OUT_COL_EMPL_ORG_COMMENTS = "Employment Org Comments"
    OUT_COL_EMPL_ORG_FROM_DATE = "Employment Org From Date"
    OUT_COL_EMPL_ORG_END_DATE = "Employment Org End Date"
    OUT_COL_EVALUATOR_CODE = "Evaluator Code"
    OUT_COL_EVALUATOR_FROM_DATE = "Evaluator From Date"
    OUT_COL_ALLOC_ORG_CODE = "Allocation Org Code"
    OUT_COL_ALLOC_ORG_ROLE_TYPE = "Allocation Org Role Type"
    OUT_COL_ALLOC_ORG_DESC = "Allocation Org Description"
    OUT_COL_ALLOC_ORG_COMMENTS = "Allocation Org Comments"
    OUT_COL_ALLOC_ORG_FROM_DATE = "Allocation Org From Date"
    OUT_COL_ALLOC_ORG_END_DATE = "Allocation Org End Date"
    OUT_COL_IS_EVAL_MGR = "Is Evaluation Manager"
    OUT_COL_APPROVER_CODE = "Approver Code"
    OUT_COL_EMAIL = "Email"
    OUT_COL_MOBILE_PHONE = "Mobile Phone"
    OUT_COL_USER_LOGIN_ID = "User Login ID"
    OUT_COL_GROUP_PROFILE_ID = "Group Profile ID"
    OUT_COL_WE_ASSIGN_CODE = "Work Effort Assignment Code"
    OUT_COL_WE_DATE = "Work Effort Date"
    OUT_COL_EMPL_POS_TYPE_DATE = "Employment Position Type Date"
    OUT_COL_DESCRIPTION = "Description"
    OUT_COL_COMMENTS = "Comments"
    OUT_COL_REF_DATE = "Reference Date"
    OUT_COL_DATA_SOURCE = "Data Source"
    
    # Valori costanti
    DEFAULT_DATE = "01/01/2025"
    DEFAULT_EMPL_AMOUNT = "1"
    DEFAULT_EMPL_ORG_ROLE_TYPE = "UOC"
    DEFAULT_DATA_SOURCE = "IMPORT_HR"
    GROUP_PROFILE_VALUTATORE = "EMPLPERF_VALUTATORE"
    GROUP_PROFILE_VALUTATO = "EMPLPERF_VALUTATO"
    FLAG_YES = "Y"
    FLAG_NO = "N"
}

# Funzione per pulire i valori, rimuovendo ".0" dai numeri float
function Clean-Value {
    param (
        [Parameter(ValueFromPipeline=$true)]
        $Value
    )
    
    if ($null -eq $Value -or [string]::IsNullOrWhiteSpace($Value)) {
        return ""
    }
    
    $strValue = $Value.ToString().Trim()
    
    # Se termina con .0, rimuovilo (è un numero intero letto come float da Excel)
    if ($strValue.EndsWith(".0")) {
        try {
            # Verifica che sia effettivamente un numero
            [void][double]::Parse($strValue)
            return $strValue.Substring(0, $strValue.Length - 2)
        } catch {
            return $strValue
        }
    }
    
    return $strValue
}

# Verifica e installa il modulo ImportExcel se necessario
if (-not (Get-Module -ListAvailable -Name ImportExcel)) {
    Write-Host "Modulo ImportExcel non trovato. Installazione in corso..." -ForegroundColor Yellow
    Install-Module -Name ImportExcel -Scope CurrentUser -Force
    Write-Host "Modulo ImportExcel installato con successo." -ForegroundColor Green
}

Import-Module ImportExcel

# Percorsi dei file
$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$templateDir = Join-Path $scriptDir $CONFIG.TEMPLATE_DIR
$sourceFile = Join-Path $templateDir $CONFIG.SOURCE_FILE
$targetFile = Join-Path $templateDir $CONFIG.TARGET_FILE

Write-Host "Inizio elaborazione..." -ForegroundColor Cyan

# Verifica esistenza file sorgente
if (-not (Test-Path $sourceFile)) {
    Write-Host "ERRORE: File sorgente $sourceFile non trovato!" -ForegroundColor Red
    exit 1
}

# Leggi i dati dal foglio "Template Dipendenti AORN" del file Template_Dipendenti_AORN.xlsx
Write-Host "Lettura dati dal file $sourceFile (sheet '$($CONFIG.SOURCE_SHEET)')..." -ForegroundColor Cyan

try {
    # Importa il foglio "Template Dipendenti AORN"
    $dipendentiData = Import-Excel -Path $sourceFile -WorksheetName $CONFIG.SOURCE_SHEET
    
    Write-Host "Trovati $($dipendentiData.Count) righe totali nel file sorgente." -ForegroundColor Green
    
    # Crea un array con tutte le matricole dei valutatori per verificare "Is Evaluation Manager"
    $valutatori = @()
    foreach ($row in $dipendentiData) {
        if ($null -ne $row.($CONFIG.SRC_COL_MATR_VALUTATORE) -and 
            -not [string]::IsNullOrWhiteSpace($row.($CONFIG.SRC_COL_MATR_VALUTATORE))) {
            $matricolaVal = $row.($CONFIG.SRC_COL_MATR_VALUTATORE).ToString().Trim()
            if ($matricolaVal -and $valutatori -notcontains $matricolaVal) {
                $valutatori += $matricolaVal
            }
        }
    }
    
    Write-Host "Identificati $($valutatori.Count) valutatori unici." -ForegroundColor Green
    
    # Crea la struttura dati per l'export
    $outputData = @()
    
    # Per ogni dipendente, crea la riga corrispondente
    foreach ($row in $dipendentiData) {
        # Salta righe vuote
        if ($null -eq $row.($CONFIG.SRC_COL_MATRICOLA) -or [string]::IsNullOrWhiteSpace($row.($CONFIG.SRC_COL_MATRICOLA))) {
            continue
        }
        
        # Estrai i dati usando Clean-Value per rimuovere i ".0" dalle matricole
        $matricola = Clean-Value $row.($CONFIG.SRC_COL_MATRICOLA)
        $nome = Clean-Value $row.($CONFIG.SRC_COL_NOME)
        $cognome = Clean-Value $row.($CONFIG.SRC_COL_COGNOME)
        $codiceFiscale = Clean-Value $row.($CONFIG.SRC_COL_CF)
        $ruoloGZOOM = Clean-Value $row.($CONFIG.SRC_COL_RUOLO)
        $codiceUOC = Clean-Value $row.($CONFIG.SRC_COL_CODICE_UOC)
        $nomeUOC = Clean-Value $row.($CONFIG.SRC_COL_NOME_UOC)
        $matricolaValutatore = Clean-Value $row.($CONFIG.SRC_COL_MATR_VALUTATORE)
        $email = Clean-Value $row.($CONFIG.SRC_COL_EMAIL)
        $username = Clean-Value $row.($CONFIG.SRC_COL_USERNAME)
        
        # Determina se questo dipendente è un Evaluation Manager
        # (se la sua matricola è presente nell'elenco dei valutatori)
        $isEvaluationManager = if ($valutatori -contains $matricola) { $CONFIG.FLAG_YES } else { $CONFIG.FLAG_NO }
        
        # Determina il Group Profile ID in base a Is Evaluation Manager
        $groupProfileId = if ($isEvaluationManager -eq $CONFIG.FLAG_YES) { $CONFIG.GROUP_PROFILE_VALUTATORE } else { $CONFIG.GROUP_PROFILE_VALUTATO }
        
        # Crea l'oggetto con tutti i campi mappati
        $outputData += [PSCustomObject]@{
            $CONFIG.OUT_COL_PERSON_CODE = $matricola
            $CONFIG.OUT_COL_FIRST_NAME = $nome
            $CONFIG.OUT_COL_LAST_NAME = $cognome
            $CONFIG.OUT_COL_FISCAL_CODE = $codiceFiscale
            $CONFIG.OUT_COL_PERSON_ROLE_TYPE = $ruoloGZOOM
            $CONFIG.OUT_COL_EMPL_POS_TYPE = $ruoloGZOOM
            $CONFIG.OUT_COL_QUAL_FROM_DATE = $CONFIG.DEFAULT_DATE
            $CONFIG.OUT_COL_EMPL_AMOUNT = $CONFIG.DEFAULT_EMPL_AMOUNT
            $CONFIG.OUT_COL_EMPL_START_DATE = $CONFIG.DEFAULT_DATE
            $CONFIG.OUT_COL_EMPL_END_DATE = ''
            $CONFIG.OUT_COL_EMPL_ORG_CODE = $codiceUOC
            $CONFIG.OUT_COL_EMPL_ORG_ROLE_TYPE = $CONFIG.DEFAULT_EMPL_ORG_ROLE_TYPE
            $CONFIG.OUT_COL_EMPL_ORG_DESC = $nomeUOC
            $CONFIG.OUT_COL_EMPL_ORG_COMMENTS = ''
            $CONFIG.OUT_COL_EMPL_ORG_FROM_DATE = $CONFIG.DEFAULT_DATE
            $CONFIG.OUT_COL_EMPL_ORG_END_DATE = ''
            $CONFIG.OUT_COL_EVALUATOR_CODE = $matricolaValutatore
            $CONFIG.OUT_COL_EVALUATOR_FROM_DATE = $CONFIG.DEFAULT_DATE
            $CONFIG.OUT_COL_ALLOC_ORG_CODE = ''
            $CONFIG.OUT_COL_ALLOC_ORG_ROLE_TYPE = ''
            $CONFIG.OUT_COL_ALLOC_ORG_DESC = ''
            $CONFIG.OUT_COL_ALLOC_ORG_COMMENTS = ''
            $CONFIG.OUT_COL_ALLOC_ORG_FROM_DATE = ''
            $CONFIG.OUT_COL_ALLOC_ORG_END_DATE = ''
            $CONFIG.OUT_COL_IS_EVAL_MGR = $isEvaluationManager
            $CONFIG.OUT_COL_APPROVER_CODE = $matricolaValutatore
            $CONFIG.OUT_COL_EMAIL = $email
            $CONFIG.OUT_COL_MOBILE_PHONE = ''
            $CONFIG.OUT_COL_USER_LOGIN_ID = $username
            $CONFIG.OUT_COL_GROUP_PROFILE_ID = $groupProfileId
            $CONFIG.OUT_COL_WE_ASSIGN_CODE = ''
            $CONFIG.OUT_COL_WE_DATE = ''
            $CONFIG.OUT_COL_EMPL_POS_TYPE_DATE = $CONFIG.DEFAULT_DATE
            $CONFIG.OUT_COL_DESCRIPTION = ''
            $CONFIG.OUT_COL_COMMENTS = ''
            $CONFIG.OUT_COL_REF_DATE = $CONFIG.DEFAULT_DATE
            $CONFIG.OUT_COL_DATA_SOURCE = $CONFIG.DEFAULT_DATA_SOURCE
        }
    }
    
    Write-Host "Totale righe da scrivere: $($outputData.Count)" -ForegroundColor Green
    
    # Esporta i dati nel file di destinazione
    Write-Host "Scrittura dati nel file $targetFile..." -ForegroundColor Cyan
    
    # Esporta con Export-Excel
    $outputData | Export-Excel -Path $targetFile -WorksheetName $CONFIG.TARGET_SHEET -AutoSize -TableName "RisorseUmane" -ClearSheet
    
    # Riapri il file per formattare le colonne con matricole come testo
    $excel = Open-ExcelPackage -Path $targetFile
    $worksheet = $excel.Workbook.Worksheets[$CONFIG.TARGET_SHEET]
    
    # Trova le colonne che contengono matricole (Person Code, Evaluator Code, Approver Code)
    $textColumns = @($CONFIG.OUT_COL_PERSON_CODE, $CONFIG.OUT_COL_EVALUATOR_CODE, $CONFIG.OUT_COL_APPROVER_CODE)
    
    # Per ogni colonna, trova l'indice e formatta come testo
    for ($colIdx = 1; $colIdx -le $worksheet.Dimension.Columns; $colIdx++) {
        $headerValue = $worksheet.Cells[1, $colIdx].Value
        if ($textColumns -contains $headerValue) {
            # Formatta tutta la colonna come testo (esclusa l'intestazione)
            for ($rowIdx = 2; $rowIdx -le $worksheet.Dimension.Rows; $rowIdx++) {
                $cell = $worksheet.Cells[$rowIdx, $colIdx]
                $cell.Style.Numberformat.Format = "@"  # @ = formato testo in Excel
                # Assicurati che il valore sia una stringa
                if ($null -ne $cell.Value) {
                    $cell.Value = $cell.Value.ToString()
                }
            }
        }
    }
    
    Close-ExcelPackage $excel -Show:$false
    
    Write-Host "COMPLETATO! File $targetFile generato con successo." -ForegroundColor Green
    Write-Host "Totale righe scritte: $($outputData.Count)" -ForegroundColor Green
    Write-Host "Valutatori identificati: $($valutatori.Count)" -ForegroundColor Green
    
} catch {
    Write-Host "ERRORE durante l'elaborazione: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host $_.Exception.StackTrace -ForegroundColor Red
    exit 1
}
