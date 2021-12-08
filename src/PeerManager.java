
import Configuration.CommonPeerProperties;
import Message.Message;
import Message.MessageGroup;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class PeerManager extends Thread {

    private ServerSocket socket;
    private Peer peer;
    private Peer hostPeer;

    private Vector<Peer>  peers = StartRemotePeers.getPeerInfo();

    private static ArrayList<Peer> interestedPeers = new ArrayList<Peer>();
    private static ArrayList<Peer> kNeighborPeers;
    private static Peer optimizedUnchokedPeer;


    public PeerManager(ServerSocket socket, Peer peer){
        super();
        this.socket = socket;
        this.peer = peer;
    }


    public void setSocket(ServerSocket socket){ this.socket = socket;}
    public void addToInterestedPeers(Peer intPeers) {
        interestedPeers.add(intPeers);
    }

    public void removeFromInterestedPeers(Peer intPeers) {
        interestedPeers.remove(intPeers);
    }

    public void kPreferredPeers() {

        long timeout = CommonPeerProperties.getUnchokingInterval() * 1000L;
        new Thread() {
            public void run() {
                try {
                    synchronized (interestedPeers) {
                        System.out.println("Finding k preferred peers");
                        if (interestedPeers.size() != 0) {
                            kNeighborPeers = new ArrayList<>();
                            if (!FileManager.hasCompleteFile()) {
                                interestedPeers.sort(new Comparator<Peer>() {
                                    Random r = new Random();

                                    @Override
                                    public int compare(Peer p1, Peer p2) {
                                        if (p1.getDownloadSpeed() == p2.getDownloadSpeed())
                                            return r.nextInt(2); // Randomly sequencing equal elements
                                        return (int) -(p1.getDownloadSpeed() - p2.getDownloadSpeed());
                                    }
                                });
                            }
                            Iterator<Peer> it = interestedPeers.iterator();
                            int peersChecked = 0;
                            while (peersChecked < CommonPeerProperties.getNumberOfPreferredNeighbors() && it.hasNext()) {
                                Peer p = it.next();
                                // chooses peer adds it to k preferred peers list and unchokes them
                                p.getConnection().resetPiecesDownloaded();
                                kNeighborPeers.add(p);
                                if (!p.isUnChoked())
                                    unChokePeer(p);
                                peersChecked++;
                            }
                            ArrayList<Integer> preferredPeersID = new ArrayList<Integer>();
                            int preferredPeersChecked = 0;
                            while (preferredPeersChecked < kNeighborPeers.size()) {
                                preferredPeersID.add(kNeighborPeers.get(preferredPeersChecked).getPeerID());
                                preferredPeersChecked++;
                            }
                            Logger.changeOfPreferredNeighbors(preferredPeersID);
                            chokePeers();
                        }
                    }
                    Thread.sleep(timeout);

                    while (!socket.isClosed()) {
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

                    while (!socket.isClosed()) {
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
        Message msgUnchoke = new Message(MessageGroup.UNCHOKE, null);
        p.getConnection().sendMessage(msgUnchoke);
        Logger.unchoking(p.getPeerID());
    }

    public void chokePeers() {
        // choke all other peers not in map kPeers
        for(Peer temp : peers){
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

    public  ArrayList<Peer> getInterestedPeers() {return interestedPeers;}
    public static void setInterestedPeers(ArrayList<Peer> interestedPeers) {PeerManager.interestedPeers = interestedPeers;}

    public static ArrayList<Peer> getkNeighborPeers() {return kNeighborPeers;}
    public static void setkNeighborPeers(ArrayList<Peer> kNeighborPeers) {PeerManager.kNeighborPeers = kNeighborPeers;}

    public static Peer getOptimizedUnchokedPeer() {return optimizedUnchokedPeer;}
    public static void setOptimizedUnchokedPeer(Peer optimizedUnchokedPeer) {PeerManager.optimizedUnchokedPeer = optimizedUnchokedPeer;}



}
