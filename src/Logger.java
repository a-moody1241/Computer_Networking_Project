import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.ArrayList;

public class Logger {
    private static int peerID;
    private static FileWriter logFile;
    private static PrintWriter logFileWriter;

    public Logger(int peerID) throws IOException {
        Logger.peerID = peerID;
        logFile = new FileWriter("log_peer_" + peerID + ".log");
        logFileWriter = new PrintWriter(logFile);
    }

    public static void peerToPeerMakesTCPConnection(int secondPeerID) {
        logFileWriter.println(getCurrentTime()+"Peer " + peerID + " makes a connection to Peer " + secondPeerID);
        logFileWriter.flush();
        //Whenever a peer makes a TCP connection to other peer, it generates the following log:
        //[Time]: Peer [peer_ID 1] makes a connection to Peer [peer_ID 2].
        //the [Time] field represents the current time, which contains the date,
        //hour, minute, and second. The format of [Time] is up to you.
    }
    public static void peerConnectedFromPeer(int secondPeerID) {
        logFileWriter.println(getCurrentTime()+"Peer " + peerID + " is connected from Peer " + secondPeerID);
        logFileWriter.flush();
        //[Time]: Peer [peer_ID 1] is connected from Peer [peer_ID 2].
    }
    public static void changeOfPreferredNeighbors (ArrayList<Integer> neighbors) {
        logFileWriter.println(getCurrentTime()+"Peer " + peerID + " has the preferred neighbors " + neighbors);
        logFileWriter.flush();
        //[Time]: Peer [peer_ID] has the preferred neighbors [preferred neighbor ID list].
        //[preferred neighbor list] is the list of peer IDs separated by comma ‘,’.
    }
    public static void unchoking (int secondPeerID) {
        logFileWriter.println(getCurrentTime()+"Peer " + peerID + " is unchoked by " + secondPeerID);
        logFileWriter.flush();
        //[Time]: Peer [peer_ID 1] is unchoked by [peer_ID 2].
    }
    public static void choking (int secondPeerID) {
        logFileWriter.println(getCurrentTime()+"Peer " + peerID + " is choked by " + secondPeerID);
        logFileWriter.flush();
        //[Time]: Peer [peer_ID 1] is choked by [peer_ID 2].
    }
    public static void receivingHaveMessage  (int secondPeerID, int index) {
        logFileWriter.println(getCurrentTime()+"Peer " + peerID + " received a 'have' message from " + secondPeerID + "for the piece " + index);
        logFileWriter.flush();
        //[Time]: Peer [peer_ID 1] received the  ‘have’ message from [peer_ID 2] for the piece
        //[piece index].
    }
    public static void receivingInterestedMessage  (int secondPeerID) {
        logFileWriter.println(getCurrentTime()+"Peer " + peerID + " received a 'interested' message from " + secondPeerID);
        logFileWriter.flush();
        //[Time]: Peer [peer_ID 1] received the ‘interested’ message from [peer_ID 2].
    }
    public static void receivingNotInterestedMessage  (int secondPeerID) {
        logFileWriter.println(getCurrentTime()+"Peer " + peerID + " received a 'not interested' message from " + secondPeerID);
        logFileWriter.flush();
        //[Time]: Peer [peer_ID 1] received the ‘not interested’ message from [peer_ID 2].
    }
    public static void downloadingAPiece (int secondPeerID, int index, int count) {
        logFileWriter.println(getCurrentTime()+"Peer " + peerID + " has downloaded the piece " + index + " from " + secondPeerID
                + ".\nNow the number of pieces it has is " + count);
        logFileWriter.flush();
        //[Time]: Peer [peer_ID 1] has downloaded the piece [piece index] from [peer_ID 2].Now
        //the number of pieces it has is [number of pieces].
    }
    public static void completionOfDownload  () {
        logFileWriter.println(getCurrentTime()+"Peer " + peerID + " has downloaded the complete file.");
        logFileWriter.flush();
        //[Time]: Peer [peer_ID] has downloaded the complete file.
    }

    public static String getCurrentTime(){
        LocalDateTime now = LocalDateTime.now();
        return (now.getYear() +"-"+ now.getMonthValue() +"-"+ now.getDayOfMonth() +" "+now.getHour()+":"+now.getMinute()+":"+now.getSecond()+"."+now.get(ChronoField.MILLI_OF_SECOND)+": ");
    }

}