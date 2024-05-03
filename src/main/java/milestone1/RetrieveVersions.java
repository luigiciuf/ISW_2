package milestone1;

import java.io.BufferedReader; // Import per leggere file
import java.io.FileReader; // Import per leggere file di testo
import java.io.FileWriter; // Import per scrivere su file
import java.io.IOException; // Eccezioni per operazioni di I/O
import java.text.ParseException; // Eccezioni per errori di parsing
import java.text.SimpleDateFormat; // Formattazione di date
import java.time.LocalDate; // Rappresentazione di date (Java 8+)
import java.time.LocalDateTime; // Rappresentazione di date con orario
import java.util.List; // Import per le liste
import java.util.Map; // Import per le mappe
import java.util.logging.Level; // Import per livelli di logging
import java.util.logging.Logger; // Import per log
import java.util.ArrayList; // Import per liste dinamiche
import java.util.Collections; // Utilità per ordinamento
import java.util.Comparator; // Interfaccia per comparatori
import java.util.Date; // Rappresentazione di date
import java.util.HashMap; // Import per mappe dinamiche
import java.util.regex.Pattern; // Import per pattern regex
import java.util.stream.Collectors; // Import per stream e operazioni su collezioni

import Utils.Utilities;
import model.Version;
import org.json.JSONArray; // Libreria per JSON
import org.json.JSONException; // Eccezioni per errori JSON
import org.json.JSONObject; // Oggetti JSON




// Classe responsabile di recuperare e gestire informazioni sulle versioni
public class RetrieveVersions {
    // Costruttore privato per impedire istanziazioni
    private RetrieveVersions() {
        super(); // Chiamata al costruttore della superclasse
    }

    // Logger per registrare messaggi di log
    private static final Logger LOGGER = Logger.getLogger("Analyzer");

    // Mappe per associazioni tra date e nomi/ID delle release
    private static Map<LocalDateTime, String> releaseNames; // Mappa di nomi delle release
    private static Map<LocalDateTime, String> releaseID; // Mappa di ID delle release
    private static List<LocalDateTime> releases; // Lista di date delle release

    // Metodo per recuperare informazioni sulle release da JIRA
    public static void GetRealeaseInfo(String projName) throws IOException, JSONException {
        // Inizializza la lista delle release
        releases = new ArrayList<>();

        // URL per recuperare informazioni dal servizio JIRA
        String url = "https://issues.apache.org/jira/rest/api/2/project/" + projName;

        // Legge JSON dall'URL e ottiene array di versioni
        JSONObject json = Utilities.readJsonFromUrl(url); // Lettura del JSON
        JSONArray versions = json.getJSONArray("versions"); // Array di versioni

        // Inizializza le mappe per nomi e ID delle release
        releaseNames = new HashMap<>();
        releaseID = new HashMap<>();

        // Itera sulle versioni per estrarre informazioni
        for (int i = 0; i < versions.length(); i++) {
            String name = ""; // Nome della versione
            String id = ""; // ID della versione

            // Se la versione ha una data di release, aggiungi alla lista
            if (versions.getJSONObject(i).has("releaseDate")) {
                if (versions.getJSONObject(i).has("name"))
                    name = versions.getJSONObject(i).get("name").toString();
                if (versions.getJSONObject(i).has("id"))
                    id = versions.getJSONObject(i).get("id").toString();
                addRelease(versions.getJSONObject(i).get("releaseDate").toString(), name, id); // Aggiunge alla lista
            }
        }

        // order releases by date
        Collections.sort(releases, new Comparator<LocalDateTime>(){
            //@Override
            public int compare(LocalDateTime o1, LocalDateTime o2) {
                return o1.compareTo(o2);
            }
        });

        // Se ci sono meno di 6 release, esci dalla funzione
        if (releases.size() < 6) {
            LOGGER.warning("Il progetto ha meno di 6 release. Impossibile procedere");
            return;
        }

        // Scrive informazioni su un file CSV
        try (FileWriter fileWriter = new FileWriter(projName + "VersionInfo.csv")) {
            // Intestazione del CSV
            fileWriter.append("Index,Version ID,Version Name,Date").append("\n");

            // Aggiunge i dettagli delle release al CSV
            for (int i = 0; i < releases.size(); i++) {
                fileWriter.append(Integer.toString(i + 1)); // Indice
                fileWriter.append(",").append(releaseID.get(releases.get(i))); // ID della versione
                fileWriter.append(",").append(releaseNames.get(releases.get(i))); // Nome della versione
                fileWriter.append(",").append(releases.get(i).toString()); // Data della versione
                fileWriter.append("\n");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore durante la scrittura del file CSV", e); // Gestione delle eccezioni
            e.printStackTrace(); // Stampa stack trace
        }
    }

    // Metodo privato per aggiungere una release alla lista
    private static void addRelease(String strDate, String name, String id) {
        LocalDate date = LocalDate.parse(strDate); // Converte la data da stringa a LocalDate
        LocalDateTime dateTime = date.atStartOfDay(); // Converte a LocalDateTime

        if (!releases.contains(dateTime)) { // Se la release non è già presente
            releases.add(dateTime); // Aggiungi alla lista delle release
        }

        // Aggiunge i dettagli alle mappe
        releaseNames.put(dateTime, name);
        releaseID.put(dateTime, id);
    }

    // Metodo per leggere le versioni da un file CSV
    public static List<Version> GetVersions(String pathVersion) throws IOException, JSONException {
        // Crea un pattern per dividere le righe
        Pattern pattern = Pattern.compile(",");

        // Usa BufferedReader per leggere il CSV
        BufferedReader in = new BufferedReader(new FileReader(pathVersion));

        // Crea una lista di versioni dal CSV
        List<Version> versions = in.lines().skip(1).map(line -> {
            String[] x = pattern.split(line); // Divide la riga in parti
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm"); // Formattazione della data
            Date d = null; // Data di fine della versione
            try {
                d = sdf.parse(x[3]); // Converte da stringa a data
            } catch (ParseException e) {
                e.printStackTrace(); // Gestione dell'eccezione
            }
            return new Version(Long.parseLong(x[1]), x[2], d); // Crea un oggetto Version
        }).collect(Collectors.toList());

        in.close(); // Chiude il BufferedReader

        // Imposta la data di inizio per le versioni
        Date d = null;
        for (int i = 0; i < versions.size(); i++) {
            versions.get(i).setStartDate(d); // Imposta la data di inizio
            d = versions.get(i).getEndDate(); // Aggiorna la data per la prossima versione
        }

        return versions; // Restituisce la lista delle versioni
    }

    // Metodo per trovare una versione da una data
    public static Version FindVersion(Date date, List<Version> allVersions) {
        // Cerca la prima versione che non è precedente alla data
        for (Version v : allVersions) {
            if (!v.isBefore(date)) {
                return v;
            }
        }
        return null; // Se nessuna versione è trovata
    }
}