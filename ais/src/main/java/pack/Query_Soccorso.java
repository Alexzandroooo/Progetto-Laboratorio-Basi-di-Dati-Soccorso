package pack;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Query_Soccorso {
    private final ConnessioneDB db;

    public Query_Soccorso(ConnessioneDB db) { this.db = db; }

    // ====================================================================
    // MOTORE CENTRALE: ESTRAE QUALSIASI QUERY IN FORMATO 'TABLEDATA'
    // ====================================================================
    private TableData eseguiQueryTabella(String title, String sql, Object... params) throws ApplicationException {
        try (Connection conn = db.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            // Popolamento dinamico dei parametri
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            
            try (ResultSet rs = stmt.executeQuery()) {
                ResultSetMetaData meta = rs.getMetaData();
                int cols = meta.getColumnCount();
                
                List<String> headers = new ArrayList<>();
                for (int i = 1; i <= cols; i++) headers.add(meta.getColumnLabel(i).toUpperCase());
                
                List<List<String>> rows = new ArrayList<>();
                while (rs.next()) {
                    List<String> row = new ArrayList<>();
                    for (int i = 1; i <= cols; i++) {
                        Object obj = rs.getObject(i);
                        row.add(obj == null ? "N/A" : obj.toString());
                    }
                    rows.add(row);
                }
                return new TableData(title, headers, rows);
            }
        } catch (SQLException ex) {
            throw new ApplicationException("Errore esecuzione query (" + title + ")", ex);
        }
    }

    // ====================================================================
    // METODI HELPER E ANTEPRIME
    // ====================================================================
    public TableData visualizzaTabellaRichiesta() throws ApplicationException {
        return eseguiQueryTabella("TABELLA RICHIESTA", "SELECT ID, indirizzo, stato FROM richiesta");
    }

    public TableData visualizzaTabellaMissione() throws ApplicationException {
        return eseguiQueryTabella("TABELLA MISSIONE", "SELECT codice, ID_richiesta, timestamp_iniziale, lvl_successo FROM missione");
    }

    public TableData visualizzaTabellaAssegnamento() throws ApplicationException {
        return eseguiQueryTabella("TABELLA ASSEGNAMENTO", "SELECT codice_missione, ID_utente, ruolo FROM assegnamento ORDER BY codice_missione DESC LIMIT 20");
    }

    public TableData visualizzaTabellaImpiego() throws ApplicationException {
        return eseguiQueryTabella("TABELLA IMPIEGO", "SELECT codice_missione, ID_mezzo FROM impiego ORDER BY codice_missione DESC LIMIT 20");
    }

    public TableData visualizzaTabellaUtilizzo() throws ApplicationException {
        return eseguiQueryTabella("TABELLA UTILIZZO", "SELECT codice_missione, ID_materiale, quantita FROM utilizzo ORDER BY codice_missione DESC LIMIT 20");
    }

    public TableData visualizzaTabellaMateriale() throws ApplicationException {
        return eseguiQueryTabella("TABELLA MATERIALE", "SELECT ID, nome, disponibilita FROM materiale LIMIT 10");
    }

    public TableData anteprimaOperatori() throws ApplicationException {
        return eseguiQueryTabella("ANAGRAFICA OPERATORI", "SELECT ID, nome, cognome FROM utente WHERE ruolo = 'operatore'");
    }

    public TableData anteprimaMezzi() throws ApplicationException {
        return eseguiQueryTabella("TABELLA MEZZI", "SELECT ID, targa, nome FROM mezzo");
    }

    public TableData anteprimaMateriali() throws ApplicationException {
        return eseguiQueryTabella("ELENCO MATERIALI", "SELECT ID, codice, nome, disponibilita FROM materiale");
    }

    public TableData anteprimaMissioni() throws ApplicationException {
        return eseguiQueryTabella("ANTEPRIMA MISSIONI", "SELECT codice, ID_richiesta, posizione, timestamp_iniziale, timestamp_finale FROM missione ORDER BY codice DESC");
    }

    public TableData anteprimaRichieste() throws ApplicationException {
        return eseguiQueryTabella("MITTENTI/IP UNICI", "SELECT email_segnalante, ip, MAX(timestamp) AS ultimo_contatto FROM richiesta GROUP BY email_segnalante, ip ORDER BY ultimo_contatto DESC LIMIT 50");
    }

    // ====================================================================
    // TRANSAZIONI E MODIFICHE 
    // ====================================================================
    public int inserisciRichiesta(String indirizzo, String coordinate, String token, String ip, String email, String nome, String descrizione, byte[] foto) throws ApplicationException {
        String sqlReq = "INSERT INTO richiesta (indirizzo, coordinate, token, ip, email_segnalante, nome_segnalante) VALUES (?, ?, ?, ?, ?, ?)";
        String sqlDet = "INSERT INTO dettaglio_richiesta (ID_richiesta, descrizione, foto) VALUES (?, ?, ?)";

        try (Connection conn = db.getConnection()) {
            conn.setAutoCommit(false);
            int idGenerato = -1;
            try (PreparedStatement stmt1 = conn.prepareStatement(sqlReq, Statement.RETURN_GENERATED_KEYS)) {
                stmt1.setString(1, indirizzo); stmt1.setString(2, coordinate); stmt1.setString(3, token);
                stmt1.setString(4, ip); stmt1.setString(5, email); stmt1.setString(6, nome);
                stmt1.executeUpdate();
                try (ResultSet rs = stmt1.getGeneratedKeys()) { if (rs.next()) idGenerato = rs.getInt(1); }
            }
            try (PreparedStatement stmt2 = conn.prepareStatement(sqlDet)) {
                stmt2.setInt(1, idGenerato); stmt2.setString(2, descrizione);
                if (foto != null) stmt2.setBytes(3, foto); else stmt2.setNull(3, java.sql.Types.BLOB);
                stmt2.executeUpdate();
            }
            conn.commit();
            return idGenerato;
        } catch (SQLException ex) {
            throw new ApplicationException("Errore inserimento transazionale richiesta", ex);
        }
    }

    public void avviaMissione(int idRichiesta, int idCaposquadra, String opsCsv, String mezziCsv, String matCsv, String qtaCsv) throws ApplicationException {
        String sql = "{call avvia_missione(?, ?, ?, ?, ?, ?)}";
        try (Connection conn = db.getConnection(); CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.setInt(1, idRichiesta); stmt.setInt(2, idCaposquadra); stmt.setString(3, opsCsv);
            stmt.setString(4, mezziCsv); stmt.setString(5, matCsv); stmt.setString(6, qtaCsv);
            stmt.execute();
        } catch (SQLException ex) { throw new ApplicationException("Errore esecuzione avvia_missione", ex); }
    }

    public void chiudiMissione(int codiceMissione, String timestampFinale, int lvlSuccesso, String commenti) throws ApplicationException {
        String sql = "{call chiudi_missione(?, ?, ?, ?)}";
        try (Connection conn = db.getConnection(); CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.setInt(1, codiceMissione); stmt.setString(2, timestampFinale);
            stmt.setInt(3, lvlSuccesso); stmt.setString(4, commenti);
            stmt.execute();
        } catch (SQLException ex) { throw new ApplicationException("Errore chiusura missione", ex); }
    }

    // ====================================================================
    // ALTRE QUERY 4-13 (Ora tornano tutte TableData)
    // ====================================================================
    public TableData ottieniOperatoriLiberi() throws ApplicationException {
        String sql = "SELECT u.ID, u.nome, u.cognome FROM utente u WHERE u.ruolo = 'operatore' " +
                     "AND u.ID NOT IN (SELECT a.ID_utente FROM assegnamento a JOIN missione m ON a.codice_missione = m.codice WHERE m.timestamp_finale IS NULL)";
        return eseguiQueryTabella("OPERATORI ATTUALMENTE LIBERI", sql);
    }

    public TableData contaMissioniOperatore(int idOperatore) throws ApplicationException {
        String sql = "SELECT a.ID_utente, u.nome, u.cognome, COUNT(a.codice_missione) AS num_missioni_svolte " +
                     "FROM assegnamento a JOIN utente u ON a.ID_utente = u.ID WHERE u.ID = ? GROUP BY a.ID_utente, u.nome, u.cognome";
        return eseguiQueryTabella("MISSIONI OPERATORE", sql, idOperatore);
    }

    public TableData tempoMedioMissioniAnno(int anno) throws ApplicationException {
        String sql = "SELECT AVG((UNIX_TIMESTAMP(timestamp_finale) - UNIX_TIMESTAMP(timestamp_iniziale)) / 60) AS tempo_medio_minuti " +
                     "FROM missione WHERE YEAR(timestamp_iniziale) = ? AND timestamp_finale IS NOT NULL";
        return eseguiQueryTabella("MEDIA ANNO " + anno, sql, anno);
    }

    public TableData tempoMedioPerCaposquadra() throws ApplicationException {
        String sql = "SELECT u.ID, u.nome, u.cognome, AVG((UNIX_TIMESTAMP(m.timestamp_finale) - UNIX_TIMESTAMP(m.timestamp_iniziale)) / 60) AS tempo_medio_minuti " +
                     "FROM assegnamento a JOIN missione m ON a.codice_missione = m.codice JOIN utente u ON a.ID_utente = u.ID " +
                     "WHERE a.ruolo = 'caposquadra' AND m.timestamp_finale IS NOT NULL GROUP BY u.ID, u.nome, u.cognome";
        return eseguiQueryTabella("TEMPO MEDIO PER CAPOSQUADRA", sql);
    }

    public TableData richiesteUltime36Ore(String parametro, boolean isEmail) throws ApplicationException {
        String sql = isEmail 
            ? "SELECT COUNT(*) AS numero_richieste FROM richiesta WHERE email_segnalante = ? AND timestamp >= DATE_SUB(NOW(), INTERVAL 36 HOUR)" 
            : "SELECT COUNT(*) AS numero_richieste FROM richiesta WHERE ip = ? AND timestamp >= DATE_SUB(NOW(), INTERVAL 36 HOUR)";
        return eseguiQueryTabella("RICHIESTE ULTIME 36H (" + parametro + ")", sql, parametro);
    }

    public TableData tempoTotaleImpiegoOperatore(int idOperatore) throws ApplicationException {
        String sql = "SELECT a.ID_utente, u.nome, u.cognome, SUM((UNIX_TIMESTAMP(m.timestamp_finale) - UNIX_TIMESTAMP(m.timestamp_iniziale)) / 3600) AS ore_impiego " +
                     "FROM assegnamento a JOIN missione m ON a.codice_missione = m.codice JOIN utente u ON a.ID_utente = u.ID " +
                     "WHERE u.ID = ? AND m.timestamp_finale IS NOT NULL GROUP BY a.ID_utente, u.nome, u.cognome";
        return eseguiQueryTabella("ORE IMPIEGO TOTALI", sql, idOperatore);
    }

    public TableData missioniStessoLuogoUltimiTreAnni(int codiceMissioneRif) throws ApplicationException {
        String sql = "SELECT m1.codice, m1.posizione, m1.timestamp_iniziale FROM missione m1 JOIN missione m2 ON m1.posizione = m2.posizione " +
                     "WHERE m2.codice = ? AND m1.codice != ? AND m1.timestamp_iniziale >= DATE_SUB(NOW(), INTERVAL 3 YEAR)";
        return eseguiQueryTabella("STORICO LUOGO MISSIONE " + codiceMissioneRif, sql, codiceMissioneRif, codiceMissioneRif);
    }

    public TableData ottieniRichiesteNonPositive() throws ApplicationException {
        String sql = "SELECT r.ID AS id_req, r.indirizzo, m.codice AS cod_miss, m.lvl_successo " +
                     "FROM richiesta r JOIN missione m ON r.ID = m.ID_richiesta WHERE r.stato = 'chiusa' AND m.lvl_successo < 5";
        return eseguiQueryTabella("MISSIONI CON SUCCESSO < 5", sql);
    }

    public TableData ottieniOperatoriCoinvoltiNonPositive() throws ApplicationException {
        String sql = "SELECT u.ID, u.nome, u.cognome, COUNT(a.codice_missione) AS num_missioni_critiche " +
                     "FROM assegnamento a JOIN missione m ON a.codice_missione = m.codice JOIN richiesta r ON m.ID_richiesta = r.ID " +
                     "JOIN utente u ON a.ID_utente = u.ID WHERE r.stato = 'chiusa' AND m.lvl_successo < 5 " +
                     "GROUP BY u.ID, u.nome, u.cognome ORDER BY num_missioni_critiche DESC";
        return eseguiQueryTabella("OPERATORI COINVOLTI IN MISSIONI CRITICHE", sql);
    }

    public TableData ottieniStoricoMezzo(int idMezzo) throws ApplicationException {
        String sql = "SELECT z.ID, z.nome, m.codice AS codice_missione, m.timestamp_iniziale, m.timestamp_finale, m.lvl_successo " +
                     "FROM missione m JOIN impiego i ON m.codice = i.codice_missione JOIN mezzo z ON i.ID_mezzo = z.ID " +
                     "WHERE z.ID = ? AND m.timestamp_finale IS NOT NULL ORDER BY m.timestamp_iniziale DESC";
        return eseguiQueryTabella("STORICO MEZZO ID " + idMezzo, sql, idMezzo);
    }

    public TableData oreUsoMateriale(int idMateriale) throws ApplicationException {
        String sql = "SELECT u.ID_materiale, mat.nome, SUM((UNIX_TIMESTAMP(m.timestamp_finale) - UNIX_TIMESTAMP(m.timestamp_iniziale)) / 3600) AS ore_uso " +
                     "FROM utilizzo u JOIN missione m ON u.codice_missione = m.codice JOIN materiale mat ON u.ID_materiale = mat.ID " +
                     "WHERE mat.ID = ? AND m.timestamp_finale IS NOT NULL GROUP BY u.ID_materiale, mat.nome";
        return eseguiQueryTabella("ORE USO MATERIALE", sql, idMateriale);
    }
}