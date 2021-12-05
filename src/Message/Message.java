package Message;

import java.io.Serializable;

public class Message implements Serializable {

    private static final long serialVersionUID = 6L;

    private int messageLength;
    private MessageGroup messageGroup;

//    private PayLoad messagePayload;

    public int getMessageLength() {return messageLength;}
    public void setMessageLength(int messageLength) {this.messageLength = messageLength;}

    public MessageGroup getMessageGroup() {return messageGroup;}
    public void setMessageGroup(MessageGroup messageGroup) {this.messageGroup = messageGroup;}

//    public PayLoad getMessagePayload() {return messagePayload;}
//    public void setMessagePayload(PayLoad messagePayload) {this.messagePayload = messagePayload;}

}
