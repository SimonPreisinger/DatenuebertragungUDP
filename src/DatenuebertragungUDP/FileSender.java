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
	static int smallPacketSize = 2;
	static byte[] smallPacketsBuffer = new byte[smallPacketSize];
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

	        FileReader reader = new FileReader(dateiName);

	        Path filePath = Paths.get(dateiName);
	        
	        byte[] fileBuffer = Files.readAllBytes(filePath);
	        FileInputStream inputStream = new FileInputStream(filePath.toString());
	        File file = filePath.toFile();       
	        
	        DataInputStream dataIn = new DataInputStream(inputStream);
	        dataIn.readFully(fileBuffer = new byte[(int) file.length()]);

	        //DataOutputStream dataOut = new DataOutputStream(new FileOutputStream("JPEG_example.jpg"));
	        //dataOut.writeByte(1);
	        /*
	        ByteArrayOutputStream baos = new ByteArrayOutputStream(160);
	        System.out.println("bOutputHeader " +baos.toByteArray());
	        baos.write(address.getAddress(),0,address.getAddress().length);
	        baos.toByteArray();
*/
	        System.out.println("JPEG_example.jpg byte size: " + fileBuffer.length + " file lenght " + file.length());
	        int c = 0;
	        for (int i = 0; i<smallPacketSize; i++)
	        {
	        	smallPacketsBuffer[c] = fileBuffer[i];
	               c++;
	               if(i%smallPacketsBuffer.length-1==0){
	       	        
	                   DatagramPacket packet = new DatagramPacket(smallPacketsBuffer, smallPacketSize, address, 4445);
	                   smallPacketsBuffer = new byte[smallPacketSize];
	                   c=0;
	                   socket.send(packet);
	                   System.out.println("sent a mini-packet");
	               }
	        }
	        /*
	        byte[] header = new byte[256];
	        System.out.println("Inet4address " + address.getAddress().length);
	        int lenght = address.getAddress().length;
	        

	        DataInputStream dataIn = new DataInputStream(new FileInputStream("JPEG_example.jpg"));




	        
	       DatagramPacket packet = new DatagramPacket(bOutputHeader.toByteArray(), bOutputHeader.toByteArray().length, address, 4445);
	       socket.send(packet);
	*/    
	        
	        
	           // get response
	       DatagramPacket packet = new DatagramPacket(buf, buf.length);
	       socket.receive(packet);

	       // display response
	       String received = new String(packet.getData(), 0, packet.getLength());
	       System.out.println("Quote of the Moment: " + received);
	    
	       //socket.close();
	       
	   
	
	//QuoteClientEnd
		
	}
}

