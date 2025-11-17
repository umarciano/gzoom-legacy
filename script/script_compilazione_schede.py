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

# Colori per output console
class Colors:
    CYAN = '\033[96m'
    GREEN = '\033[92m'
    YELLOW = '\033[93m'
    RED = '\033[91m'
    RESET = '\033[0m'

def print_colored(message, color):
    print(f"{color}{message}{Colors.RESET}")

def main():
    # Percorsi dei file
    script_dir = Path(__file__).parent.absolute()
    template_dir = script_dir / "template"
    source_file = template_dir / "Template_Dipendenti_AORN.xlsx"
    target_file = template_dir / "IMPORT_SCHEDE.xlsx"
    
    print_colored("Inizio elaborazione...", Colors.CYAN)
    
    # Verifica esistenza file sorgente
    if not source_file.exists():
        print_colored(f"ERRORE: File sorgente {source_file} non trovato!", Colors.RED)
        sys.exit(1)
    
    # Mapping dei codici template
    template_mapping = {
        "SCHEDA 1": "SCH1",
        "SCHEDA 1.1": "SCH1B",
        "SCHEDA 2": "SCH2",
        "SCHEDA 3": "SCH3",
        "SCHEDA 4": "SCH4",
        "SCHEDA 5": "SCH5"
    }
    
    try:
        # Leggi i dati dal foglio "Legenda"
        print_colored("Lettura dati dal foglio 'Legenda'...", Colors.CYAN)
        legenda_df = pd.read_excel(source_file, sheet_name="Legenda")
        
        # Crea dizionari per il lookup
        incarichi_lookup = {}
        ruolo_lookup = {}
        
        for _, row in legenda_df.iterrows():
            # Lookup per "Descrizione INCARICHI ECONOMICI"
            if not pd.isna(row.get('Descrizione INCARICHI ECONOMICI')):
                descrizione_incarico = str(row['Descrizione INCARICHI ECONOMICI']).strip()
                tipo_scheda = str(row['Tipo Scheda']).strip() if not pd.isna(row.get('Tipo Scheda')) else ''
                descrizione_scheda = str(row['Descrizione Scheda']).strip() if not pd.isna(row.get('Descrizione Scheda')) else ''
                
                if descrizione_incarico not in incarichi_lookup:
                    incarichi_lookup[descrizione_incarico] = {
                        'TipoScheda': tipo_scheda,
                        'DescrizioneScheda': descrizione_scheda
                    }
            
            # Lookup per "Ruolo GZOOM"
            if not pd.isna(row.get('Ruolo GZOOM')):
                ruolo_gzoom = str(row['Ruolo GZOOM']).strip()
                descrizione_scheda_ruolo = str(row['Descrizione Scheda']).strip() if not pd.isna(row.get('Descrizione Scheda')) else ''
                
                if ruolo_gzoom not in ruolo_lookup:
                    ruolo_lookup[ruolo_gzoom] = descrizione_scheda_ruolo
        
        print_colored(f"Trovati {len(incarichi_lookup)} incarichi economici nella Legenda.", Colors.GREEN)
        print_colored(f"Trovati {len(ruolo_lookup)} ruoli GZOOM nella Legenda.", Colors.GREEN)
        
        # Leggi i dati dal foglio "Template Dipendenti AORN"
        print_colored("Lettura dati dal foglio 'Template Dipendenti AORN'...", Colors.CYAN)
        dipendenti_df = pd.read_excel(source_file, sheet_name="Template Dipendenti AORN")
        
        print_colored(f"Trovati {len(dipendenti_df)} righe totali nel file sorgente.", Colors.GREEN)
        
        # Valori di default
        default_contesto = "IND"
        default_stato = "WEEVALST_PLANINIT"
        default_descrizione = "Scheda valutazione Performance Anno 2025"
        
        # Crea la struttura dati per l'export
        output_data = []
        
        # Per ogni dipendente, crea la scheda corrispondente
        for _, row in dipendenti_df.iterrows():
            # Salta righe vuote
            if pd.isna(row.get('Matricola')) or str(row.get('Matricola')).strip() == '':
                continue
            
            # Estrai i dati
            matricola = str(row['Matricola']).strip() if not pd.isna(row.get('Matricola')) else ''
            nome = str(row['Nome']).strip() if not pd.isna(row.get('Nome')) else ''
            cognome = str(row['Cognome']).strip() if not pd.isna(row.get('Cognome')) else ''
            codice_uoc = str(row['Codice UOC']).strip() if not pd.isna(row.get('Codice UOC')) else ''
            matricola_valutatore = str(row['Matricola Referente Valutatore']).strip() if not pd.isna(row.get('Matricola Referente Valutatore')) else ''
            descrizione_incarico = str(row['Descrizione INCARICHI ECONOMICI']).strip() if not pd.isna(row.get('Descrizione INCARICHI ECONOMICI')) else ''
            tipo_scheda = str(row['Tipo Scheda']).strip() if not pd.isna(row.get('Tipo Scheda')) else ''
            ruolo_gzoom = str(row['Ruolo GZOOM']).strip() if not pd.isna(row.get('Ruolo GZOOM')) else ''
            
            # Formatta le date senza orario
            decorrenza = ''
            if not pd.isna(row.get('Decorrenza')):
                if isinstance(row['Decorrenza'], datetime):
                    decorrenza = row['Decorrenza'].strftime('%d/%m/%Y')
                else:
                    decorrenza = str(row['Decorrenza']).strip()
            
            scadenza = ''
            if not pd.isna(row.get('Scadenza')):
                if isinstance(row['Scadenza'], datetime):
                    scadenza = row['Scadenza'].strftime('%d/%m/%Y')
                else:
                    scadenza = str(row['Scadenza']).strip()
            
            # Genera il Codice Scheda
            codice_scheda = f"SCH_{matricola}"
            
            # Lookup nella Legenda per ottenere la Descrizione Scheda tramite "Ruolo GZOOM"
            descrizione_scheda_ruolo = ''
            if ruolo_gzoom and ruolo_gzoom in ruolo_lookup:
                descrizione_scheda_ruolo = ruolo_lookup[ruolo_gzoom]
            
            # Applica il mapping del Codice Template
            codice_template = ''
            if tipo_scheda and tipo_scheda in template_mapping:
                codice_template = template_mapping[tipo_scheda]
            else:
                codice_template = tipo_scheda
            
            # Genera il Nome Scheda
            nome_scheda = f"{cognome} {nome} ({matricola})"
            if descrizione_scheda_ruolo:
                nome_scheda += f" - {descrizione_scheda_ruolo}"
            
            # Crea l'oggetto con tutti i campi mappati
            output_data.append({
                'Contesto': default_contesto,
                'Codice Scheda': codice_scheda,
                'Nome Scheda': nome_scheda,
                'Matricola Valutato': matricola,
                'Matricola Valutatore': matricola_valutatore,
                'Codice UOC': codice_uoc,
                'Codice Template': codice_template,
                'Data Inizio': decorrenza,
                'Data Fine': scadenza,
                'Stato': default_stato,
                'Descrizione': default_descrizione
            })
        
        print_colored(f"Totale schede da scrivere: {len(output_data)}", Colors.GREEN)
        
        # Esporta i dati nel file di destinazione
        print_colored(f"Scrittura dati nel file {target_file} (sheet 'SCHEDE')...", Colors.CYAN)
        
        df_output = pd.DataFrame(output_data)
        
        with pd.ExcelWriter(target_file, engine='openpyxl', mode='w') as writer:
            df_output.to_excel(writer, sheet_name='SCHEDE', index=False)
        
        print_colored(f"COMPLETATO! File {target_file} generato con successo.", Colors.GREEN)
        print_colored(f"Totale schede scritte: {len(output_data)}", Colors.GREEN)
        
    except Exception as e:
        print_colored(f"ERRORE durante l'elaborazione: {str(e)}", Colors.RED)
        import traceback
        traceback.print_exc()
        sys.exit(1)

if __name__ == "__main__":
    main()
