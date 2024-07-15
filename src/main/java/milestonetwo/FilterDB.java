package milestonetwo;

import modelml.ProfileML;
import weka.attributeSelection.AttributeSelection;
import weka.attributeSelection.BestFirst;
import weka.attributeSelection.CfsSubsetEval;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;
import weka.filters.supervised.instance.Resample;
import weka.filters.supervised.instance.SMOTE;
import weka.filters.supervised.instance.SpreadSubsample;
import weka.filters.unsupervised.attribute.Remove;
import weka.filters.unsupervised.instance.RemoveWithValues;
import weka.filters.Filter;
import java.text.DecimalFormat;

public class FilterDB {
    public FilterDB() {
        super();
    }

    Instances train;
    Instances test;
    public void featureSelection(ProfileML.FeatureSelection fs) throws Exception {
        if (fs.equals(ProfileML.FeatureSelection.BEST_FIRST)) {
            AttributeSelection attrSelection = new AttributeSelection();
            attrSelection.setEvaluator(new CfsSubsetEval());
            attrSelection.setSearch(new BestFirst());
            attrSelection.SelectAttributes(train);

            Remove removeFilter = new Remove();
            removeFilter.setAttributeIndicesArray(attrSelection.selectedAttributes());
            removeFilter.setInvertSelection(true);
            removeFilter.setInputFormat(train);

            train = Filter.useFilter(train, removeFilter);
            test = Filter.useFilter(test, removeFilter);
        }
    }
    public void sampling(ProfileML.SamplingMethod smp) throws Exception {
        switch (smp) {
            case OVERSAMPLING:
                Resample resample = new Resample();
                resample.setInputFormat(train);
                DecimalFormat df = new DecimalFormat("#.##");
                resample.setOptions(Utils.splitOptions(String.format("-B 1.0 -Z %s", df.format(computerMajorityClassPercentage()))));
                train = Filter.useFilter(train, resample);
                break;

            case UNDERSAMPLING:
                SpreadSubsample underSampling = new SpreadSubsample();
                underSampling.setInputFormat(train);
                underSampling.setOptions(Utils.splitOptions("-M 1.0"));
                train = Filter.useFilter(train, underSampling);
                break;

            case SMOTE:
                SMOTE smote = new SMOTE();
                smote.setInputFormat(train);
                train = Filter.useFilter(train, smote);
                break;
        }
    }

    private double computerMajorityClassPercentage() {
        int buggyClasses = 0;
        Instances dataset = new Instances(train);

        for (Instance recordDataset: dataset) {
            String buggy = recordDataset.stringValue(recordDataset.numAttributes()-1);
            if (buggy.equals("1"))
                buggyClasses++;
        }

        double percentage = (100 * buggyClasses/dataset.size());
        if (percentage >= 50)
            return percentage;
        else
            return 100-percentage;
    }

    public Instances getTrainSet(Instances dataset, int trainingRelease, int releases) throws Exception{
        RemoveWithValues filter = new RemoveWithValues();
        int range = releases-trainingRelease;
        int[] arr = new int[range];
        for(int i = 1; i < range+1; i++) {
            arr[range-i] = releases-i;
        }
        filter.setAttributeIndex("1");
        filter.setNominalIndicesArr(arr);
        filter.setInputFormat(dataset);
        return Filter.useFilter(dataset, filter);
    }

    public Instances getTestSet(Instances dataset, int trainingRelease) throws Exception{
        String options =  String.format("-C 1 -L %d -V", trainingRelease+1);
        RemoveWithValues filter = new RemoveWithValues();
        filter.setOptions(Utils.splitOptions(options));
        filter.setInputFormat(dataset);
        return Filter.useFilter(dataset, filter);
    }
}