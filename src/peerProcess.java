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

        System.out.println("finished cpp and pi");

        int peerID = Integer.parseInt(args[0]);
        new Logger(peerID);
        Peer callingPeer = null;

        Vector<Peer> peers;
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
        System.out.println("finished creating other elements");
        peerProcess p = new peerProcess();
        assert callingPeer != null;
        System.out.println(callingPeer.getPeerID());
        p.startProcess(callingPeer, otherPeers);
        //(ServerSocket sSocket, Peer hostPeer, HashMap<Integer, Peer> peers) {

    }

    public void startProcess(Peer callingPeer, Vector<Peer> otherPeers) throws IOException {
        //input thread here
        System.out.println("Starting the process for peer " + callingPeer.getPeerID());
        new FileManager(callingPeer.getPeerID(), callingPeer.getFilePresent());
        for (Peer connectingPeer : otherPeers) {
            Connection newConnection = new Connection(callingPeer, connectingPeer);
            connectingPeer.setConnection(newConnection);

            newConnection.startConnection(newConnection);
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