package model;

import java.util.List;

public abstract class Meldung {

    private Mitarbeiter mitarbeiter;
    private List<String> Sachbearbeiter;

    public Mitarbeiter getMitarbeiter() {
        return mitarbeiter;
    }

    public void setMitarbeiter(Mitarbeiter mitarbeiter) {
        this.mitarbeiter = mitarbeiter;
    }

    public List<String> getSachbearbeiter() {
        return Sachbearbeiter;
    }

    public void setSachbearbeiter(List<String> sachbearbeiter) {
        Sachbearbeiter = sachbearbeiter;
    }
}
