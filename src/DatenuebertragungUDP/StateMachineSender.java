package DatenuebertragungUDP;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.net.DatagramPacket;

public class StateMachineSender {
	
	static int smallPacketSize = 2;
	static byte[] smallPacketsBuffer = new byte[smallPacketSize];
    static BufferedReader in = null;
    static String dateiName = "";
    static InetAddress address = null;
	// all states for this FSM
	enum State {
		WAIT_FOR_CALL_FROM_ABOVE, WAIT_FOR_ACK
	};
	// all messages/conditions which can occur
	enum Msg {
		rcvpkt, snpkt, timeout, start_timer, stop_timer	}
	// current state of the FSM	
	private State currentState;
	// 2D array defining all transitions that can occur
	private Transition[][] transition;
	
	/**
	 * constructor
	 * @throws IOException  
	 */
	public StateMachineSender(String[] args) throws IOException {
		currentState = State.WAIT_FOR_CALL_FROM_ABOVE;
		// define all valid state transitions for our state machine
		// (undefined transitions will be ignored)
		transition = new Transition[State.values().length] [Msg.values().length];
		transition[State.WAIT_FOR_CALL_FROM_ABOVE.ordinal()] [Msg.snpkt.ordinal()] = new SndPkt();
		transition[State.WAIT_FOR_ACK.ordinal()] [Msg.start_timer.ordinal()] = new Rdt_Send();
		System.out.println("Sender constructed, current state: "+currentState);
	    if (args.length != 2 ) {
	         System.out.println("HostName oder Dateiname Argument fehlt in Run Configurations");
	         return;
	    }
	    InetAddress address = InetAddress.getByName(args[0]);
	    String dateiName = (args[1]);
	    System.out.print("address = " + address + "\n");
	    System.out.print("dateiName = " + dateiName + "\n");
	}
	
	/**
	 * Process a message (a condition has occurred).
	 * @param input Message or condition that has occurred.
	 */
	public void processMsg(Msg input){
		System.out.println("INFO Received "+input+" in state "+currentState);
		Transition trans = transition[currentState.ordinal()][input.ordinal()];
		if(trans != null){
			currentState = trans.execute(input);
		}
		System.out.println("INFO State: "+currentState);
	}
	
	/**
	 * Abstract base class for all transitions.
	 * Derived classes need to override execute thereby defining the action
	 * to be performed whenever this transition occurs.
	 */
	abstract class Transition {
		abstract public State execute(Msg input);
	}
	
	class SndPkt extends Transition {
		@Override
		public State execute(Msg input) {
			System.out.println("Wait for Ack!");
			//SuperPaket superPaket = new SuperPaket("Blume.png", "DesktopPC");
			
			
			return State.WAIT_FOR_ACK;
		}
	}
	
	class Rdt_Send extends Transition {
		@Override
		public State execute(Msg input) {
			System.out.println("rdt_send");
			return State.WAIT_FOR_ACK;
		}
	}
	
	void Test(String[] args)throws IOException
	{
		//QuoteClient

	
	        // get a datagram socket
	    DatagramSocket socket = new DatagramSocket();
	
	        // send request
	    byte[] buf = new byte[256];

	    
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
	}
}
