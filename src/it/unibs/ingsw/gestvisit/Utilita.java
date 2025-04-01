package src.it.unibs.ingsw.gestvisit;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import it.unibs.mylib.InputDati;

public class Utilita {

    private final DatabaseUpdater databaseUpdater;
    ConcurrentHashMap<Integer, Visite> visiteMap = new ConcurrentHashMap<>();
    ConcurrentHashMap<String, Luogo> luoghiMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Volontario> volontariMap = new ConcurrentHashMap<>();

    public Utilita(DatabaseUpdater databaseUpdater) {
        this.databaseUpdater = databaseUpdater;
    }

    // Metodo per stampare i luoghi
    public void stampaLuoghi() {
        databaseUpdater.sincronizzaDalDatabase();
        luoghiMap = databaseUpdater.getLuoghiMap();

        if (luoghiMap.isEmpty()) {
            System.out.println("Nessun luogo disponibile.");
            return;
        }

        System.out.println("Luoghi:");
        for (Luogo luogo : luoghiMap.values()) {
            System.out.println("Nome: " + luogo.getNome());
            System.out.println("Descrizione: " + luogo.getDescrizione());
            System.out.println("-------------------------");
        }
    }

    // Metodo per stampare i volontari
    public void stampaVolontari() {
        databaseUpdater.sincronizzaDalDatabase();
        volontariMap = databaseUpdater.getVolontariMap();

        if (volontariMap.isEmpty()) {
            System.out.println("Nessun volontario disponibile.");
            return;
        }

        System.out.println("Volontari:");
        for (Volontario volontario : volontariMap.values()) {
            System.out.println("Nome: " + volontario.getNome() + " " + volontario.getCognome());
            System.out.println("Email: " + volontario.getEmail());
            System.out.println("Tipi di Visite: " + volontario.getTipiDiVisite());
            System.out.println("-------------------------");
        }
    }

    // Metodo per stampare le visite
    public void stampaVisite() {
        databaseUpdater.sincronizzaDalDatabase();
        ConcurrentHashMap<Integer, Visite> visiteMap = databaseUpdater.getVisiteMap();

        if (visiteMap.isEmpty()) {
            System.out.println("Nessuna visita disponibile.");
            return;
        }

        System.out.println("Visite:");
        for (Map.Entry<Integer, Visite> entry : visiteMap.entrySet()) {
            Visite visita = entry.getValue();
            System.out.println("ID: " + entry.getKey());
            System.out.println("Luogo: " + visita.getLuogo());
            System.out.println("Tipo Visita: " + visita.getTipoVisita());
            System.out.println("Volontario: " + visita.getVolontario());
            System.out.println("Data: " + (visita.getData() != null ? visita.getData() : "Nessuna data"));
            System.out.println("-------------------------");
        }
    }

    // Metodo per modificare la data di una visita
    public void modificaDataVisita() {
        databaseUpdater.sincronizzaDalDatabase();
        visiteMap = databaseUpdater.getVisiteMap();

        if (visiteMap.isEmpty()) {
            System.out.println("Non ci sono visite disponibili da modificare.");
            return;
        }

        System.out.println("Visite disponibili:");
        for (Map.Entry<Integer, Visite> entry : visiteMap.entrySet()) {
            Visite visita = entry.getValue();
            System.out.printf("%d. Luogo: %s, Tipo Visita: %s, Volontario: %s, Data: %s%n",
                    entry.getKey(), visita.getLuogo(), visita.getTipoVisita(), visita.getVolontario(),
                    visita.getData() != null ? visita.getData() : "Nessuna data");
        }

        int visitaId = InputDati.leggiIntero("Seleziona l'ID della visita da modificare: ");
        if (!visiteMap.containsKey(visitaId)) {
            System.out.println("ID visita non valido.");
            return;
        }

        int anno = InputDati.leggiIntero("Inserisci il nuovo anno della visita: ");
        int mese = InputDati.leggiIntero("Inserisci il nuovo mese della visita (1-12): ");
        int giorno = InputDati.leggiIntero("Inserisci il nuovo giorno della visita: ");
        LocalDate nuovaData = LocalDate.of(anno, mese, giorno);

        Visite visitaAggiornata = visiteMap.get(visitaId);
        visitaAggiornata.setData(nuovaData);

        databaseUpdater.aggiornaVisita(visitaId, visitaAggiornata);
        System.out.println("Data della visita aggiornata con successo.");
    }

    // Metodo per impostare il numero massimo di persone per visita
    public void setMaxPersonePerVisita(int maxPersonePerVisita) {
        databaseUpdater.aggiornaMaxPersonePerVisita(maxPersonePerVisita);
        System.out.println("Numero massimo di persone per visita aggiornato a: " + maxPersonePerVisita);
    }

