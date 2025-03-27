package src.it.unibs.ingsw.gestvisit;

import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import it.unibs.fp.mylib.InputDati;

public class CredentialManager {
    
    public void caricaCredenzialiConfiguratore(List<Configuratore> configuratori) {
        String sql = "SELECT nome, cognome, email, password FROM configuratori";
    
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
    
            while (rs.next()) {
                String nome = rs.getString("nome");
                String cognome = rs.getString("cognome");
                String email = rs.getString("email");
                String password = rs.getString("password");
    
                Configuratore configuratore = new Configuratore(nome, cognome, email, password);
                configuratori.add(configuratore);
            }
        } catch (SQLException e) {
            System.out.println("Errore durante il caricamento delle credenziali: " + e.getMessage());
        }
    }

    public void saveNewConfigCredential(List<Configuratore> configuratori) {
        String newNomeUtente = InputDati.leggiStringaNonVuota("Inserisci il nuovo nome utente (email): ");
        String newPassword = InputDati.leggiStringaNonVuota("Inserisci la nuova password: ");
        String name = InputDati.leggiStringaNonVuota("Inserisci il nome: ");
        String surname = InputDati.leggiStringaNonVuota("Inserisci il cognome: ");
    
        Configuratore configuratore1 = configuratori.get(0);
        configuratore1.setEmail(newNomeUtente);
        configuratore1.setPassword(newPassword);
        configuratore1.setNome(name);
        configuratore1.setCognome(surname);
    
        String sql = "INSERT INTO configuratori (nome, cognome, email, password) VALUES (?, ?, ?, ?)";
    
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
    
            pstmt.setString(1, configuratore1.getNome());
            pstmt.setString(2, configuratore1.getCognome());
            pstmt.setString(3, configuratore1.getEmail());
            pstmt.setString(4, configuratore1.getPassword());
            pstmt.executeUpdate();
    
            System.out.println("Nuove credenziali salvate.");
        } catch (SQLException e) {
            System.out.println("Errore durante il salvataggio delle credenziali: " + e.getMessage());
        }
    }


    public boolean[] verificaCredenziali(String username, String password) {
        boolean[] esito = new boolean[2]; // esito[0] = autenticato, esito[1] = credenzialiTemporanee
    
        String sqlTemp = "SELECT * FROM credenziali_temporanee WHERE username = ? AND password = ?";
        String sqlConfig = "SELECT * FROM configuratori WHERE email = ? AND password = ?";
    
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmtTemp = conn.prepareStatement(sqlTemp);
             PreparedStatement pstmtConfig = conn.prepareStatement(sqlConfig)) {
    
            // Controllo credenziali temporanee
            pstmtTemp.setString(1, username);
            pstmtTemp.setString(2, password);
            try (ResultSet rsTemp = pstmtTemp.executeQuery()) {
                if (rsTemp.next()) {
                    esito[0] = true; // Autenticato
                    esito[1] = true; // Credenziali temporanee
                    return esito;
                }
            }
    
            // Controllo credenziali configuratori
            pstmtConfig.setString(1, username);
            pstmtConfig.setString(2, password);
            try (ResultSet rsConfig = pstmtConfig.executeQuery()) {
                if (rsConfig.next()) {
                    esito[0] = true; // Autenticato
                    esito[1] = false; // Credenziali personali
                    return esito;
                }
            }
    
        } catch (SQLException e) {
            System.out.println("Errore durante la verifica delle credenziali: " + e.getMessage());
        }
    
        esito[0] = false; // Non autenticato
        esito[1] = false; // Non rilevante
        return esito;
    }
}
