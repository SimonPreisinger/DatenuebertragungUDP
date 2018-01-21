package DatenuebertragungUDP;

import java.io.*;

public class StartUDP {
	
	public static void main(String[] args) throws IOException{
		FileReceiver fileReceiver = new FileReceiver();
		FileSender fileSender = new FileSender(args);

		fileReceiver.start();
		fileSender.start();
	}	
}

