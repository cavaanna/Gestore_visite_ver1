package src.it.unibs.ingsw.gestvisit;

import it.unibs.mylib.InputDati;

public class CredentialManager {

    private DatabaseUpdater databaseUpdater ;
    private Volontario volontarioCorrente = null;
    private Configuratore configuratoreCorrente = null;
    
    public CredentialManager(DatabaseUpdater databaseUpdater) {
        this.databaseUpdater = databaseUpdater;
    }

    //Autenticazione-------------------------------------------------------------------------
    public Utente autentica() {
        String email = InputDati.leggiStringaNonVuota("Inserisci (email): ");
        String password = InputDati.leggiStringaNonVuota("Inserisci la password: ");
        String tipoUtente = verificaCredenziali(email, password);
        boolean credenzialiModificate = isPasswordModificata(email);
    
        if (tipoUtente == null) {
            System.out.println("Credenziali non valide.");
            return null;
        }
    
        switch (tipoUtente) {
            case "Volontario":
                System.out.println("Accesso come Volontario.");
                Volontario volontario = databaseUpdater.getVolontariMap().get(email);
                if (volontario == null) {
                    System.out.println("Errore: volontario non trovato.");
                    return null;
                }
    
                if (!credenzialiModificate) {
                    System.out.println("Hai credenziali temporanee. Ti preghiamo di modificarle.");
                    salvaNuovaPasswordVol(volontario);
                }
                return volontario;
    
            case "Configuratore":
                System.out.println("Accesso come Configuratore.");
                Configuratore configuratore = databaseUpdater.getConfiguratoriMap().get(email);
                if (configuratore == null) {
                    System.out.println("Errore: configuratore non trovato.");
                    return null;
                }
                return configuratore;
    
            default:
                System.out.println("Ruolo non riconosciuto: " + tipoUtente);
                return null;
        }
    }

    // public Utente autentica(){
    //     String email = InputDati.leggiStringaNonVuota("Inserisci (email): ");
    //     String password = InputDati.leggiStringaNonVuota("Inserisci la password: ");
    //     String tipo_utente = verificaCredenziali(email, password);
    //     Boolean credenzialiModificate = isPasswordModificata(email);
    
    //     if (tipo_utente == null) {
    //         System.out.println("Credenziali non valide.");
    //         return null;
    //     }
    
    //     Menu menu;
    //     switch (tipo_utente) {
    //         case "Volontario":
    //             System.out.println("Accesso come Volontario.");
    
    //             // Cerca il volontario corrispondente all'email
    //             volontarioCorrente = databaseUpdater.getVolontariMap().get(email);
    //             if (volontarioCorrente == null) {
    //                 System.out.println("Errore: volontario non trovato.");
    //                 return null;
    //             }
    
    //             // Controlla se il volontario ha credenziali temporanee
    //             if (!credenzialiModificate) {
    //                 System.out.println("Hai credenziali temporanee. Ti preghiamo di modificarle.");
    //                 salvaNuovaPasswordVol(volontarioCorrente);
    //             }
    //             menu = new MenuVolontario();
    //             return volontarioCorrente;
    
    //         case "Configuratore":
    //             System.out.println("Accesso come Configuratore.");
    //             configuratoreCorrente = databaseUpdater.getConfiguratoriMap().get(email);
    //             if (configuratoreCorrente == null) {
    //                 System.out.println("Errore: configuratore non trovato.");
    //                 return null;
    //             }
    //             menu = new MenuConfiguratore();
    //             return configuratoreCorrente;

    
    //         case "TEMP":
    //             System.out.println("Accesso come configuratore con credenziali temporanee.");
    //             salvaNuoveCredenzialiConf();
    //             menu = new MenuConfiguratore();
    //             break;
    
    //         default:
    //             System.out.println("Ruolo non riconosciuto: " + tipo_utente);
    //             return null;
    //     }
    //     // Mostra il menu corrispondente
    //     menu.mostraMenu();
    // }

    public void caricaCredenzialiTemporanee() {
        databaseUpdater.getTemporaryCredentials();
    }

    public void salvaNuovaPasswordVol(Volontario volontario) {    
        // Inserisci la nuova password
        String nuovaPassword = InputDati.leggiStringaNonVuota("Inserisci la nuova password: ");
        
        // Aggiorna la password nella HashMap
        volontario.setPassword(nuovaPassword);
        databaseUpdater.getVolontariMap().put(volontario.getEmail(), volontario);

        // Sincronizza con il database
        databaseUpdater.aggiornaPswVolontario(volontario.getEmail(), nuovaPassword);
    }

    public void salvaNuoveCredenzialiConf() {
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
        databaseUpdater.aggiungiNuovoConf(updatedConfiguratore);
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
}
