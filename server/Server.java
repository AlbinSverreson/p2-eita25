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
    private Authenticator authenticator;

    public Server(ServerSocket ss) throws IOException {
        serverSocket = ss;
        newListener();
    }

    private void loadRecords() {

    }

    private void loadPersons() {

    }

    private void askForAction() {

    }


    private String getRecords(Person p, String name){
        return ""; //TODO metod som tar in en person och ett namn på en patient och returnerar på något sätt listan med records som p har access till
    }

    private String handleCommand(String msg, Person p){//DENNA ÄR LITE FEL DÅ AUTHEN>TICATOR SKA GÖRA LOGIKEN GÖR OM GÖR RÄTT
        String[] words = msg.split(" ");
        switch(words[0]){
            case "list":
                if(words[1]==p.getName() || p.isRole("Doctor") || p.isRole("Nurse") || p.isRole("Government")){
                    return getRecords(p, words[1]);    
                }
                else{
                    return "You don't have access to that.";
                }
            case "show":
                Record record = getRecord(words[1]);
                if(record==null) return "Record doesn't exist.";
                if(words[1]==p.getName() ||
                   p.isRole("Doctor") && record.getDoctor()==p.getName() ||
                   p.isRole("Nurse") && record.getNurse()==p.getName()
                  ){

                }
            break;
            case "write":
            break;
            case "delete":
            break;
            case "create":
            break;
            default:
                return "Wrong syntax";
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
            while ((clientMsg = in.readLine()) != null) {
                String response = handleCommand(clientMsg);
				out.println(rev);
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

                ks.load(new FileInputStream("../certificates/serverKS"), password);  // keystore password (storepass)
                ts.load(new FileInputStream("../certificates/serverTS"), password); // truststore password (storepass)
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
