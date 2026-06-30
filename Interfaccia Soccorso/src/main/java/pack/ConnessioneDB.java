package pack;

import java.sql.Connection;
import java.sql.SQLException;
import org.apache.commons.dbcp2.BasicDataSource;

public class ConnessioneDB {
    private final BasicDataSource dataSource;

    public ConnessioneDB(String nomeDatabase, String user, String password) {
        String url = "jdbc:mysql://localhost:3306/" + nomeDatabase 
                 + "?connectionTimeZone=LOCAL&forceConnectionTimeZoneToSession=false";
        
        // Inizializza il DataSource per il connection pooling
        dataSource = new BasicDataSource();
        dataSource.setUrl(url);
        dataSource.setUsername(user);
        dataSource.setPassword(password);
        
        // Impostazioni base del pool
        dataSource.setMinIdle(5);    // Numero minimo di connessioni inattive nel pool
        dataSource.setMaxIdle(10);   // Numero massimo di connessioni inattive nel pool
        dataSource.setMaxTotal(25);  // Numero massimo di connessioni (attive + inattive)
    }

    // Restituisce una connessione attiva prelevata dal pool
    public Connection getConnection() throws ApplicationException {
        try {
            return dataSource.getConnection();
        } catch (SQLException ex) {
            throw new ApplicationException("Errore di connessione al database Soccorso tramite Connection Pool", ex);
        }
    }
    
    // Chiude l'intero pool alla chiusura dell'applicazione
    public void chiudiPool() throws ApplicationException {
        try {
            if (dataSource != null && !dataSource.isClosed()) {
                dataSource.close();
            }
        } catch (SQLException ex) {
            throw new ApplicationException("Errore nella chiusura del DataSource", ex);
        }
    }
}
