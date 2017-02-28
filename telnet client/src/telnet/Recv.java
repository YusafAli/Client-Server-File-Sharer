package telnet;
//this piece will be used for recieving purpose at host side
import java.net.*;
import java.io.*;
/**
 *
 * @author  Yusaf Ali
 */
public class Recv
{
	private InetAddress IP;
	private String filename;
	private File path;
	public Recv(String filename, InetAddress IP, File path)
	{
		this.filename = filename;
		this.IP = IP;
		this.path = path;
	}
	public void reciever()
    {
    	try
    	{
    		int bytesRead = 0;
    		Socket sock = new Socket(IP ,30431);
    		//--------------------------
    		DataInputStream din = new DataInputStream(sock.getInputStream());
    		String shit = din.readUTF();
    		byte[] mybytearray = new byte[Integer.parseInt(shit)];
    		int current;
    		InputStream is = sock.getInputStream();
    		FileOutputStream fos = new FileOutputStream(path + "/" + filename);
    		BufferedOutputStream bos = new BufferedOutputStream(fos);
    		bytesRead = is.read(mybytearray, 0, mybytearray.length);
    		current = bytesRead;
    		do
    		{
    			bytesRead = is.read(mybytearray, current, mybytearray.length - current);
    			if (bytesRead > 0)
    			{
        			current += bytesRead;
    			}
    		}while (bytesRead > 0);
    		bos.write(mybytearray, 0, current);
    		bos.flush();
    		bos.close();
    		fos.flush();
    		fos.close();
    		sock.close();
    	}
    	catch(IOException e)
    	{}
    }
}
/*
try {
    Thread.sleep(3000);                 //1000 milliseconds is one second.
} catch(InterruptedException ex) {
    Thread.currentThread().interrupt();
}*/