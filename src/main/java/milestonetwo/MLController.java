package milestonetwo;


import utils.Parameters;
import modelml.EvaluationML;
import weka.classifiers.Classifier;
import weka.classifiers.CostMatrix;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.lazy.IBk;
import weka.classifiers.meta.CostSensitiveClassifier;
import weka.classifiers.trees.RandomForest;
import weka.core.converters.ConverterUtils.DataSource;
import modelml.ProfileML;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import weka.core.Instances;
import weka.classifiers.Evaluation;

public class MLController {
    private MLController(){
        super();
    }
    public static final String PROJ = Parameters.PROJECT1;
    private static final Logger LOGGER = Logger.getLogger("Analyzer");
    private static String nPofB20 = null;
    private static String pathdelimiter = "/";


    public static void main(String[] args) throws Exception{
        //load dataset
        String projPath = System.getProperty("user.dir");
        String datasetPath = projPath + pathdelimiter + PROJ + "dataset.arff";
        DataSource source = new DataSource(datasetPath);
        Instances dataset = source.getDataSet();
        dataset.deleteStringAttributes();
        // Lista per memorizzare le valutazioni
        List<EvaluationML> evals = new ArrayList<>();
        int numAttr = dataset.numAttributes();
        int numVers = dataset.attribute(0).numValues();
        FilterDB filter = new FilterDB();
        Evaluation eval;
        // Ciclo su tutte le combinazioni di Feature Selection, Sampling e Cost Sensitivity
        for (ProfileML.FeatureSelection fs: ProfileML.FeatureSelection.values()) {  			// Feature Selection
            for (ProfileML.SamplingMethod smp: ProfileML.SamplingMethod.values()) {		// Sampling
                for (ProfileML.CostSensitivity cs: ProfileML.CostSensitivity.values()) {		// Cost Sensitive
                    for (int i = 1; i < numVers; i++) {				// Walk forward database separation
                        // Walk Forward
                        Instances train = filter.getTrainSet(dataset,i,numVers);
                        Instances test = filter.getTestSet(dataset,i);
                        train.deleteAttributeAt(0);
                        test.deleteAttributeAt(0);
                        train.setClassIndex(numAttr - 2);
                        test.setClassIndex(numAttr - 2);
                        // Feature selection and sampling
                        filter.train = train;
                        filter.test = test;
                        filter.featureSelection(fs);
                        filter.sampling(smp);
                        train = filter.train;
                        test = filter.test;

                        // Execute
                        for(ProfileML.Classifier classif : ProfileML.Classifier.values()) {		// 3 Classifier: RandomForest, NaiveBayes, Ibk
                            eval = run(train, test, classif, cs);
                            evals.add(new EvaluationML(eval, fs, smp, cs, classif, nPofB20));
                        }
                    }
                }
            }
        }

        createCsv(evals, numVers, 3);
    }
    /**
     * Esegue la valutazione del modello di classificazione sui set di dati di addestramento e test.
     *
     * @param train  Il set di dati di addestramento
     * @param test   Il set di dati di test
     * @param classif Il tipo di classificatore da utilizzare
     * @param cs     Il tipo di sensibilità ai costi da applicare
     * @return L'oggetto Evaluation che contiene i risultati della valutazione
     * @throws Exception In caso di errori durante la costruzione del classificatore o la valutazione
     */
    public static Evaluation run(Instances train, Instances test, ProfileML.Classifier classif, ProfileML.CostSensitivity cs) throws Exception {
        Classifier classifier = getClassifier(classif);

        CostSensitiveClassifier costSensitiveClassifier = new CostSensitiveClassifier();
        CostMatrix costMatrix = getCostMatrix(10.0, 1.0);
        costSensitiveClassifier.setClassifier(classifier);
        costSensitiveClassifier.setCostMatrix(costMatrix);

        Evaluation evaluation;

        if (cs != ProfileML.CostSensitivity.NO_COST_SENSITIVE) {
            boolean minimizeExpectedCost = (cs == ProfileML.CostSensitivity.SENSITIVE_THRESHOLD);
            costSensitiveClassifier.setMinimizeExpectedCost(minimizeExpectedCost);
            costSensitiveClassifier.buildClassifier(train);
            evaluation = new Evaluation(test, costSensitiveClassifier.getCostMatrix());
            evaluation.evaluateModel(costSensitiveClassifier, test);
            nPofB20 = AcumeInfo.getNPofB20(test, costSensitiveClassifier);
        } else {
            classifier.buildClassifier(train);
            evaluation = new Evaluation(test);
            evaluation.evaluateModel(classifier, test);
            nPofB20 = AcumeInfo.getNPofB20(test, classifier);
        }

        return evaluation;
    }

