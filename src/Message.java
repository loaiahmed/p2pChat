import java.io.Serializable;

public class Message implements Serializable {
    private int originPeerID;
    private String msg;
    private int distPeerID;

    public Message(int originPeerID, String msg){
        this.originPeerID = originPeerID;
        this.msg = msg;
    }
    public Message(int originPeerID, String msg, int distPeerID){
        this.originPeerID = originPeerID;
        this.msg = msg;
        this.distPeerID = distPeerID;
    }

    public String getMsg(){
        return msg;
    }
    public int getOriginPeerID(){
        return originPeerID;
    }
    public int getDistPeerID(){return distPeerID;}

}
