package DatenuebertragungUDP;
import java.net.DatagramPacket;
import java.io.DataOutputStream;
import java.io.*;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Timer;

import DatenuebertragungUDP.StateMachineSender.Msg;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;



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

	        Path path = Paths.get("JPEG_example.jpg");
	        byte[] body = Files.readAllBytes(path);
	        System.out.println("JPEG_example.jpg byte size: " + body.length);
	        
	        byte[] header = new byte[256];
	        System.out.println("Inet4address " + address.getAddress().length);
	        int lenght = address.getAddress().length;
	        
	        DataOutputStream dataOut = new DataOutputStream(new FileOutputStream("JPEG_example.jpg"));
	        //dataOut.writeByte(1);
	        DataInputStream dataIn = new DataInputStream(new FileInputStream("JPEG_example.jpg"));

	        ByteArrayOutputStream bOutputHeader = new ByteArrayOutputStream(160);
	        System.out.println("bOutputHeader " +bOutputHeader.toByteArray());
	        bOutputHeader.write(address.getAddress(),0,address.getAddress().length);
	        bOutputHeader.toByteArray();


	        
	       DatagramPacket packet = new DatagramPacket(bOutputHeader.toByteArray(), bOutputHeader.toByteArray().length, address, 4445);
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

