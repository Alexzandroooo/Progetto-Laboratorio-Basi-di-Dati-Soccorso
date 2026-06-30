USE Soccorso;

-- 1. DISABILITIAMO TEMPORANEAMENTE I VINCOLI DI CHIAVE ESTERNA
-- (Questo ci permette di svuotare le tabelle in sicurezza prima di riempirle)
SET FOREIGN_KEY_CHECKS = 0;

DELETE FROM utilizzo;
DELETE FROM abilita_richiesta;
DELETE FROM materiale;
DELETE FROM impiego;
DELETE FROM patente_richiesta;
DELETE FROM mezzo;
DELETE FROM aggiornamento;
DELETE FROM assegnamento;
DELETE FROM resoconto_missione;
DELETE FROM missione;
DELETE FROM dettaglio_richiesta;
DELETE FROM richiesta;
DELETE FROM ha_competenza;
DELETE FROM ha_conseguito;
DELETE FROM utente;
DELETE FROM abilita;
DELETE FROM patente;
SET FOREIGN_KEY_CHECKS = 1;

-- ====================================================================
-- 2. POPOLAMENTO ANAGRAFICHE DI BASE E COMPETENZE
-- ====================================================================

-- Patenti Esistenti
INSERT INTO patente (tipo) VALUES 
('B'), ('BE'), ('C'), ('CE'), ('D'), ('CQC');

-- Abilitazioni Esistenti
INSERT INTO abilita (ID, nome) VALUES
(1, 'BLSD - Basic Life Support and Defibrillation'),
(2, 'PHTC - Prehospital Trauma Care'),
(3, 'Antincendio Alto Rischio'),
(4, 'Soccorso Alpino e Speleologico'),
(5, 'Guida Sicura Emergenza');

-- Utenti (1 Admin, 6 Operatori)
INSERT INTO utente (ID, nome, cognome, CF, email, hash_password, ruolo) VALUES
(1, 'Mario', 'Rossi', 'RSSMRA80A01H501A', 'admin@soccorso.it', 'hash_finto', 'amministratore'),
(2, 'Luigi', 'Verdi', 'VRDLGU85B02H501B', 'luigi.verdi@soccorso.it', 'hash_finto', 'operatore'),
(3, 'Giulia', 'Bianchi', 'BNCGLI90C43H501C', 'giulia.bianchi@soccorso.it', 'hash_finto', 'operatore'),
(4, 'Marco', 'Neri', 'NRIMRC92D04H501D', 'marco.neri@soccorso.it', 'hash_finto', 'operatore'),
(5, 'Anna', 'Gialli', 'GLLNNA88E45H501E', 'anna.gialli@soccorso.it', 'hash_finto', 'operatore'),
(6, 'Paolo', 'Marrone', 'MRRPLA95F06H501F', 'paolo.marrone@soccorso.it', 'hash_finto', 'operatore'),
(7, 'Elena', 'Blu', 'BLULNE91G47H501G', 'elena.blu@soccorso.it', 'hash_finto', 'operatore');

-- Associazione Patenti - Utenti
INSERT INTO ha_conseguito (ID_utente, tipo_patente) VALUES
(2, 'B'), (2, 'C'), (2, 'CQC'),  
(3, 'B'),                        
(4, 'B'), (4, 'C'), (4, 'CQC'),  
(5, 'B'), (5, 'C'),              
(6, 'B'),                        
(7, 'B'), (7, 'D');              

-- Associazione Abilitazioni - Utenti
INSERT INTO ha_competenza (ID_utente, ID_abilita) VALUES
(2, 1), (2, 2), (2, 5),          
(3, 1),                          
(4, 1), (4, 3),                  
(5, 1), (5, 4),                  
(6, 1),                          
(7, 1), (7, 2);                  

-- ====================================================================
-- 3. MEZZI E MATERIALI
-- ====================================================================

