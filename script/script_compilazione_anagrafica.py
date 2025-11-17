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
    target_file = template_dir / "IMPORT_RISORSE_UMANE.xlsx"
    
    print_colored("Inizio elaborazione...", Colors.CYAN)
    
    # Verifica esistenza file sorgente
    if not source_file.exists():
        print_colored(f"ERRORE: File sorgente {source_file} non trovato!", Colors.RED)
        sys.exit(1)
    
    try:
        # Leggi i dati dal foglio "Template Dipendenti AORN"
        print_colored(f"Lettura dati dal file {source_file} (sheet 'Template Dipendenti AORN')...", Colors.CYAN)
        dipendenti_df = pd.read_excel(source_file, sheet_name="Template Dipendenti AORN")
        
        print_colored(f"Trovati {len(dipendenti_df)} righe totali nel file sorgente.", Colors.GREEN)
        
        # Crea un array con tutte le matricole dei valutatori
        valutatori = set()
        for _, row in dipendenti_df.iterrows():
            if not pd.isna(row.get('Matricola Referente Valutatore')):
                matricola_val = str(row['Matricola Referente Valutatore']).strip()
                if matricola_val:
                    valutatori.add(matricola_val)
        
        print_colored(f"Identificati {len(valutatori)} valutatori unici.", Colors.GREEN)
        
        # Valori di default
        default_date = "01/01/2025"
        default_employment_amount = "1"
        default_employment_org_role_type = "ORGANIZATION_UNIT"
        default_data_source = "IMPORT_HR"
        
        # Crea la struttura dati per l'export
        output_data = []
        
        # Per ogni dipendente, crea la riga corrispondente
        for _, row in dipendenti_df.iterrows():
            # Salta righe vuote
            if pd.isna(row.get('Matricola')) or str(row.get('Matricola')).strip() == '':
                continue
            
            # Estrai i dati
            matricola = str(row['Matricola']).strip() if not pd.isna(row.get('Matricola')) else ''
            nome = str(row['Nome']).strip() if not pd.isna(row.get('Nome')) else ''
            cognome = str(row['Cognome']).strip() if not pd.isna(row.get('Cognome')) else ''
            codice_fiscale = str(row['Codice Fiscale']).strip() if not pd.isna(row.get('Codice Fiscale')) else ''
            ruolo_gzoom = str(row['Ruolo GZOOM']).strip() if not pd.isna(row.get('Ruolo GZOOM')) else ''
            codice_uoc = str(row['Codice UOC']).strip() if not pd.isna(row.get('Codice UOC')) else ''
            nome_uoc = str(row['Nome UOC']).strip() if not pd.isna(row.get('Nome UOC')) else ''
            matricola_valutatore = str(row['Matricola Referente Valutatore']).strip() if not pd.isna(row.get('Matricola Referente Valutatore')) else ''
            email = str(row['Email']).strip() if not pd.isna(row.get('Email')) else ''
            username = str(row['Username']).strip() if not pd.isna(row.get('Username')) else ''
            
            # Determina se questo dipendente è un Evaluation Manager
            is_evaluation_manager = 'Y' if matricola in valutatori else 'N'
            
            # Determina il Group Profile ID
            group_profile_id = 'EMPLPERF_VALUTATORE' if is_evaluation_manager == 'Y' else 'EMPLPERF_VALUTATO'
            
            # Crea l'oggetto con tutti i campi mappati
            output_data.append({
                'Person Code': matricola,
                'First Name': nome,
                'Last Name': cognome,
                'Fiscal Code': codice_fiscale,
                'Person Role Type': ruolo_gzoom,
                'Employment Position Type': ruolo_gzoom,
                'Qualification From Date': default_date,
                'Employment Amount': default_employment_amount,
                'Employment Start Date': default_date,
                'Employment End Date': '',
                'Employment Org Code': codice_uoc,
                'Employment Org Role Type': default_employment_org_role_type,
                'Employment Org Description': nome_uoc,
                'Employment Org Comments': '',
                'Employment Org From Date': default_date,
                'Employment Org End Date': '',
                'Evaluator Code': matricola_valutatore,
                'Evaluator From Date': default_date,
                'Allocation Org Code': '',
                'Allocation Org Role Type': '',
                'Allocation Org Description': '',
                'Allocation Org Comments': '',
                'Allocation Org From Date': '',
                'Allocation Org End Date': '',
                'Is Evaluation Manager': is_evaluation_manager,
                'Approver Code': matricola_valutatore,
                'Email': email,
                'Mobile Phone': '',
                'User Login ID': username,
                'Group Profile ID': group_profile_id,
                'Work Effort Assignment Code': '',
                'Work Effort Date': '',
                'Employment Position Type Date': default_date,
                'Description': '',
                'Comments': '',
                'Reference Date': default_date,
                'Data Source': default_data_source
            })
        
        print_colored(f"Totale righe da scrivere: {len(output_data)}", Colors.GREEN)
        
        # Esporta i dati nel file di destinazione
        print_colored(f"Scrittura dati nel file {target_file}...", Colors.CYAN)
        
        df_output = pd.DataFrame(output_data)
        df_output.to_excel(target_file, sheet_name='Sheet1', index=False)
        
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
