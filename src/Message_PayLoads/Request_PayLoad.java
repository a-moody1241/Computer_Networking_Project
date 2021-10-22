package Message_PayLoads;

public class Request_PayLoad extends PayLoad{
    private static final long serialVersionUID = 4L;


    private int index;

    public Request_PayLoad(int index) {
        super();
        this.index = index;
    }

    public int getIndex() {return index;}
    public void setIndex(int index) {this.index = index;}
}
