import java.io.IOException;
import java.util.Vector;

public class peerProcess {
    private static Vector<PeerInformation> otherPeers = new Vector<PeerInformation>();


    public static void main(String[] args){
        CommonPeerProperties cpp = new CommonPeerProperties();
        PeersInformation peersInformation = new PeersInformation();
        cpp.read();
        String fileName = cpp.getFileName();
        int numberOfPreferredNeighbors = cpp.getNumberOfPreferredNeighbors();
        int unchokingInterval = cpp.getUnchokingInterval();
        int optimisticUnchokingInterval = cpp.getOptimisticUnchokingInterval();
        int fileSize = cpp.getFileSize();
        int pieceSize = cpp.getPieceSize();
        String hostName = "";
        int portNumber = 0;
        boolean filePresent = false;
        Peer firstPeer;

        int peerID = Integer.parseInt(args[0]);
        Vector<PeerInformation> peers = new Vector<PeerInformation>();
        try{
            peersInformation.read();
            peers = peersInformation.getPeerInformation();
            for (PeerInformation peer : peers){
                if (peerID == peer.getId()){
                    hostName = peer.getPeerAddress();
                    portNumber = peer.getPeerPort();
                    filePresent = peer.isHasFile();
                    firstPeer = new Peer(peerID, hostName, portNumber, filePresent);
                    firstPeerProcess(firstPeer);
                } else{
                    otherPeers.addElement(peer);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public static void firstPeerProcess(Peer firstPeer){
        //handshake with all of the peers
        while(true){
                for(PeerInformation cpeer: otherPeers){
                    //handshake file that Dylan made between firstpeer and cpeer
                }

        }

    }

}
