package Network2.src;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client implements Runnable {

    private Socket socket; // socket for the client
    private BufferedReader in; // read from server
    private PrintWriter out; // write to server

    public Client() {


        try {
            // creating socket
            socket = new Socket("localhost", 8081);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            System.out.println("Connected to server");
        } catch (IOException e) {
            System.out.println("Failed to initiate server ");
        }
    } // This is the missing closing brace

    public void run() {
        try {
            //for input  to server
            Scanner scanner = new Scanner(System.in);

            //for output from server
            ServerListener serverListener = new ServerListener();
            Thread serverListenerThread = new Thread(serverListener);
            serverListenerThread.start();

            while (true) {

                String input = scanner.nextLine();
                out.println(input);
            }
        } catch (Exception e) {
            System.out.println("Error in receiving message from server");
        }
    }
    class ServerListener implements Runnable{


        public void run(){
            String serverMessage;
            try {
                while (true) {
                    serverMessage = in.readLine();
                    if (serverMessage!= null) {
                        System.out.println("Server: " + serverMessage);
                    }
                }
            } catch (Exception e) {
                System.out.println("Error in receiving message from server");
                e.printStackTrace();
            }
        }



    }

    public static void main(String[] args) {
        Client client = new Client();
        Thread clientThread = new Thread(client);
        clientThread.start();
    }
}
