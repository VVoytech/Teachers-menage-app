package proj.teachers_proj;

import java.util.HashMap;
import java.util.Map;

public class ClassContainer {
    Map<String, ClassTeacher> groups = new HashMap<>();

    public void addClass(String groupName, int max){
        if(!groups.containsKey(groupName)){
            groups.put(groupName, new ClassTeacher(groupName, max));
        }
        else{
            System.out.println("Grupa o nazwie " + groupName + " juz istnieje.");
        }
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
            double b = groups.get(groupName).maxTeacher;
            System.out.println("Nazwa grupy: " + groupName + "\nProcentowe zapelnienie grupy: " + (a / b) * 100 + "%");
        }
    }
}
