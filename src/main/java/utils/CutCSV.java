package utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

public class CutCSV {
    private static final Logger LOGGER = Logger.getLogger(Parameters.class.getName());

    public static void cutCsv(String projectName) {
        // Percorsi dei file basati sul nome del progetto
        String versionsCsvFilePath = projectName + "VersionInfo.csv";
        String dataCsvFilePath = projectName + "dataset.csv";
        String outputCsvFilePath = projectName + "_filter.csv";

        Set<String> versions = new HashSet<>();
        try (BufferedReader versionsReader = new BufferedReader(new FileReader(versionsCsvFilePath))) {
            List<String> allVersions = new ArrayList<>();
            String row;
            while ((row = versionsReader.readLine()) != null) {
                String[] parts = row.split(",");
                if (parts.length > 2) {
                    allVersions.add(parts[2]); // Terza colonna
                }
            }
            // Considera solo la prima metà delle versioni
            int halfSize = allVersions.size() / 2;
            for (int i = 0; i < halfSize; i++) {
                versions.add(allVersions.get(i));
            }
        } catch (IOException e) {
            LOGGER.severe("Errore durante la lettura del file delle versioni: " + e.getMessage());
            return;
        }

        // Filtra il dataset basato sulle versioni selezionate
        try (BufferedReader dataReader = new BufferedReader(new FileReader(dataCsvFilePath));
             BufferedWriter writer = new BufferedWriter(new FileWriter(outputCsvFilePath))) {

            String row;
            String header = dataReader.readLine(); // Leggi l'intestazione
            if (header != null) {
                writer.write(header);
                writer.newLine();
            }

            while ((row = dataReader.readLine()) != null) {
                String[] parts = row.split(",");
                if (parts.length > 0 && versions.contains(parts[0])) {
                    writer.write(row);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            LOGGER.severe("Errore durante la lettura del file dei dati o la scrittura del file filtrato: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        // Esempio di chiamata alla funzione cutCsv
        cutCsv("ZOOKEEPER");
    }
}
