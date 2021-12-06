import Message.Message;
import Message.MessageGroup;
import Message.Message_PayLoads.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Objects;

public class Connection {

    private Peer clientPeer;
    private Peer neighborPeer;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private ObjectInputStream clientIn;
    private int piecesDownloaded;
    private PeerManager pManager;

    public Connection(Socket sport, Peer clientPeer, Peer neighborPeer, ObjectInputStream in, ObjectInputStream clientIn, ObjectOutputStream out) {
        this.clientPeer = clientPeer;
        this.neighborPeer = neighborPeer;
        this.out = out;
        this.in = in;
        this.clientIn = clientIn;

        try {
            this.clientIn = new ObjectInputStream(neighborPeer.getSocket().getInputStream());
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
                        receivedMsg = (Message) clientIn.readObject();
                        System.out.println("Received message type: " + receivedMsg.getMessageGroup() + " from: " + neighborPeer.getPeerID());
                        if (receivedMsg != null) {
                            switch (receivedMsg.getMessageGroup()) {
                                case UNCHOKE:
                                    System.out.println("unchoked message");
                                    unChoke = true;
                                    Logger.unchoking(neighborPeer.getPeerID());
                                    sendRequest();
                                    break;
                                case CHOKE:
                                    System.out.println("choked message");
                                    Logger.choking(neighborPeer.getPeerID());
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
                                case REQUEST:
                                    System.out.println("request message");
                                    sendMessage(new Message(MessageGroup.PIECE, new Piece_PayLoad(Objects.requireNonNull(FileManager.get(((Request_PayLoad) receivedMsg.getMessagePayload()).getIndex())).getContent(), Objects.requireNonNull(FileManager.get(((Request_PayLoad) receivedMsg.getMessagePayload()).getIndex())).getIndex())));
                                    break;
                                case INTERESTED:
                                    System.out.println("interested message");
                                    pManager.add(neighborPeer);
                                    Logger.receivingInterestedMessage(neighborPeer.getPeerID());
                                    break;
                                case NOT_INTERESTED:
                                    System.out.println("not interested message");
                                    pManager.remove(neighborPeer);
                                    Logger.receivingNotInterestedMessage(neighborPeer.getPeerID());
                                    break;
                                case BITFIELD:
                                    System.out.println("bitfield message");
                                    BitField_PayLoad bitField_payLoad = (BitField_PayLoad) receivedMsg.getMessagePayload();
                                    neighborPeer.setBitField(bitField_payLoad.getBitfield());
                                    if (!FileManager.compareBitfields(bitField_payLoad.getBitfield(), clientPeer.getBitField())) {
                                        System.out.println("Peer " + neighborPeer.getPeerID() + " does not contain any interesting file pieces");
                                        Message notInterested = new Message(MessageGroup.NOT_INTERESTED, null);
                                        sendMessage(notInterested);
                                        break; //todo ?? is this necessary
                                    }
                                    System.out.println("Peer " + neighborPeer.getPeerID() + " contains interesting file pieces");
                                    Message interested = new Message(MessageGroup.INTERESTED, null);
                                    sendMessage(interested);
                                    break;
                                case PIECE:
                                    System.out.println("piece message");
                                    FileManager.store((Piece_PayLoad) receivedMsg.getMessagePayload());
                                    clientPeer.setBitField(FileManager.getBitField());
                                    pManager.sendHaveAll(((Piece_PayLoad) receivedMsg.getMessagePayload()).getIndex());
                                    piecesDownloaded++;
                                    Logger.downloadingAPiece(neighborPeer.getPeerID(), ((Piece_PayLoad) receivedMsg.getMessagePayload()).getIndex(), FileManager.getNoOfPiecesAvailable());
                                    if (FileManager.getNooffilepieces() == FileManager.getNooffilepieces()) {
                                        Logger.completionOfDownload();
                                    }
                                    if (unChoke) {
                                        sendRequest();
                                    }
                                    break;
                            }
                        }
                    } catch (SocketException e){
                        neighborPeer.setPeerUp(false);
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            void sendRequest() {
                System.out.println("in send request");
                int pieceIdx = FileManager.requestPiece(neighborPeer.getBitField(),clientPeer.getBitField(),neighborPeer.getPeerID());
                if(pieceIdx == -1){
                    System.out.println("No more interesting pieces to request from peer " + neighborPeer.getPeerID());
                    Message not_interested = new Message(MessageGroup.NOT_INTERESTED, null);
                    sendMessage(not_interested);
                    return;
                }
                PayLoad requestPayload = new Request_PayLoad(pieceIdx);
                Message msgRequest = new Message(MessageGroup.REQUEST,requestPayload);
                try{
                    out.writeObject(msgRequest);
                    out.flush();
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }.start();
    }

    public Peer getClientPeer() {
        return clientPeer;
    }

    public void setClientPeer(Peer clientPeer) {
        this.clientPeer = clientPeer;
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
}