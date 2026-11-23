import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;

import java.io.File;

public class PdfCheck {
    public static void main(String[] args) throws Exception {
        // Pfad zu eurem heruntergeladenen TU-Formular anpassen
        File pdfFile = new File("/Users/tolgataylan/Downloads/Krank_Gesundmeldung_ohne_AU-B_12.22.pdf");

        try (PDDocument document = Loader.loadPDF(pdfFile)) {
            PDAcroForm acroForm = document.getDocumentCatalog().getAcroForm();

            if (acroForm == null) {
                System.out.println("Kein interaktives Formular gefunden (flaches PDF).");
            } else {
                System.out.println("Gefundene Formularfelder:");
                for (PDField field : acroForm.getFieldTree()) {
                    System.out.println("- " + field.getFullyQualifiedName() + " (" + field.getClass().getSimpleName() + ")");
                }
            }
        }
    }
}