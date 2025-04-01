package src.it.unibs.ingsw.gestvisit;

import it.unibs.mylib.InputDati;

public class CredentialManager {

    private DatabaseUpdater databaseUpdater ;
    private Volontario volontarioCorrente = null;
    
    public CredentialManager(DatabaseUpdater databaseUpdater) {
        this.databaseUpdater = databaseUpdater;
    }

    //Autenticazione-------------------------------------------------------------------------
    public void autentica(){
        String email = InputDati.leggiStringaNonVuota("Inserisci (email): ");
        String password = InputDati.leggiStringaNonVuota("Inserisci la password: ");
        String tipo_utente = verificaCredenziali(email, password);
        Boolean credenzialiModificate = isPasswordModificata(email);
    
        if (tipo_utente == null) {
            System.out.println("Credenziali non valide.");
            return;
        }
    
        Menu menu;
        switch (tipo_utente) {
            case "Volontario":
                System.out.println("Accesso come Volontario.");
    
                // Cerca il volontario corrispondente all'email
                volontarioCorrente = databaseUpdater.getVolontariMap().get(email);
                if (volontarioCorrente == null) {
                    System.out.println("Errore: volontario non trovato.");
                    return;
                }
    
                // Controlla se il volontario ha credenziali temporanee
                if (!credenzialiModificate) {
                    System.out.println("Hai credenziali temporanee. Ti preghiamo di modificarle.");
                    modificaCredenzialiVolontario(volontarioCorrente);
                }
                menu = new MenuVolontario();
                break;
    
            case "Configuratore":
                System.out.println("Accesso come Configuratore.");
                menu = new MenuConfiguratore();
                break;
    
            case "TEMP":
                System.out.println("Accesso come configuratore con credenziali temporanee.");
                modificaCredenzialiConfiguratore();
                menu = new MenuConfiguratore();
                break;
    
            default:
                System.out.println("Ruolo non riconosciuto: " + tipo_utente);
                return;
        }
        // Mostra il menu corrispondente
        menu.mostraMenu();
    }

    public void caricaCredenzialiTemporanee() {
        databaseUpdater.getTemporaryCredentials();
    }

    public void saveNewVolCredential(Volontario volontario) {    
        // Inserisci la nuova password
        String nuovaPassword = InputDati.leggiStringaNonVuota("Inserisci la nuova password: ");
        
        // Aggiorna la password nella HashMap
        volontario.setPassword(nuovaPassword);
        databaseUpdater.getVolontariMap().put(volontario.getEmail(), volontario);

        // Sincronizza con il database
        databaseUpdater.sincronizzaConDatabase();
    }

    public void saveNewConfigCredential() {
        // Raccogli i dati del nuovo configuratore
        String newEmail = InputDati.leggiStringaNonVuota("Inserisci la nuova email: ");
        String newPassword = InputDati.leggiStringaNonVuota("Inserisci la nuova password: ");
        String name = InputDati.leggiStringaNonVuota("Inserisci il nome: ");
        String surname = InputDati.leggiStringaNonVuota("Inserisci il cognome: ");
        
        // Crea un nuovo oggetto Configuratore
        Configuratore updatedConfiguratore = new Configuratore(name, surname, newEmail, newPassword);
        
        // Aggiorna la HashMap
        databaseUpdater.getConfiguratoriMap().put(newEmail, updatedConfiguratore);
    
        // Sincronizza con il database
        databaseUpdater.sincronizzaConDatabase();
    }

    // Restituisci il tipo_utente dell'utente o null se non autenticato
    public String verificaCredenziali(String email, String password) {
        String tipo_utente = databaseUpdater.getTipoUtente(email, password);
        return tipo_utente;
    }

    // Controlla se la password è stata modificata
    public boolean isPasswordModificata(String email) {
        Boolean passwordModificata = databaseUpdater.isPasswordModificata(email);
        return passwordModificata;
    }

    public void modificaCredenzialiConfiguratore() {
        saveNewConfigCredential();
    }

    public void modificaCredenzialiVolontario(Volontario volontario) {
        saveNewVolCredential(volontario);
    }



}
