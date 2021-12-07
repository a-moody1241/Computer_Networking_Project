import java.io.IOException;
import java.util.*;

public class ManageNeighbours implements Runnable {
    private Connection myConnection;
    private Set<Integer> preferredPeerIDSet;
    private PeerManager peerManager;

    public ManageNeighbours(Connection myConnection, PeerManager peerManager){
        this.myConnection = myConnection;
        this.preferredPeerIDSet= new TreeSet<Integer>();
        this.peerManager = peerManager;
    }

    public void run(){
        try {
            findNeighbours();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void findNeighbours() throws IOException, InterruptedException {
        //todo algorithm
        List<double[]> peerDownloadRatesList = new ArrayList<>();
        ArrayList<Peer> interestedPeers = peerManager.getInterestedPeers();
        for(Peer peer : interestedPeers){
            double[] peerIDAndRatePair = new double[2];
            peerIDAndRatePair[0] = peer.getPeerID();
            if(this.myConnection.getDownloadRate().containsKey(peer.getPeerID())){
                peerIDAndRatePair[1] = myConnection.getDownloadRate().get(peer.getPeerID());
            }else{
                peerIDAndRatePair[1] = -1;
            }
            peerDownloadRatesList.add(peerIDAndRatePair);
        }
        Collections.sort(peerDownloadRatesList, new Comparator<double[]>() {
            @Override
            public int compare(double[] rate1, double[] rate2)
            {
                if(rate1[1] > rate2[1])
                    return 1;
                else if(rate1[1] < rate2[1])
                    return -1;
                else
                    return 0;
            }
        });

                //Set<Integer> interestedNeighborsList
    }
}
