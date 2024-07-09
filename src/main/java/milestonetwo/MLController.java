package milestonetwo;


import utils.Parameters;
import modelML.EvaluationML;
import weka.classifiers.Classifier;
import weka.classifiers.CostMatrix;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.lazy.IBk;
import weka.classifiers.meta.CostSensitiveClassifier;
import weka.classifiers.trees.RandomForest;
import weka.core.converters.ConverterUtils.DataSource;
import modelML.ProfileML;

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
    public static final String proj = Parameters.PROJECT2;
    private static final Logger LOGGER = Logger.getLogger("Analyzer");
    private static String nPofB20 = null;


    public static void main(String[] args) throws Exception{
        //load dataset
        String projPath = System.getProperty("user.dir");
        String datasetPath = projPath + "/" + proj + "dataset.arff";

        System.out.println("Loading dataset from: " + datasetPath);

        DataSource source = new DataSource(datasetPath);
        Instances dataset = source.getDataSet();
        dataset.deleteStringAttributes();
        List<EvaluationML> evals = new ArrayList<>();
        int numAttr = dataset.numAttributes();
        int numVers = dataset.attribute(0).numValues();
        FilterDB filter = new FilterDB();
        Evaluation eval;

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
                        filter.featureSelection(fs);		// Apply feature selection
                        filter.sampling(smp);				// Apply Sampling
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


    public static Evaluation run(Instances train, Instances test, ProfileML.Classifier  classif, ProfileML.CostSensitivity cs) throws Exception {
        Classifier classifier;
        if (classif.equals(ProfileML.Classifier.NAIVE_BAYES)) {
            classifier = new NaiveBayes();
        } else if (classif.equals(ProfileML.Classifier.IBK)) {
            classifier = new IBk();
        } else {
            classifier = new RandomForest();
        }

        Evaluation evaluation;
        CostSensitiveClassifier costSensitive = new CostSensitiveClassifier();
        CostMatrix costMatrix = getCostMatrix(10.0, 1.0);
        costSensitive.setClassifier(classifier);
        costSensitive.setCostMatrix(costMatrix);

        if (!cs.equals(ProfileML.CostSensitivity.NO_COST_SENSITIVE)) {
            costSensitive.setMinimizeExpectedCost(cs.equals(ProfileML.CostSensitivity.SENSITIVE_THRESHOLD));
            costSensitive.buildClassifier(train);
            evaluation = new Evaluation(test, costSensitive.getCostMatrix());
            evaluation.evaluateModel(costSensitive, test);
            nPofB20 = AcumeInfo.getNPofB20(test,costSensitive);
        } else {
            classifier.buildClassifier(train);
            evaluation = new Evaluation(test);
            evaluation.evaluateModel(classifier, test);
            nPofB20 = AcumeInfo.getNPofB20(test,classifier);
        }
        return evaluation;
    }

    public static CostMatrix getCostMatrix(double falseNegativeWeigth, double falsePositiveWeigth) {
        CostMatrix costMatrix = new CostMatrix(2);
        costMatrix.setCell(0, 0, 0.0);
        costMatrix.setCell(0, 1, falsePositiveWeigth);
        costMatrix.setCell(1, 0, falseNegativeWeigth);
        costMatrix.setCell(1, 1, 0.0);
        return costMatrix;
    }



    public static void createCsv(List<EvaluationML> evals, int numVers, int numClassif) throws IOException {
        String outname = proj + Parameters.DATASET_ANALISYS; //Name of CSV for output
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
                String NPofB20= eval.getnPofB20().length() > 5 ? eval.getnPofB20().substring(0, 5) : eval.getnPofB20();
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
                        trainRelease, classifier, fs, smp, cs, tp, fp, fn, tn, prec, rec, aoc, k,NPofB20);
                fileWriter.append(line);
                count++;
            }

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in analysis.csv writer", e);
            e.printStackTrace();
        }
    }


}