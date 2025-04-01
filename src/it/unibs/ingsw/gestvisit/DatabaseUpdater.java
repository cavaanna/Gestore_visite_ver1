package src.it.unibs.ingsw.gestvisit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;


public class DatabaseUpdater {
    // HashMap per memorizzare i dati sincronizzati
    private ConcurrentHashMap<String, Volontario> volontariMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Configuratore> configuratoriMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Luogo> luoghiMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Integer, Visite> visiteMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, TemporaryCredential> temporaryCredentials = new ConcurrentHashMap<>();
    

    private final ExecutorService executorService;
    private Thread aggiornamentoThread;
    private volatile boolean eseguiAggiornamento = true; // Variabile per controllare il ciclo

    public DatabaseUpdater(ExecutorService executorService) {
        this.executorService = executorService;
    }

    //Logiche Thread------------------------------------------------------------------
    // Metodo per sincronizzare i dati dal database in un thread separato
    public void sincronizzaDalDatabase() {
        executorService.submit(() -> {
            try {
                // Logica per sincronizzare i dati dal database
                caricaVolontari();
                caricaConfiguratori();
                caricaLuoghi();
                caricaVisite();
            } catch (Exception e) {
                System.err.println("Errore durante la sincronizzazione dal database: " + e.getMessage());
            }
        });
    }

    // Metodo per sincronizzare i dati con il database in modo sincrono
    public void sincronizzaConDatabase() {
        executorService.submit(() -> {
            try {
                // Sincronizza i volontari
                for (Volontario volontario : volontariMap.values()) {
                    aggiungiVolontario(volontario);
                    aggiornaPswVolontario(volontario.getEmail(), volontario.getPassword());
                }
    
                // Sincronizza i configuratori
                for (Configuratore configuratore : configuratoriMap.values()) {
                    aggiornaConfiguratore(configuratore.getEmail(), configuratore);
                }
    
                // Sincronizza i luoghi
                for (Luogo luogo : luoghiMap.values()) {
                    aggiungiLuogo(luogo);
                    aggiornaLuogo(luogo.getNome(), luogo);
                }
    
                // Sincronizza le visite
                for (Visite visita : visiteMap.values()) {
                    aggiungiVisita(visita);
                    aggiornaVisita(visita.getId(), visita);
                    aggiornaMaxPersonePerVisita(visita.getMaxPersone());
                }
    
                System.out.println("Sincronizzazione con il database completata.");
            } catch (Exception e) {
                System.err.println("Errore durante la sincronizzazione con il database: " + e.getMessage());
            }
        });
    }

    // Metodo per avviare la sincronizzazione periodica con un ciclo e sleep
    public void avviaSincronizzazioneConSleep() {
        eseguiAggiornamento = true; // Assicura che il ciclo sia attivo
        aggiornamentoThread = new Thread(() -> {
            while (eseguiAggiornamento) {
                try {
                    sincronizzaDalDatabase();
                    Thread.sleep(5000); // Pausa di 5 secondi
                } catch (InterruptedException e) {
                    System.out.println("Thread di aggiornamento interrotto.");
                    Thread.currentThread().interrupt(); // Ripristina lo stato di interruzione
                }
            }
        });
        aggiornamentoThread.start();
    }

    // Metodo per fermare la sincronizzazione periodica con un ciclo e sleep
    public void arrestaSincronizzazioneConSleep() {
        eseguiAggiornamento = false; // Ferma il ciclo
        if (aggiornamentoThread != null) {
            aggiornamentoThread.interrupt(); // Interrompe il thread se è in attesa
            try {
                aggiornamentoThread.join(); // Attende la terminazione del thread
            } catch (InterruptedException e) {
                System.err.println("Errore durante l'arresto del thread di aggiornamento.");
                Thread.currentThread().interrupt(); // Ripristina lo stato di interruzione
            }
        }
    }

    //Logiche per Credenziali Temporanee--------------------------------------------------
    public void caricaCredenzialiTemporanee() {
        String sql = "SELECT username, password FROM credenziali_temporanee";
    
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
    
            while (rs.next()) {
                String username = rs.getString("username");
                String password = rs.getString("password");
                temporaryCredentials.put(username, new TemporaryCredential(username, password));
            }
    
            System.out.println("Credenziali temporanee caricate con successo.");
        } catch (SQLException e) {
            System.out.println("Errore durante il caricamento delle credenziali temporanee: " + e.getMessage());
        }
    }

