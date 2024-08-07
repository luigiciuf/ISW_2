package utils;

/**
 * Classe che contiene parametri e costanti utilizzati in altre parti del progetto.
 * Questa classe fornisce valori predefiniti per i nomi dei progetti, tipi di file,
 * nomi di file di dataset, e URL per repository Git.
 * La classe non deve essere istanziata; è progettata per fornire solo costanti.
 */
public class Parameters {
    private Parameters () {
        throw new IllegalStateException("Parameters class must not be instantiated");
    }
    public static final String PROJECT1 = "BOOKKEEPER";
    public static final String PROJECT2 = "ZOOKEEPER";
    public static final String CLASSTYPE = ".java";
    public static final String DATASET = "dataset.csv";
    public static final String DATASET_ANALISYS = "analisys.csv";
    public static final String ACUME_DRECTORY="C:/Users/luigi/IdeaProjects/ISW_2/acume/";

    // Metodo per ottenere il percorso di base personalizzabile
    public static String getBasePath() {
        // Ottieni il percorso dal sistema o usa un valore di default
        return System.getProperty("custom.base.path", "C:/Users/luigi/IdeaProjects/ISW_2/");
    }


    public static String toUrl(String project) {
        return String.format("https://github.com/apache/%s.git", project);
    }
}
