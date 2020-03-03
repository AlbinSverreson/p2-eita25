package server;

import java.lang.*;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class RecordParser {
  private static String recordSeparator = "$%$";
  /* Format on the record files are:
  RecordID;Patient;Doctor;Nurse;Division
  Information
  recordSeparator
  Next record
  */
  //public static void main(String[] args) {
    // List<Record> records = new ArrayList<>();
    // records.add(new Record("10","Eva","Alice","Diana","Akuten","Hjärtattack \n ohno"));
    // records.add(new Record("10","Eva","Alice","Diana","Akuten","blbblblba"));
    // write(records, "testfiles/testRecordParserWrite.txt");
  //}

  public static void write(Collection<Record> records, String recordFile) {
    try {
      PrintWriter writer = new PrintWriter(new FileWriter(recordFile),true);
      for (Record r: records) {
        writer.append(r.getID() + ";" + r.getPatient() + ";" + r.getDoctor() + ";" + r.getNurse() + ";" + r.getDivision());
        writer.append(r.getInfo()).append("\n").append(recordSeparator).append("\n");
      }
      writer.flush();
      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static Map<String,List<Record>> parse(String recordFile) {
    Map<String,List<Record>> records = new HashMap<String,List<Record>>();
    try {
      BufferedReader br = new BufferedReader(new FileReader(recordFile));
      while (br.ready()) {
          List<String> line = Arrays.asList(br.readLine().split(";"));
          String id = line.get(0);
          String patient = line.get(1);
          String doctor = line.get(2);
          String nurse = line.get(3);
          String hostpitalDivision = line.get(4);

          StringBuilder infoSb = new StringBuilder();
          if (br.ready()) {
            String infoLine = br.readLine();
            while (!infoLine.equals(recordSeparator) && br.ready()) {
              infoSb.append("\n" + infoLine);
              infoLine = br.readLine();
            }
          }
          String info = infoSb.toString();
          Record record = new Record(id, patient, doctor, nurse, hostpitalDivision, info);

          if (records.containsKey(patient)){
            records.get(patient).add(record);
          } else {
            List<Record> newRecordList = new ArrayList<Record>();
            newRecordList.add(record);
            records.put(patient, newRecordList);
          }

        }
      br.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return records;
  }
}
