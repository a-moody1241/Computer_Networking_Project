package PayLoad;

import java.io.Serializable;

public abstract class PayLoad implements Serializable {

    private static final long serialVersionUID = 4L;


    private int messageLength = 0;

    public PayLoad() {
        super();
    }

    public PayLoad(int msgLength) {
        super();
        this.messageLength = msgLength;
    }

    public int getMessageLength() {return messageLength;}
    public void setMessageLength(int messageLength) {this.messageLength = messageLength;}

}
