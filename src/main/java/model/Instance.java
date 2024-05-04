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
    private int LocAdded;
    private int maxLocAdded;
    private boolean bugginess;

    public Instance(Instance c){
        super();
        this.name=c.getName();
        this.version=c.getVersion();
        this.dateCreation=c.getDateCreation();
        this.size = c.getSize();
        this.locTouched = c.getLocTouched();
        NR=c.getNR();
        NFix=c.getNFix();
        this.maxLocTouched=c.getMaxLocTouched();
        this.churn=c.getChurn();
        this.maxChurn = c.getMaxChurn();
        this.avgChurn = c.getAvgChurn();
        this.maxLocAdded = c.getMaxLocAdded();
        this.bugginess = c.isBugginess();
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
    }

    /**
     * Aggiorna le linee di codice aggiunte e rimosse, il churn, e la dimensione.
     * @param added Linee di codice aggiunte.
     * @param deleted Linee di codice rimosse.
     */
    public void updateInstanceLoc(int added, int deleted) {
        if (added > maxLocAdded) { // Se il numero di righe aggiunte è maggiore del massimo attuale
            maxLocAdded = added; // Aggiorna il massimo delle righe aggiunte
        }

        locTouched += added + deleted; // Aggiorna le linee di codice toccate

        churn += added - deleted; // Calcola il churn e lo aggiorna
        if ((added - deleted) > maxChurn) { // Se il churn attuale è maggiore del massimo attuale
            maxChurn = added - deleted; // Aggiorna il massimo churn
        }

        size += added - deleted; // Aggiorna la dimensione della classe

        LocAdded += added; // Aggiorna il totale delle linee di codice aggiunte
    }


    public boolean insideAV(Version iv, Version fv) {
        boolean flag = false; // Flag per indicare se l'istanza rientra nell'intervallo
        if (version.isBefore(fv) && (!version.isBefore(iv) || version.isEqual(iv))) {
            flag = true; // Se la versione dell'istanza è tra iv e fv, imposta il flag a true
        }
        return flag; // Restituisce se l'istanza è nell'intervallo
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public void setDateCreation(Date dateCreation) {
        this.dateCreation = dateCreation;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getLocTouched() {
        return locTouched;
    }

    public void setLocTouched(int locTouched) {
        this.locTouched = locTouched;
    }

    public int getNR() {
        return NR;
    }

    public void setNR(int NR) {
        this.NR = NR;
    }

    public int getNFix() {
        return NFix;
    }

    public void setNFix(int NFix) {
        this.NFix = NFix;
    }

    public int getMaxLocTouched() {
        return maxLocTouched;
    }

    public void setMaxLocTouched(int maxLocTouched) {
        this.maxLocTouched = maxLocTouched;
    }

    public int getChurn() {
        return churn;
    }

    public void setChurn(int churn) {
        this.churn = churn;
    }

    public int getMaxChurn() {
        return maxChurn;
    }

    public void setMaxChurn(int maxChurn) {
        this.maxChurn = maxChurn;
    }

    public int getAvgChurn() {
        return avgChurn;
    }

    public void setAvgChurn(int avgChurn) {
        this.avgChurn = avgChurn;
    }

    public int getLocAdded() {
        return LocAdded;
    }

    public void setLocAdded(int locAdded) {
        LocAdded = locAdded;
    }

    public int getMaxLocAdded() {
        return maxLocAdded;
    }

    public void setMaxLocAdded(int maxLocAdded) {
        this.maxLocAdded = maxLocAdded;
    }

    public boolean isBugginess() {
        return bugginess;
    }

    public void setBugginess(boolean bugginess) {
        this.bugginess = bugginess;
    }


}
