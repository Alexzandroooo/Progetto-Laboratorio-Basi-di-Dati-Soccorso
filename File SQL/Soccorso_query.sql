USE Soccorso;

-- ====================================================================
-- FASE 7: IMPLEMENTAZIONE DELLE OPERAZIONI RICHIESTE (QUERY)
-- ====================================================================

-- --------------------------------------------------------------------
-- 1. Inserimento di una richiesta di soccorso.
-- --------------------------------------------------------------------
INSERT INTO richiesta (indirizzo, coordinate, token, ip, email_segnalante, nome_segnalante) 
VALUES ('Via Falsa 123, Roma', '41.9028, 12.4964', 'tok_abc123', '192.168.1.100', 'utente@mail.com', 'Mario Rossi');

INSERT INTO dettaglio_richiesta (ID_richiesta, descrizione, foto) 
VALUES (LAST_INSERT_ID(), 'Incidente stradale grave', NULL);


-- --------------------------------------------------------------------
-- 2. Creazione di una missione connessa a una richiesta di soccorso attiva.
-- --------------------------------------------------------------------
CALL avvia_missione(3, 2, 'op1,op2', 'mezzo1', 'mat1', '2');


-- --------------------------------------------------------------------
-- 3. Chiusura di una missione.
-- --------------------------------------------------------------------
CALL chiudi_missione(2, '2026-06-24 20:30:00', 4, 'Intervento completato con successo. Danni limitati.');


-- --------------------------------------------------------------------
-- 4. Estrazione della lista degli operatori non coinvolti in missioni in corso.
-- --------------------------------------------------------------------
SELECT u.ID, u.nome, u.cognome
FROM utente u
WHERE u.ruolo = 'operatore'
  AND u.ID NOT IN (
      SELECT a.ID_utente
      FROM assegnamento a
      JOIN missione m ON a.codice_missione = m.codice
      WHERE m.timestamp_finale IS NULL
  );


-- --------------------------------------------------------------------
-- 5. Calcolo del numero di missioni svolte da un operatore.
-- --------------------------------------------------------------------
SELECT a.ID_utente, u.nome, u.cognome, COUNT(a.codice_missione) AS numero_missioni_svolte
FROM assegnamento a
JOIN utente u ON a.ID_utente = u.ID
WHERE u.ID = 1 -- <-- INSERIRE QUI L'ID DELL'UTENTE
GROUP BY a.ID_utente, u.nome, u.cognome;

-- --------------------------------------------------------------------
-- 6. Calcolo del tempo medio di svolgimento delle missioni in un 
--    anno specifico e per ciascun caposquadra.
-- --------------------------------------------------------------------
-- QUERY 6A: In un anno specifico (Es: 2026) espresso in minuti.
SELECT AVG((UNIX_TIMESTAMP(timestamp_finale) - UNIX_TIMESTAMP(timestamp_iniziale)) / 60) AS tempo_medio_minuti_anno
FROM missione
WHERE YEAR(timestamp_iniziale) = 2026 
  AND timestamp_finale IS NOT NULL;

-- QUERY 6B: Per ciascun caposquadra.
SELECT u.ID, u.nome, u.cognome, 
       AVG((UNIX_TIMESTAMP(m.timestamp_finale) - UNIX_TIMESTAMP(m.timestamp_iniziale)) / 60) AS tempo_medio_minuti
FROM assegnamento a
JOIN missione m ON a.codice_missione = m.codice
JOIN utente u ON a.ID_utente = u.ID
WHERE a.ruolo = 'caposquadra' 
  AND m.timestamp_finale IS NOT NULL
GROUP BY u.ID, u.nome, u.cognome;


