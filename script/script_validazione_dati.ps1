# ============================================================================
# SCRIPT DI VALIDAZIONE DATI - Template_Dipendenti_AORN.xlsx
# ============================================================================
# Questo script valida i dati del file Excel prima del processo di import massivo.
# Verifica che tutti i campi obbligatori siano valorizzati per ogni record.
# Se ci sono errori, mostra i dettagli e termina con exit code 1.
# Se tutto e OK, termina con exit code 0.
# ============================================================================

# Anno di riferimento per le validazioni delle date
$ANNO_RIFERIMENTO = 2025

# Configurazione
$CONFIG = @{
    # Percorsi
    SCRIPT_DIR = $PSScriptRoot
    TEMPLATE_DIR = "template"
    
    # File sorgente
    SOURCE_FILE = "Template_Dipendenti_AORN.xlsx"
    SOURCE_SHEET = "Template Dipendenti AORN"
    
    # Anno di riferimento
    ANNO_RIFERIMENTO = $ANNO_RIFERIMENTO
    
    # Date limiti per validazione (derivate dall'anno di riferimento)
    DATA_MIN = Get-Date -Year $ANNO_RIFERIMENTO -Month 1 -Day 1 -Hour 0 -Minute 0 -Second 0
    DATA_MAX = Get-Date -Year $ANNO_RIFERIMENTO -Month 12 -Day 31 -Hour 23 -Minute 59 -Second 59
    
    # Campi obbligatori da validare
    REQUIRED_FIELDS = @(
        "Matricola",
        "Nome",
        "Cognome",
        "Codice Fiscale",
        "Ruolo GZOOM",
        "Codice UOC",
        "Nome UOC",
        "Matricola Referente Valutatore",
        "Email",
        "Username"
    )
}

# ============================================================================
# FUNZIONI DI UTILITA
# ============================================================================

function Write-ColorMessage {
    param(
        [string]$Message,
        [string]$Color = "White",
        [switch]$NoNewline
    )
    
    if ($NoNewline) {
        Write-Host $Message -ForegroundColor $Color -NoNewline
    } else {
        Write-Host $Message -ForegroundColor $Color
    }
}

function Write-Header {
    param([string]$Title)
    
    Write-Host ""
    Write-Host ("=" * 80) -ForegroundColor Cyan
    Write-Host $Title -ForegroundColor Cyan
    Write-Host ("=" * 80) -ForegroundColor Cyan
    Write-Host ""
}

function Write-Section {
    param([string]$Title)
    
    Write-Host ""
    Write-Host ("-" * 80) -ForegroundColor Yellow
    Write-Host $Title -ForegroundColor Yellow
    Write-Host ("-" * 80) -ForegroundColor Yellow
    Write-Host ""
}

# ============================================================================
# FUNZIONI DI VALIDAZIONE
# ============================================================================

function Test-FieldEmpty {
    param([object]$Value)
    
    if ($null -eq $Value) { return $true }
    
    $strValue = $Value.ToString().Trim()
    
    # Considera vuoti anche i valori "0", "0.0", "-", "N/A", "NULL", etc.
    if ([string]::IsNullOrWhiteSpace($strValue) -or
        $strValue -eq "0" -or
        $strValue -eq "0.0" -or
        $strValue -eq "-" -or
        $strValue -eq "N/A" -or
        $strValue -eq "NULL" -or
        $strValue -eq "n/a" -or
        $strValue -eq "null") {
        return $true
    }
    
    return $false
}

function Get-MatricolaValue {
    param([object]$Row)
    
    $matricola = $Row.Matricola
    
    if (Test-FieldEmpty -Value $matricola) {
        return "<NON SPECIFICATA>"
    }
    
    # Rimuove il .0 finale se presente (es: 123456.0 -> 123456)
    if ($matricola.ToString().EndsWith(".0")) {
        return $matricola.ToString().Replace(".0", "")
    }
    
    if ([string]::IsNullOrWhiteSpace($matricola.ToString())) {
        return "<VUOTA>"
    }
    
    return $matricola
}

# ============================================================================
# INIZIO SCRIPT PRINCIPALE
# ============================================================================

