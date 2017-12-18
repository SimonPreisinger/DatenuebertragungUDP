package DatenuebertragungUDP;
import DatenuebertragungUDP.StateMachineReceiver;
import DatenuebertragungUDP.StateMachineReceiver.Msg;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.net.DatagramSocket;
import java.net.SocketException;


public class FileReceiver {
	
	public static void main(String[] args) throws SocketException{
		System.out.println("Hello FileReceiver");
		StateMachineReceiver stateMachine = (new StateMachineReceiver());
		stateMachine.processMsg(Msg.start_timer);
		DatagramSocket datagramSocket = new DatagramSocket(4445);


	}	
}

