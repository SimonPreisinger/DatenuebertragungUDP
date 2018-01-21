package DatenuebertragungUDP;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ThreadLocalRandom;


public class FileSender extends Thread {
    String filename;
    InetAddress address;
    Path filePath;
    StateMachineSender stateMachineSender;


    public FileSender(String[] args) {
        stateMachineSender = new StateMachineSender();
        this.filename = args[1];
        this.filePath = Paths.get(filename);
        try {
            this.address = InetAddress.getByName(args[0]);
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void run(){
        System.out.println("run FileSender");
        try(DatagramSocket socket = new DatagramSocket();
            FileInputStream fileInputStream = new FileInputStream(filePath.toString())) {
            stateMachineSender.processMsg(StateMachineSender.Msg.readPkt0);
            byte seqNr = 0;
            byte outChecksum = 0;
            byte[] outData = new byte[1024];
            byte[] ack = new byte[] {0};
            DatagramPacket ackPacket = new DatagramPacket(ack,ack.length);
            while(fileInputStream.read(outData,2,1022) != -1) {
                while(true) {
                    try{
                        outData[0] = seqNr;

                        for (int i = 2; i <= 1023; i++) {
                            outChecksum += outData[i];
                        }
                        outData[1] = outChecksum;
                        outChecksum = 0;
                        DatagramPacket packet = new DatagramPacket(outData,outData.length,address,4445);

                        float sendLoss = ThreadLocalRandom.current().nextFloat();

                        if( sendLoss < 0.9){ // Simulate SendPacketLoss
                            socket.send(packet);
                        }
                        if(seqNr == 0){
                            stateMachineSender.processMsg(StateMachineSender.Msg.sent0Pkt);
                        }
                        else if(seqNr == 1){
                            stateMachineSender.processMsg(StateMachineSender.Msg.sent1Pkt);
                        }

                        socket.setSoTimeout(1000);
                        socket.receive(ackPacket);

                        // got correct ACK packet
                        if(ackPacket.getData()[0] == seqNr) {
                            if(ackPacket.getData()[0] == 0){
                                 stateMachineSender.processMsg(StateMachineSender.Msg.got0Ack);
                            }
                            else if (ackPacket.getData()[0] == 1){
                                stateMachineSender.processMsg(StateMachineSender.Msg.got1Ack);
                            }
                            seqNr =  (byte) ((seqNr + 1) % 2);
                            break;
                        }
                    }
                    catch (SocketTimeoutException e) { // Receiver sent no ACK or got no Packet so send again
                        if(seqNr == 0){
                            stateMachineSender.processMsg(StateMachineSender.Msg.timeout0);
                        }
                        else if(seqNr == 1){
                            stateMachineSender.processMsg(StateMachineSender.Msg.timeout1);
                        }
                        continue;
                    }
                }
            }

        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
