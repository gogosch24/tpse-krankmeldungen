package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class Mitarbeiter extends Person{
    private int Kostenstelle;
    private Boolean Azubi;
    private Boolean Beschaeftigter;
    private Boolean Beamter;
    private List<String> WeitereArbeitgeber;
    private LocalDateTime ZuletztGearbeitet;
    private LocalDateTime TagWiederaufnahme;
    private LocalDate ArbeitsunfaehigOhneAuVon;
    private LocalDate ArbeitsunfaehigOhneAuBis;
    private LocalDate ArbeitsunfaehigMitAuVon;
    private LocalDate ArbeitsunfaehigMitAuBis;
    private Boolean Eigenverschulden;
    private Boolean Arbeitsunfall;
    private Boolean DurchDritteVerursacht;
    private String Bemerkung;

    private int Personalnummer;

    public int getPersonalnummer() {
        return Personalnummer;
    }

    public void setPersonalnummer(int personalnummer) {
        Personalnummer = personalnummer;
    }

    public int getKostenstelle() {
        return Kostenstelle;
    }

    public void setKostenstelle(int kostenstelle) {
        Kostenstelle = kostenstelle;
    }

    public Boolean getAzubi() {
        return Azubi;
    }

    public void setAzubi(Boolean azubi) {
        Azubi = azubi;
    }

    public Boolean getBeschaeftigter() {
        return Beschaeftigter;
    }

    public void setBeschaeftigter(Boolean beschaeftigter) {
        Beschaeftigter = beschaeftigter;
    }

    public Boolean getBeamter() {
        return Beamter;
    }

    public void setBeamter(Boolean beamter) {
        Beamter = beamter;
    }

    public List<String> getWeitereArbeitgeber() {
        return WeitereArbeitgeber;
    }

    public void setWeitereArbeitgeber(List<String> weitereArbeitgeber) {
        WeitereArbeitgeber = weitereArbeitgeber;
    }

    public LocalDateTime getZuletztGearbeitet() {
        return ZuletztGearbeitet;
    }

    public void setZuletztGearbeitet(LocalDateTime zuletztGearbeitet) {
        ZuletztGearbeitet = zuletztGearbeitet;
    }

    public LocalDateTime getTagWiederaufnahme() {
        return TagWiederaufnahme;
    }

    public void setTagWiederaufnahme(LocalDateTime tagWiederaufnahme) {
        TagWiederaufnahme = tagWiederaufnahme;
    }

    public LocalDate getArbeitsunfaehigOhneAuVon() {
        return ArbeitsunfaehigOhneAuVon;
    }

    public void setArbeitsunfaehigOhneAuVon(LocalDate arbeitsunfaehigOhneAuVon) {
        ArbeitsunfaehigOhneAuVon = arbeitsunfaehigOhneAuVon;
    }

    public LocalDate getArbeitsunfaehigOhneAuBis() {
        return ArbeitsunfaehigOhneAuBis;
    }

    public void setArbeitsunfaehigOhneAuBis(LocalDate arbeitsunfaehigOhneAuBis) {
        ArbeitsunfaehigOhneAuBis = arbeitsunfaehigOhneAuBis;
    }

    public LocalDate getArbeitsunfaehigMitAuVon() {
        return ArbeitsunfaehigMitAuVon;
    }

    public void setArbeitsunfaehigMitAuVon(LocalDate arbeitsunfaehigMitAuVon) {
        ArbeitsunfaehigMitAuVon = arbeitsunfaehigMitAuVon;
    }

    public LocalDate getArbeitsunfaehigMitAuBis() {
        return ArbeitsunfaehigMitAuBis;
    }

    public void setArbeitsunfaehigMitAuBis(LocalDate arbeitsunfaehigMitAuBis) {
        ArbeitsunfaehigMitAuBis = arbeitsunfaehigMitAuBis;
    }

    public Boolean getEigenverschulden() {
        return Eigenverschulden;
    }

    public void setEigenverschulden(Boolean eigenverschulden) {
        Eigenverschulden = eigenverschulden;
    }

    public Boolean getArbeitsunfall() {
        return Arbeitsunfall;
    }

    public void setArbeitsunfall(Boolean arbeitsunfall) {
        Arbeitsunfall = arbeitsunfall;
    }

    public Boolean getDurchDritteVerursacht() {
        return DurchDritteVerursacht;
    }

    public void setDurchDritteVerursacht(Boolean durchDritteVerursacht) {
        DurchDritteVerursacht = durchDritteVerursacht;
    }

    public String getBemerkung() {
        return Bemerkung;
    }

    public void setBemerkung(String bemerkung) {
        Bemerkung = bemerkung;
    }


}
