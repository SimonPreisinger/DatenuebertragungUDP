package DatenuebertragungUDP;


public class StateMachineSender {
	

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
	 */
	public StateMachineSender(){
		currentState = State.WAIT_FOR_CALL_FROM_ABOVE;
		// define all valid state transitions for our state machine
		// (undefined transitions will be ignored)
		transition = new Transition[State.values().length] [Msg.values().length];
		transition[State.WAIT_FOR_CALL_FROM_ABOVE.ordinal()] [Msg.snpkt.ordinal()] = new SndPkt();
		transition[State.WAIT_FOR_ACK.ordinal()] [Msg.start_timer.ordinal()] = new Rdt_Send();
		System.out.println("INFO FSM constructed, current state: "+currentState);
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
	
	void MakePaket()
	{
		
	}
}
