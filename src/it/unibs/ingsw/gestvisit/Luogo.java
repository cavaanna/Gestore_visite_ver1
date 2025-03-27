package src.it.unibs.ingsw.gestvisit;
import it.unibs.fp.mylib.*;
import java.util.*;

class Luogo {
    
    private String nome;
    private String descrizione;

    public Luogo(String nome, String descrizione) {
        this.nome = nome;
        this.descrizione = descrizione;
    }

    public String getNome() {
        return nome;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public static Luogo creaLuogo(){
        String nome = InputDati.leggiStringaNonVuota("inserire il nome del luogo: ");
        String descrizione = InputDati.leggiStringaNonVuota("inserire una descrizione: ");
        return new Luogo(nome, descrizione);
    }

    public static Luogo creaLuogoUtente(String nome, String descrizione) {
        return new Luogo(nome, descrizione);
    }

    @Override
    public String toString() {
        return "Luogo [descrizione=" + descrizione + ", nome=" + nome + "]";
    }

}
