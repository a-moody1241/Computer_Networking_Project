import Configuration.CommonPeerProperties;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Vector;

public class peerProcess {

    public static void main(String[] args) throws IOException {
        CommonPeerProperties cpp = new CommonPeerProperties();
        cpp.getPeerProperties();


        Vector<Peer> otherPeers = new Vector<Peer>();
        StartRemotePeers peersInformation = new StartRemotePeers();
        peersInformation.getConfiguration();
        peersInformation.getPeerInformation();


        int peerID = Integer.parseInt(args[0]);
        new Logger(peerID);
        Peer callingPeer = null;
        Vector<Peer> peers;
        try {
            peers = peersInformation.getPeerInformation();
            for (Peer peer : peers) {
                peer.setHostName("localhost");
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
        peerProcess p = new peerProcess();
        assert callingPeer != null;
        System.out.println(callingPeer.getPortNumber());

        PeerConnection peerConnection = new PeerConnection(callingPeer);
        peerConnection.startConnection(peerConnection);

        try{
            Thread.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }



    }



}