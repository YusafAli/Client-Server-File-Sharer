package telnet;
//this piece will be used for sending purpose at remote host side


/**
 *
 * @author  Yusaf Ali
 */
import java.net.*;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.*;

public class Send
{
	public void sendthis(File tifle, String filename)
	{
		try
		{
			ServerSocket servsock = new ServerSocket(30431);
			//do
			{
				//-------------------------------
				//Selecting file that needs to be sent
				File myFile = new File(tifle, filename);//setting file
				byte[] mybytearray = new byte[(int) myFile.length()];//setting buffervariable for file
				//-------------------------------
				Socket sock = servsock.accept();//create socket if server finds someone that wants to connect
				//-------------------------------
				//This portion will send the file size
				//reopen the file
				//following statements are for sending temp file on socket
				DataInputStream din = new DataInputStream(sock.getInputStream());
				DataOutputStream dout = new DataOutputStream(sock.getOutputStream());
				dout.writeUTF(String.valueOf(myFile.length()));
				//The following code is used to delete the file that we have temporarily created.
				//Path will be used for specifying the path of the file that we wanna delete
				//-------------------------------
				//-------------------------------
				//BufferedInputStream sizebuffer = new BufferedInputStream(sizeoffile);
				
				//-------------------------------
				//Sending the original file this time
				//increase the buffersize of the sendable buffer
				BufferedInputStream bis = new BufferedInputStream(new FileInputStream(myFile));
				bis.read(mybytearray, 0, mybytearray.length);
				OutputStream os = sock.getOutputStream();
				os.write(mybytearray);
				os.flush();
				sock.close();
			}
			servsock.close();
		}
		catch (IOException ex)
		{
			System.out.println("Nothing happened: " + ex.getMessage());
			System.out.println("File Sending failed");
			ex.printStackTrace();
		}
	}

}/**/