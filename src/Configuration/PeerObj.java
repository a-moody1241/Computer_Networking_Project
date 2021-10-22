package Configuration;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class PeerObj {
    private String id;
    private  String address;
    private  String port;
    private  boolean hasFile;
    private AtomicInteger bytesDownloadedFrom;
    private BitSet receivedParts;
    private AtomicBoolean interested;

    public PeerObj(String peerId, String peerAddress, String peerPort, boolean hasFile, AtomicInteger bytesDownloadedFrom, BitSet receivedParts) {
        this.id = peerId;
        this.address = peerAddress;
        this.port = peerPort;
        this.hasFile = hasFile;
        this.bytesDownloadedFrom = new AtomicInteger(0);
        this.receivedParts = new BitSet();
        this.interested = new AtomicBoolean(false);
    }

    public PeerObj(int peerId) {
        this(Integer.toString(peerId), "127.0.0.1", "0", false);
    }

    public PeerObj(String id, String address, String port, boolean hasFile) {
        super();
        this.id = id;
        this.address = address;
        this.port = port;
        this.hasFile = hasFile;
        this.bytesDownloadedFrom = new AtomicInteger(0);
        this.receivedParts = new BitSet();
        this.interested = new AtomicBoolean(false);
    }

    public AtomicInteger getBytesDownloadedFrom() {return bytesDownloadedFrom;}
    public void setBytesDownloadedFrom(AtomicInteger bytesDownloadedFrom) {this.bytesDownloadedFrom = bytesDownloadedFrom;}

    public BitSet getReceivedParts() {return receivedParts;}
    public void setReceivedParts(BitSet receivedParts) {
        this.receivedParts = receivedParts;
    }

    public int getId() {
        return Integer.parseInt(id);
    }

    public String getPeerAddress() {
        return address;
    }

    public int getPeerPort() {
        return Integer.parseInt(port);
    }

    public boolean isHasFile() {
        return hasFile;
    }

    public boolean isInterested() {
        return interested.get();
    }
    public void setInterested() {
        interested.set(true);
    }
    public void setNotIterested() {
        interested.set(false);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof PeerObj) {
            return (((PeerObj) obj).id.equals(this.id));
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public String toString() {
        return new StringBuilder(this.id).append(" Address: ").append(address).append(" Port: ")
                .append(this.port).append(" Has File: ").append(this.hasFile).toString();
    }

    public static Collection<Integer> toIdSet(Collection<PeerObj> peers) {
        Set<Integer> iDs = new HashSet<>();
        for (PeerObj peer : peers) {
            iDs.add(peer.getId());
        }
        return iDs;
    }






}
