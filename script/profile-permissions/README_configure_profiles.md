# Configurazione Profili di Sicurezza AORN

Script per configurare automaticamente i profili di sicurezza nel database PostgreSQL dell'applicazione GZOOM.

## Profili Configurati

1. **AORNADMIN** - Amministratore di Sistema AORN
   - 95 permessi amministrativi
   - 41 esclusioni menu
   - Accesso completo al sistema

2. **EMPLPERF_VALUTATO** - Dipendente Valutato
   - 8 permessi per la visualizzazione della propria scheda di valutazione
   - Permessi di base per contenuti e work effort

3. **EMPLPERF_VALUTATORE** - Valutatore
   - 9 permessi per la gestione delle valutazioni
   - Include tutti i permessi del profilo VALUTATO
   - Permessi aggiuntivi per creazione e cancellazione dati contabili

## File Disponibili

### Script di Configurazione

- `configure_profiles_aorn.sh` - Script per Linux/Unix/Mac
- `configure_profiles_aorn.ps1` - Script per Windows (PowerShell)

### File SQL

- `setup_aornadmin_profile.sql` - Configurazione profilo AORNADMIN
- `setup_valutato_profile.sql` - Configurazione profilo EMPLPERF_VALUTATO
- `setup_valutatore_profile.sql` - Configurazione profilo EMPLPERF_VALUTATORE

## Prerequisiti

### Linux/Unix/Mac

- **PostgreSQL Client** (`psql`) installato e disponibile nel PATH
- Accesso al database PostgreSQL con credenziali valide
- Permessi di esecuzione sullo script: `chmod +x configure_profiles_aorn.sh`

### Windows

- **PostgreSQL Client** (`psql.exe`) installato e disponibile nel PATH
  - Esempio PATH: `C:\Program Files\PostgreSQL\15\bin`
- PowerShell (già presente in Windows)
- Accesso al database PostgreSQL con credenziali valide

## Uso

### Linux/Unix/Mac

#### Uso Base (con valori di default)

```bash
./configure_profiles_aorn.sh
```

Valori di default:
- Host: `localhost`
- Porta: `5432`
- Database: `ofbiz`
- Utente: `ofbiz`

#### Uso con Parametri Personalizzati

```bash
./configure_profiles_aorn.sh <DB_HOST> <DB_PORT> <DB_NAME> <DB_USER>
```

Esempi:

```bash
# Connessione a database locale con nome diverso
./configure_profiles_aorn.sh localhost 5432 gzoom postgres

# Connessione a database remoto
./configure_profiles_aorn.sh 192.168.1.100 5432 ofbiz dbadmin
```

#### Password

La password può essere fornita in due modi:

1. **Interattivamente** (raccomandato):
   ```bash
   ./configure_profiles_aorn.sh
   # Lo script richiederà la password durante l'esecuzione
   ```

2. **Variabile d'ambiente**:
   ```bash
   export PGPASSWORD='your_password'
   ./configure_profiles_aorn.sh
   ```

### Windows (PowerShell)

#### Uso Base (con valori di default)

```powershell
.\configure_profiles_aorn.ps1
```

Valori di default:
- Host: `localhost`
- Porta: `5432`
- Database: `ofbiz`
- Utente: `ofbiz`

#### Uso con Parametri Personalizzati

```powershell
.\configure_profiles_aorn.ps1 -DbHost <host> -DbPort <port> -DbName <name> -DbUser <user> [-DbPassword <password>]
```

Esempi:

```powershell
# Connessione a database locale con nome diverso
.\configure_profiles_aorn.ps1 -DbHost "localhost" -DbName "gzoom" -DbUser "postgres"

# Connessione a database remoto
.\configure_profiles_aorn.ps1 -DbHost "192.168.1.100" -DbPort 5433 -DbName "ofbiz" -DbUser "admin"

# Con password specificata (NON raccomandato per sicurezza)
.\configure_profiles_aorn.ps1 -DbHost "localhost" -DbName "ofbiz" -DbUser "ofbiz" -DbPassword "MyPassword123"
```

#### Password

La password può essere fornita in due modi:

1. **Interattivamente** (raccomandato):
   ```powershell
   .\configure_profiles_aorn.ps1
   # Lo script richiederà la password in modo sicuro
   ```

2. **Parametro** (sconsigliato per sicurezza):
   ```powershell
   .\configure_profiles_aorn.ps1 -DbPassword "your_password"
   ```

## Output e Logging

### Console

Lo script mostra:
- Parametri di connessione utilizzati
- Avanzamento dell'esecuzione di ogni file SQL
- Messaggi di successo/errore colorati
- Riepilogo finale con conteggio successi/errori

