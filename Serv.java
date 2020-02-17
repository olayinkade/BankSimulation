import java.net.*;
import java.util.Hashtable;
import java.io.*;


public class Serv {

  static Hashtable<String,Integer> info = new Hashtable<String,Integer>();
  public static void main(String[] args) {

    ServerSocket sock = null;     // server's master socket
    InetAddress addr = null;      // address of server
    Socket cliSock = null;        // socket to the client
    // DataInputStream  = null;  // stream used to read from socket
    DataOutputStream strm2 = null; // stream used to write from socket
    BufferedReader strm = null;
    String clientReq = "";        // data received will be an integer

    System.out.println("Server starting.");

    // Create Socket
    try {
      addr = InetAddress.getLocalHost();
      sock = new ServerSocket(13359,3,addr); // create server socket:
    
    } catch (Exception e) {
      System.out.println("Creation of ServerSocket failed.");
      System.exit(1);
    }
    // Accept a connection
    while(true){
      try {
        cliSock = sock.accept(); // accept a connection from client
      }
      catch (Exception e) {
        System.out.println("Accept failed.");
        System.exit(1);
      }
      try {
          strm = new BufferedReader( new InputStreamReader(cliSock.getInputStream()));
          strm2 = new DataOutputStream(cliSock.getOutputStream());
      } 
      catch (Exception e) {
        System.out.println("Couldn't create socket input stream.");
        System.exit(1);
      }

      try {
        clientReq = strm.readLine();
        strm2.writeBytes(processReq(clientReq)+ "\n");
        cliSock.close();
      } 
      catch (Exception e) {
        e.printStackTrace();
        System.out.println("Socket input failed.");
        System.exit(1);
      }
    }

   
  }// main
  static String processReq(String req){

    if(req.charAt(0) == 'C'){
      return createAccount(req.substring(1).replaceAll("[<,>]", ""));

    }
    else if(req.charAt(0) == 'R'){
     return retrieveAccount(req.substring(1).replaceAll("[<,>]", ""));
    }
    else if(req.charAt(0) == 'D'){
      String divided [] = req.substring(1).replaceAll("[<>]", "").split(",");
      return depositeAccount(divided[0], Integer.parseInt(divided[1]));

    } 
    else if(req.charAt(0) == 'W'){
      String divided [] = req.substring(1).replaceAll("[<>]", "").split(",");
      return withdrawAccount(divided[0], Integer.parseInt(divided[1]));
    }
    else{
      return "Wrong command try again";
    }

  } 

  static String createAccount(String account){
    String message = "";
    if(info.containsKey(account)){
      message = "Account with number " + account +" already Exists";
    }
    else{
      info.put(account, 0);
      message = "Account with number " + account +" has been created with balance of $ 0";
    }
    return message;
  }

  static String retrieveAccount(String account){
    String message = "";
    if(info.containsKey(account)){
      message = "Account number " + account + " Current balance is $ " + info.get(account);
    }
    else{
      message = "Account number " + account +" doesn't exist";
    }
    return message;
  }

  static String depositeAccount(String account, int amount){
    String message = "";
    if(amount <=0 ){ return "Invalid amount $ "+ amount + " inserted";}
      if(info.containsKey(account)){
        info.put(account, (int)info.get(account) + amount);
        message = "Deposited " + amount + " into account number " + account + ". Current balance is $" + info.get(account);
      }
      else{
        message = "Account number " + account +" doesn't exist";
      }
      return message;
    
   
      
    
  }
   
  static String withdrawAccount(String account , int amount){
    String message = "";
    if(amount <=0 ){ return "Invalid amount $ "+ amount + " inserted";}
    if((int)info.get(account) >= amount){
      if(info.containsKey(account)){
        info.put(account, (int)info.get(account) - amount);
        message = "Withdrew " + amount + " from account number " + account + ". Current balance is $ " + info.get(account);
      }
      else{
        message = "Account with number " + account +" doesn't exist";
      }
      return message;
    }
    else{
      return "Invalid amount "+ amount + " inserted";
    }
  }
} // class:Serv
