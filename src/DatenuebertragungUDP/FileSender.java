package DatenuebertragungUDP;
import java.util.Timer;

import DatenuebertragungUDP.StateMachineSender.Msg;

public class FileSender {

	public static void main(String[] args){
		System.out.println("Hello FileSender");
		StateMachineSender stateMachine = (new StateMachineSender());
		stateMachine.processMsg(Msg.snpkt);
		
	}
}

