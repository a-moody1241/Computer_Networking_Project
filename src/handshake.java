import java.io.*;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class handshake {
    private static String header = "P2PFILESHARINGPROJ";
    private int peerID;
    public handshake(int peerID){
        super();
        this.peerID = peerID;
        this.header = header;
    }
    // Returns a handshake byte array
    public static byte[] createHandshake(int peerID) {
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

        return handshake;
    }

    public static void sendHandshake(OutputStream os, int peerID) throws IOException {
        byte[] handshake = createHandshake(peerID);
        ObjectOutputStream oos = new ObjectOutputStream(os);
        oos.writeObject(handshake);
        System.out.println("Sending the handshake to peerID: " + peerID);
    }

    public static handshake receiveHandshake(InputStream is) throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(is);
        handshake response = (handshake) ois.readObject();
        return response;
    }
}
