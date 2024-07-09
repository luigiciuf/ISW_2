package milestoneone;

import model.Commit;
import model.Ticket;
import model.Version;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.json.JSONException;

import java.io.IOException;
import java.util.*;

public class RetrieveCommits {
    private Git git;
    private ArrayList<Commit> commits;
    public RetrieveCommits(){
        this.commits=new ArrayList<>();
    }

    /**
     * Estrae i commit dal repository Git e associa informazioni sui ticket e sulle versioni.
     * @param git Il repository Git da cui estrarre i commit
     * @param tickets La lista dei ticket correlati
     * @param versions La lista delle versioni del progetto
     * @return Una lista di oggetti Commit con informazioni associate
     * @throws IOException, JSONException, GitAPIException Eccezioni relative a I/O, JSON e API Git
     */

    public List<Commit> getCommits(Git git, List<Ticket> tickets, List<Version> versions) throws IOException, JSONException, GitAPIException {
        this.git=git;
        Iterable<RevCommit> log= git.log().call();
        for(Iterator<RevCommit> iterator= log.iterator(); iterator.hasNext();){
            RevCommit rev= iterator.next();
            PersonIdent pi= rev.getAuthorIdent();
            String author= pi.getName();
            Date creationTime= pi.getWhen();
            //prendiamo la versione
            Version version= RetrieveVersions.FindVersion(creationTime, versions);
            // escludo i commit che non appartengono a nessuna versione, e continuo
            if(version == null) continue;

            Commit commit= new Commit(rev,author, version, creationTime);
            List<String> classes = getFilesCommit(rev);
            List<Ticket> buggyTickets = getBuggyTickets(rev,tickets);

            commit.setClasses(classes);
            commit.setBuggyTickets(buggyTickets);
            commits.add(commit);

        }
        commits.sort(Comparator.comparing(Commit::getDate));
        return commits;
    }




    /**
     * Restituisce i ticket che contengono il riferimento al commit nel loro messaggio.
     * @param commit Il commit corrente
     * @param tickets La lista di ticket da cui cercare
     * @return Una lista di ticket correlati al commit
     */
    private List<Ticket> getBuggyTickets(RevCommit commit, List<Ticket> tickets){
        List<Ticket> buggyTickets = new ArrayList<>();
        String msg = commit.getFullMessage();
        for(Ticket ticket : tickets) {
            if(msg.contains(ticket.getKey())) buggyTickets.add(ticket);
        }
        return buggyTickets;
    }

    /**
     * Restituisce l'elenco dei file modificati in un commit.
     * @param commit Il commit corrente
     * @return Una lista di nomi dei file modificati
     * @throws IOException Se c'è un problema con l'accesso al repository Git
     */

    private List<String> getFilesCommit(RevCommit commit) throws  IOException {
        List<String> affectedFiles = new ArrayList<>();
        ObjectId treeId = commit.getTree().getId();
        TreeWalk treeWalk = new TreeWalk(git.getRepository());
        treeWalk.reset(treeId);
        while (treeWalk.next()) {
            if (treeWalk.isSubtree()) {
                treeWalk.enterSubtree();
            } else {
                if (treeWalk.getPathString().endsWith(".java")) {
                    String fileToAdd = treeWalk.getPathString();
                    affectedFiles.add(fileToAdd);
                }
            }
        }
        return affectedFiles;
    }


}
