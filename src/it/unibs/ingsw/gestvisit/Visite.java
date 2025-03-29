package src.it.unibs.ingsw.gestvisit;

import java.time.LocalDate;

public class Visite {
    private String luogo;
    private String tipoVisita;
    private String volontario;
    private LocalDate data;

    // Costruttore
    public Visite(String luogo, String tipoVisita, String volontario, LocalDate data) {
        this.luogo = luogo;
        this.tipoVisita = tipoVisita;
        this.volontario = volontario;
        this.data = data;
    }

    // Getter e Setter
    public String getLuogo() {
        return luogo;
    }

    public void setLuogo(String luogo) {
        this.luogo = luogo;
    }

    public String getTipoVisita() {
        return tipoVisita;
    }

    public void setTipoVisita(String tipoVisita) {
        this.tipoVisita = tipoVisita;
    }

    public String getVolontario() {
        return volontario;
    }

    public void setVolontario(String volontario) {
        this.volontario = volontario;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    // Metodo toString per rappresentare l'oggetto come stringa
    @Override
    public String toString() {
        return "Visite{" +
                "luogo='" + luogo + '\'' +
                ", tipoVisita='" + tipoVisita + '\'' +
                ", volontario='" + volontario + '\'' +
                ", data=" + data +
                '}';
    }
}
   