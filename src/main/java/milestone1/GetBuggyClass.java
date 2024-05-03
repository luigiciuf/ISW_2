package milestone1;

import model.Commit;
import model.Instance;
import model.Ticket;

import java.util.List;
import java.util.Map;

public class GetBuggyClass{
    /**
     * Imposta il flag di bugginess per le istanze che corrispondono ai commit buggy.
     * @param instances Una lista di istanze da controllare per bugginess.
     * @param commits La lista di commit da analizzare.
     * @param mapInst Una mappa che collega i nomi dei file agli indici delle istanze.
     */
    public void setBugginess(List<Instance> instances, List<Commit> commits, Map<String, List<Integer>> mapInst) {
        // Itera su tutti i commit per trovare le classi toccate e i ticket buggy
        for (Commit commit : commits) {
            List<String> classes = commit.getClassesTouched();
            for (Ticket ticket : commit.getBuggyTickets()) {
                // Imposta il bugginess delle classi toccate
                setTicketBuggyClass(instances, ticket, classes, mapInst);
            }
        }
    }
    /**
     * Imposta le istanze corrispondenti come buggy in base ai ticket.
     * @param instances La lista di tutte le istanze.
     * @param ticket Il ticket con informazioni sul bug.
     * @param classes Le classi toccate dal commit.
     * @param mapInst Mappa che collega i nomi dei file agli indici delle istanze.
     */
    private void setTicketBuggyClass(List<Instance> instances, Ticket ticket, List<String> classes, Map<String, List<Integer>> mapInst){
        for (String file : classes) {
            // Ottiene gli indici delle istanze corrispondenti
            List<Integer> idxs = mapInst.get(file);
            if (idxs == null) {
                continue; // Se non ci sono indici, passa al file successivo
            }
            for(Integer idx : idxs) {
                // Ottiene l'istanza corrispondente
                Instance instance = instances.get(idx);
                // Controlla se l'istanza rientra nell'intervallo del ticket (tra Av e Fv)
                boolean ret = instance.insideAV(ticket.getAv(), ticket.getFv());
                if (ret) instance.setBugginess(true);
            }
        }
    }

}

