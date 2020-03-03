package server;

import java.lang.*;

public class Record{
  private String id;
  private String patient;
  private String doctor;
  private String nurse;
  private String division;
  private String info;

  public static void main(String args[]) {
	  System.out.println("hej");
  }

  public Record(String id, String p, String doc, String n, String d, String i){
    this.id = id;
    patient = p;
    doctor = doc;
    nurse = n;
    division = d;
    info = i;
  }

  public String getID() {
    return id;
  }

  public String getPatient(){
    return patient;
  }

  public String getDoctor(){
    return doctor;
  }

  public String getNurse(){
    return nurse;
  }

  public String getDivision(){
    return division;
  }

  public String getInfo(){
    return info;
  }

  public void addInfo(String newInfo){
    info += newInfo;
  }

  public String toString(){
    StringBuilder sb = new StringBuilder();
    sb.append("Record ID: ").append(id).append("\n");
    sb.append("Patient: ").append(patient).append("\n");
    sb.append("Doctor: ").append(doctor).append("\n");
    sb.append("Nurse: ").append(nurse).append("\n");
    sb.append("Hospital division: ").append(division).append("\n");
    sb.append("Information: ").append(info);
    return sb.toString();
  }

}
