
import java.io.*;
import java.net.*;
import java.util.Random;

class CCNServer implements Runnable {

    protected int portNumber = 1234;

    public CCNServer(int port) {
        this.portNumber = port;
        System.out.println("Connection Established");
    }

    public void run() {
        //initializing variables
        Packet packet = null;
        String Data;
        String nak = "NAK";
        Packet[] storedPacket = new Packet[11];

        try {
            //creating server socket
            ServerSocket welcomeSocket = new ServerSocket(portNumber);

            while (true) {
                //accepts connection with clients and there in and output streams
                Socket connectionSocket = welcomeSocket.accept();
                ObjectOutputStream outToClient = new ObjectOutputStream(connectionSocket.getOutputStream());
                ObjectInputStream inFromClient = new ObjectInputStream(connectionSocket.getInputStream());

                packet = (Packet) inFromClient.readObject();
                //loop to store packets in an array
                for (int y = 0; y <= 10; y++) {
                    storedPacket[y] = packet;
                    int x = storedPacket[y].getSerialNo();

                }
                //create a random number to trigger time out and dropped packets
                Random random = new Random();
                int chance = random.nextInt(100);
                int chance2 = random.nextInt(200);

                int v = packet.getSerialNo();
                Integer myx = new Integer(v);
                String str = myx.toString();
                Data = packet.getData();
                //methods to drop pakcets and trigger timeout
                if (((chance % 2) == 0)) {

                    System.out.println("From Client Packet's SerialNo#" + str + " and packet's Data is " + Data);

                    if (((chance2 % 2) == 0)) {
                        Thread.sleep(1100);
                        System.out.println("\u001B[35m" + "timeout reached" + "\u001B[0m");
                    }
                    //returns an ack
                    outToClient.writeObject(str);
                } else {
                    //if the random number's modulus of 2 does not equal 0 then a packet is dropped and a nack is sent to trigger the resending of the packet
                    System.out.println("\u001B[31m" + "Packet with SerialNo#" + str + " was dropped ... resending" + "\u001B[0m");
                    outToClient.writeObject(nak);
                }

            }
            //catching exceptions
        } catch (IOException | ClassNotFoundException ex) {
        } catch (InterruptedException ex) {
        }
    }

    public static void main(String argv[]) throws Exception {
        //creates and starts threads
        CCNServer client1 = new CCNServer(1234);
        new Thread(client1).start();
        CCNServer client2 = new CCNServer(1235);
        new Thread(client2).start();
    }
}
