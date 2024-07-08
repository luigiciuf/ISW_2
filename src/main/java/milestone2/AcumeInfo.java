package milestone2;
import com.opencsv.exceptions.CsvValidationException;
import model.Acume;
import weka.classifiers.Classifier;
import weka.core.Instance;
import weka.core.Instances;
import com.opencsv.CSVReader;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import Utils.Parameters;

public class AcumeInfo {
    private static final Logger LOGGER = Logger.getLogger("Analyzer");


    private AcumeInfo(){}

    public static String getNPofB20(Instances testingSet, Classifier cls ) {

        String output = String.format("Calculating NPofB20%n");
        LOGGER.info(output);
        List<Acume> acumeList = new ArrayList<>();

        Acume acumeObject;

        try {
            // Print predictions for each instance in the testing set
            for (int i = 0; i < testingSet.numInstances(); i++) {


                Instance instance = testingSet.instance(i);

                boolean actual = false;
                int actualClass = (int) instance.classValue(); //actual
                if (actualClass == 1) {
                    actual = true;
                }

                double[] distribution = cls.distributionForInstance(instance);
                double predictedProbability = distribution[1]; //predicted
                // Truncate predictedProbability to three decimal places
                BigDecimal bd = BigDecimal.valueOf(predictedProbability).setScale(3, RoundingMode.DOWN);
                predictedProbability = bd.doubleValue();


                double size = instance.value(instance.attribute(1)); //size
                acumeObject = new Acume(i, size, predictedProbability, actual);
                acumeList.add(acumeObject);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in ACUME.csv writer", e);
        }

        writeAcumeCsv(acumeList);
        String nPofB20 = evaluateNPofB20();
        eliminateGeneratedFiles();
        return nPofB20;
    }


    private static void writeAcumeCsv(List<Acume> acumeList){
        String output = String.format("Assembling CSV file for ACUME%n");
        LOGGER.info(output);

        try (FileWriter fileWriter = new FileWriter(Parameters.ACUME_DRECTORY +"Acume.csv")) {

            fileWriter.append("ID,Size,Predicted,Actual");
            fileWriter.append("\n");

            for (Acume acume : acumeList) {
                String line = getString(acume);
                fileWriter.append(line);
            }

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in ACUME.csv writer", e);
        }

        output = String.format(".csv file created!%n");
        LOGGER.info(output);
    }

    private static String getString(Acume acume){
        return String.format("%s,%s,%s,%s%n",acume.getId(),(int)acume.getSize(),acume.getPredicted(),acume.getActualStringValue());
    }

    private static String evaluateNPofB20() {
        String nPofB20 = null;

        try {
            // Work directory
            File directory = new File(Parameters.ACUME_DRECTORY);
            if (!directory.exists()) {
                throw new IOException("Directory " + Parameters.ACUME_DRECTORY + " does not exist.");
            }

            ProcessBuilder processBuilder = new ProcessBuilder("python", "main.py", "NPofB");
            // Imposta il percorso di lavoro del processo sulla directory "acume"
            processBuilder.directory(new File("C:/Users/luigi/IdeaProjects/ISW_2/acume/"));
            // Redirect error stream to ensure we capture any errors from the script
            processBuilder.redirectErrorStream(true);

            // Start the process
            Process process = processBuilder.start();

            // Capture output and error stream
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    LOGGER.info(line);
                }
            }

            // Wait for the process to complete
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new IOException("Python script exited with error code " + exitCode);
            }

            // Verify that the output file exists
            File outputFile = new File(Parameters.ACUME_DRECTORY, "EAM_NEAM_output.csv");
            if (!outputFile.exists()) {
                throw new FileNotFoundException("Output file EAM_NEAM_output.csv not found in directory " + Parameters.ACUME_DRECTORY);
            }

            // Extract data from CSV
            nPofB20 = extractNPofB();

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "IO error while evaluating NPofB", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOGGER.log(Level.SEVERE, "Thread interrupted while evaluating NPofB", e);
        }
        return nPofB20;
    }

    private static String extractNPofB() {
        String csvFile = Parameters.ACUME_DRECTORY + "/EAM_NEAM_output.csv"; // Search for the output file
        try (CSVReader reader = new CSVReader(new FileReader(csvFile))) {
            // ignore the first line
            reader.readNext();
            reader.readNext();

            // read the second line with the data
            String[] nextLine = reader.readNext();
            //return the fourth column in (NPofB20)
            return nextLine[3];
        } catch (IOException | CsvValidationException e) {
            LOGGER.log(Level.SEVERE, "Error while evaluating NPofB", e);
            return null;
        }
    }

    private static void eliminateGeneratedFiles(){
        File file1 = new File(Parameters.ACUME_DRECTORY+"Acume.csv");
        File file2 = new File(Parameters.ACUME_DRECTORY+"EAM_NEAM_output.csv");
        File file3 = new File(Parameters.ACUME_DRECTORY+"norm_EAM_NEAM_output.csv");

        if(file1.delete()&&file2.delete()&&file3.delete()){
            String output = String.format("Generated files from ACUME deleted%n");
            LOGGER.info(output);
        } else{
            LOGGER.log(Level.SEVERE, "Error while eliminating generated files from ACUME%n");
        }

    }

}