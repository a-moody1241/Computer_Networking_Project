
import Configuration.CommonPeerProperties;
import Message.Message;
import Message.MessageGroup;
import Message.Message_PayLoads.Have_PayLoad;

import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Random;

public class PeerManager extends Thread {

    private ServerSocket socketPort;
    private Peer hostPeer;

    // Stores the peer process objects in a map
    private HashMap<Integer, Peer> peers;
    private static ArrayList<Peer> interestedPeers = new ArrayList<Peer>();
    private static ArrayList<Peer> kNeighborPeers;
    private static Peer optimizedUnchokedPeer;

    public PeerManager(ServerSocket sSocket, Peer hostPeer, HashMap<Integer, Peer> peers) {
        super();
        this.socketPort = sSocket;
        this.peers = peers;
        this.hostPeer = hostPeer;
    }

    public void setSocketPort(ServerSocket socketPort) {
        this.socketPort = socketPort;
    }

    public void add(Peer intPeers) {
        interestedPeers.add(intPeers);
    }

    public void remove(Peer intPeers) {
        interestedPeers.remove(intPeers);
    }

    public void kPreferredPeers() {

        long timeout = CommonPeerProperties.getUnchokingInterval() * 1000;
        new Thread() {
            public void run() {
                try {
                    synchronized (interestedPeers) {
                        System.out.println("Finding k preferred peers");
                        if (interestedPeers.size() != 0) {
                            kNeighborPeers = new ArrayList<Peer>();
                            if (!FileManager.hasCompleteFile()) {
                                interestedPeers.sort(new Comparator<Peer>() {
                                    Random r = new Random();

                                    @Override
                                    public int compare(Peer o1, Peer o2) {
                                        if (o1.getDownloadSpeed() == o2.getDownloadSpeed())
                                            return r.nextInt(2); // Randomly sequencing equal elements
                                        return (int) -(o1.getDownloadSpeed() - o2.getDownloadSpeed());
                                    }
                                });
                            }
                            Iterator<Peer> it = interestedPeers.iterator();
                            int indexJ = 0;
                            while (indexJ < CommonPeerProperties.getNumberOfPreferredNeighbors() && it.hasNext()) {
                                Peer p = it.next();
                                // chooses peer adds it to k preferred peers list and unchokes them
                                p.getConnection().resetPiecesDownloaded();
                                kNeighborPeers.add(p);
                                if (!p.isUnChoked())
                                    unChokePeer(p);
                                indexJ++;
                            }
                            ArrayList<Integer> preferredPeers = new ArrayList<Integer>();
                            int indexI = 0;
                            while (indexI < kNeighborPeers.size()) {
                                preferredPeers.add(kNeighborPeers.get(indexI).getPeerID());
                                indexI++;
                            }
                            Logger.changeOfPreferredNeighbors(preferredPeers);
                            chokePeers();
                        }
                    }
                    Thread.sleep(timeout);

                    while (!socketPort.isClosed()) {
                        synchronized (interestedPeers) {
                            System.out.println("Finding k preferred peers");
                            if (interestedPeers.size() != 0) {
                                kNeighborPeers = new ArrayList<Peer>();
                                // Sorts interested peers with respect to downloading rates only when host does
                                // not have the complete file
                                if (!FileManager.hasCompleteFile()) {
                                    interestedPeers.sort(new Comparator<Peer>() {
                                        Random r = new Random();

                                        @Override
                                        public int compare(Peer o1, Peer o2) {
                                            if (o1.getDownloadSpeed() == o2.getDownloadSpeed())
                                                return r.nextInt(2); // Randomly sequencing equal elements
                                            return (int) -(o1.getDownloadSpeed() - o2.getDownloadSpeed());
                                        }
                                    });
                                }
                                Iterator<Peer> it = interestedPeers.iterator();
                                int indexJ = 0;
                                while (indexJ < CommonPeerProperties.getNumberOfPreferredNeighbors() && it.hasNext()) {
                                    Peer p = it.next();
                                    // chooses peer adds it to k preferred peers list and unchokes them
                                    p.getConnection().resetPiecesDownloaded();
                                    kNeighborPeers.add(p);
                                    if (!p.isUnChoked())
                                        unChokePeer(p);
                                    indexJ++;
                                }
                                ArrayList<Integer> preferredPeers = new ArrayList<Integer>();
                                int indexI = 0;
                                while (indexI < kNeighborPeers.size()) {
                                    preferredPeers.add(kNeighborPeers.get(indexI).getPeerID());
                                    indexI++;
                                }
                                Logger.changeOfPreferredNeighbors(preferredPeers);
                                chokePeers();
                            }
                        }
                        Thread.sleep(timeout);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }

    public void unChokeOptimisticPeer() {
        // time interval in seconds to find and unchoke next optimistic peer
        long timeout = CommonPeerProperties.getOptimisticUnchokingInterval()*1000;
        new Thread() {
            public void run() {
                try {
                    synchronized (interestedPeers) {
                        System.out.println("Finding Optimistic Peer");
                        Peer p;
                        Random r = new Random();
                        Peer[] prs = interestedPeers.toArray(new Peer[interestedPeers.size()]);
                        if (interestedPeers.size() != 0) {
                            // Selects a choked interesting peer
                            p = prs[r.nextInt(prs.length)];
                            while (p.isUnChoked()) {
                                p = prs[r.nextInt(prs.length)];
                            }
                            optimizedUnchokedPeer = p;
                            unChokePeer(p);
                            Logger.unchoking(p.getPeerID());
                        }
                    }
                    Thread.sleep(timeout);

                    while (!socketPort.isClosed()) {
                        synchronized (interestedPeers) {
                            System.out.println("Finding Optimistic Peer");
                            Peer p;
                            Random r = new Random();
                            Peer[] prs = interestedPeers.toArray(new Peer[interestedPeers.size()]);
                            if (interestedPeers.size() != 0) {
                                // Selects a choked interesting peer
                                p = prs[r.nextInt(prs.length)];
                                while (p.isUnChoked()) {
                                    p = prs[r.nextInt(prs.length)];
                                }
                                optimizedUnchokedPeer = p;
                                unChokePeer(p);
                                Logger.unchoking(p.getPeerID());
                            }
                        }
                        Thread.sleep(timeout);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }

    public void unChokePeer(Peer p) {

        p.setUnChoked(true);
        // send unchoke message to peer p
        Message msgUnchoke = new Message(MessageGroup.UNCHOKE, null);
        p.getConnection().sendMessage(msgUnchoke);
        // log here or after receiving the message
        Logger.unchoking(p.getPeerID());
    }

    public void chokePeers() {
        // choke all other peers not in map kPeers
        Iterator<Entry<Integer, Peer>> itr = peers.entrySet().iterator();
        while (itr.hasNext()) {
            Entry<Integer, Peer> entry = itr.next();
            Peer temp = (Peer) entry.getValue();
            if (!kNeighborPeers.contains(temp) && temp != optimizedUnchokedPeer && temp.getConnection() != null) {
                temp.setUnChoked(false);
                Message chokeMsg = new Message(MessageGroup.CHOKE, null);
                temp.getConnection().sendMessage(chokeMsg);
                // TODO call method to stop sending data to neighbor
                Logger.choking(temp.getPeerID());
            }
        }
    }

    public void run() {
        kPreferredPeers();
        unChokeOptimisticPeer();
    }

    public void sendHaveAll(int index) {
        Message have = new Message(MessageGroup.HAVE, new Have_PayLoad(index));

        Iterator<Entry<Integer, Peer>> itr = peers.entrySet().iterator();
        while (itr.hasNext()) {
            Entry<Integer, Peer> entry = itr.next();
            Peer temp = entry.getValue();
            temp.getConnection().sendMessage(have);
        }
    }


}
