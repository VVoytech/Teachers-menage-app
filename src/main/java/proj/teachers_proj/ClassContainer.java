package proj.teachers_proj;

import javafx.scene.control.Alert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ClassContainer {
    private static ClassContainer instance;
    Map<String, ClassTeacher> groups = new HashMap<>();

    private ClassContainer() {
    }

    // Publiczna statyczna metoda do pobierania instancji klasy
    public static ClassContainer getInstance() {
        if (instance == null) {
            // Tworzymy nową instancję tylko jeśli jeszcze nie istnieje
            instance = new ClassContainer();
            Teacher t1 = new Teacher("Jan", "Kowalski", TeacherCondition.OBECNY, 1980, 3500);
            instance.addClass("Klasa 1", 5);
            instance.addClass("Klasa 2", 3);
            instance.groups.get("Klasa 1").addTeacher(t1);
        }
        return instance;
    }

    public List<ClassTeacher> getClassTeacherList() {
        return groups.values().stream().collect(Collectors.toList());
    }

    public void addClass(String groupName, int max){
        if(!groups.containsKey(groupName)){
            groups.put(groupName, new ClassTeacher(groupName, max));
        }
        else{
            System.out.println("Grupa o nazwie " + groupName + " juz istnieje.");
            showAlert("Błąd", "Grupa o podanej nazwie istnieje!");
        }
    }
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void removeClass(String groupName){
        if(groups.containsKey(groupName)){
            groups.remove(groupName);
        }
        else{
            System.out.println("Grupa o nazwie " + groupName + " nie istnieje.");
        }
    }

    public void findEmpty(){
        System.out.println("Puste grupy:");
        for(String groupName : groups.keySet()){
            if(groups.get(groupName).teacherArrayList.isEmpty()){
                System.out.println(groupName);
            }
        }
    }

    public void summary()
    {
        for(String groupName : groups.keySet()){
            double a = groups.get(groupName).teacherArrayList.size();
            double b = groups.get(groupName).getMaxTeacher();
            System.out.println("Nazwa grupy: " + groups.get(groupName).getName() + "\nProcentowe zapelnienie grupy: " + (a / b) * 100 + "%");
        }
    }

}
