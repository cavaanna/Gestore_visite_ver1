package src.it.unibs.ingsw.gestvisit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;

public class DatabaseUpdater {
    private final ExecutorService executorService;

    // HashMap per memorizzare i dati sincronizzati
    private final HashMap<String, Volontario> volontariMap = new HashMap<>();
    private final HashMap<String, Configuratore> configuratoriMap = new HashMap<>();
    private final HashMap<String, Luogo> luoghiMap = new HashMap<>();
    private final HashMap<Integer, Visite> visiteMap = new HashMap<>();

    public DatabaseUpdater(ExecutorService executorService) {
        this.executorService = executorService;
    }

    public void sincronizzaDalDatabase() {
        executorService.submit(() -> {
            caricaVolontari();
            caricaConfiguratori();
            caricaLuoghi();
            caricaVisite();
        });
    }

    private void caricaVolontari() {
        String sql = "SELECT nome, cognome, email, password, tipi_di_visite FROM volontari";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            synchronized (volontariMap) {
                volontariMap.clear();
                while (rs.next()) {
                    String email = rs.getString("email");
                    Volontario volontario = new Volontario(
                            rs.getString("nome"),
                            rs.getString("cognome"),
                            email,
                            rs.getString("password"),
                            rs.getString("tipi_di_visite")
                    );
                    volontariMap.put(email, volontario);
                }
            }
        } catch (SQLException e) {
            System.out.println("Errore durante il caricamento dei volontari: " + e.getMessage());
        }
    }

    private void caricaConfiguratori() {
        String sql = "SELECT nome, cognome, email, password FROM configuratori";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            synchronized (configuratoriMap) {
                configuratoriMap.clear();
                while (rs.next()) {
                    String email = rs.getString("email");
                    Configuratore configuratore = new Configuratore(
                            rs.getString("nome"),
                            rs.getString("cognome"),
                            email,
                            rs.getString("password")
                    );
                    configuratoriMap.put(email, configuratore);
                }
            }
        } catch (SQLException e) {
            System.out.println("Errore durante il caricamento dei configuratori: " + e.getMessage());
        }
    }

    private void caricaLuoghi() {
        String sql = "SELECT nome, descrizione FROM luoghi";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            synchronized (luoghiMap) {
                luoghiMap.clear();
                while (rs.next()) {
                    String nome = rs.getString("nome");
                    Luogo luogo = new Luogo(
                            nome,
                            rs.getString("descrizione")
                    );
                    luoghiMap.put(nome, luogo);
                }
            }
        } catch (SQLException e) {
            System.out.println("Errore durante il caricamento dei luoghi: " + e.getMessage());
        }
    }

    private void caricaVisite() {
        String sql = "SELECT luogo, tipo_visita, volontario, data FROM visite";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            synchronized (visiteMap) {
                visiteMap.clear();
                while (rs.next()) {
                    int id = rs.getRow(); // Usa il numero di riga come ID univoco
                    Visite visita = new Visite(
                            rs.getString("luogo"),
                            rs.getString("tipo_visita"),
                            rs.getString("volontario"),
                            rs.getDate("data").toLocalDate() // Converte la data in LocalDate
                    );
                    visiteMap.put(id, visita);
                }
            }
        } catch (SQLException e) {
            System.out.println("Errore durante il caricamento delle visite: " + e.getMessage());
        }
    }

    // Metodo per aggiungere un volontario al database
    public void aggiungiVolontario(Volontario volontario) {
        String sql = "INSERT INTO volontari (nome, cognome, email, password, tipi_di_visite) VALUES (?, ?, ?, ?, ?)";
        executorService.submit(() -> {
            try (Connection conn = DatabaseConnection.connect();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, volontario.getNome());
                pstmt.setString(2, volontario.getCognome());
                pstmt.setString(3, volontario.getEmail());
                pstmt.setString(4, volontario.getPassword());
                pstmt.setString(5, volontario.getTipiDiVisite());
                pstmt.executeUpdate();
                System.out.println("Volontario aggiunto al database: " + volontario.getNome());
            } catch (SQLException e) {
                System.out.println("Errore durante l'aggiunta del volontario: " + e.getMessage());
            }
        });
    }

    // Metodo per aggiungere un luogo al database
    public void aggiungiLuogo(Luogo luogo) {
        String sql = "INSERT INTO luoghi (nome, descrizione) VALUES (?, ?)";
        executorService.submit(() -> {
            try (Connection conn = DatabaseConnection.connect();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, luogo.getNome());
                pstmt.setString(2, luogo.getDescrizione());
                pstmt.executeUpdate();
                System.out.println("Luogo aggiunto al database: " + luogo.getNome());
            } catch (SQLException e) {
                System.out.println("Errore durante l'aggiunta del luogo: " + e.getMessage());
            }
        });
    }

    // Metodo per aggiungere una visita al database
    public void aggiungiVisita(Visite visita) {
        String sql = "INSERT INTO visite (luogo, tipo_visita, volontario, data, stato, max_persone) VALUES (?, ?, ?, ?, ?, ?)";
        executorService.submit(() -> {
            try (Connection conn = DatabaseConnection.connect();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, visita.getLuogo());
                pstmt.setString(2, visita.getTipoVisita());
                pstmt.setString(3, visita.getVolontario());
                pstmt.setDate(4, visita.getData() != null ? java.sql.Date.valueOf(visita.getData()) : null);
                pstmt.setString(5, "Proposta"); // Stato iniziale
                pstmt.setInt(6, 10); // Valore predefinito per max_persone
                pstmt.executeUpdate();
                System.out.println("Visita aggiunta al database: " + visita.getLuogo());
            } catch (SQLException e) {
                System.out.println("Errore durante l'aggiunta della visita: " + e.getMessage());
            }
        });
    }

    public HashMap<String, Volontario> getVolontariMap() {
        return volontariMap;
    }

    public HashMap<String, Configuratore> getConfiguratoriMap() {
        return configuratoriMap;
    }

    public HashMap<String, Luogo> getLuoghiMap() {
        return luoghiMap;
    }

    public HashMap<Integer, Visite> getVisiteMap() {
        return visiteMap;
    }
}