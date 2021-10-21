public class Peer {
    private int peerID;
    private String hostName;
    private int portNumber;
    private boolean filePresent;

    public Peer(String peerID, String hostName, String portNumber, String filePresent){
        super();
        this.peerID = Integer.parseInt(peerID);
        this.hostName = hostName;
        this.portNumber = Integer.parseInt(portNumber);
        this.filePresent = Boolean.parseBoolean(filePresent);
    }

    public Peer(int peerID, String hostName, int portNumber, boolean filePresent){
        super();
        this.peerID = peerID;
        this.hostName = hostName;
        this.portNumber = portNumber;
        this.filePresent = filePresent;
    }

    public int getPeerID(){
        return peerID;
    }
    public void setPeerID(int peerID){
        this.peerID = peerID;
    }

    public String getHostName(){
        return hostName;
    }
    public void setHostName(String hostName){
        this.hostName = hostName;
    }

    public int getPortNumber(){
        return portNumber;
    }
    public void setPortNumber(int portNumber){
        this.portNumber = portNumber;
    }

    public boolean getFilePresent(){
        return filePresent;
    }
    public void setFilePreset(boolean filePresent){
        this.filePresent = filePresent;
    }

    public static void main(String[] args){}
}
