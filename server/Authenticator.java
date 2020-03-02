package server;

import java.util.ArrayList;
import java.util.List;

public class Authenticator {
  private Logger logger;  // don't forget to log every action

  public Authenticator(String filename){
    logger = new Logger(filename);
  }

  public List<Record> canList(Person p, List<Record> rl){
    String name = p.getName();
    String role = p.getRole();
    String patient = rl.get(0).getPatient();
    // String id = r.getID();

    if (p.isRole("Patient")) {
      if (patient.equals(name)) {
        logger.log(role + " " + name + " listed all their records.");
        return rl;
      }
      else {
        logger.log(role + " " + name + " tried to list" + patient + "'s records, but was denied.");
        return new ArrayList<>();
      }
    }

    if (p.isRole("Government")) {
      logger.log(role + " " + name + " listed " + patient + "'s records.");
      return rl;
    } 

    List<Record> permittedRecords = new ArrayList<>();
    String logString = "";

    for (Record r : rl) {
      if (p.isTreating(r.getPatient()) || p.inDivision(r.getDivision())) {
        logString += "#" + r.getID() + " ";
        permittedRecords.add(r);
      } 
    }

    if (logString.equals("")) {
      logger.log(role + " " + name + " tried to list" + patient + "'s records, but was denied.");
    } else {
      logger.log(role + " " + name + " listed " + patient + "'s records " + logString + ".");
    }

    return permittedRecords;

  }


  public  boolean canRead(Person p, Record r){
    String name = p.getName();
    String role = p.getRole();
    String patient = r.getPatient();
    String id = r.getID();

    if (p.isRole("Government")) {
      logger.log(role + " " + name + " accessed record #" + id + " for reading.");
      return true;
    } 
    if (p.isRole("Patient")) {
      if (patient.equals(name)) {
        logger.log(role + " " + name + " accessed their record #" + id + " for reading.");
        return true;
      }
      else {
        logger.log(role + " " + name + " tried to read" + patient + "'s record #" + id + ", but was denied.");
        return false;
      }
    }
    if (p.isTreating(r.getPatient()) || p.inDivision(r.getDivision())) {
      logger.log(role + " " + name + " accessed " + patient + "'s record #" + id + " for reading.");
      return true;
    }
    logger.log(role + " " + name + " tried to read " + patient + "'s record #" + id +  " but was denied.");
    return false;
  }

  public boolean canWrite(Person p, Record r){
    String name = p.getName();
    String role = p.getRole();
    String patient = r.getPatient();
    String id = r.getID();

    if (p.isRole("Patient") || p.isRole("Government")) {
      logger.log(role + " " + name + " tried to write to " + patient + "'s record #" + id + ", but was denied.");
      return false;
    }
    if (p.isTreating(r.getPatient())) {
      logger.log(role + " " + name + " accessed " + patient + "'s record #" + id + " for writing.");
      return true;
    }
    logger.log(role + " " + name + " tried to write to " + patient + "'s record #" + id +  " but was denied.");
    return false;
  }

  public boolean canCreate(Person p1, Person p2){
    String name = p1.getName();
    String patient = p2.getName();
    String role = p1.getRole();
    
    if (p1.isRole("Doctor") && p1.isTreating(p2.getName())) {
      logger.log(role + " " + name + " created a new record for patient " + patient + ".");
      return true;
    }
    logger.log(role + " " + name + " tried to create a new record for patient " + patient + " but was denied.");
    return false;
  }

  public boolean canDelete(Person p, Record r){
    String name = p.getName();
    String role = p.getRole();
    String id = r.getID();
    
    if (p.isRole("Government")) {
      logger.log(role + " " + name + " deleted record #" + id + ".");
      return true;
    }
    logger.log(role + " " + name + " tried to delete record #" + id + " but was denied.");
    return false;
  }

}
