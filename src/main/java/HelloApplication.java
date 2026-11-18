import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) {
        //Text im Fenster
        Label label = new Label("Hello World!");
        label.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        // Layout Container
        StackPane root = new StackPane(label);
        Scene scene = new Scene(root, 600, 400);

        //Titel des Fensters
        stage.setTitle("Krankmeldungen-Tool");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}