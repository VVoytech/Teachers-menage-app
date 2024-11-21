package proj.teachers_proj;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.*;
import javafx.scene.input.KeyEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;


public class Scene2Controller implements Initializable {

    private Stage stage;
    private String name;

    @FXML
    private ChoiceBox<TeacherCondition> addTeacherCondition;

    @FXML
    private TextField addTeacherName;

    @FXML
    private TextField addTeacherSalary;

    @FXML
    private TextField addTeacherSurname;

    @FXML
    private TextField addTeacherYear;

    @FXML
    private TextField searchTeacherSurname;

    @FXML
    private AnchorPane scene2Root;

    @FXML
    private TableView<Teacher> teacherTable;

    @FXML
    private TableColumn<Teacher, TeacherCondition> teacherCondition;

    @FXML
    private TableColumn<Teacher, String> teacherName;

    @FXML
    private TableColumn<Teacher, Double> teacherSalary;

    @FXML
    private TableColumn<Teacher, String> teacherSurname;

    @FXML
    private TableColumn<Teacher, Integer> teacherBirthYear;

    private final ObservableList<Teacher> teacherList = FXCollections.observableArrayList();

    ClassContainer classContainer = ClassContainer.getInstance();

    public void setTeacherData(ClassTeacher classTeacher) {
        teacherList.clear();

        if (classTeacher != null) {
            teacherList.addAll(classTeacher.teacherArrayList);
            this.name = classTeacher.getName();
        }

        teacherTable.setItems(teacherList);
    }

