package src.it.unibs.ingsw.gestvisit;

import it.unibs.fp.mylib.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class VisitManager {

    // Attributi------------------------------------------------------------------------------
    private ArrayList<Volontario> volontari = new ArrayList<>();    
    private ArrayList<Configuratore> configuratori = new ArrayList<>();
    private ArrayList<TemporaryCredential> temporaryCredentials = new ArrayList<>();
    private CredentialManager credentialManager = new CredentialManager();
    private final ExecutorService executorService = Executors.newCachedThreadPool(); // Pool con thread caching
    private final DatabaseUpdater databaseUpdater = new DatabaseUpdater(executorService);
    private final Utilita utilita = new Utilita(databaseUpdater);

    //Gestione Thread-------------------------------------------------------------------------
    public VisitManager() {
        // Sincronizza i dati iniziali dal database
        databaseUpdater.sincronizzaDalDatabase();
    }

    public void stopExecutorService() {
        executorService.shutdown();
        System.out.println("ExecutorService arrestato.");
    }


    //Autenticazione-------------------------------------------------------------------------
    public void autentica() {
        String nomeUtente = InputDati.leggiStringaNonVuota("Inserisci il nome utente (email): ");
        String password = InputDati.leggiStringaNonVuota("Inserisci la password: ");
        String tipo_utente = credentialManager.verificaCredenziali(nomeUtente, password);
        Boolean credenzialiModificate = credentialManager.isPasswordModificata(nomeUtente);
    
        if (tipo_utente == null) {
            System.out.println("Credenziali non valide.");
        } else {
            Menu menu;
            if (tipo_utente.equals("Volontario")) {
                System.out.println("Accesso come Volontario.");

                // Cerca il volontario corrispondente all'email
                Volontario volontario = databaseUpdater.getVolontariMap().get(nomeUtente);
                if (volontario == null) {
                    System.out.println("Errore: volontario non trovato.");
                    return;
                }
    
                // Controlla se il volontario ha credenziali temporanee
                if (credenzialiModificate) {
                    System.out.println("Hai credenziali temporanee. Ti preghiamo di modificarle.");
                    modificaCredenzialiVolontario(volontario);
                }
    
                menu = new MenuVolontario();
            } else if (tipo_utente.equals("Configuratore")) {
                System.out.println("Accesso come Configuratore.");
                menu = new MenuConfiguratore();
            } else if(tipo_utente.equals("TEMP")){
                System.out.println("Accesso come utente temporaneo.");
                modificaCredenzialiConfiguratore();
                menu = new MenuConfiguratore();
            } else {
                System.out.println("Ruolo non riconosciuto: " + tipo_utente);
                return;
            }
            menu.mostraMenu();
        }
    }

    
    //Logiche per i luoghi-------------------------------------------------------------------------
    public void aggiungiLuogo() {
        String nome = InputDati.leggiStringaNonVuota("inserire il nome del luogo: ");
        String descrizione = InputDati.leggiStringaNonVuota("inserire la descrizione del luogo: ");

        Luogo nuovoLuogo = new Luogo(nome, descrizione);
        databaseUpdater.getLuoghiMap().put(nome, nuovoLuogo);
        databaseUpdater.aggiungiLuogo(nuovoLuogo);
        System.out.println("Luogo aggiunto: " + nuovoLuogo);
    }

    public void mostraLuoghi() {
        utilita.stampaLuoghi();
    }

    //Logiche per i volontari-------------------------------------------------------------------------
    public void aggiungiVolontario() {
        String nome = InputDati.leggiStringaNonVuota("inserire il nome del volontario: ");
        String cognome = InputDati.leggiStringaNonVuota("inserire il cognome del volontario: ");
        String email = InputDati.leggiStringaNonVuota("inserire l'email del volontario: ");
        String nomeUtente = email;
        String password = InputDati.leggiStringaNonVuota("inserire la password: ");
        
        Volontario nuovoVolontario = new Volontario(nome, cognome, email, nomeUtente, password);
        volontari.add(nuovoVolontario);

        // Utilita.salvaVolontari(nuovoVolontario);
        databaseUpdater.aggiungiVolontario(nuovoVolontario);
        
    }

    public void mostraVolontari() {
        utilita.stampaVolontari();
    }

    //Logiche per le visite-------------------------------------------------------------------------
    public void assegnaVisita() {
        // Utilita.assegnaVisita();
    }

    public void mostraVisite() {        
        utilita.stampaVisite();
    }
    
    public void visualizzaVisitePerStato(){
        utilita.visualizzaVisitePerStato ();
    }

    public void modificaNumeroMaxPersonePerVisita() {
        int numeroMax = InputDati.leggiInteroConMinimo("Inserisci il numero massimo di persone per visita: ", 2);
        utilita.setMaxPersonePerVisita(numeroMax);
        System.out.println("Numero massimo di persone per visita modificato a: " + numeroMax);
            
    }

    public void modificaDataVisita() {        
        utilita.modificaDataVisita();
    }

    // Metodo per aggiungere una nuova visita
    public void aggiungiVisita() {
        try (Connection conn = DatabaseConnection.connect()) {
            // Recupera i luoghi disponibili (presenti in "luoghi" ma non in "visite")
            String sqlLuoghiDisponibili = "SELECT nome FROM luoghi WHERE nome NOT IN (SELECT luogo FROM visite)";
            PreparedStatement pstmtLuoghi = conn.prepareStatement(sqlLuoghiDisponibili);
            ResultSet rsLuoghi = pstmtLuoghi.executeQuery();

            List<String> luoghiDisponibili = new ArrayList<>();

            System.out.println("Elenco dei luoghi disponibili:");
            while (rsLuoghi.next()) {
                String nomeLuogo = rsLuoghi.getString("nome");
                luoghiDisponibili.add(nomeLuogo);
                System.out.println(luoghiDisponibili.size() + ". " + nomeLuogo);
            }

            // Controlla se ci sono luoghi disponibili
            if (luoghiDisponibili.isEmpty()) {
                System.out.println("Non ci sono luoghi disponibili per aggiungere una visita.");
                return;
            }

            // L'utente seleziona un luogo dalla lista
            int luogoIndex = InputDati.leggiIntero("Seleziona il numero del luogo: ", 1, luoghiDisponibili.size()) - 1;
            String luogoSelezionato = luoghiDisponibili.get(luogoIndex);

            // Chiedi il tipo di visita
            String tipoVisita = InputDati.leggiStringaNonVuota("Inserisci il tipo di visita: ");

            // Recupera i volontari disponibili dal database
            String sqlVolontari = "SELECT nome, cognome FROM volontari";
            PreparedStatement pstmtVolontari = conn.prepareStatement(sqlVolontari);
            ResultSet rsVolontari = pstmtVolontari.executeQuery();

            List<String> volontari = new ArrayList<>();
            System.out.println("Elenco dei volontari disponibili:");
            while (rsVolontari.next()) {
                String nomeVolontario = rsVolontari.getString("nome") + " " + rsVolontari.getString("cognome");
                volontari.add(nomeVolontario);
                System.out.println(volontari.size() + ". " + nomeVolontario);
            }

            // Controlla se ci sono volontari disponibili
            if (volontari.isEmpty()) {
                System.out.println("Non ci sono volontari disponibili.");
                return;
            }

            // L'utente seleziona un volontario dalla lista
            int volontarioIndex = InputDati.leggiIntero("Seleziona il numero del volontario: ", 1, volontari.size()) - 1;
            String volontarioSelezionato = volontari.get(volontarioIndex);


            // Chiedi all'utente se vuole inserire una data
            boolean risposta = InputDati.yesOrNo("Vuoi inserire una data per la visita? (sì/no): ");
            LocalDate data = LocalDate.now(); // Data di default è oggi

            if (risposta) {
                int anno = InputDati.leggiIntero("Inserisci l'anno della visita: ");
                int mese = InputDati.leggiIntero("Inserisci il mese della visita (1-12): ");
                int giorno = InputDati.leggiIntero("Inserisci il giorno della visita: ");
                data = LocalDate.of(anno, mese, giorno);
            }

            // Crea l'oggetto Visite
            Visite nuovaVisita = new Visite(luogoSelezionato, tipoVisita, volontarioSelezionato , data);

            // Genera un ID univoco per la visita
            int id = databaseUpdater.getVisiteMap().size() + 1;
            databaseUpdater.getVisiteMap().put(id, nuovaVisita);

            // Aggiungi la visita al database
            databaseUpdater.aggiungiVisita(nuovaVisita);
            System.out.println("Visita aggiunta: " + nuovaVisita);

        } catch (SQLException e) {
            System.out.println("Errore durante il recupero dei luoghi disponibili: " + e.getMessage());
        }
    }

    public void modificaStatoVisita() {
        utilita.modificaStatoVisita();
    }

    public void visualizzaArchivioStorico() {
        utilita.visualizzaArchivioStorico();
    }    

    //Logiche per le credenziali-------------------------------------------------------------------------
    public void modificaCredenzialiConfiguratore() {
        credentialManager.saveNewConfigCredential(configuratori);
    }

    public void modificaCredenzialiVolontario(Volontario volontario) {
        credentialManager.saveNewVolCredential(volontario);
    }

    public void leggiCredenziali(){
        credentialManager.caricaCredenzialiVolontario(volontari);
        credentialManager.caricaCredenzialiTemporanee(temporaryCredentials);
        credentialManager.caricaCredenzialiConfiguratore(configuratori);
    }
}
