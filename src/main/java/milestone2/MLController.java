package milestone2;

import java.io.IOException;
import java.util.Scanner;
import static Utils.ConvertCSV2Arff.convertCsvToArff;

public class MLController {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Richiesta all'utente di inserire la scelta del dataset
        System.out.println("Choose the dataset to convert (BOOKKEEPER or ZOOKEEPER): ");
        String datasetChoice = scanner.nextLine().toUpperCase(); // Leggi l'input e convertilo in maiuscolo

        String csvPath;
        switch (datasetChoice) {
            case "BOOKKEEPER":
                // Path del file CSV per il dataset BOOKKEEPER
                csvPath = "C:/Users/luigi/IdeaProjects/ISW_2/BOOKKEEPERdataset.csv";
                break;
            case "ZOOKEEPER":
                // Path del file CSV per il dataset ZOOKEEPER
                csvPath = "C:/Users/luigi/IdeaProjects/ISW_2/ZOOKEEPERdataset.csv";
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
        } catch (IOException e) {
            System.err.println("An error occurred during conversion: " + e.getMessage());
        } finally {
            // Chiudi lo scanner alla fine per evitare memory leak
            scanner.close();
        }

    }
}