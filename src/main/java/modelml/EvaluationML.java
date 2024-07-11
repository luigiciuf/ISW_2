package modelml;
import weka.classifiers.Evaluation;
import modelml.ProfileML.CostSensitivity;
import modelml.ProfileML.SamplingMethod;
import modelml.ProfileML.FeatureSelection;
import modelml.ProfileML.Classifier;
public class EvaluationML {
    Evaluation eval;
    FeatureSelection fs;
    SamplingMethod smp;
    CostSensitivity cs;
    Classifier classif;
    private String nPofB20;
    /**
     * Costruttore per creare un oggetto Evalutation con le configurazioni specificate.
     * @param eval Oggetto Evaluation per la valutazione del modello.
     * @param fs Metodo di selezione delle caratteristiche (Feature Selection).
     * @param smp Metodo di campionamento (Sampling Method).
     * @param cs Sensibilità ai costi (Cost Sensitivity).
     * @param classif Tipo di classificatore (Classifier).
     */
    public EvaluationML(Evaluation eval,FeatureSelection fs, SamplingMethod smp, CostSensitivity cs, Classifier classif, String nPofB20){
        super();
        this.eval = eval;
        this.fs = fs;
        this.smp = smp;
        this.cs = cs;
        this.classif = classif;
        this.nPofB20=nPofB20;
    }

    /**
     * Restituisce l'oggetto Evaluation.
     * @return eval Oggetto Evaluation per la valutazione del modello.
     */
    public Evaluation getEval() {
        return eval;
    }

    /**
     * Restituisce il metodo di selezione delle caratteristiche.
     * @return fs Metodo di selezione delle caratteristiche (Feature Selection).
     */
    public FeatureSelection getFs() {
        return fs;
    }

    /**
     * Restituisce il metodo di campionamento.
     * @return smp Metodo di campionamento (Sampling Method).
     */
    public SamplingMethod getSmp() {
        return smp;
    }

    /**
     * Restituisce la sensibilità ai costi.
     * @return cs Sensibilità ai costi (Cost Sensitivity).
     */
    public CostSensitivity getCs() {
        return cs;
    }

    /**
     * Restituisce il tipo di classificatore.
     * @return classif Tipo di classificatore (Classifier).
     */
    public Classifier getClassif() {
        return classif;
    }

    public String getnPofB20() {
        return nPofB20;
    }

    public void setnPofB20(String nPofB20) {
        this.nPofB20 = nPofB20;
    }

}
