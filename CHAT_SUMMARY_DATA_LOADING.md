# Chat Summary: Caricamento Dati UOC in GZOOM Legacy

**Data:** 3 Luglio 2025  
**Obiettivo:** Identificare e utilizzare i target Ant per caricare i dati UOC nel database GZOOM Legacy

## Contesto del Progetto

Il progetto GZOOM Legacy è un'applicazione OFBiz (Apache Open For Business) che gestisce dati organizzativi per strutture sanitarie, con particolare focus su UOC (Unità Operative Complesse).

### Struttura del Progetto
```
gzoom-legacy/
├── build.xml (file Ant principale)
├── hot-deploy/
│   ├── partyext/
│   │   ├── data/
│   │   │   ├── DemoUsersData.xml
│   │   │   ├── DipendenteUocSecurityData.xml
│   │   │   ├── CardarelliOrganigrammaData.xml
│   │   │   └── altri file...
│   │   └── ofbiz-component.xml
│   └── altri componenti...
└── framework/
```

## Analisi Effettuata

### 1. Identificazione dei Target Ant

Nel file `build.xml` sono stati identificati i seguenti target per il caricamento dati:

- **`run-install-custom-seed`**: Carica dati con reader `gplus`
- **`run-install-custom-seed-initial`**: Carica dati con reader `gplus` + `gplus-initial`

### 2. Configurazione del Data Reader

Nel file `hot-deploy/partyext/ofbiz-component.xml`:

```xml
<!-- Dati principali UOC (reader="gplus") -->
<data-file type="seed" reader-name="gplus" location="data/DemoUsersData.xml"/>
<data-file type="seed" reader-name="gplus" location="data/DipendenteUocSecurityData.xml"/>
<data-file type="seed" reader-name="gplus" location="data/CardarelliOrganigrammaData.xml"/>

<!-- Dati iniziali (reader="gplus-initial") -->
<data-file type="seed-initial" reader-name="gplus-initial" location="data/PartyExtInitData.xml"/>
<data-file type="seed-initial" reader-name="gplus-initial" location="data/RoleTypeInitData.xml"/>
<!-- altri file... -->
```

## Esecuzione del Caricamento

### Comando Utilizzato
```bash
.\ant run-install-custom-seed-initial
```

### Risultati
- ✅ **11.854 record caricati con successo**
- ❌ **2 errori identificati**

## Errori Riscontrati

### Errore 1: Foreign Key Constraint
**File:** `DemoUsersData.xml`  
**Problema:** 
```
Key (parent_role_type_id, party_id)=(EMPLOYEE, DIRIG001) is not present in table "party_parent_role"
```
**Causa:** Il ruolo padre `EMPLOYEE` per il party `DIRIG001` non esiste nella tabella `party_parent_role`

### Errore 2: Campo Troppo Lungo
**File:** `CardarelliOrganigrammaData.xml`  
**Problema:**
```
ERROR: value too long for type character varying(20)
roleTypeId=INTERNAL_ORGANIZATION (21 caratteri)
```
**Causa:** Il campo `role_type_id` in PostgreSQL è limitato a 20 caratteri

## Correzioni Necessarie

### Per DemoUsersData.xml
1. Aggiungere il record `PartyParentRole` mancante prima della creazione del `PartyRole`
2. Verificare la sequenza di caricamento dei dati

### Per CardarelliOrganigrammaData.xml
1. Ridurre `INTERNAL_ORGANIZATION` a max 20 caratteri, es: `INTERNAL_ORG`
2. Aggiornare tutti i riferimenti nei file XML

## File di Dati UOC Identificati

I seguenti file contengono dati specifici UOC:

1. **DemoUsersData.xml** - Utenti demo per UOC
2. **DipendenteUocSecurityData.xml** - Permessi di sicurezza dipendenti UOC  
3. **CardarelliOrganigrammaData.xml** - Organigramma Ospedale Cardarelli
4. **PartyExtData.xml** - Dati estesi party
5. **Content.xml** - Contenuti specifici
6. **ContentAssoc.xml** - Associazioni contenuti
7. **EnumerationData.xml** - Enumerazioni specifiche

## Target Ant Funzionanti

- ✅ `run-install-custom-seed-initial` - **RACCOMANDATO** (carica sia gplus che gplus-initial)
- ✅ `run-install-custom-seed` - Carica solo reader gplus
- ✅ `run-install-seed` - Carica tutti i seed data standard
- ✅ `run-install` - Caricamento completo (seed + demo + ext)

## Prossimi Passi

1. **Correggere gli errori** nei file XML identificati
2. **Testare nuovamente** il caricamento con `run-install-custom-seed-initial`
3. **Verificare l'integrità** dei dati caricati nel database
4. **Documentare** il processo di caricamento per il team

## Note Tecniche

- **Database:** PostgreSQL
- **Framework:** Apache OFBiz
- **Build Tool:** Apache Ant
- **Ambiente:** Windows con PowerShell
- **Timeout Transazioni:** 2 ore (7200 secondi)

## Strutture Dati Principali

- **Party** - Entità principali (persone, organizzazioni)
- **PartyRole** - Ruoli delle entità
- **PartyGroup** - Gruppi/organizzazioni
- **UserLogin** - Login utenti
- **SecurityGroup** - Gruppi di sicurezza
- **Content** - Contenuti sistema
- **Enumeration** - Valori enumerati

---

**Ultimo aggiornamento:** 3 Luglio 2025  
**Status:** Processo identificato e testato, correzioni in corso
