public class FileUtilities {
    public static byte booleanToByte(boolean[] booleanArr) throws Exception {
        if (booleanArr.length > 8)
            throw new Exception("Boolean array length exceeded, not compatible for byte conversions");
        byte byteValue = 0;
        for (boolean b : booleanArr) {
            byteValue = (byte) (byteValue << 1);
            byteValue = (byte) (byteValue | (b ? 1 : 0));
        }
        return byteValue;
    }

    public static boolean[] byteToBoolean(byte byteValue) {
        boolean[] booleanArr = new boolean[8];
        for (int i = 0; i < 8; i++) {
            booleanArr[7 - i] = (byteValue & 1) == 1;
            byteValue = (byte) (byteValue >> 1);
        }
        return booleanArr;
    }

    public static void updateBitfield(long index, byte[] bitfield) {
        int i = (int) (index / 8);
        int u = (int) (index % 8);
        byte update = 1;
        update = (byte) (update << u);
        bitfield[i] = (byte) (bitfield[i] | update);
    }
    public static boolean checkComplete(byte[] bitfield, int size) {

        boolean[] interestingPieces = new boolean[size];
        int finLength;

        if (size > 1)
            finLength = (size % (8));
        else
            finLength = size;

        int start, end;
        for (int i = 0, j = 0; i < bitfield.length; i++) {
            if (i == bitfield.length - 1) {
                start = 8 - finLength;
                end = finLength;
            } else {
                start = 0;
                end = 8;
            }
            boolean[] x = FileUtilities.byteToBoolean(bitfield[i]);
            System.arraycopy(x, start, interestingPieces, j, end);

            if (j + 8 < size)
                j = j + 8;
            else
                j = size - finLength;
        }
        for (int k = 0; k < interestingPieces.length; k++) {
            if (interestingPieces[k] = false)
                return false;
        }
        return true;
    }

}



