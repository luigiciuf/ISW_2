package milestoneone;

import utils.Parameters;
import model.Commit;
import model.Instance;
import model.Ticket;
import model.Version;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.json.JSONException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Controller {
    // Logger per registrare messaggi di log
    private static final Logger LOGGER = Logger.getLogger("analyzer");
    private RetrieveCommits retrieveCommits;
    private RetrieveTicketID retrieveTicketID;
    private GetMetrics getMetrics;
    private GetBuggyClass getBuggyClass;
    private Git git;
    String projName;
    String path;
    /**
     * Costruttore per il Controller.
     * @param projName Il nome del progetto per cui creare il controller.
     */
    public Controller(String projName) {
        this.projName = projName;
        retrieveCommits = new RetrieveCommits();
        retrieveTicketID = new RetrieveTicketID(projName);
        getMetrics = new GetMetrics();
        getBuggyClass = new GetBuggyClass();
    }
    /**
     * Configura il repository Git.
     * Clona il repository se la cartella è vuota, altrimenti apre il repository esistente.
     */
    public void setProject() {
        try {
            String folderName = projName.toLowerCase();
            path = System.getProperty("user.home");
            File dir = new File(path, folderName);
            dir.mkdir();
            // If is not empty, then refresh the directory
            if (dir.list().length == 0) {
                Git.cloneRepository().setURI(Parameters.toUrl(projName)).setDirectory(dir).call();
            } else {
                git = Git.open(dir);
                git.pull().call();
            }
        } catch (GitAPIException | IOException e) {
            LOGGER.log(Level.SEVERE, "Error in instantiation phase", e);
        }
    }
    /**
     * Metodo principale per eseguire il programma.
     * @param args Argomenti da riga di comando.
     * @throws IOException Se c'è un errore di I/O.
     * @throws GitAPIException Se c'è un errore con Git.
     * @throws JSONException Se c'è un errore con JSON.
     * @throws ParseException Se c'è un errore nel parsing delle date.
     */
    public static void main(String[] args) throws IOException, GitAPIException, JSONException, ParseException {
        List<Commit> commits = null;
        List<Ticket> tickets = null;
        List<Version> versions = null;
        List<Instance> instances = null;
        Map<String, List<Integer>> mapInst = new HashMap<>();
        // Inizializza il controller con il nome del progetto
        String projName = Parameters.PROJECT2;
        Controller controller = new Controller(projName);
        controller.setProject();
        String output = String.format("Dataset Creation: %s%n", projName);
        LOGGER.info(output);

        // Recupera informazioni sulle versioni
        RetrieveVersions.getrealeaseinfo(projName);
        versions = RetrieveVersions.getVersions(projName + "VersionInfo.csv");
        int size = versions.size();
        String msg = "Versions: " + size;
        if (size != -1) LOGGER.info(msg);

        // Recupera i ticket associati alle versioni
        tickets = controller.getTickets(versions);
        size = tickets.size();
        msg = "Buggy Tickets (clean): " + size;
        LOGGER.info(msg);

        // Ottieni i commit associati ai ticket e alle versioni
        commits = controller.getCommits(tickets, versions);
        size = commits.size();
        msg = "Commits: " + size;
        LOGGER.info(msg);

        // Genera istanze dalle metriche
        instances = controller.getInstances(commits, versions, mapInst);
        size = instances.size();
        msg = "Instances: " + size;
        LOGGER.info(msg);

        // Imposta il bugginess per le istanze
        controller.setBugginess(instances, commits, mapInst);

        // Genera il dataset a partire dalle istanze
        controller.fillDataset(instances);
    }
    /**
     * Ottieni i ticket associati a una lista di versioni.
     * @param versions La lista delle versioni del progetto.
     * @return Una lista di ticket associati alle versioni.
     * @throws JSONException Se c'è un errore con JSON.
     * @throws IOException Se c'è un errore di I/O.
     * @throws ParseException Se c'è un errore nel parsing delle date.
     */
    public List<Ticket> getTickets(List<Version> versions) throws JSONException, IOException, ParseException {
        return retrieveTicketID.getTickets(versions);
    }
    /**
     * Ottieni i commit associati a una lista di ticket e versioni.
     * @param tickets La lista di ticket.
     * @param versions La lista di versioni.
     * @return Una lista di commit associati ai ticket e alle versioni.
     * @throws JSONException Se c'è un errore con JSON.
     * @throws IOException Se c'è un errore di I/O.
     * @throws GitAPIException Se c'è un errore con Git.
     */
    public List<Commit> getCommits(List<Ticket> tickets, List<Version> versions) throws JSONException, IOException, GitAPIException {
        return retrieveCommits.getCommits(git, tickets, versions);
    }
    /**
     * Ottieni le istanze a partire da commit e versioni.
     * @param commits La lista di commit.
     * @param versions La lista di versioni.
     * @param mapInst Mappa per tracciare le istanze.
     * @return Una lista di istanze generate dalle metriche.
     * @throws IOException Se c'è un errore di I/O.
     */
    public List<Instance> getInstances(List<Commit> commits, List<Version> versions, Map<String, List<Integer>> mapInst) throws IOException {
        return getMetrics.getInstances(git, commits, versions, mapInst);
    }
    /**
     * Imposta il bugginess per una lista di istanze.
     * @param instances La lista di istanze.
     * @param commits La lista di commit.
     * @param mapInst Mappa per tracciare le istanze.
     */
    public void setBugginess(List<Instance> instances, List<Commit> commits, Map<String, List<Integer>> mapInst) {
        getBuggyClass.setBugginess(instances, commits, mapInst);
    }
    /**
     * Genera un dataset a partire da una lista di istanze e lo scrive su un file CSV.
     * @param instances La lista di istanze da cui generare il dataset.
     */
    public void fillDataset(List<Instance> instances){
        try (FileWriter fileWriter = new FileWriter(projName + Parameters.DATASET)) {
            fileWriter.append("Version,Name,Size,LocTouched,MaxLocAdded,Churn,MaxChurn,AvgChurn,NR,NFix,Age,maxLocTouched,Bugginess");
            fileWriter.append("\n");

            for (Instance instance : instances) {
                int bugginess = instance.isBugginess() ? 1 : 0;
                String line = String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s%n", instance.getVersion().getName(),
                        instance.getName(), instance.getSize(), instance.getLoctouched(),
                        instance.getMaxLocAdded(), instance.getChurn(), instance.getMaxChurn(),
                        instance.getAvgChurn(), instance.getNr(), instance.getnFix(), instance.getAge(),instance.getMaxLocTouched(), bugginess);
                fileWriter.append(line);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in dataset.csv writer", e);
        }
    }
}



