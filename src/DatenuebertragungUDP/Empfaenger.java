package DatenuebertragungUDP;
import DatenuebertragungUDP.StateMachine;
import DatenuebertragungUDP.StateMachine.Msg;


public class Empfaenger {
	
	public static void main(String[] args){
		System.out.println("Hello Empf�nger");
		StateMachine stateMachine = (new StateMachine());
		stateMachine.processMsg(Msg.start_timer);
		
	}
}
