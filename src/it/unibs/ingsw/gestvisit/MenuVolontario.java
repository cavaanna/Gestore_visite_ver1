package src.it.unibs.ingsw.gestvisit;

import it.unibs.mylib.MyMenu;

public class MenuVolontario implements Menu {
    private static final String[] OPZIONI_VOLONTARIO = {
        "Visualizza visite assegnate",
        "Aggiorna stato visita",
        "Esci"
    };

    @Override
    public void mostraMenu() {
        boolean continua = true;
        do {
            MyMenu menu = new MyMenu("Menu Volontario", OPZIONI_VOLONTARIO);
            int scelta = menu.scegli();

            switch (scelta) {
                case 1:
                    System.out.println("Visualizza visite assegnate.");
                    // Implementa la logica per visualizzare le visite
                    break;
                case 2:
                    System.out.println("Aggiorna stato visita.");
                    // Implementa la logica per aggiornare lo stato delle visite
                    break;
                case 3:
                    continua = false;
                    break;
                default:
                    System.out.println("Scelta non valida.");
            }
        } while (continua);
    }
}
