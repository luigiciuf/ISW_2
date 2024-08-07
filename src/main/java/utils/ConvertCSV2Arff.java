package utils;

import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class ConvertCSV2Arff {

    private ConvertCSV2Arff() {

    }
    private static final String BOOKKEEPER_DATASET = "BOOKKEEPER";
    private static final String ZOOKEEPER_DATASET = "ZOOKEEPER";

    /**
     * Converte un file CSV in un file ARFF.
     * @param csvPath Il percorso del file CSV da convertire.
     * @param arffPath Il percorso del file ARFF di destinazione.
     * @throws IOException Se si verifica un errore durante la lettura o la scrittura dei file.
     */
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
        String datasetChoice = scanner.nextLine().toUpperCase();
        String csvPath;
        switch (datasetChoice) {
            case BOOKKEEPER_DATASET:
                csvPath = getPathForDataset(BOOKKEEPER_DATASET);
                break;
            case ZOOKEEPER_DATASET:
                csvPath = getPathForDataset(ZOOKEEPER_DATASET);
                break;
            default:
                return;
        }
        String arffFileName = datasetChoice + "dataset.arff";
        String arffPath = Parameters.getBasePath() + arffFileName;

        try {
                convertCsvToArff(csvPath, arffPath);
        } finally {
            scanner.close();
        }

    }
    /**
     * Funzione per ottenere il percorso del file CSV in base al dataset.
     * @param dataset Il nome del dataset.
     * @return Il percorso del file CSV.
     */
    private static String getPathForDataset(String dataset) {
        switch (dataset) {
            case BOOKKEEPER_DATASET:
                    return Parameters.getBasePath() + "BOOKKEEPER_filter.csv";
            case ZOOKEEPER_DATASET:
                return Parameters.getBasePath() + "ZOOKEEPER_filter.csv";
            default:
                throw new IllegalArgumentException("Dataset non supportato: " + dataset);
        }
    }
}
