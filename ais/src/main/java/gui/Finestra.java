package gui;
import pack.*;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.nio.file.Files;

public class Finestra extends JFrame {
    private final Query_Soccorso queryModule;
    
    private JTextArea areaStatoAttuale;
    private JTextArea areaConsole;      
    
    private byte[] fotoSelezionata = null;

    public Finestra(Query_Soccorso queryModule) {
        this.queryModule = queryModule;
        setTitle("Sistema di Gestione Soccorso - Pannello di Controllo (MVC)");
        setSize(1150, 780); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
    }

    private void initComponents() {
        JSplitPane splitPaneSuperiore = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPaneSuperiore.setDividerLocation(400);

        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.LEFT);
        tabbedPane.setFont(new Font("SansSerif", Font.BOLD, 12));

        // =================================================================
        // TAB 1: 	QUERY 1 - INSERISCI RICHIESTA
        // =================================================================
        JPanel panelRichiesta = new JPanel(new GridLayout(9, 2, 5, 5));
        panelRichiesta.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JTextField txtIndirizzo = new JTextField("Via Roma 12, L'Aquila");
        JTextField txtCoordinate = new JTextField("42.351, 13.399");
        JTextField txtToken = new JTextField("tok_" + System.currentTimeMillis());
        txtToken.setEditable(false);
        txtToken.setBackground(new Color(230, 235, 240));

        JTextField txtIp = new JTextField("127.0.0.1");
        JTextField txtEmail = new JTextField("cittadino1@aq.it");
        JTextField txtNome = new JTextField("Luigi Bianchi");
        JTextField txtDescrizione = new JTextField("Incidente stradale grave");
        
        JButton btnCaricaFoto = new JButton("Sfoglia Immagine...");
        JLabel lblFotoStato = new JLabel("Nessun file selezionato (Opzionale)");
        lblFotoStato.setFont(new Font("SansSerif", Font.ITALIC, 11));
        JPanel pnlFotoWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        pnlFotoWrapper.add(btnCaricaFoto);
        pnlFotoWrapper.add(lblFotoStato);

        JButton btnInviaRichiesta = new JButton("Esegui: Inserisci Nuova Richiesta");

        panelRichiesta.add(new JLabel(" Indirizzo:")); panelRichiesta.add(txtIndirizzo);
        panelRichiesta.add(new JLabel(" Coordinate (Opzionali):")); panelRichiesta.add(txtCoordinate);
        panelRichiesta.add(new JLabel(" Token Validazione (Autogenerato):")); panelRichiesta.add(txtToken);
        panelRichiesta.add(new JLabel(" Indirizzo IP:")); panelRichiesta.add(txtIp);
        panelRichiesta.add(new JLabel(" Email Segnalante:")); panelRichiesta.add(txtEmail);
        panelRichiesta.add(new JLabel(" Nome Segnalante:")); panelRichiesta.add(txtNome);
        panelRichiesta.add(new JLabel(" Descrizione Emergenza:")); panelRichiesta.add(txtDescrizione);
        panelRichiesta.add(new JLabel(" Foto Emergenza (MEDIUMBLOB):")); panelRichiesta.add(pnlFotoWrapper);
        panelRichiesta.add(new JLabel("")); panelRichiesta.add(btnInviaRichiesta);
        
        JPanel tab1 = new JPanel(new BorderLayout());
        tab1.add(panelRichiesta, BorderLayout.NORTH);
        tabbedPane.addTab("Query 1: Nuova Richiesta", tab1);

        // =================================================================
        // TAB 2: QUERY 2 - AVVIA MISSIONE
        // =================================================================
        JPanel panelAvvia = new JPanel(new GridLayout(7, 2, 8, 8));
        panelAvvia.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JTextField txtIdReqAvvia = new JTextField("5");
        JTextField txtIdCapo = new JTextField("2");
        JTextField txtOperatori = new JTextField("3,7");
        JTextField txtMezzi = new JTextField("2,4");         
        JTextField txtMateriali = new JTextField("1,2");     
        JTextField txtQuantita = new JTextField("2,1");      
        
        JButton btnAiutoTeam2 = new JButton("Visualizza Operatori Liberi");
        JButton btnAiutoMezzi2 = new JButton("Visualizza Mezzi");
        JButton btnAiutoMat2 = new JButton("Visualizza Materiali in Magazzino");
        JButton btnAvvia = new JButton("Esegui: Avvia Nuova Missione");
        btnAvvia.setBackground(new Color(220, 240, 220));

