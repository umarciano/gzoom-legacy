<#
.SYNOPSIS
    Genera Template_Dipendenti_AORN.xlsx a partire da DIPENDENTI_2025_BASE_GOP_LAVORATO.xlsx.

.DESCRIPTION
    File di input : <script_dir>\templates\DIPENDENTI_2025_BASE_GOP_LAVORATO.xlsx
                    Sheet: "Lista dipendenti"
    File di output: <script_dir>\templates\Template_Dipendenti_AORN.xlsx
                    Sheet: "Template Dipendenti AORN"

    Mapping colonne input -> output (case-insensitive):
      MATRICOLA             -> Matricola
      Cognome               -> Cognome
      Nome                  -> Nome
      CF                    -> Codice Fiscale
      DESC QUALIFICA        -> Descrizione INCARICHI ECONOMICI
      CdC_new               -> Codice UOC
      Periodo completo      -> Decorrenza
      Periodo completo FINE -> Scadenza
      Mail                  -> Email  (con logica cessato, vedi sotto)
      Mt. valutatore        -> Matricola Referente Valutatore

    Valori di default:
      Codifica              -> 'd'
      Descrizione ESCLUSIVO -> 'RAPPORTO ESCLUSIVO'
      Username              -> parte della mail prima del '@'

    Logica Mail/Email:
      - Se Mail contiene 'cessato' (case-insensitive):
            email = nome.cognome@aocardarelli.it  (costruito da Nome e Cognome normalizzati)
      - Altrimenti:
            email = valore della colonna Mail as-is
      Username = email senza dominio (parte prima di '@').

.PARAMETER InputFile
    (Opzionale) Override del file di input.

.PARAMETER OutputFile
    (Opzionale) Override del file di output.

.EXAMPLE
    .\script_generazione_template_dipendenti.ps1
#>

param(
    [Parameter(Mandatory = $false)]
    [string]$InputFile  = "",

    [Parameter(Mandatory = $false)]
    [string]$OutputFile = ""
)

Set-StrictMode -Off
$ErrorActionPreference = 'Stop'

# ---------------------------------------------------------------------------
# Verifica modulo ImportExcel
# ---------------------------------------------------------------------------
if (-not (Get-Module -ListAvailable -Name ImportExcel)) {
    Write-Host "Modulo ImportExcel non trovato. Installazione in corso..."
    Install-Module -Name ImportExcel -Scope CurrentUser -Force -AllowClobber
}
Import-Module ImportExcel -ErrorAction Stop

# ---------------------------------------------------------------------------
# Configurazione
# ---------------------------------------------------------------------------
$EMAIL_DOMAIN = 'aocardarelli.it'
$INPUT_SHEET  = 'Lista dipendenti'
$OUTPUT_SHEET = 'Template Dipendenti AORN'

$ScriptDir   = Split-Path -Parent $MyInvocation.MyCommand.Path
$TemplateDir = Join-Path $ScriptDir "templates"
if (-not (Test-Path $TemplateDir)) {
    New-Item -ItemType Directory -Path $TemplateDir -Force | Out-Null
}

if ($InputFile -and $InputFile.Trim() -ne '') {
    $InputPath = $InputFile.Trim()
} else {
    $InputPath = Join-Path $TemplateDir "DIPENDENTI_2025_BASE_GOP_LAVORATO.xlsx"
}

if ($OutputFile -and $OutputFile.Trim() -ne '') {
    $OutputPath = $OutputFile.Trim()
} else {
    $OutputPath = Join-Path $TemplateDir "Template_Dipendenti_AORN.xlsx"
}

if (-not (Test-Path $InputPath)) {
    Write-Error "Errore: file di input '$InputPath' non trovato."
    exit 1
}

Write-Host "Input : $InputPath  [sheet: $INPUT_SHEET]"
Write-Host "Output: $OutputPath [sheet: $OUTPUT_SHEET]"

# ---------------------------------------------------------------------------
# Helper: Clean-Value
# ---------------------------------------------------------------------------
function Clean-Value {
    param([object]$v)
    if ($null -eq $v) { return '' }
    $s = "$v".Trim()
    if ($s -eq '' -or $s -eq 'NaN' -or $s -eq 'System.DBNull') { return '' }
    if ($s -match '^\d+\.0$') {
        try { return ([long]([double]$s)).ToString() } catch { }
    }
    return $s
}

# ---------------------------------------------------------------------------
# Helper: Get-Field (case-insensitive, primo candidato trovato non vuoto)
# ---------------------------------------------------------------------------
function Get-Field {
    param(
        [object]   $row,
        [string[]] $colNames,
        [string[]] $candidates
    )
    $lowerMap = @{}
    foreach ($c in $colNames) {
        if ($null -ne $c) { $lowerMap[$c.ToLower()] = $c }
    }
    foreach ($cand in $candidates) {
        $realName = $lowerMap[$cand.ToLower()]
        if ($realName) {
            $val = $row.$realName
            $cv  = Clean-Value $val
            if ($cv -ne '') { return $cv }
        }
    }
    return ''
}

