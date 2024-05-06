package milestone1;

import Utils.Parameters;
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
import java.lang.reflect.Parameter;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Controller {
    private static final Logger LOGGER = Logger.getLogger("analyzer");
    private RetrieveCommits RC;
    private RetrieveTicketID RT;
    private GetMetrics GM;
    private GetBuggyClass GBC;
    private Git git;
    String projName;
    String path;

    public Controller(String projName) {
        this.projName = projName;
        RC = new RetrieveCommits();
        RT = new RetrieveTicketID(projName);
        GM = new GetMetrics();
        GBC = new GetBuggyClass();
    }

    public void setProject() {
        try {
            String folderName = projName.toLowerCase();
            path = System.getProperty("user.home");
            File dir = new File(path, folderName);
            dir.mkdir();
            // If is not empty, then refresh the directory
            if (dir.list().length == 0) {
//				LOGGER.log(Level.INFO , String.format("clone repository %s inside %s", Parameters.toUrl(projName), path));
                Git.cloneRepository().setURI(Parameters.toUrl(projName)).setDirectory(dir).call();
                git = Git.open(dir);
//				LOGGER.log(Level.INFO , "checkout completed");
                // Otherwise checkout the project
            } else {
                git = Git.open(dir);
                git.pull();
                git.checkout();
//				LOGGER.log(Level.INFO , String.format("checkout completed %s/%s", path, projName));
            }
        } catch (GitAPIException | IOException e) {
            LOGGER.log(Level.SEVERE, "Error in instantiation phase", e);
        }
    }

    public static void main(String[] args) throws IOException, GitAPIException, JSONException, ParseException {
        List<Commit> commits = null;
        List<Ticket> tickets = null;
        List<Version> versions = null;
        List<Instance> instances = null;
        Map<String, List<Integer>> mapInst = new HashMap<>();

        String projName = Parameters.PROJECT1;

        Controller controller = new Controller(projName);
        controller.setProject();

        // PRINT INFO PROJECT
        String output = String.format("Dataset Creation: %s%n", projName);
        LOGGER.info(output);

        // CONSTRUCT DB VERSIONS
//		RetrieveVersions.GetRealeaseInfo(projName);

        // GET VERSIONS
        versions = RetrieveVersions.GetVersions(projName + "VersionInfo.csv");
        int size = versions.size();
        String msg = "Versions: " + size;
        if (size != -1) LOGGER.info(msg);

        // GET TICKETS
        tickets = controller.getTickets(versions);
        size = tickets.size();
        msg = "Buggy Tickets (clean): " + size;
        LOGGER.info(msg);

        // GET COMMITS
        commits = controller.getCommits(tickets, versions);
        size = commits.size();
        msg = "Commits: " + size;
        LOGGER.info(msg);

        // GENERATE INSTANCES OF DATABASE
        instances = controller.getInstances(commits, versions, mapInst);
        size = instances.size();
        msg = "Instances: " + size;
        LOGGER.info(msg);

        // SET BUGGINESS FOR EVERY INSTANCE
        controller.setBugginess(instances, commits, mapInst);

        // GENERATE DATABASE
        controller.fillDataset(instances);
    }

    public List<Ticket> getTickets(List<Version> versions) throws JSONException, IOException, ParseException {
        return RT.getTickets(versions);
    }

    public List<Commit> getCommits(List<Ticket> tickets, List<Version> versions) throws JSONException, IOException, GitAPIException {
        return RC.getCommits(git, tickets, versions);
    }

    public List<Instance> getInstances(List<Commit> commits, List<Version> versions, Map<String, List<Integer>> mapInst) throws IOException {
        return GM.getInstances(git, commits, versions, mapInst);
    }

    public void setBugginess(List<Instance> instances, List<Commit> commits, Map<String, List<Integer>> mapInst) {
        GBC.setBugginess(instances, commits, mapInst);
    }

    public void fillDataset(List<Instance> instances) throws IOException {
        try (FileWriter fileWriter = new FileWriter(projName + Parameters.DATASET)) {
            fileWriter.append("Version,Name,Size,LocTouched,MaxLocAdded,Churn,MaxChurn,AvgChurn,NR,NFix,Age,maxLocTouched,Bugginess");
            fileWriter.append("\n");

            for (Instance instance : instances) {
                int bugginess = instance.isBugginess() ? 1 : 0;
                String line = String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s%n", instance.getVersion().getName(),
                        instance.getName(), Integer.toString(instance.getSize()), Integer.toString(instance.getLocTouched()),
                        Integer.toString(instance.getMaxLocAdded()), Integer.toString(instance.getChurn()), Integer.toString(instance.getMaxChurn()),
                        Integer.toString(instance.getAvgChurn()), Integer.toString(instance.getNR()), Integer.toString(instance.getNFix()));
                fileWriter.append(line);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in dataset.csv writer", e);
            e.printStackTrace();
        }
    }
}



