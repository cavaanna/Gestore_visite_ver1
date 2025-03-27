package src.it.unibs.ingsw.gestvisit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import it.unibs.fp.mylib.InputDati;

public class Utilita {

    public static void caricaLuoghi(List<Luogo> luoghi) {
        String sql = "SELECT nome, descrizione FROM luoghi";

        try (Connection conn = DatabaseConnection.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String nome = rs.getString("nome");
                String descrizione = rs.getString("descrizione");

                Luogo luogo = new Luogo(nome, descrizione);
                luoghi.add(luogo);
            }
        } catch (SQLException e) {
            System.out.println("Errore durante il caricamento dei luoghi: " + e.getMessage());
        }
    }

    public static void popolaVolontari(List<Volontario> volontari) {
        String sql = "SELECT nome, cognome, email, password, tipi_di_visite FROM volontari";
    
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
    
            while (rs.next()) {
                String nome = rs.getString("nome");
                String cognome = rs.getString("cognome");
                String email = rs.getString("email");
                String password = rs.getString("password");
                String tipiDiVisite = rs.getString("tipi_di_visite");
    
                Volontario volontario = new Volontario(nome, cognome, email, password, tipiDiVisite);
                volontari.add(volontario);
            }
        } catch (SQLException e) {
            System.out.println("Errore durante il caricamento dei volontari: " + e.getMessage());
        }
    }

    public static void salvaLuoghi(Luogo luogo) {
        String sql = "INSERT INTO luoghi (nome, descrizione) VALUES (?, ?)";
    
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
    
            pstmt.setString(1, luogo.getNome());
            pstmt.setString(2, luogo.getDescrizione());
            pstmt.executeUpdate();
    
            System.out.println("Luogo salvato con successo.");
        } catch (SQLException e) {
            System.out.println("Errore durante il salvataggio del luogo: " + e.getMessage());
        }
    }

    public static void salvaVolontari(Volontario volontario) {
        String sql = "INSERT INTO volontari (nome, cognome, email, password, tipi_di_visite) VALUES (?, ?, ?, ?, ?)";
    
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
    
            pstmt.setString(1, volontario.getNome());
            pstmt.setString(2, volontario.getCognome());
            pstmt.setString(3, volontario.getEmail());
            pstmt.setString(4, volontario.getPassword());
            pstmt.setString(5, volontario.getTipiDiVisite());
            pstmt.executeUpdate();
    
            System.out.println("Volontario salvato con successo.");
        } catch (SQLException e) {
            System.out.println("Errore durante il salvataggio del volontario: " + e.getMessage());
        }
    }

    public static void caricaVisite(HashMap<Luogo, HashMap<String, List<String>>> mappaVisite) {
        String sql = "INSERT INTO visite (luogo, tipo_visita, volontario, data) VALUES (?, ?, ?, ?)";
    
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
    
            for (Luogo luogo : mappaVisite.keySet()) {
                for (String tipoVisita : mappaVisite.get(luogo).keySet()) {
                    for (String volontario : mappaVisite.get(luogo).get(tipoVisita)) {
                        pstmt.setString(1, luogo.getNome());
                        pstmt.setString(2, tipoVisita);
                        pstmt.setString(3, volontario);
                        pstmt.setDate(4, java.sql.Date.valueOf(LocalDate.now())); // Usa la data corrente
                        pstmt.addBatch();
                    }
                }
            }
            pstmt.executeBatch();
            System.out.println("Visite salvate con successo.");
        } catch (SQLException e) {
            System.out.println("Errore durante il salvataggio delle visite: " + e.getMessage());
        }
    }
    
    // Metodo di supporto per ottenere l'ID di un luogo
    private static int getLuogoId(Connection conn, String nomeLuogo) throws SQLException {
        String sql = "SELECT id FROM luoghi WHERE nome = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nomeLuogo);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        }
        return -1; // Ritorna -1 se il luogo non è trovato
    }

    public static void stampaVisite() {
        String sql = "SELECT luogo, tipo_visita, volontario, data FROM visite";
    
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
    
            System.out.println("Visite:");
            while (rs.next()) {
                String luogo = rs.getString("luogo");
                String tipoVisita = rs.getString("tipo_visita");
                String volontario = rs.getString("volontario");
                LocalDate data = rs.getDate("data").toLocalDate();
    
                System.out.println("Luogo: " + luogo);
                System.out.println("Tipo Visita: " + tipoVisita);
                System.out.println("Volontario: " + volontario);
                System.out.println("Data: " + data);
                System.out.println("-------------------------");
            }
        } catch (SQLException e) {
            System.out.println("Errore durante la stampa delle visite: " + e.getMessage());
        }
    }

    public static void stampaLuoghi() {
        String sql = "SELECT nome, descrizione FROM luoghi";
    
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
    
            System.out.println("Luoghi:");
            while (rs.next()) {
                String nome = rs.getString("nome");
                String descrizione = rs.getString("descrizione");
    
                System.out.println("Nome: " + nome);
                System.out.println("Descrizione: " + descrizione);
                System.out.println("-------------------------");
            }
        } catch (SQLException e) {
            System.out.println("Errore durante la stampa dei luoghi: " + e.getMessage());
        }
    }

    public static void stampaVolontari() {
        String sql = "SELECT nome, cognome, email, tipi_di_visite FROM volontari";
    
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
    
            System.out.println("Volontari:");
            while (rs.next()) {
                String nome = rs.getString("nome");
                String cognome = rs.getString("cognome");
                String email = rs.getString("email");
                String tipiDiVisite = rs.getString("tipi_di_visite");
    
                System.out.println("Nome: " + nome + " " + cognome);
                System.out.println("Email: " + email);
                System.out.println("Tipi di Visite: " + tipiDiVisite);
                System.out.println("-------------------------");
            }
        } catch (SQLException e) {
            System.out.println("Errore durante la stampa dei volontari: " + e.getMessage());
        }
    }

    public static void assegnaVisita() {
        try (Connection conn = DatabaseConnection.connect()) {
            // Recupera i luoghi disponibili
            String sqlLuoghi = "SELECT nome FROM luoghi";
            PreparedStatement pstmtLuoghi = conn.prepareStatement(sqlLuoghi);
            ResultSet rsLuoghi = pstmtLuoghi.executeQuery();

            List<String> luogoNomi = new ArrayList<>();

            System.out.println("Elenco dei luoghi disponibili:");
            while (rsLuoghi.next()) {
                String nome = rsLuoghi.getString("nome");
                luogoNomi.add(nome);
                System.out.println(luogoNomi.size() + ". " + nome);
            }

            int luogoIndex = InputDati.leggiIntero("Seleziona il numero del luogo: ", 1, luogoNomi.size()) - 1;
            String luogoNomeScelto = luogoNomi.get(luogoIndex);

            // Recupera i tipi di visita per il luogo selezionato
            String sqlTipiVisita = "SELECT DISTINCT tipo_visita FROM visite WHERE luogo = ?";
            PreparedStatement pstmtTipiVisita = conn.prepareStatement(sqlTipiVisita);
            pstmtTipiVisita.setString(1, luogoNomeScelto);
            ResultSet rsTipiVisita = pstmtTipiVisita.executeQuery();

            List<String> tipiVisita = new ArrayList<>();
            System.out.println("Tipi di visita disponibili per il luogo " + luogoNomeScelto + ":");
            while (rsTipiVisita.next()) {
                String tipoVisita = rsTipiVisita.getString("tipo_visita");
                tipiVisita.add(tipoVisita);
                System.out.println(tipiVisita.size() + ". " + tipoVisita);
            }

            int tipoVisitaIndex = InputDati.leggiIntero("Seleziona il numero del tipo di visita: ", 1, tipiVisita.size()) - 1;
            String tipoVisitaScelto = tipiVisita.get(tipoVisitaIndex);

            // Recupera i volontari disponibili
            String sqlVolontari = "SELECT nome, cognome FROM volontari";
            PreparedStatement pstmtVolontari = conn.prepareStatement(sqlVolontari);
            ResultSet rsVolontari = pstmtVolontari.executeQuery();

            List<String> volontarioNomi = new ArrayList<>();

            System.out.println("\nElenco dei volontari disponibili:");
            while (rsVolontari.next()) {
                String nome = rsVolontari.getString("nome") + " " + rsVolontari.getString("cognome");
                volontarioNomi.add(nome);
                System.out.println(volontarioNomi.size() + ". " + nome);
            }

            int volontarioIndex = InputDati.leggiIntero("Seleziona il numero del volontario: ", 1, volontarioNomi.size()) - 1;
            String volontarioNomeScelto = volontarioNomi.get(volontarioIndex);

            // Calcola le date valide (giorni feriali del terzo mese successivo all'attuale)
            LocalDate oggi = LocalDate.now();
            YearMonth meseTarget = YearMonth.of(oggi.getYear(), oggi.getMonth().plus(3));
            List<LocalDate> dateValide = new ArrayList<>();

            for (int giorno = 1; giorno <= meseTarget.lengthOfMonth(); giorno++) {
                LocalDate data = meseTarget.atDay(giorno);
                if (data.getDayOfWeek() != DayOfWeek.SATURDAY && data.getDayOfWeek() != DayOfWeek.SUNDAY) {
                    dateValide.add(data);
                }
            }

            // Mostra le date valide all'utente
            System.out.println("\nDate disponibili per la visita:");
            for (int i = 0; i < dateValide.size(); i++) {
                System.out.println((i + 1) + ". " + dateValide.get(i));
            }

            int dataIndex = InputDati.leggiIntero("Seleziona il numero della data: ", 1, dateValide.size()) - 1;
            LocalDate dataVisita = dateValide.get(dataIndex);

            // Assegna la visita al volontario
            String sqlAssegnaVisita = "INSERT INTO visite (luogo, tipo_visita, volontario, data) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmtAssegna = conn.prepareStatement(sqlAssegnaVisita);
            pstmtAssegna.setString(1, luogoNomeScelto);
            pstmtAssegna.setString(2, tipoVisitaScelto);
            pstmtAssegna.setString(3, volontarioNomeScelto);
            pstmtAssegna.setDate(4, java.sql.Date.valueOf(dataVisita));
            pstmtAssegna.executeUpdate();

            System.out.println("Visita assegnata con successo per la data " + dataVisita + "!");
        } catch (SQLException e) {
            System.out.println("Errore durante l'assegnazione della visita: " + e.getMessage());
        }
    }



}