        panelAvvia.add(new JLabel(" ID Richiesta Origine:")); panelAvvia.add(txtIdReqAvvia);
        panelAvvia.add(new JLabel(" ID Caposquadra:")); panelAvvia.add(txtIdCapo);
        
        JPanel pnlOp = new JPanel(new BorderLayout(5,0));
        pnlOp.add(txtOperatori, BorderLayout.CENTER); pnlOp.add(btnAiutoTeam2, BorderLayout.EAST);
        panelAvvia.add(new JLabel(" Operatori:")); panelAvvia.add(pnlOp);
        
        JPanel pnlMz = new JPanel(new BorderLayout(5,0));
        pnlMz.add(txtMezzi, BorderLayout.CENTER); pnlMz.add(btnAiutoMezzi2, BorderLayout.EAST);
        panelAvvia.add(new JLabel(" Mezzi:")); panelAvvia.add(pnlMz);
        
        JPanel pnlMt = new JPanel(new BorderLayout(5,0));
        pnlMt.add(txtMateriali, BorderLayout.CENTER); pnlMt.add(btnAiutoMat2, BorderLayout.EAST);
        panelAvvia.add(new JLabel(" Materiali:")); panelAvvia.add(pnlMt);
        
        panelAvvia.add(new JLabel(" Quantità (parallele ai mat.):")); panelAvvia.add(txtQuantita);
        panelAvvia.add(new JLabel("")); panelAvvia.add(btnAvvia);

        JPanel tab2 = new JPanel(new BorderLayout());
        tab2.add(panelAvvia, BorderLayout.NORTH);
        tabbedPane.addTab("Query 2: Avvia Missione", tab2);

        // =================================================================
        // TAB 3: QUERY 3 - CHIUDI MISSIONE
        // =================================================================
        JPanel panelChiudi = new JPanel(new GridLayout(5, 2, 5, 5));
        panelChiudi.setBorder(BorderFactory.createTitledBorder("Query 3: Chiudi Missione (Stored Procedure)"));
        
        JTextField txtCodMiss = new JTextField("2", 5);
        JButton btnAiutoOp3 = new JButton("?");
        btnAiutoOp3.setToolTipText("Mostra le missioni attive e concluse");
        
        JPanel pnlCodice = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pnlCodice.add(txtCodMiss);
        pnlCodice.add(Box.createHorizontalStrut(5));
        pnlCodice.add(btnAiutoOp3);
        
        JTextField txtDataFine = new JTextField("2026-08-30 20:30:00", 15);
        JComboBox<Integer> comboLvl = new JComboBox<>(new Integer[]{0, 1, 2, 3, 4, 5});
        comboLvl.setSelectedItem(4);
        JTextField txtCommenti = new JTextField("Intervento completato", 20);
        JButton btnChiudi = new JButton("Chiudi Missione");
        
        panelChiudi.add(new JLabel(" Codice Missione:")); panelChiudi.add(pnlCodice);
        panelChiudi.add(new JLabel(" Data/Ora Fine:")); panelChiudi.add(txtDataFine);
        panelChiudi.add(new JLabel(" Livello Successo:")); panelChiudi.add(comboLvl);
        panelChiudi.add(new JLabel(" Note/Commenti:")); panelChiudi.add(txtCommenti);
        panelChiudi.add(new JLabel("")); panelChiudi.add(btnChiudi);

        JPanel tab3 = new JPanel(new BorderLayout());
        tab3.add(panelChiudi, BorderLayout.NORTH);
        tabbedPane.addTab("Query 3: Chiudi Missione", tab3);

        // =================================================================
        // ALTRI TAB (4-13)
        // =================================================================
        JPanel pnlLiberi = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlLiberi.setBorder(BorderFactory.createTitledBorder("Query 4: Operatori Liberi"));
        JButton btnLiberi = new JButton("Cerca Operatori Attualmente Liberi");
        pnlLiberi.add(btnLiberi);
        JPanel tab4 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        tab4.add(pnlLiberi);
        tabbedPane.addTab("Query 4: Operatori Liberi", tab4);

