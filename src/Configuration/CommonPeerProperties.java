package Configuration;

import java.io.*;

public class CommonPeerProperties {

    public static final String CONFIG_FILE = "Common.cfg";
    private static int numberOfPreferredNeighbors;
    private static int unchokingInterval;
    private static int optimisticUnchokingInterval;
    private static String fileName;
    private static int fileSize;
    private static int pieceSize;

    public void read() {
        try {
            BufferedReader br = new BufferedReader(new FileReader(CONFIG_FILE));
            String line;
            while ((line = br.readLine()) != null) {
                int deliminatorIndex = line.indexOf(' ');
                String property = line.substring(0,deliminatorIndex);
                String value = line.substring(deliminatorIndex+1);

                switch (property){
                    case "NumberOfPreferredNeighbors":
                        numberOfPreferredNeighbors = Integer.parseInt(value) + 1;
                        break;
                    case "UnchokingInterval":
                        unchokingInterval = Integer.parseInt(value);
                        break;
                    case "OptimisticUnchokingInterval":
                        optimisticUnchokingInterval = Integer.parseInt(value);
                        break;
                    case "FileName":
                        fileName = value;
                        break;
                    case "FileSize":
                        fileSize = Integer.parseInt(value);
                        break;
                    case "PieceSize":
                        pieceSize = Integer.parseInt(value);
                        break;
                }
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getConfigFile() { return CONFIG_FILE; }

    public static int getNumberOfPreferredNeighbors() { return numberOfPreferredNeighbors; }
    public static void setNumberOfPreferredNeighbors(int numberOfPreferredNeighbors) { CommonPeerProperties.numberOfPreferredNeighbors = numberOfPreferredNeighbors; }

    public static int getUnchokingInterval() { return unchokingInterval; }
    public static void setUnchokingInterval(int unchokingInterval) { CommonPeerProperties.unchokingInterval = unchokingInterval; }

    public static int getOptimisticUnchokingInterval() { return optimisticUnchokingInterval; }
    public static void setOptimisticUnchokingInterval(int optimisticUnchokingInterval) { CommonPeerProperties.optimisticUnchokingInterval = optimisticUnchokingInterval; }

    public static String getFileName() { return fileName; }
    public static void setFileName(String fileName) { CommonPeerProperties.fileName = fileName; }

    public static int getFileSize() { return fileSize; }
    public static void setFileSize(int fileSize) { CommonPeerProperties.fileSize = fileSize; }

    public static int getPieceSize() { return pieceSize; }
    public static void setPieceSize(int pieceSize) { CommonPeerProperties.pieceSize = pieceSize; }


}
