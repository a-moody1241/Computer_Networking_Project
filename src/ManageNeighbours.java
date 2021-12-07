import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

public class ManageNeighbours implements Runnable {
    private Connection myConnection;
    private Set<Integer> preferredPeerIDSet;

    public ManageNeighbours(Connection myConnection)
    {
        this.myConnection = myConnection;
        this.preferredPeerIDSet= new TreeSet<Integer>();
    }

    public void run()
    {
        try
        {
            findNeighbours();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void findNeighbours() throws IOException, InterruptedException
    {
        //todo algorithm
    }
}
