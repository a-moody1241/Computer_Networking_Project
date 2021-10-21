import sun.security.pkcs.ParsingException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

public class PeersInformation {

    public static final String CONFIG_FILE = "PeerInfo.cfg";
    private final Vector<PeerInformation> peerInfo = new Vector<PeerInformation>();


    public void read() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(CONFIG_FILE));
        String line;
        boolean hasFile = true;
        while ((line = br.readLine()) != null) {
            line = line.trim();
            if ((line.length() == 0)) { //todo (line.startsWith("#")) || is this necessary
                continue;
            }

            String[] items = line.split("\\s+");
            if (items.length != 4) {
                throw new ParsingException(line);
            }

            if (items[3].trim().equals("0")) {
                hasFile = false;
            }

            //peerID, address, port num, hasfile
            PeerInformation peer = new PeerInformation(items[0].trim(), items[1].trim(), items[2].trim(), hasFile);

            peerInfo.addElement(peer);

        }

    }

}