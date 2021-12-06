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

    public void getPeerProperties() {
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

    public  String getConfigFile() { return CONFIG_FILE; }

    public  int getNumberOfPreferredNeighbors() { return numberOfPreferredNeighbors; }
    public  void setNumberOfPreferredNeighbors(int numberOfPreferredNeighbors) { CommonPeerProperties.numberOfPreferredNeighbors = numberOfPreferredNeighbors; }

    public  int getUnchokingInterval() { return unchokingInterval; }
    public  void setUnchokingInterval(int unchokingInterval) { CommonPeerProperties.unchokingInterval = unchokingInterval; }

    public  int getOptimisticUnchokingInterval() { return optimisticUnchokingInterval; }
    public  void setOptimisticUnchokingInterval(int optimisticUnchokingInterval) { CommonPeerProperties.optimisticUnchokingInterval = optimisticUnchokingInterval; }

    public static String getFileName() { return fileName; }
    public  void setFileName(String fileName) { CommonPeerProperties.fileName = fileName; }

    public static int getFileSize() { return fileSize; }
    public  void setFileSize(int fileSize) { CommonPeerProperties.fileSize = fileSize; }

    public static int getPieceSize() { return pieceSize; }
    public  void setPieceSize(int pieceSize) { CommonPeerProperties.pieceSize = pieceSize; }


}
