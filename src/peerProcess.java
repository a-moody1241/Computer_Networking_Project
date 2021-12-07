import Configuration.CommonPeerProperties;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Vector;

public class peerProcess {
    private static Vector<Peer> otherPeers = new Vector<Peer>();


    public static void main(String[] args) {
        CommonPeerProperties cpp = new CommonPeerProperties();
        PeersInformation peersInformation = new PeersInformation();
        cpp.getPeerProperties();


        int peerID = Integer.parseInt(args[0]);
        Peer callingPeer = null;

        Vector<Peer> peers = new Vector<>();
        try {
            peers = peersInformation.getPeerInformation();
            for (Peer peer : peers) {
                if (peerID == peer.getPeerID()) {
                    callingPeer = peer;
                    break;
                } else {
                    otherPeers.addElement(peer);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("In peerProcess: added the previous peers to a vector");
        peerProcess p = new peerProcess();
        assert callingPeer != null;
        p.startProcess(callingPeer, otherPeers);


    }

    public void startProcess(Peer callingPeer, Vector<Peer> otherPeers) {
        //input thread here
        System.out.println("Starting the process for peer " + callingPeer.getPeerID());
        for (Peer connectingPeer : otherPeers) {
             new Connection(callingPeer, connectingPeer);
             //commonconfig, map of peers, peerid
        }


    }

    public static Socket initializePeer(Peer callingPeer) {
        Socket clientSocket = null;
        try {
            clientSocket = new Socket(callingPeer.getHostName(), callingPeer.getPortNumber());
            System.out.println("Socket created for Peer " + callingPeer.getPeerID());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return clientSocket;
    }


}