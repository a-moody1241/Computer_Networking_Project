import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Vector;

public class PeerConnection extends Peer implements Runnable{
    private ServerSocket serverSocket;
    private Peer peer;
    private Peer neighbor;
    private PeerManager peerManager;
    private Vector<Peer> peers = StartRemotePeers.getPeerInfo();


    public PeerConnection(Peer callingPeer) throws IOException {
        super();
        this.peer = callingPeer;
        new Logger(callingPeer.getPeerID());
        peerManager = new PeerManager(serverSocket, peer);

    }

    public void startConnection(PeerConnection peerConnection){
        Thread t = new Thread(peerConnection);
        t.start();
    }

    public void startServer(){
        (new Thread() {
            @Override
            public void run() {
                while(!serverSocket.isClosed()){
                    try{
                        Socket socket = serverSocket.accept();
                        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                        out.flush();
                        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

                        handshake receivingH = (handshake) in.readObject();
                        System.out.println("Receiving handshake from " + receivingH.getPeerID());

                        for (Peer p : peers){
                            if (p.getPeerID() == receivingH.getPeerID()){
                                neighbor = p;
                            }
                        }
                        //connectionhandler conn = new connectionhandler
                        //conn.start()
                        //sending bitfield message
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public void startSender(){
        new Thread(){
            public void run(){
                while(true) {
                    try {
                        for (Peer neighbor : peers) {
                            if (!neighbor.isPeerUp()) {
                                Socket neighborSocket = new Socket(neighbor.getHostName(), neighbor.getPortNumber());
                                ObjectOutputStream out = new ObjectOutputStream(neighborSocket.getOutputStream());
                                out.flush();
                                System.out.println("Handshake sent from peer " + getPeerID() + " to " + neighbor.getPeerID());
                                Logger.peerToPeerMakesTCPConnection(neighbor.getPeerID());
                                out.writeObject(new handshake(getPeerID()));
                                out.flush();
                                out.reset();
                                neighbor.setPeerSocket(neighborSocket);
                                neighbor.setPeerUp(true);
                            }
                        }
                        Thread.sleep(60000);
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }


    @Override
    public void run() {
        System.out.println("Starting peer " + this.peer.getPeerID());
        try {
            new FileManager(this.peer.getPeerID(), this.peer.getFilePresent());
            //this.setBitfield(filemanager.getbitfield
            serverSocket = new ServerSocket(this.peer.getPortNumber());
            System.out.println("Server socket created for peer " + this.peer.getPeerID());

        } catch (IOException e) {
            e.printStackTrace();
        }
        startServer();
        startSender();

        peerManager.start();


        //this terminates
        //CHANGE
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                try {
                    serverSocket.close();
                    for (Peer p : peers) {
                        p.getPeerSocket().close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }));
    }
}
