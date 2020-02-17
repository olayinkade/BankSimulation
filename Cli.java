import java.net.*;
import java.io.*;

public class Cli {

  public static void main(String[] args) {
    
    Socket sock = null;              // client's socket
    InetAddress addr = null;         // addr of server (local host for now)
    DataOutputStream sockStrm = null;// stream used to write to socket
    InputStreamReader instrm = null; // terminal input stream
    BufferedReader stdin = null;     // buffered version of instrm
    BufferedReader strm = null;

    System.out.println("Client starting.");

    String currReq = "";
    do {
    // create socket
    try {
      addr = InetAddress.getByName("owl.cs.umanitoba.ca");
      sock = new Socket(addr,13359); // create client socket
      
    } catch (Exception e) {
      System.out.println("Creation of client's Socket failed.");
      System.exit(1);
    }
    System.out.println("Connected");
    // set up terminal input and socket output streams

    try {
      stdin = new BufferedReader(new InputStreamReader(System.in));
      sockStrm = new DataOutputStream(sock.getOutputStream());
       strm = new BufferedReader(new InputStreamReader(sock.getInputStream()));
    } catch (Exception e) {
      System.out.println("Socket output stream failed.");
      System.exit(1);
    }

    
      try {
        currReq = stdin.readLine();
        if(currReq.equals("E")){
          break;
        }
        sockStrm.writeBytes(currReq+"\n");
        System.out.println(strm.readLine());
      } 
      catch (Exception e) {
        System.out.println("Terminal read or socket output failed.");
        System.exit(1);
      }
    } while (true) ;

    // close the streams and  socket
    try {
      stdin.close();
      sockStrm.close();
      sock.close();
      if(!currReq.equals("E")){
        instrm.close();
      }
      
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("Client couldn't close socket.");
      System.exit(1);
    }

    System.out.println("Client finished.");

  } // main
} // class:Cli
