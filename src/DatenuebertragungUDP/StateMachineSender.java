package DatenuebertragungUDP;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.net.DatagramPacket;
import DatenuebertragungUDP.StateMachineReceiver;
import DatenuebertragungUDP.StateMachineReceiver.Msg;
public class StateMachineSender {


	// all states for this FSM
	enum State {
		SENDER_WAIT_FOR_CALL_0_FROM_ABOVE, SENDER_WAIT_FOR_ACK0, SENDER_WAIT_FOR_CALL_1_FROM_ABOVE, SENDER_WAIT_FOR_ACK1
	};
	// all messages/conditions which can occur
	enum Msg {
		wait0ToWait0 ,wait0ToAck0, ack0ToAck0, ack0ToWait1, wait1ToWait1, wait1ToAck1	, ack1Toack1, ack1ToWait0
	}
	// current state of the FSM	
	private State currentState;
	// 2D array defining all transitions that can occur
	private Transition[][] transition;

	public StateMachineSender() {
		currentState = State.SENDER_WAIT_FOR_CALL_0_FROM_ABOVE;
		// define all valid state transitions for our state machine
		// (undefined transitions will be ignored)
		transition = new Transition[State.values().length] [Msg.values().length];
		//transition[State.SENDER_WAIT_FOR_CALL_0_FROM_ABOVE.ordinal()] [Msg.wait0ToWait0.ordinal()] = new WAIT_FOR_CALL_0_FROM_ABOVE();
		transition[State.SENDER_WAIT_FOR_CALL_0_FROM_ABOVE.ordinal()] [Msg.wait0ToAck0.ordinal()] = new WAIT_FOR_ACK0();
		//transition[State.SENDER_WAIT_FOR_ACK0.ordinal()][Msg.ack0ToAck0.ordinal()] = new WAIT_FOR_ACK0();
		transition[State.SENDER_WAIT_FOR_ACK0.ordinal()][Msg.ack0ToWait1.ordinal()] = new WAIT_FOR_CALL_1_FROM_ABOVE();
		//transition[State.SENDER_WAIT_FOR_CALL_1_FROM_ABOVE.ordinal()] [Msg.wait1ToWait1.ordinal()] = new WAIT_FOR_CALL_1_FROM_ABOVE();
		transition[State.SENDER_WAIT_FOR_CALL_1_FROM_ABOVE.ordinal()] [Msg.wait1ToAck1.ordinal()] = new WAIT_FOR_ACK1();
		//transition[State.SENDER_WAIT_FOR_ACK1.ordinal()][Msg.ack1Toack1.ordinal()] = new WAIT_FOR_ACK1();
		transition[State.SENDER_WAIT_FOR_ACK1.ordinal()][Msg.ack1ToWait0.ordinal()] = new WAIT_FOR_CALL_0_FROM_ABOVE();
		System.out.println("Sender constructed, current state: "+currentState);
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
	
	class WAIT_FOR_CALL_0_FROM_ABOVE extends Transition {
		@Override
		public State execute(Msg input) {
			System.out.println("SENDER_WAIT_FOR_CALL_0_FROM_ABOVE");
			return State.SENDER_WAIT_FOR_CALL_0_FROM_ABOVE;
		}
	}
	class WAIT_FOR_ACK0 extends Transition {
		@Override
		public  State execute(Msg input){
			System.out.println("SENDER_WAIT_FOR_ACK0");
			return State.SENDER_WAIT_FOR_ACK0;
		}
	}

	class WAIT_FOR_CALL_1_FROM_ABOVE extends  Transition {
		@Override
		public  State execute(Msg input){
			System.out.println("SENDER_WAIT_FOR_CALL_1_FROM_ABOVE");
			return  State.SENDER_WAIT_FOR_CALL_1_FROM_ABOVE;
		}
	}

	class WAIT_FOR_ACK1 extends  Transition {
		@Override
		public  State execute(Msg input){
			System.out.println("SENDER_WAIT_FOR_ACK1");
			return  State.SENDER_WAIT_FOR_ACK1;
		}
	}
}
