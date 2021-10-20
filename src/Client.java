import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

public class Client {
	Socket requestSocket;           //socket connect to the server
	ObjectOutputStream out;         //stream write to the socket
 	ObjectInputStream in;          //stream read from the socket
	String message;                //message send to the server
	String MESSAGE;                //capitalized message read from the server

    private final static String fileOutput = "C:\\Users\\swimg\\IdeaProjects\\Computer Networking Project\\src\\peer_1002\\testout.jpg";

    public void Client() {}

    public void fileTransfer(){
		System.out.println("in file transfer");

		byte[] aByte = new byte[1];
    	int bytesRead;
		System.out.println("in file transfer");

    	InputStream is = null;
    	try{
    		is = requestSocket.getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("in file transfer");

    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
		System.out.println("in file transfer");

    	if (is != null){
			System.out.println("in file transfer");

			FileOutputStream fos = null;
    		BufferedOutputStream bos = null;
    		try{
				System.out.println("in file transfer");

				fos = new FileOutputStream(fileOutput);
				System.out.println("oeoe");

				bos = new BufferedOutputStream(fos);
				System.out.println("oeoe");

				bytesRead = is.read(aByte, 0, aByte.length);
				System.out.println("oeoe");
    			do{
    				baos.write(aByte);
    				bytesRead = is.read(aByte);
				} while (bytesRead != -1);
				System.out.println("oifjoeifj");
    			bos.write(baos.toByteArray());
    			bos.flush();
    			bos.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}


	void run()
	{

		try{
			//create a socket to connect to the server
			requestSocket = new Socket("localhost", 8001);
			System.out.println("Connected to localhost in port 8001");
			//initialize inputStream and outputStream

			out = new ObjectOutputStream(requestSocket.getOutputStream());
			out.flush();
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
				System.out.println("back on client");
				//END OF ADDED SECTION

				//show the message to the user
				System.out.println("Receive message: " + MESSAGE);
			}
		}
		catch (ConnectException e) {
    			System.err.println("Connection refused. You need to initiate a server first.");
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
				System.out.println("boom");
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
