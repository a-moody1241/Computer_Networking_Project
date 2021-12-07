
import sun.security.pkcs.ParsingException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

/*
    The peersInformation is done in a way that we can use more of. Therefore, we combined dylan's
    startRemotePeers and this to start peerProcess

    What this does is read the PeerInfo config file to create the vector of peers. It then calls peerProcess for each peer
 */

public class PeersInformation {

    public static final String CONFIG_FILE = "PeerInfo.cfg";
    private final Vector<Peer> peerInfo = new Vector<Peer>();


    public void getConfiguration() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(CONFIG_FILE));
        String line;
        boolean hasFile = true;
        while ((line = br.readLine()) != null) {
            line = line.trim();
            if ( (line.length() == 0)) {
                continue;
            }

            String[] items = line.split("\\s+");
            if (items.length != 4){
                throw new ParsingException(line);
            }

            if(items[3].trim().equals("0")){
                hasFile = false;
            }

            Peer peer = new Peer(Integer.parseInt(items[0].trim()), items[1].trim(), Integer.parseInt(items[2].trim()), hasFile);
            peerInfo.addElement(peer);

        }
        br.close();
    }

    public Vector<Peer> getPeerInformation() {
        return new Vector<Peer>(peerInfo);
    }

    //Dylan's stuff
    public static void main(String[] args) {
        try {
            PeersInformation myStart = new PeersInformation();
            myStart.getConfiguration();
            Vector<Peer> peerInfoVector = myStart.getPeerInformation();

            // get current path
            String path = System.getProperty("user.dir");

            // start clients at remote hosts
            for (int i = 0; i < peerInfoVector.size(); i++) {
                Peer pInfo = peerInfoVector.elementAt(i);
                System.out.println("Start remote peer " + pInfo.getPeerID() +  " at " + pInfo.getHostName() );
                Runtime.getRuntime().exec("ssh " + pInfo.getHostName() + " cd " + path + "; java peerProcess " + pInfo.getPeerID());
            }
            System.out.println("Starting all remote peers has done." );
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }
}
