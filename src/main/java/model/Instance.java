package model;

import java.util.Date;

public class Instance {

    private String name;
    private Version version;
    private Date dateCreation;
    private int size;
    private int locTouched;
    private int NR;
    private int NFix;
    private int maxLocTouched;
    private int churn;
    private int maxChurn;
    private int avgChurn;
    private int age;
    private int maxLocAdded;
    private boolean bugginess;

    public Instance(Instance c){
        super();
        this.name=c.getName();
        this.version=c.getVersion();
        this.dateCreation=c.getDateCreation();
        this.size = c.getSize();
        this.locTouched = c.getLocTouched();
        this.age = c.getAge();
        NR=c.getNR();
        NFix=c.getNFix();
        this.maxLocTouched=c.getMaxLocTouched();
        this.churn=c.getChurn();
        this.maxChurn = c.getMaxChurn();
        this.avgChurn = c.getAvgChurn();
        this.maxLocAdded = c.getMaxLocAdded();
        this.bugginess = c.isBugginess();
    }

    private int getAge() { return age;
    }

    public Instance(String name, Version version,Date dateCreation){
        this.name=name;
        this.version=version;
        this.dateCreation=dateCreation;
        this.size=0;
        this.locTouched= 0;
        this.NR=0;
        this.NFix=0;
        this.maxLocTouched=0;
        this.churn=0;
        this.maxChurn=0;
        this.avgChurn=0;
        this.maxLocAdded = 0;
        this.bugginess=false;
        this.age=0;
    }

    /**
     * Aggiorna le linee di codice aggiunte e rimosse, il churn, e la dimensione.
     * @param added Linee di codice aggiunte.
     * @param deleted Linee di codice rimosse.
     */
    public void updateInstanceLoc(int added, int deleted) {
        if (added > maxLocAdded)
            maxLocAdded = added;

        locTouched += added + deleted;

        int ch = added - deleted;
        this.churn += ch;
        if (ch > maxChurn)
            maxChurn = ch;
        size += ch;
    }

    public void updateInstanceMeta(boolean fixCommit) {
        NR++;

        if(fixCommit) NFix++;

        this.avgChurn = churn / NR;
    }


    public boolean insideAV(Version iv, Version fv) {
        boolean flag = false; // Flag per indicare se l'istanza rientra nell'intervallo
        if (version.isBefore(fv) && (!version.isBefore(iv) || version.isEqual(iv))) {
            flag = true; // Se la versione dell'istanza è tra iv e fv, imposta il flag a true
        }
        return flag; // Restituisce se l'istanza è nell'intervallo
    }

    public void increaseAge() {
        this.age++;
    }

    public String getName() {
        return name;
    }


    public Version getVersion() {
        return version;
    }

    public void setVersion(Version version) {
        this.version = version;
    }

    public Date getDateCreation() {
        return dateCreation;
    }


    public int getSize() {
        return size;
    }


    public int getLocTouched() {
        return locTouched;
    }


    public int getNR() {
        return NR;
    }


    public int getNFix() {
        return NFix;
    }


    public int getMaxLocTouched() {
        return maxLocTouched;
    }


    public int getChurn() {
        return churn;
    }


    public int getMaxChurn() {
        return maxChurn;
    }


    public int getAvgChurn() {
        return avgChurn;
    }



    public int getMaxLocAdded() {
        return maxLocAdded;
    }


    public boolean isBugginess() {
        return bugginess;
    }

    public void setBugginess(boolean bugginess) {
        this.bugginess = bugginess;
    }


}
