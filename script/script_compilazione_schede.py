#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Script per la compilazione automatica di IMPORT_SCHEDE.xlsx
Legge i dati dal file Template_Dipendenti_AORN.xlsx (sheet "Template Dipendenti AORN" e "Legenda")
e popola il file IMPORT_SCHEDE.xlsx (sheet "SCHEDE") con i dati delle schede di valutazione
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
    'TEMPLATE_DIR': 'templates',
    
    # File sorgente
    'SOURCE_FILE': 'Template_Dipendenti_AORN.xlsx',
    'SOURCE_SHEET_DIPENDENTI': 'Template Dipendenti AORN',
    'SOURCE_SHEET_LEGENDA': 'Legenda',
    
    # File destinazione
    'TARGET_FILE': 'IMPORT_SCHEDE.xlsx',
    'TARGET_SHEET': 'SCHEDE',
    'TARGET_FILE_RUOLI': 'IMPORT_RUOLI.xlsx',
    'TARGET_SHEET_RUOLI': 'RUOLI',
    
    # Colonne sheet Legenda (lookup)
    'LEG_COL_DESC_INCARICHI': 'Descrizione INCARICHI ECONOMICI',
    'LEG_COL_TIPO_SCHEDA': 'Tipo Scheda',
    'LEG_COL_DESC_SCHEDA': 'Descrizione Scheda',
    'LEG_COL_RUOLO_GZOOM': 'Ruolo GZOOM',
    
    # Colonne sheet Template Dipendenti
    'DIP_COL_MATRICOLA': 'Matricola',
    'DIP_COL_NOME': 'Nome',
    'DIP_COL_COGNOME': 'Cognome',
    'DIP_COL_CODICE_UOC': 'Codice UOC',
    'DIP_COL_MATR_VALUTATORE': 'Matricola Referente Valutatore',
    'DIP_COL_DESC_INCARICHI': 'Descrizione INCARICHI ECONOMICI',
    'DIP_COL_TIPO_SCHEDA': 'Tipo Scheda',
    'DIP_COL_RUOLO_GZOOM': 'Ruolo GZOOM',
    'DIP_COL_DECORRENZA': 'Decorrenza',
    'DIP_COL_SCADENZA': 'Scadenza',
    
    # Colonne file destinazione
    'OUT_COL_CONTESTO': 'Contesto',
    'OUT_COL_CODICE_SCHEDA': 'Codice Scheda',
    'OUT_COL_NOME_SCHEDA': 'Nome Scheda',
    'OUT_COL_MATR_VALUTATO': 'Matricola Valutato',
    'OUT_COL_MATR_VALUTATORE': 'Matricola Valutatore',
    'OUT_COL_CODICE_UOC': 'Codice UOC',
    'OUT_COL_CODICE_TEMPLATE': 'templateCode',
    'OUT_COL_DATA_INIZIO': 'Data Inizio',
    'OUT_COL_DATA_FINE': 'Data Fine',
    'OUT_COL_STATO': 'Stato',
    'OUT_COL_DESCRIZIONE': 'Descrizione',
    
    # Mapping tipi scheda -> codici template
    'TEMPLATE_MAPPING': {
        "SCHEDA 1": "SCH1",
        "SCHEDA 1.1": "SCH1B",
        "SCHEDA 2": "SCH2",
        "SCHEDA 3": "SCH3",
        "SCHEDA 4": "SCH4",
        "SCHEDA 5": "SCH5"
    },
    
    # Valori costanti
    'DEFAULT_CONTESTO': 'IND',
    'DEFAULT_WORK_EFFORT_TYPE': 'CTX_EP',  # NUOVO: Tipo WorkEffort per le schede di valutazione
    'DEFAULT_STATO': 'WEEVALST_EXECPEND',
    'DEFAULT_DESCRIZIONE': 'Scheda valutazione Performance Anno 2025',
    'CODICE_SCHEDA_PREFIX': 'SCH_',
    'DATE_FORMAT': '%d/%m/%Y',
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
        print_colored(f"Lettura dati dal foglio '{CONFIG['SOURCE_SHEET_LEGENDA']}'...", Colors.CYAN)
        legenda_df = pd.read_excel(source_file, sheet_name=CONFIG['SOURCE_SHEET_LEGENDA'])
        
        # Crea dizionari per il lookup
        incarichi_lookup = {}
        ruolo_lookup = {}
        
        for _, row in legenda_df.iterrows():
            # Lookup per "Descrizione INCARICHI ECONOMICI"
            if not pd.isna(row.get(CONFIG['LEG_COL_DESC_INCARICHI'])):
                descrizione_incarico = str(row[CONFIG['LEG_COL_DESC_INCARICHI']]).strip()
                tipo_scheda = str(row[CONFIG['LEG_COL_TIPO_SCHEDA']]).strip() if not pd.isna(row.get(CONFIG['LEG_COL_TIPO_SCHEDA'])) else ''
                descrizione_scheda = str(row[CONFIG['LEG_COL_DESC_SCHEDA']]).strip() if not pd.isna(row.get(CONFIG['LEG_COL_DESC_SCHEDA'])) else ''
                
                if descrizione_incarico not in incarichi_lookup:
                    incarichi_lookup[descrizione_incarico] = {
                        'TipoScheda': tipo_scheda,
                        'DescrizioneScheda': descrizione_scheda
                    }
            
            # Lookup per "Ruolo GZOOM"
            if not pd.isna(row.get(CONFIG['LEG_COL_RUOLO_GZOOM'])):
                ruolo_gzoom = str(row[CONFIG['LEG_COL_RUOLO_GZOOM']]).strip()
                descrizione_scheda_ruolo = str(row[CONFIG['LEG_COL_DESC_SCHEDA']]).strip() if not pd.isna(row.get(CONFIG['LEG_COL_DESC_SCHEDA'])) else ''
                
                if ruolo_gzoom not in ruolo_lookup:
                    ruolo_lookup[ruolo_gzoom] = descrizione_scheda_ruolo
        
        print_colored(f"Trovati {len(incarichi_lookup)} incarichi economici nella Legenda.", Colors.GREEN)
        print_colored(f"Trovati {len(ruolo_lookup)} ruoli GZOOM nella Legenda.", Colors.GREEN)
        
        # Leggi i dati dal foglio dipendenti
        print_colored(f"Lettura dati dal foglio '{CONFIG['SOURCE_SHEET_DIPENDENTI']}'...", Colors.CYAN)
        dipendenti_df = pd.read_excel(source_file, sheet_name=CONFIG['SOURCE_SHEET_DIPENDENTI'])
        
        print_colored(f"Trovati {len(dipendenti_df)} righe totali nel file sorgente.", Colors.GREEN)
        
        # Crea la struttura dati per l'export
        output_data = []
        
        # Per ogni dipendente, crea la scheda corrispondente
        for _, row in dipendenti_df.iterrows():
            # Salta righe vuote
            if pd.isna(row.get(CONFIG['DIP_COL_MATRICOLA'])) or str(row.get(CONFIG['DIP_COL_MATRICOLA'])).strip() == '':
                continue
            
            # Estrai i dati usando clean_value per rimuovere i ".0" dalle matricole
            matricola = clean_value(row.get(CONFIG['DIP_COL_MATRICOLA']))
            nome = clean_value(row.get(CONFIG['DIP_COL_NOME']))
            cognome = clean_value(row.get(CONFIG['DIP_COL_COGNOME']))
            codice_uoc = clean_value(row.get(CONFIG['DIP_COL_CODICE_UOC']))
            matricola_valutatore = clean_value(row.get(CONFIG['DIP_COL_MATR_VALUTATORE']))
            descrizione_incarico = clean_value(row.get(CONFIG['DIP_COL_DESC_INCARICHI']))
            tipo_scheda = clean_value(row.get(CONFIG['DIP_COL_TIPO_SCHEDA']))
            ruolo_gzoom = clean_value(row.get(CONFIG['DIP_COL_RUOLO_GZOOM']))
            
            # Formatta le date senza orario
            decorrenza = ''
            if not pd.isna(row.get(CONFIG['DIP_COL_DECORRENZA'])):
                if isinstance(row[CONFIG['DIP_COL_DECORRENZA']], datetime):
                    decorrenza = row[CONFIG['DIP_COL_DECORRENZA']].strftime(CONFIG['DATE_FORMAT'])
                else:
                    decorrenza = str(row[CONFIG['DIP_COL_DECORRENZA']]).strip()
            
            scadenza = ''
            if not pd.isna(row.get(CONFIG['DIP_COL_SCADENZA'])):
                if isinstance(row[CONFIG['DIP_COL_SCADENZA']], datetime):
                    scadenza = row[CONFIG['DIP_COL_SCADENZA']].strftime(CONFIG['DATE_FORMAT'])
                else:
                    scadenza = str(row[CONFIG['DIP_COL_SCADENZA']]).strip()
            
            # Genera il Codice Scheda
            codice_scheda = f"{CONFIG['CODICE_SCHEDA_PREFIX']}{matricola}"
            
            # Lookup nella Legenda per ottenere la Descrizione Scheda tramite "Ruolo GZOOM"
            descrizione_scheda_ruolo = ''
            if ruolo_gzoom and ruolo_gzoom in ruolo_lookup:
                descrizione_scheda_ruolo = ruolo_lookup[ruolo_gzoom]
            
            # Applica il mapping del Codice Template
            codice_template = ''
            if tipo_scheda and tipo_scheda in CONFIG['TEMPLATE_MAPPING']:
                codice_template = CONFIG['TEMPLATE_MAPPING'][tipo_scheda]
            else:
                codice_template = tipo_scheda
            
            # Genera il Nome Scheda
            nome_scheda = f"{nome} {cognome} ({matricola})"
            if descrizione_scheda_ruolo:
                nome_scheda += f" - {descrizione_scheda_ruolo}"
            
            # Crea l'oggetto con tutti i campi mappati
            output_data.append({
                CONFIG['OUT_COL_CONTESTO']: CONFIG['DEFAULT_CONTESTO'],
                CONFIG['OUT_COL_CODICE_SCHEDA']: codice_scheda,
                CONFIG['OUT_COL_NOME_SCHEDA']: nome_scheda,
                CONFIG['OUT_COL_MATR_VALUTATO']: matricola,
                CONFIG['OUT_COL_MATR_VALUTATORE']: matricola_valutatore,
                CONFIG['OUT_COL_CODICE_UOC']: codice_uoc,
                CONFIG['OUT_COL_CODICE_TEMPLATE']: codice_template,
                CONFIG['OUT_COL_DATA_INIZIO']: decorrenza,
                CONFIG['OUT_COL_DATA_FINE']: scadenza,
                CONFIG['OUT_COL_STATO']: CONFIG['DEFAULT_STATO'],
                CONFIG['OUT_COL_DESCRIZIONE']: CONFIG['DEFAULT_DESCRIZIONE']
            })
        
        print_colored(f"Totale schede da scrivere: {len(output_data)}", Colors.GREEN)
        
        # =========================================================================
        # GENERAZIONE FOGLIO RUOLI (WePartyInterface - Work Effort Party Assignment)
        # Per ogni scheda creiamo 2 righe: VALUTATO + VALUTATORE
        # Formato compatibile con WePartyInterface table
        # =========================================================================
        print_colored("Generazione associazioni VALUTATO/VALUTATORE...", Colors.CYAN)
        
        wepa_data = []
        
        for scheda in output_data:
            # Riga 1: VALUTATO (WEM_EVAL_IN_CHARGE)
            wepa_data.append({
                'dataSource': 'IMPORT_RUOLI',
                'sourceReferenceRootId': scheda[CONFIG['OUT_COL_CODICE_SCHEDA']],
                'sourceReferenceId': scheda[CONFIG['OUT_COL_CODICE_SCHEDA']],  # CORREZIONE: Usa codice scheda invece di vuoto
                'workEffortName': scheda[CONFIG['OUT_COL_NOME_SCHEDA']],      # CORREZIONE: Usa nome scheda invece di _NA_
                'workEffortTypeId': CONFIG['DEFAULT_WORK_EFFORT_TYPE'],       # CORREZIONE: Usa CTX_EP invece di _NA_
                'roleTypeId': 'WEM_EVAL_IN_CHARGE',                           # CORREZIONE: Usa nome campo database
                'partyCode': scheda[CONFIG['OUT_COL_MATR_VALUTATO']],         # CORREZIONE: Usa nome campo database
                'partyName': f"{scheda[CONFIG['OUT_COL_NOME_VALUTATO']]} {scheda[CONFIG['OUT_COL_COGNOME_VALUTATO']]}",  # Nome completo (formato: Nome Cognome)
                'roleTypeDesc': '_NA_',                                       # FIX: Non usare description per matching (causa duplicati)
                'fromDate': scheda[CONFIG['OUT_COL_DATA_INIZIO']],            # CORREZIONE: Usa nome campo database
                'thruDate': scheda[CONFIG['OUT_COL_DATA_FINE']]               # CORREZIONE: Usa nome campo database
            })
            
            # Riga 2: VALUTATORE (WEM_EVAL_MANAGER) - solo se presente
            if scheda[CONFIG['OUT_COL_MATR_VALUTATORE']] and scheda[CONFIG['OUT_COL_MATR_VALUTATORE']].strip():
                wepa_data.append({
                    'dataSource': 'IMPORT_RUOLI',
                    'sourceReferenceRootId': scheda[CONFIG['OUT_COL_CODICE_SCHEDA']],
                    'sourceReferenceId': scheda[CONFIG['OUT_COL_CODICE_SCHEDA']],  # CORREZIONE: Usa codice scheda invece di vuoto
                    'workEffortName': scheda[CONFIG['OUT_COL_NOME_SCHEDA']],      # CORREZIONE: Usa nome scheda invece di _NA_
                    'workEffortTypeId': CONFIG['DEFAULT_WORK_EFFORT_TYPE'],       # CORREZIONE: Usa CTX_EP invece di _NA_
                    'roleTypeId': 'WEM_EVAL_MANAGER',                             # CORREZIONE: Usa nome campo database
                    'partyCode': scheda[CONFIG['OUT_COL_MATR_VALUTATORE']],       # CORREZIONE: Usa nome campo database
                    'partyName': f"{scheda[CONFIG['OUT_COL_NOME_VALUTATORE']]} {scheda[CONFIG['OUT_COL_COGNOME_VALUTATORE']]}",  # Nome completo (formato: Nome Cognome)
                    'roleTypeDesc': '_NA_',                                       # FIX: Non usare description per matching (causa duplicati)
                    'fromDate': scheda[CONFIG['OUT_COL_DATA_INIZIO']],            # CORREZIONE: Usa nome campo database
                    'thruDate': scheda[CONFIG['OUT_COL_DATA_FINE']]               # CORREZIONE: Usa nome campo database
                })
        
        print_colored(f"Totale associazioni WEPA da scrivere: {len(wepa_data)}", Colors.GREEN)
        
        # Esporta i dati SCHEDE nel file IMPORT_SCHEDE.xlsx (sheet "SCHEDE")
        print_colored(f"Scrittura dati nel file {target_file} (sheet '{CONFIG['TARGET_SHEET']}')...", Colors.CYAN)
        
        df_output = pd.DataFrame(output_data)
        
        # Converti TUTTE le colonne in stringhe per evitare problemi di formattazione in Excel
        for col in df_output.columns:
            df_output[col] = df_output[col].astype(str)
        
        # Esporta sheet SCHEDE
        with pd.ExcelWriter(target_file, engine='openpyxl', mode='w') as writer:
            df_output.to_excel(writer, sheet_name=CONFIG['TARGET_SHEET'], index=False)
        
        # Esporta i dati RUOLI nel file separato IMPORT_RUOLI.xlsx (sheet "RUOLI") nella stessa directory templates
        target_file_ruoli = script_dir / CONFIG['TEMPLATE_DIR'] / CONFIG['TARGET_FILE_RUOLI']
        print_colored(f"Scrittura dati nel file {target_file_ruoli} (sheet '{CONFIG['TARGET_SHEET_RUOLI']}')...", Colors.CYAN)
        
        df_wepa = pd.DataFrame(wepa_data)
        for col in df_wepa.columns:
            df_wepa[col] = df_wepa[col].astype(str)
        
        with pd.ExcelWriter(target_file_ruoli, engine='openpyxl', mode='w') as writer:
            df_wepa.to_excel(writer, sheet_name=CONFIG['TARGET_SHEET_RUOLI'], index=False)
        
        print_colored(f"COMPLETATO! File generati con successo:", Colors.GREEN)
        print_colored(f"  - {target_file} (Schede: {len(output_data)})", Colors.GREEN)
        print_colored(f"  - {target_file_ruoli} (Ruoli: {len(wepa_data)})", Colors.GREEN)
        
    except Exception as e:
        print_colored(f"ERRORE durante l'elaborazione: {str(e)}", Colors.RED)
        import traceback
        traceback.print_exc()
        sys.exit(1)

if __name__ == "__main__":
    main()
