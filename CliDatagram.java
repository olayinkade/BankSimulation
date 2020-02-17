import java.net.*;
import java.io.*;

public class CliDatagram {

    public static void main(String[] args) {

	DatagramSocket sock=null;        // client's DATAGRAM socket
	DatagramPacket packet=null;	 // the datagram packet
	byte[] buf = new byte[60];	 // buffer for datagram packet payload
	String tmpStr = null;		// temporary string for inserting int
	InetAddress srvAddr=null;        // address of the server
	InputStreamReader instrm = null; // terminal input stream
	BufferedReader stdin = null;     // buffered version of instrm

	System.out.println("Client starting.");

	// create socket and address
	try {
   		srvAddr = InetAddress.getByName("owl.cs.umanitoba.ca");
   		sock = new DatagramSocket(); // create client socket
	} catch (Exception e) {
   		System.err.println("Creation of Client DataGramSocket failed.");
   		System.exit(1);
	} // end try-catch


	// set up terminal input stream
	
	try {
	    instrm = new InputStreamReader(System.in);
	    stdin = new BufferedReader(instrm);
	} catch (Exception e) {
	    System.out.println("Socket output stream failed.");
	    System.exit(1);
	}

	while (true) {
	    try {
		tmpStr = new String(stdin.readLine());
		buf=tmpStr.getBytes();
		packet = new DatagramPacket(buf, buf.length, srvAddr, 13359);
        sock.send(packet);
		byte[] buf1= new byte[100];
		packet =  new DatagramPacket(buf1, buf1.length);
              
        sock.receive(packet);
        tmpStr=new String(packet.getData(), 0, packet.getLength());
        System.out.println(tmpStr);

	    } catch (Exception e) {
		System.out.println("Terminal read or socket output failed.");
		System.exit(1);
	    }
	}


    } // main
}