package proj.teachers_proj;

import javafx.scene.control.Alert;
import org.hibernate.Session;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ClassContainer {
    private static ClassContainer instance;
    Map<String, ClassTeacher> groups = new HashMap<>();

    private ClassContainer() {
    }

    public static ClassContainer getInstance() {
        if (instance == null) {
            instance = new ClassContainer();
            instance.loadClassTeachersFromDatabase();
        }
        return instance;
    }

    public List<ClassTeacher> getClassTeacherList() {
        return groups.values().stream().collect(Collectors.toList());
    }

    public void addClass(String groupName, int max){
        if(!groups.containsKey(groupName)){
            ClassTeacher newGroup = new ClassTeacher(groupName, max);
            groups.put(groupName, newGroup);

            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                session.beginTransaction();
                session.save(newGroup);
                session.getTransaction().commit();
            }

        }
        else{
            System.out.println("Grupa o nazwie " + groupName + " juz istnieje.");
            showAlert("Błąd", "Grupa o podanej nazwie istnieje!");
        }
    }

    private void loadClassTeachersFromDatabase() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<ClassTeacher> teachersFromDb = session.createQuery("from ClassTeacher", ClassTeacher.class).list();
            for (ClassTeacher group : teachersFromDb) {
                groups.put(group.getName(), group);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveClassToDatabase(ClassTeacher newGroup) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.save(newGroup);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Błąd", "Wystąpił błąd podczas zapisywania grupy do bazy danych!");
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
            ClassTeacher groupToRemove = groups.get(groupName);

            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                session.beginTransaction();
                session.delete(groupToRemove);
                session.getTransaction().commit();
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Błąd", "Wystąpił błąd podczas usuwania grupy z bazy danych!");
            }

            groups.remove(groupName);
        }
        else{
            System.out.println("Grupa o nazwie " + groupName + " nie istnieje.");
        }
    }

}
