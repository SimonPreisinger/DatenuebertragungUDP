package DatenuebertragungUDP;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import DatenuebertragungUDP.StateMachineSender.Msg;

public class StateMachineReceiver extends Thread {
	

    protected DatagramSocket socket = null;
    protected BufferedReader in = null;
    protected boolean morePackages = true;
	// all states for this FSM
	enum State {
		RECEIVER_WAIT_FOR_CALL_FROM_BELOW, RECEIVER_WAIT_FOR_ACK, RECEIVER_WAIT_FOR_PACKET, RECEIVER_SEND_PACKET
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
	 */
    public StateMachineReceiver() throws IOException {
	this("StateMachineReceiver");
    }
	public StateMachineReceiver(String name) throws IOException {
        super(name);
        socket = new DatagramSocket(4445);      

		currentState = State.RECEIVER_WAIT_FOR_PACKET;
		// define all valid state transitions for our state machine
		// (undefined transitions will be ignored)
		transition = new Transition[State.values().length] [Msg.values().length];
		transition[State.RECEIVER_WAIT_FOR_CALL_FROM_BELOW.ordinal()][Msg.snpkt.ordinal()] = new SndPkt();
		transition[State.RECEIVER_WAIT_FOR_ACK.ordinal()] [Msg.start_timer.ordinal()] = new Rdt_Send();
		transition[State.RECEIVER_SEND_PACKET.ordinal()][Msg.snpkt.ordinal()] = new SndPkt();
		transition[State.RECEIVER_WAIT_FOR_PACKET.ordinal()][Msg.rcvpkt.ordinal()] = new Rdt_Send();
		System.out.println("Receiver constructed, current state: "+currentState);
		processMsg(Msg.rcvpkt);

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
		System.out.println("RECEIVER Received "+input+" in state "+currentState);
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
			System.out.println("Receiver SendPkt");
	        while (morePackages) {
	            try {
	                byte[] buf = new byte[2048];

	                // receive request
	                DatagramPacket packet = new DatagramPacket(buf, buf.length);
	                socket.receive(packet);
	                System.out.println("packet.getData() "+ packet.getData());


	                //	 send the response to the client at "address" and "port"
	                InetAddress address = packet.getAddress();
	                int port = packet.getPort();
	                packet = new DatagramPacket(buf, buf.length, address, port);
	                socket.send(packet);
	                
	            } catch (IOException e) {
	                e.printStackTrace();
			morePackages = false;
	            }
	        }
	        socket.close();
			return State.RECEIVER_WAIT_FOR_PACKET;
		}
	}
	
	class Rdt_Send extends Transition {
		@Override
		public State execute(Msg input) {
			System.out.println("Receiver ReadPkt");
			 while (morePackages) {
		            try {
		                byte[] buf = new byte[2048];

		                // receive request
		                DatagramPacket packet = new DatagramPacket(buf, buf.length);

		                	socket.receive(packet);
		                	
		                	System.out.println("packet.getData() "+ packet.getData());
		                	
		                	//	 send the response to the client at "address" and "port"
		                	InetAddress address = packet.getAddress();
		                	int port = packet.getPort();
		                	packet = new DatagramPacket(buf, buf.length, address, port);
		                	socket.send(packet);
		                	
		                
		                
		            } catch (IOException e) {
		                e.printStackTrace();
				morePackages = false;
		            }
			 }
			return State.RECEIVER_SEND_PACKET;
		}
	}
}

