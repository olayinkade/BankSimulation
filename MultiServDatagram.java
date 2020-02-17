import java.net.*;
import java.io.*;
import java.util.Hashtable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.TimeUnit;




public class MultiServDatagram implements Runnable {
    static Hashtable<String,Integer> info = new Hashtable<String,Integer>();
    DatagramPacket packet = null; 
    static DatagramSocket sock = null;  	// server's master socket
    final static Lock lock = new ReentrantLock();
    MultiServDatagram(DatagramPacket packet) {// constructor called by server for each client
    this.packet=packet;
    
    } // end constructor


    public static void main(String[] args) {// The method run when the
        // server is started from the command line

        DatagramPacket packet1 = null;	// the datagram packet
        byte [] buf = null;		// buffer for packet payload
        System.out.println("Server starting.");

        // Create main ServerSocket
        try {
            sock = new DatagramSocket(13359); 
        } 
        catch (Exception e) {
            System.err.println("Creation of Datagram Socket failed.");
            System.exit(1);
        } // end try-catch
        // Loop forever accepting client connections
        while (true) {
            buf = new byte[16];
            try {
                // lock.lock();
                packet1 = new DatagramPacket(buf, buf.length);
                sock.receive(packet1);
                System.out.println(new String(packet1.getData(), 0, packet1.getLength()));
                // lock.unlock();
                new Thread(new MultiServDatagram(packet1)).start();
                // TimeUnit.SECONDS.sleep(1);
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Accept failed.");
                System.exit(1);
            } // end try-catch
            
        } // end while - accept
    } // end main


    public void run() {// The method run by each thread when it is started
        // lock.lock();
        String clientReq = null;	
        String reply = null;
        DatagramPacket packet1 = null;
        byte [] buf = null;		
        
        clientReq = new String(packet.getData(), 0, packet.getLength());
        try {
            if(clientReq != null){
                reply = processReq(clientReq);
                buf = reply.getBytes();
                packet1 = new DatagramPacket(buf, buf.length, packet.getSocketAddress());
                sock.send(packet1);
                TimeUnit.SECONDS.sleep(1);
              
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("Socket input failed.");
            System.exit(1);
        }
        // lock.unlock();
    } // end run

    synchronized void sendResponse(DatagramPacket pack){
        try{
            sock.send(pack);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("Socket input failed.");
            System.exit(1);
        }
        
    }

    synchronized static String processReq(String req){
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
    if(amount <=0 ){ return "Invalid amount $ "+ amount + " insert";}
        if(info.containsKey(account)){
        if((int)info.get(account) >= amount){
            info.put(account, (int)info.get(account) - amount);
            message = "Withdrew " + amount + " from account number " + account + ". Current balance is $ " + info.get(account);
        }
        else{
            return "You are broke , you dont have this much $ "+ amount;
        }
        }
        else{
        message = "Account with number " + account +" doesn't exist";
        }
        return message;

    }
}

