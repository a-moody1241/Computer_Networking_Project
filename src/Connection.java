import Message.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Connection {
    private Peer clientPeer;
    private Peer neighborPeer;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private ObjectInputStream clientIn;

    public Connection(Socket sport, Peer clientPeer, Peer neighborPeer, ObjectInputStream in, ObjectInputStream clientIn, ObjectOutputStream out){
        this.clientPeer = clientPeer;
        this.neighborPeer = neighborPeer;
        this.out = out;
        this.in = in;
        this.clientIn = clientIn;

        try{
            this.clientIn = new ObjectInputStream(neighborPeer.getSocket().getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(Message m){
        try {
            out.writeObject(m);
            out.reset();
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void receiveMessage(){
        new Thread() {
            public void run(){
                Message m = null;
                while(true){
                    try{
                        m = (Message) clientIn.readObject();
                        //Message.ReceiveMessage r = receiveMessage();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }
}
