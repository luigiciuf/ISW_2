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



    public Ticket(long id, String key, Date created, Date resolDate, Version av, Version ob, Version fv) {
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
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getResolDate() {
        return resolDate;
    }

    public void setResolDate(Date resolDate) {
        this.resolDate = resolDate;
    }

    public Version getAv() {
        return av;
    }

    public void setAv(Version av) {
        this.av = av;
    }

    public Version getOv() {
        return ov;
    }

    public void setOv(Version ov) {
        this.ov = ov;
    }

    public Version getFv() {
        return fv;
    }

    public void setFv(Version fv) {
        this.fv = fv;
    }


}
