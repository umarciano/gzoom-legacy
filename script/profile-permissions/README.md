# Permesso ADMINISTRATOR_VIEW

Script SQL per gestire il permesso che rende gli amministratori FULLADMIN in modalità **read-only** sulle valutazioni.

**Eccezione**: Gli amministratori **POSSONO modificare** la Performance Strategica (CTX_BS).

## File

### `ROLLBACK_ADMINISTRATOR_VIEW.sql`
Rimuove il permesso dal database.  
**Risultato**: Gli admin POSSONO modificare tutte le valutazioni.

### `DEPLOY_ADMINISTRATOR_VIEW.sql`
Aggiunge il permesso al database.  
**Risultato**: 
- Gli admin NON POSSONO modificare Performance Individuale e altre valutazioni (solo visualizzare)
- Gli admin **POSSONO modificare** Performance Strategica (CTX_BS)

### `TEST_PROCEDURE_ADMINISTRATOR_VIEW.md`
Guida passo-passo per testare gli script.

## Utilizzo

```bash
# Rimuovere il permesso (admin può modificare tutto)
psql -U [user] -d [database] -f ROLLBACK_ADMINISTRATOR_VIEW.sql

# Applicare il permesso (admin read-only tranne CTX_BS)
psql -U [user] -d [database] -f DEPLOY_ADMINISTRATOR_VIEW.sql
```

**Importante**: Fare logout/login dopo ogni script per ricaricare i permessi.

## Matrice Comportamenti

| Tipo Valutazione | Senza Permesso | Con Permesso ADMINISTRATOR_VIEW |
|------------------|----------------|----------------------------------|
| Performance Strategica (CTX_BS) | ✅ Può modificare | ✅ **Può modificare** |
| Performance Individuale (CTX_EP) | ✅ Può modificare | ❌ Solo lettura |
| Altre valutazioni | ✅ Può modificare | ❌ Solo lettura |

## Note

- La modifica riguarda tutti gli utenti del gruppo `FULLADMIN`
- Gli script sono completamente reversibili
- Non viene cancellato alcun dato
- L'eccezione per CTX_BS è implementata a livello di codice Groovy

