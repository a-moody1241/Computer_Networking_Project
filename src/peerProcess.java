
public class peerProcess {

    public static void main(String[] args){
        CommonPeerProperties cpp = new CommonPeerProperties();
        cpp.read();
        String fileName = cpp.getFileName();
        int numberOfPreferredNeighbors = cpp.getNumberOfPreferredNeighbors();
        int unchokingInterval = cpp.getUnchokingInterval();
        int optimisticUnchokingInterval = cpp.getOptimisticUnchokingInterval();
        int fileSize = cpp.getFileSize();
        int pieceSize = cpp.getPieceSize();

        int peerID = Integer.parseInt(args[0]);

    }
}
