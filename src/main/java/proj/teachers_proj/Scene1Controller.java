package proj.teachers_proj;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
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
import org.hibernate.Session;

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
    private Label averageRateLabel;

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
            scene2Controller.loadTeachersFromDatabase();
        }

        Stage stage;
        if (event.getSource() instanceof Node) {
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        } else {
            stage = (Stage) Stage.getWindows().filtered(Window::isShowing).get(0);
        }
        loadClassTeachersFromDatabase();

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

            // Aktualizacja widoku
            classContainer.addClass(groupNameText, maxTeachers); // Opcjonalne: lokalna aktualizacja
            ObservableList<ClassTeacher> data = FXCollections.observableArrayList(classContainer.getClassTeacherList());
            groupTable.setItems(data);

            addGroupName.clear();
            addGroupMax.clear();
        } catch (NumberFormatException e) {
            showAlert("Błąd", "Maksymalna liczba nauczycieli musi być liczbą całkowitą!");
        }
    }

    @FXML
    private void handleExportToCsv(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Zapisz plik CSV");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));

        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            String filePath = file.getAbsolutePath();

            CsvExporter exporter = new CsvExporter();
            exporter.exportToCsv(filePath);

            showAlert("Eksport zakończony", "Dane zostały zapisane do pliku:\n" + filePath);
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
        loadClassTeachersFromDatabase();

        groupName.setCellValueFactory(cellData -> cellData.getValue().groupNameProperty());
        groupMax.setCellValueFactory(cellData -> cellData.getValue().maxTeacherProperty().asObject());
        groupPercent.setCellValueFactory(cellData -> cellData.getValue().percent().asObject());

        ObservableList<ClassTeacher> data = FXCollections.observableArrayList(classContainer.getClassTeacherList());
        groupTable.setItems(data);

        groupTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {

                ClassTeacher selectedTeacher = groupTable.getSelectionModel().getSelectedItem();
                if (selectedTeacher != null) {
                    updateGroupStatistics(selectedTeacher);
                }
            } else if (event.getClickCount() == 2) {

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

            MenuItem addRateItem = new MenuItem("Wystaw ocenę");
            addRateItem.setOnAction(event -> {
                ClassTeacher selectedTeacherClass = row.getItem();
                if (selectedTeacherClass != null) {
                    showAddRateDialog(selectedTeacherClass);
                }
            });

            MenuItem deleteRateItem = new MenuItem("Usuń ocenę");
            deleteRateItem.setOnAction(event -> {
                ClassTeacher selectedTeacherClass = row.getItem();
                if (selectedTeacherClass != null) {
                    showDeleteRateDialog(selectedTeacherClass);  // Wywołanie dialogu usuwania oceny
                }
            });

            contextMenu.getItems().addAll(deleteItem, editItem, addRateItem, deleteRateItem);

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

                try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                    session.beginTransaction();
                    session.update(teacherClass);
                    session.getTransaction().commit();
                    loadClassTeachersFromDatabase();

                }

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

    private void showAddRateDialog(ClassTeacher teacherClass) {
        Dialog<Rate> dialog = new Dialog<>();
        dialog.setTitle("Wystaw ocenę");

        TextField rateValueField = new TextField();
        rateValueField.setPromptText("Wartość (0-6)");

        TextField rateCommentField = new TextField();
        rateCommentField.setPromptText("Komentarz");

        DatePicker rateDatePicker = new DatePicker();
        rateDatePicker.setValue(LocalDate.now());

        VBox layout = new VBox(10);
        layout.getChildren().addAll(
                new Label("Wartość oceny (0-6):"), rateValueField,
                new Label("Komentarz:"), rateCommentField,
                new Label("Data wystawienia:"), rateDatePicker
        );

        dialog.getDialogPane().setContent(layout);

        ButtonType saveButtonType = new ButtonType("Zapisz", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    int rateValue = Integer.parseInt(rateValueField.getText());
                    if (rateValue < 0 || rateValue > 6) {
                        throw new IllegalArgumentException("Wartość oceny musi być w przedziale 0-6.");
                    }
                    String comment = rateCommentField.getText();
                    LocalDate rateDate = rateDatePicker.getValue();

                    Rate newRate = new Rate(rateValue, teacherClass, rateDate, comment);

                    try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                        session.beginTransaction();
                        session.save(newRate);
                        session.getTransaction().commit();
                    }
                    return newRate;
                } catch (NumberFormatException e) {
                    showAlert("Błąd", "Wartość oceny musi być liczbą całkowitą.");
                } catch (IllegalArgumentException e) {
                    showAlert("Błąd", e.getMessage());
                } catch (Exception e) {
                    e.printStackTrace();
                    showAlert("Błąd", "Wystąpił nieoczekiwany błąd.");
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    private void showDeleteRateDialog(ClassTeacher teacherClass) {
        Dialog<Rate> dialog = new Dialog<>();
        dialog.setTitle("Usuń ocenę");

        List<Rate> rates = teacherClass.getRatesForTeacher(teacherClass);

        if (rates.isEmpty()) {
            showAlert("Błąd", "Brak ocen do usunięcia.");
            return;
        }

        ComboBox<Rate> rateComboBox = new ComboBox<>();
        rateComboBox.setItems(FXCollections.observableArrayList(rates));
        rateComboBox.setPromptText("Wybierz ocenę do usunięcia");

        VBox layout = new VBox(10);
        layout.getChildren().addAll(new Label("Wybierz ocenę do usunięcia:"), rateComboBox);

        dialog.getDialogPane().setContent(layout);

        ButtonType deleteButtonType = new ButtonType("Usuń", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(deleteButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == deleteButtonType) {
                Rate selectedRate = rateComboBox.getValue();
                if (selectedRate != null) {
                    try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                        session.beginTransaction();
                        session.delete(selectedRate); // Usunięcie wybranej oceny z bazy danych
                        session.getTransaction().commit();
                        showAlert("Sukces", "Ocena została usunięta.");
                        loadClassTeachersFromDatabase(); // Odświeżenie widoku
                    } catch (Exception e) {
                        e.printStackTrace();
                        showAlert("Błąd", "Wystąpił błąd podczas usuwania oceny.");
                    }
                } else {
                    showAlert("Błąd", "Nie wybrano oceny.");
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    private void refreshGroupTable() {
        ObservableList<ClassTeacher> updatedData = FXCollections.observableArrayList(classContainer.getClassTeacherList());
        groupTable.setItems(updatedData);
        groupTable.refresh();
    }

    private void loadClassTeachersFromDatabase() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            var criteria = session.getCriteriaBuilder();
            var query = criteria.createQuery(ClassTeacher.class);
            var root = query.from(ClassTeacher.class);

            query.groupBy(root.get("groupName"));
            query.select(root);

            List<ClassTeacher> teachersFromDb = session.createQuery(query).getResultList();

            classContainer.getClassTeacherList().clear();
            classContainer.getClassTeacherList().addAll(teachersFromDb);

            ObservableList<ClassTeacher> data = FXCollections.observableArrayList(classContainer.getClassTeacherList());
            groupTable.setItems(data);
            groupTable.refresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateGroupStatistics(ClassTeacher selectedGroup) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            var criteria = session.getCriteriaBuilder();
            var query = criteria.createQuery(Double.class);
            var root = query.from(Rate.class);

            query.select(criteria.avg(root.get("value")))
                    .where(criteria.equal(root.get("group"), selectedGroup));

            Double avgRate = session.createQuery(query).getSingleResult();

            averageRateLabel.setText("Średnia ocena grupy: " + avgRate);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}