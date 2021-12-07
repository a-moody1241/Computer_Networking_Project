
import Configuration.CommonPeerProperties;
import sun.security.pkcs.ParsingException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

/*
    The peersInformation is done in a way that we can use more of. Therefore, we combined dylan's
    startRemotePeers and this to start peerProcess

    What this does is read the PeerInfo config file to create the vector of peers. It then calls peerProcess for each peer

    Add your ssh key to each of the hosts with "ssh-copy-id your-username@lin114-00.cise.ufl.edu". This will allow us to skip the password prompt at runtime.
 */

public class StartRemotePeers {

    public static final String CONFIG_FILE = "PeerInfo.cfg";
    private static Vector<Peer> peerInfo = new Vector<Peer>();

    public static Vector<Peer> getPeerInfo(){
        return peerInfo;
    }

    public void getConfiguration() {
        try {
            BufferedReader br = new BufferedReader(new FileReader(CONFIG_FILE));
            String line;
            boolean hasFile = true;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if ((line.length() == 0)) {
                    continue;
                }

                String[] items = line.split("\\s+");
                if (items.length != 4) {
                    throw new ParsingException(line);
                }

                if (items[3].trim().equals("0")) {
                    hasFile = false;
                }

                Peer peer = new Peer(Integer.parseInt(items[0].trim()), items[1].trim(), Integer.parseInt(items[2].trim()), hasFile);
                peerInfo.addElement(peer);

            }
            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (ParsingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("We just finished reading the peerInfo config file");
    }

    public Vector<Peer> getPeerInformation() {
        return new Vector<Peer>(peerInfo);
    }

    //Dylan's stuff
    public static void main(String[] args) {
        System.out.println("Hello, welcome to main of StartRemotePeers\n");
        try {
            CommonPeerProperties cpp = new CommonPeerProperties();
            cpp.getPeerProperties();

            StartRemotePeers myStart = new StartRemotePeers();
            myStart.getConfiguration();

            Vector<Peer> peerInfoVector = myStart.getPeerInformation();

            // get current path
            String path = System.getProperty("user.dir");


            // start clients at remote hosts
            for (int i = 0; i < peerInfoVector.size(); i++) {
                System.out.println("\n");
                Peer pInfo = peerInfoVector.elementAt(i);
                System.out.println("Start remote peer " + pInfo.getPeerID() +  " at " + pInfo.getHostName() );
                Runtime.getRuntime().exec("ssh anna.moody@" + pInfo.getHostName() + " cd " + path + "; java Computer_Networking_Project/src/peerProcess " + pInfo.getPeerID());

                peerProcess.main(new String[]{Integer.toString(pInfo.getPeerID())});
            }
            System.out.println("\n\nStarting all remote peers has done." );
        } catch (Exception ex) {
            System.out.println(ex);
        }

    }
}