    // Metodo per visualizzare le visite per stato
    public void visualizzaVisitePerStato() {
        databaseUpdater.sincronizzaDalDatabase();
        visiteMap = databaseUpdater.getVisiteMap();

        synchronized (visiteMap) {
            if (visiteMap.isEmpty()) {
                System.out.println("Non ci sono visite disponibili.");
                return;
            }
    
            String[] stati = {"Proposta", "Completa", "Confermata", "Cancellata", "Effettuata"};
            System.out.println("Stati disponibili:");
            for (int i = 0; i < stati.length; i++) {
                System.out.printf("%d. %s%n", i + 1, stati[i]);
            }
    
            int sceltaStato = InputDati.leggiIntero("Seleziona lo stato da visualizzare: ", 1, stati.length) - 1;
            String statoScelto = stati[sceltaStato];
    
            System.out.printf("Visite in stato '%s':%n", statoScelto);
            for (Visite visita : visiteMap.values()) {
                if (visita.getStato().equalsIgnoreCase(statoScelto)) {
                    System.out.printf("Luogo: %s, Tipo Visita: %s, Volontario: %s, Data: %s%n",
                            visita.getLuogo(), visita.getTipoVisita(), visita.getVolontario(),
                            visita.getData() != null ? visita.getData() : "Nessuna data");
                }
            }
        }
    }

    public void modificaStatoVisita() {
        databaseUpdater.sincronizzaDalDatabase();
        visiteMap = databaseUpdater.getVisiteMap();
    
        if (visiteMap.isEmpty()) {
            System.out.println("Non ci sono visite disponibili da modificare.");
            return;
        }
    
        System.out.println("Visite disponibili:");
        for (Map.Entry<Integer, Visite> entry : visiteMap.entrySet()) {
            Visite visita = entry.getValue();
            System.out.printf("%d. Luogo: %s, Tipo Visita: %s, Stato: %s%n",
                    entry.getKey()+1, visita.getLuogo(), visita.getTipoVisita(), visita.getStato());
        }
    
        int visitaId = InputDati.leggiIntero("Seleziona la visita da modificare: ");
        if (!visiteMap.containsKey(visitaId)) {
            System.out.println("Visita non valida.");
            return;
        }
    
        String[] stati = {"Proposta", "Completa", "Confermata", "Cancellata", "Effettuata"};
        System.out.println("Stati disponibili:");
        for (int i = 0; i < stati.length; i++) {
            System.out.printf("%d. %s%n", i + 1, stati[i]);
        }
    
        int sceltaStato = InputDati.leggiIntero("Seleziona il nuovo stato: ", 1, stati.length) - 1;
        String nuovoStato = stati[sceltaStato];
    
        Visite visitaAggiornata = visiteMap.get(visitaId);
        visitaAggiornata.setStato(nuovoStato);
    
        databaseUpdater.aggiornaVisita(visitaId+1, visitaAggiornata);
        System.out.println("Stato della visita aggiornato con successo.");
    }

    public void visualizzaArchivioStorico() {
        databaseUpdater.sincronizzaDalDatabase();
        visiteMap = databaseUpdater.getVisiteMap();
    
        if (visiteMap.isEmpty()) {
            System.out.println("Non ci sono visite disponibili nell'archivio storico.");
            return;
        }
    
        System.out.println("Archivio storico delle visite effettuate:");
        for (Visite visita : visiteMap.values()) {
            if ("Effettuata".equalsIgnoreCase(visita.getStato())) {
                System.out.printf("Luogo: %s, Tipo Visita: %s, Volontario: %s, Data: %s%n",
                        visita.getLuogo(), visita.getTipoVisita(), visita.getVolontario(),
                        visita.getData() != null ? visita.getData() : "Nessuna data");
            }
        }
    }

