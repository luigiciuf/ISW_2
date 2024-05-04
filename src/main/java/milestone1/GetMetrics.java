package milestone1;

import model.Commit;
import model.Instance;
import model.Version;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.revwalk.RevCommit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static jdk.internal.org.jline.utils.DiffHelper.diff;

public class GetMetrics {
    private Git git;
    private ArrayList<Instance> instances;

    private GetMetrics(){
        this.instances=new ArrayList<>();

    }


    public List<Instance> getInstances(Git git, List<Commit> commits, List<Version> versions, Map<String, List<Integer>> mapInst) throws IOException{
        this.git=git;
        ArrayList<Instance> temp= new ArrayList<>();
        Map<String,Integer> mapTemp= new HashMap<>();
        Instance instance= null;
        RevCommit prevCommit= null;
        Version version = versions.get(0);

        for (Commit commit: commits){
            //recuperiamo autore del commit
            String author = commit.getAuthor();
            //verifichiamo che il commit sia un commit di fix
            boolean fixCommit= !commit.getBuggyTickets().isEmpty();
            if(!version.getName().equals(commit.getVersion().getName())){
                updateIstances(mapInst,temp,mapTemp);
                version=commit.getVersion();
                for(Instance t : temp){
                    t.setVersion(version);
                }
            }
            manageFiles(commit,prevCommit,temp,mapTemp,version,author,fixCommit);
            int nCommTogheter = commit.getClassesTouched().size();
            for (String file: commit.getClassesTouched()){
                instance= temp.get(mapTemp.get(file));
            }
            prevCommit=commit.getRev();
        }
        updateInstances(mapInst,temp,mapTemp);
        return instances;
    }

    private void manageFiles(Commit commit, RevCommit prevCommit, ArrayList<Instance> temp, Map<String, Integer> mapTemp, Version version, String author, boolean fixCommit) throws IOException{
        Instance inst =null;
        List<DiffEntry> listDe = diff(commit.getRev(), prevCommit);
        for(String file : commit.getClasses()){
            List<Edit> edits= getEdits
        }

    }

    private   diff(RevCommit rev, RevCommit prevCommit) {
    }


}
