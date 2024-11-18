package proj.teachers_proj;

import java.util.Comparator;

public class Teacher implements Comparable<Teacher> {
    String name;
    String surname;
    TeacherCondition teacherCondition;
    int birthYear;
    double salary;

    public Teacher(String name, String surname, TeacherCondition teacherCondition, int birthYear, double salary) {
        this.name = name;
        this.surname = surname;
        this.teacherCondition = teacherCondition;
        this.birthYear = birthYear;
        this.salary = salary;
    }

    public void print(){
        System.out.println("Imie i nazwisko: " + this.name + " " + this.surname + "\nStatus:" + this.teacherCondition + "\nRok urodzenia: " + this.birthYear + "\nWyplata: " + this.salary + "\n");
    }

    public int compareTo(Teacher other) {
        return this.surname.compareToIgnoreCase(other.surname);
    }

    public static final Comparator<Teacher> COMPARE_BY_NAME = (t1, t2) -> {
        return t1.name.compareToIgnoreCase(t2.name);
    };
    public static final Comparator<Teacher> COMPARE_BY_SURNAME = (t1, t2) ->{
        return t1.surname.compareToIgnoreCase(t2.surname);
    };
    public static final Comparator<Teacher> COMPARE_BY_BIRTH_YEAR = Comparator.comparingInt(teacher -> teacher.birthYear);
    public static final Comparator<Teacher> COMPARE_BY_SALARY = Comparator.comparingDouble(teacher -> teacher.salary);
    public static final Comparator<Teacher> COMPARE_BY_SALARY_DESCENDING = (t1, t2) -> {
        return Double.compare(t2.salary, t1.salary);
    };
}