    public void assegnaVisita() {
        databaseUpdater.sincronizzaDalDatabase();
        luoghiMap = databaseUpdater.getLuoghiMap();
        volontariMap = databaseUpdater.getVolontariMap();
        visiteMap = databaseUpdater.getVisiteMap();

        // Se non ci sono luoghi o volontari disponibili, mostra un messaggio e termina il metodo
        if (luoghiMap.isEmpty()) {
            System.out.println("Nessun luogo disponibile.");
            return;
        }
        
        System.out.println("Elenco dei luoghi disponibili:");
        List<String> luoghiNomi = new ArrayList<>(luoghiMap.keySet());
        for (int i = 0; i < luoghiNomi.size(); i++) {
            System.out.printf("%d. %s%n", i + 1, luoghiNomi.get(i));
        }
    
        int luogoIndex = InputDati.leggiIntero("Seleziona il numero del luogo: ", 1, luoghiNomi.size()) - 1;
        String luogoNomeScelto = luoghiNomi.get(luogoIndex);
    
        String tipoVisitaScelto = InputDati.leggiStringaNonVuota("Inserisci il tipo di visita: ");
    
        if (volontariMap.isEmpty()) {
            System.out.println("Nessun volontario disponibile.");
            return;
        }
    
        System.out.println("\nElenco dei volontari disponibili:");
        List<String> volontariNomi = new ArrayList<>(volontariMap.keySet());
        for (int i = 0; i < volontariNomi.size(); i++) {
            System.out.printf("%d. %s%n", i + 1, volontariNomi.get(i));
        }
    
        int volontarioIndex = InputDati.leggiIntero("Seleziona il numero del volontario: ", 1, volontariNomi.size()) - 1;
        String volontarioNomeScelto = volontariNomi.get(volontarioIndex);
    
        LocalDate oggi = LocalDate.now();
        YearMonth meseTarget = YearMonth.of(oggi.getYear(), oggi.getMonth().plus(3));
        List<LocalDate> dateValide = new ArrayList<>();
        
        Boolean dataPersonale = InputDati.yesOrNo("Vuoi inserire una data personale? ");
        LocalDate dataVisita;
        if(dataPersonale){
            int anno = InputDati.leggiIntero("Inserisci l'anno della visita: ");
            int mese = InputDati.leggiIntero("Inserisci il mese della visita (1-12): ");
            int giorno = InputDati.leggiIntero("Inserisci il giorno della visita: ");
            LocalDate dataPers = LocalDate.of(anno, mese, giorno);
            dataVisita = dataPers;
        } else {
            for (int giorno = 1; giorno <= meseTarget.lengthOfMonth(); giorno++) {
                LocalDate data = meseTarget.atDay(giorno);
                if (data.getDayOfWeek() != DayOfWeek.SATURDAY && data.getDayOfWeek() != DayOfWeek.SUNDAY) {
                    dateValide.add(data);
                }
            }
        
            System.out.println("\nDate disponibili per la visita:");
            for (int i = 0; i < dateValide.size(); i++) {
                System.out.printf("%d. %s%n", i + 1, dateValide.get(i));
            }
        
            int dataIndex = InputDati.leggiIntero("Seleziona il numero della data: ", 1, dateValide.size()) - 1;
            LocalDate dataI = dateValide.get(dataIndex);
            dataVisita = dataI;
        }

    
        int maxPersone = databaseUpdater.getMaxPersoneDefault();
        String stato = "Proposta"; // Stato iniziale della visita
    
        // Genera un ID univoco per la visita
        int id = visiteMap.size() + 1;
    
        // Crea l'oggetto Visite utilizzando il costruttore completo
        Visite nuovaVisita = new Visite(id, luogoNomeScelto, tipoVisitaScelto, volontarioNomeScelto, dataVisita, maxPersone, stato);
        System.out.println("Visita assegnata con successo per la data " + dataVisita + "!");
        // Aggiungi la visita al database
        databaseUpdater.aggiungiVisita(nuovaVisita);
    
    }

}


// package src.it.unibs.ingsw.gestvisit;

// import java.sql.Connection;
// import java.sql.PreparedStatement;
// import java.sql.ResultSet;
// import java.sql.SQLException;
// import java.time.DayOfWeek;
// import java.time.LocalDate;
// import java.time.YearMonth;
// import java.util.ArrayList;
// import java.util.Arrays;
// import java.util.HashMap;
// import java.util.List;
// import java.util.concurrent.ExecutorService;
// import java.util.concurrent.Executors;

// import it.unibs.fp.mylib.InputDati;

// public class Utilita {

//     private final ExecutorService executorService = Executors.newCachedThreadPool(); // Pool con thread caching
//     private final DatabaseUpdater databaseUpdater = new DatabaseUpdater(executorService);

//     // public static void popolaLuoghi(List<Luogo> luoghi) {
//     //     // Leggi i dati dei luoghi da un file (ad esempio, "luoghi.txt")
//     //     try (BufferedReader br = new BufferedReader(new FileReader(CREDENZIALI_FILE_PATH_LUOGHI))) {
//     //         String line;
//     //         while ((line = br.readLine()) != null) {
//     //             String[] dati = line.split(",");
//     //             String nome = dati[0];
//     //             String descrizione = dati[1];
//     //             String collocazioneGeografica = dati[2];
//     //             Luogo luogo = new Luogo(nome, descrizione, collocazioneGeografica);
//     //             luoghi.add(luogo);
//     //         }
//     //     } catch (IOException e) {
//     //         e.printStackTrace();
//     //     }
//     // }

//     public static void caricaLuoghi(List<Luogo> luoghi) {
//         String sql = "SELECT nome, descrizione FROM luoghi";

//         try (Connection conn = DatabaseConnection.connect();
//             PreparedStatement pstmt = conn.prepareStatement(sql);
//             ResultSet rs = pstmt.executeQuery()) {

//             while (rs.next()) {
//                 String nome = rs.getString("nome");
//                 String descrizione = rs.getString("descrizione");