        JPanel pnlStatOp5 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlStatOp5.setBorder(BorderFactory.createTitledBorder("Query 5: Numero di Missioni Svolte da un Operatore"));
        JTextField txtIdOp5 = new JTextField("2", 5);
        JButton btnAiutoOp5 = new JButton("?"); 
        JButton btnContaMiss = new JButton("Calcola N. Missioni");
        pnlStatOp5.add(new JLabel("ID Operatore:")); 
        pnlStatOp5.add(txtIdOp5);
        pnlStatOp5.add(btnAiutoOp5);
        pnlStatOp5.add(btnContaMiss);
        JPanel tab5 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        tab5.add(pnlStatOp5);
        tabbedPane.addTab("Query 5: N. Miss. Operatore", tab5);

        JPanel pnlMediaAnno = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlMediaAnno.setBorder(BorderFactory.createTitledBorder("Query 6A: Tempo Medio Missioni per Anno"));
        JTextField txtAnno = new JTextField("2026", 5);
        JButton btnMediaAnno = new JButton("Calcola Media Anno");
        pnlMediaAnno.add(new JLabel("Anno:")); pnlMediaAnno.add(txtAnno);
        pnlMediaAnno.add(btnMediaAnno);
        JPanel pnlMediaCapo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlMediaCapo.setBorder(BorderFactory.createTitledBorder("Query 6B: Tempo Medio per Caposquadra"));
        JButton btnMediaCapo = new JButton("Calcola Medie Capi Squadra");
        pnlMediaCapo.add(btnMediaCapo);
        JPanel pnlOp6Container = new JPanel(new GridLayout(2, 1, 5, 5));
        pnlOp6Container.add(pnlMediaAnno);
        pnlOp6Container.add(pnlMediaCapo);
        JPanel tab6 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        tab6.add(pnlOp6Container);
        tabbedPane.addTab("Query 6: Tempi Medi", tab6);

        JPanel pnl36h = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnl36h.setBorder(BorderFactory.createTitledBorder("Query 7: Richieste Ultime 36 Ore"));
        JComboBox<String> comboTipo36 = new JComboBox<>(new String[]{"Email Segnalante", "Indirizzo IP"});
        JTextField txtParam36 = new JTextField("", 15);
        
        JButton btnAiutoOp7 = new JButton("?");
        btnAiutoOp7.setToolTipText("Mostra email e IP delle richieste recenti");
        
        JButton btn36h = new JButton("Cerca Richieste");
        pnl36h.add(comboTipo36); 
        pnl36h.add(txtParam36); 
        pnl36h.add(btnAiutoOp7);
        pnl36h.add(btn36h);
        JPanel tab7 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        tab7.add(pnl36h);
        tabbedPane.addTab("Query 7: Ultime 36 Ore", tab7);

        JPanel pnlStatOp8 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlStatOp8.setBorder(BorderFactory.createTitledBorder("Query 8: Ore Totali di Impiego di un Operatore"));
        JTextField txtIdOp8 = new JTextField("2", 5);
        JButton btnAiutoOp8 = new JButton("?"); 
        JButton btnOreUso = new JButton("Calcola Ore Impiego");
        pnlStatOp8.add(new JLabel("ID Operatore:")); 
        pnlStatOp8.add(txtIdOp8);
        pnlStatOp8.add(btnAiutoOp8);
        pnlStatOp8.add(btnOreUso);
        JPanel tab8 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        tab8.add(pnlStatOp8);
        tabbedPane.addTab("Query 8: Ore Impiego Op.", tab8);

        JPanel pnlLuogo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlLuogo.setBorder(BorderFactory.createTitledBorder("Query 9: Missioni Nello Stesso Luogo (Ultimi 3 Anni)"));
        JTextField txtMissRif = new JTextField("3", 5);
        
        JButton btnAiutoOp9 = new JButton("?");
        btnAiutoOp9.setToolTipText("Mostra i codici delle missioni disponibili");
        
        JButton btnLuogo = new JButton("Cerca Correlati");
        pnlLuogo.add(new JLabel("Rispetto alla missione Codice:")); 
        pnlLuogo.add(txtMissRif);
        pnlLuogo.add(btnAiutoOp9);
        pnlLuogo.add(btnLuogo);
        
        JPanel tab9 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        tab9.add(pnlLuogo);
        tabbedPane.addTab("Query 9: Storico Luogo", tab9);

        JPanel pnlNonPositive = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlNonPositive.setBorder(BorderFactory.createTitledBorder("Query 10: Analisi Interventi Critici (< 5 lvl)"));
        JButton btnOp10 = new JButton("Elenco Missioni Critiche");
        pnlNonPositive.add(btnOp10);
        JPanel tab10 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        tab10.add(pnlNonPositive);
        tabbedPane.addTab("Query 10: Missioni Critiche", tab10);

