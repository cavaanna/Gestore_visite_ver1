package src.it.unibs.ingsw.gestvisit.gestvisit;

import java.util.List;

import it.unibs.mylib.InputDati;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CredentialManager {
    // private List<Utente> utenti = new ArrayList<>();
    // public List<Volontario> volontari = new ArrayList<>();


    // public void aggiungiUtente(Utente utente) {
    //     utenti.add(utente);
    // }

    // public void salvaCredenziali() {
    //     File file = new File(CREDENZIALI_FILE_PATH_GENERALS);

    //     try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, false))) {
    //         for (Utente utente : utenti) {
    //             String tipoUtente = "";
    //             if (utente instanceof Configuratore) {
    //                 tipoUtente = "Configuratore";
    //                 Configuratore config = (Configuratore) utente;
    //                 writer.write(tipoUtente + "," + config.getPassword() + "," + config.getEmail());
    //             } else if (utente instanceof Volontario) {
    //                 tipoUtente = "Volontario";
    //                 Volontario vol = (Volontario) utente;
    //                 writer.write(tipoUtente + "," + vol.getPassword() + "," + vol.getEmail());
    //             } else {
    //                 tipoUtente = "UtentePubblico";
    //             }
    //         }
    //         System.out.println("Credenziali salvate.");
    //     } catch (IOException e) {
    //         System.out.println("Errore durante la scrittura del file.");
    //         e.printStackTrace();
    //     }
    // }

    // public void caricaCredenziali() {
    //     File file = new File(CREDENZIALI_FILE_PATH_GENERALS);

    //     try (Scanner scanner = new Scanner(file)) {
    //         while (scanner.hasNextLine()) {
    //             String[] credenziali = scanner.nextLine().split(",");
    //             if (credenziali.length != 6) {
    //                 System.out.println("Formato credenziali non valido: " + String.join(":", credenziali));
    //                 continue;
    //             }
    //             String tipoUtente = credenziali[0];
    //             String nome = credenziali[1];
    //             String cognome = credenziali[2];
    //             String email = credenziali[3];
    //             String password = credenziali[4];
    //             String tipiDiVisite = credenziali[5];
    //             switch (tipoUtente) {
    //                 case "Volontario":
    //                     volontari.add(new Volontario(nome, cognome, email, password, tipiDiVisite));
    //                     break;
                     
    //                 case "Utente":
    //                     utenti.add(new Utente(nome, cognome, nomeUtente, password));
    //                     break;
                    
                    
    //                 default:
    //                     System.out.println("Tipo utente non riconosciuto: " + tipoUtente);
    //                     break;
    //             }
    //         }
    //         System.out.println("Credenziali caricate.");
    //     } catch (IOException e) {
    //         System.out.println("Errore durante la lettura del file.");
    //         e.printStackTrace();
    //     }
    // }
    //-----------------------------------------------------------------------------------
    // public void caricaCredenzialiConfiguratore(List<Configuratore> configuratori) {
    //     File file = new File(CREDENZIALI_FILE_PATH_CONFIG_PERS);

    //     if (!file.exists()) {
    //         System.out.println("File " + CREDENZIALI_FILE_PATH_CONFIG_PERS + " non trovato.");
    //         System.out.println("Percorso assoluto: " + file.getAbsolutePath());
    //         return;
    //     }

    //     try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
    //         String line;
    //         while ((line = reader.readLine()) != null) {
    //             String[] credenziali = line.split(",");
    //             if (credenziali.length == 5 && credenziali[0].equals("Configuratore")) {
    //                 String nome = credenziali[1].trim();
    //                 String cognome = credenziali[2].trim();
    //                 String email = credenziali[3].trim();
    //                 String password = credenziali[4].trim();
    //                 Configuratore configuratore = new Configuratore(nome, cognome, email, password);
    //                 configuratori.add(configuratore);
    //             }
    //         }
    //     } catch (IOException e) {
    //         System.out.println("Errore durante la lettura del file.");
    //         e.printStackTrace();
    //     }
    // }
    public void caricaCredenzialiVolontario(List<Volontario> volontari) {
        String sql = "SELECT nome, cognome, email, password, tipi_di_visite FROM volontari";
    
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
    
            while (rs.next()) {
                String nome = rs.getString("nome");
                String cognome = rs.getString("cognome");
                String email = rs.getString("email");
                String password = rs.getString("password");
                String tipoDiVisita = rs.getString("tipi_di_visite");
    
                Volontario volontario = new Volontario(nome, cognome, email, password, tipoDiVisita);
                volontari.add(volontario);
            }
        } catch (SQLException e) {
            System.out.println("Errore durante il caricamento delle credenziali: " + e.getMessage());
        }
    }

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

    // public void saveNewConfigCredential(List<Configuratore> configuratori) {
    //     String newNomeUtente = InputDati.leggiStringaNonVuota("Inserisci il nuovo nome utente (email): ");
    //     String newPassword = InputDati.leggiStringaNonVuota("Inserisci la nuova password: ");
    //     String name = InputDati.leggiStringaNonVuota("Inserisci il nome: ");
    //     String surname = InputDati.leggiStringaNonVuota("Inserisci il cognome: ");
        
    //     Configuratore configuratore1 = configuratori.get(0); 
    //     configuratore1.setEmail(newNomeUtente);
    //     configuratore1.setPassword(newPassword);
    //     configuratore1.setNome(name);
    //     configuratore1.setCognome(surname);

    //     try (BufferedWriter writer = new BufferedWriter(new FileWriter(CREDENZIALI_FILE_PATH_CONFIG_PERS, true))) {
    //         writer.write("Configuratore," + configuratore1.getNome() + "," + configuratore1.getCognome() + "," + configuratore1.getEmail() + "," + configuratore1.getPassword());
    //         writer.newLine();
    //         System.out.println("Nuove credenziali salvate.");
    //     } catch (IOException e) {
    //         System.out.println("Errore durante la scrittura del file.");
    //         e.printStackTrace();
    //     }
    // }

    public void saveNewVolCredential(Volontario volontario) {    
        // Inserisci la nuova password
        String nuovaPassword = InputDati.leggiStringaNonVuota("Inserisci la nuova password: ");
        volontario.setPassword(nuovaPassword);
    
        // Aggiorna la password e il campo password_modificata nel database
        String sql = "UPDATE volontari SET password = ?, password_modificata = ? WHERE email = ?";
    
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
    
            pstmt.setString(1, nuovaPassword);
            pstmt.setBoolean(2, true); // Imposta password_modificata a TRUE
            pstmt.setString(3, volontario.getEmail());
            int rowsUpdated = pstmt.executeUpdate();
    
            if (rowsUpdated > 0) {
                System.out.println("Password aggiornata con successo per il volontario: " + volontario.getNome() + " " + volontario.getCognome());
            } else {
                System.out.println("Errore: nessun volontario trovato con l'email " + volontario.getEmail());
            }
        } catch (SQLException e) {
            System.out.println("Errore durante l'aggiornamento della password: " + e.getMessage());
        }
    }

    //  public void saveNewVolCredential(List<Volontario> volontari) {
    
    //     String newPassword = InputDati.leggiStringaNonVuota("Inserisci la nuova password: ");
    
    //     Volontario volontario = volontari.get(0);
    //     volontario.setPassword(newPassword);
        
    //     String sql = "INSERT INTO volontari (nome, cognome, email, password, tipi_di_visite) VALUES (?, ?, ?, ?, ?)";
    
    //     try (Connection conn = DatabaseConnection.connect();
    //          PreparedStatement pstmt = conn.prepareStatement(sql)) {
    
    //         pstmt.setString(1, volontario.getNome());
    //         pstmt.setString(2, volontario.getCognome());
    //         pstmt.setString(3, volontario.getEmail());
    //         pstmt.setString(4, volontario.getPassword());
    //         pstmt.setString(5, volontario.getTipiDiVisite());
    //         pstmt.executeUpdate();
    
    //         System.out.println("Nuove credenziali salvate.");
    //     } catch (SQLException e) {
    //         System.out.println("Errore durante il salvataggio delle credenziali: " + e.getMessage());
    //     }
    // }

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

    // public boolean[] verificaCredenziali(String username, String password, ArrayList<Configuratore> configuratori, ArrayList<TemporaryCredential> temporaryCredentials) {
    //     boolean[] esito = new boolean[2]; // esito[0] = autenticato, esito[1] = credenzialiTemporanee

    //     for (TemporaryCredential tempCred : temporaryCredentials) {
    //         if (tempCred.getUsername().equals(username) && tempCred.getPassword().equals(password)) {
    //             esito[0] = true; // Autenticato
    //             esito[1] = true; // Credenziali temporanee
    //             return esito;
    //         }
    //     }

    //     for (Configuratore configuratore : configuratori) {
    //         if (configuratore.getEmail().equals(username) && configuratore.getPassword().equals(password)) {
    //             esito[0] = true; // Autenticato
    //             esito[1] = false; // Credenziali personali
    //             return esito;
    //         }
    //     }

    //     esito[0] = false; // Non autenticato
    //     esito[1] = false; // Non rilevante
    //     return esito;
    // }

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


    // public boolean[] verificaCredenzialiVolontario(String username, String password) {
    //     boolean[] esito = new boolean[2]; // esito[0] = autenticato, esito[1] = credenzialiTemporanee
    
    //     String sqlVol = "SELECT * FROM volontari WHERE email = ? AND password = ?";
    
    //     try (Connection conn = DatabaseConnection.connect();
    //          PreparedStatement pstmtVol = conn.prepareStatement(sqlVol)) {
        
    
    //         // Controllo credenziali configuratori
    //         pstmtVol.setString(1, username);
    //         pstmtVol.setString(2, password);
    //         try (ResultSet rsVol = pstmtVol.executeQuery()) {
    //             if (rsVol.next()) {
    //                 esito[0] = true; // Autenticato
    //                 esito[1] = false; // Credenziali personali
    //                 return esito;
    //             }
    //         }
    
    //     } catch (SQLException e) {
    //         System.out.println("Errore durante la verifica delle credenziali: " + e.getMessage());
    //     }
    
    //     esito[0] = false; // Non autenticato
    //     esito[1] = false; // Non rilevante
    //     return esito;
    // }
}
