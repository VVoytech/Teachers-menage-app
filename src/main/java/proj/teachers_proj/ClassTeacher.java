package proj.teachers_proj;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Alert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ClassTeacher {
    private SimpleStringProperty groupName;
    ArrayList<Teacher> teacherArrayList = new ArrayList<>();
    private SimpleIntegerProperty maxTeacher;

    public ClassTeacher(String groupName, int maxTeacher) {
        this.groupName = new SimpleStringProperty(groupName);
        this.teacherArrayList = new ArrayList<>();
        this.maxTeacher = new SimpleIntegerProperty(maxTeacher);
    }

    public int getMaxTeacher() {
        return maxTeacher.get();
    }

    public String getName() {
        return groupName.get();
    }

    public SimpleStringProperty nameProperty() {
        return groupName;
    }

    public SimpleIntegerProperty maxTeachersProperty() {
        return maxTeacher;
    }

    public void setName(String name) {
        this.groupName = new SimpleStringProperty(name);
    }

    public void setMaxTeacher(int maxTeacher) {
        this.maxTeacher = new SimpleIntegerProperty(maxTeacher);
    }


    public void addTeacher(Teacher teacher) {
        boolean n = true;
        boolean m = true;
        if(!teacherArrayList.isEmpty()){
            for(int i = 0; i < teacherArrayList.size(); i++)
            {
                if(teacherArrayList.get(i).name.equals(teacher.name) && teacherArrayList.get(i).surname.equals(teacher.surname)) {
                    n = false;
                }
                if(getMaxTeacher() <= teacherArrayList.size()) {
                    m = false;
                }
            }
        }
        if(n && m){
            teacherArrayList.add(teacher);
        }
        else if(!n){
            System.out.println("W tej grupie jest już ten nauczyciel!");
            showAlert("Błąd", "W tej grupie jest już ten nauczyciel!");
        }
        else{
            System.out.println("Grupa pełna!");
            showAlert("Błąd", "Grupa pełna!");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void addSalary(Teacher teacher, double salary) {
        teacher.salary += salary;
    }

    public void removeTeacher(Teacher teacher){
        this.teacherArrayList.remove(teacher);
    }

    public void changeCondition(Teacher teacher, TeacherCondition condition) {
        teacher.teacherCondition = condition;
    }

    public ClassTeacher search(String surname){
        ClassTeacher temp = new ClassTeacher(getName(), getMaxTeacher());
        for(Teacher teacher : teacherArrayList){
            if(teacher.compareTo(new Teacher("", surname, TeacherCondition.NIEOBECNY, 0, 0)) == 0){
                System.out.println("Znaleziono nauczyciela o nastepujacych danych:");
                teacher.print();
                temp.addTeacher(teacher);
            }
        }
        if(temp.teacherArrayList.size() == 0) showAlert("Błąd", "Brak nauczyciela o podanym nazwisku!");
        return temp;
    }

    public void searchPartial(String str){
        this.teacherArrayList.forEach(teacher -> {
            if (teacher.name.contains(str) || teacher.surname.contains(str)) {
                teacher.print();
            }
        });
    }

    public int countByCondition(TeacherCondition condition){
        int count = 0;
        for (Teacher teacher : teacherArrayList) {
            if (teacher.teacherCondition == condition) {
                count++;
            }
        }
        return count;
    }

    public void summary(){
        this.teacherArrayList.forEach(teacher -> teacher.print());
    }

    public ArrayList<Teacher> sortByName(){
        this.teacherArrayList.sort((t1, t2) -> t1.name.compareTo(t2.name));
        return this.teacherArrayList;
    }

    public ArrayList<Teacher> sortBySalary(){
        this.teacherArrayList.sort(Teacher.COMPARE_BY_SALARY);
        return this.teacherArrayList;
    }

    public void max(){
        Collections.max(teacherArrayList, Teacher.COMPARE_BY_SALARY).print();
    }

    public SimpleDoubleProperty percent()
    {
        double a = teacherArrayList.size();
        return new SimpleDoubleProperty(a / maxTeacher.get() * 100);
    }
}
