#!/usr/bin/env python3
# -*- coding: utf-8 -*-

"""
============================================================================
SCRIPT DI VALIDAZIONE DATI - Template_Dipendenti_AORN.xlsx
============================================================================

Scopo: Validare la completezza e coerenza dei dati nel file Excel
       Template_Dipendenti_AORN.xlsx prima dell'import massivo

Verifica eseguita:
- Presenza di tutti i campi obbligatori per ogni record
- Segnalazione di campi mancanti con indicazione della matricola

Output: Report di validazione con elenco errori/warning
============================================================================
"""

import sys
import os
from pathlib import Path
import pandas as pd
from typing import List, Dict, Any

# ============================================================================
# CONFIGURAZIONE
# ============================================================================

from datetime import datetime

# Anno di riferimento per le validazioni delle date
ANNO_RIFERIMENTO = 2025

CONFIG = {
    # Cartelle
    'TEMPLATE_DIR': 'template',
    
    # File da validare
    'SOURCE_FILE': 'Template_Dipendenti_AORN.xlsx',
    'SOURCE_SHEET': 'Template Dipendenti AORN',
    
    # Anno di riferimento
    'ANNO_RIFERIMENTO': ANNO_RIFERIMENTO,
    
    # Date limiti per validazione (derivate dall'anno di riferimento)
    'DATA_MIN': datetime(ANNO_RIFERIMENTO, 1, 1),
    'DATA_MAX': datetime(ANNO_RIFERIMENTO, 12, 31),
    
    # Campi obbligatori da verificare
    'REQUIRED_FIELDS': [
        'Matricola',
        'Nome',
        'Cognome',
        'Codice Fiscale',
        'Ruolo GZOOM',
        'Codice UOC',
        'Nome UOC',
        'Matricola Referente Valutatore',
        'Email',
        'Username'
    ],
    
    # Campi opzionali (solo per info)
    'OPTIONAL_FIELDS': [
        # Eventuali campi che possono essere vuoti
    ]
}

# ============================================================================
# FUNZIONI DI UTILITÀ
# ============================================================================

class Colors:
    """Codici ANSI per colorare l'output nel terminale"""
    RED = '\033[91m'
    GREEN = '\033[92m'
    YELLOW = '\033[93m'
    BLUE = '\033[94m'
    MAGENTA = '\033[95m'
    CYAN = '\033[96m'
    WHITE = '\033[97m'
    GRAY = '\033[90m'
    RESET = '\033[0m'
    BOLD = '\033[1m'

def print_color(message: str, color: str = Colors.WHITE, end: str = '\n'):
    """Stampa un messaggio colorato"""
    print(f"{color}{message}{Colors.RESET}", end=end)

def print_header(title: str):
    """Stampa un'intestazione"""
    print()
    print_color("=" * 80, Colors.CYAN)
    print_color(title, Colors.CYAN)
    print_color("=" * 80, Colors.CYAN)
    print()

def print_section(title: str):
    """Stampa una sezione"""
    print()
    print_color("-" * 80, Colors.YELLOW)
    print_color(title, Colors.YELLOW)
    print_color("-" * 80, Colors.YELLOW)

def is_field_empty(value: Any) -> bool:
    """
    Verifica se un campo è vuoto o non valorizzato
    
    Args:
        value: Valore da verificare
        
    Returns:
        True se il campo è vuoto, False altrimenti
    """
    if value is None or pd.isna(value):
        return True
    
    if isinstance(value, str):
        str_value = value.strip()
        # Considera vuoti anche i valori "0", "-", "N/A", "NULL", etc.
        if not str_value or str_value in ["0", "0.0", "-", "N/A", "NULL", "n/a", "null"]:
            return True
    elif isinstance(value, (int, float)):
        # Se è 0 o NaN considera vuoto
        if value == 0 or pd.isna(value):
            return True
    
    return False

def get_matricola_value(row: pd.Series) -> str:
    """
    Estrae il valore della matricola da una riga
    
    Args:
        row: Riga del DataFrame
        
    Returns:
        Valore della matricola (o messaggio se non presente)
    """
    if 'Matricola' not in row or pd.isna(row['Matricola']):
        return "<NON SPECIFICATA>"
    
    matricola = str(row['Matricola']).strip()
    
    # Rimuove .0 se presente (numero intero letto come float)
    if matricola.endswith('.0'):
        matricola = matricola[:-2]
    
    if not matricola:
        return "<VUOTA>"
    
    return matricola

