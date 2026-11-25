package model;

import java.nio.file.Path;

public class Einstellungen {
    private Path Speicherort;
    private String MailVorlage;

    public String getMailVorlage() {
        return MailVorlage;
    }

    public void setMailVorlage(String mailVorlage) {
        MailVorlage = mailVorlage;
    }

    public Path getSpeicherort() {
        return Speicherort;
    }

    public void setSpeicherort(Path speicherort) {
        Speicherort = speicherort;
    }


}
