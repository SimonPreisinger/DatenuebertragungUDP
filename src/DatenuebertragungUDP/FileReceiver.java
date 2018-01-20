package DatenuebertragungUDP;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class FileReceiver extends Thread{
    StateMachineReceiver stateMachineReceiver;

    public FileReceiver() {

        stateMachineReceiver = new StateMachineReceiver();
    }

    @Override
    public void run(){
        System.out.println("Run FileReceiver");
        byte seqNr = 0;
        byte checksum = 0;
        byte[] inData = new byte[1024];
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket(4445);
            socket.setSoTimeout(5000);

            FileOutputStream fileOutputStream = new FileOutputStream("Copy.jpg");
            DatagramPacket inPacket = new DatagramPacket(inData , inData .length);
            while (true)
            {
                socket.receive(inPacket);
                fileOutputStream.write(inPacket.getData());
                inData = inPacket.getData();
                if(inData[0] == seqNr && checksum == inData[1])
                {
                    DatagramPacket ackPacket = new DatagramPacket(new byte[]{seqNr},1, inPacket.getAddress(), inPacket.getPort());
                    socket.send(ackPacket);
                    if(inData[0] == 0){
                        stateMachineReceiver.processMsg(StateMachineReceiver.Msg.wait0ToWait1);
                    }
                    else if(inData[0] == 1)
                    {
                        stateMachineReceiver.processMsg(StateMachineReceiver.Msg.wait1ToWait0);
                    }
                    fileOutputStream.write(inData, 2, 1022);
                    seqNr = (byte) ((seqNr + 1) % 2);
                    checksum = 0;
                }
                else
                {
                    checksum = 0;
                    continue;
                }

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
