# Script per la compilazione automatica di IMPORT_SCHEDE.xls
# Legge i dati dal file Template_Dipendenti_AORN.xlsx (sheet "Template Dipendenti AORN" e "Legenda")
# e popola il file IMPORT_SCHEDE.xls (sheet "SCHEDE") con i dati delle schede di valutazione

# Verifica e installa il modulo ImportExcel se necessario
if (-not (Get-Module -ListAvailable -Name ImportExcel)) {
    Write-Host "Modulo ImportExcel non trovato. Installazione in corso..." -ForegroundColor Yellow
    Install-Module -Name ImportExcel -Scope CurrentUser -Force
    Write-Host "Modulo ImportExcel installato con successo." -ForegroundColor Green
}

Import-Module ImportExcel

# Percorsi dei file
$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$workspaceRoot = Split-Path -Parent $scriptDir
$sourceFile = Join-Path $workspaceRoot "..\Template_Dipendenti_AORN.xlsx"
$targetFile = Join-Path $workspaceRoot "..\IMPORT_SCHEDE.xlsx"

Write-Host "Inizio elaborazione..." -ForegroundColor Cyan

# Verifica esistenza file sorgente
if (-not (Test-Path $sourceFile)) {
    Write-Host "ERRORE: File sorgente $sourceFile non trovato!" -ForegroundColor Red
    exit 1
}

# Mapping dei codici template
$templateMapping = @{
    "SCHEDA 1" = "SCH1"
    "SCHEDA 1.1" = "SCH1B"
    "SCHEDA 2" = "SCH2"
    "SCHEDA 3" = "SCH3"
    "SCHEDA 4" = "SCH4"
    "SCHEDA 5" = "SCH5"
}

