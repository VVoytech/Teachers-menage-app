package proj.teachers_proj;

import java.util.ArrayList;
import java.util.Collections;

public class ClassTeacher {
    String groupName;
    ArrayList<Teacher> teacherArrayList = new ArrayList<>();
    int maxTeacher;

    public ClassTeacher(String groupName, int maxTeacher) {
        this.groupName = groupName;
        ArrayList<Teacher> teacherArrayList = new ArrayList<>();
        this.maxTeacher = maxTeacher;
    }

    public void addTeacher(Teacher teacher) {
        boolean p = true;
        if(!teacherArrayList.isEmpty()){
            for(int i = 0; i < teacherArrayList.size(); i++)
            {
                if(teacherArrayList.get(i).compareTo(teacher) == 0 && teacherArrayList.get(i).COMPARE_BY_NAME == teacher || maxTeacher > teacherArrayList.size()) {
                    p = false;
                }
            }
        }
        if(p){
            teacherArrayList.add(teacher);
        }
        else{
            System.out.println("Nauczyciel nie zostal dodany");
        }
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

    public void search(String surname){
        for(Teacher teacher : teacherArrayList){
            if(teacher.compareTo(new Teacher("", surname, TeacherCondition.NIEOBECNY, 0, 0)) == 0){
                System.out.println("Znaleziono nauczyciela o nastepujacych danych:");
                teacher.print();
                return;
            }
        }
        System.out.println("Nie ma nauczyciela o takim nazwisku");
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
}
