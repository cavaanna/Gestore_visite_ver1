package src.it.unibs.ingsw.gestvisit;

public class MainVisite {
    public static void main(String[] args) {
    VisitManager visitManager = VisitManager.getInstance();        //manager.leggiCredenziali();
        visitManager.autentica();

        // Arresta il thread di aggiornamento prima di uscire
        visitManager.stopExecutorService();
        
    }
}