Write-Header "VALIDAZIONE DATI - Template_Dipendenti_AORN.xlsx"

# Verifica e installa modulo ImportExcel se necessario
if (-not (Get-Module -ListAvailable -Name ImportExcel)) {
    Write-ColorMessage "Modulo ImportExcel non trovato. Installazione in corso..." "Yellow"
    try {
        Install-Module -Name ImportExcel -Scope CurrentUser -Force -AllowClobber
        Write-ColorMessage "Modulo ImportExcel installato con successo." "Green"
    } catch {
        Write-ColorMessage "ERRORE: Impossibile installare il modulo ImportExcel." "Red"
        Write-ColorMessage "Dettaglio errore: $($_.Exception.Message)" "Red"
        exit 1
    }
}

Import-Module ImportExcel

# ============================================================================
# CARICAMENTO DATI DA FILE EXCEL
# ============================================================================

$sourceFile = Join-Path (Join-Path $CONFIG.SCRIPT_DIR $CONFIG.TEMPLATE_DIR) $CONFIG.SOURCE_FILE

Write-ColorMessage "File da validare: " "Cyan" -NoNewline
Write-ColorMessage $sourceFile "White"
Write-ColorMessage "Foglio: " "Cyan" -NoNewline
Write-ColorMessage $CONFIG.SOURCE_SHEET "White"
Write-Host ""

# Verifica esistenza file
if (-not (Test-Path $sourceFile)) {
    Write-ColorMessage "ERRORE: File sorgente non trovato!" "Red"
    Write-ColorMessage "Percorso: $sourceFile" "Red"
    exit 1
}

Write-ColorMessage "Lettura dati dal file Excel..." "Cyan"

try {
    $dipendentiData = Import-Excel -Path $sourceFile -WorksheetName $CONFIG.SOURCE_SHEET
    
    if ($null -eq $dipendentiData -or $dipendentiData.Count -eq 0) {
        Write-ColorMessage "ERRORE: Il foglio Excel e vuoto o non contiene dati validi!" "Red"
        exit 1
    }
    
    Write-ColorMessage "Trovate $($dipendentiData.Count) righe nel file." "Green"
} catch {
    Write-ColorMessage "ERRORE durante la lettura del file Excel!" "Red"
    Write-ColorMessage "Dettaglio errore: $($_.Exception.Message)" "Red"
    exit 1
}

# ============================================================================
# FASE 1: VERIFICA PRESENZA COLONNE NEL FILE
# ============================================================================

Write-Section "FASE 1: Verifica presenza colonne nel file"

$availableColumns = $dipendentiData[0].PSObject.Properties.Name
$missingColumns = @()

Write-ColorMessage "Colonne disponibili nel file:" "Cyan"
foreach ($col in $availableColumns) {
    Write-ColorMessage "  - $col" "Gray"
}

Write-Host ""

foreach ($requiredCol in $CONFIG.REQUIRED_FIELDS) {
    if ($availableColumns -notcontains $requiredCol) {
        $missingColumns += $requiredCol
    }
}

if ($missingColumns.Count -gt 0) {
    Write-ColorMessage "ERRORE: Mancano le seguenti colonne obbligatorie nel file Excel:" "Red"
    foreach ($col in $missingColumns) {
        Write-ColorMessage "  X $col" "Red"
    }
    Write-Host ""
    Write-ColorMessage "Impossibile procedere con la validazione." "Red"
    exit 1
} else {
    Write-ColorMessage "OK - Tutte le colonne obbligatorie sono presenti." "Green"
}

# ============================================================================
# FASE 2: VALIDAZIONE COMPLETEZZA DATI PER OGNI RECORD
# ============================================================================

Write-Section "FASE 2: Validazione completezza dati per ogni record"

$validationErrors = @()
$processedRecords = 0
$skippedEmptyRows = 0
$rowNumber = 2  # Inizia da 2 perche la riga 1 e la intestazione

