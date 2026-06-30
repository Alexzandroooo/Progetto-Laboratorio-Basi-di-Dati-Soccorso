-- ====================================================================
-- 1. CREAZIONE DATABASE E UTENTI
-- ====================================================================
DROP DATABASE IF EXISTS Soccorso;
CREATE DATABASE Soccorso;
USE Soccorso;

DROP USER IF EXISTS 'soccorsoUser'@'localhost';
CREATE USER 'soccorsoUser'@'localhost' IDENTIFIED BY 'soccorsoPwd1234!';
GRANT SELECT, INSERT, UPDATE, DELETE, EXECUTE ON Soccorso.* TO 'soccorsoUser'@'localhost';

-- ====================================================================
-- 2. STRUTTURA DELLE TABELLE
-- ====================================================================

CREATE TABLE patente (
    tipo VARCHAR(15) NOT NULL,
    PRIMARY KEY (tipo)
);

CREATE TABLE abilita (
    ID INTEGER UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    CONSTRAINT abilita_distinta UNIQUE (nome)
);

CREATE TABLE utente (
    ID INTEGER UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    cognome VARCHAR(100) NOT NULL,
    CF CHAR(16) NOT NULL,
    email VARCHAR(150) NOT NULL,
    hash_password VARCHAR(255) NOT NULL,
    ruolo ENUM('amministratore', 'operatore') NOT NULL DEFAULT 'operatore',
    CONSTRAINT utente_cf_distinto UNIQUE (CF),
    CONSTRAINT utente_email_distinta UNIQUE (email)
);

