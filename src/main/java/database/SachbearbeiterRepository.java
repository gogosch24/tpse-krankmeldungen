package database;

import model.Sachbearbeiter;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Verwaltet die internen Sachbearbeitenden (Nutzer des Programms)
 * in der Tabelle "sachbearbeiter".
 */
public class SachbearbeiterRepository {

    /**
     * Legt einen neuen Sachbearbeiter an und speichert die generierte ID im Objekt.
     */
    public void save(Sachbearbeiter sb) throws SQLException {
        String sql = "INSERT INTO sachbearbeiter (vorname, nachname) VALUES (?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, sb.getVorname());
            stmt.setString(2, sb.getNachname());
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    sb.setId(keys.getInt(1));
                }
            }
        }
    }

    /**
     * Lädt alle Sachbearbeitenden alphabetisch sortiert (z. B. für das Auswahl-Dropdown).
     */
    public List<Sachbearbeiter> findAll() throws SQLException {
        List<Sachbearbeiter> liste = new ArrayList<>();
        String sql = "SELECT * FROM sachbearbeiter ORDER BY nachname, vorname";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Sachbearbeiter sb = new Sachbearbeiter();
                sb.setId(rs.getInt("id"));
                sb.setVorname(rs.getString("vorname"));
                sb.setNachname(rs.getString("nachname"));
                liste.add(sb);
            }
        }
        return liste;
    }

    /**
     * Entfernt einen Sachbearbeiter anhand seiner ID aus der Datenbank.
     */
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM sachbearbeiter WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}