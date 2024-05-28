package Utils;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CSVFilter {
    public static void main(String[] args) {
        // Percorsi dei file
        String versionsCsvFilePath = "BOOKKEEPERVersionInfo.csv";
        String dataCsvFilePath = "BOOKKEEPERdataset.csv";
        String outputCsvFilePath = "BOOKKEEPER_filter.csv";

        // Set per memorizzare le versioni uniche
        Set<String> versions = new HashSet<>();

        // considero la prima metà delle versioni del file csv per andare ad ottenere un csv filtrato
        String[] targetVersions = {
                "4.0.0", "4.1.0", "4.1.1", "4.2.0", "4.2.1", "4.2.2", "4.3.0"
        };

        for (String version : targetVersions) {
            versions.add(version);
        }

        // Leggi il file dei dati CSV e filtra le righe, salvandole nel nuovo file CSV
        try (BufferedReader dataReader = new BufferedReader(new FileReader(dataCsvFilePath));
             FileWriter writer = new FileWriter(outputCsvFilePath)) {

            String row;
            String header = dataReader.readLine(); // Leggi l'intestazione
            if (header != null) {
                writer.write(header + "\n"); // Scrivi l'intestazione nel file di output
            }

            while ((row = dataReader.readLine()) != null) {
                // Dividi la riga per virgole e prendi la prima colonna
                String[] parts = row.split(",");
                if (parts.length > 0 && versions.contains(parts[0])) {
                    writer.write(row + "\n");
                    System.out.println(row); // Stampa la riga
                }
            }
        } catch (IOException e) {
            System.out.println("Errore durante la lettura del file dei dati o la scrittura del file filtrato: " + e.getMessage());
        }
    }
}