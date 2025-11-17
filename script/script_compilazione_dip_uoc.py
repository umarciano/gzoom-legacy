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
    target_file = template_dir / "IMPORT_DIPARTIMENTO_E_UOC.xlsx"
    
    print_colored("Inizio elaborazione...", Colors.CYAN)
    
    # Verifica esistenza file sorgente
    if not source_file.exists():
        print_colored(f"ERRORE: File sorgente {source_file} non trovato!", Colors.RED)
        sys.exit(1)
    
    try:
        # Leggi i dati dal foglio "Legenda"
        print_colored(f"Lettura dati dal file {source_file} (sheet Legenda)...", Colors.CYAN)
        legenda_df = pd.read_excel(source_file, sheet_name="Legenda")
        
        # Crea un dizionario per raggruppare le UOC per Dipartimento
        dipartimenti = {}
        
        for _, row in legenda_df.iterrows():
            # Salta righe vuote
            if pd.isna(row.get('Codice Dipartimento')) or str(row.get('Codice Dipartimento')).strip() == '':
                continue
            
            codice_dip = str(row['Codice Dipartimento']).strip()
            nome_dip = str(row['Dipartimento']).strip() if not pd.isna(row.get('Dipartimento')) else ''
            resp_dip = str(row['Matricola Responsabile Dipartimento']).strip() if not pd.isna(row.get('Matricola Responsabile Dipartimento')) else ''
            codice_uoc = str(row['Codice UOC']).strip() if not pd.isna(row.get('Codice UOC')) else ''
            nome_uoc = str(row['UOC']).strip() if not pd.isna(row.get('UOC')) else ''
            resp_uoc = str(row['Matricola Responsabile UOC']).strip() if not pd.isna(row.get('Matricola Responsabile UOC')) else ''
            
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
        reference_date = "01/01/2025"
        
        # Per ogni dipartimento, crea prima la riga del dipartimento e poi le righe delle UOC
        for codice_dip in sorted(dipartimenti.keys()):
            dip = dipartimenti[codice_dip]
            
            # Aggiungi la riga del Dipartimento
            output_data.append({
                'UOC Code': dip['Codice'],
                'Description': dip['Nome'],
                'Unit Type': 'ORG',
                'Parent UOC Code': 'ORGUNIT001',
                'Parent Unit Type': 'ORG',
                'Responsible Code': dip['Responsabile'],
                'Reference Date': reference_date,
                'End date': ''
            })
            
            # Aggiungi le righe delle UOC di questo dipartimento
            for uoc in dip['UOCs']:
                output_data.append({
                    'UOC Code': uoc['Codice'],
                    'Description': uoc['Nome'],
                    'Unit Type': 'ORGANIZATION_UNIT',
                    'Parent UOC Code': dip['Codice'],
                    'Parent Unit Type': 'ORG',
                    'Responsible Code': uoc['Responsabile'],
                    'Reference Date': reference_date,
                    'End date': ''
                })
        
        print_colored(f"Totale righe da scrivere: {len(output_data)}", Colors.GREEN)
        
        # Esporta i dati nel file di destinazione
        print_colored(f"Scrittura dati nel file {target_file}...", Colors.CYAN)
        
        df_output = pd.DataFrame(output_data)
        df_output.to_excel(target_file, sheet_name='Sheet1', index=False)
        
        print_colored(f"COMPLETATO! File {target_file} generato con successo.", Colors.GREEN)
        print_colored(f"Totale righe scritte: {len(output_data)}", Colors.GREEN)
        
    except Exception as e:
        print_colored(f"ERRORE durante l'elaborazione: {str(e)}", Colors.RED)
        import traceback
        traceback.print_exc()
        sys.exit(1)

if __name__ == "__main__":
    main()
