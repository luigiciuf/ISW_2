package model;

import java.util.Date;
import java.util.List;

public class Ticket {


    private long id;
    private String key;
    private Date created;
    private Date resolDate;
    private Version av;
    private Version ov;
    private Version fv;

    /**
     * Costruttore della classe Ticket.
     * Inizializza un ticket con i dati forniti.
     *
     * @param id L'ID univoco del ticket.
     * @param key La chiave del ticket.
     * @param created La data di creazione del ticket.
     * @param resolDate La data di risoluzione del ticket.
     * @param av La versione affetta.
     * @param ov La versione di apertura.
     * @param fv La versione di risoluzione.
     */
    public Ticket(long id, String key, Date created, Date resolDate, Version av, Version ov, Version fv) {
        this.id = id;
        this.key = key;
        this.created = created;
        this.resolDate = resolDate;
        this.av = av;
        this.ov = ov;
        this.fv = fv;
    }

    /**
     * Verifica se il ticket non ha una versione affetta (AV).
     *
     * @return `true` se non c'è una versione affetta, `false` altrimenti.
     */
    public boolean withoutAv(){
        boolean flag = false;
        if (this.av == null) flag= true;
        return flag;
    }

    /**
     * Imposta la versione affetta (AV) utilizzando proportion.
     *
     * @param prop Il fattore di proporzione tra ov e fv.
     * @param allVersions Lista di tutte le versioni.
     */
    public void setAvWithProp(float prop, List<Version> allVersions){
        float ovRel = ov.getNumRel();
        float fvRel=fv.getNumRel();
        float posFloat = fvRel- (fvRel-ovRel)* prop;
        int pos = Math.round(posFloat);
        if(pos<1) pos=1;
        av = allVersions.get(pos-1);
        av.setNumRel(pos);

    }

    /**
     * Restituisce la versione di apertura (OV).
     *
     * @return La versione di apertura del ticket.
     */
    public Version getOv() {
        return ov;
    }
    /**
     * Imposta la versione di apertura (OV).
     *
     * @param ov La versione di apertura da impostare.
     */
    public void setOv(Version ov) {
        this.ov = ov;
    }

    /**
     * Restituisce la data di risoluzione del ticket.
     *
     * @return La data di risoluzione.
     */
    public Date getResolDate() {
        return resolDate;
    }
    /**
     * Imposta la data di risoluzione del ticket.
     *
     * @param resolDate La data di risoluzione da impostare.
     */
    public void setResolDate(Date resolDate) {
        this.resolDate = resolDate;
    }
    /**
     * Restituisce l'ID univoco del ticket.
     *
     * @return L'ID del ticket.
     */
    public long getId() {
        return id;
    }
    /**
     * Restituisce la chiave del ticket.
     *
     * @return La chiave del ticket.
     */
    public String getKey() {
        return key;
    }
    /**
     * Restituisce la versione affetta (AV).
     *
     * @return La versione affetta del ticket.
     */
    public Version getAv() {
        return av;
    }

    /**
     * Restituisce la data di creazione del ticket.
     *
     * @return La data di creazione.
     */
    public Date getCreated() {
        return created;
    }

    public Version getFv() {
        return fv;
    }



}
