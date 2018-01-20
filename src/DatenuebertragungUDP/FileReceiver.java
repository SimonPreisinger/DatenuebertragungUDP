package DatenuebertragungUDP;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class FileReceiver extends Thread{

    public void run(){
        byte seqNr = 0;
        byte checksum = 0;
        byte[] inData = new byte[1024];
        DatagramSocket socket = null;
        try {
            socket.setSoTimeout(5000);

            socket = new DatagramSocket(4445);
            FileOutputStream fileOutputStream = new FileOutputStream("Copy.jpg");
            DatagramPacket inPacket = new DatagramPacket(inData , inData .length);
            while (true)
            {
                socket.receive(inPacket);
                fileOutputStream.write(inPacket.getData());
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (SocketException e) {
            e.printStackTrace();
        }

        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
