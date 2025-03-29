package src.it.unibs.ingsw.gestvisit;

import java.time.LocalDate;

import it.unibs.fp.mylib.MyMenu;

public class MenuConfiguratore implements Menu {
    private static final String[] SELECT = {"Aggiungi Luogo", "Aggiungi Volontario", 
    "Visualizza Luoghi", "Visualizza Volontari", "Assegna Visita", "Visualizza Visite", 
    "Modifica numero massimo di persone per visita", 
    "Modifica stato della visita", "Visualizza visite per stato", "Visualizza archivio storico"};
    private VisitManager visitManager = new VisitManager();

    @Override
    public void mostraMenu() {
        // Utilita.caricaLuoghi(luoghi);
        // Utilita.popolaVolontari(volontari);
        //Utilita.creazioneTipiVisite(tipiVisita);
        boolean goOn = true;
        System.out.printf("oggi è il: %d/%d/%d\n", LocalDate.now().getDayOfMonth(), LocalDate.now().getMonthValue(), LocalDate.now().getYear());
        do {
            MyMenu menu = new MyMenu("What do you want to do?\n", SELECT);
            int chosed = menu.scegli();

            if (chosed != 0) {
                if (chosed == 1) {
                    visitManager.aggiungiLuogo();
                } else if (chosed == 2) {
                    visitManager.addVolontario();
                } else if (chosed == 3) {
                    visitManager.showLuoghi();
                } else if (chosed == 4) {
                    visitManager.showVolontari();
                } else if (chosed == 5) {
                    visitManager.assegnaVisita();
                } else if (chosed == 6) {
                    visitManager.showVisite();
                } else if (chosed == 7) {
                    visitManager.modifycaNumeroMaxPersonePerVisita();
                }else if (chosed == 8) {
                    visitManager.modificaStatoVisita();
                }else if (chosed == 9) {
                    visitManager.visualizzaVisitePerStato();
                }else if(chosed == 10){
                    visitManager.visualizzaArchivioStorico();
                }else if (chosed == 0) {
                    goOn = false;
                }
            } else
                goOn = false;
        } while (goOn);
    }

}
