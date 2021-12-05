package Message;

public class ReceiveMessage {
    public void receiveMessage(Message rm) {
        if (rm.getMessageGroup().equals(MessageGroup.CHOKE)){}
        else if (rm.getMessageGroup().equals(MessageGroup.UNCHOKE)){}
        else if (rm.getMessageGroup().equals(MessageGroup.INTERESTED)){}
        else if (rm.getMessageGroup().equals(MessageGroup.NOT_INTERESTED)){}
        else if (rm.getMessageGroup().equals(MessageGroup.HAVE)){}
        else if (rm.getMessageGroup().equals(MessageGroup.BITFIELD)){}
        else if (rm.getMessageGroup().equals(MessageGroup.REQUEST)){}
        else if (rm.getMessageGroup().equals(MessageGroup.PIECE)){}

    }
}
