

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
import java.util.Vector;

public class Connection extends Thread{

    private Peer peer;
    private Peer neighbor;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private ObjectInputStream hostIn;
    private ServerSocket serverSocket;
    private int pieces;
    private PeerManager peerManager;
    private Vector<Peer> peers = StartRemotePeers.getPeerInfo();

    public Map<Integer, Double> getDownloadRate() {
        return downloadRate;
    }

    private Map<Integer, Double> downloadRate; // peer id --> download rate
    private final long start_Download = 0;
    private long stop_Download;
    private peerProcess peerProcess;



    public Connection(Peer peer, Peer neighbor, ObjectInputStream in, ObjectOutputStream out, Socket socket, PeerManager peerManager, int pieces) {
        System.out.println("In connection");
        this.peer = peer;
        this.neighbor = neighbor;
        this.downloadRate = new HashMap<Integer, Double>();
        this.peers = peers;
        this.peerManager = peerManager;
        this.pieces = pieces;
        try {
            if (neighbor.getPeerSocket() == null){
                this.hostIn = null;
            } else {
                this.hostIn = new ObjectInputStream(neighbor.getPeerSocket().getInputStream());
            }
        } catch (IOException e) {
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
                        receivedMsg = (Message) hostIn.readObject();
                        //MessageGroup messageGroup
                        //PayLoad messagePayload
                        //clientIn
                        System.out.println("Received message type: " + receivedMsg.getMessageGroup() + " from: " + neighbor.getPeerID());

                        if (receivedMsg != null) {
                            switch (receivedMsg.getMessageGroup()) {
                                case CHOKE:
                                    System.out.println("choked message");
                                    Logger.choking(neighbor.getPeerID());
                                    downloadRate.put(neighbor.getPeerID(), 0.0);
                                    break;
                                case UNCHOKE:
                                    System.out.println("unchoked message");
                                    unChoke = true;
                                    Logger.unchoking(neighbor.getPeerID());
                                    sendRequest();
                                    break;
                                case INTERESTED:
                                    System.out.println("interested message");
                                    peerManager.addToInterestedPeers(neighbor);
                                    Logger.receivingInterestedMessage(neighbor.getPeerID());
                                    break;
                                case NOT_INTERESTED:
                                    System.out.println("not interested message");
                                    peerManager.removeFromInterestedPeers(neighbor);
                                    Logger.receivingNotInterestedMessage(neighbor.getPeerID());
                                    break;
                                case HAVE:
                                    System.out.println("have message");
                                    Have_PayLoad have_payLoad = (Have_PayLoad) (receivedMsg.getMessagePayload());
                                    FileUtilities.updateBitfield(have_payLoad.getIndex(), neighbor.getBitField());
                                    Logger.receivingHaveMessage(neighbor.getPeerID(), have_payLoad.getIndex());
                                    if (!FileManager.isInteresting(have_payLoad.getIndex())) {
                                        System.out.println("Peer " + neighbor.getPeerID() + " contains interesting file pieces");
                                        Message interested = new Message(MessageGroup.INTERESTED, null);
                                        sendMessage(interested);
                                    }
                                    break;
                                case BITFIELD:
                                    System.out.println("bitfield message");
                                    BitField_PayLoad bitField_payLoad = (BitField_PayLoad) receivedMsg.getMessagePayload();
                                    neighbor.setBitField(bitField_payLoad.getBitfield());
                                    if (!FileManager.compareBitfields(bitField_payLoad.getBitfield(), peer.getBitField())) {
                                        System.out.println("Peer " + neighbor.getPeerID() + " does not contain any interesting file pieces");
                                        Message notInterested = new Message(MessageGroup.NOT_INTERESTED, null);
                                        sendMessage(notInterested);
                                    }else{
                                        System.out.println("Peer " + neighbor.getPeerID() + " contains interesting file pieces");
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
                                    for (Peer temp: StartRemotePeers.getPeerInfo()){
                                        temp.getConnection().sendMessage(have);
                                    }
                                    pieces++;
                                    stop_Download = System.currentTimeMillis();
                                    double downloadRateT = (double) CommonPeerProperties.getPieceSize() /(stop_Download -start_Download);
                                    downloadRate.put(neighbor.getPeerID(), downloadRateT);

                                    Logger.downloadingAPiece(neighbor.getPeerID(), ((Piece_PayLoad) receivedMsg.getMessagePayload()).getIndex(), FileManager.getFilePiecesAvailableCount());
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
                        neighbor.setPeerUp(false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            void sendRequest() {
                System.out.println("in send request");
                int pieceIdx = FileManager.requestPiece(neighbor.getBitField(), peer.getBitField(), neighbor.getPeerID());
                if (pieceIdx == -1) {
                    System.out.println("No more interesting pieces to request from peer " + neighbor.getPeerID());
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
        return neighbor;
    }

    public void setNeighborPeer(Peer neighborPeer) {
        this.neighbor = neighborPeer;
    }

    public void resetPiecesDownloaded() {
        pieces = 0;
    }

    // get/set methods for ClientIn ObjectInputStream
    public ObjectInputStream getHostIn() {
        return hostIn;
    }
    public void setClientIn(ObjectInputStream clientIn) {
        this.hostIn = clientIn;
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
                        Peer neighbor;
                        System.out.println("Received handshake from " + neighborID);
                        Logger.peerToPeerMakesTCPConnection(neighborID);

                        //creating connection not worrying about peers being interested

                        for (int i = 0; i < peers.size(); i++){
                            if (peers.get(i).getPeerID() == neighborID){
                                neighbor = peers.get(i);
                            }
                        }
                        //send bitfield message

                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    public void run() {
       receiveMessage();
       FileManager.checker();
       neighbor.setDownloadSpeed(pieces);
    }
}