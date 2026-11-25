package database;

import model.Mitarbeiter;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Verwaltet alle Datenbankzugriffe (Speichern, Lesen, Ändern, Löschen)
 * für die Tabelle "mitarbeiter".
 */
public class MitarbeiterRepository {

    /**
     * Speichert einen neuen Mitarbeiter in der Datenbank und schreibt die
     * von H2 automatisch vergebene ID direkt in das Java-Objekt zurück.
     */
    public void save(Mitarbeiter m) throws SQLException {
        // Fragezeichen (?) sind Platzhalter, die sicher gegen SQL-Injection befüllt werden
        String sql = """
            INSERT INTO mitarbeiter (
                vorname, nachname, personalnummer, kostenstelle,
                azubi, beschaeftigter, beamter, weitere_arbeitgeber,
                zuletzt_gearbeitet, tag_wiederaufnahme,
                arbeitsunfaehig_ohne_au_von, arbeitsunfaehig_ohne_au_bis,
                arbeitsunfaehig_mit_au_von, arbeitsunfaehig_mit_au_bis,
                eigenverschulden, arbeitsunfall, durch_dritte_verursacht, bemerkung
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        // try-with-resources schließt Verbindung und Statement am Ende automatisch
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            fillStatement(stmt, m);
            stmt.executeUpdate(); // Führt das INSERT in der Datenbank aus

            // Die von H2 erzeugte AUTO_INCREMENT-ID auslesen und dem Objekt zuweisen
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    m.setId(keys.getInt(1));
                }
            }
        }
    }

    /**
     * Aktualisiert alle Daten eines bereits vorhandenen Mitarbeiters anhand seiner ID.
     */
    public void update(Mitarbeiter m) throws SQLException {
        String sql = """
            UPDATE mitarbeiter SET
                vorname = ?, nachname = ?, personalnummer = ?, kostenstelle = ?,
                azubi = ?, beschaeftigter = ?, beamter = ?, weitere_arbeitgeber = ?,
                zuletzt_gearbeitet = ?, tag_wiederaufnahme = ?,
                arbeitsunfaehig_ohne_au_von = ?, arbeitsunfaehig_ohne_au_bis = ?,
                arbeitsunfaehig_mit_au_von = ?, arbeitsunfaehig_mit_au_bis = ?,
                eigenverschulden = ?, arbeitsunfall = ?, durch_dritte_verursacht = ?, bemerkung = ?
            WHERE id = ?
            """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            fillStatement(stmt, m);
            // Das 19. Fragezeichen gehört zur WHERE-Bedingung (welche Zeile geändert wird)
            stmt.setInt(19, m.getId());
            stmt.executeUpdate();
        }
    }

    /**
     * Lädt alle gespeicherten Mitarbeitenden alphabetisch sortiert aus der Datenbank.
     */
    public List<Mitarbeiter> findAll() throws SQLException {
        List<Mitarbeiter> liste = new ArrayList<>();
        String sql = "SELECT * FROM mitarbeiter ORDER BY nachname, vorname";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            // Zeile für Zeile durch das Ergebnis wandern und in Java-Objekte umwandeln
            while (rs.next()) {
                liste.add(mapRow(rs));
            }
        }
        return liste;
    }

    /**
     * Sucht genau einen Mitarbeiter anhand seiner Datenbank-ID.
     * Gibt null zurück, falls kein Eintrag gefunden wurde.
     */
    public Mitarbeiter findById(int id) throws SQLException {
        String sql = "SELECT * FROM mitarbeiter WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    /**
     * Setzt nach dem Löschen einer Meldung nur die krankheitsbezogenen Felder zurück.
     * Dauerhafte Stammdaten (Name, Personalnummer, Status, Bemerkung) bleiben erhalten.
     */
    public void resetKrankheitsdaten(int mitarbeiterId) throws SQLException {
        String sql = """
            UPDATE mitarbeiter SET
                zuletzt_gearbeitet = NULL,
                tag_wiederaufnahme = NULL,
                arbeitsunfaehig_ohne_au_von = NULL,
                arbeitsunfaehig_ohne_au_bis = NULL,
                arbeitsunfaehig_mit_au_von = NULL,
                arbeitsunfaehig_mit_au_bis = NULL,
                eigenverschulden = FALSE,
                arbeitsunfall = FALSE,
                durch_dritte_verursacht = FALSE
            WHERE id = ?
            """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, mitarbeiterId);
            stmt.executeUpdate();
        }
    }

    /**
     * Löscht einen Mitarbeiter komplett aus der Datenbank.
     * Durch "ON DELETE CASCADE" wird eine eventuelle Meldung automatisch mitgelöscht.
     */
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM mitarbeiter WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    /**
     * Hilfsmethode: Überträgt die Java-Attribute eines Mitarbeiter-Objekts
     * in die SQL-Platzhalter (1 bis 18) und wandelt Java-Typen in SQL-Typen um.
     */
    private void fillStatement(PreparedStatement stmt, Mitarbeiter m) throws SQLException {
        stmt.setString(1, m.getVorname());
        stmt.setString(2, m.getNachname());
        stmt.setInt(3, m.getPersonalnummer());
        stmt.setInt(4, m.getKostenstelle());

        // Boolean.TRUE.equals(...) verhindert NullPointerExceptions, falls ein Boolean null ist
        stmt.setBoolean(5, Boolean.TRUE.equals(m.getAzubi()));
        stmt.setBoolean(6, Boolean.TRUE.equals(m.getBeschaeftigter()));
        stmt.setBoolean(7, Boolean.TRUE.equals(m.getBeamter()));

        // Liste von Arbeitgebern mit Semikolon zu einem einzigen Text verbinden
        List<String> ag = m.getWeitereArbeitgeber();
        stmt.setString(8, (ag == null || ag.isEmpty()) ? null : String.join(";", ag));

        // LocalDateTime und LocalDate in SQL-Zeitstempel bzw. SQL-Datum umwandeln (falls gesetzt)
        stmt.setTimestamp(9, m.getZuletztGearbeitet() != null ? Timestamp.valueOf(m.getZuletztGearbeitet()) : null);
        stmt.setTimestamp(10, m.getTagWiederaufnahme() != null ? Timestamp.valueOf(m.getTagWiederaufnahme()) : null);
        stmt.setDate(11, m.getArbeitsunfaehigOhneAuVon() != null ? Date.valueOf(m.getArbeitsunfaehigOhneAuVon()) : null);
        stmt.setDate(12, m.getArbeitsunfaehigOhneAuBis() != null ? Date.valueOf(m.getArbeitsunfaehigOhneAuBis()) : null);
        stmt.setDate(13, m.getArbeitsunfaehigMitAuVon() != null ? Date.valueOf(m.getArbeitsunfaehigMitAuVon()) : null);
        stmt.setDate(14, m.getArbeitsunfaehigMitAuBis() != null ? Date.valueOf(m.getArbeitsunfaehigMitAuBis()) : null);

        stmt.setBoolean(15, Boolean.TRUE.equals(m.getEigenverschulden()));
        stmt.setBoolean(16, Boolean.TRUE.equals(m.getArbeitsunfall()));
        stmt.setBoolean(17, Boolean.TRUE.equals(m.getDurchDritteVerursacht()));
        stmt.setString(18, m.getBemerkung());
    }

    /**
     * Hilfsmethode: Baut aus einer gelesenen Datenbankzeile (ResultSet)
     * wieder ein vollständiges Java-Mitarbeiter-Objekt zusammen.
     */
    private Mitarbeiter mapRow(ResultSet rs) throws SQLException {
        Mitarbeiter m = new Mitarbeiter();
        m.setId(rs.getInt("id"));
        m.setVorname(rs.getString("vorname"));
        m.setNachname(rs.getString("nachname"));
        m.setPersonalnummer(rs.getInt("personalnummer"));
        m.setKostenstelle(rs.getInt("kostenstelle"));
        m.setAzubi(rs.getBoolean("azubi"));
        m.setBeschaeftigter(rs.getBoolean("beschaeftigter"));
        m.setBeamter(rs.getBoolean("beamter"));

        // Semikolon-getrennten Text wieder in eine Java-Liste zerlegen
        String ag = rs.getString("weitere_arbeitgeber");
        m.setWeitereArbeitgeber(ag == null || ag.isBlank() ? new ArrayList<>() : new ArrayList<>(Arrays.asList(ag.split(";"))));

        // SQL-Datumswerte nur dann in LocalDate/LocalDateTime umwandeln, wenn sie nicht NULL sind
        Timestamp zg = rs.getTimestamp("zuletzt_gearbeitet");
        if (zg != null) m.setZuletztGearbeitet(zg.toLocalDateTime());

        Timestamp tw = rs.getTimestamp("tag_wiederaufnahme");
        if (tw != null) m.setTagWiederaufnahme(tw.toLocalDateTime());

        Date ohneVon = rs.getDate("arbeitsunfaehig_ohne_au_von");
        if (ohneVon != null) m.setArbeitsunfaehigOhneAuVon(ohneVon.toLocalDate());

        Date ohneBis = rs.getDate("arbeitsunfaehig_ohne_au_bis");
        if (ohneBis != null) m.setArbeitsunfaehigOhneAuBis(ohneBis.toLocalDate());

        Date mitVon = rs.getDate("arbeitsunfaehig_mit_au_von");
        if (mitVon != null) m.setArbeitsunfaehigMitAuVon(mitVon.toLocalDate());

        Date mitBis = rs.getDate("arbeitsunfaehig_mit_au_bis");
        if (mitBis != null) m.setArbeitsunfaehigMitAuBis(mitBis.toLocalDate());

        m.setEigenverschulden(rs.getBoolean("eigenverschulden"));
        m.setArbeitsunfall(rs.getBoolean("arbeitsunfall"));
        m.setDurchDritteVerursacht(rs.getBoolean("durch_dritte_verursacht"));
        m.setBemerkung(rs.getString("bemerkung"));
        return m;
    }
}