# ---------------------------------------------------------------------------
# Helper: Get-ColName (nome reale della colonna, case-insensitive)
# ---------------------------------------------------------------------------
function Get-ColName {
    param([string[]]$colNames, [string[]]$candidates)
    $lowerMap = @{}
    foreach ($c in $colNames) {
        if ($null -ne $c) { $lowerMap[$c.ToLower()] = $c }
    }
    foreach ($cand in $candidates) {
        $realName = $lowerMap[$cand.ToLower()]
        if ($realName) { return $realName }
    }
    return $null
}

# ---------------------------------------------------------------------------
# Helper: Format-Date
# ---------------------------------------------------------------------------
function Format-Date {
    param([object]$v)
    if ($null -eq $v) { return '' }
    $s = "$v".Trim()
    if ($s -eq '' -or $s -eq 'NaN') { return '' }
    if ($v -is [datetime]) { return $v.ToString('dd/MM/yyyy') }
    $dt = [datetime]::MinValue
    if ([datetime]::TryParse($s, [ref]$dt)) { return $dt.ToString('dd/MM/yyyy') }
    return $s
}

# ---------------------------------------------------------------------------
# Helper: Username-Part (normalizza: minuscolo, senza accenti, solo a-z0-9)
# ---------------------------------------------------------------------------
function Username-Part {
    param([string]$s)
    if (-not $s) { return '' }
    $s = $s.Trim().ToLower()
    $normalized = $s.Normalize([System.Text.NormalizationForm]::FormKD)
    $sb = [System.Text.StringBuilder]::new()
    foreach ($c in $normalized.ToCharArray()) {
        $cat = [System.Globalization.CharUnicodeInfo]::GetUnicodeCategory($c)
        if ($cat -ne [System.Globalization.UnicodeCategory]::NonSpacingMark) {
            [void]$sb.Append($c)
        }
    }
    $s = $sb.ToString()
    $s = $s -replace '\s+', ''
    $s = $s -replace '[^a-z0-9]', ''
    return $s
}

# ---------------------------------------------------------------------------
# Helper: Sanitize-ForExcel
# ---------------------------------------------------------------------------
function Sanitize-ForExcel {
    param([string]$s)
    if (-not $s) { return '' }
    $s = $s.TrimStart()
    $s = [regex]::Replace($s, '^[=\-]+', '')
    return $s
}

# ---------------------------------------------------------------------------
# Lettura Excel di input
# Il foglio ha intestazioni duplicate (Scheda / SCHEDA), per cui usiamo
# -HeaderName con nomi univoci e -StartRow 2 per saltare la riga header.
# ---------------------------------------------------------------------------
$headerNames = @(
    'MATRICOLA','DIPENDENTE','Nome','Cognome','CF','DESC QUALIFICA',
    'Scheda','Num. Scheda','DATA ASSUNZIONE','DATA CESSAZIONE',
    'UNITA OPERATIVA','CdC','CdC_new','DATA INIZIO','DATA FINE',
    'Periodo completo','Periodo completo FINE','Occorrenze','NOTE',
    'Mt. valutatore','Valutatore','Dipartimento','SCHEDA_2','Mail',
    '_col25','_col26','_col27','_col28'
)