def install_dependencies():
    """Verifica e installa le dipendenze necessarie"""
    try:
        import pandas
        import openpyxl
    except ImportError:
        print_color("Installazione dipendenze necessarie...", Colors.YELLOW)
        import subprocess
        subprocess.check_call([sys.executable, '-m', 'pip', 'install', 'pandas', 'openpyxl'])
        print_color("Dipendenze installate con successo.", Colors.GREEN)

# ============================================================================
# SCRIPT PRINCIPALE
# ============================================================================

def main():
    """Funzione principale"""
    
    print_header("VALIDAZIONE DATI - Template_Dipendenti_AORN.xlsx")
    
    # Verifica dipendenze
    install_dependencies()
    
    # Determina il percorso del file
    script_dir = Path(__file__).parent
    template_dir = script_dir / CONFIG['TEMPLATE_DIR']
    source_file = template_dir / CONFIG['SOURCE_FILE']
    
    print_color(f"File da validare: ", Colors.CYAN, end='')
    print_color(str(source_file), Colors.WHITE)
    print_color(f"Foglio: ", Colors.CYAN, end='')
    print_color(CONFIG['SOURCE_SHEET'], Colors.WHITE)
    print()
    
    # Verifica esistenza file
    if not source_file.exists():
        print_color("ERRORE: File sorgente non trovato!", Colors.RED)
        print_color(f"Percorso: {source_file}", Colors.RED)
        return 1
    
    # Leggi i dati dal file Excel
    print_color("Lettura dati dal file Excel...", Colors.CYAN)
    
    try:
        df = pd.read_excel(source_file, sheet_name=CONFIG['SOURCE_SHEET'])
        
        if df.empty:
            print_color("ERRORE: Il foglio Excel è vuoto o non contiene dati validi!", Colors.RED)
            return 1
        
        print_color(f"Trovate {len(df)} righe nel file.", Colors.GREEN)
        
    except Exception as e:
        print_color("ERRORE durante la lettura del file Excel!", Colors.RED)
        print_color(f"Dettaglio errore: {str(e)}", Colors.RED)
        return 1
    
    # ========================================================================
    # FASE 1: VERIFICA PRESENZA COLONNE NEL FILE
    # ========================================================================
    
    print_section("FASE 1: Verifica presenza colonne nel file")
    
    available_columns = df.columns.tolist()
    
    print_color("Colonne disponibili nel file:", Colors.CYAN)
    for col in available_columns:
        print_color(f"  - {col}", Colors.GRAY)
    
    print()
    
    missing_columns = [field for field in CONFIG['REQUIRED_FIELDS'] 
                      if field not in available_columns]
    
    if missing_columns:
        print_color("ERRORE: Mancano le seguenti colonne obbligatorie nel file Excel:", Colors.RED)
        for col in missing_columns:
            print_color(f"  ✗ {col}", Colors.RED)
        print()
        print_color("Impossibile procedere con la validazione.", Colors.RED)
        return 1
    else:
        print_color("✓ Tutte le colonne obbligatorie sono presenti.", Colors.GREEN)
    
    # ========================================================================
    # FASE 2: VALIDAZIONE COMPLETEZZA DATI PER OGNI RECORD
    # ========================================================================
    
    print_section("FASE 2: Validazione completezza dati per ogni record")
    
    validation_errors = []
    processed_records = 0
    skipped_empty_rows = 0
    
    for idx, row in df.iterrows():
        row_number = idx + 2  # +2 perché Excel inizia da 1 e la prima riga è l'intestazione
        
        # Verifica se la riga è completamente vuota (tutti i campi obbligatori sono vuoti)
        all_fields_empty = all(is_field_empty(row[field]) for field in CONFIG['REQUIRED_FIELDS'])
        
        if all_fields_empty:
            # Salta le righe completamente vuote
            skipped_empty_rows += 1
            continue
        
        # La riga ha almeno un campo valorizzato, quindi la processiamo
        processed_records += 1
        matricola = get_matricola_value(row)
        missing_fields = []
        
        # Verifica ogni campo obbligatorio
        for field in CONFIG['REQUIRED_FIELDS']:
            if is_field_empty(row[field]):
                missing_fields.append(field)
        
        # Se ci sono campi mancanti, registra l'errore
        if missing_fields:
            validation_errors.append({
                'row_number': row_number,
                'matricola': matricola,
                'missing_fields': missing_fields
            })
    
    # ========================================================================
    # FASE 3: VALIDAZIONE UNICITA MATRICOLE
    # ========================================================================
    
    print_section("FASE 3: Validazione unicita matricole")
    
    # Verifica che non ci siano matricole duplicate
    matricole_count = {}
    duplicate_errors = []
    
    for idx, row in df.iterrows():
        row_number = idx + 2
        
        # Salta righe completamente vuote
        all_fields_empty = all(is_field_empty(row[field]) for field in CONFIG['REQUIRED_FIELDS'])
        if all_fields_empty:
            continue
        
        matricola = get_matricola_value(row)
        if matricola != "<NON SPECIFICATA>" and matricola != "<VUOTA>":
            matricola_clean = str(matricola).replace('.0', '').strip()
            if matricola_clean:
                if matricola_clean not in matricole_count:
                    matricole_count[matricola_clean] = []
                matricole_count[matricola_clean].append(row_number)
    
    # Trova duplicati
    for matricola, rows in matricole_count.items():
        if len(rows) > 1:
            duplicate_errors.append({
                'matricola': matricola,
                'rows': rows
            })
    
    if duplicate_errors:
        print_color(f"✗ Trovate {len(duplicate_errors)} matricole duplicate!", Colors.RED)
    else:
        print_color(f"✓ Tutte le {len(matricole_count)} matricole sono univoche.", Colors.GREEN)
    
    # ========================================================================
    # FASE 4: VALIDAZIONE REFERENTI VALUTATORI
    # ========================================================================
    
    print_section("FASE 4: Validazione referenti valutatori")
    
    # Costruisce un set di tutte le matricole valide presenti nel file
    valid_matricole = set()
    for idx, row in df.iterrows():
        # Salta righe completamente vuote
        all_fields_empty = all(is_field_empty(row[field]) for field in CONFIG['REQUIRED_FIELDS'])
        if all_fields_empty:
            continue
        
        matricola = get_matricola_value(row)
        if matricola != "<NON SPECIFICATA>" and matricola != "<VUOTA>":
            # Rimuove il .0 se presente e converte a stringa
            matricola_clean = str(matricola).replace('.0', '').strip()
            if matricola_clean:
                valid_matricole.add(matricola_clean)
    
    print_color(f"Trovate {len(valid_matricole)} matricole uniche nel file.", Colors.CYAN)
    
    # Verifica che ogni referente valutatore esista nel file
    referent_errors = []
    for idx, row in df.iterrows():
        row_number = idx + 2
        
        # Salta righe completamente vuote
        all_fields_empty = all(is_field_empty(row[field]) for field in CONFIG['REQUIRED_FIELDS'])
        if all_fields_empty:
            continue
        
        matricola_dipendente = get_matricola_value(row)
        matricola_referente = row.get('Matricola Referente Valutatore', '')
        
        # Se il referente è valorizzato, verifica che esista
        if not is_field_empty(matricola_referente):
            # Pulisce la matricola referente
            matricola_ref_clean = str(matricola_referente).replace('.0', '').strip()
            
            if matricola_ref_clean not in valid_matricole:
                referent_errors.append({
                    'row_number': row_number,
                    'matricola_dipendente': matricola_dipendente,
                    'matricola_referente': matricola_ref_clean
                })
    
    if referent_errors:
        print_color(f"✗ Trovati {len(referent_errors)} referenti non validi.", Colors.RED)
    else:
        print_color("✓ Tutti i referenti valutatori sono presenti nel file.", Colors.GREEN)
    
    # ========================================================================
    # FASE 5: VALIDAZIONE DATE DECORRENZA E SCADENZA
    # ========================================================================
    
    print_section("FASE 5: Validazione date Decorrenza e Scadenza")
    
    print_color(f"Anno di riferimento: {CONFIG['ANNO_RIFERIMENTO']}", Colors.CYAN)
    print_color(f"Periodo valido: 01/01/{CONFIG['ANNO_RIFERIMENTO']} - 31/12/{CONFIG['ANNO_RIFERIMENTO']}", Colors.CYAN)
    
    date_errors = []
    
    def parse_date(date_value):
        """Converte vari formati di data in datetime"""
        if is_field_empty(date_value):
            return None
        
        try:
            if isinstance(date_value, str):
                # Prova diversi formati di data
                for fmt in ['%d/%m/%Y', '%Y-%m-%d', '%d-%m-%Y', '%Y/%m/%d']:
                    try:
                        return datetime.strptime(date_value, fmt)
                    except ValueError:
                        continue
                return None
            elif hasattr(date_value, 'to_pydatetime'):
                # pandas Timestamp
                return date_value.to_pydatetime()
            elif hasattr(date_value, 'date'):
                # datetime object
                return datetime.combine(date_value, datetime.min.time())
            else:
                return None
        except:
            return None
    
    for idx, row in df.iterrows():
        row_number = idx + 2
        
        # Salta righe completamente vuote
        all_fields_empty = all(is_field_empty(row[field]) for field in CONFIG['REQUIRED_FIELDS'])
        if all_fields_empty:
            continue
        
        matricola_dipendente = get_matricola_value(row)
        decorrenza_value = row.get('Decorrenza', '')
        scadenza_value = row.get('Scadenza', '')
        
        error_messages = []
        
        decorrenza_date = parse_date(decorrenza_value)
        scadenza_date = parse_date(scadenza_value)
        
        # Verifica Decorrenza
        if decorrenza_date:
            # Normalizza al solo giorno per confronto (ignora ore/minuti/secondi)
            decorrenza_date_only = decorrenza_date.date()
            data_min_only = CONFIG['DATA_MIN'].date()
            data_max_only = CONFIG['DATA_MAX'].date()
            
            if decorrenza_date_only < data_min_only:
                error_messages.append(f"Decorrenza ({decorrenza_date.strftime('%d/%m/%Y')}) deve essere maggiore o uguale a 01/01/{CONFIG['ANNO_RIFERIMENTO']}")
            elif decorrenza_date_only > data_max_only:
                error_messages.append(f"Decorrenza ({decorrenza_date.strftime('%d/%m/%Y')}) deve essere minore o uguale a 31/12/{CONFIG['ANNO_RIFERIMENTO']}")
        
        # Verifica Scadenza
        if scadenza_date:
            # Normalizza al solo giorno per confronto (ignora ore/minuti/secondi)
            scadenza_date_only = scadenza_date.date()
            data_min_only = CONFIG['DATA_MIN'].date()
            data_max_only = CONFIG['DATA_MAX'].date()
            
            if scadenza_date_only < data_min_only:
                error_messages.append(f"Scadenza ({scadenza_date.strftime('%d/%m/%Y')}) deve essere maggiore o uguale a 01/01/{CONFIG['ANNO_RIFERIMENTO']}")
            elif scadenza_date_only > data_max_only:
                error_messages.append(f"Scadenza ({scadenza_date.strftime('%d/%m/%Y')}) deve essere minore o uguale a 31/12/{CONFIG['ANNO_RIFERIMENTO']}")
        
        # Verifica che Scadenza sia successiva a Decorrenza
        if decorrenza_date and scadenza_date:
            if scadenza_date <= decorrenza_date:
                error_messages.append(
                    f"Scadenza ({scadenza_date.strftime('%d/%m/%Y')}) deve essere successiva a " +
                    f"Decorrenza ({decorrenza_date.strftime('%d/%m/%Y')})"
                )
        
        if error_messages:
            date_errors.append({
                'row_number': row_number,
                'matricola_dipendente': matricola_dipendente,
                'error_messages': error_messages
            })
    
    if date_errors:
        print_color(f"✗ Trovati {len(date_errors)} record con date non valide.", Colors.RED)
    else:
        print_color("✓ Tutte le date Decorrenza e Scadenza sono valide.", Colors.GREEN)
    
    # ========================================================================
    # FASE 6: REPORT FINALE
    # ========================================================================
    
    print_section("FASE 6: Report di validazione")
    
    print_color(f"Totale righe nel file: ", Colors.CYAN, end='')
    print_color(str(len(df)), Colors.WHITE)
    print_color(f"Righe completamente vuote (saltate): ", Colors.GRAY, end='')
    print_color(str(skipped_empty_rows), Colors.WHITE)
    print_color(f"Record effettivamente valorizzati: ", Colors.CYAN, end='')
    print_color(str(processed_records), Colors.WHITE)
    
    if not validation_errors and not duplicate_errors and not referent_errors and not date_errors:
        print()
        print_color("═" * 80, Colors.GREEN)
        print_color("  ✓✓✓ VALIDAZIONE COMPLETATA CON SUCCESSO! ✓✓✓", Colors.GREEN)
        print_color("═" * 80, Colors.GREEN)
        print()
        if processed_records == 0:
            print_color("ATTENZIONE: Non ci sono record valorizzati nel file.", Colors.YELLOW)
            print_color("Il file contiene solo righe vuote.", Colors.YELLOW)
        else:
            print_color(f"Tutti i {processed_records} record valorizzati hanno tutti i campi obbligatori completi.", Colors.GREEN)
            print_color("Il file e pronto per il processo di import massivo.", Colors.GREEN)
        print()
        return 0
    
    else:
        print()
        print_color("═" * 80, Colors.RED)
        print_color("  ✗✗✗ VALIDAZIONE FALLITA! ✗✗✗", Colors.RED)
        print_color("═" * 80, Colors.RED)
        print()
        
        # Raggruppa tutti gli errori per riga
        errors_by_row = {}
        
        # Aggiungi errori di campi mancanti
        for error in validation_errors:
            row_num = error['row_number']
            if row_num not in errors_by_row:
                errors_by_row[row_num] = {
                    'matricola': error['matricola'],
                    'missing_fields': [],
                    'duplicate_error': None,
                    'referent_error': None,
                    'date_errors': []
                }
            errors_by_row[row_num]['missing_fields'] = error['missing_fields']
        
        # Aggiungi errori di matricole duplicate
        for error in duplicate_errors:
            for row_num in error['rows']:
                if row_num not in errors_by_row:
                    errors_by_row[row_num] = {
                        'matricola': error['matricola'],
                        'missing_fields': [],
                        'duplicate_error': None,
                        'referent_error': None,
                        'date_errors': []
                    }
                # Mostra tutte le righe dove compare la stessa matricola
                other_rows = [r for r in error['rows'] if r != row_num]
                errors_by_row[row_num]['duplicate_error'] = other_rows
        
        # Aggiungi errori referenti
        for error in referent_errors:
            row_num = error['row_number']
            if row_num not in errors_by_row:
                errors_by_row[row_num] = {
                    'matricola': error['matricola_dipendente'],
                    'missing_fields': [],
                    'duplicate_error': None,
                    'referent_error': None,
                    'date_errors': []
                }
            errors_by_row[row_num]['referent_error'] = error['matricola_referente']
        
        # Aggiungi errori date
        for error in date_errors:
            row_num = error['row_number']
            if row_num not in errors_by_row:
                errors_by_row[row_num] = {
                    'matricola': error['matricola_dipendente'],
                    'missing_fields': [],
                    'duplicate_error': None,
                    'referent_error': None,
                    'date_errors': []
                }
            errors_by_row[row_num]['date_errors'] = error['error_messages']
        
        # Mostra riepilogo errori
        total_errors = len(validation_errors) + len(duplicate_errors) + len(referent_errors) + len(date_errors)
        print_color(f"Trovati {total_errors} errori in {len(errors_by_row)} record su {processed_records} valorizzati.", Colors.RED)
        print()
        print_color("DETTAGLIO ERRORI PER RECORD:", Colors.YELLOW)
        print_color("=" * 80, Colors.YELLOW)
        
        # Mostra errori raggruppati per riga
        for row_num in sorted(errors_by_row.keys()):
            error_data = errors_by_row[row_num]
            print()
            print_color(f"► Riga {row_num} - Matricola: ", Colors.RED, end='')
            print_color(error_data['matricola'], Colors.WHITE)
            
            # Campi mancanti
            if error_data['missing_fields']:
                print_color("  ✗ Campi obbligatori NON valorizzati:", Colors.YELLOW)
                for field in error_data['missing_fields']:
                    print_color(f"      - {field}", Colors.RED)
            
            # Matricola duplicata
            if error_data['duplicate_error']:
                other_rows_str = ", ".join(map(str, error_data['duplicate_error']))
                print_color(f"  ✗ Matricola DUPLICATA: presente anche nelle righe ", Colors.YELLOW, end='')
                print_color(other_rows_str, Colors.RED)
            
            # Errore referente
            if error_data['referent_error']:
                print_color(f"  ✗ Referente valutatore: matricola ", Colors.YELLOW, end='')
                print_color(error_data['referent_error'], Colors.RED, end='')
                print_color(" NON ESISTE nel file", Colors.YELLOW)
            
            # Errori date
            if error_data['date_errors']:
                print_color("  ✗ Errori date:", Colors.YELLOW)
                for msg in error_data['date_errors']:
                    print_color(f"      - {msg}", Colors.RED)
        
        print()
        print_color("=" * 80, Colors.YELLOW)
        print()
        
        print_color("AZIONE RICHIESTA:", Colors.YELLOW)
        print_color("Correggere i campi mancanti nel file Excel prima di procedere con l'import.", Colors.YELLOW)
        print()
        
        return 1

if __name__ == "__main__":
    try:
        exit_code = main()
        sys.exit(exit_code)
    except KeyboardInterrupt:
        print()
        print_color("\nOperazione interrotta dall'utente.", Colors.YELLOW)
        sys.exit(130)
    except Exception as e:
        print()
        print_color(f"ERRORE CRITICO: {str(e)}", Colors.RED)
        import traceback
        traceback.print_exc()
        sys.exit(1)
