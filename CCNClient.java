
import java.io.*;
import java.net.*;

class CCNClient implements Runnable {

    private static InetAddress host;

    public void run() {
        //initializing variables
        String serverReturn;
        int i = 1, d = 0;
        String str = "";
        Integer serialNumber;
        String[] packetData = new String[11];
        Packet myPacket = null;
        
        try {
            //take user input to an array to send to server
            for (serialNumber = 1; serialNumber <= 10; serialNumber++) {
                System.out.print("Enter the data for client 1 packet #" + serialNumber + ": ");
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                packetData[serialNumber] = (String) br.readLine();
            }
            
        } catch (IOException exc) {

        }
        serialNumber = 0;
        //looping through program to send 10 packets
        for (serialNumber = 0; serialNumber <= 10;) {
            boolean timedOut = true;

            while (timedOut) {

                serialNumber++;

                try {
                    //setting up the host connection
                    host = InetAddress.getLocalHost();

                    Socket clientSocket = new Socket(host, 1234);
                    clientSocket.setSoTimeout(1000);
                    
                    //creating output and input for the client program
                    ObjectInputStream inFromServer = new ObjectInputStream(clientSocket.getInputStream());
                    ObjectOutputStream outToServer = new ObjectOutputStream(clientSocket.getOutputStream());
                    
                    //assigning the users input to the correct serial number
                    if (serialNumber < 11) {
                        str = packetData[serialNumber];
                    } else {
                        break;
                    }
                    
                    //creating and sending the packet to the server
                    myPacket = new Packet(serialNumber, str);
                    outToServer.writeObject(myPacket);
                    
                    //recieving the ack from the server
                    serverReturn = (String) inFromServer.readObject();
                    
                    //if the server sends a NACK resend packet
                    if (!serverReturn.equals("NAK")) {
                        System.out.println("FROM SERVER:Packet with SerialNo#" + serverReturn + " has been recieved");
                        timedOut = false;

                    } else {

                        System.out.println("\u001B[31m" + "Package dropped re-sending Packet with SerialNo# " + serialNumber + "\u001B[0m");
                        serialNumber--;
                    }
                    
                    //catch thrown exceptions
                } catch (SocketTimeoutException e) {
                    System.out.println("\u001B[35m" + "Timed Out SerialNo# " + serialNumber + "\u001B[0m");
                    serialNumber--;
                    break;
                } catch (IOException | ClassNotFoundException ex) {
                }
            }
        }
    }
    
    //starts client thread
    public static void main(String argv[]) throws Exception {
        CCNClient Client = new CCNClient();
        new Thread(Client).start();
    }
}
