import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerConnection implements Runnable {
    private ServerSocket listener;
    private Connection connection;

    public ServerConnection(Peer peer, Connection connection) {
        try {
            this.listener = new ServerSocket(peer.getPortNumber());
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.connection = connection;
        //log input
        System.out.println("Server is open");
    }


    @Override
    public void run() {
        setUpClientServer();
    }

    public void setUpClientServer() {
        while(true){
            Socket cSocket = null;
            try {
                cSocket = this.listener.accept();
                //log info

                (new Thread(new MessageManager(new ClientConnection(cSocket, connection), connection))).start();
                System.out.println("Creating client's server connection");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
