package server;

import java.lang.*;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

public class RecordParser {

  public static void main(String[] args) {
    parse("testfiles/exempelRecord.txt");
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
            while (!infoLine.equals("$%$") && br.ready()) {
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
