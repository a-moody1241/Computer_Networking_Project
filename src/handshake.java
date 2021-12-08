import com.oracle.xmlns.internal.webservices.jaxws_databinding.SoapBindingUse;

import java.io.*;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.SQLOutput;

public class handshake implements Serializable {
    private static final long serialVersionUID = -1482860868859618509L;
    private static String header = "P2PFILESHARINGPROJ";
    private static int peerID;
    public handshake(int peerID){
        super();
        this.peerID = peerID;
        this.header = getHeader();
    }
    public String getHeader(){ return header; }
    public int getPeerID(){
        return peerID;
    }
    public void setPeerID(int peerID){ this.peerID = peerID;}


    // Returns a handshake byte array
    public static byte[] createHandshake(int peerID) {
        handshake h = new handshake(peerID);
        // Construct handshake header byte array
        Charset charset = StandardCharsets.US_ASCII;
        byte[] headerBytes = charset.encode(header).array();

        // Construct peerID byte array
        String temp = Integer.toString(peerID);
        byte[] peerIDBytes = temp.getBytes(); //the peerID.toByteArray was throwing an error for me. Changed it to this

        // Concatenate handshake header and peerID byte strings into final handshake message
        // There are 10 bytes of zero padding between header and peerID in final message.
        byte[] handshake = new byte [32];
        System.arraycopy(headerBytes, 0, handshake, 0, headerBytes.length);
        System.arraycopy(peerIDBytes, 0, handshake, 27, peerIDBytes.length);
        System.out.println("Creating the handshake");
        return handshake;
    }


    public static int receiveHandshake(ObjectInputStream is) throws IOException, ClassNotFoundException {

            ObjectInputStream ois = new ObjectInputStream(is);
            handshake receive = (handshake) ois.readObject();
            System.out.println("receiving handshake from " + receive.getPeerID());
        ois.close();
        return receive.getPeerID();

    }

    public static void printHandshake(handshake h){
        System.out.println("Header :" + h.getHeader() + "\nPeer ID: " + h.getPeerID());
    }
}
