import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;


/**
 * TO RUN THE FILE TRANSFER:
 * 		Run Server.java
 * 		While Server.java is running, run Client.java
 * 		When prompted with "Hello, please input a sentence:", type in "download" and press enter
 * 		The file will then be transferred from the peer_1001 package to the peer_1002 package through the 8000 socket.
**/

public class Client {
	Socket requestSocket;           //socket connect to the server
	ObjectOutputStream out;         //stream write to the socket
 	ObjectInputStream in;          //stream read from the socket
	String message;                //message send to the server
	String MESSAGE;                //capitalized message read from the server

    private final static String fileOutput = "test";//"/Users/catherinehealy/Documents/GitHub/Computer_Networking_Project/src/peer_1002/testout.jpg";//"src/peer_1002/testout.jpg"; //"src/peer_1001/tree.jpg"

    public void Client() {}

    public void fileTransfer(){

		File file = new File(fileOutput);
		try {
			DataInputStream dis = new DataInputStream(requestSocket.getInputStream());
			OutputStream out = new FileOutputStream(file);
			int fileLength = dis.readInt();
			byte[] filebytes = new byte[fileLength];
			dis.readFully(filebytes, 0, filebytes.length);
			out.write(filebytes);
		} catch (IOException e) {
            e.printStackTrace();
        }
    }


	void run()
	{

		try{

			//create a socket to connect to the server
			requestSocket = new Socket("localhost", 8001);
			System.out.println("Connected to localhost in port 8000");
			//initialize inputStream and outputStream

			out = new ObjectOutputStream(requestSocket.getOutputStream());
			out.flush();

			//create and send handshake here
			byte[] handshakes = handshake.sendHandshake(out, 1001);
			String s = new String(handshakes, StandardCharsets.UTF_8);
			System.out.println("Created the handshake: " + s + " !");

			in = new ObjectInputStream(requestSocket.getInputStream());

			//get Input from standard input
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
			while(true)
			{
				System.out.print("Hello, please input a sentence: ");
				//read a sentence from the standard input
				message = bufferedReader.readLine();

				//Send the sentence to the server
				sendMessage(message);

				//Receive the upperCase sentence from the server
				MESSAGE = (String)in.readObject();

				//ADDED THIS
				if (MESSAGE.equals("DOWNLOAD")){
					fileTransfer();
				}
				//END OF ADDED SECTION

				//show the message to the user
				System.out.println("Receive message: " + MESSAGE);
				message=null;
			}
		}
		catch (ConnectException e) {
    			System.err.println("Connections.Connection refused. You need to initiate a server first.");
		} 
		catch ( ClassNotFoundException e ) {
            		System.err.println("Class not found");
        	} 
		catch(UnknownHostException unknownHost){
			System.err.println("You are trying to connect to an unknown host!");
		}
		catch(IOException ioException){
			System.out.println("Error: ");
			ioException.printStackTrace();
		}
		finally{
			//Close connections
			try{
				in.close();
				out.close();
				requestSocket.close();
			}
			catch(IOException ioException){
				ioException.printStackTrace();
			}
		}
	}
	//send a message to the output stream
	void sendMessage(String msg)
	{
		try{
			//stream write the message
			out.writeObject(msg);
			out.flush();
		}
		catch(IOException ioException){
			ioException.printStackTrace();
		}
	}
	//main method
	public static void main(String args[])
	{

		Client client = new Client();
		client.run();
	}

}
