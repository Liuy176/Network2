package Network2.src;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Server implements Runnable {
    private ServerSocket serverSocket;
    private Map<String  , Socket  >  map ; // keeps track of all connections

    public Server() {
        map = new HashMap<>();
        try {
            serverSocket = new ServerSocket(8081);
            System.out.println("Server started");
        } catch (Exception e) {
            System.out.println("Error in opening server socket");
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            while (true) {
                if (serverSocket != null) {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Client connected");
                    Thread handler = new Thread(new ClientHandler(clientSocket));
                    handler.start();

                }
            }
        } catch (Exception e) {
            System.out.println("Error in server client accept method");
            e.printStackTrace();
        }
    }

    class ClientHandler implements Runnable {
        private Socket clientSocket;
        private BufferedReader in;
        private PrintWriter outSender;
        private PrintWriter outReceiver;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
            try {
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                outSender = new PrintWriter(clientSocket.getOutputStream(), true);
            } catch (IOException e) {
                // e.printStackTrace();
                System.err.println("Error in opening stream");
            }
        }

        public void run() {
            String clientMessage;
            try {

                // get the name of the client
                outSender.println("Welcome to the server ,  Please enter your name:");
                String name = in.readLine();
                map.put(name, clientSocket);
                System.out.println("Client name: " + name);

                // get the name of the user to chat with
                outSender.println("Please enter the users name you want to chat with: ");
                String receiverName = in.readLine();
                if (map.containsKey(receiverName)) {
                    outSender.println("User found , you can start chatting now");
                } else {
                    while (!map.containsKey(receiverName)) {
                        outSender.println("User not found , Please enter the users name you want to chat with: ");
                        receiverName = in.readLine();
                    }
                }

                while ((clientMessage = in.readLine()) != null) {
                    outReceiver = new PrintWriter(map.get(receiverName).getOutputStream() , true );
                    outReceiver.println("From " + name + " : " + clientMessage);
                }

                // close the connection
                in.close();
                outSender.close();
                outReceiver.close();
                clientSocket.close();
            } catch (IOException e) {
                System.out.println("Error in message reading (Client disconnected)");
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        Thread serverThread = new Thread(server);
        serverThread.start();

    }
}
