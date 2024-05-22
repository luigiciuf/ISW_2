package Utils;

import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;

import java.io.File;
import java.io.IOException;

public class ConvertCSV2Arff {
    public static void convertCsvToArff(String csvPath, String arffPath) throws IOException {
        // Load CSV
        CSVLoader csvLoader = new CSVLoader();
        csvLoader.setSource(new File(csvPath));
        Instances data = csvLoader.getDataSet();

        // Save ARFF
        ArffSaver arffSaver = new ArffSaver();
        arffSaver.setInstances(data);
        arffSaver.setFile(new File(arffPath));
        arffSaver.writeBatch();
    }
}
