package milestone1;

import model.Commit;
import model.Ticket;
import model.Version;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.revwalk.RevCommit;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class RetrieveCommits {
    private Git git;
    private ArrayList<Commit> commits;
    public RetrieveCommits(){
        this.commits=new ArrayList<Commit>();
    }

    public List<Commit> getCommits(Git git, List<Ticket> tickets, List<Version> versions) throws IOException, JSONException, GitAPIException {
    this.git=git;
    Iterable<RevCommit> log= git.log().call();
    for(Iterator<RevCommit> iterator= log.iterator(); iterator.hasNext();){
        RevCommit rev= iterator.next();
        // prendiamo l'autore
        PersonIdent pi= rev.getAuthorIdent();
        String author= pi.getName();
        Date creationTime= pi.getWhen();

        //prendiamo la versione
        Version version= RetrieveVersions.FindVersion(creationTime, versions);
        // escludo i comit che non appartengono a nessuna versione, e continuo
        if(version == null) continue;

        Commit commit= new Commit(rev,author, version, creationTime);



    }
    }


}
