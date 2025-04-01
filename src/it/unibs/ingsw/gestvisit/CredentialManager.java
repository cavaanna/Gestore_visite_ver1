package src.it.unibs.ingsw.gestvisit;

import java.util.List;

import it.unibs.mylib.InputDati;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;

public class CredentialManager {

    private final DatabaseUpdater databaseUpdater;
    private ConcurrentHashMap<String, Volontario> volontariMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Configuratore> configuratoriMap = new ConcurrentHashMap<>();
    
    public CredentialManager(DatabaseUpdater databaseUpdater) {
        this.databaseUpdater = databaseUpdater;
    }

    // public void caricaCredenzialiVolontario() {
    //     databaseUpdater.sincronizzaDalDatabase();
    //     volontariMap = databaseUpdater.getVolontariMap();
    //     // String sql = "SELECT nome, cognome, email, password, tipi_di_visite FROM volontari";
    
    //     // try (Connection conn = DatabaseConnection.connect();
    //     //      PreparedStatement pstmt = conn.prepareStatement(sql);
    //     //      ResultSet rs = pstmt.executeQuery()) {
    
    //     //     while (rs.next()) {
    //     //         String nome = rs.getString("nome");
    //     //         String cognome = rs.getString("cognome");
    //     //         String email = rs.getString("email");
    //     //         String password = rs.getString("password");
    //     //         String tipoDiVisita = rs.getString("tipi_di_visite");
    
    //     //         Volontario volontario = new Volontario(nome, cognome, email, password, tipoDiVisita);
    //     //         volontari.add(volontario);
    //     //     }
    //     // } catch (SQLException e) {
    //     //     System.out.println("Errore durante il caricamento delle credenziali: " + e.getMessage());
    //     // }
    // }

    // public void caricaCredenzialiConfiguratore(List<Configuratore> configuratori) {
    //     databaseUpdater.sincronizzaDalDatabase();
    //     databaseUpdater.getConfiguratoriMap();
    //     // String sql = "SELECT nome, cognome, email, password FROM configuratori";
    
    //     // try (Connection conn = DatabaseConnection.connect();
    //     //      PreparedStatement pstmt = conn.prepareStatement(sql);
    //     //      ResultSet rs = pstmt.executeQuery()) {
    
    //     //     while (rs.next()) {
    //     //         String nome = rs.getString("nome");
    //     //         String cognome = rs.getString("cognome");
    //     //         String email = rs.getString("email");
    //     //         String password = rs.getString("password");
    
    //     //         Configuratore configuratore = new Configuratore(nome, cognome, email, password);
    //     //         configuratori.add(configuratore);
    //     //     }
    //     // } catch (SQLException e) {
    //     //     System.out.println("Errore durante il caricamento delle credenziali: " + e.getMessage());
    //     // }
    // }

    public void caricaCredenzialiTemporanee(List<TemporaryCredential> temporaryCredentials) {
        String sql = "SELECT username, password FROM credenziali_temporanee";
    
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
    
            while (rs.next()) {
                String username = rs.getString("username");
                String password = rs.getString("password");
                temporaryCredentials.add(new TemporaryCredential(username, password));
            }
    
            System.out.println("Credenziali temporanee caricate con successo.");
        } catch (SQLException e) {
            System.out.println("Errore durante il caricamento delle credenziali temporanee: " + e.getMessage());
        }
    }

    public void saveNewVolCredential(Volontario volontario) {    
        // Inserisci la nuova password
        String nuovaPassword = InputDati.leggiStringaNonVuota("Inserisci la nuova password: ");
    
        // Aggiorna la password e il campo password_modificata nel database
        databaseUpdater.aggiornaPswVolontario(volontario.getEmail(), nuovaPassword);
    }

    public void saveNewConfigCredential(List<Configuratore> configuratori) {
        // Raccogli i dati del nuovo configuratore
        String newNomeUtente = InputDati.leggiStringaNonVuota("Inserisci il nuovo nome utente (email): ");
        String newPassword = InputDati.leggiStringaNonVuota("Inserisci la nuova password: ");
        String name = InputDati.leggiStringaNonVuota("Inserisci il nome: ");
        String surname = InputDati.leggiStringaNonVuota("Inserisci il cognome: ");
    
        // Crea un nuovo oggetto Configuratore
        Configuratore configuratore1 = new Configuratore(name, surname, newNomeUtente, newPassword);
        configuratori.add(configuratore1);
    
        // Query per inserire il configuratore nella tabella configuratori
        String sqlConfiguratori = "INSERT INTO configuratori (nome, cognome, email, password) VALUES (?, ?, ?, ?)";
    
        // Query per inserire il configuratore nella tabella utenti_unificati
        String sqlUtentiUnificati = "INSERT INTO utenti_unificati (nome, cognome, email, password, ruolo, password_modificata) VALUES (?, ?, ?, ?, ?, ?)";
    
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmtConfiguratori = conn.prepareStatement(sqlConfiguratori);
             PreparedStatement pstmtUtentiUnificati = conn.prepareStatement(sqlUtentiUnificati)) {
    
            // Inserisci il configuratore nella tabella configuratori
            pstmtConfiguratori.setString(1, configuratore1.getNome());
            pstmtConfiguratori.setString(2, configuratore1.getCognome());
            pstmtConfiguratori.setString(3, configuratore1.getEmail());
            pstmtConfiguratori.setString(4, configuratore1.getPassword());
            pstmtConfiguratori.executeUpdate();
    
            // Inserisci il configuratore nella tabella utenti_unificati
            pstmtUtentiUnificati.setString(1, configuratore1.getNome());
            pstmtUtentiUnificati.setString(2, configuratore1.getCognome());
            pstmtUtentiUnificati.setString(3, configuratore1.getEmail());
            pstmtUtentiUnificati.setString(4, configuratore1.getPassword());
            pstmtUtentiUnificati.setString(5, "Configuratore"); // Ruolo specifico
            pstmtUtentiUnificati.setBoolean(6, true); // PSW modificata
            pstmtUtentiUnificati.executeUpdate();
    
            System.out.println("Nuove credenziali salvate sia in configuratori che in utenti_unificati.");
        } catch (SQLException e) {
            System.out.println("Errore durante il salvataggio delle credenziali: " + e.getMessage());
        }
    }

    public String verificaCredenziali(String username, String password) {
        String tipo_utente = null; // Variabile per memorizzare il ruolo dell'utente
    
        String sql = "SELECT tipo_utente FROM utenti_unificati WHERE email = ? AND password = ?";
    
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
    
            // Imposta i parametri della query
            pstmt.setString(1, username);
            pstmt.setString(2, password);
    
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    tipo_utente = rs.getString("tipo_utente"); // Recupera il tipo_utente
                }
            }
        } catch (SQLException e) {
            System.out.println("Errore durante la verifica delle credenziali: " + e.getMessage());
        }
    
        // Restituisci il tipo_utente dell'utente o null se non autenticato
        return tipo_utente;
    }

    public boolean isPasswordModificata(String email) {
        String sql = "SELECT password_modificata FROM utenti_unificati WHERE email = ?";
        boolean passwordModificata = false;
    
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
    
            // Imposta il parametro della query
            pstmt.setString(1, email);
    
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    passwordModificata = rs.getBoolean("password_modificata");
                }
            }
        } catch (SQLException e) {
            System.out.println("Errore durante la verifica del campo password_modificata: " + e.getMessage());
        }
    
        return passwordModificata;
    }
}
