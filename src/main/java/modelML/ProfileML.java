package modelML;

public class ProfileML {
    // Costruttore privato per impedire l'istanza della classe
    /**
     * Costruttore privato che lancia un'eccezione per impedire l'istanza della classe.
     * Questa classe è progettata per non essere istanziata.
     */
    private ProfileML() {
        throw new IllegalStateException("ProfileML class must not be instantiated");
    }

    // Enumerazione per i tipi di classificatori
    /**
     * Enumerazione per i diversi tipi di classificatori.
     */
    public enum CLASSIF {
        RANDOM_FOREST, // Classificatore Random Forest
        NAIVE_BAYES,   // Classificatore Naive Bayes
        IBK;           // Classificatore IBK (K-nearest neighbors)
    }

    // Enumerazione per i metodi di selezione delle caratteristiche
    /**
     * Enumerazione per i diversi metodi di selezione delle caratteristiche (Feature Selection).
     */
    public enum FS {
        NO_SELECTION, // Nessuna selezione delle caratteristiche
        BEST_FIRST;   // Selezione delle migliori caratteristiche (Best First)
    }

    // Enumerazione per i metodi di campionamento
    /**
     * Enumerazione per i diversi metodi di campionamento.
     */
    public enum SMP {
        NO_SAMPLING,    // Nessun campionamento
        OVERSAMPLING,   // Aumento del campione (Oversampling)
        UNDERSAMPLING,  // Riduzione del campione (Undersampling)
        SMOTE;          // Tecnica SMOTE (Synthetic Minority Over-sampling Technique)
    }

    // Enumerazione per la sensibilità ai costi
    /**
     * Enumerazione per i diversi approcci alla sensibilità ai costi.
     */
    public enum CS {
        NO_COST_SENSITIVE,   // Nessuna sensibilità ai costi
        SENSITIVE_THRESHOLD, // Soglia sensibile ai costi
        SENSITIVE_LEARNING;  // Apprendimento sensibile ai costi
    }
}
