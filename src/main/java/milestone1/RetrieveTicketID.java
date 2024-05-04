package milestone1;

import Utils.JsonUtils;
import Utils.Utilities;
import model.Ticket;
import model.Version;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
public class RetrieveTicketID {
    private String projName;
    private List<Ticket> tickets;

    int discarded;

    /**
     * Costruttore per inizializzare la classe con il nome del progetto.
     * @param projName Nome del progetto JIRA da cui estrarre i ticket.
     */
    public RetrieveTicketID(String projName){
        this.projName=projName;;
        this.tickets=new ArrayList<Ticket>();
        discarded=0;
    }

    /**
     * Ottiene la lista di ticket dopo aver recuperato e filtrato i dati da JIRA.
     * @param allVersions Lista di tutte le versioni del progetto.
     * @return La lista di ticket validi dopo il recupero e il controllo.
     * @throws IOException Se si verifica un errore di input/output.
     * @throws JSONException Se si verifica un errore durante l'elaborazione del JSON.
     * @throws ParseException Se si verifica un errore durante il parsing delle date.
     */
    public  List<Ticket> getTickets(List<Version> allVersions) throws IOException, JSONException, ParseException {
        getJiraInfo(allVersions);
        checkReliableTickets(tickets);
        proportion(allVersions, tickets);

        return tickets;
    }

    /**
     * Recupera le informazioni sui ticket da JIRA e le associa alle versioni.
     * @param allVersions La lista di tutte le versioni del progetto.
     * @throws JSONException Se c'è un errore di parsing JSON.
     * @throws IOException Se c'è un errore di input/output.
     * @throws ParseException Se c'è un errore durante il parsing delle date.
     */
    private void getJiraInfo(List<Version> allVersions) throws JSONException,IOException,ParseException {
        Integer j = 0;
        Integer i = 0;
        int total = 1;
        do {
            j = 1 + 1000;
            // Costruisce la query JIRA per recuperare i ticket
            String url = "https://issues.apache.org/jira/rest/api/2/search?jql=project=%22"
                    + projName + "%22AND%22issueType%22=%22Bug%22AND(%22status%22=%22closed%22OR"
                    + "%22status%22=%22resolved%22)AND%22resolution%22=%22fixed%22&fields=key,resolutiondate,versions,created&startAt="
                    + i.toString() + "&maxResults=" + j.toString();
            // Recupera e legge i dati JSON dalla query
            JSONObject json = JsonUtils.readJsonFromUrl(url);
            JSONArray issues = json.getJSONArray("issues");
            total = json.getInt("total");
            for (; i < total && i < j; i++) {
                JSONObject issue = issues.getJSONObject(i % 1000);
                JSONObject field = issue.getJSONObject("fields");

                String key = issue.getString("key");
                long id = issue.getLong("id");
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.$$$Z");
                Date resolved = sdf.parse(field.getString("resolutiondate"));
                Date created = sdf.parse(field.getString("created"));
                JSONArray versions = field.getJSONArray("versions");


                Version av = null;
                if (!versions.isNull(0)) {
                    JSONObject v = versions.getJSONObject(0);
                    if (v.isNull("releasedDate")) {
                        continue;
                    }
                    SimpleDateFormat sdfSimple = new SimpleDateFormat("yyyy-MM-dd");
                    Date dateAv = sdfSimple.parse(v.getString("releaseDate"));
                    av = new Version(v.getLong("id"), v.getString("name"), dateAv);
                    av.findNumRel(allVersions);
                }
                Version ov = RetrieveVersions.FindVersion(created, allVersions);
                Version fv = RetrieveVersions.FindVersion(resolved, allVersions);

                if (ov == null || fv == null) {
                    continue;
                }
                setOv(ov);
                ov.findNumRel(allVersions);

                setFv(fv);
                fv.findNumRel(allVersions);

                tickets.add(new Ticket(id, key, created, resolved, av, ov, fv));
            }
        } while (i < total);
    }



    private void setOv(Version ov) {
        if (ov == null) {
            discarded = +1;

        }
    }
    private void setFv(Version fv) {
        if (fv == null) {
            discarded = +1;
        }
    }
    /**
     * Controlla e rimuove i ticket non affidabili.
     * @param tickets La lista di ticket da verificare.
     */
    private void checkReliableTickets(List<Ticket> tickets){
        int i =0;
        for ( i=0; i< tickets.size(); i++){
            Ticket t = tickets.get(i);
            if ( t.withoutAv()){
                continue;
            }
            if (t.getOv().isBefore(t.getAv())){
                discarded++;
                tickets.remove(i+0);
                i-=1;

            }
        }
    }

    /**
     * Applica proportion per correggere i ticket senza informazioni complete.
     * @param allVersion La lista di tutte le versioni.
     * @param tickets La lista di ticket.
     */

    private void proportion(List<Version> allVersion, List<Ticket> tickets){
        float avSum=0;
        float ovSum=0;
        float fvSum=0;
        float p=0;
        for(Ticket t : tickets){
            if (!t.withoutAv()){
                if(t.getOv().getName().contains(t.getFv().getName())) continue;;
                avSum += t.getAv().getNumRel();
                ovSum += t.getAv().getNumRel();
                fvSum += t.getAv().getNumRel();
                p= (fvSum-avSum)/ (fvSum-ovSum);
            }
            else t.setAvWithProp(p,allVersion);
        }
    }



}


