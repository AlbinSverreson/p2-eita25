package server;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class PersonParser {
    
    public static List<Person> createPersons(String filename) {
        ArrayList<Person> persons = new ArrayList<>();

        try {

            BufferedReader br = new BufferedReader(new FileReader(filename));

            while (br.ready()) {
                Person person = null;

                List<String> splittedInfo = Arrays.asList(br.readLine().split(";"));
                String name = splittedInfo.get(0);
                String role = splittedInfo.get(1);

                if (role.equals("Doctor") || role.equals("Nurse")) {
                    String division = splittedInfo.get(2);
                    List<String> patients = Arrays.asList(splittedInfo.get(3).split(","));

                    person = new Person(name, role, division);
                    for (String p : patients) {
                        person.addPatient(p);
                    }
                } else {
                    person = new Person(name, role);
                }
                persons.add(person);
            }            
            br.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return persons;
    }
}