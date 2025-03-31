package src.it.unibs.ingsw.gestvisit;

import it.unibs.fp.mylib.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class VisitManager {

    // Attributi------------------------------------------------------------------------------
    private ArrayList<Volontario> volontari = new ArrayList<>();    
    private ArrayList<Configuratore> configuratori = new ArrayList<>();
    private ArrayList<TemporaryCredential> temporaryCredentials = new ArrayList<>();
    private CredentialManager credentialManager = new CredentialManager();
    private final ExecutorService executorService = Executors.newSingleThreadExecutor(); // Pool con thread caching
    private final DatabaseUpdater databaseUpdater = new DatabaseUpdater(executorService);
    private final Utilita utilita = new Utilita(databaseUpdater);

    //Gestione Thread-------------------------------------------------------------------------
    public VisitManager() {
        // Sincronizza i dati iniziali dal database
        databaseUpdater.sincronizzaDalDatabase();
    }

    public void stopExecutorService() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
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
        System.out.println("Luogo aggiunto: " + nuovoLuogo);

        databaseUpdater.aggiungiLuogo(nuovoLuogo);
        
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
        utilita.assegnaVisita();
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