Write-Host "Lettura file di input (sheet: '$INPUT_SHEET')..."
$inputData = Import-Excel -Path $InputPath -WorksheetName $INPUT_SHEET `
    -HeaderName $headerNames -StartRow 2 -ErrorAction Stop

if (-not $inputData -or $inputData.Count -eq 0) {
    Write-Host "Nessun dato trovato nel foglio '$INPUT_SHEET'."
    exit 0
}

$colNames = $inputData[0].PSObject.Properties.Name

# ---------------------------------------------------------------------------
# Loop righe
# ---------------------------------------------------------------------------
$rows = [System.Collections.Generic.List[PSCustomObject]]::new()

foreach ($r in $inputData) {

    $matricola = Get-Field $r $colNames @('MATRICOLA', 'Matricola', 'matricola')
    if ($matricola -eq '') { continue }

    $cognome       = Get-Field $r $colNames @('Cognome', 'COGNOME', 'cognome')
    $nome          = Get-Field $r $colNames @('Nome', 'NOME', 'nome')
    $cf            = Get-Field $r $colNames @('CF', 'Codice Fiscale')
    $descQualifica = Get-Field $r $colNames @('DESC QUALIFICA', 'Desc Qualifica', 'DESC_QUALIFICA')
    $codiceUoc     = Get-Field $r $colNames @('CdC_new', 'CDC_NEW', 'cdc_new')
    $matVal        = Get-Field $r $colNames @('Mt. valutatore', 'MT. VALUTATORE', 'Mt valutatore', 'Mt_valutatore')

    $ruoloGzoom  = ''
    $tipoScheda  = ''
    $nomeUoc     = ''
    $codiceDip   = ''
    $nomeDip     = ''
    $dataNascita = ''

    $descrizioneEsclusivo = 'RAPPORTO ESCLUSIVO'

    $colDec     = Get-ColName $colNames @('Periodo completo', 'DATA INIZIO', 'Data Inizio')
    $colSca     = Get-ColName $colNames @('Periodo completo FINE', 'DATA FINE', 'Data Fine')
    $decorrenza = if ($colDec) { Format-Date $r.$colDec } else { '' }
    $scadenza   = if ($colSca) { Format-Date $r.$colSca } else { '' }

    # ------------------------------------------------------------------
    # Email / Username
    # ------------------------------------------------------------------
    $mailRaw = Get-Field $r $colNames @('Mail', 'MAIL', 'mail', 'Email', 'EMAIL')

    if ($mailRaw -match '(?i)cessato') {
        $partNome    = Username-Part $nome
        $partCognome = Username-Part $cognome
        if ($partNome -and $partCognome) {
            $local = "$partNome.$partCognome"
        } elseif ($partNome) {
            $local = $partNome
        } elseif ($partCognome) {
            $local = $partCognome
        } else {
            $local = ''
        }
        $email = if ($local) { "$local@$EMAIL_DOMAIN" } else { '' }
    } else {
        $email = $mailRaw
    }

    $username = if ($email -match '@') { $email.Split('@')[0] } else { $email }

    $rowOut = [PSCustomObject]@{
        'Codifica'                        = Sanitize-ForExcel 'd'
        'Matricola'                       = Sanitize-ForExcel $matricola
        'Cognome'                         = Sanitize-ForExcel $cognome
        'Nome'                            = Sanitize-ForExcel $nome
        'Codice Fiscale'                  = Sanitize-ForExcel $cf
        'Descrizione INCARICHI ECONOMICI' = Sanitize-ForExcel $descQualifica
        'Ruolo GZOOM'                     = Sanitize-ForExcel $ruoloGzoom
        'Tipo Scheda'                     = Sanitize-ForExcel $tipoScheda
        'Codice UOC'                      = Sanitize-ForExcel $codiceUoc
        'Nome UOC'                        = Sanitize-ForExcel $nomeUoc
        'Codice Dipartimento'             = Sanitize-ForExcel $codiceDip
        'Nome Dipartimento'               = Sanitize-ForExcel $nomeDip
        'Descrizione ESCLUSIVO'           = Sanitize-ForExcel $descrizioneEsclusivo
        'Decorrenza'                      = Sanitize-ForExcel $decorrenza
        'Scadenza'                        = Sanitize-ForExcel $scadenza
        'Data di Nascita'                 = Sanitize-ForExcel $dataNascita
        'Email'                           = Sanitize-ForExcel $email
        'Username'                        = Sanitize-ForExcel $username
        'Matricola Referente Valutatore'  = Sanitize-ForExcel $matVal
    }

    $rows.Add($rowOut)
}

if ($rows.Count -eq 0) {
    Write-Host "Nessuna riga valida trovata."
    exit 0
}

Write-Host "Righe elaborate: $($rows.Count)"

# ---------------------------------------------------------------------------
# Scrittura Excel di output
# ---------------------------------------------------------------------------
try {
    $rows | Export-Excel -Path $OutputPath -WorksheetName $OUTPUT_SHEET -ClearSheet -AutoSize -ErrorAction Stop
    Write-Host "Generato file: $OutputPath (righe: $($rows.Count))"
} catch [System.UnauthorizedAccessException] {
    $ext      = [System.IO.Path]::GetExtension($OutputPath)
    $stem     = [System.IO.Path]::GetFileNameWithoutExtension($OutputPath)
    $dir      = [System.IO.Path]::GetDirectoryName($OutputPath)
    $fallback = Join-Path $dir ($stem + '_generated' + $ext)
    try {
        $rows | Export-Excel -Path $fallback -WorksheetName $OUTPUT_SHEET -ClearSheet -AutoSize -ErrorAction Stop
        Write-Host "File originale bloccato. Generato file alternativo: $fallback (righe: $($rows.Count))"
    } catch {
        Write-Error "Errore scrittura file di output: $_"
        exit 1
    }
} catch {
    Write-Error "Errore inatteso: $_"
    exit 1
}