package server;

public class Authenticator{
  private Logger logger;  // don't forget to log every action

  public Authenticator(Logger l){
    logger = l;
  }

  public boolean canRead(Person p, Record r){
    String n = p.getName();
    if (r.getPatient().equals(n)) return true;
    if (r.getNurse().equals(n) || r.getDoctor().equals(n)) return true;
    if (p.inDivision(r.getDivision()) return true;
    return false;
  }

  public boolean canWrite(Person p, Record r){
    String n = p.getName();
    if (r.getNurse().equals(n) || r.getDoctor().equals(n)) return true;
    return false;
  }

  public boolean canCreate(Person p1, Person p2){
    String n = p.getName();
    if (p1.isRole("Doctor") && p1.isTreating(p2.getName())) return true;
    return false;
  }

}
