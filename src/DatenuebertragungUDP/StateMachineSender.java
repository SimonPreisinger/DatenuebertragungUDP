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
import DatenuebertragungUDP.StateMachineReceiver;
import DatenuebertragungUDP.StateMachineReceiver.Msg;
public class StateMachineSender {
	
	static int smallPacketSize = 2048;
	static byte[] smallPacketsBuffer = new byte[smallPacketSize];
    static BufferedReader in = null;
    static String dateiName = "";
    static InetAddress address = null;
    static DatagramSocket socket;
    static byte[] fileBuffer;
	// all states for this FSM
	enum State {
		SENDER_WAIT_FOR_CALL_FROM_ABOVE, SENDER_WAIT_FOR_ACK, SENDER_WAIT_FOR_PACKET, SENDER_SEND_PACKET
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
		currentState = State.SENDER_SEND_PACKET;
		// define all valid state transitions for our state machine
		// (undefined transitions will be ignored)
		transition = new Transition[State.values().length] [Msg.values().length];
		transition[State.SENDER_WAIT_FOR_CALL_FROM_ABOVE.ordinal()] [Msg.snpkt.ordinal()] = new SendPkt();
		transition[State.SENDER_WAIT_FOR_ACK.ordinal()] [Msg.start_timer.ordinal()] = new ReadPkt();
		transition[State.SENDER_SEND_PACKET.ordinal()] [Msg.snpkt.ordinal()] = new SendPkt();
		transition[State.SENDER_WAIT_FOR_PACKET.ordinal()][Msg.rcvpkt.ordinal()] = new ReadPkt();
		System.out.println("Sender constructed, current state: "+currentState);
	    address = InetAddress.getByName(args[0]);
	    dateiName = (args[1]);
	    System.out.print("address = " + address + "\n");
	    System.out.print("dateiName = " + dateiName + "\n");
        // get a datagram socket
	    socket = new DatagramSocket();
		 // read sendFile
	     try {
	         FileReader reader = new FileReader(dateiName);;
	     } catch (FileNotFoundException e) {
	         System.err.println(dateiName + " existiert nicht");
	     }	       	     
	     
	     Path filePath = Paths.get(dateiName);
	     
	     fileBuffer = Files.readAllBytes(filePath);
	     FileInputStream inputStream = new FileInputStream(filePath.toString());
	     File file = filePath.toFile();       
	     
	     DataInputStream dataIn = new DataInputStream(inputStream);
	     dataIn.readFully(fileBuffer = new byte[(int) file.length()]);
	     System.out.println("JPEG_example.jpg byte size: " + fileBuffer.length + " file lenght " + file.length());
	
	     processMsg(Msg.snpkt);
		    
	}
	
	/**
	 * Process a message (a condition has occurred).
	 * @param input Message or condition that has occurred.
	 */
	public void processMsg(Msg input){
		Transition trans = transition[currentState.ordinal()][input.ordinal()];
		if(trans != null){
			currentState = trans.execute(input);
		}
		System.out.println("SENDER Received "+input+" in state "+currentState);
	}
	
	/**
	 * Abstract base class for all transitions.
	 * Derived classes need to override execute thereby defining the action
	 * to be performed whenever this transition occurs.
	 */
	abstract class Transition {
		abstract public State execute(Msg input);
	}
	
	class SendPkt extends Transition {
		@Override
		public State execute(Msg input) {
			System.out.println("SndPkt");
		    // send request		   

		     int c = 0;
		     for (int i = 0; i<smallPacketSize; i++)
		     {
		     	smallPacketsBuffer[c] = fileBuffer[i];
		            c++;
		            if(i%smallPacketsBuffer.length-1==0){
		    	        
		                DatagramPacket packet = new DatagramPacket(smallPacketsBuffer, smallPacketSize, address, 4445);
		                packet = packet;
		                smallPacketsBuffer = new byte[smallPacketSize];
		                c=0;
		                try {
							socket.send(packet);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
		                System.out.println("sent a mini-packet");
		            }
		     }
			

			return State.SENDER_WAIT_FOR_PACKET;
		}
	}
	
	class ReadPkt extends Transition {
		@Override
		public State execute(Msg input) {
	        // get response
			byte[] buf = new byte[256];	    
		    DatagramPacket packet = new DatagramPacket(buf, buf.length);
		    try {
				socket.receive(packet);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	
	    // display response
	    String received = new String(packet.getData(), 0, packet.getLength());
	    System.out.println("Quote of the Moment: " + received);
			System.out.println("rdt_send");
			return State.SENDER_SEND_PACKET;
		}
	}
}