foreach ($row in $dipendentiData) {
    # Verifica se la riga e completamente vuota (tutti i campi obbligatori sono vuoti)
    $allFieldsEmpty = $true
    foreach ($field in $CONFIG.REQUIRED_FIELDS) {
        if (-not (Test-FieldEmpty -Value $row.$field)) {
            $allFieldsEmpty = $false
            break
        }
    }
    
    if ($allFieldsEmpty) {
        # Salta le righe completamente vuote
        $skippedEmptyRows++
        $rowNumber++
        continue
    }
    
    # La riga ha almeno un campo valorizzato, quindi la processiamo
    $processedRecords++
    $matricola = Get-MatricolaValue -Row $row
    $missingFields = @()
    
    # Verifica ogni campo obbligatorio
    foreach ($field in $CONFIG.REQUIRED_FIELDS) {
        if (Test-FieldEmpty -Value $row.$field) {
            $missingFields += $field
        }
    }
    
    # Se ci sono campi mancanti, registra errore
    if ($missingFields.Count -gt 0) {
        $validationErrors += @{
            RowNumber = $rowNumber
            Matricola = $matricola
            MissingFields = $missingFields
        }
    }
    
    $rowNumber++
}

# ============================================================================
# FASE 3: VALIDAZIONE UNICITA MATRICOLE
# ============================================================================

Write-Section "FASE 3: Validazione unicita matricole"

# Verifica che non ci siano matricole duplicate
$matricoleCount = @{}
$duplicateErrors = @()
$rowNumber = 2

foreach ($row in $dipendentiData) {
    # Salta righe completamente vuote
    $allFieldsEmpty = $true
    foreach ($field in $CONFIG.REQUIRED_FIELDS) {
        if (-not (Test-FieldEmpty -Value $row.$field)) {
            $allFieldsEmpty = $false
            break
        }
    }
    
    if (-not $allFieldsEmpty) {
        $matricola = Get-MatricolaValue -Row $row
        if ($matricola -ne "<NON SPECIFICATA>" -and $matricola -ne "<VUOTA>") {
            $matricolaClean = $matricola.ToString().Replace('.0', '').Trim()
            if ($matricolaClean) {
                if (-not $matricoleCount.ContainsKey($matricolaClean)) {
                    $matricoleCount[$matricolaClean] = @()
                }
                $matricoleCount[$matricolaClean] += $rowNumber
            }
        }
    }
    $rowNumber++
}

# Trova duplicati
foreach ($matricola in $matricoleCount.Keys) {
    if ($matricoleCount[$matricola].Count -gt 1) {
        $duplicateErrors += [PSCustomObject]@{
            Matricola = $matricola
            Rows = $matricoleCount[$matricola]
        }
    }
}

if ($duplicateErrors.Count -gt 0) {
    Write-ColorMessage "X - Trovate $($duplicateErrors.Count) matricole duplicate!" "Red"
} else {
    Write-ColorMessage "OK - Tutte le $($matricoleCount.Count) matricole sono univoche." "Green"
}

# ============================================================================
# FASE 4: VALIDAZIONE REFERENTI VALUTATORI
# ============================================================================

Write-Section "FASE 4: Validazione referenti valutatori"

# Costruisce un set di tutte le matricole valide presenti nel file
$validMatricole = @{}
$rowNumber = 2

foreach ($row in $dipendentiData) {
    # Salta righe completamente vuote
    $allFieldsEmpty = $true
    foreach ($field in $CONFIG.REQUIRED_FIELDS) {
        if (-not (Test-FieldEmpty -Value $row.$field)) {
            $allFieldsEmpty = $false
            break
        }
    }
    
    if (-not $allFieldsEmpty) {
        $matricola = Get-MatricolaValue -Row $row
        if ($matricola -ne "<NON SPECIFICATA>" -and $matricola -ne "<VUOTA>") {
            # Rimuove il .0 se presente
            $matricolaClean = $matricola.ToString().Replace('.0', '').Trim()
            if ($matricolaClean) {
                $validMatricole[$matricolaClean] = $true
            }
        }
    }
}

Write-ColorMessage "Trovate $($validMatricole.Count) matricole uniche nel file." "Cyan"

# Verifica che ogni referente valutatore esista nel file
$referentErrors = @()
$rowNumber = 2

