package DatenuebertragungUDP;

import DatenuebertragungUDP.StateMachine.Msg;

public class Sender {

	public static void main(String[] args){
		System.out.println("Hello Sender");
		StateMachine stateMachine = (new StateMachine());
		stateMachine.processMsg(Msg.start_timer);
		
	}
}
