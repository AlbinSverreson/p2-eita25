package server;
import java.io.*;
import java.net.*;
import java.security.KeyStore;
import java.util.*;
import javax.net.*;
import javax.net.ssl.*;
import javax.security.cert.X509Certificate;

public class Server implements Runnable {
    private ServerSocket serverSocket = null;
    private static int numConnectedClients = 0;
    private List<Person> persons;
    private Map<String,List<Record>> patients;
    private Map<Integer, Record> records;
    private Authenticator authenticator = new Authenticator("./testfiles/log.txt");
    boolean quit = false;

    public Server(ServerSocket ss) throws IOException {
        serverSocket = ss;
        newListener();
    }

    private void loadRecords() {
        patients = RecordParser.parse("./testfiles/exempelRecord.txt");
        records = new HashMap<>();
        for (List<Record> l : patients.values()) {
            for (Record r : l) {
            records.put(Integer.parseInt(r.getID()), r);
            }
        }
    }

    private void loadPersons() {
        persons = PersonParser.parse("./testfiles/persons.txt");
    }


    private String getRecords(Person p, String name){
            try {
                List<Record> patientsRecords = patients.get(name);
                List<Record> permittedRecords = authenticator.canList(p, patientsRecords);
                if (permittedRecords.isEmpty()) {
                    return "You don't have read access for any of " + name + "'s records, or no such patient was found.";
                } else {
                    String recordString = "";
                    for (Record r : permittedRecords) {
                        recordString += "#" + r.getID() + " ";
                    }
                    return "ID's of records you have access to: " + recordString;
                }
            } catch(NullPointerException e) {
                return "You don't have read access for any of " + name + "'s records, or no such patient was found.";
            }
        }

    private String handleCommand(String msg, Person p, BufferedReader in, PrintWriter out){//DENNA ÄR LITE FEL DÅ AUTHENATICATOR SKA GÖRA LOGIKEN GÖR OM GÖR RÄTT
        String[] words = msg.split(" ");
            switch (words[0]) {
                case "list":
                    if (words.length != 2) return "Wrong syntax, try: list [patient]";
                    return getRecords(p, words[1]);
                case "read":
                    if (words.length != 2) return "Wrong syntax, try: read [record id]";
                    try {
                        int requestedRec = Integer.parseInt(words[1]);
                        Record rec = records.get(requestedRec);
                        if (rec!= null && authenticator.canRead(p, rec)) {
                            return rec.toString();
                        } else {
                            return "You don't have access to this record, or it does not exist.";
                        }
                    } catch (NumberFormatException e) {
                        return "The second argument needs to be a number.";
                    }
                case "write":
                    if (words.length < 3) return "Wrong syntax, try: write [record id] [info]";
                    try {
                        int requestedRec = Integer.parseInt(words[1]);
                        Record rec = records.get(requestedRec);
                        if (rec!= null && authenticator.canWrite(p, rec)) {
                            StringBuilder sb = new StringBuilder().append("\n");
                            for (int i = 2; i < words.length; i++) {
                                sb.append(words[i]).append(" ");
                            }
                            rec.addInfo(sb.toString());
                            RecordParser.write(records.values(), "./testfiles/exempelRecord.txt");
                            return "Record updated";
                        } else {
                            return "You don't have access to this record, or it does not exist.";
                        }
                    } catch (NumberFormatException e) {
                        return "The second argument needs to be a number.";
                    }
                case "delete":
                    if (words.length != 2) return "Wrong syntax, try: delete [record id]";
		            try {
                        int requestedRec = Integer.parseInt(words[1]);
                        Record rec = records.get(requestedRec);
                        if (rec!= null && authenticator.canDelete(p, rec)) {
                            records.remove(requestedRec);
                            patients.get(rec.getPatient()).remove(rec); // Also remove from the patients map
                            RecordParser.write(records.values(), "./testfiles/exempelRecord.txt");
                            return "Record deleted";
                        } else {
                            return "You don't have access to this record, or it does not exist.";
                        }
                    } catch (NumberFormatException e) {
                        return "The second argument needs to be a number.";
                    }
                case "create":
                    if (words.length != 2) return "Wrong syntax, try: create [patient]";
                    int newRecId = records.size() + 1;
                    Person patient = null;
                    String nurseName;
                    String divisionName;
                    String info;
                    for (Person p1 : persons){
                        if (p1.getName()==words[1]) patient = p1;
                    }
                    if(patient==null) return "No such patient in the system";
                    if(!authenticator.canCreate(p, patient)) return "You do not have the permissions to create that record.";
                    try {
                        out.println("What is the name of the nurse?");
                        nurseName = in.readLine();
                        out.println("What is the name of the division?");
                        divisionName = in.readLine();
                        out.println("What info should be stored in the record?");
                        info = in.readLine();

                        records.put(newRecId, new Record(
                            Integer.toString(newRecId), patient.getName(), p.getName(), nurseName, divisionName, info
                        ));

                        return "Record created.";
                    } catch (Exception e) {
                        return "uwu something went wrong";
                    }
                    case "help":
                        StringBuilder sb = new StringBuilder();
                        sb.append("The following commands are available: \n").append("list [patient] \n");
                        sb.append("read [record id] \n").append("write [record id] [info] \n");
                        sb.append("delete [record id] \n").append("create [patient]");
                        return sb.toString();
                    case "quit":
                        quit = true;

                default:
                    return "Wrong syntax, try: [command] [patient or id], or type in 'help' to see available commands.";
            }
    }

