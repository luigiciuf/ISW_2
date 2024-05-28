package milestone2;

import weka.attributeSelection.AttributeSelection;
import weka.attributeSelection.BestFirst;
import weka.attributeSelection.CfsSubsetEval;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;
import weka.filters.Filter;
import weka.filters.supervised.instance.Resample;
import weka.filters.supervised.instance.SMOTE;
import weka.filters.supervised.instance.SpreadSubsample;
import weka.filters.unsupervised.attribute.Remove;
import weka.filters.unsupervised.instance.RemoveWithValues;

import java.text.DecimalFormat;

public class DataProcessor {
    private Instances trainingData; // Dati di addestramento
    private Instances testingData; // Dati di test

    // Costruttore di default
    public DataProcessor() {
        super();
    }

    // Setter per i dati di addestramento
    public void setTrainingData(Instances trainingData) {
        this.trainingData = trainingData;
    }

    // Setter per i dati di test
    public void setTestingData(Instances testingData) {
        this.testingData = testingData;
    }

    // Metodo per la selezione delle caratteristiche
    // selectionMethod - metodo di selezione (es. "BEST_FIRST")
    public void performFeatureSelection(String selectionMethod) throws Exception {
        if (selectionMethod.equals("BEST_FIRST")) {
            // Inizializza la selezione degli attributi con BestFirst e CfsSubsetEval
            AttributeSelection attributeSelection = new AttributeSelection();
            attributeSelection.setEvaluator(new CfsSubsetEval());
            attributeSelection.setSearch(new BestFirst());
            attributeSelection.SelectAttributes(trainingData);

            // Filtro per rimuovere gli attributi non selezionati
            Remove removeFilter = new Remove();
            removeFilter.setAttributeIndicesArray(attributeSelection.selectedAttributes());
            removeFilter.setInvertSelection(true); // Mantiene solo gli attributi selezionati
            removeFilter.setInputFormat(trainingData);

            // Applica il filtro ai dati di addestramento e test
            trainingData = Filter.useFilter(trainingData, removeFilter);
            testingData = Filter.useFilter(testingData, removeFilter);
        }
    }

    // Metodo per applicare tecniche di campionamento
    // samplingMethod - metodo di campionamento (es. "OVERSAMPLING", "UNDERSAMPLING", "SMOTE")
    public void applySampling(String samplingMethod) throws Exception {
        if (samplingMethod.equals("OVERSAMPLING")) {
            // Applica l'oversampling con Resample
            Resample resample = new Resample();
            resample.setInputFormat(trainingData);
            DecimalFormat df = new DecimalFormat("#.##");
            resample.setOptions(Utils.splitOptions(String.format("-B 1.0 -Z %s", df.format(computeMajorityClassPercentage()))));
            trainingData = Filter.useFilter(trainingData, resample);
        } else if (samplingMethod.equals("UNDERSAMPLING")) {
            // Applica l'undersampling con SpreadSubsample
            SpreadSubsample undersample = new SpreadSubsample();
            undersample.setInputFormat(trainingData);
            undersample.setOptions(Utils.splitOptions("-M 1.0"));
            trainingData = Filter.useFilter(trainingData, undersample);
        } else if (samplingMethod.equals("SMOTE")) {
            // Applica SMOTE
            SMOTE smote = new SMOTE();
            smote.setInputFormat(trainingData);
            trainingData = Filter.useFilter(trainingData, smote);
        }
    }

    // Calcola la percentuale della classe maggioritaria
    private double computeMajorityClassPercentage() {
        int positiveClasses = 0;
        Instances data = new Instances(trainingData);
        data.addAll(trainingData);

        // Conta le istanze della classe positiva
        for (Instance instance : data) {
            String classValue = instance.stringValue(instance.numAttributes() - 1);
            if (classValue.equals("1"))
                positiveClasses++;
        }

        // Calcola la percentuale
        double percentage = (100.0 * 2 * positiveClasses / data.size());
        return percentage >= 50 ? percentage : 100 - percentage;
    }

    // Crea il set di addestramento rimuovendo i dati delle release future
    // dataset - l'insieme di dati completo
    // trainingRelease - l'indice della release per l'addestramento
    // totalReleases - il numero totale di release
    public Instances createTrainingSet(Instances dataset, int trainingRelease, int totalReleases) throws Exception {
        RemoveWithValues removeFilter = new RemoveWithValues();
        int range = totalReleases - trainingRelease;
        int[] indices = new int[range];

        // Imposta gli indici delle release da rimuovere
        for (int i = 1; i <= range; i++) {
            indices[range - i] = totalReleases - i;
        }

        removeFilter.setAttributeIndex("1"); // Rimuove in base al primo attributo (release)
        removeFilter.setNominalIndicesArr(indices);
        removeFilter.setInputFormat(dataset);

        // Applica il filtro per ottenere il set di addestramento
        return Filter.useFilter(dataset, removeFilter);
    }

    // Crea il set di test rimuovendo i dati delle release precedenti
    // dataset - l'insieme di dati completo
    // trainingRelease - l'indice della release per l'addestramento
    public Instances createTestingSet(Instances dataset, int trainingRelease) throws Exception {
        String options = String.format("-C 1 -L %d -V", trainingRelease + 1);
        RemoveWithValues removeFilter = new RemoveWithValues();

        removeFilter.setOptions(Utils.splitOptions(options));
        removeFilter.setInputFormat(dataset);

        // Applica il filtro per ottenere il set di test
        return Filter.useFilter(dataset, removeFilter);
    }
}
