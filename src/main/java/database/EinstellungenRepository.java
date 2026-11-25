package database;

import model.Einstellungen;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Verwaltet die globalen Programmeinstellungen (Speicherort und E-Mail-Vorlage).
 * In der Tabelle existiert immer genau ein Datensatz mit der festen ID 1.
 */
public class EinstellungenRepository {

    /**
     * Speichert oder überschreibt die Programmeinstellungen unter der festen ID 1.
     */
    public void save(Einstellungen einstellungen) throws SQLException {
        String sql = "MERGE INTO einstellungen (id, speicherort, mail_vorlage) KEY(id) VALUES (1, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Path-Objekt für die Speicherung in Text umwandeln
            stmt.setString(1, einstellungen.getSpeicherort() != null ? einstellungen.getSpeicherort().toString() : null);
            stmt.setString(2, einstellungen.getMailVorlage());
            stmt.executeUpdate();
        }
    }

    /**
     * Lädt die gespeicherten Einstellungen. Falls noch nichts gespeichert wurde,
     * wird ein leeres Standard-Einstellungen-Objekt zurückgegeben.
     */
    public Einstellungen load() throws SQLException {
        String sql = "SELECT speicherort, mail_vorlage FROM einstellungen WHERE id = 1";
        Einstellungen einstellungen = new Einstellungen();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                String ort = rs.getString("speicherort");
                if (ort != null && !ort.isBlank()) {
                    // Gespeicherten Pfad-Text wieder in ein Java-Path-Objekt umwandeln
                    einstellungen.setSpeicherort(Path.of(ort));
                }
                einstellungen.setMailVorlage(rs.getString("mail_vorlage"));
            }
        }
        return einstellungen;
    }
}