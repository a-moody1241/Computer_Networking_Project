import Message.Message;
import Message.MessageGroup;
import Message.Message_PayLoads.BitField_PayLoad;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PipedInputStream;

public class MessageManager implements Runnable {
    private ClientConnection clientConnection;
    private Connection connection;
    private DataInputStream in;
    private int peerID;
    private int neighborID;


    public MessageManager(ClientConnection clientConnection, Connection connection) {
        try {
            this.clientConnection = clientConnection;
            this.connection = connection;
            this.peerID = connection.getClientPeer().getPeerID();
            this.in = new DataInputStream(new PipedInputStream(clientConnection.getPipedOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
        try {
            this.connection.getOut().writeObject(new handshake(peerID));
            this.receiveHandshake(this.connection.getIn());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        //this.sendHandshake(peerID);
        //this.receiveHandshake();

        try {
            BitField_PayLoad bitField_payLoad = new BitField_PayLoad(FileManager.getBitField());
            Message message = new Message(MessageGroup.BITFIELD, bitField_payLoad);
            this.connection.sendMessage(message);

            this.connection.receiveMessage(); //this is the same as processData
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static int receiveHandshake(ObjectInputStream is) throws IOException, ClassNotFoundException {

        ObjectInputStream ois = new ObjectInputStream(is);
        handshake receive = (handshake) ois.readObject();
        System.out.println("receiving handshake from " + receive.getPeerID());

        return receive.getPeerID();

    }


}