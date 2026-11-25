package model;

public class PD_Mitarbeiter extends Person{
    private String Email;
    private Boolean zustaendigkeitAngestellter;
    private Boolean setZustaendigkeitBeamte;

    public Boolean getZustaendigkeitAngestellter() {
        return zustaendigkeitAngestellter;
    }

    public void setZustaendigkeitAngestellter(Boolean zustaendigkeitAngestellter) {
        this.zustaendigkeitAngestellter = zustaendigkeitAngestellter;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        this.Email = email;
    }

    public Boolean getZustaendigkeitBeamte() {
        return setZustaendigkeitBeamte;
    }

    public void setZustaendigkeitBeamte(Boolean setZustaendigkeitBeamte) {
        this.setZustaendigkeitBeamte = setZustaendigkeitBeamte;
    }
}
