package DatenuebertragungUDP;

public class StateMachineSender {


	// all states for this FSM
	enum State {
		SENDER_WAIT_FOR_CALL_0_FROM_ABOVE, SENDER_WAIT_FOR_ACK0, SENDER_WAIT_FOR_CALL_1_FROM_ABOVE, SENDER_WAIT_FOR_ACK1
	};
	// all messages/conditions which can occur
	enum Msg {
		readPkt0 , readPkt1,sent0Pkt, sent1Pkt, got0Ack, got1Ack, invalidAck0, invalidAck1, timeout0, timeout1
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
		transition[State.SENDER_WAIT_FOR_CALL_0_FROM_ABOVE.ordinal()] [Msg.sent0Pkt.ordinal()] = new WAIT_FOR_ACK0();
		transition[State.SENDER_WAIT_FOR_CALL_0_FROM_ABOVE.ordinal()] [Msg.readPkt0.ordinal()] = new WAIT_FOR_CALL_0_FROM_ABOVE();

		transition[State.SENDER_WAIT_FOR_ACK0.ordinal()][Msg.got0Ack.ordinal()] = new WAIT_FOR_CALL_1_FROM_ABOVE();
		transition[State.SENDER_WAIT_FOR_ACK0.ordinal()][Msg.invalidAck0.ordinal()] = new WAIT_FOR_ACK0();
		transition[State.SENDER_WAIT_FOR_ACK0.ordinal()][Msg.timeout0.ordinal()] = new WAIT_FOR_ACK0();

		transition[State.SENDER_WAIT_FOR_CALL_1_FROM_ABOVE.ordinal()] [Msg.sent1Pkt.ordinal()] = new WAIT_FOR_ACK1();
		transition[State.SENDER_WAIT_FOR_CALL_1_FROM_ABOVE.ordinal()] [Msg.readPkt1.ordinal()] = new WAIT_FOR_CALL_1_FROM_ABOVE();

		transition[State.SENDER_WAIT_FOR_ACK1.ordinal()][Msg.got1Ack.ordinal()] = new WAIT_FOR_CALL_0_FROM_ABOVE();
		transition[State.SENDER_WAIT_FOR_ACK1.ordinal()][Msg.invalidAck1.ordinal()] = new WAIT_FOR_ACK1();
		transition[State.SENDER_WAIT_FOR_ACK1.ordinal()][Msg.timeout1.ordinal()] = new WAIT_FOR_ACK0();
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
		System.out.println("SENDER received "+input+" in state "+currentState);
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
			return State.SENDER_WAIT_FOR_CALL_0_FROM_ABOVE;
		}
	}
	class WAIT_FOR_ACK0 extends Transition {
		@Override
		public  State execute(Msg input){
			return State.SENDER_WAIT_FOR_ACK0;
		}
	}

	class WAIT_FOR_CALL_1_FROM_ABOVE extends  Transition {
		@Override
		public  State execute(Msg input){
			return  State.SENDER_WAIT_FOR_CALL_1_FROM_ABOVE;
		}
	}

	class WAIT_FOR_ACK1 extends  Transition {
		@Override
		public  State execute(Msg input){
			return  State.SENDER_WAIT_FOR_ACK1;
		}
	}
}
