package proj.teachers_proj;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.*;
import javafx.scene.input.MouseButton;
import javafx.stage.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class Scene1Controller implements Initializable {

    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    private Button addButton;

    @FXML
    private TableView<ClassTeacher> groupTable;

    @FXML
    private TableColumn<ClassTeacher, Integer> groupMax;

    @FXML
    private TableColumn<ClassTeacher, String> groupName;

    @FXML
    private TableColumn<ClassTeacher, Double> groupPercent;

    @FXML
    private TextField addGroupMax;

    @FXML
    private TextField addGroupName;

    ClassContainer classContainer = ClassContainer.getInstance();

    @FXML
    private AnchorPane scene1Root;

    public void switchToScene2(Event event, ClassTeacher selectedTeacher) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("scene2.fxml"));
        Parent root = loader.load();

        Scene2Controller scene2Controller = loader.getController();
        if (selectedTeacher != null) {
            scene2Controller.setTeacherData(selectedTeacher);
        }

        Stage stage;
        if (event.getSource() instanceof Node) {
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        } else {
            stage = (Stage) Stage.getWindows().filtered(Window::isShowing).get(0);
        }

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Nauczyciele z grupy ["+selectedTeacher.getName()+"]");
        stage.show();
    }

    @FXML
    private void handleAddButtonAction(ActionEvent event) {
        String groupNameText = addGroupName.getText();
        String groupMaxText = addGroupMax.getText();

        if (groupNameText.isEmpty() || groupMaxText.isEmpty()) {
            showAlert("Błąd", "Wszystkie pola muszą być wypełnione!");
            return;
        }

        try {
            int maxTeachers = Integer.parseInt(groupMaxText);

            ClassTeacher newGroup = new ClassTeacher(groupNameText, maxTeachers);

            classContainer.addClass(groupNameText, maxTeachers);

            ObservableList<ClassTeacher> data = FXCollections.observableArrayList(classContainer.getClassTeacherList());
            groupTable.setItems(data);

            addGroupName.clear();
            addGroupMax.clear();
        } catch (NumberFormatException e) {
            showAlert("Błąd", "Maksymalna liczba nauczycieli musi być liczbą całkowitą!");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        groupName.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        groupMax.setCellValueFactory(cellData -> cellData.getValue().maxTeachersProperty().asObject());
        groupPercent.setCellValueFactory(cellData -> cellData.getValue().percent().asObject());

        ObservableList<ClassTeacher> data = FXCollections.observableArrayList(classContainer.getClassTeacherList());
        groupTable.setItems(data);

        groupTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                ClassTeacher selectedTeacher = groupTable.getSelectionModel().getSelectedItem();
                if (selectedTeacher != null) {
                    try {
                        switchToScene2(event, selectedTeacher);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        configureContextMenu();
    }

    private void configureContextMenu() {
        groupTable.setRowFactory(tv -> {
            TableRow<ClassTeacher> row = new TableRow<>();
            ContextMenu contextMenu = new ContextMenu();

            MenuItem deleteItem = new MenuItem("Usuń grupę");
            deleteItem.setOnAction(event -> {
                ClassTeacher selectedTeacherClass = row.getItem();
                if (selectedTeacherClass != null) {
                    classContainer.removeClass(selectedTeacherClass.getName());
                    ObservableList<ClassTeacher> updatedData = FXCollections.observableArrayList(classContainer.getClassTeacherList());
                    groupTable.setItems(updatedData);
                    groupTable.refresh();
                    refreshGroupTable();
                }
            });

            MenuItem editItem = new MenuItem("Edytuj grupę");
            editItem.setOnAction(event -> {
                ClassTeacher selectedTeacherClass = row.getItem();
                if (selectedTeacherClass != null) {
                    showEditDialog(selectedTeacherClass);
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

    private void showEditDialog(ClassTeacher teacherClass) {
        Dialog<ClassTeacher> dialog = new Dialog<>();
        dialog.setTitle("Edytuj nauczyciela");

        TextField groupNameField = new TextField(teacherClass.getName());
        TextField groupMaxField = new TextField(Integer.toString(teacherClass.getMaxTeacher()));

        VBox layout = new VBox(10);
        layout.getChildren().addAll(new Label("Nazwa grupy:"),groupNameField, new Label("Pojemność grupy:"), groupMaxField);

        dialog.getDialogPane().setContent(layout);

        ButtonType saveButtonType = new ButtonType("Zapisz", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                teacherClass.setName(groupNameField.getText());
                teacherClass.setMaxTeacher(Integer.parseInt(groupMaxField.getText()));

                ObservableList<ClassTeacher> updatedData = FXCollections.observableArrayList(classContainer.getClassTeacherList());
                groupTable.setItems(updatedData);
                groupTable.refresh();

                return teacherClass;
            }
            return null;
        });

        dialog.showAndWait();
        refreshGroupTable();
    }

    private void refreshGroupTable() {
        ObservableList<ClassTeacher> updatedData = FXCollections.observableArrayList(classContainer.getClassTeacherList());
        groupTable.setItems(updatedData);
        groupTable.refresh();
    }

}