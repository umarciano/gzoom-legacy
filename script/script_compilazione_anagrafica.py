#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Script per la compilazione automatica di IMPORT_RISORSE_UMANE.xlsx
Legge i dati dal file Template_Dipendenti_AORN.xlsx (sheet "Template Dipendenti AORN")
e popola il file IMPORT_RISORSE_UMANE.xlsx con i dati delle risorse umane
"""

import os
import sys
from pathlib import Path
import pandas as pd
from datetime import datetime

# =====================================================================
# CONFIGURAZIONE - NOMI FILE E CAMPI
# =====================================================================
CONFIG = {
    # Cartelle
    'TEMPLATE_DIR': 'template',
    
    # File sorgente
    'SOURCE_FILE': 'Template_Dipendenti_AORN.xlsx',
    'SOURCE_SHEET': 'Template Dipendenti AORN',
    
    # File destinazione
    'TARGET_FILE': 'IMPORT_RISORSE_UMANE.xlsx',
    'TARGET_SHEET': 'Sheet1',
    
    # Colonne file sorgente
    'SRC_COL_MATRICOLA': 'Matricola',
    'SRC_COL_NOME': 'Nome',
    'SRC_COL_COGNOME': 'Cognome',
    'SRC_COL_CF': 'Codice Fiscale',
    'SRC_COL_RUOLO': 'Ruolo GZOOM',
    'SRC_COL_CODICE_UOC': 'Codice UOC',
    'SRC_COL_NOME_UOC': 'Nome UOC',
    'SRC_COL_MATR_VALUTATORE': 'Matricola Referente Valutatore',
    'SRC_COL_EMAIL': 'Email',
    'SRC_COL_USERNAME': 'Username',
    
    # Colonne file destinazione
    'OUT_COL_PERSON_CODE': 'Person Code',
    'OUT_COL_FIRST_NAME': 'First Name',
    'OUT_COL_LAST_NAME': 'Last Name',
    'OUT_COL_FISCAL_CODE': 'Fiscal Code',
    'OUT_COL_PERSON_ROLE_TYPE': 'Person Role Type',
    'OUT_COL_EMPL_POS_TYPE': 'Employment Position Type',
    'OUT_COL_QUAL_FROM_DATE': 'Qualification From Date',
    'OUT_COL_EMPL_AMOUNT': 'Employment Amount',
    'OUT_COL_EMPL_START_DATE': 'Employment Start Date',
    'OUT_COL_EMPL_END_DATE': 'Employment End Date',
    'OUT_COL_EMPL_ORG_CODE': 'Employment Org Code',
    'OUT_COL_EMPL_ORG_ROLE_TYPE': 'Employment Org Role Type',
    'OUT_COL_EMPL_ORG_DESC': 'Employment Org Description',
    'OUT_COL_EMPL_ORG_COMMENTS': 'Employment Org Comments',
    'OUT_COL_EMPL_ORG_FROM_DATE': 'Employment Org From Date',
    'OUT_COL_EMPL_ORG_END_DATE': 'Employment Org End Date',
    'OUT_COL_EVALUATOR_CODE': 'Evaluator Code',
    'OUT_COL_EVALUATOR_FROM_DATE': 'Evaluator From Date',
    'OUT_COL_ALLOC_ORG_CODE': 'Allocation Org Code',
    'OUT_COL_ALLOC_ORG_ROLE_TYPE': 'Allocation Org Role Type',
    'OUT_COL_ALLOC_ORG_DESC': 'Allocation Org Description',
    'OUT_COL_ALLOC_ORG_COMMENTS': 'Allocation Org Comments',
    'OUT_COL_ALLOC_ORG_FROM_DATE': 'Allocation Org From Date',
    'OUT_COL_ALLOC_ORG_END_DATE': 'Allocation Org End Date',
    'OUT_COL_IS_EVAL_MGR': 'Is Evaluation Manager',
    'OUT_COL_APPROVER_CODE': 'Approver Code',
    'OUT_COL_EMAIL': 'Email',
    'OUT_COL_MOBILE_PHONE': 'Mobile Phone',
    'OUT_COL_USER_LOGIN_ID': 'User Login ID',
    'OUT_COL_GROUP_PROFILE_ID': 'Group Profile ID',
    'OUT_COL_WE_ASSIGN_CODE': 'Work Effort Assignment Code',
    'OUT_COL_WE_DATE': 'Work Effort Date',
    'OUT_COL_EMPL_POS_TYPE_DATE': 'Employment Position Type Date',
    'OUT_COL_DESCRIPTION': 'Description',
    'OUT_COL_COMMENTS': 'Comments',
    'OUT_COL_REF_DATE': 'Reference Date',
    'OUT_COL_DATA_SOURCE': 'Data Source',
    
    # Valori costanti
    'DEFAULT_DATE': '01/01/2025',
    'DEFAULT_EMPL_AMOUNT': '1',
    'DEFAULT_EMPL_ORG_ROLE_TYPE': 'ORGANIZATION_UNIT',
    'DEFAULT_DATA_SOURCE': 'IMPORT_HR',
    'GROUP_PROFILE_VALUTATORE': 'EMPLPERF_VALUTATORE',
    'GROUP_PROFILE_VALUTATO': 'EMPLPERF_VALUTATO',
    'FLAG_YES': 'Y',
    'FLAG_NO': 'N',
}

# Colori per output console
class Colors:
    CYAN = '\033[96m'
    GREEN = '\033[92m'
    YELLOW = '\033[93m'
    RED = '\033[91m'
    RESET = '\033[0m'

def print_colored(message, color):
    print(f"{color}{message}{Colors.RESET}")

def clean_value(value):
    """
    Converte un valore in stringa pulita, rimuovendo '.0' dai numeri float.
    Es: 123456.0 -> "123456", "test" -> "test"
    """
    if pd.isna(value):
        return ''
    
    str_value = str(value).strip()
    
    # Se termina con .0, rimuovilo (è un numero intero letto come float)
    if str_value.endswith('.0'):
        try:
            # Verifica che sia effettivamente un numero
            float(str_value)
            return str_value[:-2]
        except ValueError:
            return str_value
    
    return str_value

def main():
    # Percorsi dei file
    script_dir = Path(__file__).parent.absolute()
    template_dir = script_dir / CONFIG['TEMPLATE_DIR']
    source_file = template_dir / CONFIG['SOURCE_FILE']
    target_file = template_dir / CONFIG['TARGET_FILE']
    
    print_colored("Inizio elaborazione...", Colors.CYAN)
    
    # Verifica esistenza file sorgente
    if not source_file.exists():
        print_colored(f"ERRORE: File sorgente {source_file} non trovato!", Colors.RED)
        sys.exit(1)
    
    try:
        # Leggi i dati dal foglio template dipendenti
        print_colored(f"Lettura dati dal file {source_file} (sheet '{CONFIG['SOURCE_SHEET']}')...", Colors.CYAN)
        dipendenti_df = pd.read_excel(source_file, sheet_name=CONFIG['SOURCE_SHEET'])
        
        print_colored(f"Trovati {len(dipendenti_df)} righe totali nel file sorgente.", Colors.GREEN)
        
        # Crea un array con tutte le matricole dei valutatori
        valutatori = set()
        for _, row in dipendenti_df.iterrows():
            if not pd.isna(row.get(CONFIG['SRC_COL_MATR_VALUTATORE'])):
                matricola_val = str(row[CONFIG['SRC_COL_MATR_VALUTATORE']]).strip()
                if matricola_val:
                    valutatori.add(matricola_val)
        
        print_colored(f"Identificati {len(valutatori)} valutatori unici.", Colors.GREEN)
        
        # Crea la struttura dati per l'export
        output_data = []
        
        # Per ogni dipendente, crea la riga corrispondente
        for _, row in dipendenti_df.iterrows():
            # Salta righe vuote
            if pd.isna(row.get(CONFIG['SRC_COL_MATRICOLA'])) or str(row.get(CONFIG['SRC_COL_MATRICOLA'])).strip() == '':
                continue
            
            # Estrai i dati usando clean_value per rimuovere i ".0" dalle matricole
            matricola = clean_value(row.get(CONFIG['SRC_COL_MATRICOLA']))
            nome = clean_value(row.get(CONFIG['SRC_COL_NOME']))
            cognome = clean_value(row.get(CONFIG['SRC_COL_COGNOME']))
            codice_fiscale = clean_value(row.get(CONFIG['SRC_COL_CF']))
            ruolo_gzoom = clean_value(row.get(CONFIG['SRC_COL_RUOLO']))
            codice_uoc = clean_value(row.get(CONFIG['SRC_COL_CODICE_UOC']))
            nome_uoc = clean_value(row.get(CONFIG['SRC_COL_NOME_UOC']))
            matricola_valutatore = clean_value(row.get(CONFIG['SRC_COL_MATR_VALUTATORE']))
            email = clean_value(row.get(CONFIG['SRC_COL_EMAIL']))
            username = clean_value(row.get(CONFIG['SRC_COL_USERNAME']))
            
            # Determina se questo dipendente è un Evaluation Manager
            is_evaluation_manager = CONFIG['FLAG_YES'] if matricola in valutatori else CONFIG['FLAG_NO']
            
            # Determina il Group Profile ID
            group_profile_id = CONFIG['GROUP_PROFILE_VALUTATORE'] if is_evaluation_manager == CONFIG['FLAG_YES'] else CONFIG['GROUP_PROFILE_VALUTATO']
            
            # Crea l'oggetto con tutti i campi mappati
            output_data.append({
                CONFIG['OUT_COL_PERSON_CODE']: matricola,
                CONFIG['OUT_COL_FIRST_NAME']: nome,
                CONFIG['OUT_COL_LAST_NAME']: cognome,
                CONFIG['OUT_COL_FISCAL_CODE']: codice_fiscale,
                CONFIG['OUT_COL_PERSON_ROLE_TYPE']: ruolo_gzoom,
                CONFIG['OUT_COL_EMPL_POS_TYPE']: ruolo_gzoom,
                CONFIG['OUT_COL_QUAL_FROM_DATE']: CONFIG['DEFAULT_DATE'],
                CONFIG['OUT_COL_EMPL_AMOUNT']: CONFIG['DEFAULT_EMPL_AMOUNT'],
                CONFIG['OUT_COL_EMPL_START_DATE']: CONFIG['DEFAULT_DATE'],
                CONFIG['OUT_COL_EMPL_END_DATE']: '',
                CONFIG['OUT_COL_EMPL_ORG_CODE']: codice_uoc,
                CONFIG['OUT_COL_EMPL_ORG_ROLE_TYPE']: CONFIG['DEFAULT_EMPL_ORG_ROLE_TYPE'],
                CONFIG['OUT_COL_EMPL_ORG_DESC']: nome_uoc,
                CONFIG['OUT_COL_EMPL_ORG_COMMENTS']: '',
                CONFIG['OUT_COL_EMPL_ORG_FROM_DATE']: CONFIG['DEFAULT_DATE'],
                CONFIG['OUT_COL_EMPL_ORG_END_DATE']: '',
                CONFIG['OUT_COL_EVALUATOR_CODE']: matricola_valutatore,
                CONFIG['OUT_COL_EVALUATOR_FROM_DATE']: CONFIG['DEFAULT_DATE'],
                CONFIG['OUT_COL_ALLOC_ORG_CODE']: '',
                CONFIG['OUT_COL_ALLOC_ORG_ROLE_TYPE']: '',
                CONFIG['OUT_COL_ALLOC_ORG_DESC']: '',
                CONFIG['OUT_COL_ALLOC_ORG_COMMENTS']: '',
                CONFIG['OUT_COL_ALLOC_ORG_FROM_DATE']: '',
                CONFIG['OUT_COL_ALLOC_ORG_END_DATE']: '',
                CONFIG['OUT_COL_IS_EVAL_MGR']: is_evaluation_manager,
                CONFIG['OUT_COL_APPROVER_CODE']: matricola_valutatore,
                CONFIG['OUT_COL_EMAIL']: email,
                CONFIG['OUT_COL_MOBILE_PHONE']: '',
                CONFIG['OUT_COL_USER_LOGIN_ID']: username,
                CONFIG['OUT_COL_GROUP_PROFILE_ID']: group_profile_id,
                CONFIG['OUT_COL_WE_ASSIGN_CODE']: '',
                CONFIG['OUT_COL_WE_DATE']: '',
                CONFIG['OUT_COL_EMPL_POS_TYPE_DATE']: CONFIG['DEFAULT_DATE'],
                CONFIG['OUT_COL_DESCRIPTION']: '',
                CONFIG['OUT_COL_COMMENTS']: '',
                CONFIG['OUT_COL_REF_DATE']: CONFIG['DEFAULT_DATE'],
                CONFIG['OUT_COL_DATA_SOURCE']: CONFIG['DEFAULT_DATA_SOURCE']
            })
        
        print_colored(f"Totale righe da scrivere: {len(output_data)}", Colors.GREEN)
        
        # Esporta i dati nel file di destinazione
        print_colored(f"Scrittura dati nel file {target_file}...", Colors.CYAN)
        
        df_output = pd.DataFrame(output_data)
        
        # Converti TUTTE le colonne in stringhe per evitare problemi di formattazione in Excel
        # Questo assicura che le matricole e altri valori non vengano interpretati come numeri
        for col in df_output.columns:
            df_output[col] = df_output[col].astype(str)
        
        # Scrivi il file Excel
        df_output.to_excel(target_file, sheet_name=CONFIG['TARGET_SHEET'], index=False, engine='openpyxl')
        
        print_colored(f"COMPLETATO! File {target_file} generato con successo.", Colors.GREEN)
        print_colored(f"Totale righe scritte: {len(output_data)}", Colors.GREEN)
        print_colored(f"Valutatori identificati: {len(valutatori)}", Colors.GREEN)
        
    except Exception as e:
        print_colored(f"ERRORE durante l'elaborazione: {str(e)}", Colors.RED)
        import traceback
        traceback.print_exc()
        sys.exit(1)

if __name__ == "__main__":
    main()
