package model;

import java.util.Date;
import java.util.List;

public class Version {
    private long id;
    private String name;

    private Date startDate;
    private Date endDate;

    private int numRel;


    public Version(long id, String name, Date endDate) {
        this.id = id;
        this.name = name;
        this.startDate = null;
        this.endDate = endDate;
        this.numRel = -1;
    }

    /**
     * Metodo usato per verificare se la data di questa versione è prima
     * o uguale a una data fornita come parametro
     * @param v
     * @return
     */

    public boolean isBefore(Date v){
        boolean flag= false;
        if(this.endDate.before(v)|| this.endDate.equals(v)) flag= true;
        return flag;
    }

    /**
     * Metodo per verificare se la data di fine di questa versione è prima
     * della data di fine di un'altra versione.
     * @param v
     * @return
     */
    public boolean isBefore(Version v){
        boolean flag = false;
        if ( this.endDate.before(v.endDate)) flag= true;
    return flag;
    }

    /**
     * Metodo per verificare se la data di fine di questa versione
     * è uguale a quella di un'altra versione.
     * @param v
     * @return
     */
    public boolean isEqual( Version v){
        boolean flag = false;
        if ( this.endDate.equals(v.endDate)) flag=true;
        return flag;
    }

    /**
     * Metodo per trovare il numero di rilascio di questa versione
     * all'interno di una lista di versioni.
     * @param vs
     * @return
     */
    public boolean booleanfindNumRel(List<Version> vs){
        boolean flag = false;
        for ( int i=0; i< vs.size(); i++){
            if(vs.get(i).getId()== this.id)
            {
                this.numRel= i+1;

            }
        }
        return flag;
    }


    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public int getNumRel() {
        return numRel;
    }

    public void setNumRel(int numRel) {
        this.numRel = numRel;
    }

}
