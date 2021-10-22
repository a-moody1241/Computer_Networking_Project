package Message.Message_PayLoads;

public class Piece_PayLoad extends PayLoad{
    private static final long serialVersionUID = 4L;

    private byte[] content = null;
    private int index = 0;

    public Piece_PayLoad(byte[] content, int index) {
        super();
        this.content = content;
        this.index = index;
    }

    public byte[] getContent() {
        return content;
    }
    public void setContent(byte[] content) {
        this.content = content;
    }

    public int getIndex() {
        return index;
    }
    public void setIndex(int index) {
        this.index = index;
    }
}