    public void run() {
        try {
            SSLSocket socket=(SSLSocket)serverSocket.accept();
            newListener();
            SSLSession session = socket.getSession();
            X509Certificate cert = (X509Certificate)session.getPeerCertificateChain()[0];
            String subject = cert.getSubjectDN().getName();
            String issuer = cert.getIssuerDN().getName();
            String serial = cert.getSerialNumber().toString();
            numConnectedClients++;

            // these two lines are filtering out the clients name.
            String subjectNameCN =  subject.split(" ")[0];
            String subjectName = subjectNameCN.substring(3, subjectNameCN.length()-1);

            int i = 0;
            boolean isFound = false;
            Person currentClient = null;
            loadPersons();
            loadRecords();

            while (!isFound && i<persons.size()) {
                Person p = persons.get(i);
                if (p.getName().equals(subjectName)) {
                    currentClient = p;
                    isFound = true;
                }
                i++;
            }

            if (!isFound) {
                socket.close();
                numConnectedClients--;
                System.out.println("invalid login");
                //Här vill vi kasta ut klienten, just nu händer inget för klienten. Den får dock ingen åtkomst.
                return;
            }

            System.out.println("Client connected");
            System.out.println("client name (cert subject DN field): " + subject);
            System.out.println("Issuer: " + issuer);
            System.out.println("Certificate serial number: " + serial);
            System.out.println(numConnectedClients + " concurrent connection(s)\n");

            PrintWriter out = null;
            BufferedReader in = null;
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String clientMsg = null;
            while ((clientMsg = in.readLine()) != null && !quit) {
                String response = handleCommand(clientMsg, currentClient, in, out);
                out.println(response);
                out.println("");
				out.flush();
                System.out.println("done\n");
			}
			in.close();
			out.close();
			socket.close();
    	    numConnectedClients--;
            System.out.println("client disconnected");
            System.out.println(numConnectedClients + " concurrent connection(s)\n");
		} catch (IOException e) {
            System.out.println("Client died: " + e.getMessage());
            e.printStackTrace();
            return;
        }
    }

    private void newListener() { (new Thread(this)).start(); } // calls run()

    public static void main(String args[]) {
        System.out.println("\nServer Started\n");
        int port = -1;
        if (args.length >= 1) {
            port = Integer.parseInt(args[0]);
        }
        String type = "TLS";
        try {
            ServerSocketFactory ssf = getServerSocketFactory(type);
            ServerSocket ss = ssf.createServerSocket(port);
            ((SSLServerSocket)ss).setNeedClientAuth(true); // enables client authentication
            new Server(ss);
        } catch (IOException e) {
            System.out.println("Unable to start Server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static ServerSocketFactory getServerSocketFactory(String type) {
        if (type.equals("TLS")) {
            SSLServerSocketFactory ssf = null;
            try { // set up key manager to perform server authentication
                SSLContext ctx = SSLContext.getInstance("TLS");
                KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
                TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
                KeyStore ks = KeyStore.getInstance("JKS");
				KeyStore ts = KeyStore.getInstance("JKS");
                char[] password = "password".toCharArray();

                ks.load(new FileInputStream("./certificates/serverKS"), password);  // keystore password (storepass)
                ts.load(new FileInputStream("./certificates/serverKS"), password); // truststore password (storepass)
                kmf.init(ks, password); // certificate password (keypass)
                tmf.init(ts);  // possible to use keystore as truststore here
                ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
                ssf = ctx.getServerSocketFactory();
                return ssf;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            return ServerSocketFactory.getDefault();
        }
        return null;
    }
}
