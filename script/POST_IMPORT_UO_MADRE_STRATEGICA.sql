-- =====================================================================
-- POST-IMPORT: mappa "UO madre" per la performance strategica (UO splittate)
-- =====================================================================
-- Alcune UO sono sdoppiate a DB (stessa unita' reale rappresentata da piu' record:
-- suffissi ' C.O.' / ' REPARTO' / ' - DONNE' / ' - UOMINI' / ' DIALISI', ecc.).
-- Le split hanno una scheda CTX_BS quasi vuota (1 indicatore placeholder) e NON hanno
-- obiettivi propri: il punteggio strategico reale sta sulla UO "madre".
--
-- Le persone restano assegnate alla loro UO split (in stampa vedono REPARTO/C.O./DONNE/...),
-- ma il valore di performance organizzativa va preso dalla UO MADRE. Questa tabella di
-- mappatura (PartyRelationship type STRATPERF_MOTHER) lega: split -> madre.
--   party_id_from = UO split  |  party_id_to = UO madre  |  ruoli ORGANIZATION_UNIT
-- La consuma PerformanceOrganizzativaDS (SchedaObiettiviOrganizzativi.rptdesign).
--
-- Collegamento (dove pulito) derivabile dallo stem del codice-scheda:
--   split  OB_PF_STG_<STEM>[C]  ->  madre  OB_STG_<STEM>
-- I casi con codici disallineati (Nefrologia DIALISI, Medicina Interna) sono mappati per UO.
--
-- CASI NON MAPPATI (anomalie dati da chiarire col cliente):
--   * CENTRO ANTIVELENI R.O. (10307): esiste solo il record R.O., nessuna UO base -> non e' uno split.
--   * MEDICINA INTERNA 2 - DONNE (10334) / - UOMINI (10288): non esiste una base 'MEDICINA INTERNA 2'
--     con obiettivi -> nessuna madre valorizzabile.
--   * CENTRO GRANDI USTIONATI (madre 10225): mappata, ma la madre a DB ha 0 indicatori
--     -> il valore resta 0 finche' non vengono configurati/importati gli obiettivi della base.
--
-- Idempotente (WHERE NOT EXISTS). Rieseguibile a ogni re-import.
-- =====================================================================

-- ---------- Tipo di relazione dedicato ----------
INSERT INTO party_relationship_type
       (party_relationship_type_id, party_relationship_name, description,
        role_type_id_valid_from, role_type_id_valid_to,
        last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
SELECT 'STRATPERF_MOTHER', 'UO madre performance strategica',
       'La UO_from (split) eredita il punteggio strategico dalla UO_to (madre)',
       'ORGANIZATION_UNIT', 'ORGANIZATION_UNIT', now(), now(), now(), now()
WHERE NOT EXISTS (SELECT 1 FROM party_relationship_type WHERE party_relationship_type_id='STRATPERF_MOTHER');

-- ---------- Ruolo ORGANIZATION_UNIT per le UO coinvolte (idempotente, evita FK) ----------
INSERT INTO party_role (party_id, role_type_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
SELECT p, 'ORGANIZATION_UNIT', now(), now(), now(), now()
FROM (VALUES
       ('10291'),('10292'),('10212'),
       ('10280'),('10176'),('10293'),('10208'),('10301'),('10223'),('10300'),('10220'),
       ('10281'),('10177'),('10298'),('10226'),('10299'),('10219'),('10295'),('10218'),
       ('10297'),('10222'),('10296'),('10221'),('10308'),('10235'),('10302'),('10225'),
       ('10304'),('10204'),('10335'),('10206'),('10287'),('10333'),('10195'),('10286')
     ) AS x(p)
WHERE NOT EXISTS (SELECT 1 FROM party_role r WHERE r.party_id=x.p AND r.role_type_id='ORGANIZATION_UNIT');

-- ---------- Mappatura split -> madre ----------
INSERT INTO party_relationship
       (party_id_from, role_type_id_from, party_id_to, role_type_id_to, from_date,
        party_relationship_type_id, comments,
        last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
SELECT v.split_id, 'ORGANIZATION_UNIT', v.mother_id, 'ORGANIZATION_UNIT', TIMESTAMP '2026-01-01 00:00:00',
       'STRATPERF_MOTHER', v.note, now(), now(), now(), now()
FROM (VALUES
        -- OSTETRICIA E GINECOLOGIA (REPARTO + C.O.) -> P.S. OSTETRICIA
        ('10291','10212','Ostetricia Reparto -> P.S. Ostetricia'),
        ('10292','10212','Ostetricia C.O. -> P.S. Ostetricia'),
        -- C.O. -> base (stem codice)
        ('10280','10176','Chirurgia Urgenza C.O.'),
        ('10293','10208','Chirurgia Pancreatica C.O.'),
        ('10301','10223','Chirurgia Maxillo Facciale C.O.'),
        ('10300','10220','Chirurgia Vascolare C.O.'),
        ('10281','10177','Neurochirurgia Urgenza C.O.'),
        ('10298','10226','Oculistica C.O.'),
        ('10299','10219','Ortopedia 1 C.O.'),
        ('10295','10218','Ortopedia 2 C.O.'),
        ('10297','10222','Otorinolaringoiatria C.O.'),
        ('10296','10221','Urologia C.O.'),
        ('10308','10235','Terapia Intensiva Trapianti C.O. -> Fegato'),
        ('10302','10225','Centro Grandi Ustionati C.O. [madre senza obiettivi a DB]'),
        -- Nefrologia DIALISI -> base Nefrologia
        ('10304','10204','Nefrologia Dialisi'),
        -- Medicina Interna 1 e 3 (DONNE/UOMINI) -> base
        ('10335','10206','Medicina Interna 1 - Donne'),
        ('10287','10206','Medicina Interna 1 - Uomini'),
        ('10333','10195','Medicina Interna 3 - Donne'),
        ('10286','10195','Medicina Interna 3 - Uomini')
     ) AS v(split_id, mother_id, note)
WHERE NOT EXISTS (
        SELECT 1 FROM party_relationship pr
        WHERE pr.party_id_from = v.split_id AND pr.party_id_to = v.mother_id
          AND pr.role_type_id_from = 'ORGANIZATION_UNIT' AND pr.role_type_id_to = 'ORGANIZATION_UNIT'
          AND pr.party_relationship_type_id = 'STRATPERF_MOTHER');

-- ---------- Verifica ----------
SELECT pr.party_id_from AS split_id, sf.group_name AS split_uo,
       pr.party_id_to AS mother_id, st.group_name AS mother_uo,
       (SELECT COALESCE(SUM(ate.amount),0) FROM work_effort we
          JOIN work_effort_measure wm ON wm.work_effort_id=we.work_effort_id
          JOIN acctg_trans att ON att.voucher_ref=wm.work_effort_measure_id
          JOIN acctg_trans_entry ate ON ate.acctg_trans_id=att.acctg_trans_id AND ate.gl_account_id='SCOREKPI'
        WHERE we.org_unit_id=pr.party_id_to AND we.work_effort_type_id='CTX_BS') AS madre_scorekpi
FROM party_relationship pr
JOIN party_group sf ON sf.party_id = pr.party_id_from
JOIN party_group st ON st.party_id = pr.party_id_to
WHERE pr.party_relationship_type_id = 'STRATPERF_MOTHER'
ORDER BY split_uo;