//Logiche dei volontari--------------------------------------------------
    // Metodo per caricare i volontari dal database e memorizzarli nella HashMap
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
            System.err.println("Errore durante il caricamento dei volontari: " + e.getMessage());
        }
    }

    // Metodo per aggiungere un volontario al database
    private void aggiungiVolontario(Volontario volontario) {
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
            } catch (SQLException e) {
                System.err.println("Errore durante l'aggiunta del volontario: " + e.getMessage());
            }
        });
    }

    // Metodo per aggiornare un volontario nel database
    private void aggiornaPswVolontario(String email, String nuovaPassword) {
        String sqlVolontari = "UPDATE volontari SET password = ?, password_modificata = ? WHERE email = ?";
        String sqlUtentiUnificati = "UPDATE utenti_unificati SET password = ?, password_modificata = ? WHERE email = ?";
    
        executorService.submit(() -> {
            try (Connection conn = DatabaseConnection.connect()) {
                // Aggiorna la tabella "volontari"
                try (PreparedStatement pstmtVolontari = conn.prepareStatement(sqlVolontari)) {
                    pstmtVolontari.setString(1, nuovaPassword);
                    pstmtVolontari.setBoolean(2, true); // Imposta password_modificata a true
                    pstmtVolontari.setString(3, email);
                    int rowsUpdatedVolontari = pstmtVolontari.executeUpdate();
    
                    if (rowsUpdatedVolontari > 0) {
                        System.err.println("Password aggiornata con successo nella tabella 'volontari'.");
                    } else {
                        System.err.println("Errore: Nessun volontario trovato con l'email specificata.");
                    }
                }
    
                // Aggiorna la tabella "utenti_unificati"
                try (PreparedStatement pstmtUtenti = conn.prepareStatement(sqlUtentiUnificati)) {
                    pstmtUtenti.setString(1, nuovaPassword);
                    pstmtUtenti.setBoolean(2, true); // Imposta password_modificata a true
                    pstmtUtenti.setString(3, email);
                    int rowsUpdatedUtenti = pstmtUtenti.executeUpdate();
    
                    if (rowsUpdatedUtenti > 0) {
                        System.err.println("Password aggiornata con successo nella tabella 'utenti_unificati'.");
                    } else {
                        System.err.println("Errore: Nessun utente trovato con l'email specificata nella tabella 'utenti_unificati'.");
                    }
                }
            } catch (SQLException e) {
                System.err.println("Errore durante l'aggiornamento della password: " + e.getMessage());
            }
        });
    }

//Logiche dei configuratori--------------------------------------------------
    // Metodo per caricare i configuratori dal database e memorizzarli nella HashMap
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

    // Metodo per aggiornare un configuratore nel database
    private void aggiornaConfiguratore(String email, Configuratore configuratoreAggiornato) {
        String sqlConfiguratori = "UPDATE configuratori SET nome = ?, cognome = ?, password = ?, email = ? WHERE email = ?";
        String sqlUtentiUnificati = "UPDATE utenti_unificati SET nome = ?, cognome = ?, password = ?, email = ? WHERE email = ?";
    
        executorService.submit(() -> {
            try (Connection conn = DatabaseConnection.connect()) {
                // Aggiorna la tabella "configuratori"
                try (PreparedStatement pstmtConfiguratori = conn.prepareStatement(sqlConfiguratori)) {
                    pstmtConfiguratori.setString(1, configuratoreAggiornato.getNome());
                    pstmtConfiguratori.setString(2, configuratoreAggiornato.getCognome());
                    pstmtConfiguratori.setString(3, configuratoreAggiornato.getPassword());
                    pstmtConfiguratori.setString(4, configuratoreAggiornato.getEmail()); // Nuova email
                    pstmtConfiguratori.setString(5, email); // Email corrente
                    int rowsUpdatedConfiguratori = pstmtConfiguratori.executeUpdate();
    
                    if (rowsUpdatedConfiguratori > 0) {
                        System.err.println("Configuratore aggiornato con successo nella tabella 'configuratori'.");
                    } else {
                        System.err.println("Errore: Nessun configuratore trovato con l'email specificata.");
                    }
                }
    
                // Aggiorna la tabella "utenti_unificati"
                try (PreparedStatement pstmtUtentiUnificati = conn.prepareStatement(sqlUtentiUnificati)) {
                    pstmtUtentiUnificati.setString(1, configuratoreAggiornato.getNome());
                    pstmtUtentiUnificati.setString(2, configuratoreAggiornato.getCognome());
                    pstmtUtentiUnificati.setString(3, configuratoreAggiornato.getPassword());
                    pstmtUtentiUnificati.setString(4, configuratoreAggiornato.getEmail()); // Nuova email
                    pstmtUtentiUnificati.setString(5, email); // Email corrente
                    int rowsUpdatedUtentiUnificati = pstmtUtentiUnificati.executeUpdate();
    
                    if (rowsUpdatedUtentiUnificati > 0) {
                        System.err.println("Configuratore aggiornato con successo nella tabella 'utenti_unificati'.");
                    } else {
                        System.err.println("Errore: Nessun utente trovato con l'email specificata nella tabella 'utenti_unificati'.");
                    }
                }
            } catch (SQLException e) {
                System.err.println("Errore durante l'aggiornamento del configuratore: " + e.getMessage());
            }
        });
    }

