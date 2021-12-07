

import Configuration.CommonPeerProperties;
import Message.Message;
import Message.MessageGroup;
import Message.Message_PayLoads.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Connection implements Runnable{

    private Peer peer;
    private Peer neighborPeer;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private ObjectInputStream clientIn;
    private ServerSocket serverSocket;
    private int piecesDownloaded;
    private PeerManager pManager;

    public Map<Integer, Double> getDownloadRate() {
        return downloadRate;
    }

    private Map<Integer, Double> downloadRate; // peer id --> download rate
    private final long start_Download = 0;
    private long stop_Download;
    private peerProcess peerProcess;


    public Connection(Peer peer, Peer neighborPeer) {
        System.out.println("In connection");
        this.peer = peer;
        this.neighborPeer = neighborPeer;
        this.downloadRate = new HashMap<Integer, Double>();
        //pManager = new PeerManager()
        this.startConnection();
    }

    public void startConnection(Connection connection) {
        try {
            //Thread sThread = new Thread(new ServerConnection(this.peer, this));
            //sThread.start();

            System.out.println("Creating a client for " + this.peer.getPeerID() + " to " + this.neighborPeer.getPeerID());
            //Socket cSocket = new Socket("localhost", 8001);
           // Socket cSocket = new Socket(this.neighborPeer.getHostName(), this.neighborPeer.getPortNumber());
            //ClientConnection newConnection = new ClientConnection(cSocket, this);
            //MessageManager m = new MessageManager(newConnection, this);
            //(new Thread(m)).start();
            Socket cSocket = new Socket(this.neighborPeer.getHostName(), this.neighborPeer.getPortNumber());
            pManager = new PeerManager(cSocket, peer);
            ClientConnection newConnection = new ClientConnection(cSocket, this);
            MessageManager m = new MessageManager(newConnection, this);
            (new Thread(m)).start();
            //(new Thread(newConnection)).start();
            //receiveMessage();

            Thread t = new Thread(connection);
            t.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(Message message) {
        try {
            out.writeObject(message);
            out.reset();
            out.flush();
        } catch (ConnectException e) {
            getNeighborPeer().setPeerUp(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void receiveMessage() {
        new Thread() {
            public void run() {

                Message receivedMsg = null;
                boolean unChoke = false;
                while (true) {
                    try {
                        receivedMsg = (Message) clientIn.readObject();
                        //MessageGroup messageGroup
                        //PayLoad messagePayload
                        //clientIn
                        System.out.println("Received message type: " + receivedMsg.getMessageGroup() + " from: " + neighborPeer.getPeerID());
                        if (receivedMsg != null) {
                            switch (receivedMsg.getMessageGroup()) {
                                case CHOKE:
                                    System.out.println("choked message");
                                    Logger.choking(neighborPeer.getPeerID());
                                    downloadRate.put(neighborPeer.getPeerID(), 0.0);
                                    break;
                                case UNCHOKE:
                                    System.out.println("unchoked message");
                                    unChoke = true;
                                    Logger.unchoking(neighborPeer.getPeerID());
                                    sendRequest();
                                    break;
                                case INTERESTED:
                                    System.out.println("interested message");
                                    pManager.addToInterestedPeers(neighborPeer);
                                    Logger.receivingInterestedMessage(neighborPeer.getPeerID());
                                    break;
                                case NOT_INTERESTED:
                                    System.out.println("not interested message");
                                    pManager.removeFromInterestedPeers(neighborPeer);
                                    Logger.receivingNotInterestedMessage(neighborPeer.getPeerID());
                                    break;
                                case HAVE:
                                    System.out.println("have message");
                                    Have_PayLoad have_payLoad = (Have_PayLoad) (receivedMsg.getMessagePayload());
                                    FileUtilities.updateBitfield(have_payLoad.getIndex(), neighborPeer.getBitField());
                                    Logger.receivingHaveMessage(neighborPeer.getPeerID(), have_payLoad.getIndex());
                                    if (!FileManager.isInteresting(have_payLoad.getIndex())) {
                                        System.out.println("Peer " + neighborPeer.getPeerID() + " contains interesting file pieces");
                                        Message interested = new Message(MessageGroup.INTERESTED, null);
                                        sendMessage(interested);
                                    }
                                    break;
                                case BITFIELD:
                                    System.out.println("bitfield message");
                                    BitField_PayLoad bitField_payLoad = (BitField_PayLoad) receivedMsg.getMessagePayload();
                                    neighborPeer.setBitField(bitField_payLoad.getBitfield());
                                    if (!FileManager.compareBitfields(bitField_payLoad.getBitfield(), peer.getBitField())) {
                                        System.out.println("Peer " + neighborPeer.getPeerID() + " does not contain any interesting file pieces");
                                        Message notInterested = new Message(MessageGroup.NOT_INTERESTED, null);
                                        sendMessage(notInterested);
                                    }else{
                                        System.out.println("Peer " + neighborPeer.getPeerID() + " contains interesting file pieces");
                                        Message interested = new Message(MessageGroup.INTERESTED, null);
                                        sendMessage(interested);
                                    }
                                    break;
                                case REQUEST:
                                    System.out.println("request message");
                                    sendMessage(new Message(MessageGroup.PIECE, new Piece_PayLoad(Objects.requireNonNull(FileManager.get(((Request_PayLoad) receivedMsg.getMessagePayload()).getIndex())).getContent(), Objects.requireNonNull(FileManager.get(((Request_PayLoad) receivedMsg.getMessagePayload()).getIndex())).getIndex())));
                                    break;
                                case PIECE:
                                    System.out.println("piece message");
                                    FileManager.store((Piece_PayLoad) receivedMsg.getMessagePayload());
                                    peer.setBitField(FileManager.getBitField());

                                    Message have = new Message(MessageGroup.HAVE, new Have_PayLoad(((Piece_PayLoad) receivedMsg.getMessagePayload()).getIndex()));
                                        Map.Entry<Integer, Peer> entry = iterator.next();
                                    for (Peer temp: StartRemotePeers.getPeerInfo()){
                                        temp.getConnection().sendMessage(have);
                                    }
                                    piecesDownloaded++;
                                    stop_Download = System.currentTimeMillis();
                                    double downloadRateT = (double) CommonPeerProperties.getPieceSize() /(stop_Download -start_Download);
                                    downloadRate.put(neighborPeer.getPeerID(), downloadRateT);

                                    Logger.downloadingAPiece(neighborPeer.getPeerID(), ((Piece_PayLoad) receivedMsg.getMessagePayload()).getIndex(), FileManager.getFilePiecesAvailableCount());
                                    if (FileManager.getFilePiecesCompletedCount() == FileManager.getFilePiecesCompletedCount()) {
                                        Logger.completionOfDownload();
                                    }
                                    if (unChoke) {
                                        sendRequest();
                                    }
                                    break;
                            }
                        }
                    } catch (SocketException e) {
                        neighborPeer.setPeerUp(false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            void sendRequest() {
                System.out.println("in send request");
                int pieceIdx = FileManager.requestPiece(neighborPeer.getBitField(), peer.getBitField(), neighborPeer.getPeerID());
                if (pieceIdx == -1) {
                    System.out.println("No more interesting pieces to request from peer " + neighborPeer.getPeerID());
                    Message not_interested = new Message(MessageGroup.NOT_INTERESTED, null);
                    sendMessage(not_interested);
                    return;
                }
                PayLoad requestPayload = new Request_PayLoad(pieceIdx);
                Message msgRequest = new Message(MessageGroup.REQUEST, requestPayload);
                try {
                    out.writeObject(msgRequest);
                    out.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }.start();
    }

    public Peer getClientPeer() {
        return peer;
    }

    public void setClientPeer(Peer clientPeer) {
        this.peer = clientPeer;
    }

    public Peer getNeighborPeer() {
        return neighborPeer;
    }

    public void setNeighborPeer(Peer neighborPeer) {
        this.neighborPeer = neighborPeer;
    }

    public void resetPiecesDownloaded() {
        piecesDownloaded = 0;
    }

    // get/set methods for ClientIn ObjectInputStream
    public ObjectInputStream getClientIn() {
        return clientIn;
    }
    public void setClientIn(ObjectInputStream clientIn) {
        this.clientIn = clientIn;
    }

    public void startServer() {
        (new Thread() {
            @Override
            public void run() {
                while(!serverSocket.isClosed()){
                    try{
                        Socket connectingSocket = serverSocket.accept();
                        ObjectOutputStream out = new ObjectOutputStream(connectingSocket.getOutputStream());
                        out.flush();
                        ObjectInputStream in = new ObjectInputStream(connectingSocket.getInputStream());

                        handshake receiving = (handshake) in.readObject();
                        int neighborID = receiving.getPeerID();

                        System.out.println("Received handshake from " + neighborID);
                        Logger.peerToPeerMakesTCPConnection(neighborID);

                        //this is where i am
                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        })
    }

    @Override
    public void run() {
        System.out.println("Starting peer " + this.getClientPeer().getPeerID());
        try {
            serverSocket = new ServerSocket(this.getClientPeer().getPortNumber());
            System.out.println("Server socket created for " + this.getClientPeer().getHostName());
    public void run() {
        receiveMessage();
        FileManager.checker();
    }

        } catch (IOException e) {
            e.printStackTrace();
        }
        startServer();
        startSender()
    }
}