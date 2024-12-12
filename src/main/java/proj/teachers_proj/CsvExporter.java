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

                exportTeachers(session, writer);

                writer.newLine();
                writer.newLine();

                exportClassTeachers(session, writer);

                writer.newLine();
                writer.newLine();

                exportRates(session, writer);

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

        String hqlQuery = "SELECT t.id, t.name, t.surname, t.teacherCondition, t.birthYear, t.salary, t.classTeacher.id FROM Teacher t";
        Query<Object[]> query = session.createQuery(hqlQuery, Object[].class);

        List<Object[]> results = query.getResultList();

        writer.write("Teachers Data");
        writer.newLine();
        writer.write("Id;Name;Surname;Condition;BirthYear;Salary;GroupId");
        writer.newLine();

        for (Object[] row : results) {
            String line = String.format("%d;%s;%s;%s;%d;%.2f;%d",
                    row[0], // Id
                    row[1], // Name
                    row[2], // Surname
                    row[3], // TeacherCondition
                    row[4], // BirthYear
                    row[5], // Salary
                    row[6]  //Id grupy
            );
            writer.write(line);
            writer.newLine();
        }
    }

    private void exportClassTeachers(Session session, BufferedWriter writer) throws IOException {
        String hqlQuery = "SELECT c.id, c.groupName, c.maxTeacher, COUNT(t.id) FROM ClassTeacher c LEFT JOIN c.teacherArrayList t GROUP BY c.id";
        Query<Object[]> query = session.createQuery(hqlQuery, Object[].class);

        List<Object[]> results = query.getResultList();

        writer.write("ClassTeacher Data");
        writer.newLine();
        writer.write("Id;GroupName;MaxTeachers;CurrentTeachers");
        writer.newLine();

        for (Object[] row : results) {
            String line = String.format("%d;%s;%d;%d",
                    row[0], // Id grupy
                    row[1], // Nazwa grupy
                    row[2],  // Maksymalna pojemnosc
                    row[3]  // Aktualne zapelnienie
            );
            writer.write(line);
            writer.newLine();
        }
    }

    private void exportRates(Session session, BufferedWriter writer) throws IOException {
        // Zapytanie HQL do pobrania danych z tabeli Rate
        String hqlQuery = "SELECT r.id, r.value, r.date, r.group.id FROM Rate r";
        Query<Object[]> query = session.createQuery(hqlQuery, Object[].class);

        List<Object[]> results = query.getResultList();

        writer.write("Rate Data");
        writer.newLine();
        writer.write("Id;Value;Date;GroupId");
        writer.newLine();

        for (Object[] row : results) {
            String line = String.format("%d;%d;%s;%d",
                    row[0], // Id
                    row[1], // Value
                    row[2], // Date
                    row[3] // GroupId
            );
            writer.write(line);
            writer.newLine();
        }
    }
}
