#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Genera `Template_Dipendenti_AORN.xlsx` a partire da un Excel di input fornito.

Uso:
  python script_generazione_template_dipendenti.py input_file.xlsx

Mapping richiesto dall'utente:
Input columns expected:
  MATRICOLA, DIPENDENTE, Nome, Cognome, CF, DESC QUALIFICA, Scheda,
  DATA ASSUNZIONE, DATA CESSAZIONE, UNITA OPERATIVA, CdC, CdC_new,
  DATA INIZIO, DATA FINE, Periodo completo, Periodo completo FINE,
  Occorrenze, NOTE, Mt. valutatore, Valutatore, Dipartimento

Output columns (Template_Dipendenti_AORN):
  Codifica, Matricola, Cognome, Nome, Codice Fiscale,
  Descrizione INCARICHI ECONOMICI, Ruolo GZOOM, Tipo Scheda,
  Codice UOC, Nome UOC, Codice Dipartimento, Nome Dipartimento,
  Descrizione ESCLUSIVO, Decorrenza, Scadenza, Data di Nascita,
  Email, Username, Matricola Referente Valutatore

Nota: i campi contrassegnati come "autocompilato" sono lasciati vuoti
e nel codice è aggiunto un commento "# TODO: COMPLETARE" per indicare
che vanno popolati con regole specifiche.
"""

import sys
from pathlib import Path
import re
import unicodedata
import pandas as pd
from datetime import datetime


CONFIG = {
    'TEMPLATE_DIR': 'templates',
    'OUTPUT_FILE': 'Template_Dipendenti_AORN.xlsx',
    'OUTPUT_SHEET': 'Template Dipendenti AORN',
}


def clean_value(v):
    if pd.isna(v):
        return ''
    s = str(v).strip()
    if s.endswith('.0'):
        try:
            float(s)
            return s[:-2]
        except ValueError:
            return s
    return s


def format_date(v):
    if pd.isna(v) or v == '':
        return ''
    if isinstance(v, datetime):
        return v.strftime('%d/%m/%Y')
    # try parse
    s = str(v).strip()
    # if already in dd/mm/yyyy-ish, return
    return s


def _username_part(s: str) -> str:
    """Normalize a name part for username: remove accents, spaces, punctuation, lowercase."""
    if not s:
        return ''
    s = clean_value(s)
    if not s:
        return ''
    # remove accents
    s = unicodedata.normalize('NFKD', s)
    s = s.encode('ascii', 'ignore').decode('ascii')
    s = s.lower()
    # remove spaces and non-alphanumeric
    s = re.sub(r'\s+', '', s)
    s = re.sub(r'[^a-z0-9]', '', s)
    return s


def _sanitize_for_excel(s: str) -> str:
    """Prevent Excel from interpreting strings as formulas by prefixing a single quote
    when the string starts with characters that can trigger formula parsing.
    """
    if s is None:
        return ''
    try:
        if not isinstance(s, str):
            s = str(s)
    except Exception:
        s = ''
    if s == '':
        return s
    # If string starts with '=' or '-' remove those leading chars (user requested)
    s = s.lstrip()
    s = re.sub(r'^[=\-]+', '', s)
    return s


def main():
    if len(sys.argv) < 2:
        print("Usage: python script_generazione_template_dipendenti.py <input_excel>")
        sys.exit(1)

    input_path = Path(sys.argv[1])
    script_dir = Path(__file__).parent
    template_dir = script_dir / CONFIG['TEMPLATE_DIR']
    template_dir.mkdir(parents=True, exist_ok=True)

    output_file = template_dir / CONFIG['OUTPUT_FILE']
    # Se è passato un secondo argomento, usalo come percorso di output alternativo
    if len(sys.argv) >= 3 and sys.argv[2].strip():
        output_file = Path(sys.argv[2])

    if not input_path.exists():
        print(f"Errore: file di input {input_path} non trovato")
        sys.exit(1)

    # Leggi il primo foglio dell'input
    df_in = pd.read_excel(input_path, sheet_name=0)

    # Prepara lista righe di output
    rows = []

    for _, r in df_in.iterrows():
        # salta righe senza matricola
        matricola = clean_value(r.get('MATRICOLA') or r.get('Matricola') or r.get('matricola'))
        if matricola == '':
            continue

        cognome = clean_value(r.get('Cognome') or r.get('COGNOME') or r.get('cognome'))
        nome = clean_value(r.get('Nome') or r.get('NOME') or r.get('nome'))
        cf = clean_value(r.get('CF') or r.get('Codice Fiscale') or r.get('CF'))
        desc_qualifica = clean_value(r.get('DESC QUALIFICA') or r.get('Desc Qualifica') or r.get('DESC_QUALIFICA'))

        # campi autocompilati: lasciare vuoti e segnare TODO nel codice
        # TODO: COMPLETARE -> Ruolo GZOOM
        ruolo_gzoom = ''
        # TODO: COMPLETARE -> Tipo Scheda
        tipo_scheda = ''
        # Codice UOC prende da CdC_new
        codice_uoc = clean_value(r.get('CdC_new') or r.get('CDC_NEW') or r.get('CdC_new'))
        # TODO: COMPLETARE -> Nome UOC
        nome_uoc = ''
        # TODO: COMPLETARE -> Codice Dipartimento
        codice_dip = ''
        # TODO: COMPLETARE -> Nome Dipartimento
        nome_dip = ''

        descrizione_esclusivo = 'RAPPORTO ESCLUSIVO'

        # Decorrenza / Scadenza: usa 'Periodo completo' e 'Periodo completo FINE' se presenti,
        # altrimenti usa 'DATA INIZIO' e 'DATA FINE'
        decorrenza = ''
        scadenza = ''
        if 'Periodo completo' in df_in.columns:
            decorrenza = format_date(r.get('Periodo completo'))
        if 'Periodo completo FINE' in df_in.columns:
            scadenza = format_date(r.get('Periodo completo FINE'))
        if not decorrenza and ('DATA INIZIO' in df_in.columns or 'Data Inizio' in df_in.columns):
            decorrenza = format_date(r.get('DATA INIZIO') or r.get('Data Inizio'))
        if not scadenza and ('DATA FINE' in df_in.columns or 'Data Fine' in df_in.columns):
            scadenza = format_date(r.get('DATA FINE') or r.get('Data Fine'))

        data_nascita = ''
        email = ''
        # Username: nome.cognome, minuscolo, senza spazi e senza accentazioni
        nome_for_un = nome
        cognome_for_un = cognome
        part_nome = _username_part(nome_for_un)
        part_cognome = _username_part(cognome_for_un)
        if part_nome and part_cognome:
            username = f"{part_nome}.{part_cognome}"
        elif part_nome:
            username = part_nome
        elif part_cognome:
            username = part_cognome
        else:
            username = ''

        # Matricola referente valutatore: preferisci Mt. valutatore o 'MT. VALUTATORE'
        mat_val = clean_value(r.get('Mt. valutatore') or r.get('MT. VALUTATORE') or r.get('Mt valutatore') or r.get('Mt_valutatore'))

        row_out = {
            'Codifica': 'd',
            'Matricola': matricola,
            'Cognome': cognome,
            'Nome': nome,
            'Codice Fiscale': cf,
            'Descrizione INCARICHI ECONOMICI': desc_qualifica,
            'Ruolo GZOOM': ruolo_gzoom,  # TODO: COMPLETARE
            'Tipo Scheda': tipo_scheda,  # TODO: COMPLETARE
            'Codice UOC': codice_uoc,
            'Nome UOC': nome_uoc,        # TODO: COMPLETARE
            'Codice Dipartimento': codice_dip,  # TODO: COMPLETARE
            'Nome Dipartimento': nome_dip,      # TODO: COMPLETARE
            'Descrizione ESCLUSIVO': descrizione_esclusivo,
            'Decorrenza': decorrenza,
            'Scadenza': scadenza,
            'Data di Nascita': data_nascita,
            'Email': email,
            'Username': username,
            'Matricola Referente Valutatore': mat_val
        }

        rows.append(row_out)

    if not rows:
        print('Nessuna riga valida trovata nel file di input.')
        sys.exit(0)

    df_out = pd.DataFrame(rows)

    # Forza tutte le colonne a stringa per evitare conversioni indesiderate in Excel
    for c in df_out.columns:
        df_out[c] = df_out[c].astype(str)

    # Sanitizza le celle per evitare formule accidentali in Excel (es. valori che iniziano con '=')
    df_out = df_out.applymap(lambda x: _sanitize_for_excel(x))

    # Scrivi il file di output con fallback in caso di PermissionError
    try:
        df_out.to_excel(output_file, sheet_name=CONFIG['OUTPUT_SHEET'], index=False, engine='openpyxl')
        print(f"Generato file: {output_file} (righe: {len(df_out)})")
    except PermissionError:
        fallback = output_file.with_name(output_file.stem + '_generated' + output_file.suffix)
        try:
            df_out.to_excel(fallback, sheet_name=CONFIG['OUTPUT_SHEET'], index=False, engine='openpyxl')
            print(f"File originale bloccato. Generato file alternativo: {fallback} (righe: {len(df_out)})")
        except Exception as e:
            print(f"Errore scrittura file di output: {e}")
            raise


if __name__ == '__main__':
    main()
