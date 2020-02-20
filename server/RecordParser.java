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

  public static Map<String,Record> parse(String recordFile) {
    Map<String,Record> records = new HashMap<String,Record>();
    try {
      BufferedReader br = new BufferedReader(new FileReader(recordFile));
      while (br.ready()) {
          List<String> line = Arrays.asList(br.readLine().split(";"));
          String patient = line.get(0);
          String doctor = line.get(1);
          String nurse = line.get(2);
          String hostpitalDivision = line.get(3);

          StringBuilder infoSb = new StringBuilder();
          if (br.ready()) {
            String infoLine = br.readLine();
            while (!infoLine.equals("$%$") && br.ready()) {
              infoSb.append("\n" + infoLine);
              infoLine = br.readLine();
            }
          }
          String info = infoSb.toString();
          Record record = new Record(patient, doctor, nurse, hostpitalDivision, info);
          records.put(patient, record);
        }
      br.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return records;
  }
}
