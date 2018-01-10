package DatenuebertragungUDP;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import DatenuebertragungUDP.StateMachineReceiver.Msg;

public class StateMachineReceiver extends Thread {
	

    protected DatagramSocket socket = null;
    protected BufferedReader in = null;
    protected boolean moreQuotes = true;
	// all states for this FSM
	enum State {
		WAIT_FOR_CALL_FROM_BELOW, WAIT_FOR_ACK
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
        

		//stateMachine.processMsg(Msg.start_timer);



		currentState = State.WAIT_FOR_CALL_FROM_BELOW;
		// define all valid state transitions for our state machine
		// (undefined transitions will be ignored)
		transition = new Transition[State.values().length] [Msg.values().length];
		transition[State.WAIT_FOR_CALL_FROM_BELOW.ordinal()][Msg.snpkt.ordinal()] = new SndPkt();
		transition[State.WAIT_FOR_ACK.ordinal()] [Msg.start_timer.ordinal()] = new Rdt_Send();
		System.out.println("Receiver constructed, current state: "+currentState);

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
	        while (moreQuotes) {
	            try {
	                byte[] buf = new byte[256];

	                // receive request
	                DatagramPacket packet = new DatagramPacket(buf, buf.length);
	                socket.receive(packet);
	                System.out.println("packet.getData() "+ packet.getData());

	                // figure out response
	                String dString = null;
	                /*
	                if (in == null)
	                    dString = new Date().toString();
	                else
	                    dString = getNextQuote();

	                buf = dString.getBytes();
	*/
		//	 send the response to the client at "address" and "port"
	                InetAddress address = packet.getAddress();
	                int port = packet.getPort();
	                packet = new DatagramPacket(buf, buf.length, address, port);
	                socket.send(packet);
	                
	            } catch (IOException e) {
	                e.printStackTrace();
			moreQuotes = true;
	            }
	        }
	        socket.close();
			return State.WAIT_FOR_CALL_FROM_BELOW;
		}
	}
	
	class Rdt_Send extends Transition {
		@Override
		public State execute(Msg input) {
			System.out.println("rdt_send");
			return State.WAIT_FOR_ACK;
		}
	}
    public void run() {


    }
    protected String getNextQuote() {
        String returnValue = null;
        try {
            if ((returnValue = in.readLine()) == null) {
                in.close();
		moreQuotes = false;
                returnValue = "No more quotes. Goodbye.";
            }
        } catch (IOException e) {
            returnValue = "IOException occurred in server.";
        }
        return returnValue;
    }
}