        JPanel pnlOp11 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlOp11.setBorder(BorderFactory.createTitledBorder("Query 11: Operatori Più Coinvolti in Missioni Critiche"));
        JButton btnOp11 = new JButton("Operatori Più Coinvolti");
        pnlOp11.add(btnOp11);
        JPanel tab11 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        tab11.add(pnlOp11);
        tabbedPane.addTab("Query 11: Operatori Critici", tab11);

        JPanel pnlMezzo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlMezzo.setBorder(BorderFactory.createTitledBorder("Query 12: Storico Mezzo"));
        JTextField txtIdMezzo = new JTextField("1", 5);
        JButton btnAiutoMezzo = new JButton("?");
        JButton btnCercaMezzo = new JButton("Cerca Storico Mezzo");
        pnlMezzo.add(new JLabel("ID Mezzo:")); 
        pnlMezzo.add(txtIdMezzo);
        pnlMezzo.add(btnAiutoMezzo);
        pnlMezzo.add(btnCercaMezzo);
        JPanel tab12 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        tab12.add(pnlMezzo);
        tabbedPane.addTab("Query 12: Storico Mezzo", tab12);

        JPanel pnlMateriale = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlMateriale.setBorder(BorderFactory.createTitledBorder("Query 13: Ore Uso Materiale"));
        JTextField txtIdMat = new JTextField("3", 5);
        JButton btnAiutoMat = new JButton("?");
        JButton btnCercaMat = new JButton("Calcola Ore Uso");
        pnlMateriale.add(new JLabel("ID Materiale:")); 
        pnlMateriale.add(txtIdMat);
        pnlMateriale.add(btnAiutoMat);
        pnlMateriale.add(btnCercaMat);
        JPanel tab13 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        tab13.add(pnlMateriale);
        tabbedPane.addTab("Query 13: Uso Materiale", tab13);


        // =================================================================
        // ZONA DI OUTPUT LOG - DIVISIONE DINAMICA DELLE CONSOLE
        // =================================================================
        areaStatoAttuale = new JTextArea();
        areaStatoAttuale.setEditable(false);
        areaStatoAttuale.setFont(new Font("Monospaced", Font.PLAIN, 12));
        areaStatoAttuale.setBackground(new Color(250, 252, 248)); 
        JScrollPane scrollStato = new JScrollPane(areaStatoAttuale);
        scrollStato.setBorder(BorderFactory.createTitledBorder("Stato Attuale del Database (PRIMA)"));

        areaConsole = new JTextArea();
        areaConsole.setEditable(false);
        areaConsole.setFont(new Font("Monospaced", Font.PLAIN, 12));
        areaConsole.setBackground(new Color(245, 245, 250));
        JScrollPane scrollConsole = new JScrollPane(areaConsole);
        scrollConsole.setBorder(BorderFactory.createTitledBorder("Console Eventi & Output Query"));

        JSplitPane splitPaneInferiore = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPaneInferiore.setLeftComponent(scrollStato);
        splitPaneInferiore.setRightComponent(scrollConsole);
        splitPaneInferiore.setDividerLocation(520); 

        splitPaneSuperiore.setTopComponent(tabbedPane);
        splitPaneSuperiore.setBottomComponent(splitPaneInferiore); 
        add(splitPaneSuperiore);

