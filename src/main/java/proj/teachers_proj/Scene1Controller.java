package proj.teachers_proj;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.*;
import javafx.scene.input.KeyEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class SceneController implements Initializable {

    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    private Button Button1;

    @FXML
    private TableView<ClassTeacher> groupTable;

    @FXML
    private TableColumn<ClassTeacher, Integer> groupMax;

    @FXML
    private TableColumn<ClassTeacher, String> groupName;

    @FXML
    private TableColumn<ClassTeacher, Double> groupPercent;

    private ClassContainer classContainer = new ClassContainer();

    @FXML
    private AnchorPane scene1Root;

    public void switchToScene1(ActionEvent event) throws IOException {
        // Jeśli event jest null, pobierz aktualny stage w inny sposób
        stage = (event != null) ? (Stage) ((Node) event.getSource()).getScene().getWindow() :
                (Stage) Stage.getWindows().filtered(Window::isShowing).get(0);

        // Ładowanie sceny 1
        root = FXMLLoader.load(getClass().getResource("scene1.fxml"));
        setupScene(root, "Scena 1", "Scene2");
    }

    public void switchToScene2(ActionEvent event) throws IOException {
        // Jeśli event jest null, pobierz aktualny stage w inny sposób
        stage = (event != null) ? (Stage) ((Node) event.getSource()).getScene().getWindow() :
                (Stage) Stage.getWindows().filtered(Window::isShowing).get(0);

        // Ładowanie sceny 2
        root = FXMLLoader.load(getClass().getResource("scene2.fxml"));
        setupScene(root, "Scena 2", "Scene1");
    }

    private void setupScene(Parent root, String title, String targetScene) throws IOException {
        // Tworzenie nowej sceny
        scene = new Scene(root);

        // Obsługa zdarzenia klawiatury
        scene.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            switch (e.getCode()) {
                case ESCAPE:
                    try {
                        if ("Scene1".equals(targetScene)) {
                            switchToScene1(null); // Przełączenie na scenę 1
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        });

        // Ustawienia sceny i tytułu
        stage.setScene(scene);
        stage.setTitle(title);
        stage.show();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        groupName.setCellValueFactory(cellData -> cellData.getValue().nameProperty()); // Połącz z metodą getName()
        groupMax.setCellValueFactory(cellData -> cellData.getValue().maxTeachersProperty().asObject()); // Połącz z metodą getMaxTeachers()
        groupPercent.setCellValueFactory(cellData -> cellData.getValue().percent().asObject());

        Teacher t1 = new Teacher("Jan", "Kowalski", TeacherCondition.OBECNY , 1980, 3500);
        classContainer.addClass("Klasa 1", 5);
        classContainer.addClass("Klasa 2", 3);
        classContainer.groups.get("Klasa 1").addTeacher(t1);

        // Konwersja mapy na ObservableList
        ObservableList<ClassTeacher> data = FXCollections.observableArrayList(classContainer.getClassTeacherList());

        // Przypisanie danych do TableView
        groupTable.setItems(data);
    }
}