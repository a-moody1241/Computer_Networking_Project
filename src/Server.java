import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

public class Server {

	//add
	private final static String fileToSend = "C:\\Users\\swimg\\IdeaProjects\\Computer Networking Project\\src\\peer_1001\\tree.jpg";
	//added

	private static final int sPort = 8001;   //The server will be listening on this port number
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

        public void fileTransfer() {
            try {
				System.out.println("Starting file transfer on Server side...");
				File myFile = new File(fileToSend);
				byte[] mybytearray = new byte[(int) myFile.length()];
				FileInputStream fis = null;
				try {
					fis = new FileInputStream(myFile);

				} catch (FileNotFoundException ex) {
					System.out.println("File not found");
				}
				BufferedOutputStream outToClient = new BufferedOutputStream(connection.getOutputStream());
				BufferedInputStream bis = new BufferedInputStream(fis);
				try {
					System.out.println(".");
					bis.read(mybytearray, 0, mybytearray.length);
					outToClient.write(mybytearray, 0, mybytearray.length);
					System.out.println("..");
					outToClient.flush();
					System.out.println("...");


				} catch (IOException ex) {
				}

			} catch (IOException ioException) {
				System.out.println("Disconnect with Client1 " + no);
			}
			System.out.println("Finishing file transfer on Server side...");
		}
        public void run() {
 		try{


			//initialize Input and Output streams
			out = new ObjectOutputStream(connection.getOutputStream());
			out.flush();
			in = new ObjectInputStream(connection.getInputStream());
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
					System.out.println("pop");

				}
			}
			catch(ClassNotFoundException classnot){
					System.err.println("Data received in unknown format");
				}
		}
		catch(IOException ioException){
			System.out.println("Disconnect with Client2 " + no + " " + ioException);
			ioException.printStackTrace();
		}
		finally{
			//Close connections
			try{
			    System.out.println("ow");
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
