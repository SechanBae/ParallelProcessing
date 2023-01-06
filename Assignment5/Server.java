import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Server class that creates server
 * I, Sechan Bae, student number 000803348 ,
 * certify that all code submitted is my own work; that I have not copied it from any other source.
 * I also certify that I have not allowed my work to be copied by others.
 */
public class Server implements Runnable {

    /**
     * Socket for server
     */
    private Socket server;
    /**
     * For reading in data
     */
    private DataInputStream in;
    /**
     * For writing out data
     */
    private DataOutputStream out;
    /**
     * List of clients connected
     */
    private static LinkedBlockingQueue<DataOutputStream> clients=new LinkedBlockingQueue<>();

    /**
     * Constructor
     * @param theSocket
     * @throws IOException
     */
    public Server(Socket theSocket) throws IOException {
        server = theSocket;
    }


    /**
     * Runs server for each client, listens to client message, and send message to all clients
     */
    public void run() {
        String line = "";

        try {
            System.out.println("Just connected to " + server.getRemoteSocketAddress());

            in = new DataInputStream(server.getInputStream());
            out = new DataOutputStream(server.getOutputStream());

            //add client
            clients.put(out);

            /* Writes to all connected clients what a client has sent */
            while (true) {
                if (in.available() > 0) {  // non blocking
                    line = in.readUTF();
                    if(line.equals("exit"))
                        break;
                    for (DataOutputStream o:clients) {
                        o.writeUTF(line);
                    }
                }

            }

            // close all connections
            out.close();
            in.close();
            server.close();

        } catch (SocketTimeoutException s) {
            System.out.println("Socket timed out!");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            //remove client
            clients.remove(out);
        }

    }


    /**
     * Main method for server class
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {

        int port = 0;
        ServerSocket mySocket = new ServerSocket(port);  // Create the listening socket for client requests
        while (true) {
            System.out.println("Waiting for client on port "
                    + mySocket.getLocalPort() + "...");
            Socket server = mySocket.accept(); //blocking, awaiting a new client connection
            try {
                // new client connection recieved, spawn a thread to handle it
                Server s = new Server(server);
                Thread t = new Thread(s);
                t.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}