-- Inserimento Mezzi
INSERT INTO mezzo (ID, targa, nome, descrizione) VALUES
(1, 'CRI-12345', 'Ambulanza ALS (Avanzata)', 'Ambulanza Rianimazione con medico a bordo'),
(2, 'CRI-67890', 'Ambulanza BLS (Base)', 'Ambulanza per il soccorso base'),
(3, 'VVF-11223', 'Autopompa Serbatoio', 'Camion pompieri principale 4x4'),
(4, 'PC-99887',  'Fuoristrada Soccorso', 'Jeep 4x4 per terreni impervi');

-- Requisiti di Patente per i Mezzi
INSERT INTO patente_richiesta (ID_mezzo, tipo_patente) VALUES
(1, 'CQC'),  
(2, 'B'),    
(3, 'C'),    
(4, 'B');    

-- Inserimento Materiali 
INSERT INTO materiale (ID, codice, nome, descrizione, disponibilita) VALUES
(1, 'MAT-DEF-01', 'Defibrillatore DAE', 'Defibrillatore semiautomatico esterno', 15),
(2, 'MAT-ZAI-02', 'Zaino Trauma', 'Zaino completo per primo soccorso PHTC', 10),
(3, 'MAT-EST-03', 'Estintore CO2 5Kg', 'Estintore per principi di incendio', 30),
(4, 'MAT-COR-04', 'Set Corde Alpino', 'Imbragature, moschettoni e corde 50m', 5),
(5, 'MAT-OSS-05', 'Bombola Ossigeno 2L', 'Ossigeno medicale portatile', 20);

-- Requisiti Tecnici (Abilitazioni) per utilizzare i Materiali
INSERT INTO abilita_richiesta (ID_materiale, ID_abilita) VALUES
(1, 1), 
(2, 2), 
(3, 3), 
(4, 4); 

-- ====================================================================
-- 4. SIMULAZIONE RICHIESTE E MISSIONI
-- ====================================================================

-- Creiamo 5 Richieste
INSERT INTO richiesta (ID, timestamp, coordinate, indirizzo, token, ip, email_segnalante, nome_segnalante, stato) VALUES
(1, DATE_SUB(NOW(), INTERVAL 36 HOUR), '42.3476, 13.4045', 'Via Strinella 168, L''Aquila', 'tok_101', '192.168.1.10', 'cittadino1@mail.it', 'Aldo Baglio', 'chiusa'),
(2, DATE_SUB(NOW(), INTERVAL 35 HOUR), '42.3498, 13.3995', 'Piazza del Duomo, L''Aquila', 'tok_110', '192.168.1.20', 'cittadino1@mail.it', 'Aldo Baglio', 'chiusa'),
(3, DATE_SUB(NOW(), INTERVAL 24 HOUR), '42.3498, 13.3995', 'Piazza del Duomo, L''Aquila', 'tok_111', '192.168.1.10', 'cittadino1@mail.it', 'Aldo Baglio', 'annullata'),
(4, DATE_SUB(NOW(), INTERVAL 2 HOUR), '42.3550, 13.4000', 'Via Roma 15, L''Aquila', 'tok_222', '192.168.1.11', 'cittadino2@mail.it', 'Giovanni Storti', 'in corso'),
(5, DATE_SUB(NOW(), INTERVAL 10 MINUTE), '42.3600, 13.4050', 'Viale della Croce Rossa, L''Aquila', 'tok_333', '192.168.1.12', 'cittadino3@mail.it', 'Giacomo Poretti', 'attiva');

-- Dettagli per le richieste
INSERT INTO dettaglio_richiesta (ID_richiesta, foto, descrizione) VALUES
(1, NULL, 'Malore improvviso in piazza, la persona è svenuta a terra.'),
(2, NULL, 'Incidente tra due auto, fumo dal motore.'),
(3, NULL, 'Malore improvviso in piazza, la persona è svenuta a terra.'),
(4, NULL, 'Incidente tra due auto, fumo dal motore.'),
(5, NULL, 'Incidente in bicicletta, sospetta frattura alla gamba.');

