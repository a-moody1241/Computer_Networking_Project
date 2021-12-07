import Message.Message;

import java.io.DataInputStream;
import java.io.IOException;
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
        this.sendHandshake(peerID);
        this.receiveHandshake();
        //(new Thread(clientConnection)).start();
        //this.processData();
    }

    private void sendHandshake(int peerID){
        byte[] handshakeMsg = handshake.createHandshake(peerID);
        clientConnection.send(handshakeMsg);
    }

    private void receiveHandshake() {
        try {
            clientConnection.receive(32);
            byte[] handshakeMsg = new byte[32];
            in.readFully(handshakeMsg);

            byte[] headerBytes = new byte[18];
            System.arraycopy(handshakeMsg, 0, headerBytes, 0, 18);

            byte[] peerIDBytes = new byte[4];
            System.arraycopy(handshakeMsg, 27, peerIDBytes, 0, 4);

            //logger to receive handshake
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processData() {
        //this is just receiveMessage in COnnection
    }
}