//                 Luogo luogo = new Luogo(nome, descrizione);
//                 luoghi.add(luogo);
//             }
//         } catch (SQLException e) {
//             System.out.println("Errore durante il caricamento dei luoghi: " + e.getMessage());
//         }
//     }

//     // public static void popolaVolontari(List<Volontario> volontari) {
//     //     // Leggi i dati dei volontari da un file (ad esempio, "volontari.txt")
//     //     try (BufferedReader br = new BufferedReader(new FileReader(CREDENZIALI_FILE_PATH_VOLONTARI))) {
//     //         String line;
//     //         while ((line = br.readLine()) != null) {
//     //             String[] dati = line.split(",");
//     //             String nome = dati[0];
//     //             String cognome = dati[1];
//     //             String email = dati[2];
//     //             String password = dati[3];
//     //             String tipiDiVisite = dati[4];
//     //             Volontario volontario = new Volontario(nome, cognome, email, password, tipiDiVisite);
//     //             volontari.add(volontario);
//     //         }
//     //     } catch (IOException e) {
//     //         e.printStackTrace();
//     //     }
//     // }

//     // public static void creazioneLuoghi(ArrayList<Luogo> luoghi, HashMap<String, List<String>> volontari, HashMap<String, List<String>> tipiVisite){
//     //     Luogo luogo1 = Luogo.creaLuogoUtente("Castello Bonoris", "una fantastica visita alla scoperta del bellissimo castello di montichiari: il luogo che rappresenta appieno questa favola cittadina", "Montichiari, piazza Santa Maria, 36");
//     //     Luogo luogo2 = Luogo.creaLuogoUtente("Pinacoteca Pasinetti: un luogo d'eccellenza per scoprire l'arte monteclarense", "una fantastica visita attraverso le varie epoche dell'arte monteclarense", "Montichiari, Via Trieste, 56");
//     //     luoghi.add(luogo1);
//     //     luoghi.add(luogo2);
//     // }

//     public static void popolaVolontari(List<Volontario> volontari) {
//         String sql = "SELECT nome, cognome, email, password, tipi_di_visite FROM volontari";
    
//         try (Connection conn = DatabaseConnection.connect();
//              PreparedStatement pstmt = conn.prepareStatement(sql);
//              ResultSet rs = pstmt.executeQuery()) {
    
//             while (rs.next()) {
//                 String nome = rs.getString("nome");
//                 String cognome = rs.getString("cognome");
//                 String email = rs.getString("email");
//                 String password = rs.getString("password");
//                 String tipiDiVisite = rs.getString("tipi_di_visite");
    
//                 Volontario volontario = new Volontario(nome, cognome, email, password, tipiDiVisite);
//                 volontari.add(volontario);
//             }
//         } catch (SQLException e) {
//             System.out.println("Errore durante il caricamento dei volontari: " + e.getMessage());
//         }
//     }

//     /*public static void creazioneTipiVisite(ArrayList<Visite> tipiVisite){
//         List<Giorni> giornis = new ArrayList<Giorni>();
//         giornis.add(Giorni.DOMENICA);
//         giornis.add(Giorni.SABATO);
//         giornis.add(Giorni.VENERDI);
//         Visite visita1 = Visite.creaVisite("alla scoperta del castello Bonoris", "magnifica visita guidata all'interno del castello", "nel cortile del castello", "tutto l'anno", giornis, 18, 1, "biglietto acquistabile in loco", 10, "effettuata");
//         Visite visita2 = Visite.creaVisite("alla scoperta del castello di Brescia", "visita libera all'interno del castello", "nessun luogo", "tutto l'anno", giornis, 14, 2, "biglietto acquistabile in loco", 20, "completa");
//         Visite visita3 = Visite.creaVisite("alla scoperta della pinacoteca Pasinetti", "magnifica visita guidata all'interno della celebrissima pinacoteca monteclarense", "nell'atrio della pinacoteca", "tutto l'anno", giornis, 16, 2, "biglietto acquistabile in loco", VisitManager., );
//         tipiVisite.add(visita1);
//         tipiVisite.add(visita2);
//         tipiVisite.add(visita3);
//         // visita1.salvaVisita();
//         // visita2.salvaVisita();
//         // visita3.salvaVisita();
//     }*/

//     // public static void salvaLuoghi(Luogo luogo) {
//     //     try (BufferedWriter bw = new BufferedWriter(new FileWriter(CREDENZIALI_FILE_PATH_LUOGHI))) {
//     //         bw.write(luogo.getNome() + "," + luogo.getCollocazioneGeografica() + "," + luogo.getDescrizione());
//     //         bw.newLine();
//     //     } catch (IOException e) {
//     //         e.printStackTrace();
//     //     }
//     // }

//     public static void salvaLuoghi(Luogo luogo) {
//         String sql = "INSERT INTO luoghi (nome, descrizione) VALUES (?, ?)";
    