//Logiche dei luoghi--------------------------------------------------
    // Metodo per caricare i luoghi dal database e memorizzarli nella HashMap
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
            System.err.println("Errore durante il caricamento dei luoghi: " + e.getMessage());
        }
    }

    // Metodo per aggiornare un luogo nel database
    private void aggiornaLuogo(String nome, Luogo luogoAggiornato) {
        String sql = "UPDATE luoghi SET descrizione = ? WHERE nome = ?";
        executorService.submit(() -> {
            try (Connection conn = DatabaseConnection.connect();
                    PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, luogoAggiornato.getDescrizione());
                pstmt.setString(2, nome);
    
                int rowsUpdated = pstmt.executeUpdate();
    
                if (rowsUpdated > 0) {
                    System.err.println("Luogo aggiornato con successo.");
                } else {
                    System.err.println("Errore: Nessun luogo trovato con il nome specificato.");
                }
            } catch (SQLException e) {
                System.err.println("Errore durante l'aggiornamento del luogo: " + e.getMessage());
            }
        });
    }

    // Metodo per aggiungere un luogo al database
    private void aggiungiLuogo(Luogo luogo) {
        String sql = "INSERT INTO luoghi (nome, descrizione) VALUES (?, ?)";
        executorService.submit(() -> {
            try (Connection conn = DatabaseConnection.connect();
                    PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, luogo.getNome());
                pstmt.setString(2, luogo.getDescrizione());
                pstmt.executeUpdate();
            } catch (SQLException e) {
                System.err.println("Errore durante l'aggiunta del luogo: " + e.getMessage());
            }
        });
    }

//Logiche delle visite--------------------------------------------------
    // Metodo per caricare un luogo nel database e memorizzarlo nella HashMap
    private void caricaVisite() {
        String sql = "SELECT id, luogo, tipo_visita, volontario, data, stato, max_persone FROM visite";
        try (Connection conn = DatabaseConnection.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery()) {

            synchronized (visiteMap) {
                visiteMap.clear();
                while (rs.next()) {
                    int id = rs.getInt("id"); // ID della visita
                    String luogo = rs.getString("luogo");
                    String tipoVisita = rs.getString("tipo_visita");
                    String volontario = rs.getString("volontario");
                    LocalDate data = rs.getDate("data") != null ? rs.getDate("data").toLocalDate() : null; // Converte la data in LocalDate
                    int maxPersone = rs.getInt("max_persone");
                    String stato = rs.getString("stato");

                    // Usa il costruttore completo di Visite
                    Visite visita = new Visite(id, luogo, tipoVisita, volontario, data, maxPersone, stato);
                    visiteMap.put(id, visita);
                }
            }
        } catch (SQLException e) {
            System.err.println("Errore durante il caricamento delle visite: " + e.getMessage());
        }
    }

    // Metodo per aggiungere una visita al database
    private void aggiungiVisita(Visite visita) {
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
            } catch (SQLException e) {
                System.err.println("Errore durante l'aggiunta della visita: " + e.getMessage());
            }
        });
    }

    // Metodo per aggiornare una visita specifica
    private void aggiornaVisita(int visitaId, Visite visitaAggiornata) {
        String sql = "UPDATE visite SET luogo = ?, tipo_visita = ?, volontario = ?, data = ?, stato = ?, max_persone = ? WHERE id = ?";
        executorService.submit(() -> {
            try (Connection conn = DatabaseConnection.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, visitaAggiornata.getLuogo());
                pstmt.setString(2, visitaAggiornata.getTipoVisita());
                pstmt.setString(3, visitaAggiornata.getVolontario());
                pstmt.setDate(4, visitaAggiornata.getData() != null ? java.sql.Date.valueOf(visitaAggiornata.getData()) : null);
                pstmt.setString(5, visitaAggiornata.getStato());
                pstmt.setInt(6, visitaAggiornata.getMaxPersone());
                pstmt.setInt(7, visitaId);

                int rowsUpdated = pstmt.executeUpdate();

                if (rowsUpdated > 0) {
                    System.err.println("Visita aggiornata con successo.");
                } else {
                    System.err.println("Errore: Nessuna visita trovata con l'ID specificato.");
                }
            } catch (SQLException e) {
                System.err.println("Errore durante l'aggiornamento della visita: " + e.getMessage());
            }
        });
    }

    // Metodo per aggiornare il numero massimo di persone per tutte le visite
    private void aggiornaMaxPersonePerVisita(int maxPersonePerVisita) {
        String sql = "UPDATE visite SET max_persone = ?";
        executorService.submit(() -> {
            try (Connection conn = DatabaseConnection.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, maxPersonePerVisita);
                int rowsUpdated = pstmt.executeUpdate();

                if (rowsUpdated > 0) {
                    System.err.println("Numero massimo di persone per visita aggiornato con successo.");
                } else {
                    System.err.println("Nessuna visita trovata da aggiornare.");
                }
            } catch (SQLException e) {
                System.err.println("Errore durante l'aggiornamento del numero massimo di persone per visita: " + e.getMessage());
            }
        });
    }

    public void sincronizzaVisita(int visitaId, Visite visitaAggiornata) {
        aggiornaVisita(visitaId, visitaAggiornata);
    }

    public void sincronizzaMaxPersonePerVisita(int maxPersonePerVisita) {
        aggiornaMaxPersonePerVisita(maxPersonePerVisita);
    }

    


