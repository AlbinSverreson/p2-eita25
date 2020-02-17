package server;

public class Authenticator{
  private Logger logger;

  public boolean canRead(Person p, Record r){
    String n = p.getName();
    if (r.getPatient().equals(n)) return true;
    if (r.getNurse().equals(n) || r.getDoctor().equals(n)) return true;
    if (p.inDivision(r.getDivision()) return true;
    return false;
  }

  public boolean canWrite(Person p, Record r){

  }

  public boolean canCreate(Person p1, Person p2){

  }

}
