# Script per la compilazione automatica di IMPORT_SCHEDE.xls
# Legge i dati dal file Template_Dipendenti_AORN.xlsx (sheet "Template Dipendenti AORN" e "Legenda")
# e popola il file IMPORT_SCHEDE.xls (sheet "SCHEDE") con i dati delle schede di valutazione

# ============================================================================
# CONFIGURAZIONE - NOMI FILE E CARTELLE
# ============================================================================
$CONFIG = @{
    # Cartelle
    TEMPLATE_DIR = "template"
    
    # File sorgente
    SOURCE_FILE = "Template_Dipendenti_AORN.xlsx"
    SOURCE_SHEET_LEGENDA = "Legenda"
    SOURCE_SHEET_DIPENDENTI = "Template Dipendenti AORN"
    
    # File destinazione
    TARGET_FILE = "IMPORT_SCHEDE.xlsx"
    TARGET_SHEET = "SCHEDE"
    
    # Colonne foglio Legenda
    LEG_COL_DESC_INCARICHI = "Descrizione INCARICHI ECONOMICI"
    LEG_COL_TIPO_SCHEDA = "Tipo Scheda"
    LEG_COL_DESC_SCHEDA = "Descrizione Scheda"
    LEG_COL_RUOLO_GZOOM = "Ruolo GZOOM"
    
    # Colonne foglio Dipendenti
    DIP_COL_MATRICOLA = "Matricola"
    DIP_COL_NOME = "Nome"
    DIP_COL_COGNOME = "Cognome"
    DIP_COL_CODICE_UOC = "Codice UOC"
    DIP_COL_MATR_VALUTATORE = "Matricola Referente Valutatore"
    DIP_COL_DESC_INCARICHI = "Descrizione INCARICHI ECONOMICI"
    DIP_COL_TIPO_SCHEDA = "Tipo Scheda"
    DIP_COL_RUOLO_GZOOM = "Ruolo GZOOM"
    DIP_COL_DECORRENZA = "Decorrenza"
    DIP_COL_SCADENZA = "Scadenza"
    
    # Colonne file destinazione
    OUT_COL_CONTESTO = "Contesto"
    OUT_COL_CODICE_SCHEDA = "Codice Scheda"
    OUT_COL_NOME_SCHEDA = "Nome Scheda"
    OUT_COL_MATR_VALUTATO = "Matricola Valutato"
    OUT_COL_MATR_VALUTATORE = "Matricola Valutatore"
    OUT_COL_CODICE_UOC = "Codice UOC"
    OUT_COL_CODICE_TEMPLATE = "Codice Template"
    OUT_COL_DATA_INIZIO = "Data Inizio"
    OUT_COL_DATA_FINE = "Data Fine"
    OUT_COL_STATO = "Stato"
    OUT_COL_DESCRIZIONE = "Descrizione"
    
    # Mapping dei codici template
    TEMPLATE_MAPPING = @{
        "SCHEDA 1" = "SCH1"
        "SCHEDA 1.1" = "SCH1B"
        "SCHEDA 2" = "SCH2"
        "SCHEDA 3" = "SCH3"
        "SCHEDA 4" = "SCH4"
        "SCHEDA 5" = "SCH5"
    }
    
    # Valori costanti
    DEFAULT_CONTESTO = "IND"
    DEFAULT_STATO = "WEEVALST_PLANINIT"
    DEFAULT_DESCRIZIONE = "Scheda valutazione Performance Anno 2025"
    CODICE_SCHEDA_PREFIX = "SCH_"
    DATE_FORMAT = "dd/MM/yyyy"
}