foreach ($row in $dipendentiData) {
    # Salta righe completamente vuote
    $allFieldsEmpty = $true
    foreach ($field in $CONFIG.REQUIRED_FIELDS) {
        if (-not (Test-FieldEmpty -Value $row.$field)) {
            $allFieldsEmpty = $false
            break
        }
    }
    
    if (-not $allFieldsEmpty) {
        $matricolaDipendente = Get-MatricolaValue -Row $row
        $matricolaReferente = $row.'Matricola Referente Valutatore'
        
        # Se il referente e valorizzato, verifica che esista
        if (-not (Test-FieldEmpty -Value $matricolaReferente)) {
            # Pulisce la matricola referente
            $matricolaRefClean = $matricolaReferente.ToString().Replace('.0', '').Trim()
            
            if (-not $validMatricole.ContainsKey($matricolaRefClean)) {
                $referentErrors += @{
                    RowNumber = $rowNumber
                    MatricolaDipendente = $matricolaDipendente
                    MatricolaReferente = $matricolaRefClean
                }
            }
        }
    }
    
    $rowNumber++
}

if ($referentErrors.Count -gt 0) {
    Write-ColorMessage "X - Trovati $($referentErrors.Count) referenti non validi." "Red"
} else {
    Write-ColorMessage "OK - Tutti i referenti valutatori sono presenti nel file." "Green"
}

# ============================================================================
# FASE 5: VALIDAZIONE DATE DECORRENZA E SCADENZA
# ============================================================================

Write-Section "FASE 5: Validazione date Decorrenza e Scadenza"

Write-ColorMessage "Anno di riferimento: $($CONFIG.ANNO_RIFERIMENTO)" "Cyan"
Write-ColorMessage "Periodo valido: 01/01/$($CONFIG.ANNO_RIFERIMENTO) - 31/12/$($CONFIG.ANNO_RIFERIMENTO)" "Cyan"

# Funzione per parsare le date
function ConvertTo-DateSafe {
    param([object]$Value)
    
    if (Test-FieldEmpty -Value $Value) {
        return $null
    }
    
    try {
        if ($Value -is [DateTime]) {
            return $Value
        }
        
        if ($Value -is [string]) {
            # Prova diversi formati
            $formats = @('dd/MM/yyyy', 'yyyy-MM-dd', 'dd-MM-yyyy', 'yyyy/MM/dd')
            foreach ($fmt in $formats) {
                try {
                    return [DateTime]::ParseExact($Value, $fmt, $null)
                } catch {
                    continue
                }
            }
        }
        
        # Prova conversione diretta
        return [DateTime]$Value
    } catch {
        return $null
    }
}

$dateErrors = @()
$rowNumber = 2

foreach ($row in $dipendentiData) {
    # Salta righe completamente vuote
    $allFieldsEmpty = $true
    foreach ($field in $CONFIG.REQUIRED_FIELDS) {
        if (-not (Test-FieldEmpty -Value $row.$field)) {
            $allFieldsEmpty = $false
            break
        }
    }
    
    if (-not $allFieldsEmpty) {
        $matricolaDipendente = Get-MatricolaValue -Row $row
        $decorrenzaValue = $row.Decorrenza
        $scadenzaValue = $row.Scadenza
        
        $errorMessages = @()
        
        $decorrenzaDate = ConvertTo-DateSafe -Value $decorrenzaValue
        $scadenzaDate = ConvertTo-DateSafe -Value $scadenzaValue
        
        # Verifica Decorrenza
        if ($decorrenzaDate) {
            # Normalizza al solo giorno per confronto (ignora ore/minuti/secondi)
            $decorrenzaDateOnly = $decorrenzaDate.Date
            if ($decorrenzaDateOnly -lt $CONFIG.DATA_MIN.Date) {
                $errorMessages += "Decorrenza ($($decorrenzaDate.ToString('dd/MM/yyyy'))) deve essere maggiore o uguale a 01/01/$($CONFIG.ANNO_RIFERIMENTO)"
            } elseif ($decorrenzaDateOnly -gt $CONFIG.DATA_MAX.Date) {
                $errorMessages += "Decorrenza ($($decorrenzaDate.ToString('dd/MM/yyyy'))) deve essere minore o uguale a 31/12/$($CONFIG.ANNO_RIFERIMENTO)"
            }
        }
        
        # Verifica Scadenza
        if ($scadenzaDate) {
            # Normalizza al solo giorno per confronto (ignora ore/minuti/secondi)
            $scadenzaDateOnly = $scadenzaDate.Date
            if ($scadenzaDateOnly -lt $CONFIG.DATA_MIN.Date) {
                $errorMessages += "Scadenza ($($scadenzaDate.ToString('dd/MM/yyyy'))) deve essere maggiore o uguale a 01/01/$($CONFIG.ANNO_RIFERIMENTO)"
            } elseif ($scadenzaDateOnly -gt $CONFIG.DATA_MAX.Date) {
                $errorMessages += "Scadenza ($($scadenzaDate.ToString('dd/MM/yyyy'))) deve essere minore o uguale a 31/12/$($CONFIG.ANNO_RIFERIMENTO)"
            }
        }
        
        # Verifica che Scadenza sia successiva a Decorrenza
        if ($decorrenzaDate -and $scadenzaDate) {
            if ($scadenzaDate -le $decorrenzaDate) {
                $errorMessages += "Scadenza ($($scadenzaDate.ToString('dd/MM/yyyy'))) deve essere successiva a Decorrenza ($($decorrenzaDate.ToString('dd/MM/yyyy')))"
            }
        }
        
        if ($errorMessages.Count -gt 0) {
            $dateErrors += @{
                RowNumber = $rowNumber
                MatricolaDipendente = $matricolaDipendente
                ErrorMessages = $errorMessages
            }
        }
    }
    
    $rowNumber++
}

