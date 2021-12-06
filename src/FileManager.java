import Configuration.CommonPeerProperties;
import Message.Message_PayLoads.Piece_PayLoad;

import java.io.*;
import java.util.Arrays;
import java.util.Hashtable;

public class FileManager { //todo fix all this
    private static boolean[] filePiecesOwned;
    private static Hashtable<Integer, Integer> requestedPieces = new Hashtable<Integer, Integer>();
    private static final int noOfFilePieces = (int) Math
            .ceil((double) CommonPeerProperties.getFileSize() / CommonPeerProperties.getPieceSize());
    private static int noOfPiecesAvailable = 0;
    private static String directory = null;
    private static String fileName = CommonPeerProperties.getFileName();
    private static int fileSize = CommonPeerProperties.getFileSize();
    private static File file = null;

    public FileManager(int peerId, boolean hasFile) {
        directory = "peer_" + peerId + "/";

        filePiecesOwned = new boolean[noOfFilePieces];

        if (hasFile) {
            Arrays.fill(filePiecesOwned, true);
            noOfPiecesAvailable = noOfFilePieces;
        }

        File folder = new File(directory);

        if (!folder.exists()) {
            folder.mkdirs();
        }

        file = new File(directory + fileName);

        if (!file.exists()) {
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(file);
                fos.write(new byte[fileSize]);
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
    public static int getNoOfPiecesAvailable() {
        return noOfPiecesAvailable;
    }
    public static File getFile() {
        return file;
    }
    public static void setFile(File file) {
        FileManager.file = file;
    }
    public static int getNooffilepieces() {
        return noOfFilePieces;
    }
    public static boolean isInteresting(int index) {
        return filePiecesOwned[index] ? true : false;
    }
    public static synchronized boolean hasCompleteFile() {
        return noOfPiecesAvailable == noOfFilePieces ? true : false;
    }
    public static synchronized byte[] getBitField() throws Exception {
        int size = (int) Math.ceil((double) noOfFilePieces / 8);
        byte[] bitfield = new byte[size];
        int counter = 0;
        int indexI = 0;
        // TODO Implement Professor Logic
        while (indexI < noOfFilePieces) {
            int temp;
            if (noOfFilePieces > indexI + 8) {
                temp = indexI + 8;
            } else {
                temp = noOfFilePieces;
            }
            bitfield[counter++] = FileUtilities.booleanToByte(Arrays.copyOfRange(filePiecesOwned, indexI, temp));
            indexI = indexI + 8;
        }
        return bitfield;
    }

    public static synchronized Piece_PayLoad get(int index) {
        try {
            FileInputStream fis = new FileInputStream(file);
            int loc = CommonPeerProperties.getPieceSize() * index;
            fis.skip(loc);
            int contentSize = CommonPeerProperties.getPieceSize();
            if (fileSize - loc < CommonPeerProperties.getPieceSize())
                contentSize = fileSize - loc;
            byte[] content = new byte[contentSize];
            fis.read(content);
            fis.close();
            return new Piece_PayLoad(content, index);
        } catch (FileNotFoundException e) {
            return null;
        } catch (IOException e) {
            return null;
        }
    }
    public static synchronized void store(Piece_PayLoad piece) throws Exception {
        int loc = CommonPeerProperties.getPieceSize() * piece.getIndex();
        RandomAccessFile fos = null;
        try {
            fos = new RandomAccessFile(file, "rw");
            fos.seek(loc);
            fos.write(piece.getContent());
            fos.close();

            noOfPiecesAvailable++;
            filePiecesOwned[piece.getIndex()] = true;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean compareBitfields(byte[] neighborBitfield, byte[] bitfield) {
        boolean flag = false;
        int size = (int) Math.ceil((double) noOfFilePieces / 8);
        byte[] interesting = new byte[size];
        if (neighborBitfield == null) {
            return flag;
        }
        int indexI = 0;
        while (indexI < bitfield.length) {
            interesting[indexI] = (byte) ((bitfield[indexI] ^ neighborBitfield[indexI]) & neighborBitfield[indexI]);
            if (interesting[indexI] != 0) {
                flag = true;
            }
            indexI++;
        }
        return flag;
    }

    public static int requestPiece(byte[] neighborBitfield, byte[] bitfield, int nPID) {
        int size = (int) Math.ceil((double) noOfFilePieces / 8);
        byte[] interesting = new byte[size];
        boolean[] interestingPieces = new boolean[noOfFilePieces];
        int finLength;

        finLength = size > 1 ? noOfFilePieces % (8) : noOfFilePieces;

        int start, end;

        int indexI = 0, indexJ = 0;
        while (indexI < bitfield.length) {
            interesting[indexI] = (byte) ((bitfield[indexI] ^ neighborBitfield[indexI]) & neighborBitfield[indexI]);
            start = indexI == size - 1 ? 8 - finLength : 0;
            end = indexI == size - 1 ? finLength : 8;
            boolean[] x = FileUtilities.byteToBoolean(interesting[indexI]);
            System.arraycopy(x, start, interestingPieces, indexJ, end);
            indexJ = indexJ + 8 < noOfFilePieces ? indexJ + 8 : noOfFilePieces - finLength;
            indexI++;
        }
        int indexK = 0;
        while (indexK < noOfFilePieces) {
            if (interestingPieces[indexK] == true && !requestedPieces.containsKey(indexK)) {
                requestedPieces.put(indexK, indexK);
                return indexK;
            }
            indexK++;
        }
        return -1;
    }

    public static void checker() {

        (new Thread() {
            @Override
            public void run() {
                try {
                    do {
                        Thread.sleep(60000);
                        for (Integer ind : requestedPieces.keySet()) {
                            if (!filePiecesOwned[ind])
                                requestedPieces.remove(ind);
                        }
                    } while (noOfPiecesAvailable < noOfFilePieces);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }
}