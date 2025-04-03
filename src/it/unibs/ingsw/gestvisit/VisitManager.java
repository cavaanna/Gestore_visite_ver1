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
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
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
        utilita.aggiungiLuogo();
    }

    public void mostraLuoghi() {
        utilita.stampaLuoghi();
    }

    //Logiche per i volontari-------------------------------------------------------------------------
    public void aggiungiVolontario() {
        utilita.aggiungiVolontario();
    }

    public void mostraVolontari() {
        utilita.stampaVolontari();
    }

    //Logiche per le visite-------------------------------------------------------------------------
    public void mostraVisite() {        
        utilita.stampaVisite();
    }
    
    public void visualizzaVisitePerStato(){
        utilita.stampaVisitePerStato ();
    }

    public void modificaNumeroMaxPersonePerVisita() {
        int numeroMax = InputDati.leggiInteroConMinimo("Inserisci il numero massimo di persone per visita: ", 2);
        utilita.modificaMaxPersone(numeroMax);
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
