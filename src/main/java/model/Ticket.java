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



    public Ticket(long id, String key, Date created, Date resolDate, Version av, Version ov, Version fv) {
        this.id = id;
        this.key = key;
        this.created = created;
        this.resolDate = resolDate;
        this.av = av;
        this.ov = ov;
        this.fv = fv;
    }

    public boolean withoutAv(){
        boolean flag = false;
        if (this.av == null) flag= true;
        return flag;
    }

    public void setAvWithProp(float prop, List<Version> allVersions){
        float ovRel = ov.getNumRel();
        float fvRel=fv.getNumRel();
        float posFloat = fvRel- (fvRel-ovRel)* prop;
        int pos = Math.round(posFloat);
        if(pos<1) pos=1;
        av = allVersions.get(pos-1);
        av.setNumRel(pos);

    }

    public Version getOv() {
        return ov;
    }

    public void setOv(Version ov) {
        this.ov = ov;
    }

    public Date getResolDate() {
        return resolDate;
    }

    public void setResolDate(Date resolDate) {
        this.resolDate = resolDate;
    }

    public long getId() {
        return id;
    }

    public String getKey() {
        return key;
    }

    public Version getAv() {
        return av;
    }

    public Date getCreated() {
        return created;
    }

    public Version getFv() {
        return fv;
    }



}
