package ui;
import database.DatabaseManager;

public class Launcher {
    public static void main(String[] args) {
        DatabaseManager.initDatabase();
        HelloApplication.main(args);
    }
}