package server;

public class Authenticator{
  private Logger logger;  // don't forget to log every action

  public Authenticator(String filename){
    logger = new Logger(filename);
  }

  public  boolean canRead(Person p, Record r){
    String name = p.getName();
    String role = p.getRole();
    String patient = r.getPatient();
    String id = r.getID();

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

    if (p.isRole("Patient")) {
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
      logger.log(role + " " + name + " created record a new record for patient " + patient + ".");
      return true;
    }
    logger.log(role + " " + name + " tried to create a new record for patient " + patient + " but was denied.");
    return false;
  }

}