if ($dateErrors.Count -gt 0) {
    Write-ColorMessage "X - Trovati $($dateErrors.Count) record con date non valide." "Red"
} else {
    Write-ColorMessage "OK - Tutte le date Decorrenza e Scadenza sono valide." "Green"
}

# ============================================================================
# FASE 6: REPORT FINALE
# ============================================================================

Write-Section "FASE 6: Report di validazione"

Write-ColorMessage "Totale righe nel file: " "Cyan" -NoNewline
Write-ColorMessage $dipendentiData.Count "White"
Write-ColorMessage "Righe completamente vuote (saltate): " "Gray" -NoNewline
Write-ColorMessage $skippedEmptyRows "White"
Write-ColorMessage "Record effettivamente valorizzati: " "Cyan" -NoNewline
Write-ColorMessage $processedRecords "White"

if ($validationErrors.Count -eq 0 -and $duplicateErrors.Count -eq 0 -and $referentErrors.Count -eq 0 -and $dateErrors.Count -eq 0) {
    Write-Host ""
    Write-ColorMessage ("=" * 80) "Green"
    Write-ColorMessage "  VALIDAZIONE COMPLETATA CON SUCCESSO!" "Green"
    Write-ColorMessage ("=" * 80) "Green"
    Write-Host ""
    if ($processedRecords -eq 0) {
        Write-ColorMessage "ATTENZIONE: Non ci sono record valorizzati nel file." "Yellow"
        Write-ColorMessage "Il file contiene solo righe vuote." "Yellow"
    } else {
        Write-ColorMessage "Tutti i $processedRecords record valorizzati hanno tutti i campi obbligatori completi." "Green"
        Write-ColorMessage "Il file e pronto per il processo di import massivo." "Green"
    }
    Write-Host ""
    exit 0
    
} else {
    Write-Host ""
    Write-ColorMessage ("=" * 80) "Red"
    Write-ColorMessage "  VALIDAZIONE FALLITA!" "Red"
    Write-ColorMessage ("=" * 80) "Red"
    Write-Host ""
    
    # Raggruppa tutti gli errori per riga
    $errorsByRow = @{}
    
    # Aggiungi errori di campi mancanti
    foreach ($validationError in $validationErrors) {
        $rowNum = $validationError.RowNumber
        if (-not $errorsByRow.ContainsKey($rowNum)) {
            $errorsByRow[$rowNum] = @{
                Matricola = $validationError.Matricola
                MissingFields = @()
                DuplicateError = $null
                ReferentError = $null
                DateErrors = @()
            }
        }
        $errorsByRow[$rowNum].MissingFields = $validationError.MissingFields
    }
    
    # Aggiungi errori di matricole duplicate
    foreach ($dupError in $duplicateErrors) {
        foreach ($rowNum in $dupError.Rows) {
            if (-not $errorsByRow.ContainsKey($rowNum)) {
                $errorsByRow[$rowNum] = @{
                    Matricola = $dupError.Matricola
                    MissingFields = @()
                    DuplicateError = $null
                    ReferentError = $null
                    DateErrors = @()
                }
            }
            # Mostra tutte le altre righe dove compare la stessa matricola
            $otherRows = $dupError.Rows | Where-Object { $_ -ne $rowNum }
            $errorsByRow[$rowNum].DuplicateError = $otherRows
        }
    }
    
    # Aggiungi errori referenti
    foreach ($refError in $referentErrors) {
        $rowNum = $refError.RowNumber
        if (-not $errorsByRow.ContainsKey($rowNum)) {
            $errorsByRow[$rowNum] = @{
                Matricola = $refError.MatricolaDipendente
                MissingFields = @()
                DuplicateError = $null
                ReferentError = $null
                DateErrors = @()
            }
        }
        $errorsByRow[$rowNum].ReferentError = $refError.MatricolaReferente
    }
    
    # Aggiungi errori date
    foreach ($dateError in $dateErrors) {
        $rowNum = $dateError.RowNumber
        if (-not $errorsByRow.ContainsKey($rowNum)) {
            $errorsByRow[$rowNum] = @{
                Matricola = $dateError.MatricolaDipendente
                MissingFields = @()
                DuplicateError = $null
                ReferentError = $null
                DateErrors = @()
            }
        }
        $errorsByRow[$rowNum].DateErrors = $dateError.ErrorMessages
    }
    
    # Mostra riepilogo errori
    $totalErrors = $validationErrors.Count + $duplicateErrors.Count + $referentErrors.Count + $dateErrors.Count
    Write-ColorMessage "Trovati $totalErrors errori in $($errorsByRow.Count) record su $processedRecords valorizzati." "Red"
    Write-Host ""
    Write-ColorMessage "DETTAGLIO ERRORI PER RECORD:" "Yellow"
    Write-ColorMessage ("=" * 80) "Yellow"
    
    # Mostra errori raggruppati per riga
    foreach ($rowNum in ($errorsByRow.Keys | Sort-Object)) {
        $errorData = $errorsByRow[$rowNum]
        Write-Host ""
        Write-ColorMessage "Riga $rowNum - Matricola: " "Red" -NoNewline
        Write-ColorMessage $errorData.Matricola "White"
        
        # Campi mancanti
        if ($errorData.MissingFields.Count -gt 0) {
            Write-ColorMessage "  X Campi obbligatori NON valorizzati:" "Yellow"
            foreach ($field in $errorData.MissingFields) {
                Write-ColorMessage "      - $field" "Red"
            }
        }
        
        # Matricola duplicata
        if ($errorData.DuplicateError) {
            $otherRowsStr = $errorData.DuplicateError -join ", "
            Write-ColorMessage "  X Matricola DUPLICATA: presente anche nelle righe " "Yellow" -NoNewline
            Write-ColorMessage $otherRowsStr "Red"
        }
        
        # Errore referente
        if ($errorData.ReferentError) {
            Write-ColorMessage "  X Referente valutatore: matricola " "Yellow" -NoNewline
            Write-ColorMessage $errorData.ReferentError "Red" -NoNewline
            Write-ColorMessage " NON ESISTE nel file" "Yellow"
        }
        
        # Errori date
        if ($errorData.DateErrors.Count -gt 0) {
            Write-ColorMessage "  X Errori date:" "Yellow"
            foreach ($msg in $errorData.DateErrors) {
                Write-ColorMessage "      - $msg" "Red"
            }
        }
    }
    
    Write-Host ""
    Write-ColorMessage ("=" * 80) "Yellow"
    Write-Host ""
    
    Write-ColorMessage "AZIONE RICHIESTA:" "Yellow"
    Write-ColorMessage "Correggere i campi mancanti nel file Excel prima di procedere con import." "Yellow"
    Write-Host ""
    
    exit 1
}