try {
    # Leggi i dati dal foglio "Legenda"
    Write-Host "Lettura dati dal foglio 'Legenda'..." -ForegroundColor Cyan
    $legendaData = Import-Excel -Path $sourceFile -WorksheetName "Legenda"
    
    # Crea un dizionario per il lookup veloce tra "Descrizione INCARICHI ECONOMICI" e i dati della Legenda
    # La tabella degli incarichi economici dovrebbe essere presente nel foglio Legenda
    $incarichiLookup = @{}
    
    # Crea anche un dizionario per il lookup tramite "Ruolo GZOOM"
    $ruoloLookup = @{}
    
    foreach ($row in $legendaData) {
        # Cerca le colonne relative agli incarichi economici
        # Assumiamo che ci siano colonne: "Descrizione INCARICHI ECONOMICI", "Tipo Scheda", "Descrizione Scheda"
        if ($null -ne $row.'Descrizione INCARICHI ECONOMICI' -and 
            -not [string]::IsNullOrWhiteSpace($row.'Descrizione INCARICHI ECONOMICI')) {
            
            $descrizioneIncarico = $row.'Descrizione INCARICHI ECONOMICI'.ToString().Trim()
            $tipoScheda = if ($row.'Tipo Scheda') { $row.'Tipo Scheda'.ToString().Trim() } else { "" }
            $descrizioneScheda = if ($row.'Descrizione Scheda') { $row.'Descrizione Scheda'.ToString().Trim() } else { "" }
            
            # Aggiungi al dizionario solo se non esiste già (primo valore vince)
            if (-not $incarichiLookup.ContainsKey($descrizioneIncarico)) {
                $incarichiLookup[$descrizioneIncarico] = @{
                    'TipoScheda' = $tipoScheda
                    'DescrizioneScheda' = $descrizioneScheda
                }
            }
        }
        
        # Lookup per "Ruolo GZOOM" -> "Descrizione Scheda"
        if ($null -ne $row.'Ruolo GZOOM' -and 
            -not [string]::IsNullOrWhiteSpace($row.'Ruolo GZOOM')) {
            
            $ruoloGZOOM = $row.'Ruolo GZOOM'.ToString().Trim()
            $descrizioneSchedaRuolo = if ($row.'Descrizione Scheda') { $row.'Descrizione Scheda'.ToString().Trim() } else { "" }
            
            # Aggiungi al dizionario solo se non esiste già (primo valore vince)
            if (-not $ruoloLookup.ContainsKey($ruoloGZOOM)) {
                $ruoloLookup[$ruoloGZOOM] = $descrizioneSchedaRuolo
            }
        }
    }
    
    Write-Host "Trovati $($incarichiLookup.Count) incarichi economici nella Legenda." -ForegroundColor Green
    Write-Host "Trovati $($ruoloLookup.Count) ruoli GZOOM nella Legenda." -ForegroundColor Green
    
    # Leggi i dati dal foglio "Template Dipendenti AORN"
    Write-Host "Lettura dati dal foglio 'Template Dipendenti AORN'..." -ForegroundColor Cyan
    $dipendentiData = Import-Excel -Path $sourceFile -WorksheetName "Template Dipendenti AORN"
    
    Write-Host "Trovati $($dipendentiData.Count) righe totali nel file sorgente." -ForegroundColor Green
    
    # Valori di default
    $defaultContesto = "IND"
    $defaultStato = "WEEVALST_PLANINIT"
    $defaultDescrizione = "Scheda valutazione Performance Anno 2025"
    
    # Crea la struttura dati per l'export
    $outputData = @()
    
    # Per ogni dipendente, crea la scheda corrispondente
    foreach ($row in $dipendentiData) {
        # Salta righe vuote (senza matricola)
        if ($null -eq $row.'Matricola' -or [string]::IsNullOrWhiteSpace($row.'Matricola')) {
            continue
        }
        
        # Estrai i dati con gestione dei valori null
        $matricola = if ($row.'Matricola') { $row.'Matricola'.ToString().Trim() } else { "" }
        $nome = if ($row.'Nome') { $row.'Nome'.ToString().Trim() } else { "" }
        $cognome = if ($row.'Cognome') { $row.'Cognome'.ToString().Trim() } else { "" }
        $codiceUOC = if ($row.'Codice UOC') { $row.'Codice UOC'.ToString().Trim() } else { "" }
        $matricolaValutatore = if ($row.'Matricola Referente Valutatore') { $row.'Matricola Referente Valutatore'.ToString().Trim() } else { "" }
        $descrizioneIncarico = if ($row.'Descrizione INCARICHI ECONOMICI') { $row.'Descrizione INCARICHI ECONOMICI'.ToString().Trim() } else { "" }
        $tipoScheda = if ($row.'Tipo Scheda') { $row.'Tipo Scheda'.ToString().Trim() } else { "" }
        $ruoloGZOOM = if ($row.'Ruolo GZOOM') { $row.'Ruolo GZOOM'.ToString().Trim() } else { "" }
        
        # Formatta le date senza orario (solo data nel formato dd/MM/yyyy)
        $decorrenza = ""
        if ($row.'Decorrenza') {
            if ($row.'Decorrenza' -is [DateTime]) {
                $decorrenza = $row.'Decorrenza'.ToString("dd/MM/yyyy")
            } else {
                # Se è già una stringa, prova a parsarla
                try {
                    $dateDecorrenza = [DateTime]::Parse($row.'Decorrenza'.ToString())
                    $decorrenza = $dateDecorrenza.ToString("dd/MM/yyyy")
                } catch {
                    $decorrenza = $row.'Decorrenza'.ToString().Trim()
                }
            }
        }
        
        $scadenza = ""
        if ($row.'Scadenza') {
            if ($row.'Scadenza' -is [DateTime]) {
                $scadenza = $row.'Scadenza'.ToString("dd/MM/yyyy")
            } else {
                # Se è già una stringa, prova a parsarla
                try {
                    $dateScadenza = [DateTime]::Parse($row.'Scadenza'.ToString())
                    $scadenza = $dateScadenza.ToString("dd/MM/yyyy")
                } catch {
                    $scadenza = $row.'Scadenza'.ToString().Trim()
                }
            }
        }
        
        # Genera il Codice Scheda: "SCH_" + Matricola
        $codiceScheda = "SCH_$matricola"
        
        # Lookup nella Legenda per ottenere la Descrizione Scheda tramite "Descrizione INCARICHI ECONOMICI"
        $descrizioneSchedaIncarichi = ""
        if ($descrizioneIncarico -and $incarichiLookup.ContainsKey($descrizioneIncarico)) {
            $descrizioneSchedaIncarichi = $incarichiLookup[$descrizioneIncarico].DescrizioneScheda
        }
        
        # Lookup nella Legenda per ottenere la Descrizione Scheda tramite "Ruolo GZOOM"
        $descrizioneSchedaRuolo = ""
        if ($ruoloGZOOM -and $ruoloLookup.ContainsKey($ruoloGZOOM)) {
            $descrizioneSchedaRuolo = $ruoloLookup[$ruoloGZOOM]
        }
        
        # Applica il mapping del Codice Template (es. "SCHEDA 1" -> "SCH1")
        $codiceTemplate = ""
        if ($tipoScheda -and $templateMapping.ContainsKey($tipoScheda)) {
            $codiceTemplate = $templateMapping[$tipoScheda]
        } else {
            $codiceTemplate = $tipoScheda  # Se non c'è mapping, usa il valore originale
        }
        
        # Genera il Nome Scheda: Cognome + Nome + (Matricola) + " - " + Descrizione Scheda (da Ruolo GZOOM)
        $nomeScheda = "$cognome $nome ($matricola)"
        if ($descrizioneSchedaRuolo) {
            $nomeScheda += " - $descrizioneSchedaRuolo"
        }
        
        # Crea l'oggetto con tutti i campi mappati
        $outputData += [PSCustomObject]@{
            'Contesto' = $defaultContesto
            'Codice Scheda' = $codiceScheda
            'Nome Scheda' = $nomeScheda
            'Matricola Valutato' = $matricola
            'Matricola Valutatore' = $matricolaValutatore
            'Codice UOC' = $codiceUOC
            'Codice Template' = $codiceTemplate
            'Data Inizio' = $decorrenza
            'Data Fine' = $scadenza
            'Stato' = $defaultStato
            'Descrizione' = $defaultDescrizione
        }
    }
    
    Write-Host "Totale schede da scrivere: $($outputData.Count)" -ForegroundColor Green
    
    # Esporta i dati nel file di destinazione (sheet "SCHEDE")
    Write-Host "Scrittura dati nel file $targetFile (sheet 'SCHEDE')..." -ForegroundColor Cyan
    
    $outputData | Export-Excel -Path $targetFile -WorksheetName "SCHEDE" -AutoSize -TableName "Schede" -ClearSheet
    
    Write-Host "COMPLETATO! File $targetFile generato con successo." -ForegroundColor Green
    Write-Host "Totale schede scritte: $($outputData.Count)" -ForegroundColor Green
    
} catch {
    Write-Host "ERRORE durante l'elaborazione: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host $_.Exception.StackTrace -ForegroundColor Red
    exit 1
}
