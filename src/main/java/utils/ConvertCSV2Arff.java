package utils;

import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;
import java.io.File;
import java.util.Scanner;

public class ConvertCSV2Arff {
    // Costruttore privato per nascondere quello pubblico implicito
    private ConvertCSV2Arff() {
        // Costruttore privato
    }
    public static void convertCsvToArff(String csvPath, String arffPath) throws Exception {
        // Load CSV
        CSVLoader csvLoader = new CSVLoader();
        csvLoader.setSource(new File(csvPath));
        csvLoader.setNominalAttributes("last"); // Imposta l'ultimo attributo come nominale
        Instances data = csvLoader.getDataSet();

        // Save ARFF
        ArffSaver arffSaver = new ArffSaver();
        arffSaver.setInstances(data);
        arffSaver.setFile(new File(arffPath));
        arffSaver.writeBatch();
    }
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        // qui andava il print rimosso, per code smell, per scegliere il dataset da convertire
        // Richiesta all'utente di inserire la scelta del dataset
        String datasetChoice = scanner.nextLine().toUpperCase(); // Leggi l'input e convertilo in maiuscolo

        String csvPath;
        switch (datasetChoice) {
            case "BOOKKEEPER":
                // Path del file CSV per il dataset BOOKKEEPER
                csvPath = "C:/Users/luigi/IdeaProjects/ISW_2/BOOKKEEPER_filter.csv";
                break;
            case "ZOOKEEPER":
                // Path del file CSV per il dataset ZOOKEEPER
                csvPath = "C:/Users/luigi/IdeaProjects/ISW_2/ZOOKEEPER_filter.csv";
                break;
            default:
                // Se la scelta del dataset non è valida, stampa un messaggio di errore e termina il programma
                System.err.println("Invalid dataset choice.");
                return;
        }
        // Nome del file ARFF basato sulla scelta del dataset
        String arffFileName = datasetChoice + "dataset.arff";
        // Path completo del file ARFF di output
        String arffPath = "C:/Users/luigi/IdeaProjects/ISW_2/" + arffFileName;

        try {
            convertCsvToArff(csvPath, arffPath); // Chiama il metodo per convertire il CSV in ARFF
            System.out.println("Conversion completed successfully.");
        } catch (Exception e) {
            System.err.println("An error occurred during conversion: " + e.getMessage());
        } finally {
            // Chiudi lo scanner alla fine per evitare memory leak
            scanner.close();
        }

    }
}
