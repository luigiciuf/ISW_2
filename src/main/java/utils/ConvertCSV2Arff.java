package utils;

import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class ConvertCSV2Arff {
    // Costruttore privato per nascondere quello pubblico implicito
    private ConvertCSV2Arff() {
        // Costruttore privato
    }
    private static final String BOOKKEEPER_DATASET = "BOOKKEEPER";
    private static final String ZOOKEEPER_DATASET = "ZOOKEEPER";

    public static void convertCsvToArff(String csvPath, String arffPath) throws IOException {
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
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        // qui andava il print rimosso, per code smell, per scegliere il dataset da convertire
        // Richiesta all'utente di inserire la scelta del dataset
        String datasetChoice = scanner.nextLine().toUpperCase(); // Leggi l'input e convertilo in maiuscolo

        String csvPath;
        switch (datasetChoice) {
            case BOOKKEEPER_DATASET:
                // Path del file CSV per il dataset BOOKKEEPER
                csvPath = getPathForDataset(BOOKKEEPER_DATASET);
                break;
            case ZOOKEEPER_DATASET:
                // Path del file CSV per il dataset ZOOKEEPER
                csvPath = getPathForDataset(ZOOKEEPER_DATASET);
                break;
            default:
                return;
        }
        // Nome del file ARFF basato sulla scelta del dataset
        String arffFileName = datasetChoice + "dataset.arff";
        // Path completo del file ARFF di output
        String arffPath = Parameters.BASE_PATH + arffFileName;

        try {
                convertCsvToArff(csvPath, arffPath); // Chiama il metodo per convertire il CSV in ARFF
        } finally {
            // Chiudi lo scanner alla fine per evitare memory leak
            scanner.close();
        }

    }
    // Funzione per ottenere il percorso del file CSV in base al dataset
    private static String getPathForDataset(String dataset) {
        switch (dataset) {
            case BOOKKEEPER_DATASET:
                    return Parameters.BASE_PATH + "BOOKKEEPER_filter.csv";
            case ZOOKEEPER_DATASET:
                return Parameters.BASE_PATH + "ZOOKEEPER_filter.csv";
            default:
                throw new IllegalArgumentException("Dataset non supportato: " + dataset);
        }
    }
}
