import java.io.Serializable;

public class Message implements Serializable {
    private Peer originPeer;
    private String msg;

    public Message(Peer originPeer, String msg){
        this.originPeer = originPeer;
        this.msg = msg;
    }

    public String getMsg(){
        return msg;
    }
    public Peer getOriginPeer(){
        return originPeer;
    }

}
