import java.io.Serializable;

public class Message implements Serializable {
    private int originPeerID;
    private String msg;
    private Object extra;
    private int password;


    public Message(int originPeerID, String msg){
        this.originPeerID = originPeerID;
        this.msg = msg;
        this.password = -1;
    }
    public Message(int originPeerID, String msg, int password){
        this.originPeerID = originPeerID;
        this.msg = msg;
        this.password = password;
    }

    public Message(int originPeerID, String msg, Object extra) {
        this.originPeerID = originPeerID;
        this.msg = msg;
        this.extra = extra;
        this.password = password;
    }
    public Object getExtra() {
        return extra;
    }

    public void setExtra(Object extra) {
        this.extra = extra;
    }

    public String getMsg(){
        if(password == -1) {
            return msg;
        }
        if(password == 0){
            return "(whisper) " + msg;
        }
        return "Encrypted";
    }
    public int getOriginPeerID(){
        return originPeerID;
    }
    public int getPassword(){return password;}

    public void setPassword(int password) {
        this.password = password;
    }
}
