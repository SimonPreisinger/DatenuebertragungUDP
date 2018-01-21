package DatenuebertragungUDP;

public class StateMachineReceiver {

	// all states for this FSM
	enum State {
		RECEIVER_WAIT_FOR_0_FROM_BELOW, RECEIVER_WAIT_FOR_1_FROM_BELOW
	};
	// all messages/conditions which can occur
	enum Msg {
		send0Ack, send1Ack, gotInvalidPkt 	}
	// current state of the FSM	
	private State currentState;
	// 2D array defining all transitions that can occur
	private Transition[][] transition;
	
	/**
	 * constructor
	 */
    public StateMachineReceiver() {
		currentState = State.RECEIVER_WAIT_FOR_0_FROM_BELOW;
		// define all valid state transitions for our state machine
		// (undefined transitions will be ignored)
		transition = new Transition[State.values().length] [Msg.values().length];
		transition[State.RECEIVER_WAIT_FOR_0_FROM_BELOW.ordinal()][Msg.send0Ack.ordinal()] = new WAIT_FOR_1_FROM_BELOW();
		transition[State.RECEIVER_WAIT_FOR_1_FROM_BELOW.ordinal()] [Msg.send1Ack.ordinal()] = new WAIT_FOR_0_FROM_BELOW();

		transition[State.RECEIVER_WAIT_FOR_0_FROM_BELOW.ordinal()][Msg.gotInvalidPkt.ordinal()] = new WAIT_FOR_1_FROM_BELOW();
		transition[State.RECEIVER_WAIT_FOR_1_FROM_BELOW.ordinal()] [Msg.gotInvalidPkt.ordinal()] = new WAIT_FOR_0_FROM_BELOW();



		System.out.println("Receiver constructed, current state: "+currentState);
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
		System.out.println("RECEIVER received "+input+" in state "+currentState);
	}
	
	/**
	 * Abstract base class for all transitions.
	 * Derived classes need to override execute thereby defining the action
	 * to be performed whenever this transition occurs.
	 */
	abstract class Transition {
		abstract public State execute(Msg input);
	}
	
	class WAIT_FOR_0_FROM_BELOW extends Transition {
		@Override
		public State execute(Msg input) {
			return State.RECEIVER_WAIT_FOR_0_FROM_BELOW;
		}
	}
	
	class WAIT_FOR_1_FROM_BELOW extends Transition {
		@Override
		public State execute(Msg input) {
			return State.RECEIVER_WAIT_FOR_1_FROM_BELOW;
		}
	}
}

