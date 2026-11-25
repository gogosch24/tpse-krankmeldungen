package model;

import java.nio.file.Path;

public class mitAttest extends Meldung{
    private Boolean Attesttyp;
    private Path AttestDatei;
    private Path PdfKrankmeldungsvorlageMitAttest;

    public Path getAttestDatei() {
        return AttestDatei;
    }

    public void setAttestDatei(Path attestDatei) {
        AttestDatei = attestDatei;
    }

    public Boolean getAttesttyp() {
        return Attesttyp;
    }

    public void setAttesttyp(Boolean attesttyp) {
        Attesttyp = attesttyp;
    }

    public Path getPdfKrankmeldungsvorlageMitAttest() {
        return PdfKrankmeldungsvorlageMitAttest;
    }

    public void setPdfKrankmeldungsvorlageMitAttest(Path pdfKrankmeldungsvorlageMitAttest) {
        PdfKrankmeldungsvorlageMitAttest = pdfKrankmeldungsvorlageMitAttest;
    }

}