CREATE TABLE ha_conseguito (
    ID_utente INTEGER UNSIGNED NOT NULL,
    tipo_patente VARCHAR(15) NOT NULL,
    PRIMARY KEY (ID_utente , tipo_patente),
    CONSTRAINT hc_utente FOREIGN KEY (ID_utente)
        REFERENCES utente (ID)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT hc_patente FOREIGN KEY (tipo_patente)
        REFERENCES patente (tipo)
        ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE ha_competenza (
    ID_utente INTEGER UNSIGNED NOT NULL,
    ID_abilita INTEGER UNSIGNED NOT NULL,
    PRIMARY KEY (ID_utente , ID_abilita),
    CONSTRAINT comp_utente FOREIGN KEY (ID_utente)
        REFERENCES utente (ID)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT comp_abilita FOREIGN KEY (ID_abilita)
        REFERENCES abilita (ID)
        ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE richiesta (
    ID INTEGER UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    `timestamp` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    coordinate VARCHAR(100) DEFAULT NULL,
    indirizzo VARCHAR(255) NOT NULL,
    token VARCHAR(255) NOT NULL,
    ip VARCHAR(45) NOT NULL,
    email_segnalante VARCHAR(150) NOT NULL,
    nome_segnalante VARCHAR(100) NOT NULL,
    stato ENUM('inviata', 'attiva', 'in corso', 'chiusa', 'annullata') NOT NULL DEFAULT 'inviata',
    CONSTRAINT richiesta_token_distinto UNIQUE (token)
);

CREATE TABLE dettaglio_richiesta (
    ID_richiesta INTEGER UNSIGNED NOT NULL PRIMARY KEY,
    foto MEDIUMBLOB DEFAULT NULL,
    descrizione TEXT NOT NULL,
    CONSTRAINT dett_richiesta FOREIGN KEY (ID_richiesta)
        REFERENCES richiesta (ID)
        ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE missione (
    codice INTEGER UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    ID_richiesta INTEGER UNSIGNED NOT NULL,
    posizione VARCHAR(255) NOT NULL,
    timestamp_iniziale DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    timestamp_finale DATETIME DEFAULT NULL,
    lvl_successo TINYINT UNSIGNED DEFAULT NULL,
    CONSTRAINT missione_richiesta FOREIGN KEY (ID_richiesta)
        REFERENCES richiesta (ID)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT controllo_livello_successo CHECK (lvl_successo IS NULL OR (lvl_successo >= 0 AND lvl_successo <= 5))
);

CREATE TABLE resoconto_missione (
    codice_missione INTEGER UNSIGNED NOT NULL PRIMARY KEY,
    obiettivo TEXT NOT NULL,
    commenti TEXT DEFAULT NULL,
    CONSTRAINT resoconto_missione FOREIGN KEY (codice_missione)
        REFERENCES missione (codice)
        ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE assegnamento (
    codice_missione INTEGER UNSIGNED NOT NULL,
    ID_utente INTEGER UNSIGNED NOT NULL,
    ruolo ENUM('caposquadra', 'operatore') NOT NULL DEFAULT 'operatore',
    PRIMARY KEY (codice_missione , ID_utente),
    CONSTRAINT ass_missione FOREIGN KEY (codice_missione)
        REFERENCES missione (codice)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT ass_utente FOREIGN KEY (ID_utente)
        REFERENCES utente (ID)
        ON DELETE NO ACTION ON UPDATE CASCADE
);

CREATE TABLE aggiornamento (
    codice_missione INTEGER UNSIGNED NOT NULL,
    `timestamp` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ID_utente INTEGER UNSIGNED NOT NULL,
    descrizione TEXT NOT NULL,
    PRIMARY KEY (codice_missione , `timestamp`),
    CONSTRAINT agg_missione FOREIGN KEY (codice_missione)
        REFERENCES missione (codice)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT agg_utente FOREIGN KEY (ID_utente)
        REFERENCES utente (ID)
        ON DELETE NO ACTION ON UPDATE CASCADE
);

CREATE TABLE mezzo (
    ID INTEGER UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    targa VARCHAR(20) NOT NULL,
    nome VARCHAR(100) NOT NULL,
    descrizione TEXT DEFAULT NULL,
    CONSTRAINT mezzo_targa_distinta UNIQUE (targa)
);

CREATE TABLE patente_richiesta (
    ID_mezzo INTEGER UNSIGNED NOT NULL,
    tipo_patente VARCHAR(15) NOT NULL,
    PRIMARY KEY (ID_mezzo , tipo_patente),
    CONSTRAINT pr_mezzo FOREIGN KEY (ID_mezzo)
        REFERENCES mezzo (ID)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT pr_patente FOREIGN KEY (tipo_patente)
        REFERENCES patente (tipo)
        ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE impiego (
    codice_missione INTEGER UNSIGNED NOT NULL,
    ID_mezzo INTEGER UNSIGNED NOT NULL,
    PRIMARY KEY (codice_missione , ID_mezzo),
    CONSTRAINT impiego_missione FOREIGN KEY (codice_missione)
        REFERENCES missione (codice)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT impiego_mezzo FOREIGN KEY (ID_mezzo)
        REFERENCES mezzo (ID)
        ON DELETE NO ACTION ON UPDATE CASCADE
);

CREATE TABLE materiale (
    ID INTEGER UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    codice VARCHAR(50) NOT NULL,
    nome VARCHAR(100) NOT NULL,
    descrizione TEXT DEFAULT NULL,
    disponibilita SMALLINT UNSIGNED NOT NULL DEFAULT 0,
    CONSTRAINT materiale_codice_distinto UNIQUE (codice)
);

CREATE TABLE abilita_richiesta (
    ID_materiale INTEGER UNSIGNED NOT NULL,
    ID_abilita INTEGER UNSIGNED NOT NULL,
    PRIMARY KEY (ID_materiale , ID_abilita),
    CONSTRAINT ar_materiale FOREIGN KEY (ID_materiale)
        REFERENCES materiale (ID)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT ar_abilita FOREIGN KEY (ID_abilita)
        REFERENCES abilita (ID)
        ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE utilizzo (
    codice_missione INTEGER UNSIGNED NOT NULL,
    ID_materiale INTEGER UNSIGNED NOT NULL,
    quantita SMALLINT UNSIGNED NOT NULL DEFAULT 1,
    PRIMARY KEY (codice_missione , ID_materiale),
    CONSTRAINT uso_missione FOREIGN KEY (codice_missione)
        REFERENCES missione (codice)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT uso_materiale FOREIGN KEY (ID_materiale)
        REFERENCES materiale (ID)
        ON DELETE NO ACTION ON UPDATE CASCADE,
    CONSTRAINT controllo_quantita_positiva CHECK (quantita > 0)
); 

-- ====================================================================
-- 3. TRIGGER E STORED PROCEDURE (Logica di Business e Vincoli)
-- ====================================================================

DELIMITER $$

-- --------------------------------------------------------------------
-- TRIGGER: Controlli di disponibilità Risorse
-- --------------------------------------------------------------------

-- Verifica che un operatore non sia già in un'altra missione attiva
DROP TRIGGER IF EXISTS trg_check_operatore_libero$$
CREATE TRIGGER trg_check_operatore_libero
BEFORE INSERT ON assegnamento
FOR EACH ROW
BEGIN
    DECLARE v_missioni_attive INT;

    SELECT COUNT(*) INTO v_missioni_attive
    FROM assegnamento a
    JOIN missione m ON a.codice_missione = m.codice
    WHERE a.ID_utente = NEW.ID_utente 
      AND m.timestamp_finale IS NULL;

    IF v_missioni_attive > 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Errore Vincolo: L''operatore selezionato è già impegnato in una missione attiva.';
    END IF;
END$$

-- Verifica che un mezzo non sia già in un'altra missione attiva
DROP TRIGGER IF EXISTS trg_check_mezzo_libero$$
CREATE TRIGGER trg_check_mezzo_libero
BEFORE INSERT ON impiego
FOR EACH ROW
BEGIN
    DECLARE v_missioni_attive INT;

    SELECT COUNT(*) INTO v_missioni_attive
    FROM impiego i
    JOIN missione m ON i.codice_missione = m.codice
    WHERE i.ID_mezzo = NEW.ID_mezzo 
      AND m.timestamp_finale IS NULL;

    IF v_missioni_attive > 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Errore Vincolo: Il mezzo selezionato è già impegnato in un altra missione attiva.';
    END IF;
END$$

-- Verifica che la richiesta di materiale non superi la giacenza attuale in magazzino
DROP TRIGGER IF EXISTS trg_check_materiale_disponibile$$
CREATE TRIGGER trg_check_materiale_disponibile
BEFORE INSERT ON utilizzo
FOR EACH ROW
BEGIN
    DECLARE v_disp_totale INT;
    DECLARE v_in_uso INT;

    SELECT disponibilita INTO v_disp_totale 
    FROM materiale 
    WHERE ID = NEW.ID_materiale;

    SELECT IFNULL(SUM(u.quantita), 0) INTO v_in_uso
    FROM utilizzo u
    JOIN missione m ON u.codice_missione = m.codice
    WHERE u.ID_materiale = NEW.ID_materiale 
      AND m.timestamp_finale IS NULL;

    IF (v_in_uso + NEW.quantita) > v_disp_totale THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Errore Vincolo: Quantità di materiale insufficiente in magazzino per soddisfare la richiesta.';
    END IF;
END$$

DROP PROCEDURE IF EXISTS avvia_missione$$

DROP PROCEDURE IF EXISTS avvia_missione$$

CREATE PROCEDURE avvia_missione(
    IN p_id_richiesta INT,
    IN p_id_caposquadra INT,
    IN p_operatori_csv VARCHAR(255),
    IN p_mezzi_csv VARCHAR(255),
    IN p_materiali_csv VARCHAR(255),
    IN p_quantita_csv VARCHAR(255)
)
BEGIN
    DECLARE v_posizione VARCHAR(255);
    DECLARE v_stato VARCHAR(20); -- Variabile per memorizzare lo stato della richiesta
    DECLARE v_codice_missione INT;
    
    DECLARE v_id_str VARCHAR(50);
    DECLARE v_id INT;
    DECLARE v_qta_str VARCHAR(50);
    DECLARE v_qta INT;
    
    -- Gestione errori con Rollback automatico
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;

    START TRANSACTION;

    -- 1. Eredità posizione e recupero stato dalla richiesta
    SELECT indirizzo, stato INTO v_posizione, v_stato 
    FROM richiesta 
    WHERE ID = p_id_richiesta;
    
    IF v_posizione IS NULL THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'ERRORE: Richiesta originaria non trovata.';
    END IF;

    -- 2. Controllo Vincolo di Stato (DEVE essere 'attiva')
    IF v_stato != 'attiva' THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'ERRORE: Impossibile avviare la missione. La richiesta associata non è in stato "attiva".';
    END IF;

    -- 3. Creazione Missione
    INSERT INTO missione (ID_richiesta, posizione, timestamp_iniziale)
    VALUES (p_id_richiesta, v_posizione, NOW());
    SET v_codice_missione = LAST_INSERT_ID();

    -- Aggiornamento dello stato della richiesta
    UPDATE richiesta SET stato = 'in corso' WHERE ID = p_id_richiesta;

    -- 4. Assegnamento Caposquadra
    IF p_id_caposquadra IS NOT NULL AND p_id_caposquadra > 0 THEN
        INSERT INTO assegnamento (ID_utente, codice_missione, ruolo)
        VALUES (p_id_caposquadra, v_codice_missione, 'caposquadra');
    END IF;

    -- 5. Inserimento Operatori
    IF p_operatori_csv IS NOT NULL AND p_operatori_csv != '' THEN
        WHILE LOCATE(',', p_operatori_csv) > 0 DO
            SET v_id_str = SUBSTRING_INDEX(p_operatori_csv, ',', 1);
            SET v_id = CAST(TRIM(v_id_str) AS UNSIGNED);
            INSERT INTO assegnamento (ID_utente, codice_missione, ruolo) VALUES (v_id, v_codice_missione, 'operatore');
            SET p_operatori_csv = SUBSTRING(p_operatori_csv, LOCATE(',', p_operatori_csv) + 1);
        END WHILE;
        IF p_operatori_csv != '' THEN
            SET v_id = CAST(TRIM(p_operatori_csv) AS UNSIGNED);
            INSERT INTO assegnamento (ID_utente, codice_missione, ruolo) VALUES (v_id, v_codice_missione, 'operatore');
        END IF;
    END IF;

    -- 6. Inserimento Mezzi (`impiego`)
    IF p_mezzi_csv IS NOT NULL AND p_mezzi_csv != '' THEN
        WHILE LOCATE(',', p_mezzi_csv) > 0 DO
            SET v_id_str = SUBSTRING_INDEX(p_mezzi_csv, ',', 1);
            SET v_id = CAST(TRIM(v_id_str) AS UNSIGNED);
            INSERT INTO impiego (codice_missione, ID_mezzo) VALUES (v_codice_missione, v_id);
            SET p_mezzi_csv = SUBSTRING(p_mezzi_csv, LOCATE(',', p_mezzi_csv) + 1);
        END WHILE;
        IF p_mezzi_csv != '' THEN
            SET v_id = CAST(TRIM(p_mezzi_csv) AS UNSIGNED);
            INSERT INTO impiego (codice_missione, ID_mezzo) VALUES (v_codice_missione, v_id);
        END IF;
    END IF;

    -- 7. Inserimento Materiali e Quantità (`utilizzo`)
    IF p_materiali_csv IS NOT NULL AND p_materiali_csv != '' THEN
        WHILE LOCATE(',', p_materiali_csv) > 0 DO
            SET v_id_str = SUBSTRING_INDEX(p_materiali_csv, ',', 1);
            SET v_id = CAST(TRIM(v_id_str) AS UNSIGNED);
            
            SET v_qta_str = SUBSTRING_INDEX(p_quantita_csv, ',', 1);
            SET v_qta = CAST(TRIM(v_qta_str) AS UNSIGNED);
            IF v_qta IS NULL OR v_qta = 0 THEN SET v_qta = 1; END IF;
            
            INSERT INTO utilizzo (codice_missione, ID_materiale, quantita) VALUES (v_codice_missione, v_id, v_qta);
            
            SET p_materiali_csv = SUBSTRING(p_materiali_csv, LOCATE(',', p_materiali_csv) + 1);
            SET p_quantita_csv = SUBSTRING(p_quantita_csv, LOCATE(',', p_quantita_csv) + 1);
        END WHILE;
        IF p_materiali_csv != '' THEN
            SET v_id = CAST(TRIM(p_materiali_csv) AS UNSIGNED);
            SET v_qta = CAST(TRIM(p_quantita_csv) AS UNSIGNED);
            IF v_qta IS NULL OR v_qta = 0 THEN SET v_qta = 1; END IF;
            INSERT INTO utilizzo (codice_missione, ID_materiale, quantita) VALUES (v_codice_missione, v_id, v_qta);
        END IF;
    END IF;

    COMMIT;
END$$


-- --------------------------------------------------------------------
-- PROCEDURA: Chiusura di una missione
-- --------------------------------------------------------------------
DROP PROCEDURE IF EXISTS chiudi_missione$$

CREATE PROCEDURE chiudi_missione (
    IN p_codice_missione INT,
    IN p_timestamp_finale DATETIME,
    IN p_lvl_successo TINYINT,
    IN p_commenti TEXT
)
BEGIN
    DECLARE v_timestamp_iniziale DATETIME;
    DECLARE v_num_capisquadra INT;
    DECLARE v_id_richiesta INT;
    DECLARE v_stato_richiesta VARCHAR(20); -- Variabile per memorizzare lo stato

    -- Recupero dati missione e stato della richiesta tramite JOIN
    SELECT m.timestamp_iniziale, m.ID_richiesta, r.stato 
    INTO v_timestamp_iniziale, v_id_richiesta, v_stato_richiesta
    FROM missione m
    JOIN richiesta r ON m.ID_richiesta = r.ID
    WHERE m.codice = p_codice_missione;

    -- Controllo Vincolo di Stato (DEVE essere 'in corso')
    IF v_stato_richiesta != 'in corso' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Errore Vincolo: Impossibile chiudere la missione. La richiesta associata non è in stato "in corso".';
    END IF;

    -- Controllo coerenza temporale
    IF p_timestamp_finale < v_timestamp_iniziale THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Errore Vincolo: Il timestamp finale non può essere antecedente al timestamp iniziale.';
    END IF;

    -- Controllo presenza caposquadra
    SELECT COUNT(*) INTO v_num_capisquadra
    FROM assegnamento
    WHERE codice_missione = p_codice_missione AND ruolo = 'caposquadra';

    IF v_num_capisquadra = 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Errore Vincolo: Impossibile chiudere la missione. Nessun caposquadra assegnato alla squadra.';
    ELSE
        -- Aggiornamento missione
        UPDATE missione
        SET timestamp_finale = p_timestamp_finale, lvl_successo = p_lvl_successo
        WHERE codice = p_codice_missione;

        -- Aggiornamento resoconto (che dovrebbe essere stato inizializzato con i task precedentemente o tramite un trigger, qui lo aggiorniamo)
        UPDATE resoconto_missione
        SET commenti = p_commenti
        WHERE codice_missione = p_codice_missione;

        -- Chiusura della richiesta associata
        UPDATE richiesta SET stato = 'chiusa' WHERE ID = v_id_richiesta;
    END IF;
END$$

DELIMITER ;
