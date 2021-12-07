import Configuration.CommonPeerProperties;
import Message.Message_PayLoads.Piece_PayLoad;

import java.io.*;
import java.util.Arrays;
import java.util.Hashtable;

public class FileManager { //todo fix all this
    private static boolean[] filePiecesCompleted;
    private static Hashtable<Integer, Integer> filePiecesRequested = new Hashtable<Integer, Integer>();
    private static final int filePiecesCompletedCount = (int) Math.ceil((double) CommonPeerProperties.getFileSize() / CommonPeerProperties.getPieceSize());
    private static int filePiecesAvailableCount = 0;
    private static String directory = null;
    private static String fileName = CommonPeerProperties.getFileName();
    private static int fileSize = CommonPeerProperties.getFileSize();
    private static File file = null;

    public FileManager(int peerId, boolean hasFile) throws IOException {
        filePiecesCompleted = new boolean[filePiecesCompletedCount];
        if (hasFile) {
            Arrays.fill(filePiecesCompleted, true);
            filePiecesAvailableCount = filePiecesCompletedCount;
        }
        directory = "peer_" + peerId + "/";
        File folder = new File(directory);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        file = new File(directory + fileName);
        if (!file.exists()) {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(new byte[fileSize]);
            fileOutputStream.close();
            FileOutputStream fos = null;
        }
    }
    public static int getFilePiecesAvailableCount() {
        return filePiecesAvailableCount;
    }
    public static File getFile() {
        return file;
    }
    public static void setFile(File file) {
        FileManager.file = file;
    }
    public static int getFilePiecesCompletedCount() {
        return filePiecesCompletedCount;
    }
    public static boolean isInteresting(int index) {
        return filePiecesCompleted[index] ? true : false;
    }
    public static synchronized boolean hasCompleteFile() {
        return filePiecesAvailableCount == filePiecesCompletedCount ? true : false;
    }
    public static synchronized byte[] getBitField() throws Exception {
        int size = (int) Math.ceil((double) filePiecesCompletedCount / 8);
        byte[] bitfield = new byte[size];
        int index = 0;
        int i = 0;
        while (i < filePiecesCompletedCount) {
            int temp = Math.min(filePiecesCompletedCount, i + 8);
            bitfield[index++] = FileUtilities.booleanArrToByte(Arrays.copyOfRange(filePiecesCompleted, i, temp));
            i = i + 8;
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

            filePiecesAvailableCount++;
            filePiecesCompleted[piece.getIndex()] = true;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static boolean compareBitfields(byte[] neighborBitfield, byte[] bitfield) {
        boolean flag = false;
        int size = (int) Math.ceil((double) filePiecesCompletedCount / 8);
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
        int size = (int) Math.ceil((double) filePiecesCompletedCount / 8);
        byte[] interesting = new byte[size];
        boolean[] interestingPieces = new boolean[filePiecesCompletedCount];
        int finLength;

        finLength = size > 1 ? filePiecesCompletedCount % (8) : filePiecesCompletedCount;

        int start, end;

        int indexI = 0, indexJ = 0;
        while (indexI < bitfield.length) {
            interesting[indexI] = (byte) ((bitfield[indexI] ^ neighborBitfield[indexI]) & neighborBitfield[indexI]);
            start = indexI == size - 1 ? 8 - finLength : 0;
            end = indexI == size - 1 ? finLength : 8;
            boolean[] x = FileUtilities.byteToBoolean(interesting[indexI]);
            System.arraycopy(x, start, interestingPieces, indexJ, end);
            indexJ = indexJ + 8 < filePiecesCompletedCount ? indexJ + 8 : filePiecesCompletedCount - finLength;
            indexI++;
        }
        int indexK = 0;
        while (indexK < filePiecesCompletedCount) {
            if (interestingPieces[indexK] == true && !filePiecesRequested.containsKey(indexK)) {
                filePiecesRequested.put(indexK, indexK);
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
                        for (Integer ind : filePiecesRequested.keySet()) {
                            if (!filePiecesCompleted[ind])
                                filePiecesRequested.remove(ind);
                        }
                    } while (filePiecesAvailableCount < filePiecesCompletedCount);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }
}

