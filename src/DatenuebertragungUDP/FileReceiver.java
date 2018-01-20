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
        byte[] inData = new byte[1024];
        byte inSeqNr = 0;
        byte inChecksumMustBe = 0;
        byte inChecksumCalc = 0;
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket(4445);
            socket.setSoTimeout(5000);

            FileOutputStream fileOutputStream = new FileOutputStream("Copy.jpg");
            DatagramPacket inPacket = new DatagramPacket(inData , inData .length);
            while (true)
            {
                socket.receive(inPacket);
                inSeqNr = inData[0];
                inChecksumMustBe = inData[1];
                for (int i = 2; i <= 1023; i++){
                    inChecksumCalc += inData[i];
                }
                fileOutputStream.write(inPacket.getData());
                inData = inPacket.getData();
                if(inSeqNr == seqNr && inChecksumCalc == inChecksumMustBe)
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
                    inChecksumCalc = 0;
                }
                else
                {
                    inChecksumCalc = 0;
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
