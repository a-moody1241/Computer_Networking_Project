package Message_PayLoads;

public class Have_PayLoad extends PayLoad{
    private static final long serialVersionUID = 4L;

    private int index;

    public Have_PayLoad(int index) {
        super();
        this.index = index;
    }
    public int getIndex() {
        return index;
    }
    public void setIndex(int index) {
        this.index = index;
    }


}
