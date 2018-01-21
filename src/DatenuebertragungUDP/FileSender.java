package DatenuebertragungUDP;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;


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
                        System.out.println("seqNrSent " + seqNr);
                        socket.send(packet);
                        if(seqNr == 0){
                            stateMachineSender.processMsg(StateMachineSender.Msg.sent0Pkt);
                        }
                        else if(seqNr == 1){
                            stateMachineSender.processMsg(StateMachineSender.Msg.sent1Pkt);
                        }

                        //if(new Random().nextInt(20) +1 == 3) { //duplicate packet
                          //  socket.send(packet);
                        //}

                        socket.setSoTimeout(200);
                        socket.receive(ackPacket);

                        System.out.println("S got ackPacket " + ackPacket.getData()[0]);
                        // got correct ACK packet
                        if(ackPacket.getData()[0] == seqNr) {
                            if(ackPacket.getData()[0] == 0){
                                 stateMachineSender.processMsg(StateMachineSender.Msg.got0Ack);
                            }
                            else if (ackPacket.getData()[0] == 1){
                                stateMachineSender.processMsg(StateMachineSender.Msg.got1Ack);
                            }
                            seqNr =  (byte) ((seqNr + 1) % 2);
                            continue;
                        }
                        else if(ackPacket.getData()[0] != seqNr)
                        {

                        }

                    }
                    catch (SocketTimeoutException e) { // Receiver sent no ACK or got no Packet so send again
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
