package server;
import java.util.*;

public class Person{
  private String name;
  private String role;
  private String division;
  HashSet<String> patients;

  public Person(String n, String r, String d){
    name = n;
    role = r;
    division = d;
    patients = new HashSet<>();
  }

  public Person(String n, String r){
    name = n;
    role = r;
    division = null;
    patients = new HashSet<>();
  }

  public String getName(){
    return name;
  }

  public String getRole(){
    return role;
  }

  public void addPatient(String p){
    patients.add(p);
  }

  public boolean isRole(String r){
    return role.equals(r);
  }

  public boolean isTreating(String p){
    return patients.contains(p);
  }

  public boolean inDivision(String d){
    return division.equals(d);
  }

  @Override
  public int hashCode(){
    return name.hashCode();
  }

  @Override
  public boolean equals(Object o){
      Person p2 = (Person) o;
      return name.equals(p2.getName());
  }

}
