package proj.teachers_proj;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application {
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;

        // Ładowanie pierwszej sceny
        FXMLLoader loader = new FXMLLoader(getClass().getResource("scene1.fxml"));
        Scene scene1 = new Scene(loader.load());

        primaryStage.setTitle("Przykład dwóch scen");
        primaryStage.setScene(scene1);
        primaryStage.show();
    }

    public static void changeScene(String fxmlFile) throws Exception {
        // Metoda pomocnicza do zmiany sceny
        FXMLLoader loader = new FXMLLoader(Main.class.getResource(fxmlFile));
        Scene newScene = new Scene(loader.load());
        primaryStage.setScene(newScene);
    }

    public static void main(String[] args) {
        launch();
    }
}