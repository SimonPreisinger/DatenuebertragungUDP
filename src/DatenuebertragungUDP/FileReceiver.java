package DatenuebertragungUDP;

import java.io.*;

public class FileReceiver {
	
	public static void main(String[] args) throws IOException{


		new FileReceiverThread().start();

	}	
}

