# Script di Compilazione Excel - Multi-Platform

Questa cartella contiene script per la compilazione automatica dei file Excel di import per GZOOM.
Gli script sono disponibili sia per **Windows** (PowerShell) che per **Linux/macOS** (Bash/Python).

## 📋 Script Disponibili

### 1. ScriptCompilazioneDipUoc
Compila il file `IMPORT_DIPARTIMENTO_E_UOC.xlsx` leggendo i dati dalla Legenda.

### 2. ScriptCompilazioneAnagrafica
Compila il file `IMPORT_RISORSE_UMANE.xlsx` con i dati delle risorse umane.

### 3. ScriptCompilazioneSchede
Compila il file `IMPORT_SCHEDE.xlsx` con le schede di valutazione.

---

## 🪟 Windows

### Prerequisiti
- PowerShell 5.1 o superiore (già incluso in Windows 10/11)
- Il modulo `ImportExcel` verrà installato automaticamente al primo avvio

### Esecuzione
```powershell
# Dalla cartella script
.\ScriptCompilazioneDipUoc.ps1
.\ScriptCompilazioneAnagrafica.ps1
.\ScriptCompilazioneSchede.ps1
```

---

## 🐧 Linux / macOS

### Prerequisiti
1. **Python 3.7+** deve essere installato
2. **pip3** deve essere installato

Verifica installazione:
```bash
python3 --version
pip3 --version
```

### Installazione Dipendenze
La prima volta (o dopo aggiornamenti), installa le dipendenze Python:
```bash
pip3 install -r requirements.txt
```

### Dare i Permessi di Esecuzione (solo la prima volta)
```bash
chmod +x ScriptCompilazioneDipUoc.sh
chmod +x ScriptCompilazioneAnagrafica.sh
chmod +x ScriptCompilazioneSchede.sh
```

### Esecuzione
```bash
# Dalla cartella script
./ScriptCompilazioneDipUoc.sh
./ScriptCompilazioneAnagrafica.sh
./ScriptCompilazioneSchede.sh
```

---

## 📁 Struttura File

```
script/
├── template/                           # Cartella con i file Excel
│   ├── Template_Dipendenti_AORN.xlsx  # File sorgente (INPUT)
│   ├── IMPORT_DIPARTIMENTO_E_UOC.xlsx # Output script 1
│   ├── IMPORT_RISORSE_UMANE.xlsx      # Output script 2
│   └── IMPORT_SCHEDE.xlsx             # Output script 3
│
├── ScriptCompilazioneDipUoc.ps1       # Windows
├── ScriptCompilazioneDipUoc.sh        # Linux/macOS
├── script_compilazione_dip_uoc.py     # Python (chiamato da .sh)
│
├── ScriptCompilazioneAnagrafica.ps1   # Windows
├── ScriptCompilazioneAnagrafica.sh    # Linux/macOS
├── script_compilazione_anagrafica.py  # Python (chiamato da .sh)
│
├── ScriptCompilazioneSchede.ps1       # Windows
├── ScriptCompilazioneSchede.sh        # Linux/macOS
├── script_compilazione_schede.py      # Python (chiamato da .sh)
│
├── requirements.txt                    # Dipendenze Python
└── README.md                          # Questo file
```

---

## 🔧 Troubleshooting

### Windows
**Errore "impossibile caricare il file... perché l'esecuzione di script è disabilitata":**
```powershell
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser
```

### Linux/macOS
**Errore "Permission denied":**
```bash
chmod +x *.sh
```

**Errore "python3: command not found":**
Installa Python 3:
- Ubuntu/Debian: `sudo apt-get install python3 python3-pip`
- CentOS/RHEL: `sudo yum install python3 python3-pip`
- macOS: `brew install python3`

**Errore durante l'installazione delle dipendenze:**
```bash
# Prova con l'opzione --user
pip3 install --user -r requirements.txt
```

---

## 📝 Note

- Tutti gli script leggono il file sorgente `Template_Dipendenti_AORN.xlsx` dalla cartella `template/`
- I file di output vengono generati nella stessa cartella `template/`
- Gli script Windows (`.ps1`) e Linux (`.sh`) producono **esattamente** lo stesso risultato
- È possibile usare entrambe le versioni sulla stessa macchina senza conflitti

---

## 🤝 Supporto

Per problemi o domande, contattare il team di sviluppo GZOOM.
