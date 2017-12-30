package DatenuebertragungUDP;
import java.net.DatagramPacket;
import java.io.*;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Timer;

import DatenuebertragungUDP.StateMachineSender.Msg;


// DataOutputSteam verwenden
// ByteArrayOutputStream.toByteArray() verwenden
// Prüfsumme berechnen: CRC32 Interface Checksum
public class FileSender {

    static BufferedReader in = null;
	public static void main(String[] args)throws IOException {
		System.out.println("Hello FileSender benötigt Argumente HostName + Dateiname");
		StateMachineSender stateMachine = (new StateMachineSender());
		stateMachine.processMsg(Msg.snpkt);
		
		
		//QuoteClient
	       if (args.length != 2 ) {
	            System.out.println("HostName oder Dateiname Argument fehlt in Run Configurations");
	            return;
	       }

	           // get a datagram socket
	       DatagramSocket socket = new DatagramSocket();

	           // send request
	       byte[] buf = new byte[256];
	       InetAddress address = InetAddress.getByName(args[0]);
	       String dateiName = (args[1]);
	       System.out.print("address = " + address + "\n");
	       System.out.print("dateiName = " + dateiName + "\n");
	       
	       // read sendFile
	        try {
	            in = new BufferedReader(new FileReader(dateiName));
	        } catch (FileNotFoundException e) {
	            System.err.println(dateiName + " existiert nicht");
	        }
	       
	        SuperPaket superPaket = new SuperPaket(buf,0, buf.length, address, 4445, dateiName);


	       DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 4445);
	       socket.send(packet);
	    
	           // get response
	       packet = new DatagramPacket(buf, buf.length);
	       socket.receive(packet);

	       // display response
	       String received = new String(packet.getData(), 0, packet.getLength());
	       System.out.println("Quote of the Moment: " + received);
	    
	       socket.close();
	   
	
	//QuoteClientEnd
		
	}
}