# Funzione per pulire i valori rimuovendo il ".0" dalle matricole
function Clean-Value {
    param (
        [Parameter(ValueFromPipeline=$true)]
        $Value
    )
    
    # Gestisci valori null o vuoti
    if ($null -eq $Value -or [string]::IsNullOrWhiteSpace($Value)) {
        return ""
    }
    
    # Converti in stringa e rimuovi spazi
    $strValue = $Value.ToString().Trim()
    
    # Se termina con ".0", verifica se è un numero e rimuovi il ".0"
    if ($strValue.EndsWith(".0")) {
        try {
            # Verifica se può essere convertito in numero
            [void][double]::Parse($strValue)
            # Se sì, rimuovi il ".0"
            return $strValue.Substring(0, $strValue.Length - 2)
        } catch {
            # Se non è un numero valido, restituisci il valore originale
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

try {
    # Leggi i dati dal foglio "Legenda"
    Write-Host "Lettura dati dal foglio '$($CONFIG.SOURCE_SHEET_LEGENDA)'..." -ForegroundColor Cyan
    $legendaData = Import-Excel -Path $sourceFile -WorksheetName $CONFIG.SOURCE_SHEET_LEGENDA
    
    # Crea un dizionario per il lookup veloce tra "Descrizione INCARICHI ECONOMICI" e i dati della Legenda
    # La tabella degli incarichi economici dovrebbe essere presente nel foglio Legenda
    $incarichiLookup = @{}
    
    # Crea anche un dizionario per il lookup tramite "Ruolo GZOOM"
    $ruoloLookup = @{}
    
    foreach ($row in $legendaData) {
        # Cerca le colonne relative agli incarichi economici
        # Assumiamo che ci siano colonne: "Descrizione INCARICHI ECONOMICI", "Tipo Scheda", "Descrizione Scheda"
        if ($null -ne $row.($CONFIG.LEG_COL_DESC_INCARICHI) -and 
            -not [string]::IsNullOrWhiteSpace($row.($CONFIG.LEG_COL_DESC_INCARICHI))) {
            
            # Usa Clean-Value per pulire i dati
            $descrizioneIncarico = Clean-Value $row.($CONFIG.LEG_COL_DESC_INCARICHI)
            $tipoScheda = Clean-Value $row.($CONFIG.LEG_COL_TIPO_SCHEDA)
            $descrizioneScheda = Clean-Value $row.($CONFIG.LEG_COL_DESC_SCHEDA)
            
            # Aggiungi al dizionario solo se non esiste già (primo valore vince)
            if (-not $incarichiLookup.ContainsKey($descrizioneIncarico)) {
                $incarichiLookup[$descrizioneIncarico] = @{
                    'TipoScheda' = $tipoScheda
                    'DescrizioneScheda' = $descrizioneScheda
                }
            }
        }
        
        # Lookup per "Ruolo GZOOM" -> "Descrizione Scheda"
        if ($null -ne $row.($CONFIG.LEG_COL_RUOLO_GZOOM) -and 
            -not [string]::IsNullOrWhiteSpace($row.($CONFIG.LEG_COL_RUOLO_GZOOM))) {
            
            # Usa Clean-Value per pulire i dati
            $ruoloGZOOM = Clean-Value $row.($CONFIG.LEG_COL_RUOLO_GZOOM)
            $descrizioneSchedaRuolo = Clean-Value $row.($CONFIG.LEG_COL_DESC_SCHEDA)
            
            # Aggiungi al dizionario solo se non esiste già (primo valore vince)
            if (-not $ruoloLookup.ContainsKey($ruoloGZOOM)) {
                $ruoloLookup[$ruoloGZOOM] = $descrizioneSchedaRuolo
            }
        }
    }
    
    Write-Host "Trovati $($incarichiLookup.Count) incarichi economici nella Legenda." -ForegroundColor Green
    Write-Host "Trovati $($ruoloLookup.Count) ruoli GZOOM nella Legenda." -ForegroundColor Green
    
    # Leggi i dati dal foglio "Template Dipendenti AORN"
    Write-Host "Lettura dati dal foglio '$($CONFIG.SOURCE_SHEET_DIPENDENTI)'..." -ForegroundColor Cyan
    $dipendentiData = Import-Excel -Path $sourceFile -WorksheetName $CONFIG.SOURCE_SHEET_DIPENDENTI
    
    Write-Host "Trovati $($dipendentiData.Count) righe totali nel file sorgente." -ForegroundColor Green
    
    # Crea la struttura dati per l'export
    $outputData = @()
    
    # Per ogni dipendente, crea la scheda corrispondente
    foreach ($row in $dipendentiData) {
        # Salta righe vuote (senza matricola)
        if ($null -eq $row.($CONFIG.DIP_COL_MATRICOLA) -or [string]::IsNullOrWhiteSpace($row.($CONFIG.DIP_COL_MATRICOLA))) {
            continue
        }
        
        # Estrai i dati usando Clean-Value per rimuovere i ".0" dalle matricole
        $matricola = Clean-Value $row.($CONFIG.DIP_COL_MATRICOLA)
        $nome = Clean-Value $row.($CONFIG.DIP_COL_NOME)
        $cognome = Clean-Value $row.($CONFIG.DIP_COL_COGNOME)
        $codiceUOC = Clean-Value $row.($CONFIG.DIP_COL_CODICE_UOC)
        $matricolaValutatore = Clean-Value $row.($CONFIG.DIP_COL_MATR_VALUTATORE)
        $descrizioneIncarico = Clean-Value $row.($CONFIG.DIP_COL_DESC_INCARICHI)
        $tipoScheda = Clean-Value $row.($CONFIG.DIP_COL_TIPO_SCHEDA)
        $ruoloGZOOM = Clean-Value $row.($CONFIG.DIP_COL_RUOLO_GZOOM)
        
        # Formatta le date senza orario (solo data nel formato dd/MM/yyyy)
        $decorrenza = ""
        if ($row.($CONFIG.DIP_COL_DECORRENZA)) {
            if ($row.($CONFIG.DIP_COL_DECORRENZA) -is [DateTime]) {
                $decorrenza = $row.($CONFIG.DIP_COL_DECORRENZA).ToString($CONFIG.DATE_FORMAT)
            } else {
                # Se è già una stringa, prova a parsarla
                try {
                    $dateDecorrenza = [DateTime]::Parse($row.($CONFIG.DIP_COL_DECORRENZA).ToString())
                    $decorrenza = $dateDecorrenza.ToString($CONFIG.DATE_FORMAT)
                } catch {
                    $decorrenza = $row.($CONFIG.DIP_COL_DECORRENZA).ToString().Trim()
                }
            }
        }
        
        $scadenza = ""
        if ($row.($CONFIG.DIP_COL_SCADENZA)) {
            if ($row.($CONFIG.DIP_COL_SCADENZA) -is [DateTime]) {
                $scadenza = $row.($CONFIG.DIP_COL_SCADENZA).ToString($CONFIG.DATE_FORMAT)
            } else {
                # Se è già una stringa, prova a parsarla
                try {
                    $dateScadenza = [DateTime]::Parse($row.($CONFIG.DIP_COL_SCADENZA).ToString())
                    $scadenza = $dateScadenza.ToString($CONFIG.DATE_FORMAT)
                } catch {
                    $scadenza = $row.($CONFIG.DIP_COL_SCADENZA).ToString().Trim()
                }
            }
        }
        
        # Genera il Codice Scheda: "SCH_" + Matricola
        $codiceScheda = "$($CONFIG.CODICE_SCHEDA_PREFIX)$matricola"
        
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
        if ($tipoScheda -and $CONFIG.TEMPLATE_MAPPING.ContainsKey($tipoScheda)) {
            $codiceTemplate = $CONFIG.TEMPLATE_MAPPING[$tipoScheda]
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
            $CONFIG.OUT_COL_CONTESTO = $CONFIG.DEFAULT_CONTESTO
            $CONFIG.OUT_COL_CODICE_SCHEDA = $codiceScheda
            $CONFIG.OUT_COL_NOME_SCHEDA = $nomeScheda
            $CONFIG.OUT_COL_MATR_VALUTATO = $matricola
            $CONFIG.OUT_COL_MATR_VALUTATORE = $matricolaValutatore
            $CONFIG.OUT_COL_CODICE_UOC = $codiceUOC
            $CONFIG.OUT_COL_CODICE_TEMPLATE = $codiceTemplate
            $CONFIG.OUT_COL_DATA_INIZIO = $decorrenza
            $CONFIG.OUT_COL_DATA_FINE = $scadenza
            $CONFIG.OUT_COL_STATO = $CONFIG.DEFAULT_STATO
            $CONFIG.OUT_COL_DESCRIZIONE = $CONFIG.DEFAULT_DESCRIZIONE
        }
    }
    
    Write-Host "Totale schede da scrivere: $($outputData.Count)" -ForegroundColor Green
    
    # Esporta i dati nel file di destinazione (sheet "SCHEDE")
    Write-Host "Scrittura dati nel file $targetFile (sheet '$($CONFIG.TARGET_SHEET)')..." -ForegroundColor Cyan
    
    $outputData | Export-Excel -Path $targetFile -WorksheetName $CONFIG.TARGET_SHEET -AutoSize -TableName "Schede" -ClearSheet
    
    Write-Host "COMPLETATO! File $targetFile generato con successo." -ForegroundColor Green
    Write-Host "Totale schede scritte: $($outputData.Count)" -ForegroundColor Green
    
} catch {
    Write-Host "ERRORE durante l'elaborazione: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host $_.Exception.StackTrace -ForegroundColor Red
    exit 1
}
