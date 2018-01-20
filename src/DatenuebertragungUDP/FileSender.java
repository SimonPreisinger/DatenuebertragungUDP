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
            byte seqNr = 0;
            byte checksum = 0;
            byte[] data = new byte[1024];
            byte[] ack = new byte[] {0};
            DatagramPacket ackPacket = new DatagramPacket(ack,ack.length);
            while(fileInputStream.read(data,2,1022) != -1) {
                data[0] = seqNr;

                for (int i = 2; i <= 1023; i++) {
                    checksum += data[i];
                }
                data[1] = checksum;
                checksum = 0;

                while(true) {
                    try{
                        DatagramPacket packet = new DatagramPacket(data,data.length,address,4445);
                        socket.send(packet);
                        if(seqNr == 0){
                            stateMachineSender.processMsg(StateMachineSender.Msg.wait0ToAck0);
                        }
                        else if(seqNr == 1){
                            stateMachineSender.processMsg(StateMachineSender.Msg.wait1ToAck1);
                        }

                        //if(new Random().nextInt(20) +1 == 3) { //duplicate packet
                          //  socket.send(packet);
                        //}

                        socket.setSoTimeout(200);
                        socket.receive(ackPacket);
                        if(ackPacket.getData()[0] != seqNr) {
                            continue;
                        }

                        else {
                            seqNr =  (byte) ((seqNr + 1) % 2);
                            break;
                        }
                    }
                    catch (SocketTimeoutException e) {
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
