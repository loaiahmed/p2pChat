import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
public class Coordinator{
    public static final int DEFAULT_PORT = 8080;
    private ServerSocket serverSocket;
    HashMap<Integer , Peer> peers;

    public Coordinator(){
        peers = new HashMap<Integer, Peer>();
    }
    public void registerPeer(int port){
        if(peers.isEmpty()){
            Peer peer = new Peer(DEFAULT_PORT+Peer.count);
            peers.put(peer.getId(), peer);
            return;
        }
        // connects the new peer with first peer in hashmap and last peer connects to new peer
        // and new peer becomes new last peer in hashmap
        Peer beforePeer = peers.get(Peer.count-1);
        Peer afterPeer = peers.get(1);
        Peer peer = new Peer(DEFAULT_PORT+Peer.count, "localhost", afterPeer.getPort());
        beforePeer.setTarget("localhost", peer.getPort());
        peers.put(peer.getId(), peer);
    }
    public String unregisterPeer(Peer peer){
        Peer peerLeave = peers.remove(peer.getId());
        if(peerLeave == null){
            return "FAILED: couldn't find peer to be deleted";
        }
        Peer beforePeer = peers.get(peer.getId()-1);
        Peer afterPeer = peers.get((peer.getId()+1));
        beforePeer.setTarget(afterPeer.getTargetHost(), afterPeer.getTargetPort());
        return "UNREGISTERED";
    }
    public void start() throws IOException {
        serverSocket = new ServerSocket(Coordinator.DEFAULT_PORT);
        System.out.println("Coordinator started on port: " + Coordinator.DEFAULT_PORT);

        // Continuously listen for incoming connections
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Socket clientSocket = serverSocket.accept();
                        new Thread(new Coordinator.ClientHandler(clientSocket)).start();
                    } catch (IOException e) {
                        System.out.println(e.getMessage());
                    }
                }
            }
        }).start();
        // Thread to handle sending messages to target peer
    }
    public void notifyPeers(String msg){
//        for (Peer peer : peers.values()) {
//            DataOutputStream out = new DataOutputStream();
//
//        }
        return;
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

                if (msg.getMsg().equals("exit")) {
                    out.writeUTF(unregisterPeer(msg.getOriginPeer()));
                }

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
