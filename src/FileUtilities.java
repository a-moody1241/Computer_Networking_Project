public class FileUtilities {
    public static byte booleanToByte(boolean[] bool) throws Exception {
        if (bool.length > 8)
            throw new Exception("boolean array length exceeded: not compatible with byte");
        byte val = 0;
        for (boolean x : bool) {
            val = (byte) (val << 1);
            val = (byte) (val | (x ? 1 : 0));
        }
        return val;
    }

    public static boolean[] byteToBoolean(byte val) {
        boolean[] bool = new boolean[8];
        for (int i = 0; i < 8; i++) {
            bool[7 - i] = (val & 1) == 1;
            val = (byte) (val >> 1);
        }
        return bool;
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
