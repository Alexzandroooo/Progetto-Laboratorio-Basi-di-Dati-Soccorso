import pack.*;
import java.sql.SQLException;
import gui.*;
import javax.swing.SwingUtilities;

public class Main {
    private static final String DB_NAME = "Soccorso";
    private static final String DB_USER = "soccorsoUser";
    private static final String DB_PASS = "soccorsoPwd1234!";

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            try {
                ConnessioneDB db = new ConnessioneDB(DB_NAME, DB_USER, DB_PASS);
                
                // Passiamo l'oggetto di gestione del Pool
                Query_Soccorso queryModule = new Query_Soccorso(db);
                
                // Assicura la chiusura pulita del pool alla terminazione del programma
                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    try {
                        db.chiudiPool();
                        System.out.println("Pool di connessioni chiuso correttamente.");
                    } catch (ApplicationException ex) {
                        System.err.println("Errore nella chiusura del pool: " + ex.getMessage());
                    }
                }));
                
                // Creiamo e mostriamo la finestra interattiva
                Finestra gui = new Finestra(queryModule);
                gui.setVisible(true);
                
                System.out.println("Interfaccia interattiva avviata correttamente.");
            } catch (Exception e) {
                System.err.println("Impossibile avviare l'applicazione grafica: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
}