        // ====================================================================
        // GESTIONE DEL COLLASSO DINAMICO DELLA CONSOLE DI SINISTRA E STAMPE (PRIMA)
        // ====================================================================
        tabbedPane.addChangeListener(e -> {
            int tabIndex = tabbedPane.getSelectedIndex();
            try {
                if (tabIndex <= 2) {
                    splitPaneInferiore.setRightComponent(scrollConsole); 
                    splitPaneSuperiore.setBottomComponent(splitPaneInferiore); 
                    splitPaneInferiore.setDividerLocation(520); 

                    switch (tabIndex) {
                        case 0: 
                            areaStatoAttuale.setText(TextPresenter.format(queryModule.visualizzaTabellaRichiesta()));
                            areaConsole.setText("--- Pronti per eseguire la query 1 ---\n");
                            break;
                        case 1: // TAB 2 - AVVIA MISSIONE
                            areaStatoAttuale.setText(TextPresenter.formatMulti(
                                queryModule.visualizzaTabellaRichiesta(), 
                                queryModule.visualizzaTabellaMissione(), 
                                queryModule.visualizzaTabellaAssegnamento(), 
                                queryModule.anteprimaMezzi(), 
                                queryModule.visualizzaTabellaImpiego(), 
                                queryModule.visualizzaTabellaMateriale(),
                                queryModule.visualizzaTabellaUtilizzo()
                            ));
                            areaConsole.setText("--- Pronti per eseguire la query 2 ---\nPuoi assegnare 0,n mezzi e materiali separati da virgola.\n");
                            break;
                        case 2: // TAB 3 - CHIUDI MISSIONE
                            areaStatoAttuale.setText(TextPresenter.formatMulti(
                                queryModule.visualizzaTabellaRichiesta(), 
                                queryModule.visualizzaTabellaMissione()
                            ));
                            areaConsole.setText("--- Pronti per eseguire la query 3 ---\n");
                            break;
                    }
                } else {
                    splitPaneSuperiore.setBottomComponent(scrollConsole);
                    areaConsole.setText("--- Pronti per eseguire la query " + (tabIndex + 1) + " ---\n");
                }
            } catch (ApplicationException ex) { stampaErrore(ex); }
        });

        // Caricamento iniziale per il primo tab
        SwingUtilities.invokeLater(() -> {
            try { 
                areaStatoAttuale.setText(TextPresenter.format(queryModule.visualizzaTabellaRichiesta())); 
                areaConsole.setText("--- Pronti per eseguire la query 1 ---\n");
            } 
            catch (ApplicationException ignored) {}
        });

        // ====================================================================
        // GESTIONE EVENTI - AZIONI DEI PULSANTI
        // ====================================================================

