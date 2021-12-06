import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;
import Configuration.PeerObj;

/*
 * The StartRemotePeers class begins remote peer processes.
 * It reads configuration file PeerInfo.cfg and starts remote peer processes.
 */
public class StartRemotePeers {

    public Vector<PeerObj> peerInfoVector;

    public void getConfiguration() {
        String st;
        int i1;
        peerInfoVector = new Vector<PeerObj>();
        try {
            BufferedReader in = new BufferedReader(new FileReader("PeerInfo.cfg"));
            while((st = in.readLine()) != null) {

                // split peer info line into tokens
                String[] tokens = st.split("\\s+");

                // check if last token is a 1 or 0
                boolean hasFile = "1".equalsIgnoreCase(tokens[3]);

                peerInfoVector.addElement(new PeerObj(tokens[0], tokens[1], tokens[2], hasFile));
            }

            in.close();
        }
        catch (Exception ex) {
            System.out.println(ex);
        }
    }

    public static void main(String[] args) {
        try {
            StartRemotePeers myStart = new StartRemotePeers();
            myStart.getConfiguration();

            // get current path
            String path = System.getProperty("user.dir");

            // start clients at remote hosts
            for (int i = 0; i < myStart.peerInfoVector.size(); i++) {
                PeerObj pInfo = myStart.peerInfoVector.elementAt(i);
                System.out.println("Start remote peer " + pInfo.getId() +  " at " + pInfo.getPeerAddress() );
                Runtime.getRuntime().exec("ssh " + pInfo.getPeerAddress() + " cd " + path + "; java peerProcess " + pInfo.getId());
            }
            System.out.println("Starting all remote peers has completed." );
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

}