-- --------------------------------------------------------------------
-- 7. Calcolo del numero di richieste provenienti da un certo soggetto segnalante
--    o da un certo indirizzo IP nelle ultime 36 ore.
-- --------------------------------------------------------------------
-- QUERY 7A: Per soggetto segnalante (Identificato dall'email)
SELECT COUNT(*) AS numero_richieste
FROM richiesta
WHERE email_segnalante = 'cittadino1@aq.it'
  AND timestamp >= DATE_SUB(NOW(), INTERVAL 36 HOUR);

-- QUERY 7B: Per indirizzo IP
SELECT COUNT(*) AS numero_richieste
FROM richiesta
WHERE ip = '185.25.44.12'
  AND timestamp >= DATE_SUB(NOW(), INTERVAL 36 HOUR);


-- --------------------------------------------------------------------
-- 8. Calcolo del tempo totale di impiego in missione di un certo operatore.
-- --------------------------------------------------------------------
SELECT a.ID_utente, u.nome, u.cognome, 
       SUM((UNIX_TIMESTAMP(m.timestamp_finale) - UNIX_TIMESTAMP(m.timestamp_iniziale)) / 3600) AS ore_totali_impiego
FROM assegnamento a
JOIN missione m ON a.codice_missione = m.codice
JOIN utente u ON a.ID_utente = u.ID
WHERE u.ID = 2 -- <-- INSERIRE QUI L'ID DELL'UTENTE
  AND m.timestamp_finale IS NOT NULL
GROUP BY a.ID_utente, u.nome, u.cognome;

-- --------------------------------------------------------------------
-- 9. Estrazione delle missioni svoltesi negli ultimi tre anni 
--    nello stesso luogo di una missione data.
-- --------------------------------------------------------------------
SELECT m1.codice, m1.posizione, m1.timestamp_iniziale 
FROM missione m1
JOIN missione m2 ON m1.posizione = m2.posizione
WHERE m2.codice = 1 
  AND m1.codice != 1 
  AND m1.timestamp_iniziale >= DATE_SUB(NOW(), INTERVAL 3 YEAR);


-- --------------------------------------------------------------------
-- 10. Estrazione della lista delle richieste chiuse con risultato 
--     non totalmente positivo (livello di successo minore di 5).
-- --------------------------------------------------------------------
SELECT r.ID AS id_req, r.indirizzo, m.codice AS cod_miss, m.lvl_successo
FROM richiesta r
JOIN missione m ON r.ID = m.ID_richiesta
WHERE r.stato = 'chiusa' 
  AND m.lvl_successo < 5;


-- --------------------------------------------------------------------
-- 11. Estrazione degli operatori maggiormente coinvolti nelle richieste 
--     chiuse con risultato non totalmente positivo.
-- --------------------------------------------------------------------
SELECT u.ID, u.nome, u.cognome, COUNT(a.codice_missione) AS num_missioni_non_positive
FROM assegnamento a
JOIN missione m ON a.codice_missione = m.codice
JOIN richiesta r ON m.ID_richiesta = r.ID
JOIN utente u ON a.ID_utente = u.ID
WHERE r.stato = 'chiusa' 
  AND m.lvl_successo < 5
GROUP BY u.ID, u.nome, u.cognome
ORDER BY num_missioni_non_positive DESC;


-- --------------------------------------------------------------------
-- 12. Estrazione dello storico delle missioni in cui è stato coinvolto un certo mezzo.
-- --------------------------------------------------------------------
SELECT z.ID, z.nome, m.codice AS codice_missione, m.timestamp_iniziale, m.timestamp_finale, m.lvl_successo
FROM missione m
JOIN impiego i ON m.codice = i.codice_missione
JOIN mezzo z ON i.ID_mezzo = z.ID
WHERE z.ID = 1 
  AND m.timestamp_finale IS NOT NULL -- <-- Esclude le missioni ancora in corso
ORDER BY m.timestamp_iniziale DESC;


-- --------------------------------------------------------------------
-- 13. Calcolo delle ore d'uso di un certo materiale.
-- --------------------------------------------------------------------
SELECT u.ID_materiale, mat.nome, 
       SUM((UNIX_TIMESTAMP(m.timestamp_finale) - UNIX_TIMESTAMP(m.timestamp_iniziale)) / 3600) AS ore_uso_totali
FROM utilizzo u
JOIN missione m ON u.codice_missione = m.codice
JOIN materiale mat ON u.ID_materiale = mat.ID
WHERE mat.ID = 3 -- <-- INSERIRE QUI L'ID DEL MATERIALE
  AND m.timestamp_finale IS NOT NULL
GROUP BY u.ID_materiale, mat.nome;
