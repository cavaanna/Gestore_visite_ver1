package src.it.unibs.ingsw.gestvisit;

//import it.unibs.mylib.*;
import it.unibs.mylib.InputDati;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class VisitManager {

    // Attributi------------------------------------------------------------------------------  
    private final ExecutorService executorService = Executors.newFixedThreadPool(4); // Pool con thread caching
    private final DatabaseUpdater databaseUpdater = new DatabaseUpdater(executorService);
    private final Utilita utilita = new Utilita(databaseUpdater);
    private final CredentialManager credentialManager = new CredentialManager(databaseUpdater);

    
    

    //Gestione Thread-------------------------------------------------------------------------
    public VisitManager() {
        // Sincronizza i dati iniziali dal database
        databaseUpdater.sincronizzaDalDatabase();
        // Avvia il thread di aggiornamento periodico
        databaseUpdater.avviaSincronizzazioneConSleep(); // Esegui ogni 5 secondi
        System.out.println("Thread di aggiornamento avviato.");
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
        databaseUpdater.arrestaSincronizzazioneConSleep();
        System.out.println("ExecutorService arrestato.");
        
    }


    //Autenticazione-------------------------------------------------------------------------
    public void autentica() {
        credentialManager.autentica();
    }

    
    //Logiche per i luoghi-------------------------------------------------------------------------
    public void aggiungiLuogo() {
        String nome = InputDati.leggiStringaNonVuota("inserire il nome del luogo: ");
        String descrizione = InputDati.leggiStringaNonVuota("inserire la descrizione del luogo: ");

        Luogo nuovoLuogo = new Luogo(nome, descrizione);
        databaseUpdater.getLuoghiMap().put(nome, nuovoLuogo);
        databaseUpdater.sincronizzaConDatabase();    
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
        // Aggiungi il volontario alla HashMap
        databaseUpdater.getVolontariMap().put(email, nuovoVolontario);

        // Sincronizza con il database
        databaseUpdater.sincronizzaConDatabase();

        System.out.println("Volontario aggiunto: " + nuovoVolontario);
    }

    public void mostraVolontari() {
        utilita.stampaVolontari();
    }

    //Logiche per le visite-------------------------------------------------------------------------
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
        utilita.aggiungiVisita();
    }

    public void modificaStatoVisita() {
        utilita.modificaStatoVisita();
    }

    public void visualizzaArchivioStorico() {
        utilita.visualizzaArchivioStorico();
    }    

}
