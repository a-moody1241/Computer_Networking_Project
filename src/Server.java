import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Server {

	//add
	private final static String fileToSend = "/Users/catherinehealy/Documents/GitHub/Computer_Networking_Project/src/peer_1001/tree.jpg";//"src/peer_1001/tree.jpg";
	//added

	private static final int sPort = 8000;   //The server will be listening on this port number
	public static void main(String[] args) throws Exception {


		System.out.println("The server is running.");
        	ServerSocket listener = new ServerSocket(sPort);
		int clientNum = 1;
        	try {
            		while(true) {
                		new Handler(listener.accept(),clientNum).start();
				System.out.println("Client "  + clientNum + " is connected!");
				clientNum++;
            			}
        	} finally {
            		listener.close();
        	}
    	}

	/**
     	* A handler thread class.  Handlers are spawned from the listening
     	* loop and are responsible for dealing with a single client's requests.
     	*/
    	private static class Handler extends Thread {
    		private String message;    //message received from the client
			private String MESSAGE;    //uppercase message send to the client
			private Socket connection;
        	private ObjectInputStream in;	//stream read from the socket
        	private ObjectOutputStream out;    //stream write to the socket
			private int no;		//The index number of the client

        	public Handler(Socket connection, int no) {
            		this.connection = connection;
	    			this.no = no;
        	}

        public void fileTransfer(){
        		try{
        			FileInputStream fis = new FileInputStream(fileToSend);
        			DataOutputStream dos = new DataOutputStream(connection.getOutputStream());
        			File file = new File(fileToSend);
        			byte[] filebytes = new byte[(int) file.length()];
        			fis.read(filebytes);
        			dos.writeInt(filebytes.length);
        			dos.write(filebytes);
        		} catch (IOException e) {
					e.printStackTrace();
				}
		}


        public void run() {



 		try{


			//initialize Input and Output streams
			out = new ObjectOutputStream(connection.getOutputStream());
			out.flush();

			//byte[] handshakes = handshake.sendHandshake(out, 1002);
			//String s = new String(handshakes, StandardCharsets.UTF_8);
			//System.out.println("Created the handshake: " + s);

			in = new ObjectInputStream(connection.getInputStream());
			byte[] receivedH = handshake.receiveHandshake(in);
			System.out.println("Received the handshake" );

			try{
				while(true)
				{
					//receive the message sent from the client
					message = (String)in.readObject();
					//show the message to the user
					System.out.println("Receive message: " + message + " from client " + no);
					//Capitalize all letters in the message
					MESSAGE = message.toUpperCase();
					//send MESSAGE back to the client
					sendMessage(MESSAGE);

					//ADDED THIS
					if (MESSAGE.equals("DOWNLOAD")){
						fileTransfer();

					}
					//DONE WITH ADDED SECTION

				}
			}
			catch(ClassNotFoundException classnot){
					System.err.println("Data received in unknown format");
				}
		}
		catch(IOException | ClassNotFoundException ioException){
			System.out.println("Disconnect with Client2 " + no + " " + ioException);
			ioException.printStackTrace();
		}
		finally{
			//Close connections
			try{
				in.close();
				out.close();
				connection.close();
			}
			catch(IOException ioException){
				System.out.println("Disconnect with Client3 " + no);
			}
		}
	}

	//send a message to the output stream
	public void sendMessage(String msg)
	{
		try{
			out.writeObject(msg);
			out.flush();
			System.out.println("Send message: " + msg + " to Client " + no);
		}
		catch(IOException ioException){
			ioException.printStackTrace();
		}
	}

    }

}
