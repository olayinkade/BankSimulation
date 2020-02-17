import java.net.*;
import java.io.*;
import java.util.Hashtable;

public class MultiServ implements Runnable {
  static Hashtable<String, Integer> info = new Hashtable<String, Integer>();
  Socket cliSock = null; // socket for each client
                         // *** This is instantiated per client whenever
                         // a new Thread is created

  MultiServ(Socket csocket) {// constructor called by server for each client
    this.cliSock = csocket;
  } // end constructor

  public static void main(String[] args) {// The method run when the
    // server is started from the command line

    ServerSocket sock = null; // server's master socket
    InetAddress addr = null; // address of server
    Socket cli = null; // client socket returned from accept

    System.out.println("Server starting.");

    // Create main ServerSocket
    try {
      addr = InetAddress.getLocalHost();
      sock = new ServerSocket(13359, 3, addr); // create server socket:
    } catch (Exception e) {
      System.err.println("Creation of ServerSocket failed.");
      System.exit(1);
    } // end try-catch

    // Loop forever accepting client connections
    while (true) {
      // Accept a connection
      try {
        cli = sock.accept(); // accept a connection from client,
        System.out.println("Accepted");
        // returns socket to client
      } catch (Exception e) {
        System.err.println("Accept failed.");
        System.exit(1);
      } // end try-catch

      // Create a thread for this client (which starts our run() method
      new Thread(new MultiServ(cli)).start();
    } // end while - accept

    // Will never get here - its a server!

  } // end main

  public void run() {// The method run by each thread when it is started
    DataOutputStream strm2 = null; // stream used to write from socket
    BufferedReader strm = null;
    String clientReq = ""; // data received will be an integer

    // Socket temp = getSocket();
    Socket temp = this.cliSock;
    try {
      strm = new BufferedReader(new InputStreamReader(temp.getInputStream()));
      strm2 = new DataOutputStream(temp.getOutputStream());
    } catch (Exception e) {
      System.out.println("Couldn't create socket input stream.");
      System.exit(1);
    }
    try {

      clientReq = strm.readLine();
      if (clientReq != null) {
        System.out.println(clientReq);
        strm2.write(processReq(clientReq).getBytes());
      }
      cliSock.close();
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("Socket input failed.");
      System.exit(1);
    }

    // provide the service over the connection using cliSock (declared above).

    // close the socket
    try {
      cliSock.close();
    } catch (Exception e) {
      System.err.println("couldn't close client socket.");
      System.exit(1);
    } // end try-catch
  } // end run

  synchronized String processReq(String req) {

    if (req.charAt(0) == 'C') {
      return createAccount(req.substring(1).replaceAll("[<,>]", ""));

    } else if (req.charAt(0) == 'R') {
      return retrieveAccount(req.substring(1).replaceAll("[<,>]", ""));
    } else if (req.charAt(0) == 'D') {
      String divided[] = req.substring(1).replaceAll("[<>]", "").split(",");
      return depositeAccount(divided[0], Integer.parseInt(divided[1]));

    } else if (req.charAt(0) == 'W') {
      String divided[] = req.substring(1).replaceAll("[<>]", "").split(",");
      return withdrawAccount(divided[0], Integer.parseInt(divided[1]));
    } else {
      return "Wrong command try again";
    }

  }

  static String createAccount(String account) {
    String message = "";
    if (info.containsKey(account)) {
      message = "Account with number " + account + " already Exists";
    } else {
      info.put(account, 0);
      message = "Account with number " + account + " has been created with balance of $ 0";
    }
    return message;
  }

  static String retrieveAccount(String account) {
    String message = "";
    if (info.containsKey(account)) {
      message = "Account number " + account + " Current balance is $ " + info.get(account);
    } else {
      message = "Account number " + account + " doesn't exist";
    }
    return message;
  }

  static String depositeAccount(String account, int amount) {
    String message = "";
    if (amount <= 0) {
      return "Invalid amount $ " + amount + " inserted";
    }
    if (info.containsKey(account)) {
      info.put(account, (int) info.get(account) + amount);
      message = "Deposited " + amount + " into account number " + account + ". Current balance is $"
          + info.get(account);
    } else {
      message = "Account number " + account + " doesn't exist";
    }
    return message;

  }

  static String withdrawAccount(String account, int amount) {
    String message = "";
    if (amount <= 0) {
      return "Invalid amount $ " + amount + " insert";
    }

    if (info.containsKey(account)) {
      if ((int) info.get(account) >= amount) {
        info.put(account, (int) info.get(account) - amount);
        message = "Withdrew " + amount + " from account number " + account + ". Current balance is $ "
            + info.get(account);
      } else {
        return "You are broke , you dont have this much $ " + amount;
      }
    } else {
      message = "Account with number " + account + " doesn't exist";
    }
    return message;

  }

} // end class:MultiServ