    /**
     * Restituisce un classificatore in base al tipo specificato.
     *
     * @param classif Il tipo di classificatore
     * @return L'oggetto Classifier corrispondente
     */
    private static Classifier getClassifier(ProfileML.Classifier classif) {
        switch (classif) {
            case NAIVE_BAYES:
                return new NaiveBayes();
            case IBK:
                return new IBk();
            default:
                return new RandomForest();
        }
    }
    /**
     * Crea una matrice dei costi.
     * @param falseNegativeWeigth Il peso dei falsi negativi.
     * @param falsePositiveWeigth Il peso dei falsi positivi.
     * @return La matrice dei costi.
     */
    public static CostMatrix getCostMatrix(double falseNegativeWeigth, double falsePositiveWeigth) {
        CostMatrix costMatrix = new CostMatrix(2);
        costMatrix.setCell(0, 0, 0.0);
        costMatrix.setCell(0, 1, falsePositiveWeigth);
        costMatrix.setCell(1, 0, falseNegativeWeigth);
        costMatrix.setCell(1, 1, 0.0);
        return costMatrix;
    }

    /**
     * Crea un file CSV con i risultati delle valutazioni.
     * @param evals La lista delle valutazioni.
     * @param numVers Il numero di versioni del dataset.
     * @param numClassif Il numero di classificatori.
     * @throws IOException Se si verifica un errore durante la scrittura del file.
     */
    public static void createCsv(List<EvaluationML> evals, int numVers, int numClassif) throws IOException {
        String outname = PROJ + Parameters.DATASET_ANALISYS; //Name of CSV for output
        // Utilizza try-with-resources per garantire la chiusura automatica del FileWriter
        try (FileWriter fileWriter = new FileWriter(outname)) {
            fileWriter.append("Dataset,#TrainRelease,Classifier,FeatSel,Sampling,CostSens,TP,FP,FN,TN,Precision,Recall,AUC,Kappa,NPofB20\n");
            int trainRelease = 1;
            int count = 0;
            String classifier;
            String fs;
            String smp;
            String cs;
            int tp;
            int fp;
            int fn;
            int tn;
            Evaluation e = null;
            for (EvaluationML eval : evals) {
                if (count >= numClassif) {
                    if (trainRelease >= numVers - 1) trainRelease = 1;
                    else trainRelease++;
                    count = 0;
                }
                e = eval.getEval();
                String prec = String.format(Locale.US, "%.3f", e.precision(1));
                String rec = String.format(Locale.US, "%.3f", e.recall(1));
                String aoc = String.format(Locale.US, "%.3f", e.areaUnderROC(1));
                String k = String.format(Locale.US, "%.3f", e.kappa());
                String nPofB20= eval.getnPofB20().length() > 5 ? eval.getnPofB20().substring(0, 5) : eval.getnPofB20();
                classifier = eval.getClassif().toString().toLowerCase().replace("_", " ");
                fs = eval.getFs().toString().toLowerCase().replace("_", " ");
                smp = eval.getSmp().toString().toLowerCase().replace("_", " ");
                cs = eval.getCs().toString().toLowerCase().replace("_", " ");
                double[][] confMatr = e.confusionMatrix();
                tp = (int) confMatr[0][0];
                fp = (int) confMatr[0][1];
                fn = (int) confMatr[1][0];
                tn = (int) confMatr[1][1];

                String line = String.format("%s,%d,%s,%s,%s,%s,%d,%d,%d,%d,%s,%s,%s,%s,%s%n", Parameters.PROJECT1,
                        trainRelease, classifier, fs, smp, cs, tp, fp, fn, tn, prec, rec, aoc, k,nPofB20);
                fileWriter.append(line);
                count++;
            }

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in analysis.csv writer", e);
            e.printStackTrace();
        }
    }


}