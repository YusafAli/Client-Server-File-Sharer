package telnet;
//the remote host is here
import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.StringTokenizer;

import javax.swing.JOptionPane;

public class Serv {
	public static void main(String[] args) throws InterruptedException
	{
		try 
		{
			int portno = 24000;
			String tempiler = JOptionPane.showInputDialog("Enter port number");
			if(!tempiler.equals(null))
			{
				portno = Integer.parseInt(tempiler);
			}
			ServerSocket soc = new ServerSocket(portno);
			do
			{
				System.out.println("Waiting for another pc to connect to connect");
				InetAddress IP = InetAddress.getLocalHost();
				System.out.println(IP);
				Socket csoc = soc.accept();
				Afterworks nafis = new Afterworks(csoc);
			}while(true);
		}
		catch (IOException e)
		{
			System.out.println(e.getMessage());
		}
		catch (InterruptedException IE)
		{
			IE.printStackTrace();
		}
	}
}
class Afterworks
{
	DataInputStream din;
	DataOutputStream dout;
	String Login;
	String Pass;
	Afterworks(Socket csoc) throws IOException, InterruptedException
	{
		System.out.println("User connected");
		din = new DataInputStream(csoc.getInputStream());
		dout = new DataOutputStream(csoc.getOutputStream());
		System.out.println("Waiting for username and password");
		Login = din.readUTF();
		Pass = din.readUTF();
		BufferedReader fd = new BufferedReader(new FileReader("Password"));
		String filedata = new String("");
		boolean allow = false;
		while((filedata = fd.readLine()) != null)
		{
			StringTokenizer users = new StringTokenizer(filedata);
			if((Login.equals(users.nextToken())) && (Pass.equals(users.nextToken())))
			{
				System.out.println("User: \"" + Login + "\" successfully logged into this machine");
				dout.writeUTF("ALLOWED");
				allow = true;
				break;
			}
		}
		if(allow == false)
		{
			System.out.println("Unauthorized Access");
			System.out.println("Closing connections");
			dout.writeUTF("NOT_ALLOWED");
			fd.close();
			csoc.close();
			return;
		}
		fd.close();
		
		
		String stdout = new String("");
		String statement;
		File path = new File(System.getProperty("user.dir"));
		do
		{
			statement = new String("");
			try
			{
				statement = din.readUTF();
			}catch(EOFException p){}
			catch(SocketException px){}
			
			if(statement.toLowerCase().equals("quit") || statement.toLowerCase().equals("exit"))
			{
				System.out.println("Quitting");
				allow = false;
				break;
			}
			else if(statement.equals("cd.."))
			{
				System.out.println("Cd");
				path = new File(path.getParent());
			}
			else if(statement.length() > 3 && statement.substring(0, 2).equals("cd"))
			{
				System.out.println("CD path");
				StringTokenizer ns = new StringTokenizer(statement);
				ns.nextToken();
				int tot = ns.countTokens();
				//--------------------------------------------
				//This here is the logic for changing to a directory that contains a space in its name
				String tea = ns.nextToken();
				if(tea.charAt(0) == '"' && tea.charAt(tea.length() - 1) != '"')
				{
					do
					{
						if(tot == 0) break;
						tea += " " + ns.nextToken();
						tot--;
					}
					while(tea.charAt(tea.length() - 1) != '"');
					tea = tea.substring(1, tea.length() - 1);
				}
				//---------------------------------------------
				File temp;
				if(tea.charAt(1) == ':')
				{
					temp = new File(tea);
				}
				else
				{
					temp = new File(path.getAbsolutePath() + "/" + tea);
				}
				if(temp.exists())
				{
					if(tea.charAt(1) == ':')
					{
						path = new File(tea);
					}
					else
					{
						path = new File(path.getAbsolutePath() + "/" + tea);
					}
				}
				else
				{
					//Do nothing so the path is not modified
				}
				statement = "cd";
				Excalibur nexus = new Excalibur(statement, path);
				nexus.startthings();
				try
				{
					dout.writeUTF(nexus.getError());
				}
				catch(NullPointerException pex){}
				try
				{
					dout.writeUTF(nexus.getOutput());
				}catch(NullPointerException pex){}
			}
			else if(statement.length() > 4 && statement.toLowerCase().substring(0, 5).equals("rcopy"))
			{
				System.out.println("RCopy");
				StringTokenizer ea = new StringTokenizer(statement);
				ea.nextToken();
				int tot = ea.countTokens();
				//--------------------------------------------
				//This here is the logic for changing to a directory that contains a space in its name
				String tea = ea.nextToken();
				if(tea.charAt(0) == '"' && tea.charAt(tea.length() - 1) != '"')
				{
					do
					{
						if(tot == 0) break;
						tea += " " + ea.nextToken();
						tot--;
					}
					while(tea.charAt(tea.length() - 1) != '"');
					tea = tea.substring(1, tea.length() - 1);
				}
				//---------------------------------------------
				String err = din.readUTF();
				String outp = din.readUTF();
				if(err.equals("true") && outp.equals("true"))
				{
					//means the file exists so start recieving
					fsleep obb = new fsleep(1500);
					tot = ea.countTokens();
					//--------------------------------------------
					//This here is the logic for changing to a directory that contains a space in its name
					tea = ea.nextToken();
					if(tea.charAt(0) == '"' && tea.charAt(tea.length() - 1) != '"')
					{
						do
						{
							if(tot == 0) break;
							tea += " " + ea.nextToken();
							tot--;
						}
						while(tea.charAt(tea.length() - 1) != '"');
						tea = tea.substring(1, tea.length() - 1);
					}
					//---------------------------------------------
					Recv reckless = new Recv(tea, csoc.getInetAddress(), path);
					reckless.reciever();
				}
				else
				{
					//means the file did'nt existed so don't recieve that file
				}
			}
			else if(statement.length() > 4 && statement.toLowerCase().substring(0, 4).equals("copy"))
			{
				System.out.println("Copy");
				Excalibur nexus = new Excalibur(statement, path);
				nexus.startthings();
				dout.writeUTF(nexus.getError());
				System.out.println(nexus.getError());
				if(nexus.getError().equals("true"))
				{
					nexus.sendnow();
				}
			}
			else
			{
				System.out.println("Rest");
				Excalibur nexus = new Excalibur(statement, path);
				nexus.startthings();
				try
				{
					dout.writeUTF(nexus.getError()); //Send the error output to the client
				}
				catch(NullPointerException pex){}
				try
				{
					dout.writeUTF(nexus.getOutput());
				}catch(NullPointerException pex){}
			}
		}while(allow == true);
		csoc.close();
	}
	/*
	public String chdir(String output) throws IOException
	{
		int x = 0, i = output.length();
		while(i > 0)
		{
			if(output.charAt(i-1) == '\\')
			{
				x = i-1;
				break;
			}
			i--;
		}
		return output.substring(0, x);
	}
	*/
}
class fsleep
{
	public fsleep(int x)
	{
		try {
			Thread.sleep(x);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}