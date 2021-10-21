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

    // Returns a handshake byte array
		public byte[] createHandshake(int peerID) {
      // Construct handshake header byte array
      String headerString = "P2PFILESHARINGPROJ";
      Charset charset = StandardCharsets.US_ASCII;
      byte[] headerBytes = charset.encode(headerString).array();

      // Construct peerID byte array
      byte[] peerIDBytes = peerID.toByteArray;

      // Concatenate handshake header and peerID byte strings into final handshake message
      // There are 10 bytes of zero padding between header and peerID in final message.
      byte[] handshake = new byte [32];
      System.arraycopy(headerBytes, 0, handshake, 0, headerBytes.length);
      System.arraycopy(peerIDBytes, 0, handshake, 27, peerIDBytes.length);
      
      return handshake;
      /* 
      *  TODO (dylan): I am going to move this function
      *  into a new class called Handshake.java and add few more helper functions etc.
      */

		}

    public void fileTransfer(){
		/*System.out.println("in file transfer");

		byte[] aByte = new byte[1];
    	int bytesRead;

    	InputStream is = null;
    	try{
    		is = requestSocket.getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();

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
		}*/
		File file = new File(fileOutput);
		try{
		DataInputStream dis = new DataInputStream(requestSocket.getInputStream());
		OutputStream out = new FileOutputStream(file);
		int fileLength = dis.readInt();
		byte[] filebytes = new byte[fileLength];
		dis.readFully(filebytes, 0 , filebytes.length);
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
				message=null;
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
