import Configuration.CommonPeerProperties;
import Configuration.PeerObj;
import Configuration.PeersInformation;
//import Connections.Connection;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Vector;

public class peerProcess {
    private static Vector<PeerObj> otherPeers = new Vector<PeerObj>();


    public static void main(String[] args) {
        //args is equal to that peer's ID
        CommonPeerProperties cpp = new CommonPeerProperties();
        PeersInformation peersInformation = new PeersInformation();
        cpp.getPeerProperties();


        int peerID = Integer.parseInt(args[0]);
        PeerObj callingPeer = null;

        Vector<PeerObj> peers = new Vector<PeerObj>();
        try {
            peers = peersInformation.getPeerInformation();
            for (PeerObj peer : peers) {
                if (peerID == peer.getId()) {
                    callingPeer = peer;
                    break;
                } else {
                    otherPeers.addElement(peer);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        peerProcess p = new peerProcess();
        p.startProcess(callingPeer, otherPeers);


    }

    public void startProcess(PeerObj callingPeer, Vector<PeerObj> otherPeers) {
        //input thread here
        System.out.println("Starting the process for peer " + callingPeer.getId());
        for (PeerObj connectingPeer : otherPeers) {
             new Connection(callingPeer, connectingPeer);
             //commonconfig, map of peers, peerid
        }


    }

    public static Socket initializePeer(PeerObj callingPeer) {
        Socket clientSocket = null;
        try {
            clientSocket = new Socket(callingPeer.getPeerAddress(), callingPeer.getPeerPort());
            System.out.println("Socket created for Peer " + callingPeer.getId());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return clientSocket;
    }


}