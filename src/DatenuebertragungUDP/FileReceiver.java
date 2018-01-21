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
        try {
            byte seqNr = 0;
            byte[] inData = new byte[1024];
            byte inSeqNr = 0;
            byte inChecksumMustBe = 0;
            byte inChecksumCalc = 0;
            DatagramSocket socket = null;
            socket = new DatagramSocket(4445);
            socket.setSoTimeout(10000);

            FileOutputStream fileOutputStream = new FileOutputStream("Copy.rar");
            DatagramPacket inPacket = new DatagramPacket(inData , inData .length);
            while (true)
            {
                socket.receive(inPacket);
                inData = inPacket.getData();
                inSeqNr = inData[0];
                inChecksumMustBe = inData[1];
                for (int i = 2; i <= 1023; i++){
                    inChecksumCalc += inData[i];
                }
                DatagramPacket ackPacket = new DatagramPacket(new byte[]{seqNr},1, inPacket.getAddress(), inPacket.getPort());
                if(inSeqNr == seqNr && inChecksumCalc == inChecksumMustBe) //correct seqNr
                {
                    System.out.println("R got inSeqNR: " + inSeqNr);
                    ackPacket = new DatagramPacket(new byte[]{seqNr},1, inPacket.getAddress(), inPacket.getPort());
                    socket.send(ackPacket);
                    if(inSeqNr == 0){
                        stateMachineReceiver.processMsg(StateMachineReceiver.Msg.send0Ack);
                    }
                    else if(inSeqNr == 1){
                        stateMachineReceiver.processMsg(StateMachineReceiver.Msg.send1Ack);
                    }
                    fileOutputStream.write(inData, 2, 1022);
                    seqNr =  (byte) ((seqNr + 1) % 2);
                    inChecksumCalc = 0;
                }
                else if(inSeqNr != seqNr && inChecksumCalc == inChecksumMustBe) //Sender got no ACK, send ACK again
                {
                    ackPacket = new DatagramPacket(new byte[]{seqNr},1, inPacket.getAddress(), inPacket.getPort());
                    socket.send(ackPacket);
                    inChecksumCalc = 0;
                }
                else
                {
                    inChecksumCalc = 0;
                    continue;
                }

            }
        }
        catch (SocketTimeoutException e)
        {
            System.out.println("sollte fertig sein");
            e.printStackTrace();
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
