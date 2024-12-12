package proj.teachers_proj;

import jakarta.persistence.*;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Alert;
import org.hibernate.Session;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "class_teacher")
public class ClassTeacher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "group_name", nullable = false)
    private String groupName;

    @Column(name = "max_teacher")
    private int maxTeacher;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "class_teacher_id")
    private List<Teacher> teacherArrayList = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "group_id")
    private List<Rate> rates = new ArrayList<>();

    public ClassTeacher() {}

    public ClassTeacher(String groupName, int maxTeacher) {
        this.groupName = groupName;
        this.maxTeacher = maxTeacher;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return groupName;
    }

    public void setName(String groupName) {
        this.groupName = groupName;
    }

    public int getMaxTeacher() {
        return maxTeacher;
    }

    public void setMaxTeacher(int maxTeacher) {
        this.maxTeacher = maxTeacher;
    }

    public List<Teacher> getTeacherArrayList() {
        return teacherArrayList;
    }

    public void setTeacherArrayList(List<Teacher> teacherArrayList) {
        this.teacherArrayList = teacherArrayList;
    }

    public SimpleStringProperty groupNameProperty() {
        return new SimpleStringProperty(groupName);
    }

    public SimpleIntegerProperty maxTeacherProperty() {
        return new SimpleIntegerProperty(maxTeacher);
    }

    public void addTeacher(Teacher teacher) {
        boolean n = true;
        boolean m = true;
        if(!teacherArrayList.isEmpty()){
            for(int i = 0; i < teacherArrayList.size(); i++)
            {
                if(teacherArrayList.get(i).getName().equals(teacher.getName()) && teacherArrayList.get(i).getSurname().equals(teacher.getSurname())) {
                    n = false;
                }
                if(getMaxTeacher() <= teacherArrayList.size()) {
                    m = false;
                }
            }
        }
        if(n && m){
            teacher.setClassTeacher(this);
            teacherArrayList.add(teacher);

            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                session.beginTransaction();
                session.save(teacher);
                session.getTransaction().commit();
            }
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

    public void removeTeacher(Teacher teacher){
        this.teacherArrayList.remove(teacher);
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.delete(teacher);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Błąd", "Wystąpił błąd podczas usuwania nauczyciela z bazy danych!");
        }
    }

    public ClassTeacher search(String surname){
        ClassTeacher temp = new ClassTeacher(getName(), getMaxTeacher());
        for(Teacher teacher : teacherArrayList){
            if(teacher.compareTo(new Teacher("", surname, TeacherCondition.NIEOBECNY, 0, 0)) == 0){
                System.out.println("Znaleziono nauczyciela o nastepujacych danych:");
                teacher.print();
                temp.teacherArrayList.add(teacher);
            }
        }
        if(temp.teacherArrayList.isEmpty()) showAlert("Błąd", "Brak nauczyciela o podanym nazwisku!");
        return temp;
    }

    public SimpleDoubleProperty percent() {
        double a = teacherArrayList.size();
        return new SimpleDoubleProperty(a / maxTeacher * 100);
    }

    public List<Rate> getRatesForTeacher(ClassTeacher teacherClass) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Rate r WHERE r.group = :teacherClass";
            return session.createQuery(hql, Rate.class)
                    .setParameter("teacherClass", teacherClass)
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Błąd", "Wystąpił błąd podczas ładowania ocen.");
            return List.of();  // Zwraca pustą listę w przypadku błędu
        }
    }



}
