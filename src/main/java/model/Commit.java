package model;

import org.eclipse.jgit.revwalk.RevCommit;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;

public class Commit {
    private RevCommit rev;
    private String author;
    private Version version;
    private Date date;
    private List<String> classes;
    private List<String> classesTouched;
    private List<Ticket> buggyTickets;

    public Commit(RevCommit rev, String author, Version version, Date date) {
        super();
        this.rev = rev;
        this.author = author;
        this.version = version;
        this.date = date;
        this.classes = new ArrayList<>();
        this.classesTouched = new ArrayList<>();
        this.buggyTickets= new ArrayList<>();
    }

    public Version getVersion() {
        return version;
    }

    public Date getDate() {
        return date;
    }

    public List<String> getClasses() {
        return classes;
    }

    public List<Ticket> getBuggyTickets() {
        return buggyTickets;
    }

    public void setClasses(List<String> classes) {
        this.classes = classes;
    }

    public void setBuggyTickets(List<Ticket> buggyTickets) {
        this.buggyTickets = buggyTickets;
    }

    public String getAuthor() {
        return author;
    }

    public RevCommit getRev() {
        return rev;
    }

    public List<String> getClassesTouched() {
        return classesTouched;
    }

    public void addTouchedClass (String file) {
        classesTouched.add(file);
    }
}