//         try (Connection conn = DatabaseConnection.connect();
//              PreparedStatement pstmt = conn.prepareStatement(sql)) {
    
//             pstmt.setString(1, luogo.getNome());
//             pstmt.setString(2, luogo.getDescrizione());
//             pstmt.executeUpdate();
    
//             System.out.println("Luogo salvato con successo.");
//         } catch (SQLException e) {
//             System.out.println("Errore durante il salvataggio del luogo: " + e.getMessage());
//         }
//     }

//     // public static void salvaVolontari(Volontario volontario) {
//     //     try (BufferedWriter bw = new BufferedWriter(new FileWriter(CREDENZIALI_FILE_PATH_VOLONTARI))) {
//     //         bw.write(volontario.getNome() + "," + volontario.getCognome() + "," + volontario.getEmail() + "," + volontario.getPassword() + "," + volontario.getTipiDiVisite());
//     //         bw.newLine();
//     //     } catch (IOException e) {
//     //         e.printStackTrace();
//     //     }
//     // }

//     public static void salvaVolontari(Volontario volontario) {
//         String sql = "INSERT INTO volontari (nome, cognome, email, password, tipi_di_visite) VALUES (?, ?, ?, ?, ?)";
    
//         try (Connection conn = DatabaseConnection.connect();
//              PreparedStatement pstmt = conn.prepareStatement(sql)) {
    
//             pstmt.setString(1, volontario.getNome());
//             pstmt.setString(2, volontario.getCognome());
//             pstmt.setString(3, volontario.getEmail());
//             pstmt.setString(4, volontario.getPassword());
//             pstmt.setString(5, volontario.getTipiDiVisite());
//             pstmt.executeUpdate();
    
//             System.out.println("Volontario salvato con successo.");
//         } catch (SQLException e) {
//             System.out.println("Errore durante il salvataggio del volontario: " + e.getMessage());
//         }
//     }

//     // public static void caricaVisite(HashMap<Luogo, HashMap<String, List<String>>> mappaVisite) {
//     //     try (BufferedWriter bw = new BufferedWriter(new FileWriter(CREDENZIALI_FILE_PATH_VISITE))) {
//     //         for (Luogo luogo : mappaVisite.keySet()) {
//     //             for (String tipoVisita : mappaVisite.get(luogo).keySet()) {
//     //                 for (String volontario : mappaVisite.get(luogo).get(tipoVisita)) {
//     //                     bw.write(luogo.getNome() + "," + tipoVisita + "," + volontario);
//     //                     bw.newLine();
//     //                 }
//     //             }
//     //         }
//     //     } catch (IOException e) {
//     //         e.printStackTrace();
//     //     }
//     // }

//     // public static void caricaVisite(HashMap<Luogo, HashMap<String, List<String>>> mappaVisite) {
//     //     String sql = "INSERT INTO visite (luogo, tipo_visita, volontario, data) VALUES (?, ?, ?, ?)";
    
//     //     try (Connection conn = DatabaseConnection.connect();
//     //          PreparedStatement pstmt = conn.prepareStatement(sql)) {
    
//     //         for (Luogo luogo : mappaVisite.keySet()) {
//     //             for (String tipoVisita : mappaVisite.get(luogo).keySet()) {
//     //                 for (String volontario : mappaVisite.get(luogo).get(tipoVisita)) {
//     //                     pstmt.setString(1, luogo.getNome());
//     //                     pstmt.setString(2, tipoVisita);
//     //                     pstmt.setString(3, volontario);
//     //                     pstmt.setDate(4, java.sql.Date.valueOf(LocalDate.now())); // Usa la data corrente
//     //                     pstmt.addBatch();
//     //                 }
//     //             }
//     //         }
//     //         pstmt.executeBatch();
//     //         System.out.println("Visite salvate con successo.");
//     //     } catch (SQLException e) {
//     //         System.out.println("Errore durante il salvataggio delle visite: " + e.getMessage());
//     //     }
//     // }
    
//     // // Metodo di supporto per ottenere l'ID di un luogo
//     // private static int getLuogoId(Connection conn, String nomeLuogo) throws SQLException {
//     //     String sql = "SELECT id FROM luoghi WHERE nome = ?";
//     //     try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
//     //         pstmt.setString(1, nomeLuogo);
//     //         try (ResultSet rs = pstmt.executeQuery()) {
//     //             if (rs.next()) {
//     //                 return rs.getInt("id");
//     //             }
//     //         }
//     //     }
//     //     return -1; // Ritorna -1 se il luogo non è trovato
//     // }

//     // public static void stampaVisite(HashMap<Luogo, Visite> mappaVisite) {
//     //     for (Luogo luogo : mappaVisite.keySet()) {
//     //         System.out.println("Luogo: " + luogo.getNome());
//     //         Visite visite = mappaVisite.get(luogo);
//     //         System.out.println(visite);
//     //     }
//     // }