        btnCaricaFoto.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(Finestra.this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                try {
                    fotoSelezionata = Files.readAllBytes(file.toPath());
                    lblFotoStato.setText(file.getName() + " (" + fotoSelezionata.length + " bytes)");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(Finestra.this, "Errore nel caricamento file: " + ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
                    fotoSelezionata = null;
                    lblFotoStato.setText("Caricamento fallito.");
                }
            }
        });

        btnInviaRichiesta.addActionListener(e -> {
            try {
                areaConsole.setText("...Esecuzione transazione in corso...\n");
                
                // Effettua l'inserimento
                int newId = queryModule.inserisciRichiesta(txtIndirizzo.getText(), txtCoordinate.getText(), txtToken.getText(),
                        txtIp.getText(), txtEmail.getText(), txtNome.getText(), txtDescrizione.getText(), fotoSelezionata);
                
                // Stampa l'esito formattato a video (Delegando al Presenter per le tabelle)
                areaConsole.append(">>> SUCCESS: Richiesta ID " + newId + " inserita correttamente.\n");
                if (fotoSelezionata != null) {
                    areaConsole.append(">>> Immagine allegata salvata (" + fotoSelezionata.length + " bytes).\n");
                }
                
                areaConsole.append("\n=== TABELLA RICHIESTA (DOPO L'ESECUZIONE DELLA QUERY) ===\n");
                areaConsole.append(TextPresenter.format(queryModule.visualizzaTabellaRichiesta())); 
                
                fotoSelezionata = null;
                lblFotoStato.setText("Nessun file selezionato (Opzionale)");
                txtToken.setText("tok_" + System.currentTimeMillis()); 
            } catch (ApplicationException ex) { stampaErrore(ex); }
        });

        btnAvvia.addActionListener(e -> {
            try {
                areaConsole.setText("...Invocazione 'avvia_missione' in corso...\n");
                int idReq = Integer.parseInt(txtIdReqAvvia.getText().trim());
                int idCapo = Integer.parseInt(txtIdCapo.getText().trim());
                String opsCsv = txtOperatori.getText().trim();
                String mezziCsv = txtMezzi.getText().trim();
                String matCsv = txtMateriali.getText().trim();
                String qtaCsv = txtQuantita.getText().trim();
                
                queryModule.avviaMissione(idReq, idCapo, opsCsv, mezziCsv, matCsv, qtaCsv);
                
                areaConsole.append(">>> SUCCESS: Missione avviata con successo per la Richiesta ID " + idReq + "!\n");
                areaConsole.append("\n=== TABELLE AGGIORNATE DOPO LA QUERY ===\n\n");
                areaConsole.append(TextPresenter.formatMulti(
                    queryModule.visualizzaTabellaRichiesta(),
                    queryModule.visualizzaTabellaMissione(),
                    queryModule.visualizzaTabellaAssegnamento(),
                    queryModule.anteprimaMezzi(),
                    queryModule.visualizzaTabellaImpiego(),
                    queryModule.visualizzaTabellaMateriale(),
                    queryModule.visualizzaTabellaUtilizzo()
                ));
                
            } catch (NumberFormatException nfe) { 
                areaConsole.setText("ERRORE: ID Richiesta e ID Caposquadra devono essere numerici.\n");
            } catch (ApplicationException ex) { stampaErrore(ex); }
        });

        btnChiudi.addActionListener(e -> {
            try {
                areaConsole.setText("...Invocazione 'chiudi_missione' in corso...\n");
                int codMiss = Integer.parseInt(txtCodMiss.getText().trim());
                int lvl = (Integer) comboLvl.getSelectedItem();
                
                queryModule.chiudiMissione(codMiss, txtDataFine.getText(), lvl, txtCommenti.getText());
                
                areaConsole.append(">>> SUCCESS: Modifica applicata. Missione [" + codMiss + "] chiusa correttamente.\n");
                areaConsole.append("\n=== TABELLE AGGIORNATE (DOPO L'ESECUZIONE DELLA QUERY) ===\n");
                areaConsole.append(TextPresenter.formatMulti(
                    queryModule.visualizzaTabellaRichiesta(), 
                    queryModule.visualizzaTabellaMissione()
                )); 
            } catch (NumberFormatException nfe) { areaConsole.setText("ERRORE: Codice Missione deve essere numerico.\n");
            } catch (ApplicationException ex) { stampaErrore(ex); }
        });


        // ====================================================================
        // PULSANTI DI AIUTO / ANTEPRIMA (Stampa lato destro usando TextPresenter)
        // ====================================================================
        btnAiutoTeam2.addActionListener(e -> {
            try { areaConsole.setText(TextPresenter.format(queryModule.ottieniOperatoriLiberi())); } 
            catch (ApplicationException ex) { stampaErrore(ex); }
        });
        btnAiutoMezzi2.addActionListener(e -> {
            try { areaConsole.setText(TextPresenter.format(queryModule.anteprimaMezzi())); } 
            catch (ApplicationException ex) { stampaErrore(ex); }
        });
        btnAiutoMat2.addActionListener(e -> {
            try { areaConsole.setText(TextPresenter.format(queryModule.anteprimaMateriali())); } 
            catch (ApplicationException ex) { stampaErrore(ex); }
        });
        
        btnAiutoOp3.addActionListener(e -> {
            try { areaConsole.setText(TextPresenter.format(queryModule.anteprimaMissioni())); } 
            catch (ApplicationException ex) { stampaErrore(ex); }
        });

        btnAiutoOp7.addActionListener(e -> {
            try { areaConsole.setText(TextPresenter.format(queryModule.anteprimaRichieste())); } 
            catch (ApplicationException ex) { stampaErrore(ex); }
        });

        btnAiutoOp9.addActionListener(e -> {
            try { areaConsole.setText(TextPresenter.format(queryModule.anteprimaMissioni())); } 
            catch (ApplicationException ex) { stampaErrore(ex); }
        });
        
        btnAiutoOp5.addActionListener(e -> {
            try { areaConsole.setText(TextPresenter.format(queryModule.anteprimaOperatori())); } 
            catch (ApplicationException ex) { stampaErrore(ex); }
        });

        btnAiutoOp8.addActionListener(e -> {
            try { areaConsole.setText(TextPresenter.format(queryModule.anteprimaOperatori())); } 
            catch (ApplicationException ex) { stampaErrore(ex); }
        });

        btnAiutoMezzo.addActionListener(e -> {
            try { areaConsole.setText(TextPresenter.format(queryModule.anteprimaMezzi())); } 
            catch (ApplicationException ex) { stampaErrore(ex); }
        });

        btnAiutoMat.addActionListener(e -> {
            try { areaConsole.setText(TextPresenter.format(queryModule.anteprimaMateriali())); } 
            catch (ApplicationException ex) { stampaErrore(ex); }
        });

        // ====================================================================
        // ALTRE FUNZIONI DI QUERY (Sempre delegate al TextPresenter)
        // ====================================================================
        btnLiberi.addActionListener(e -> {
            try { areaConsole.setText(TextPresenter.format(queryModule.ottieniOperatoriLiberi()));
            } catch (ApplicationException ex) { stampaErrore(ex); }
        });

        btnContaMiss.addActionListener(e -> {
            try {
                int idOp = Integer.parseInt(txtIdOp5.getText().trim());
                areaConsole.setText(TextPresenter.format(queryModule.contaMissioniOperatore(idOp)));
            } catch (NumberFormatException nfe) { areaConsole.setText("ERRORE: ID Operatore deve essere numerico.");
            } catch (ApplicationException ex) { stampaErrore(ex); }
        });

        btnOreUso.addActionListener(e -> {
            try {
                int idOp = Integer.parseInt(txtIdOp8.getText().trim());
                areaConsole.setText(TextPresenter.format(queryModule.tempoTotaleImpiegoOperatore(idOp)));
            } catch (NumberFormatException nfe) { areaConsole.setText("ERRORE: ID Operatore deve essere numerico.");
            } catch (ApplicationException ex) { stampaErrore(ex); }
        });

        btnMediaAnno.addActionListener(e -> {
            try { areaConsole.setText(TextPresenter.format(queryModule.tempoMedioMissioniAnno(Integer.parseInt(txtAnno.getText().trim()))));
            } catch (NumberFormatException nfe) { areaConsole.setText("ERRORE: Anno deve essere numerico.");
            } catch (ApplicationException ex) { stampaErrore(ex); }
        });

        btnMediaCapo.addActionListener(e -> {
            try { areaConsole.setText(TextPresenter.format(queryModule.tempoMedioPerCaposquadra()));
            } catch (ApplicationException ex) { stampaErrore(ex); }
        });

        btnOp10.addActionListener(e -> {
            try { areaConsole.setText(TextPresenter.format(queryModule.ottieniRichiesteNonPositive()));
            } catch (ApplicationException ex) { stampaErrore(ex); }
        });

        btnOp11.addActionListener(e -> {
            try { areaConsole.setText(TextPresenter.format(queryModule.ottieniOperatoriCoinvoltiNonPositive()));
            } catch (ApplicationException ex) { stampaErrore(ex); }
        });

        btnCercaMezzo.addActionListener(e -> {
            try {
                int idMezzo = Integer.parseInt(txtIdMezzo.getText().trim());
                areaConsole.setText(TextPresenter.format(queryModule.ottieniStoricoMezzo(idMezzo)));
            } catch (NumberFormatException nfe) { areaConsole.setText("ERRORE: ID Mezzo deve essere numerico.");
            } catch (ApplicationException ex) { stampaErrore(ex); }
        });

        btnCercaMat.addActionListener(e -> {
            try {
                int idMat = Integer.parseInt(txtIdMat.getText().trim());
                areaConsole.setText(TextPresenter.format(queryModule.oreUsoMateriale(idMat)));
            } catch (NumberFormatException nfe) { areaConsole.setText("ERRORE: ID Materiale deve essere numerico.");
            } catch (ApplicationException ex) { stampaErrore(ex); }
        });

        btn36h.addActionListener(e -> {
            try {
                boolean isEmail = comboTipo36.getSelectedIndex() == 0;
                areaConsole.setText(TextPresenter.format(queryModule.richiesteUltime36Ore(txtParam36.getText().trim(), isEmail)));
            } catch (ApplicationException ex) { stampaErrore(ex); }
        });

        btnLuogo.addActionListener(e -> {
            try { 
                areaConsole.setText(TextPresenter.format(queryModule.missioniStessoLuogoUltimiTreAnni(Integer.parseInt(txtMissRif.getText().trim()))));
            } catch (NumberFormatException nfe) { areaConsole.setText("ERRORE: ID Missione deve essere numerico.");
            } catch (ApplicationException ex) { stampaErrore(ex); }
        });
    }

    private void stampaErrore(ApplicationException ex) {
        areaConsole.setText("!!! ERRORE CATTURATO !!!\n");
        areaConsole.append(ex.getMessage() + "\n");
        if (ex.getCause() instanceof java.sql.SQLException sqlEx) {
            areaConsole.append("-> Dettaglio MySQL:\n");
            areaConsole.append("   [SQLState " + sqlEx.getSQLState() + "] Codice errore: " + sqlEx.getErrorCode() + "\n");
            areaConsole.append("   " + sqlEx.getMessage() + "\n");
        }
    }
}