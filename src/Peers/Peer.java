package Peers;

import java.io.*;
import java.net.*;

public class Peer {
    private ServerSocket serverSocket;
    private int port;
    private volatile String targetHost;
    private volatile int targetPort;

    public Peer(int port, String targetHost, int targetPort) {
        this.port = port;
        this.targetHost = targetHost;
        this.targetPort = targetPort;
    }

    public int getPort() {
        return port;
    }

    public String getTargetHost() {
        return targetHost;
    }

    public int getTargetPort() {
        return targetPort;
    }

    public void setTarget(String targetHost, int targetPort) {
        this.targetPort = targetPort;
        this.targetHost = targetHost;
    }

    public void start() throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.println("Peer started on port: " + port);

        // Continuously listen for incoming connections
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Socket clientSocket = serverSocket.accept();
                        new Thread(new ClientHandler(clientSocket)).start();
                    } catch (IOException e) {
                        System.out.println(e.getMessage());
                    }
                }
            }
        }).start();
        // Thread to handle sending messages to target peer
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        sendMessage(getUserInput());
                    } catch (IOException e) {
                        System.out.println(e.getMessage());
                        break;
                    }
                }
            }
        }).start();
    }

    private String getUserInput() throws IOException {
        System.out.println("Enter message to send: ");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        return reader.readLine();
    }

    private void sendMessage(String message) throws IOException {
        Socket targetSocket = new Socket(targetHost, targetPort);
        DataOutputStream out = new DataOutputStream(targetSocket.getOutputStream());
//        PrintWriter out = new PrintWriter(targetSocket.getOutputStream(), true);
//        out.println(message);
        out.writeUTF(message);
        targetSocket.close();
    }

    private class ClientHandler implements Runnable {

        private Socket cSocket;

        public ClientHandler(Socket cSocket) {
            this.cSocket = cSocket;
        }

        @Override
        public void run() {
            try {
                // Get input and output streams

                DataOutputStream out = new DataOutputStream(cSocket.getOutputStream());
                DataInputStream in = new DataInputStream(cSocket.getInputStream());

                // Read incoming message
                String message = in.readUTF();
                System.out.println("Received msg from " + cSocket.getLocalPort() +": " + message);

                // Send a response message
                out.writeUTF("Thanks for the message!");

            } catch (IOException e) {
                System.out.println(e);
            } finally {
                try {
                    cSocket.close();
                } catch (IOException e) {
                    System.out.println(e);
                }
            }
        }
    }
    public static void main(String[] args) throws Exception {
        // Replace with your desired port number and target peer details
        int port = 8080;
        String targetHost = "localhost";
        int targetPort = 8081;
        Peer peer = new Peer(port, targetHost, targetPort);
        peer.start();
    }
}
