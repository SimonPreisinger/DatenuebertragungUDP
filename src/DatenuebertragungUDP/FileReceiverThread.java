package DatenuebertragungUDP;

import DatenuebertragungUDP.StateMachineReceiver;
import DatenuebertragungUDP.StateMachineReceiver.Msg;
import java.io.*;
import java.net.*;
import java.util.*;

import DatenuebertragungUDP.StateMachineReceiver.Msg;

public class FileReceiverThread extends Thread {

    protected DatagramSocket socket = null;
    protected BufferedReader in = null;
    protected boolean moreQuotes = true;

    public FileReceiverThread() throws IOException {
	this("FileReceiverThread");
    }

    public FileReceiverThread(String name) throws IOException {
        super(name);
        socket = new DatagramSocket(4445);
        
		System.out.println("Hello FileReceiverThread");
		StateMachineReceiver stateMachine = (new StateMachineReceiver());
		stateMachine.processMsg(Msg.start_timer);

        try {
            in = new BufferedReader(new FileReader("one-liners.txt"));
        } catch (FileNotFoundException e) {
            System.err.println("Could not open quote file. Serving time instead.");
        }
    }

    public void run() {

        while (moreQuotes) {
            try {
                byte[] buf = new byte[256];

                // receive request
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);

                // figure out response
                String dString = null;
                if (in == null)
                    dString = new Date().toString();
                else
                    dString = getNextQuote();

                buf = dString.getBytes();

		// send the response to the client at "address" and "port"
                InetAddress address = packet.getAddress();
                int port = packet.getPort();
                packet = new DatagramPacket(buf, buf.length, address, port);
                socket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
		moreQuotes = false;
            }
        }
        socket.close();
    }

    protected String getNextQuote() {
        String returnValue = null;
        try {
            if ((returnValue = in.readLine()) == null) {
                in.close();
		moreQuotes = false;
                returnValue = "No more quotes. Goodbye.";
            }
        } catch (IOException e) {
            returnValue = "IOException occurred in server.";
        }
        return returnValue;
    }
}