    public void switchToScene1() throws IOException {
        stage = (Stage) scene2Root.getScene().getWindow();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("scene1.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root);
        scene.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                System.out.println("Pressed ESCAPE on Scene 1");
            }
        });

        // Ustaw scenę i wyświetl okno
        stage.setScene(scene);
        stage.setTitle("Grupy Nauczycieli");
        stage.show();
    }

    @FXML
    private void handleAddButtonAction(ActionEvent event) {
        // Pobranie wartości z pól tekstowych
        String teacherNameText = addTeacherName.getText();
        String teacherSurnameText = addTeacherSurname.getText();
        String teacherSalaryText = addTeacherSalary.getText();
        String teacherYearText = addTeacherYear.getText();

        // Walidacja danych wejściowych
        if (teacherNameText.isEmpty() || teacherSurnameText.isEmpty() || teacherSalaryText.isEmpty() || teacherYearText.isEmpty()) {
            showAlert("Błąd", "Wszystkie pola muszą być wypełnione!");
            return;
        }

        try {
            TeacherCondition teacherCondition = addTeacherCondition.getValue();

            if (teacherCondition == null) {
                showAlert("Błąd", "Proszę wybrać status nauczyciela!");
                return;
            }

            double teacherSalary = Double.parseDouble(teacherSalaryText);
            int teacherYear = Integer.parseInt(teacherYearText);

            Teacher newTeacher = new Teacher(teacherNameText, teacherSurnameText, teacherCondition, teacherYear, teacherSalary);

            classContainer.groups.get(name).addTeacher(newTeacher);

            ClassTeacher classTeacher = classContainer.groups.get(name);
            teacherList.setAll(classTeacher.teacherArrayList);
            teacherTable.setItems(teacherList);

            addTeacherName.clear();
            addTeacherSurname.clear();
            addTeacherSalary.clear();
            addTeacherYear.clear();
            addTeacherCondition.getSelectionModel().clearSelection();

        } catch (NumberFormatException e) {
            showAlert("Błąd", "Wszystkie pola numeryczne muszą być liczbami!");
        }
    }

    @FXML
    private void handleSearchButtonAction(ActionEvent event) {

        String searchSurname = searchTeacherSurname.getText();

        if (searchSurname.isEmpty()) {
            showAlert("Błąd", "Proszę wpisać nazwisko nauczyciela do wyszukiwania!");
            return;
        }

        ClassTeacher filteredTeachers = classContainer.groups.get(name).search(searchSurname);

        if (filteredTeachers != null && !filteredTeachers.teacherArrayList.isEmpty()) {
            teacherList.setAll(filteredTeachers.teacherArrayList);
            teacherTable.setItems(teacherList);
        } else {
            showAlert("Brak wyników", "Nie znaleziono nauczycieli o nazwisku: " + searchSurname);
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void initialize(URL url, ResourceBundle resourceBundle) {
        teacherName.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        teacherSurname.setCellValueFactory(cellData -> cellData.getValue().surnameProperty());
        teacherCondition.setCellValueFactory(cellData -> cellData.getValue().conditionProperty());
        teacherBirthYear.setCellValueFactory(cellData -> cellData.getValue().birthYearProperty().asObject());
        teacherSalary.setCellValueFactory(cellData -> cellData.getValue().salaryProperty().asObject());

        addTeacherCondition.setItems(FXCollections.observableArrayList(TeacherCondition.values()));
        addTeacherCondition.getSelectionModel().select(TeacherCondition.OBECNY);

        teacherTable.setItems(teacherList);

        scene2Root.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                try {
                    switchToScene1(); // Wywołanie metody bez argumentu
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        searchTeacherSurname.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                ClassTeacher classTeacher = classContainer.groups.get(name);
                if (classTeacher != null) {
                    teacherList.setAll(classTeacher.teacherArrayList);
                    teacherTable.setItems(teacherList);
                }
            }
        });

        configureContextMenu();
    }

    private void configureContextMenu() {
        teacherTable.setRowFactory(tv -> {
            TableRow<Teacher> row = new TableRow<>();
            ContextMenu contextMenu = new ContextMenu();

            MenuItem deleteItem = new MenuItem("Usuń nauczyciela");
            deleteItem.setOnAction(event -> {
                Teacher selectedTeacher = row.getItem();
                if (selectedTeacher != null) {
                    teacherList.remove(selectedTeacher);
                    classContainer.groups.get(name).removeTeacher(selectedTeacher); // Zakładam, że masz metodę removeTeacher()
                    teacherTable.refresh();
                }
            });

            MenuItem editItem = new MenuItem("Edytuj nauczyciela");
            editItem.setOnAction(event -> {
                Teacher selectedTeacher = row.getItem();
                if (selectedTeacher != null) {
                    showEditDialog(selectedTeacher);
                }
            });

            contextMenu.getItems().addAll(deleteItem, editItem);

            row.setOnContextMenuRequested(event -> {
                if (!row.isEmpty()) {
                    contextMenu.show(row, event.getScreenX(), event.getScreenY());
                }
            });

            row.setOnMousePressed(event -> {
                if (event.getButton() == MouseButton.PRIMARY) {
                    contextMenu.hide();
                }
            });

            return row;
        });
    }

    private void showEditDialog(Teacher teacher) {
        // Tworzenie okna dialogowego do edycji nauczyciela
        Dialog<Teacher> dialog = new Dialog<>();
        dialog.setTitle("Edytuj nauczyciela");

        // Tworzenie pól do edycji
        TextField nameField = new TextField(teacher.name);
        TextField surnameField = new TextField(teacher.surname);
        TextField salaryField = new TextField(Double.toString(teacher.salary));
        TextField yearField = new TextField(Integer.toString(teacher.birthYear));

        // Layout dialogu
        VBox layout = new VBox(10);
        layout.getChildren().addAll(new Label("Imię:"), nameField, new Label("Nazwisko:"), surnameField,
                new Label("Pensja:"), salaryField, new Label("Rok urodzenia:"), yearField);

        dialog.getDialogPane().setContent(layout);

        // Przyciski dialogu
        ButtonType saveButtonType = new ButtonType("Zapisz", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                teacher.setName(nameField.getText());
                teacher.setSurname(surnameField.getText());
                teacher.setSalary(Double.parseDouble(salaryField.getText()));
                teacher.setBirthYear(Integer.parseInt(yearField.getText()));
                return teacher;
            }
            return null;
        });

        dialog.showAndWait();
        teacherTable.refresh(); // Odśwież tabelę po edycji
    }
}
