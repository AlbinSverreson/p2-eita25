package client;
import java.net.*;
import java.io.*;
import javax.net.ssl.*;
import javax.security.cert.X509Certificate;
import java.security.KeyStore;
import java.security.cert.*;
import java.io.Console;

/*
 * This example shows how to set up a key manager to perform client
 * authentication.
 *
 * This program assumes that the client is not inside a firewall.
 * The application can be modified to connect to a server outside
 * the firewall by following SSLSocketClientWithTunneling.java.
 */
public class Client {


    public static void main(String[] args) throws Exception {
        Console cons = System.console();
        String host = null;
        int port = -1;
        System.out.println("Connecting to " + args[0] + " on port " + args[1]);
        if (args.length < 2) {
            System.out.println("USAGE: java client host port");
            System.exit(-1);
        }
        try { /* get input parameters */
            host = args[0];
            port = Integer.parseInt(args[1]);
        } catch (IllegalArgumentException e) {
            System.out.println("USAGE: java client host port");
            System.exit(-1);
        }

        try { /* set up a key manager for client authentication */
            SSLSocketFactory factory = null;
            try {
                System.out.println("Welcome to whatever");
                System.out.println("What is the name of your keystore?");
                String ksName = cons.readLine();
                System.out.println("What is the name of your truststore?");
                String tsName = cons.readLine();
                System.out.println("What is your password? ;)");
                char[] password = cons.readPassword();
                KeyStore ks = KeyStore.getInstance("JKS");
                KeyStore ts = KeyStore.getInstance("JKS");
                KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
                TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
                SSLContext ctx = SSLContext.getInstance("TLS");
                ks.load(new FileInputStream("./certificates/" + ksName), password);  // keystore password (storepass)
				ts.load(new FileInputStream("./certificates/" + tsName), password); // truststore password (storepass);
				kmf.init(ks, password); // user password (keypass)
				tmf.init(ts); // keystore can be used as truststore here
				ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
                factory = ctx.getSocketFactory();
            } catch (Exception e) {
                throw new IOException(e.getMessage());
            }
            SSLSocket socket = (SSLSocket)factory.createSocket(host, port);

            /*
             * send http request
             *
             * See SSLSocketClient.java for more information about why
             * there is a forced handshake here when using PrintWriters.
             */
            socket.startHandshake();

            SSLSession session = socket.getSession();
            X509Certificate cert = (X509Certificate)session.getPeerCertificateChain()[0];
            String subject = cert.getSubjectDN().getName();
            String issuer = cert.getIssuerDN().getName();
            String serial = cert.getSerialNumber().toString();

            System.out.println("certificate name (subject DN field) on certificate received from server:\n" + subject + "\n");
            System.out.println("Issuer: " + issuer);
            System.out.println("Certificate serial number: " + serial);
            System.out.println("Secure connection established\n\n");

            BufferedReader read = new BufferedReader(new InputStreamReader(System.in));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String msg;
			for (;;) {
                System.out.print(">");
                msg = read.readLine(); //LÄSER IN MEDDELANDE ATT SKICKA
                if (msg.equalsIgnoreCase("quit")) {
				    break;
                }
                out.println(msg);//Skickar meddelandet till servern
                out.flush();

                String answer = null;

                while(!(answer=in.readLine()).equals("")) { 
                    System.out.println(answer); //SKRIVER UT DET DEN TAR EMOT
                }
                
            }
            in.close();
			out.close();
			read.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
