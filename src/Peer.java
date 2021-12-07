
import java.net.Socket;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Peer {
    private int peerID;
    private String hostName;
    private int portNumber;
    private boolean filePresent;
    private AtomicInteger bytesDownloadedFrom;
    private BitSet receivedParts;
    private AtomicBoolean interested;
    private long downloadSpeed; //todo change name
    private boolean peerUp;//todo change name
    private Socket socket;
    private boolean unChoked; //todo change name
    private Connection connection;


    public long getDownloadSpeed() {
        return downloadSpeed;
    }
    public void setDownloadSpeed(long downloadSpeed) {
        this.downloadSpeed = downloadSpeed;
    }
    public boolean isUnChoked() {return unChoked;}
    public void setUnChoked(boolean unChoked) {this.unChoked = unChoked;}
    public Connection getConnection() {return connection;}

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public byte[] getBitField() {
        return bitField;
    }

    public void setBitField(byte[] bitField) {
        this.bitField = bitField;
    }

    private byte[] bitField; //todo change name

    public Peer(String peerID, String hostName, String portNumber, String filePresent, AtomicInteger bytesDownloadedFrom, BitSet receivedParts){
        super();
        this.peerID = Integer.parseInt(peerID);
        this.hostName = hostName;
        this.portNumber = Integer.parseInt(portNumber);
        this.filePresent = Boolean.parseBoolean(filePresent);
        this.bytesDownloadedFrom = new AtomicInteger(0);
        this.receivedParts = new BitSet();
        this.interested = new AtomicBoolean(false);
    }

    public Peer(int peerID, String hostName, int portNumber, boolean filePresent){
        super();
        this.peerID = peerID;
        this.hostName = hostName;
        this.portNumber = portNumber;
        this.filePresent = filePresent;
        this.bytesDownloadedFrom = new AtomicInteger(0);
        this.receivedParts = new BitSet();
        this.interested = new AtomicBoolean(false);
    }

    public int getPeerID(){
        return peerID;
    }
    public void setPeerID(int peerID){
        this.peerID = peerID;
    }

    public String getHostName(){
        return hostName;
    }
    public void setHostName(String hostName){
        this.hostName = hostName;
    }

    public int getPortNumber(){
        return portNumber;
    }
    public void setPortNumber(int portNumber){
        this.portNumber = portNumber;
    }

    public boolean getFilePresent(){
        return filePresent;
    }
    public void setFilePreset(boolean filePresent){
        this.filePresent = filePresent;
    }

    public void setPeerUp(boolean peerUp) {
        this.peerUp = peerUp;
    }

    public AtomicInteger getBytesDownloadedFrom() {return bytesDownloadedFrom;}
    public void setBytesDownloadedFrom(AtomicInteger bytesDownloadedFrom) {this.bytesDownloadedFrom = bytesDownloadedFrom;}

    public BitSet getReceivedParts() {return receivedParts;}
    public void setReceivedParts(BitSet receivedParts) {
        this.receivedParts = receivedParts;
    }

    public boolean isInterested() {
        return interested.get();
    }
    public void setInterested() {
        interested.set(true);
    }
    public void setNotInterested() {
        interested.set(false);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof Peer) {
            return (((Peer) obj).peerID == (this.peerID));
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + Objects.hashCode(this.peerID);
        return hash;
    }

    @Override
    public String toString() {
        return new StringBuilder(this.peerID).append(" Address: ").append(hostName).append(" Port: ")
                .append(this.portNumber).append(" Has File: ").append(this.filePresent).toString();
    }

    public static Collection<Integer> toIdSet(Collection<Peer> peers) {
        Set<Integer> iDs = new HashSet<>();
        for (Peer peer : peers) {
            iDs.add(peer.getPeerID());
        }
        return iDs;
    }

}


