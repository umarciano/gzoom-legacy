#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Script per la compilazione automatica di IMPORT_DIPARTIMENTO_E_UOC.xlsx
Legge i dati dal file Template_Dipendenti_AORN.xlsx (sheet Legenda, colonne F-K)
e popola il file IMPORT_DIPARTIMENTO_E_UOC.xlsx con la gerarchia Dipartimento/UOC
"""

import os
import sys
from pathlib import Path
import pandas as pd
from datetime import datetime

# =====================================================================
# CONFIGURAZIONE - NOMI FILE E CARTELLE
# =====================================================================
CONFIG = {
    # Cartelle
    'TEMPLATE_DIR': 'template',
    
    # File sorgente
    'SOURCE_FILE': 'Template_Dipendenti_AORN.xlsx',
    'SOURCE_SHEET': 'Legenda',
    
    # File destinazione
    'TARGET_FILE': 'IMPORT_DIPARTIMENTO_E_UOC.xlsx',
    'TARGET_SHEET': 'Sheet1',
    
    # Colonne file sorgente (sheet Legenda)
    'COL_CODICE_DIP': 'Codice Dipartimento',
    'COL_NOME_DIP': 'Dipartimento',
    'COL_RESP_DIP': 'Matricola Responsabile Dipartimento',
    'COL_CODICE_UOC': 'Codice UOC',
    'COL_NOME_UOC': 'UOC',
    'COL_RESP_UOC': 'Matricola Responsabile UOC',
    
    # Colonne file destinazione
    'OUT_COL_UOC_CODE': 'UOC Code',
    'OUT_COL_DESCRIPTION': 'Description',
    'OUT_COL_UNIT_TYPE': 'Unit Type',
    'OUT_COL_PARENT_UOC': 'Parent UOC Code',
    'OUT_COL_PARENT_TYPE': 'Parent Unit Type',
    'OUT_COL_RESPONSIBLE': 'Responsible Code',
    'OUT_COL_REF_DATE': 'Reference Date',
    'OUT_COL_END_DATE': 'End date',
    
    # Valori costanti
    'UNIT_TYPE_ORG': 'ORG',
    'UNIT_TYPE_ORG_UNIT': 'ORGANIZATION_UNIT',
    'PARENT_ROOT': 'ORGUNIT001',
    'DEFAULT_REF_DATE': '01/01/2025',
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
        # Leggi i dati dal foglio "Legenda"
        print_colored(f"Lettura dati dal file {source_file} (sheet {CONFIG['SOURCE_SHEET']})...", Colors.CYAN)
        legenda_df = pd.read_excel(source_file, sheet_name=CONFIG['SOURCE_SHEET'])
        
        # Crea un dizionario per raggruppare le UOC per Dipartimento
        dipartimenti = {}
        
        for _, row in legenda_df.iterrows():
            # Salta righe vuote
            if pd.isna(row.get(CONFIG['COL_CODICE_DIP'])) or str(row.get(CONFIG['COL_CODICE_DIP'])).strip() == '':
                continue
            
            codice_dip = clean_value(row.get(CONFIG['COL_CODICE_DIP']))
            nome_dip = clean_value(row.get(CONFIG['COL_NOME_DIP']))
            resp_dip = clean_value(row.get(CONFIG['COL_RESP_DIP']))
            codice_uoc = clean_value(row.get(CONFIG['COL_CODICE_UOC']))
            nome_uoc = clean_value(row.get(CONFIG['COL_NOME_UOC']))
            resp_uoc = clean_value(row.get(CONFIG['COL_RESP_UOC']))
            
            # Se il dipartimento non esiste, crealo
            if codice_dip not in dipartimenti:
                dipartimenti[codice_dip] = {
                    'Codice': codice_dip,
                    'Nome': nome_dip,
                    'Responsabile': resp_dip,
                    'UOCs': []
                }
            
            # Aggiungi la UOC al dipartimento (solo se non è vuota)
            if codice_uoc:
                dipartimenti[codice_dip]['UOCs'].append({
                    'Codice': codice_uoc,
                    'Nome': nome_uoc,
                    'Responsabile': resp_uoc
                })
        
        print_colored(f"Trovati {len(dipartimenti)} dipartimenti.", Colors.GREEN)
        
        # Crea la struttura dati per l'export
        output_data = []
        
        # Per ogni dipartimento, crea prima la riga del dipartimento e poi le righe delle UOC
        for codice_dip in sorted(dipartimenti.keys()):
            dip = dipartimenti[codice_dip]
            
            # Aggiungi la riga del Dipartimento
            output_data.append({
                CONFIG['OUT_COL_UOC_CODE']: dip['Codice'],
                CONFIG['OUT_COL_DESCRIPTION']: dip['Nome'],
                CONFIG['OUT_COL_UNIT_TYPE']: CONFIG['UNIT_TYPE_ORG'],
                CONFIG['OUT_COL_PARENT_UOC']: CONFIG['PARENT_ROOT'],
                CONFIG['OUT_COL_PARENT_TYPE']: CONFIG['UNIT_TYPE_ORG'],
                CONFIG['OUT_COL_RESPONSIBLE']: dip['Responsabile'],
                CONFIG['OUT_COL_REF_DATE']: CONFIG['DEFAULT_REF_DATE'],
                CONFIG['OUT_COL_END_DATE']: ''
            })
            
            # Aggiungi le righe delle UOC di questo dipartimento
            for uoc in dip['UOCs']:
                output_data.append({
                    CONFIG['OUT_COL_UOC_CODE']: uoc['Codice'],
                    CONFIG['OUT_COL_DESCRIPTION']: uoc['Nome'],
                    CONFIG['OUT_COL_UNIT_TYPE']: CONFIG['UNIT_TYPE_ORG_UNIT'],
                    CONFIG['OUT_COL_PARENT_UOC']: dip['Codice'],
                    CONFIG['OUT_COL_PARENT_TYPE']: CONFIG['UNIT_TYPE_ORG'],
                    CONFIG['OUT_COL_RESPONSIBLE']: uoc['Responsabile'],
                    CONFIG['OUT_COL_REF_DATE']: CONFIG['DEFAULT_REF_DATE'],
                    CONFIG['OUT_COL_END_DATE']: ''
                })
        
        print_colored(f"Totale righe da scrivere: {len(output_data)}", Colors.GREEN)
        
        # Esporta i dati nel file di destinazione
        print_colored(f"Scrittura dati nel file {target_file}...", Colors.CYAN)
        
        df_output = pd.DataFrame(output_data)
        
        # Converti TUTTE le colonne in stringhe per evitare problemi di formattazione in Excel
        for col in df_output.columns:
            df_output[col] = df_output[col].astype(str)
        
        df_output.to_excel(target_file, sheet_name=CONFIG['TARGET_SHEET'], index=False, engine='openpyxl')
        
        print_colored(f"COMPLETATO! File {target_file} generato con successo.", Colors.GREEN)
        print_colored(f"Totale righe scritte: {len(output_data)}", Colors.GREEN)
        
    except Exception as e:
        print_colored(f"ERRORE durante l'elaborazione: {str(e)}", Colors.RED)
        import traceback
        traceback.print_exc()
        sys.exit(1)

if __name__ == "__main__":
    main()
