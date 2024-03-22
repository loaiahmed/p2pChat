import java.io.*;
import java.net.*;


public class Peer {     // encryption missing, private messaging missing.
    private int id;
    private ServerSocket serverSocket;
    private int port;
    private volatile String targetHost;
    private volatile int targetPort;
    public static int count = 1;

    public Peer(int port, String targetHost, int targetPort) {
        this.port = port;
        this.targetHost = targetHost;
        this.targetPort = targetPort;
        this.id  = count;
        count++;
    }
    public Peer(int port){
        this.port = port;
        this.id = count;
        count++;
    }
    public int getId(){ return id;}

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
        System.out.println("Target set to: " + targetHost + ":" + targetPort);
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
                        String message = getUserInput();
                        if (message.equalsIgnoreCase("exit")) {
                            leave();    //leave ring
                            return;
                        }
                        // Send user-entered message directly (if target is set)
                        if (targetHost != null && targetPort > 0) {
                            sendMessage(message);
                        } else {
                            System.out.println("No target assigned");
                        }
                    } catch (IOException e) {
                        System.out.println(e.getMessage());
                        break;
                    }
                }
            }
        }).start();
    }
    public void leave() throws IOException{
        //cooridnator please i wanna leave;
        try {
            Message msg = new Message(this, "exit");
            Socket coordSocket = new Socket("localhost", Coordinator.DEFAULT_PORT);
            ObjectOutputStream out = new ObjectOutputStream(coordSocket.getOutputStream());
            DataInputStream in = new DataInputStream(coordSocket.getInputStream());
            out.writeObject(msg);

            String response = in.readUTF();
            if (response.equals("UNREGISTERED")) {
                System.out.println("unregistered successfully");
            } else {
                System.out.println("Unregister failed : " + response);
            }
            coordSocket.close();
            out.close();
            in.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private String getUserInput() throws IOException {
        System.out.println("Enter message to send (type 'exit' without quotes to exit): ");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        return reader.readLine();
    }

    private void sendMessage(String message) throws IOException {

        Message msg = new Message(this, message);
        Socket targetSocket = new Socket(targetHost, targetPort);
        ObjectOutputStream out = new ObjectOutputStream(targetSocket.getOutputStream());
        out.writeObject(msg);
        targetSocket.close();
        out.close();
    }
    private void relayMessage(Message message) throws IOException {
        Socket targetSocket = new Socket(targetHost, targetPort);
        ObjectOutputStream out = new ObjectOutputStream(targetSocket.getOutputStream());
        out.writeObject(message);
        targetSocket.close();
        out.close();
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

                ObjectOutputStream out = new ObjectOutputStream(cSocket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(cSocket.getInputStream());

                // Read incoming message
                Message msg = (Message) in.readObject();
                System.out.println(msg.getOriginPeer().getId() + ": " + msg.getMsg());

                if (targetHost != null && targetPort > 0 && !msg.getMsg().startsWith(id + ": ")) {
                    relayMessage(msg);
                } else if (msg.getMsg().startsWith(id + ": ")) {
                    System.out.println("Ignoring message originated from self.");
                }
                // Send a response message
                out.writeUTF("Thanks for the message!");
                out.close();
                in.close();
            } catch (IOException e) {
                System.out.println(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            } finally {
                try {
                    cSocket.close();
                } catch (IOException e) {
                    System.out.println(e);
                }
            }
        }
    }
//    public void main(String[] args) throws Exception {
//        // Replace with your desired port number and target peer details
//        int port = 8080;
//        String targetHost = "localhost";
//        int targetPort = 8081;
//
//        Peer peer = new Peer(port, targetHost, targetPort);
//        peer.start();
//    }
}