//Getters e Setters--------------------------------------------------
    public String getTipoUtente(String email, String password){
        String tipo_utente = null; // Inizializza a null per evitare NullPointerException
        String sql = "SELECT tipo_utente FROM utenti_unificati WHERE email = ? AND password = ?";
    
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
    
            // Imposta i parametri della query
            pstmt.setString(1, email);
            pstmt.setString(2, password);
    
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    tipo_utente = rs.getString("tipo_utente"); // Recupera il tipo_utente
                }
            }
        } catch (SQLException e) {
            System.out.println("Errore durante la verifica delle credenziali: " + e.getMessage());
        }
        return tipo_utente; // Restituisce il tipo_utente o null se non trovato
    }

    public boolean isPasswordModificata(String email) {
        String sql = "SELECT password_modificata FROM utenti_unificati WHERE email = ?";
        boolean passwordModificata = false; // Valore di default
    
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
    
            // Imposta il parametro della query
            pstmt.setString(1, email);
    
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    passwordModificata = rs.getBoolean("password_modificata"); // Recupera il valore del campo password_modificata
                } else {
                    System.out.println("Nessun record trovato per l'email: " + email);
                }
            }
        } catch (SQLException e) {
            System.out.println("Errore durante la verifica del campo password_modificata: " + e.getMessage());
        }
    
        return passwordModificata;
    }
    

    // Metodo per recuperare il numero massimo di persone per visita dal database
    public int getMaxPersoneDefault() {
        String sql = "SELECT valore FROM configurazioni WHERE chiave = 'max_persone_default'";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
    
            if (rs.next()) {
                return rs.getInt("valore");
            }
        } catch (SQLException e) {
            System.err.println("Errore durante il recupero del numero massimo di persone: " + e.getMessage());
        }
        return 10; // Valore di default se non trovato nel database
    }

    public ConcurrentHashMap<String, Volontario> getVolontariMap() {
        return volontariMap;
    }
    
    public void setVolontariMap(ConcurrentHashMap<String, Volontario> volontariMap) {
        this.volontariMap = volontariMap;
    }
    
    public ConcurrentHashMap<String, Configuratore> getConfiguratoriMap() {
        return configuratoriMap;
    }
    
    public void setConfiguratoriMap(ConcurrentHashMap<String, Configuratore> configuratoriMap) {
        this.configuratoriMap = configuratoriMap;
    }
    
    public ConcurrentHashMap<String, Luogo> getLuoghiMap() {
        return luoghiMap;
    }
    
    public void setLuoghiMap(ConcurrentHashMap<String, Luogo> luoghiMap) {
        this.luoghiMap = luoghiMap;
    }
    
    public ConcurrentHashMap<Integer, Visite> getVisiteMap() {
        return visiteMap;
    }
    
    public void setVisiteMap(ConcurrentHashMap<Integer, Visite> visiteMap) {
        this.visiteMap = visiteMap;
    }

    public ConcurrentHashMap<String, TemporaryCredential> getTemporaryCredentials() {
        return temporaryCredentials;
    }
}