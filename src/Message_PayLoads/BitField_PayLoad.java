package Message_PayLoads;

public class BitField_PayLoad extends PayLoad{
    private static final long serialVersionUID = 4L;

    private byte[] bitfield;

    public BitField_PayLoad(byte[] bitfield) {
        super();
        this.bitfield = bitfield;
    }

    public byte[] getBitfield() {
        return bitfield;
    }
    public void setBitfield(byte[] bitfield) {
        this.bitfield = bitfield;
    }
}