//     public static void stampaVisite() {
//         String sql = "SELECT luogo, tipo_visita, volontario, data FROM visite";
    
//         try (Connection conn = DatabaseConnection.connect();
//              PreparedStatement pstmt = conn.prepareStatement(sql);
//              ResultSet rs = pstmt.executeQuery()) {
    
//             System.out.println("Visite:");
//             while (rs.next()) {
//                 String luogo = rs.getString("luogo");
//                 String tipoVisita = rs.getString("tipo_visita");
//                 String volontario = rs.getString("volontario");
//                 LocalDate data = rs.getDate("data").toLocalDate();
    
//                 System.out.println("Luogo: " + luogo);
//                 System.out.println("Tipo Visita: " + tipoVisita);
//                 System.out.println("Volontario: " + volontario);
//                 System.out.println("Data: " + data);
//                 System.out.println("-------------------------");
//             }
//         } catch (SQLException e) {
//             System.out.println("Errore durante la stampa delle visite: " + e.getMessage());
//         }
//     }
    
//     // public static void stampaLuoghi(ArrayList<Luogo> luoghi) {
//     //     System.out.println("Luoghi:");
//     //     for (Luogo luogo : luoghi) {
//     //         System.out.println(luogo);
//     //     }
//     // }

//     public static void stampaLuoghi() {
//         String sql = "SELECT nome, descrizione FROM luoghi";
    
//         try (Connection conn = DatabaseConnection.connect();
//              PreparedStatement pstmt = conn.prepareStatement(sql);
//              ResultSet rs = pstmt.executeQuery()) {
    
//             System.out.println("Luoghi:");
//             while (rs.next()) {
//                 String nome = rs.getString("nome");
//                 String descrizione = rs.getString("descrizione");
    
//                 System.out.println("Nome: " + nome);
//                 System.out.println("Descrizione: " + descrizione);
//                 System.out.println("-------------------------");
//             }
//         } catch (SQLException e) {
//             System.out.println("Errore durante la stampa dei luoghi: " + e.getMessage());
//         }
//     }

//     // public static void stampaVolontari(List<Volontario> volontari) {
//     //     System.out.println("Volontari:");
//     //     for (Volontario volontario : volontari) {
//     //         System.out.println(volontario);
//     //     }
//     // }

//     public static void stampaVolontari() {
//         String sql = "SELECT nome, cognome, email, tipi_di_visite FROM volontari";
    
//         try (Connection conn = DatabaseConnection.connect();
//              PreparedStatement pstmt = conn.prepareStatement(sql);
//              ResultSet rs = pstmt.executeQuery()) {
    
//             System.out.println("Volontari:");
//             while (rs.next()) {
//                 String nome = rs.getString("nome");
//                 String cognome = rs.getString("cognome");
//                 String email = rs.getString("email");
//                 String tipiDiVisite = rs.getString("tipi_di_visite");
    
//                 System.out.println("Nome: " + nome + " " + cognome);
//                 System.out.println("Email: " + email);
//                 System.out.println("Tipi di Visite: " + tipiDiVisite);
//                 System.out.println("-------------------------");
//             }
//         } catch (SQLException e) {
//             System.out.println("Errore durante la stampa dei volontari: " + e.getMessage());
//         }
//     }

//     public static void assegnaVisita() {
//         try (Connection conn = DatabaseConnection.connect()) {
//             // Recupera i luoghi disponibili
//             String sqlLuoghi = "SELECT nome FROM luoghi";
//             PreparedStatement pstmtLuoghi = conn.prepareStatement(sqlLuoghi);
//             ResultSet rsLuoghi = pstmtLuoghi.executeQuery();
    
//             List<String> luogoNomi = new ArrayList<>();
    
//             System.out.println("Elenco dei luoghi disponibili:");
//             while (rsLuoghi.next()) {
//                 String nome = rsLuoghi.getString("nome");
//                 luogoNomi.add(nome);
//                 System.out.println(luogoNomi.size() + ". " + nome);
//             }
    
//             int luogoIndex = InputDati.leggiIntero("Seleziona il numero del luogo: ", 1, luogoNomi.size()) - 1;
//             String luogoNomeScelto = luogoNomi.get(luogoIndex);
    
//             // Recupera i tipi di visita per il luogo selezionato
//             String sqlTipiVisita = "SELECT DISTINCT tipo_visita FROM visite WHERE luogo = ?";
//             PreparedStatement pstmtTipiVisita = conn.prepareStatement(sqlTipiVisita);
//             pstmtTipiVisita.setString(1, luogoNomeScelto);
//             ResultSet rsTipiVisita = pstmtTipiVisita.executeQuery();
    