-- ====================================================================
-- 5. CREAZIONE MISSIONI E LOGICA DI ESECUZIONE
-- ====================================================================

-- --------------------------------------------------------------------
-- MISSIONE 1 (CHIUSA, SUCCESSO 5) -> Collegata alla richiesta 1 
-- --------------------------------------------------------------------
INSERT INTO missione (codice, ID_richiesta, posizione, timestamp_iniziale, timestamp_finale, lvl_successo) VALUES
(1, 1, 'Via Strinella 168, L''Aquila', DATE_SUB(NOW(), INTERVAL 24 HOUR), DATE_SUB(NOW(), INTERVAL 22 HOUR), 5);

INSERT INTO resoconto_missione (codice_missione, obiettivo, commenti) VALUES
(1, 'Intervento soccorso persona svenuta', 'Paziente rianimato con DAE e trasportato in ospedale in codice giallo. Intervento riuscito.');

INSERT INTO assegnamento (codice_missione, ID_utente, ruolo) VALUES
(1, 2, 'caposquadra'),  
(1, 3, 'operatore');    

INSERT INTO impiego (codice_missione, ID_mezzo) VALUES (1, 2); 
INSERT INTO utilizzo (codice_missione, ID_materiale, quantita) VALUES (1, 1, 1); 


-- --------------------------------------------------------------------
-- MISSIONE 2 (CHIUSA, SUCCESSO 4) -> Collegata alla richiesta 2 
-- --------------------------------------------------------------------
INSERT INTO missione (codice, ID_richiesta, posizione, timestamp_iniziale, timestamp_finale, lvl_successo) VALUES
(2, 2, 'Piazza del Duomo, L''Aquila', DATE_SUB(NOW(), INTERVAL 34 HOUR), DATE_SUB(NOW(), INTERVAL 32 HOUR), 4);

INSERT INTO resoconto_missione (codice_missione, obiettivo, commenti) VALUES
(2, 'Messa in sicurezza area per fumo da veicolo', 'Incidente risolto, applicato estintore a polvere per principio d''incendio nel vano motore. Nessun ferito grave.');

INSERT INTO assegnamento (codice_missione, ID_utente, ruolo) VALUES
(2, 7, 'caposquadra'),  
(2, 6, 'operatore');    

INSERT INTO impiego (codice_missione, ID_mezzo) VALUES (2, 2); 
INSERT INTO utilizzo (codice_missione, ID_materiale, quantita) VALUES (2, 3, 1); 


-- --------------------------------------------------------------------
-- MISSIONE 3 (IN CORSO) -> Collegata alla richiesta 4 (in corso)
-- --------------------------------------------------------------------
INSERT INTO missione (codice, ID_richiesta, posizione, timestamp_iniziale, timestamp_finale, lvl_successo) VALUES
(3, 4, 'Via Roma 15, L''Aquila', DATE_SUB(NOW(), INTERVAL 115 MINUTE), NULL, NULL);

INSERT INTO resoconto_missione (codice_missione, obiettivo, commenti) VALUES
(3, 'Soccorso incidente stradale grave', NULL);

INSERT INTO assegnamento (codice_missione, ID_utente, ruolo) VALUES
(3, 4, 'caposquadra'),  
(3, 5, 'operatore');    

INSERT INTO impiego (codice_missione, ID_mezzo) VALUES 
(3, 1),  
(3, 3);  

INSERT INTO utilizzo (codice_missione, ID_materiale, quantita) VALUES 
(3, 3, 2), 
(3, 2, 1); 

INSERT INTO aggiornamento (codice_missione, timestamp, ID_utente, descrizione) VALUES
(3, DATE_SUB(NOW(), INTERVAL 60 MINUTE), 4, 'Arrivati sul posto. Iniziato spegnimento auto e messa in sicurezza.'),
(3, DATE_SUB(NOW(), INTERVAL 30 MINUTE), 5, 'Paziente estratto e stabilizzato, parametri vitali nella norma, attendiamo rientro.');
