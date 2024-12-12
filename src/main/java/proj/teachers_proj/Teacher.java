package proj.teachers_proj;

import jakarta.persistence.*; // Importujemy adnotacje Hibernate
import javafx.beans.property.*;

@Entity  // Określamy, że klasa jest encją Hibernate
@Table(name = "teachers") // Możemy określić nazwę tabeli w bazie danych
public class Teacher implements Comparable<Teacher> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Określamy, że id będzie auto-generowane
    @Column(name = "id")  // Nazwa kolumny w tabeli
    private Long id;  // Zmieniamy na Long, ponieważ Hibernate lepiej obsługuje typ Long dla ID

    @Column(name = "name", nullable = false)  // Mamy możliwość określenia właściwości kolumny
    private String name;

    @Column(name = "surname", nullable = false)
    private String surname;

    @Enumerated(EnumType.STRING)
    @Column(name = "teacher_condition")
    private TeacherCondition teacherCondition;

    @Column(name = "birth_year")
    private int birthYear;

    @Column(name = "salary")
    private double salary;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_teacher_id")
    private ClassTeacher classTeacher;

    public Teacher() {}

    public Teacher(String name, String surname, TeacherCondition teacherCondition, int birthYear, double salary) {
        this.name = name;
        this.surname = surname;
        this.teacherCondition = teacherCondition;
        this.birthYear = birthYear;
        this.salary = salary;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public ClassTeacher getClassTeacher() {
        return classTeacher;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public TeacherCondition getTeacherCondition() {
        return teacherCondition;
    }

    public void setTeacherCondition(TeacherCondition teacherCondition) {
        this.teacherCondition = teacherCondition;
    }

    public int getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(int birthYear) {
        this.birthYear = birthYear;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public SimpleStringProperty nameProperty() {
        return new SimpleStringProperty(name);
    }

    public SimpleStringProperty surnameProperty() {
        return new SimpleStringProperty(surname);
    }

    public SimpleIntegerProperty birthYearProperty() {
        return new SimpleIntegerProperty(birthYear);
    }

    public SimpleDoubleProperty salaryProperty() {
        return new SimpleDoubleProperty(salary);
    }

    public ObjectProperty<TeacherCondition> conditionProperty() {
        return new SimpleObjectProperty<>(teacherCondition);
    }

    public void setClassTeacher(ClassTeacher classTeacher) {
        this.classTeacher = classTeacher;
    }

    public void print() {
        System.out.println("Imie i nazwisko: " + this.name + " " + this.surname +
                "\nStatus:" + this.teacherCondition + "\nRok urodzenia: " + this.birthYear + "\nWyplata: " + this.salary + "\n");
    }

    @Override
    public int compareTo(Teacher other) {
        return this.surname.compareToIgnoreCase(other.surname);
    }
}