//             List<String> tipiVisita = new ArrayList<>();
//             System.out.println("Tipi di visita disponibili per il luogo " + luogoNomeScelto + ":");
//             while (rsTipiVisita.next()) {
//                 String tipoVisita = rsTipiVisita.getString("tipo_visita");
//                 tipiVisita.add(tipoVisita);
//                 System.out.println(tipiVisita.size() + ". " + tipoVisita);
//             }
    
//             int tipoVisitaIndex = InputDati.leggiIntero("Seleziona il numero del tipo di visita: ", 1, tipiVisita.size()) - 1;
//             String tipoVisitaScelto = tipiVisita.get(tipoVisitaIndex);
    
//             // Recupera i volontari disponibili
//             String sqlVolontari = "SELECT nome, cognome FROM volontari";
//             PreparedStatement pstmtVolontari = conn.prepareStatement(sqlVolontari);
//             ResultSet rsVolontari = pstmtVolontari.executeQuery();
    
//             List<String> volontarioNomi = new ArrayList<>();
    
//             System.out.println("\nElenco dei volontari disponibili:");
//             while (rsVolontari.next()) {
//                 String nome = rsVolontari.getString("nome") + " " + rsVolontari.getString("cognome");
//                 volontarioNomi.add(nome);
//                 System.out.println(volontarioNomi.size() + ". " + nome);
//             }
    
//             int volontarioIndex = InputDati.leggiIntero("Seleziona il numero del volontario: ", 1, volontarioNomi.size()) - 1;
//             String volontarioNomeScelto = volontarioNomi.get(volontarioIndex);
    
//             // Calcola le date valide (giorni feriali del terzo mese successivo all'attuale)
//             LocalDate oggi = LocalDate.now();
//             YearMonth meseTarget = YearMonth.of(oggi.getYear(), oggi.getMonth().plus(3));
//             List<LocalDate> dateValide = new ArrayList<>();
    
//             for (int giorno = 1; giorno <= meseTarget.lengthOfMonth(); giorno++) {
//                 LocalDate data = meseTarget.atDay(giorno);
//                 if (data.getDayOfWeek() != DayOfWeek.SATURDAY && data.getDayOfWeek() != DayOfWeek.SUNDAY) {
//                     dateValide.add(data);
//                 }
//             }
    
//             // Mostra le date valide all'utente
//             System.out.println("\nDate disponibili per la visita:");
//             for (int i = 0; i < dateValide.size(); i++) {
//                 System.out.println((i + 1) + ". " + dateValide.get(i));
//             }
    
//             int dataIndex = InputDati.leggiIntero("Seleziona il numero della data: ", 1, dateValide.size()) - 1;
//             LocalDate dataVisita = dateValide.get(dataIndex);
    
//             // Assegna la visita al volontario
//             String sqlAssegnaVisita = "INSERT INTO visite (luogo, tipo_visita, volontario, data, stato) VALUES (?, ?, ?, ?, ?)";
//             PreparedStatement pstmtAssegna = conn.prepareStatement(sqlAssegnaVisita);
//             pstmtAssegna.setString(1, luogoNomeScelto);
//             pstmtAssegna.setString(2, tipoVisitaScelto);
//             pstmtAssegna.setString(3, volontarioNomeScelto);
//             pstmtAssegna.setDate(4, java.sql.Date.valueOf(dataVisita));
//             pstmtAssegna.setString(5, "Proposta"); // Stato iniziale della visita
//             pstmtAssegna.executeUpdate();
    
//             System.out.println("Visita assegnata con successo per la data " + dataVisita + "!");
//         } catch (SQLException e) {
//             System.out.println("Errore durante l'assegnazione della visita: " + e.getMessage());
//         }
//     }

//     public static void modificaStatoVisita() {
//         try (Connection conn = DatabaseConnection.connect()) {
//             // Recupera le visite disponibili
//             String sqlVisite = "SELECT id, luogo, tipo_visita, stato FROM visite";
//             PreparedStatement pstmtVisite = conn.prepareStatement(sqlVisite);
//             ResultSet rsVisite = pstmtVisite.executeQuery();
    
//             List<Integer> visitaIds = new ArrayList<>();
//             System.out.println("Visite disponibili:");
//             while (rsVisite.next()) {
//                 int id = rsVisite.getInt("id");
//                 String luogo = rsVisite.getString("luogo");
//                 String tipoVisita = rsVisite.getString("tipo_visita");
//                 String stato = rsVisite.getString("stato");
//                 visitaIds.add(id);
//                 System.out.printf("%d. %s - %s (Stato: %s)%n", visitaIds.size(), luogo, tipoVisita, stato);
//             }
    
//             int sceltaVisita = InputDati.leggiIntero("Seleziona il numero della visita da modificare: ", 1, visitaIds.size()) - 1;
//             int visitaId = visitaIds.get(sceltaVisita);
    
