import Message.Message;
import Message.MessageGroup;
import Message.Message_PayLoads.PayLoad;

import javax.swing.text.Utilities;
import java.io.*;
import java.net.Socket;
import java.util.*;

public class ClientConnection implements Runnable {
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Socket cSocket;
    private Connection connection;
    private PipedOutputStream pipedOutputStream = new PipedOutputStream();

    public ClientConnection(Socket cSocket, Connection connection){
       try {
           this.cSocket = cSocket;
           this.connection = connection;
           this.out = new ObjectOutputStream(cSocket.getOutputStream());
           this.in = new ObjectInputStream(cSocket.getInputStream());
       } catch (IOException e) {
           e.printStackTrace();
       }
        System.out.println("Client server is running");
    }
    public void receive(){
        try {
            byte[] b = new byte[4];
            in.readFully(b);
            int i = FileUtilities.byteArrToInt(b,0);
            pipedOutputStream.write(FileUtilities.intToByteArr(i));

            byte[] c = new byte[i];
            in.readFully(c);
            pipedOutputStream.write(c);

            pipedOutputStream.flush();
            //clientBlocker();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void receive(int length){
        try {
            byte[] b = new byte[length];
            in.readFully(b);
            pipedOutputStream.write(b);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public PipedOutputStream getPipedOutputStream() {
        return this.pipedOutputStream;
    }

    public void send(byte[] msg){
        try {
            out.write(msg);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while(true){
            try{
                this.receive();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
