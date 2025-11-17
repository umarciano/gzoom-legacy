# Script per la compilazione automatica di IMPORT_DIPARTIMENTO_E_UOC.xls
# Legge i dati dal file Template_Dipendenti_AORN.xlsx (sheet Legenda, colonne F-K)
# e popola il file IMPORT_DIPARTIMENTO_E_UOC.xls con la gerarchia Dipartimento/UOC

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
$targetFile = Join-Path $templateDir "IMPORT_DIPARTIMENTO_E_UOC.xlsx"

Write-Host "Inizio elaborazione..." -ForegroundColor Cyan

# Verifica esistenza file sorgente
if (-not (Test-Path $sourceFile)) {
    Write-Host "ERRORE: File sorgente $sourceFile non trovato!" -ForegroundColor Red
    exit 1
}

# Leggi i dati dal foglio "Legenda" del file Template_Dipendenti_AORN.xlsx
# La tabella è nelle colonne F-K (colonne 6-11 in indice 0-based: F=6, G=7, H=8, I=9, J=10, K=11)
Write-Host "Lettura dati dal file $sourceFile (sheet Legenda)..." -ForegroundColor Cyan

try {
    # Importa tutto il foglio Legenda
    $legendaData = Import-Excel -Path $sourceFile -WorksheetName "Legenda"
    
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
        if ($null -eq $row.'Codice Dipartimento' -or [string]::IsNullOrWhiteSpace($row.'Codice Dipartimento')) {
            continue
        }
        
        $codiceDip = if ($row.'Codice Dipartimento') { $row.'Codice Dipartimento'.ToString().Trim() } else { "" }
        $nomeDip = if ($row.'Dipartimento') { $row.'Dipartimento'.ToString().Trim() } else { "" }
        $respDip = if ($row.'Matricola Responsabile Dipartimento') { $row.'Matricola Responsabile Dipartimento'.ToString().Trim() } else { "" }
        $codiceUoc = if ($row.'Codice UOC') { $row.'Codice UOC'.ToString().Trim() } else { "" }
        $nomeUoc = if ($row.'UOC') { $row.'UOC'.ToString().Trim() } else { "" }
        $respUoc = if ($row.'Matricola Responsabile UOC') { $row.'Matricola Responsabile UOC'.ToString().Trim() } else { "" }
        
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
    $referenceDate = "01/01/2025"
    
    # Per ogni dipartimento, crea prima la riga del dipartimento e poi le righe delle UOC
    foreach ($dipKey in ($dipartimentiHash.Keys | Sort-Object)) {
        $dip = $dipartimentiHash[$dipKey]
        
        # Aggiungi la riga del Dipartimento
        $outputData += [PSCustomObject]@{
            'UOC Code' = $dip.Codice
            'Description' = $dip.Nome
            'Unit Type' = 'ORG'
            'Parent UOC Code' = 'ORGUNIT001'
            'Parent Unit Type' = 'ORG'
            'Responsible Code' = $dip.Responsabile
            'Reference Date' = $referenceDate
            'End Date' = ''
        }
        
        # Aggiungi le righe delle UOC di questo dipartimento
        foreach ($uoc in $dip.UOCs) {
            $outputData += [PSCustomObject]@{
                'UOC Code' = $uoc.Codice
                'Description' = $uoc.Nome
                'Unit Type' = 'ORGANIZATION_UNIT'
                'Parent UOC Code' = $dip.Codice
                'Parent Unit Type' = 'ORG'
                'Responsible Code' = $uoc.Responsabile
                'Reference Date' = $referenceDate
                'End Date' = ''
            }
        }
    }
    
    Write-Host "Totale righe da scrivere: $($outputData.Count)" -ForegroundColor Green
    
    # Esporta i dati nel file di destinazione
    Write-Host "Scrittura dati nel file $targetFile..." -ForegroundColor Cyan
    
    # Usa il formato xlsx invece di xls (più compatibile)
    $targetFileXlsx = $targetFile -replace '\.xls$', '.xlsx'
    
    $outputData | Export-Excel -Path $targetFileXlsx -WorksheetName "Sheet1" -AutoSize -TableName "DipartimentiUOC" -ClearSheet
    
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