### File di Log

Viene creato automaticamente un file di log con timestamp:
- **Formato**: `configure_profiles_aorn_YYYYMMDD_HHMMSS.log`
- **Contenuto**: 
  - Timestamp per ogni operazione
  - Parametri di connessione
  - Output completo di ogni esecuzione SQL
  - Messaggi di errore dettagliati
  - Riepilogo finale

Esempio nome file: `configure_profiles_aorn_20251124_143025.log`

## Gestione Errori

### Errori Comuni

1. **psql non trovato**
   - **Linux**: Installare PostgreSQL client: `sudo apt-get install postgresql-client`
   - **Windows**: Aggiungere la directory bin di PostgreSQL al PATH

2. **Connessione rifiutata**
   - Verificare che PostgreSQL sia in esecuzione
   - Controllare host e porta
   - Verificare configurazione firewall

3. **Autenticazione fallita**
   - Verificare username e password
   - Controllare file `pg_hba.conf` per le policy di autenticazione

4. **Permessi insufficienti**
   - L'utente deve avere permessi di INSERT/UPDATE sulle tabelle:
     - `party`, `person`, `user_login`, `user_login_security_group`
     - `security_group`, `security_permission`, `security_group_permission`
     - `security_group_content`

### Comportamento in Caso di Errori

- Lo script continua l'esecuzione anche se un file SQL fallisce
- Ogni errore viene registrato nel log
- Il riepilogo finale mostra il numero di successi ed errori
- Exit code:
  - `0` = Tutti i file eseguiti con successo
  - `1` = Almeno un file ha generato errori

## Idempotenza

Gli script SQL sono **idempotenti**:
- Possono essere eseguiti multiple volte senza causare errori
- Utilizzano `ON CONFLICT DO NOTHING` per le INSERT
- Le configurazioni esistenti non vengono duplicate

Questo permette di:
- Re-eseguire lo script in caso di errori parziali
- Aggiornare configurazioni esistenti
- Aggiungere nuovi record senza problemi

## Verifica Configurazione

Dopo l'esecuzione, verificare che i profili siano stati creati correttamente:

```sql
-- Verifica security groups
SELECT group_id, description 
FROM security_group 
WHERE group_id IN ('AORNADMIN', 'EMPLPERF_VALUTATO', 'EMPLPERF_VALUTATORE');

-- Verifica permessi AORNADMIN (deve restituire 95)
SELECT COUNT(*) 
FROM security_group_permission 
WHERE group_id = 'AORNADMIN';

-- Verifica permessi VALUTATO (deve restituire 8)
SELECT COUNT(*) 
FROM security_group_permission 
WHERE group_id = 'EMPLPERF_VALUTATO';

-- Verifica permessi VALUTATORE (deve restituire 9)
SELECT COUNT(*) 
FROM security_group_permission 
WHERE group_id = 'EMPLPERF_VALUTATORE';

-- Verifica utente admin
SELECT user_login_id, enabled, party_id 
FROM user_login 
WHERE user_login_id = 'admin';

-- Verifica associazione admin a AORNADMIN
SELECT user_login_id, group_id 
FROM user_login_security_group 
WHERE user_login_id = 'admin' AND group_id = 'AORNADMIN';
```

## Note di Sicurezza

1. **Password**: Non includere mai password in chiaro negli script o nei comandi della cronologia shell
2. **Utente Admin**: Dopo il primo accesso, cambiare immediatamente la password dell'utente `admin` (default: "ofbiz")
3. **Backup**: Eseguire sempre un backup del database prima di eseguire script di configurazione
4. **Permessi**: Gli script SQL richiedono privilegi elevati - eseguire solo in ambiente controllato
5. **Log**: I file di log potrebbero contenere informazioni sensibili - proteggerli adeguatamente

## Troubleshooting

### Linux: Errore "Permission denied"

```bash
chmod +x configure_profiles_aorn.sh
```

### Windows: Errore "Impossibile eseguire script"

Abilitare l'esecuzione di script PowerShell (da PowerShell con privilegi amministrativi):

```powershell
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser
```

### Connessione PostgreSQL Fallisce

Verificare la connessione manualmente:

```bash
# Linux/Mac
psql -h localhost -p 5432 -d ofbiz -U ofbiz

# Windows
psql.exe -h localhost -p 5432 -d ofbiz -U ofbiz
```

## Supporto

Per problemi o domande:
1. Controllare il file di log generato
2. Verificare i prerequisiti e la connessione al database
3. Consultare la documentazione PostgreSQL per errori specifici

## Licenza

Questo script fa parte del progetto GZOOM per AORN Cardarelli.
