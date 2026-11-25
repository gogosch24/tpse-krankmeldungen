package database;

import model.PD_Mitarbeiter;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Verwaltet die Ansprechpartner des Personaldezernats (Empfänger der Meldungen)
 * inklusive ihrer Zuständigkeit für Angestellte oder Beamte.
 */
public class PdMitarbeiterRepository {

    /**
     * Speichert einen neuen Ansprechpartner des Personaldezernats in der Datenbank.
     */
    public void save(PD_Mitarbeiter pd) throws SQLException {
        String sql = """
            INSERT INTO pd_mitarbeiter (
                vorname, nachname, email, zustaendigkeit_angestellter, zustaendigkeit_beamte
            ) VALUES (?, ?, ?, ?, ?)
            """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, pd.getVorname());
            stmt.setString(2, pd.getNachname());
            stmt.setString(3, pd.getEmail());
            stmt.setBoolean(4, Boolean.TRUE.equals(pd.getZustaendigkeitAngestellter()));
            stmt.setBoolean(5, Boolean.TRUE.equals(pd.getZustaendigkeitBeamte()));
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    pd.setId(keys.getInt(1));
                }
            }
        }
    }

    /**
     * Lädt alle hinterlegten Ansprechpartner des Personaldezernats.
     */
    public List<PD_Mitarbeiter> findAll() throws SQLException {
        List<PD_Mitarbeiter> liste = new ArrayList<>();
        String sql = "SELECT * FROM pd_mitarbeiter ORDER BY nachname, vorname";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                PD_Mitarbeiter pd = new PD_Mitarbeiter();
                pd.setId(rs.getInt("id"));
                pd.setVorname(rs.getString("vorname"));
                pd.setNachname(rs.getString("nachname"));
                pd.setEmail(rs.getString("email"));
                pd.setZustaendigkeitAngestellter(rs.getBoolean("zustaendigkeit_angestellter"));
                pd.setZustaendigkeitBeamte(rs.getBoolean("zustaendigkeit_beamte"));
                liste.add(pd);
            }
        }
        return liste;
    }

    /**
     * Löscht einen Ansprechpartner des Personaldezernats anhand seiner ID.
     */
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM pd_mitarbeiter WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}