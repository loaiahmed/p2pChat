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
    private LinkedList<Peer> peers;
    public static int portCount = 0;
    private static int coordId = 0;

    public Coordinator(){
        peers = new LinkedList<Peer>();
    }

    public LinkedList<Peer> getPeers() {
        return peers;
    }
    public Peer registerPeer(){     // port number is automatically assigned
        portCount+=10;
        if(peers.isEmpty()){
            Peer peer = new Peer(DEFAULT_PORT+portCount);
            peers.add(peer);
            return peer;
        }
        // connects the new peer with first peer in hashmap and last peer connects to new peer
        // and new peer becomes new last peer in Linkedlist
        Peer beforePeer = peers.getLast();
        Peer afterPeer = peers.getFirst();
        Peer peer = new Peer(DEFAULT_PORT+portCount, "localhost", afterPeer.getPort());
        beforePeer.setTarget("localhost", peer.getPort());
        peers.add(peer);
        return peer;
    }
    public String unregisterPeer(Peer peer) {
        int indexOfPeer = peers.indexOf(peer);
        if (indexOfPeer == -1) {
            return "FAILED: couldn't find peer to be deleted";
        }

        Peer previousPeer = null;
        Peer nextPeer = null;
        if (indexOfPeer > 0) {
            previousPeer = peers.get(indexOfPeer - 1);
        }
        if (indexOfPeer < peers.size() - 1) {
            nextPeer = peers.get(indexOfPeer + 1);
        }
        if (previousPeer != null) {
            previousPeer.setTarget("localhost", nextPeer != null ? nextPeer.getPort() : peers.get(0).getPort());
        }

        peers.remove(indexOfPeer);
        peer.setTarget(null, -1);

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
                        Socket peerSocket = serverSocket.accept();
                        new Thread(new Coordinator.PeerHandler(peerSocket)).start();
                    } catch (IOException e) {
                        System.out.println(e.getMessage());
                    }
                }
            }
        }).start();
        // Thread to handle sending messages to target peer
    }
    public void broadcastMessage(String message) throws IOException {
        // Iterate through all connected peers and send the message
        for (Peer peer : peers) {
            try {
                Socket peerSocket = new Socket(peer.getTargetHost(), peer.getTargetPort());
                ObjectOutputStream out = new ObjectOutputStream(peerSocket.getOutputStream());
                out.writeObject(new Message(coordId, message, savePeersIds(peers)));
                peerSocket.close();
                out.close();
            } catch (IOException e) {
                System.err.println("Error sending message to peer " + peer + ": " + e.getMessage());
            }
        }
    }
    public ArrayList<Integer> savePeersIds(LinkedList<Peer> peers){
        ArrayList<Integer> arr = new ArrayList<>();
        for (Peer peer : peers){
            arr.add(peer.getId());
        }
        return arr;
    }
    public Peer findPeerByID(int id){
        for(Peer peer : peers){
            if(peer.getId() == id){
                return peer;
            }
        }
        return null;
    }
    private class PeerHandler implements Runnable {

        private Socket cSocket;

        public PeerHandler(Socket cSocket) {
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
                System.out.println(msg.getMsg());

                if (msg.getMsg().substring(3).equals("exit")) { // 1: message
                    out.writeObject(unregisterPeer(findPeerByID(msg.getOriginPeerID())));
                    broadcastMessage(msg.getOriginPeerID() + ": has left the ring");
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
