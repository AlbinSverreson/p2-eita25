package server;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
public class Logger{

  private File file;

  public static void main(String[] args) {
    Logger logger = new Logger("./testfiles/log.txt");
    logger.log("Hejhej");
    logger.log("Hallå");
  }

  public Logger(String file) {
    this.file = new File(file);
  }

  public void log(String logMessage){
    LocalDateTime timeNow = LocalDateTime.now();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    String formatDateTime = timeNow.format(formatter);
    try {
      PrintWriter writer = new PrintWriter(new FileWriter(file, true));

      writer.append(formatDateTime + " ; " + logMessage + "\n");
      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

}
