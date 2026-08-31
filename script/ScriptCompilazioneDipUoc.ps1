# Script per la compilazione automatica di IMPORT_DIPARTIMENTO_E_UOC.xls
# Legge i dati dal file Template_Dipendenti_AORN.xlsx (sheet Legenda, colonne F-K)
# e popola il file IMPORT_DIPARTIMENTO_E_UOC.xls con la gerarchia Dipartimento/UOC

# ============================================================================
# CONFIGURAZIONE - NOMI FILE E CARTELLE
# ============================================================================
$CONFIG = @{
    # Cartelle
    TEMPLATE_DIR = "templates"
    
    # File sorgente
    SOURCE_FILE = "Template_Dipendenti_AORN.xlsx"
    SOURCE_SHEET = "Legenda"
    
    # File destinazione
    TARGET_FILE = "IMPORT_DIPARTIMENTO_E_UOC.xlsx"
    TARGET_SHEET = "Sheet1"
    
    # Colonne file sorgente (Legenda)
    COL_CODICE_DIP = "Codice Dipartimento"
    COL_NOME_DIP = "Dipartimento"
    COL_RESP_DIP = "Matricola Responsabile Dipartimento"
    COL_CODICE_UOC = "Codice UOC"
    COL_NOME_UOC = "UOC"
    COL_RESP_UOC = "Matricola Responsabile UOC"
    
    # Colonne file destinazione
    OUT_COL_UOC_CODE = "UOC Code"
    OUT_COL_DESCRIPTION = "Description"
    OUT_COL_UNIT_TYPE = "Unit Type"
    OUT_COL_PARENT_UOC_CODE = "Parent UOC Code"
    OUT_COL_PARENT_UNIT_TYPE = "Parent Unit Type"
    OUT_COL_RESPONSIBLE_CODE = "Responsible Code"
    OUT_COL_REFERENCE_DATE = "Reference Date"
    OUT_COL_END_DATE = "End Date"
    
    # Valori costanti
    UNIT_TYPE_ORG = "ORG"
    UNIT_TYPE_ORG_UNIT = "ORGANIZATION_UNIT"
    PARENT_ROOT = "ORGUNIT001"
    DEFAULT_REF_DATE = "01/01/2025"
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

# Leggi i dati dal foglio "Legenda" del file Template_Dipendenti_AORN.xlsx
# La tabella è nelle colonne F-K (colonne 6-11 in indice 0-based: F=6, G=7, H=8, I=9, J=10, K=11)
Write-Host "Lettura dati dal file $sourceFile (sheet $($CONFIG.SOURCE_SHEET))..." -ForegroundColor Cyan

try {
    # Importa tutto il foglio Legenda
    $legendaData = Import-Excel -Path $sourceFile -WorksheetName $CONFIG.SOURCE_SHEET
    
    # Estrai le colonne dalla F alla K (i nomi delle colonne potrebbero variare)
    # Assumiamo che le colonne abbiano questi nomi basati sulla tua descrizione:
    # F: Codice Dipartimento
    # G: Dipartimento
    # H: Matricola Responsabile Dipartimento
    # I: Codice UOC
    # J: UOC
    # K: Matricola Responsabile UOC
    
    # Crea una hashtable per raggruppare le UOC per Dipartimento
    $dipartimentiHash = @{}
    
    foreach ($row in $legendaData) {
        # Salta righe vuote
        if ($null -eq $row.($CONFIG.COL_CODICE_DIP) -or [string]::IsNullOrWhiteSpace($row.($CONFIG.COL_CODICE_DIP))) {
            continue
        }
        
        # Estrai i dati usando Clean-Value per rimuovere i ".0" dalle matricole
        $codiceDip = Clean-Value $row.($CONFIG.COL_CODICE_DIP)
        $nomeDip = Clean-Value $row.($CONFIG.COL_NOME_DIP)
        $respDip = Clean-Value $row.($CONFIG.COL_RESP_DIP)
        $codiceUoc = Clean-Value $row.($CONFIG.COL_CODICE_UOC)
        $nomeUoc = Clean-Value $row.($CONFIG.COL_NOME_UOC)
        $respUoc = Clean-Value $row.($CONFIG.COL_RESP_UOC)
        
        # Se il dipartimento non esiste, crealo
        if (-not $dipartimentiHash.ContainsKey($codiceDip)) {
            $dipartimentiHash[$codiceDip] = @{
                'Codice' = $codiceDip
                'Nome' = $nomeDip
                'Responsabile' = $respDip
                'UOCs' = @()
            }
        }
        
        # Aggiungi la UOC al dipartimento (solo se non è vuota)
        if (-not [string]::IsNullOrWhiteSpace($codiceUoc)) {
            $dipartimentiHash[$codiceDip].UOCs += @{
                'Codice' = $codiceUoc
                'Nome' = $nomeUoc
                'Responsabile' = $respUoc
            }
        }
    }
    
    Write-Host "Trovati $($dipartimentiHash.Count) dipartimenti." -ForegroundColor Green
    
    # Crea la struttura dati per l'export
    $outputData = @()
    $referenceDate = $CONFIG.DEFAULT_REF_DATE
    
    # Per ogni dipartimento, crea prima la riga del dipartimento e poi le righe delle UOC
    foreach ($dipKey in ($dipartimentiHash.Keys | Sort-Object)) {
        $dip = $dipartimentiHash[$dipKey]
        
        # Aggiungi la riga del Dipartimento
        $outputData += [PSCustomObject]@{
            $CONFIG.OUT_COL_UOC_CODE = $dip.Codice
            $CONFIG.OUT_COL_DESCRIPTION = $dip.Nome
            $CONFIG.OUT_COL_UNIT_TYPE = $CONFIG.UNIT_TYPE_ORG
            $CONFIG.OUT_COL_PARENT_UOC_CODE = $CONFIG.PARENT_ROOT
            $CONFIG.OUT_COL_PARENT_UNIT_TYPE = $CONFIG.UNIT_TYPE_ORG
            $CONFIG.OUT_COL_RESPONSIBLE_CODE = $dip.Responsabile
            $CONFIG.OUT_COL_REFERENCE_DATE = $referenceDate
            $CONFIG.OUT_COL_END_DATE = ''
        }
        
        # Aggiungi le righe delle UOC di questo dipartimento
        foreach ($uoc in $dip.UOCs) {
            $outputData += [PSCustomObject]@{
                $CONFIG.OUT_COL_UOC_CODE = $uoc.Codice
                $CONFIG.OUT_COL_DESCRIPTION = $uoc.Nome
                $CONFIG.OUT_COL_UNIT_TYPE = $CONFIG.UNIT_TYPE_ORG_UNIT
                $CONFIG.OUT_COL_PARENT_UOC_CODE = $dip.Codice
                $CONFIG.OUT_COL_PARENT_UNIT_TYPE = $CONFIG.UNIT_TYPE_ORG
                $CONFIG.OUT_COL_RESPONSIBLE_CODE = $uoc.Responsabile
                $CONFIG.OUT_COL_REFERENCE_DATE = $referenceDate
                $CONFIG.OUT_COL_END_DATE = ''
            }
        }
    }
    
    Write-Host "Totale righe da scrivere: $($outputData.Count)" -ForegroundColor Green
    
    # Esporta i dati nel file di destinazione
    Write-Host "Scrittura dati nel file $targetFile..." -ForegroundColor Cyan
    
    # Usa il formato xlsx invece di xls (più compatibile)
    $targetFileXlsx = $targetFile -replace '\.xls$', '.xlsx'
    
    $outputData | Export-Excel -Path $targetFileXlsx -WorksheetName $CONFIG.TARGET_SHEET -AutoSize -TableName "DipartimentiUOC" -ClearSheet
    
    Write-Host "COMPLETATO! File $targetFileXlsx generato con successo." -ForegroundColor Green
    Write-Host "Totale righe scritte: $($outputData.Count)" -ForegroundColor Green
    Write-Host ""
    Write-Host "NOTA: Il file è stato salvato come .xlsx invece di .xls per garantire la compatibilità." -ForegroundColor Yellow
    Write-Host "Se hai bisogno del formato .xls, puoi aprire il file .xlsx in Excel e salvarlo come .xls manualmente." -ForegroundColor Yellow
    
} catch {
    Write-Host "ERRORE durante l'elaborazione: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host $_.Exception.StackTrace -ForegroundColor Red
    exit 1
}
