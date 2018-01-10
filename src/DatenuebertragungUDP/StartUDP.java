package DatenuebertragungUDP;

import java.io.*;

public class StartUDP {
	
	public static void main(String[] args) throws IOException{


		new StateMachineReceiver().start();
		new StateMachineSender(args);

	}	
}

