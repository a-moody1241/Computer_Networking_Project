import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ServerFileTransfer extends Server {
    public void fileTransfer(){
        		/*System.out.println("in file transfer");
        		BufferedOutputStream outToClient = null;
        		FileInputStream fis = null;
        		try{
        			outToClient = new BufferedOutputStream(connection.getOutputStream());
				} catch (IOException e) {
					e.printStackTrace();
				}

        		if (outToClient != null){
        			File file = new File(fileToSend);
        			byte[] aByte = new byte[(int) file.length()];
        			try {
        				fis = new FileInputStream(file);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
        			BufferedInputStream bis = new BufferedInputStream(fis);
        			try{
        				bis.read(aByte, 0, aByte.length);
        				outToClient.write(aByte, 0, aByte.length);
        				outToClient.flush();
        				outToClient.close();
        				connection.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}*/
        try{
            FileInputStream fis = new FileInputStream(fileToSend);
            DataOutputStream dos;// = new DataOutputStream(connection.getOutputStream());
            File file = new File(fileToSend);
            byte[] filebytes = new byte[(int) file.length()];
            fis.read(filebytes);
            //dos.writeInt(filebytes.length);
            //dos.write(filebytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
