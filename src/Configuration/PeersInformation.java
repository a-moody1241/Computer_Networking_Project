package Configuration;

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
    private final Vector<PeerObj> peerInfo = new Vector<PeerObj>();


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

            //PeerObj(id, address, port, hasfile)
            PeerObj peer = new PeerObj(items[0].trim(), items[1].trim(), items[2].trim(), hasFile);

            peerInfo.addElement(peer);

        }
        br.close();
    }

    public Vector<PeerObj> getPeerInformation() {
        return new Vector<PeerObj>(peerInfo);
    }

    //Dylan's stuff
    public static void main(String[] args) {
        try {
            PeersInformation myStart = new PeersInformation();
            myStart.getConfiguration();
            Vector<PeerObj> peerInfoVector = myStart.getPeerInformation();

            // get current path
            String path = System.getProperty("user.dir");

            // start clients at remote hosts
            for (int i = 0; i < peerInfoVector.size(); i++) {
                PeerObj pInfo = peerInfoVector.elementAt(i);
                System.out.println("Start remote peer " + pInfo.getId() +  " at " + pInfo.getPeerAddress() );
                Runtime.getRuntime().exec("ssh " + pInfo.getPeerAddress() + " cd " + path + "; java peerProcess " + pInfo.getId());
            }
            System.out.println("Starting all remote peers has done." );
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }
}
