package database;

import org.h2.tools.Server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    private static final String DB_URL = "jdbc:h2:./data/krankmeldungen;AUTO_SERVER=TRUE";
    private static final String USER = "sa";
    private static final String PASSWORD = "";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASSWORD);
    }

    public static void initDatabase() {
        String sqlMitarbeiter = """
            CREATE TABLE IF NOT EXISTS mitarbeiter (
                id INT AUTO_INCREMENT PRIMARY KEY,
                vorname VARCHAR(100) NOT NULL,
                nachname VARCHAR(100) NOT NULL,
                personalnummer INT,
                kostenstelle INT,
                azubi BOOLEAN DEFAULT FALSE,
                beschaeftigter BOOLEAN DEFAULT FALSE,
                beamter BOOLEAN DEFAULT FALSE,
                weitere_arbeitgeber VARCHAR(500),
                zuletzt_gearbeitet TIMESTAMP,
                tag_wiederaufnahme TIMESTAMP,
                arbeitsunfaehig_ohne_au_von DATE,
                arbeitsunfaehig_ohne_au_bis DATE,
                arbeitsunfaehig_mit_au_von DATE,
                arbeitsunfaehig_mit_au_bis DATE,
                eigenverschulden BOOLEAN DEFAULT FALSE,
                arbeitsunfall BOOLEAN DEFAULT FALSE,
                durch_dritte_verursacht BOOLEAN DEFAULT FALSE,
                bemerkung VARCHAR(500)
            );
            """;

        String sqlMeldung = """
            CREATE TABLE IF NOT EXISTS meldung (
                mitarbeiter_id INT PRIMARY KEY,
                typ VARCHAR(30) NOT NULL,
                sachbearbeiter VARCHAR(500),
                attesttyp BOOLEAN,
                attest_datei VARCHAR(500),
                pdf_krankmeldungsvorlage_mit_attest VARCHAR(500),
                pdf_gesundmeldungsvorlage_mit_attest VARCHAR(500),
                pdf_krank_und_gesundmeldungsvorlage_ohne_attest VARCHAR(500),
                FOREIGN KEY (mitarbeiter_id) REFERENCES mitarbeiter(id) ON DELETE CASCADE
            );
            """;

        String sqlSachbearbeiter = """
            CREATE TABLE IF NOT EXISTS sachbearbeiter (
                id INT AUTO_INCREMENT PRIMARY KEY,
                vorname VARCHAR(100) NOT NULL,
                nachname VARCHAR(100) NOT NULL
            );
            """;

        String sqlPdMitarbeiter = """
            CREATE TABLE IF NOT EXISTS pd_mitarbeiter (
                id INT AUTO_INCREMENT PRIMARY KEY,
                vorname VARCHAR(100) NOT NULL,
                nachname VARCHAR(100) NOT NULL,
                email VARCHAR(150),
                zustaendigkeit_angestellter BOOLEAN DEFAULT FALSE,
                zustaendigkeit_beamte BOOLEAN DEFAULT FALSE
            );
            """;

        String sqlEinstellungen = """
            CREATE TABLE IF NOT EXISTS einstellungen (
                id INT PRIMARY KEY,
                speicherort VARCHAR(500),
                mail_vorlage VARCHAR(2000)
            );
            """;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(sqlMitarbeiter);
            stmt.execute(sqlMeldung);
            stmt.execute(sqlSachbearbeiter);
            stmt.execute(sqlPdMitarbeiter);
            stmt.execute(sqlEinstellungen);

            Server.createWebServer("-web", "-webPort", "8082").start();

            System.out.println("H2-Datenbank passend zum UML-Modell initialisiert (http://localhost:8082).");

        } catch (SQLException e) {
            System.err.println("Fehler beim Initialisieren der Datenbank: " + e.getMessage());
            e.printStackTrace();
        }
    }
}