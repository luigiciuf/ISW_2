package milestone1;

import model.Ticket;
import model.Version;
import org.json.JSONException;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
public class RetrieveTicketID {
    private String projname;
    private List<Ticket> tickets;

    int discarded;

    public RetrieveTicketID(String projname){
        this.projname=projname;;
        this.tickets=new ArrayList<Ticket>();
        discarded=0;
    }
    public  List<Ticket> getTickets(List<Version> allVersions) throws IOException, JSONException, ParseException{
        getJiraInfo(allVersions);

    }

    private void getJiraInfo(List<Version> allVersions) throws JSONException,IOException,ParseException {
        Integer j=0;
        Integer i=0;
        int total =1;
        do {
            j=1+1000;

        }

    }

}
