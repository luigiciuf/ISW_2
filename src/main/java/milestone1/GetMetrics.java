package milestone1;

import Utils.Utilities;
import model.Commit;
import model.Instance;
import model.Version;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.diff.EditList;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.EmptyTreeIterator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class GetMetrics {
    private Git git;
    private ArrayList<Instance> instances;

    private GetMetrics(){
        this.instances=new ArrayList<>();

    }

    /**
     * Ottiene le istanze delle classi basate su commit e versioni.
     * @param git Il repository Git da analizzare.
     * @param commits La lista dei commit da analizzare.
     * @param versions La lista delle versioni nel progetto.
     * @param mapInst Una mappa che associa nomi di file a indici delle istanze.
     * @return Una lista di istanze delle classi.
     * @throws IOException Se si verifica un errore di I/O.
     */
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
            // Se la versione corrente cambia, aggiorna le istanze e reimposta la versione
            if(!version.getName().equals(commit.getVersion().getName())){
                updateInstances(mapInst,temp,mapTemp);
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
    /**
     * Gestisce i file toccati da un commit e aggiorna le istanze delle classi.
     * @param commit Il commit corrente.
     * @param prevCommit Il commit precedente.
     * @param temp La lista temporanea di istanze.
     * @param mapTemp Mappa temporanea per associazioni tra nomi di file e indici.
     * @param version La versione corrente.
     * @param author L'autore del commit.
     * @param fixCommit Se il commit è un fix commit.
     * @throws IOException Se si verifica un errore di I/O.
     */
    private void manageFiles(Commit commit, RevCommit prevCommit, ArrayList<Instance> temp, Map<String, Integer> mapTemp, Version version, String author, boolean fixCommit) throws IOException{
        Instance inst =null;
        List<DiffEntry> listDe = diff(commit.getRev(), prevCommit);
        for(String file : commit.getClasses()){
            List<Edit> edits= getEdits(listDe,file);
            if ( edits.isEmpty()) continue;
            commit.addTouchedClass(file);

            Integer isPresent= mapTemp.get(file);
            if(isPresent!= null) inst= temp.get(mapTemp.get(file));
            else inst= new Instance(file,version,commit.getDate());
            for(Edit edit : edits) {
                int added = edit.getEndB() - edit.getBeginB();
                int deleted = edit.getEndA() - edit.getBeginA();
                inst.updateInstanceLoc(added, deleted);
            }
            inst.updateInstanceMeta(fixCommit);
            if (isPresent == null) {
                temp.add(inst);
            }
            mapTemp.computeIfAbsent(file, k -> temp.size()-1);
        }
    }
    /**
     * Restituisce le differenze tra due commit.
     * @param newCommit Il commit corrente.
     * @param oldCommit Il commit precedente.
     * @return Una lista di differenze (DiffEntry).
     * @throws IOException Se si verifica un errore di I/O.
     */
    private List<DiffEntry> diff(RevCommit newCommit, RevCommit oldCommit) throws IOException {
        List<DiffEntry> lstDe = null;
        DiffFormatter df = new DiffFormatter(new ByteArrayOutputStream());
        df.setRepository(git.getRepository());
        if (oldCommit != null) {
            lstDe = df.scan(oldCommit.getTree(), newCommit.getTree());
        } else {           // (?) corretto ?
            ObjectReader reader = git.getRepository().newObjectReader();
            AbstractTreeIterator newTree = new CanonicalTreeParser(null, reader, newCommit.getTree());
            AbstractTreeIterator oldTree = new EmptyTreeIterator();
            lstDe = df.scan(oldTree, newTree);
        }
        return lstDe;
    }
    /**
     * Restituisce la lista di modifiche (Edit) per un file da una lista di differenze.
     * @param listDe La lista delle differenze.
     * @param file Il nome del file da cercare.
     * @return Una lista di modifiche (Edit).
     * @throws IOException Se si verifica un errore di I/O.
     */
    private List<Edit> getEdits(List<DiffEntry> listDe, String file) throws IOException{
        ArrayList<Edit> edits = new ArrayList<Edit>();
        DiffFormatter df = new DiffFormatter(null);
        df.setRepository(git.getRepository());
        for (DiffEntry diff : listDe) {
            if (diff.toString().contains(file)) {
                df.setDetectRenames(true);
                EditList editList = df.toFileHeader(diff).toEditList();

                for (Edit editElement : editList)
                    edits.add(editElement);
            } else {
                df.setDetectRenames(false);
            }
        }
        return edits;
    }
    /**
     * Aggiorna la mappa delle istanze e aggiunge le istanze dalla lista temporanea alla lista principale.
     * @param mapInst La mappa delle istanze principali.
     * @param temp La lista temporanea delle istanze.
     * @param mapTemp Mappa temporanea per associazioni tra nomi di file e indici.
     */
    private void updateInstances(Map<String, List<Integer>> mapInst, List<Instance> temp, Map<String, Integer> mapTemp) {
        int size = instances.size();
        for(int i = 0; i < temp.size(); i++) {
            String f = temp.get(i).getName();
            mapInst.computeIfAbsent(f, k -> new ArrayList<>()).add(mapTemp.get(f) + size);
        }

        instances.addAll(Utilities.clone(temp));
    }

    }

