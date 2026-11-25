package model;

import java.nio.file.Path;

public class Gesundmeldung extends Meldung{

    private Path PdfGesundmeldungsvorlageMitAttest;
    private Path PdfKrankUndGesundmeldungsvorlageOhneAttest;

    public Path getPdfGesundmeldungsvorlageMitAttest() {
        return PdfGesundmeldungsvorlageMitAttest;
    }

    public void setPdfGesundmeldungsvorlageMitAttest(Path pdfGesundmeldungsvorlageMitAttest) {
        PdfGesundmeldungsvorlageMitAttest = pdfGesundmeldungsvorlageMitAttest;
    }

    public Path getPdfKrankUndGesundmeldungsvorlageOhneAttest() {
        return PdfKrankUndGesundmeldungsvorlageOhneAttest;
    }

    public void setPdfKrankUndGesundmeldungsvorlageOhneAttest(Path pdfKrankUndGesundmeldungsvorlageOhneAttest) {
        PdfKrankUndGesundmeldungsvorlageOhneAttest = pdfKrankUndGesundmeldungsvorlageOhneAttest;
    }
}
