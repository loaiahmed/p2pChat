import java.io.*;
import java.net.*;
import java.util.LinkedList;


public class Peer implements Serializable{     // encryption missing, private messaging missing.
    private int id;
    private ServerSocket serverSocket;
    private int port;
    private volatile String targetHost;
    private volatile int targetPort;
    public static int count = 1;
    private PeerUI peerUI;

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

    public PeerUI getPeerUI() {
        return peerUI;
    }
    public void setPeerUI(PeerUI peerUI){
        this.peerUI = peerUI;
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
    }
    public boolean leave() throws IOException{
        //cooridnator please i wanna leave;
        boolean result = false;
        try {
            Message msg = new Message(this.id, "exit");
            Socket coordSocket = new Socket("localhost", Coordinator.DEFAULT_PORT);
            ObjectOutputStream out = new ObjectOutputStream(coordSocket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(coordSocket.getInputStream());
            out.writeObject(msg);

            String response = (String) in.readObject();
            if (response.equals("UNREGISTERED")) {
                System.out.println("unregistered successfully");
                peerUI.appendToTextArea("unregistered successfully\n");
                result = true;
            } else {
                System.out.println("Unregister failed : " + response);
                peerUI.appendToTextArea("unregister failed : " + response + "\n");
            }
            coordSocket.close();
            out.close();
            in.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }

    private String getUserInput() throws IOException {
        System.out.println("Enter message to send (type 'exit' without quotes to exit): ");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        return reader.readLine();
    }

    public void sendMessage(String  message) throws IOException {

        Message msg = new Message(this.id, message);
        Socket targetSocket = new Socket(targetHost, targetPort);
        ObjectOutputStream out = new ObjectOutputStream(targetSocket.getOutputStream());
        out.writeObject(msg);
        targetSocket.close();
        out.close();
    }
    public void relayMessage(Message message) throws IOException {
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
                Object msg1 = in.readObject();
                if(msg1 instanceof String){
                    peerUI.appendToTextArea((String) msg1);
                }
                else if(msg1 instanceof Message msg) {
                    System.out.println(msg.getOriginPeerID() + ": " + msg.getMsg());
                    if (msg.getOriginPeerID() == id) {
                        System.out.println("Ignoring message originated from self.");
                    } else if (targetHost != null && targetPort > 0 && !msg.getMsg().startsWith(id + ": ")) {
                        relayMessage(msg);
                        peerUI.appendToTextArea(msg.getMsg() + "\n");
                    }
                }
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
}
