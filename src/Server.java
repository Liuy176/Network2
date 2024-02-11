package Network2.src;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable {
    private ServerSocket serverSocket;

    public Server() {
        try {
            serverSocket = new ServerSocket(8081);
            System.out.println("Server started");
        } catch (Exception e) {
            System.err.println("Error in opening server socket");
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
        private PrintWriter out;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
            try {
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new PrintWriter(clientSocket.getOutputStream(), true);
            } catch (IOException e) {
                // e.printStackTrace();
                System.err.println("Error in opening stream");
            }
        }

        public void run() {
            String clientMessage;
            try {
                while ((clientMessage = in.readLine()) != null) {
                    System.out.println("From client: " + clientMessage);
                }
                in.close();
                out.close();
                clientSocket.close();
            } catch (IOException e) {
                System.out.println("Error in message reading");
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
