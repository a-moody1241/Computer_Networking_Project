package Message;

public class ReceiveMessage {
    boolean unChoke = false;

    public void receiveMessage(Message rm) {
        if (rm != null) {
            switch (rm.getMessageGroup()) {
                case UNCHOKE:
                    unChoke = true; //unChokeFlag = true;
                    //Logger.peerUnchoked(neighboringPeer.getPeerId());

                    //sendRequest();
                    System.out.println("unchoked message");
                    break;
                case CHOKE:
                    System.out.println("choked message");
                    // Logger.peerChoked(neighboringPeer.getPeerId());
                    break;
                case HAVE:
                    System.out.println("have message");
                    //HavePayLoad have = (HavePayLoad) (receivedMsg.getMsgPayload());
                    //FileUtilities.updateBitfield(have.getIndex(), neighboringPeer.getBitField());
                    //Logger.haveFileMsgRecieved(neighboringPeer.getPeerId(), have.getIndex());
                    //// Check whether the piece is interesting and send interested message
                    //if (!FileManager.isInteresting(have.getIndex())) {
                    //System.out.println("Peer " + neighboringPeer.getPeerId()
                    //+ " contains interesting file pieces");
                    //Message interested = new Message(MessageType.INTERESTED, null);
                    //sendMessage(interested);
                    //}
                    break;
                case REQUEST:
                    //int requestedIndex = ((RequestPayLoad) receivedMsg.getMsgPayload()).getIndex();
                    //byte[] pieceContent = FileManager.get(requestedIndex).getContent();
                    //int pieceIndex = FileManager.get(requestedIndex).getIndex();
                    //Message pieceToSend = new Message(MessageType.PIECE,
                    // new PiecePayLoad(pieceContent, pieceIndex));
                    //sendMessage(pieceToSend);
                    System.out.println("request message");
                    break;
                case INTERESTED:
                    System.out.println("interested message");
//                    pManager.add(neighboringPeer);
//                    Logger.interestedMsgRecieved(neighboringPeer.getPeerId());
                    break;
                case BITFIELD:
                    System.out.println("bitfield message");
                    //BitFieldPayLoad in_payload = (BitFieldPayLoad) (receivedMsg.getMsgPayload());
                    //// setting bitfield for the neighboring peer
                    //neighboringPeer.setBitField(in_payload.getBitfield());
                    //if (!FileManager.compareBitfields(in_payload.getBitfield(), hostPeer.getBitField())) {
                    //System.out.println("Peer " + neighboringPeer.getPeerId()
                    //+ " does not contain any interesting file pieces");
                    //Message notInterested = new Message(MessageType.NOT_INTERESTED, null);
                    //sendMessage(notInterested);
                    //break;
                    //}
                    //System.out.println(
                    //"Peer " + neighboringPeer.getPeerId() + " contains interesting file pieces");
                    //Message interested = new Message(MessageType.INTERESTED, null);
                    //sendMessage(interested);
                    //// No need to add peers that you are interested in.
                    break;
                case PIECE:
                    System.out.println("piece message");
//                    try {
//                        FileManager.store((PiecePayLoad) receivedMsg.getMsgPayload());
//                    } catch (Exception e) {
//                        // TODO: handle exception
//                        e.printStackTrace();
//                    }
//
//                    hostPeer.setBitField(FileManager.getBitField());
//
//                    pManager.sendHaveAll(((PiecePayLoad) receivedMsg.getMsgPayload()).getIndex());
//                    piecesDownloaded++;
//                    Logger.fileDownloading(neighboringPeer.getPeerId(),
//                            ((PiecePayLoad) receivedMsg.getMsgPayload()).getIndex(),
//                            FileManager.getNoOfPiecesAvailable());
//                    if (FileManager.getNooffilepieces() == FileManager.getNoOfPiecesAvailable())
//                        Logger.fileDownloadCompleted();
//
//                    if (unChokeFlag)
//                        sendRequest();
//                    break;

            }
            if (rm.getMessageGroup().equals(MessageGroup.CHOKE)) {

            } else if (rm.getMessageGroup().equals(MessageGroup.UNCHOKE)) {

            } else if (rm.getMessageGroup().equals(MessageGroup.INTERESTED)) {

            } else if (rm.getMessageGroup().equals(MessageGroup.NOT_INTERESTED)) {

            } else if (rm.getMessageGroup().equals(MessageGroup.HAVE)) {

            } else if (rm.getMessageGroup().equals(MessageGroup.BITFIELD)) {

            } else if (rm.getMessageGroup().equals(MessageGroup.REQUEST)) {

            } else if (rm.getMessageGroup().equals(MessageGroup.PIECE)) {

            }
        }


    }
}
