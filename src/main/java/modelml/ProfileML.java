package modelml;

/**
 * Questa classe contiene enumerazioni per classificatori, metodi di selezione delle caratteristiche,
 * metodi di campionamento e sensibilità ai costi. Non deve essere istanziata.
 */
public abstract class ProfileML {

    private ProfileML() {
        throw new UnsupportedOperationException("ProfileML class cannot be instantiated");
    }

    /**
     * Enumerazione per i diversi tipi di classificatori.
     */
    public enum Classifier {
        RANDOM_FOREST,
        NAIVE_BAYES,
        IBK
    }

    /**
     * Enumerazione per i diversi metodi di selezione delle caratteristiche (Feature Selection).
     */
    public enum FeatureSelection {
        NO_SELECTION,
        BEST_FIRST
    }

    /**
     * Enumerazione per i diversi metodi di campionamento.
     */
    public enum SamplingMethod {
        NO_SAMPLING,
        OVERSAMPLING,
        UNDERSAMPLING,
        SMOTE
    }

    /**
     * Enumerazione per i diversi approcci alla sensibilità ai costi.
     */
    public enum CostSensitivity {
        NO_COST_SENSITIVE,
        SENSITIVE_THRESHOLD,
        SENSITIVE_LEARNING
    }
}
