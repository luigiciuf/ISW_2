package modelML;

import weka.classifiers.Evaluation; // Importa la classe Evaluation dal pacchetto Weka

public class Evalutation {
    // Variabili di istanza per immagazzinare la valutazione e le configurazioni del modello
    Evaluation eval; // Oggetto Evaluation per la valutazione del modello
    ProfileML.FS fs; // Metodo di selezione delle caratteristiche (Feature Selection)
    ProfileML.SMP smp; // Metodo di campionamento (Sampling Method)
    ProfileML.CS cs; // Sensibilità ai costi (Cost Sensitivity)
    ProfileML.CLASSIF classif; // Tipo di classificatore (Classifier)

    // Costruttore della classe che inizializza tutte le variabili di istanza
    /**
     * Costruttore per creare un oggetto Evalutation con le configurazioni specificate.
     * @param eval Oggetto Evaluation per la valutazione del modello.
     * @param fs Metodo di selezione delle caratteristiche (Feature Selection).
     * @param smp Metodo di campionamento (Sampling Method).
     * @param cs Sensibilità ai costi (Cost Sensitivity).
     * @param classif Tipo di classificatore (Classifier).
     */
    public Evalutation(Evaluation eval, ProfileML.FS fs, ProfileML.SMP smp, ProfileML.CS cs, ProfileML.CLASSIF classif){
        super(); // Chiama il costruttore della superclasse (Object)
        this.eval = eval; // Assegna il parametro eval alla variabile di istanza eval
        this.fs = fs; // Assegna il parametro fs alla variabile di istanza fs
        this.smp = smp; // Assegna il parametro smp alla variabile di istanza smp
        this.cs = cs; // Assegna il parametro cs alla variabile di istanza cs
        this.classif = classif; // Assegna il parametro classif alla variabile di istanza classif
    }

    // Metodi getter per accedere alle variabili di istanza
    /**
     * Restituisce l'oggetto Evaluation.
     * @return eval Oggetto Evaluation per la valutazione del modello.
     */
    public Evaluation getEval() {
        return eval; // Restituisce l'oggetto Evaluation
    }

    /**
     * Restituisce il metodo di selezione delle caratteristiche.
     * @return fs Metodo di selezione delle caratteristiche (Feature Selection).
     */
    public ProfileML.FS getFs() {
        return fs; // Restituisce il metodo di selezione delle caratteristiche
    }

    /**
     * Restituisce il metodo di campionamento.
     * @return smp Metodo di campionamento (Sampling Method).
     */
    public ProfileML.SMP getSmp() {
        return smp; // Restituisce il metodo di campionamento
    }

    /**
     * Restituisce la sensibilità ai costi.
     * @return cs Sensibilità ai costi (Cost Sensitivity).
     */
    public ProfileML.CS getCs() {
        return cs; // Restituisce la sensibilità ai costi
    }

    /**
     * Restituisce il tipo di classificatore.
     * @return classif Tipo di classificatore (Classifier).
     */
    public ProfileML.CLASSIF getClassif() {
        return classif; // Restituisce il tipo di classificatore
    }
}
