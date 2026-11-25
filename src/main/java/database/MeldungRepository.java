package database;

import model.*;

import java.nio.file.Path;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Verwaltet die aktiven Meldungen in der Tabelle "meldung".
 * Da jeder Mitarbeiter maximal eine aktive Meldung besitzt, dient seine ID
 * gleichzeitig als Primärschlüssel der Meldung.
 */
public class MeldungRepository {

    // Wird benötigt, um zu einer Meldung das zugehörige Mitarbeiter-Objekt zu laden
    private final MitarbeiterRepository mitarbeiterRepo = new MitarbeiterRepository();

    /**
     * Speichert eine neue Meldung oder aktualisiert sie, falls für diesen Mitarbeiter
     * bereits eine Meldung existiert (z. B. beim Wechsel von Krank- auf Gesundmeldung).
     */
    public void saveOrUpdate(Meldung meldung) throws SQLException {
        // MERGE INTO ist ein H2-Befehl für "Einfügen, falls neu; sonst Überschreiben"
        String sql = """
            MERGE INTO meldung (
                mitarbeiter_id, typ, sachbearbeiter, attesttyp, attest_datei,
                pdf_krankmeldungsvorlage_mit_attest,
                pdf_gesundmeldungsvorlage_mit_attest,
                pdf_krank_und_gesundmeldungsvorlage_ohne_attest
            ) KEY(mitarbeiter_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Die ID des verknüpften Mitarbeiters als Schlüssel setzen
            stmt.setInt(1, meldung.getMitarbeiter().getId());

            // Liste der beteiligten Sachbearbeiter mit Semikolon getrennt speichern
            List<String> sb = meldung.getSachbearbeiter();
            stmt.setString(3, (sb == null || sb.isEmpty()) ? null : String.join(";", sb));

            // Alle unterklassenspezifischen Spalten zunächst mit NULL vorbelegen
            stmt.setNull(4, Types.BOOLEAN);
            stmt.setNull(5, Types.VARCHAR);
            stmt.setNull(6, Types.VARCHAR);
            stmt.setNull(7, Types.VARCHAR);
            stmt.setNull(8, Types.VARCHAR);

            // Prüfen, um welche Unterklasse von Meldung es sich handelt, und deren Felder füllen
            if (meldung instanceof mitAttest ma) {
                stmt.setString(2, "MIT_ATTEST");
                if (ma.getAttesttyp() != null) stmt.setBoolean(4, ma.getAttesttyp());
                if (ma.getAttestDatei() != null) stmt.setString(5, ma.getAttestDatei().toString());
                if (ma.getPdfKrankmeldungsvorlageMitAttest() != null) {
                    stmt.setString(6, ma.getPdfKrankmeldungsvorlageMitAttest().toString());
                }
            } else if (meldung instanceof Gesundmeldung gm) {
                stmt.setString(2, "GESUNDMELDUNG");
                if (gm.getPdfGesundmeldungsvorlageMitAttest() != null) {
                    stmt.setString(7, gm.getPdfGesundmeldungsvorlageMitAttest().toString());
                }
                if (gm.getPdfKrankUndGesundmeldungsvorlageOhneAttest() != null) {
                    stmt.setString(8, gm.getPdfKrankUndGesundmeldungsvorlageOhneAttest().toString());
                }
            } else {
                stmt.setString(2, "OHNE_ATTEST");
            }

            stmt.executeUpdate();
        }
    }

    /**
     * Lädt alle aktuell offenen Meldungen (z. B. für die Übersicht auf der Startseite).
     */
    public List<Meldung> findAll() throws SQLException {
        List<Meldung> liste = new ArrayList<>();
        String sql = "SELECT * FROM meldung";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                liste.add(mapRow(rs));
            }
        }
        return liste;
    }

    /**
     * Sucht die aktive Meldung eines bestimmten Mitarbeiters anhand seiner ID.
     */
    public Meldung findByMitarbeiterId(int mitarbeiterId) throws SQLException {
        String sql = "SELECT * FROM meldung WHERE mitarbeiter_id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, mitarbeiterId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    /**
     * Löscht die Meldung eines Mitarbeiters (nach Ablauf der Aufbewahrungsfrist)
     * und setzt gleichzeitig seine Krankheitsdaten in der Mitarbeiter-Tabelle zurück.
     */
    public void deleteByMitarbeiterId(int mitarbeiterId) throws SQLException {
        String sql = "DELETE FROM meldung WHERE mitarbeiter_id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, mitarbeiterId);
            stmt.executeUpdate();
        }
        // Verhindert, dass alte Krankheitszeiträume am Mitarbeiter hängen bleiben
        mitarbeiterRepo.resetKrankheitsdaten(mitarbeiterId);
    }

    /**
     * Hilfsmethode: Erzeugt anhand der Spalte "typ" die passende Java-Unterklasse
     * (mitAttest, Gesundmeldung oder ohneAttest) und befüllt ihre Felder.
     */
    private Meldung mapRow(ResultSet rs) throws SQLException {
        String typ = rs.getString("typ");
        Meldung meldung;

        if ("MIT_ATTEST".equals(typ)) {
            mitAttest ma = new mitAttest();
            boolean attesttyp = rs.getBoolean("attesttyp");
            // rs.wasNull() prüft, ob der Boolean in der Datenbank tatsächlich NULL war
            if (!rs.wasNull()) ma.setAttesttyp(attesttyp);

            // Gespeicherte Dateipfad-Strings wieder in Java-Path-Objekte umwandeln
            String attestDatei = rs.getString("attest_datei");
            if (attestDatei != null) ma.setAttestDatei(Path.of(attestDatei));

            String vorlageMit = rs.getString("pdf_krankmeldungsvorlage_mit_attest");
            if (vorlageMit != null) ma.setPdfKrankmeldungsvorlageMitAttest(Path.of(vorlageMit));

            meldung = ma;
        } else if ("GESUNDMELDUNG".equals(typ)) {
            Gesundmeldung gm = new Gesundmeldung();
            String vorlageGesundMit = rs.getString("pdf_gesundmeldungsvorlage_mit_attest");
            if (vorlageGesundMit != null) gm.setPdfGesundmeldungsvorlageMitAttest(Path.of(vorlageGesundMit));

            String vorlageOhne = rs.getString("pdf_krank_und_gesundmeldungsvorlage_ohne_attest");
            if (vorlageOhne != null) gm.setPdfKrankUndGesundmeldungsvorlageOhneAttest(Path.of(vorlageOhne));

            meldung = gm;
        } else {
            meldung = new ohneAttest();
        }

        // Zugehörigen Mitarbeiter komplett nachladen und der Meldung zuweisen
        int mitarbeiterId = rs.getInt("mitarbeiter_id");
        meldung.setMitarbeiter(mitarbeiterRepo.findById(mitarbeiterId));

        String sb = rs.getString("sachbearbeiter");
        meldung.setSachbearbeiter(sb == null || sb.isBlank() ? new ArrayList<>() : new ArrayList<>(Arrays.asList(sb.split(";"))));

        return meldung;
    }
}