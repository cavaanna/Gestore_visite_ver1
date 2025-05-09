package src.it.unibs.ingsw.gestvisit;

import it.unibs.mylib.MyMenu;
import java.time.LocalDate;

public class MenuVolontario implements Menu {
    private VisitManager visitManager = new VisitManager(); // Inizializza il VisitManager
    private static final String[] OPZIONI_VOLONTARIO = {
        "Visualizza visite assegnate",
<<<<<<< HEAD
=======
       // "Inserisci preferenze",
>>>>>>> abba7a4cf2ea03957507777c3ae08e213ddab7ef
        "Inserisci disponibilità"
    };

    VisitManager visitManager = new VisitManager();

    @Override
    public void mostraMenu() {
        // Inizializza il menu con le opzioni disponibili
        boolean goOn = true;
        System.out.printf("oggi è il: %d/%d/%d\n", LocalDate.now().getDayOfMonth(), LocalDate.now().getMonthValue(), LocalDate.now().getYear());
        do {
            MyMenu menu = new MyMenu("Digitare l'opzione desiderata\n", OPZIONI_VOLONTARIO);
            int chosed = menu.scegli();

            if (chosed != 0) {
                if (chosed == 1) {
                    //Logica per visualizzare le visite assegnate al volontario
                    visitManager.visualizzaVisiteVolontario();
<<<<<<< HEAD
                } else if (chosed == 2) {
                    //Logica per inserire le disponibilità del volontario
                    //VisitManager.inserisciDisponibilita();
                } else if (chosed == 4) {
=======
                } //else if (chosed == 2) {
                    //Logica per inserire le preferenze del volontario
                 else if (chosed == 2) {
                    //Logica per inserire le disponibilità del volontario
                //} else if (chosed == 4) {
>>>>>>> abba7a4cf2ea03957507777c3ae08e213ddab7ef
                    //TODO
                } else if (chosed == 0) {
                    goOn = false;
                }
            } else
                goOn = false;
        } while (goOn);
    }
}
