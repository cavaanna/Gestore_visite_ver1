package src.it.unibs.ingsw.gestvisit;

import it.unibs.fp.mylib.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

public class VisitManager {

    private static final String[] SELECT = {"Aggiungi Luogo", "Aggiungi Volontario", "Visualizza Luoghi", "Visualizza Volontari", "Assegna Visita", "Visualizza Visite", "Modifica numero massimo di persone per visita", "Visualizza data", "Visualizza visite per stato", "Esci"};
    private ArrayList<Luogo> luoghi = new ArrayList<>();
    private ArrayList<Volontario> volontari = new ArrayList<>();
    private ArrayList<Configuratore> configuratori = new ArrayList<>();
    private ArrayList<TemporaryCredential> temporaryCredentials = new ArrayList<>();
    private CredentialManager credentialManager = new CredentialManager();
    private HashMap<Luogo, Visite> mappaVisiteLuogo = new HashMap<>();
    private ArrayList<Visite> tipiVisita = new ArrayList<>();
    private boolean credenzialiModificate = false;

    /**
     * 
     */
    public void menu() {
        Utilita.caricaLuoghi(luoghi);
        Utilita.popolaVolontari(volontari);
        //Utilita.creazioneTipiVisite(tipiVisita);
        boolean goOn = true;
        System.out.printf("oggi è il: %d/%d/%d\n", LocalDate.now().getDayOfMonth(), LocalDate.now().getMonthValue(), LocalDate.now().getYear());
        do {
            MyMenu menu = new MyMenu("What do you want to do?\n", SELECT);
            int chosed = menu.scegli();

            if (chosed != 0) {
                if (chosed == 1) {
                    System.out.println("U choose Luogo");
                    addLuogo();
                } else if (chosed == 2) {
                    System.out.println("U choose Volontario");
                    addVolontario();
                } else if (chosed == 3) {
                    showLuoghi();
                } else if (chosed == 4) {
                    showVolontari();
                } else if (chosed == 5) {
                    assegnaVisita();
                } else if (chosed == 6) {
                    showVisite();
                } else if (chosed == 7) {
                    modifycaNumeroMaxPersonePerVisita();
                }else if (chosed == 8) {
                    showCalendarVisit();
                }else if (chosed == 9) {
                    //showVisitePerStato();
                } else if (chosed == 0) {
                    goOn = false;
                }
            } else
                goOn = false;
        } while (goOn);
    }

    public void addLuogo() {
        String nome = InputDati.leggiStringaNonVuota("inserire il nome del luogo: ");
        String descrizione = InputDati.leggiStringaNonVuota("inserire una descrizione: ");
        Luogo luogo = new Luogo(nome, descrizione);
        luoghi.add(luogo);
        
        Utilita.salvaLuoghi(luogo);
    }

    public void addVolontario() {
        String nome = InputDati.leggiStringaNonVuota("inserire il nome del volontario: ");
        String cognome = InputDati.leggiStringaNonVuota("inserire il cognome del volontario: ");
        String email = InputDati.leggiStringaNonVuota("inserire l'email del volontario: ");
        String nomeUtente = email;
        String password = InputDati.leggiStringaNonVuota("inserire la password: ");
        Volontario volontario = new Volontario(nome, cognome, email, nomeUtente, password);
        volontari.add(volontario);

        Utilita.salvaVolontari(volontario);
    }

    public void showLuoghi() {
        Utilita.stampaLuoghi();
    }

    public void showVolontari() {
        Utilita.stampaVolontari();
    }

    public void assegnaVisita() {
        Utilita.assegnaVisita();
    }

    public void showVisite() {
        Utilita.stampaVisite();
    }

    public void modifycaNumeroMaxPersonePerVisita() {
       int numeroMax = InputDati.leggiInteroConMinimo("Inserisci il numero massimo di persone per visita: ", 2);
        for(int i = 0; i < tipiVisita.size(); i++){
            tipiVisita.get(i).setMaxPersonePerVisita(numeroMax);
        }
    }

    public void showCalendarVisit(){
        Calendar.creaCalendario(LocalDate.now().getYear(), LocalDate.now().getMonthValue());
        System.out.printf("oggi è il: %d/%d/%d", LocalDate.now().getDayOfMonth(), LocalDate.now().getMonthValue(), LocalDate.now().getYear());
        System.out.println();
    }

    public boolean autenticaConfiguratore() {
        String nomeUtente = InputDati.leggiStringaNonVuota("Inserisci il nome utente (email): ");
        String password = InputDati.leggiStringaNonVuota("Inserisci la password: ");

        boolean[] esito = credentialManager.verificaCredenziali(nomeUtente, password);
        if (esito[0]) {
            if (esito[1]) {
                modificaCredenzialiConfiguratore();
            }
            return true;
        } else {
            System.out.println("Credenziali non valide.");
            return false;
        }
    }

    public void modificaCredenzialiConfiguratore() {
        credentialManager.saveNewConfigCredential(configuratori);
    }

    public void leggiCredenzialiConfiguratore() {
        credentialManager.caricaCredenzialiConfiguratore(configuratori);
    }

    public boolean isCredenzialiModificate() {
        return credenzialiModificate;
    }
}