//             // Mostra gli stati disponibili
//             String[] stati = {"Proposta", "Completa", "Confermata", "Cancellata", "Effettuata"};
//             System.out.println("Stati disponibili:");
//             for (int i = 0; i < stati.length; i++) {
//                 System.out.printf("%d. %s%n", i + 1, stati[i]);
//             }
    
//             int sceltaStato = InputDati.leggiIntero("Seleziona il nuovo stato: ", 1, stati.length) - 1;
//             String nuovoStato = stati[sceltaStato];
    
//             // Aggiorna lo stato della visita
//             String sqlUpdate = "UPDATE visite SET stato = ? WHERE id = ?";
//             PreparedStatement pstmtUpdate = conn.prepareStatement(sqlUpdate);
//             pstmtUpdate.setString(1, nuovoStato);
//             pstmtUpdate.setInt(2, visitaId);
//             pstmtUpdate.executeUpdate();
    
//             System.out.println("Stato della visita aggiornato con successo.");
//         } catch (SQLException e) {
//             System.out.println("Errore durante la modifica dello stato della visita: " + e.getMessage());
//         }
//     }
    
//     public static void visualizzaVisitePerStato() {
//         try (Connection conn = DatabaseConnection.connect()) {
//             // Mostra gli stati disponibili
//             String[] stati = {"Proposta", "Completa", "Confermata", "Cancellata", "Effettuata"};
//             System.out.println("Stati disponibili:");
//             for (int i = 0; i < stati.length; i++) {
//                 System.out.printf("%d. %s%n", i + 1, stati[i]);
//             }
    
//             int sceltaStato = InputDati.leggiIntero("Seleziona lo stato da visualizzare: ", 1, stati.length) - 1;
//             String statoScelto = stati[sceltaStato];
    
//             // Recupera le visite per stato
//             String sqlVisite = "SELECT luogo, tipo_visita, volontario, data FROM visite WHERE stato = ?";
//             PreparedStatement pstmtVisite = conn.prepareStatement(sqlVisite);
//             pstmtVisite.setString(1, statoScelto);
//             ResultSet rsVisite = pstmtVisite.executeQuery();
    
//             System.out.printf("Visite in stato '%s':%n", statoScelto);
//             while (rsVisite.next()) {
//                 String luogo = rsVisite.getString("luogo");
//                 String tipoVisita = rsVisite.getString("tipo_visita");
//                 String volontario = rsVisite.getString("volontario");
//                 LocalDate data = rsVisite.getDate("data").toLocalDate();
//                 System.out.printf("Luogo: %s, Tipo Visita: %s, Volontario: %s, Data: %s%n", luogo, tipoVisita, volontario, data);
//             }
//         } catch (SQLException e) {
//             System.out.println("Errore durante la visualizzazione delle visite per stato: " + e.getMessage());
//         }
//     }
    
//     public static void visualizzaArchivioStorico() {
//         try (Connection conn = DatabaseConnection.connect()) {
//             // Recupera le visite in stato "Effettuata"
//             String sqlArchivio = "SELECT luogo, tipo_visita, volontario, data FROM visite WHERE stato = 'Effettuata'";
//             PreparedStatement pstmtArchivio = conn.prepareStatement(sqlArchivio);
//             ResultSet rsArchivio = pstmtArchivio.executeQuery();
    
//             System.out.println("Archivio storico delle visite effettuate:");
//             while (rsArchivio.next()) {
//                 String luogo = rsArchivio.getString("luogo");
//                 String tipoVisita = rsArchivio.getString("tipo_visita");
//                 String volontario = rsArchivio.getString("volontario");
//                 LocalDate data = rsArchivio.getDate("data").toLocalDate();
//                 System.out.printf("Luogo: %s, Tipo Visita: %s, Volontario: %s, Data: %s%n", luogo, tipoVisita, volontario, data);
//             }
//         } catch (SQLException e) {
//             System.out.println("Errore durante la visualizzazione dell'archivio storico: " + e.getMessage());
//         }
//     }

//     public static void setMaxPersonePerVisita(int maxPersonePerVisita) {
//         String sql = "UPDATE visite SET max_persone = ?";
    
//         try (Connection conn = DatabaseConnection.connect();
//              PreparedStatement pstmt = conn.prepareStatement(sql)) {
    
//             // Imposta il nuovo valore per il numero massimo di persone
//             pstmt.setInt(1, maxPersonePerVisita);
//             int rowsUpdated = pstmt.executeUpdate();
    
//             if (rowsUpdated > 0) {
//                 System.out.println("Numero massimo di persone per visita aggiornato con successo.");
//             } else {
//                 System.out.println("Nessuna visita trovata da aggiornare.");
//             }
//         } catch (SQLException e) {
//             System.out.println("Errore durante l'aggiornamento del numero massimo di persone per visita: " + e.getMessage());
//         }
//     }

// }