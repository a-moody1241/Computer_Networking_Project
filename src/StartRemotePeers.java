import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

/*
 * The StartRemotePeers class begins remote peer processes.
 * It reads configuration file PeerInfo.cfg and starts remote peer processes.
 */
public class StartRemotePeers {

    public Vector<RemotePeerInfo> peerInfoVector;

    public void getConfiguration() {
        String st;
        int i1;
        peerInfoVector = new Vector<RemotePeerInfo>();
        try {
            BufferedReader in = new BufferedReader(new FileReader("PeerInfo.cfg"));
            while((st = in.readLine()) != null) {

                String[] tokens = st.split("\\s+");

                peerInfoVector.addElement(new RemotePeerInfo(tokens[0], tokens[1], tokens[2]));

            }

            in.close();
        }
        catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            StartRemotePeers myStart = new StartRemotePeers();
            myStart.getConfiguration();

            // get current path
            String path = System.getProperty("user.dir");

            // start clients at remote hosts
            for (int i = 0; i < myStart.peerInfoVector.size(); i++) {
                RemotePeerInfo pInfo = (RemotePeerInfo) myStart.peerInfoVector.elementAt(i);
                System.out.println("Start remote peer " + pInfo.peerId +  " at " + pInfo.peerAddress );
                Runtime.getRuntime().exec("ssh " + pInfo.peerAddress + " cd " + path + "; java peerProcess " + pInfo.peerId);
            }
            System.out.println("Starting all remote peers has done." );
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

}
