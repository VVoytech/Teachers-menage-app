package proj.teachers_proj;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CsvExporter {

    public void exportToCsv(String filePath) {
        // Pobieranie sesji Hibernate
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {

                // Sekcja 1: Eksport danych z tabeli Teacher
                exportTeachers(session, writer);

                // Dodanie pustej linii między sekcjami
                writer.newLine();
                writer.newLine();

                // Sekcja 2: Eksport danych z tabeli ClassTeacher
                exportClassTeachers(session, writer);

                System.out.println("Eksport zakończony sukcesem. Plik zapisano w: " + filePath);

            } catch (IOException e) {
                System.err.println("Błąd podczas zapisu pliku CSV: " + e.getMessage());
            }

            transaction.commit();
        } catch (Exception e) {
            System.err.println("Błąd podczas eksportu danych: " + e.getMessage());
        }
    }

    private void exportTeachers(Session session, BufferedWriter writer) throws IOException {
        // Zapytanie HQL do pobrania danych z tabeli teachers
        String hqlQuery = "SELECT t.id, t.name, t.surname, t.teacherCondition, t.birthYear, t.salary, t.classTeacher.id FROM Teacher t";
        Query<Object[]> query = session.createQuery(hqlQuery, Object[].class);

        List<Object[]> results = query.getResultList(); // Wyniki zapytania

        // Nagłówki dla sekcji Teacher
        writer.write("Teachers Data");
        writer.newLine();
        writer.write("Id;Name;Surname;Condition;BirthYear;Salary;GroupId");
        writer.newLine();

        // Dane z bazy
        for (Object[] row : results) {
            String line = String.format("%d;%s;%s;%s;%d;%.2f;%d",
                    row[0], // Id
                    row[1], // Name
                    row[2], // Surname
                    row[3], // TeacherCondition
                    row[4], // BirthYear
                    row[5],  // Salary
                    row[6]
            );
            writer.write(line);
            writer.newLine();
        }
    }

    private void exportClassTeachers(Session session, BufferedWriter writer) throws IOException {
        // Zapytanie HQL do pobrania danych z tabeli ClassTeacher
        String hqlQuery = "SELECT c.id, c.groupName, c.maxTeacher, COUNT(t.id) FROM ClassTeacher c LEFT JOIN c.teacherArrayList t GROUP BY c.id";
        Query<Object[]> query = session.createQuery(hqlQuery, Object[].class);

        List<Object[]> results = query.getResultList(); // Wyniki zapytania

        // Nagłówki dla sekcji ClassTeacher
        writer.write("ClassTeacher Data");
        writer.newLine();
        writer.write("Id;GroupName;MaxTeachers;CurrentTeachers");
        writer.newLine();

        // Dane z bazy
        for (Object[] row : results) {
            String line = String.format("%d;%s;%d;%d",
                    row[0], // GroupName
                    row[1], // MaxTeacher
                    row[2],  // Current number of teachers
                    row[3]
            );
            writer.write(line);
            writer.newLine();
        }
    }
}
