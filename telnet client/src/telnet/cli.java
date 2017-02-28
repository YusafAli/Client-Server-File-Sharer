package telnet;
//the host is here
import java.net.*;
import java.util.StringTokenizer;

import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import java.io.*;
class cli
{
	public static void main(String args[]) throws Exception
	{
		File path = new File(System.getProperty("user.dir"));
		System.out.println("User Login");
		Socket soc = new Socket();
		int portno = 24000;
		String tempiler = JOptionPane.showInputDialog("Enter Port Number");
		//System.out.println(tempiler);
		String ipaddr = JOptionPane.showInputDialog("Enter Ip address");
		System.out.println(tempiler);
		try
		{
			tempiler.equals(null);
			portno = Integer.parseInt(tempiler);
		}
		catch(NullPointerException amon)
		{
			System.out.println("You entered the port number incorrectly"
					+ "\nit is set to default (24000)");
		}
		try
		{
			soc.close();
			//System.out.println(portno);
			soc = new Socket(ipaddr, portno);
		}catch(UnknownHostException uhe)
		{
			System.out.println("The ip address you entered is not a valid address");
			return;
		}
		catch(IllegalArgumentException iae)
		{
			System.out.println("The port range is 1024 ~ 65535");
			return;
		}
		catch(ConnectException ce)
		{
			System.out.println("No Connections on this port or address available");
			return;
		}
		InetAddress IP = InetAddress.getLocalHost();
		System.out.println("Ip Address of the Computer:\t" + soc.getInetAddress());
		System.out.println("Ip Address of this User:\t" + IP.getHostAddress());
		String LoginName;
		String Password;
		String Command;
		DataInputStream din=new DataInputStream(soc.getInputStream());
		DataOutputStream dout=new DataOutputStream(soc.getOutputStream());
		BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
		System.out.println("You are The User");
		System.out.println("Your Username and Password?");
		LoginName = Password = null;
		//The following line gets the username as input
		LoginName = JOptionPane.showInputDialog("Your username", null);
		//The following procedure will be used to get the password
		JPasswordField pf = new JPasswordField();
		int okCxl = JOptionPane.showConfirmDialog(null, pf, "Enter Password", JOptionPane.OK_CANCEL_OPTION);
		if(okCxl == JOptionPane.OK_OPTION)
		{
			Password = new String(pf.getPassword());
		}
		//If cancel is chosen, then null will be sent over the connection.
		//Rendering it incorrect.
		//Server rejects the password and bam! the socket is automatically closed
		//Now sending this username and password to the server for verification
		dout.writeUTF(LoginName);
		dout.writeUTF(Password);
		//This runtime is blocked at this if statement as it is trying to read data from the server.
		//It will not proceed untill the client is authorized by the server
		int online = 1;
		if (din.readUTF().equals("ALLOWED"))
		{
			do
			{
				System.out.println("User Prompt");
				System.out.println("Enter Command Enabled");
				Command = br.readLine();
				if(Command.equals("?"))
				{
					//This will be used to print out all the known commands that can work on this software
					System.out.println("Commands Help");
					System.out.println("This is an implemented program, "
							+ "\nit may or may not support all commands that are expected"
							+ "\nThis program can run single line commands such as\n"
							+ "cd, !cd, dir, !dir etc"
							+ "\nas they require no runtime input");
					System.out.println("This program can copy a file from the remote host to this host");
					System.out.println("The file will be copied to the active directory from where this software is run");
					System.out.println("You must specify the filename with correct extension only for destination");
					System.out.println("You must specify the filename with extension along path[if not from current directory]");
					/*
					System.out.println("This program can also copy file to the remote host[current directory]");
					System.out.println("You must specify correct destination filename and extension");
					System.out.println("You must specify correct source filename and extension");
					*/
					System.out.println("Commands:-");
					System.out.println("cd\t\tprint current directory");
					System.out.println("cd..\t\tchange directory to parent directory");
					System.out.println("cd \"dir_name\"\t\tchange to the specified directory[Experimental]");
					System.out.println("dir\t\tshow all contents of the current directory[remote host]");
					System.out.println("dir dir_name\t\tshow all contents of the specified directory[remote host]");
					System.out.println("copy src_filename dest_filename\tcopy a file from remote host");
					//System.out.println("rcopy src_filename dest_filename\tcopy a file to remote host");
					System.out.println("quit or exit\t\texit this program");
					System.out.println("md\t\tmake directory in the remote host");
					System.out.println("rd\t\tdelete directory in the remote host [replacement for deltree]");
					System.out.println("!dir\t\tshow all contents of current directory[this host]");
					System.out.println("!dir dir_name\t\tprint all contents of the specified directory[this host]");
				}
				else if(Command.toLowerCase().equals("quit") || Command.toLowerCase().equals("exit"))
				{
					System.out.println("Exiting the server and terminating connections");
					online = 0;
					dout.writeUTF("quit");
					break;
				}
				else if(Command.equals("!dir"))
				{
					Excalibur eax = new Excalibur("dir", path);
					eax.startthings();
					System.out.println(eax.getError());
					System.out.println(eax.getOutput());
				}
				else if(Command.length() > 4 && Command.substring(0, 4).equals("!dir"))
				{
					Excalibur eax = new Excalibur(Command.substring(1), path);
					eax.startthings();
					System.out.println(eax.getError());
					System.out.println(eax.getOutput());
				}
				else if(Command.equals("!cd.."))
				{
					path = new File(path.getParent());
				}
				else if(Command.equals("cd.."))
				{
					dout.writeUTF(Command);
				}
				else if(Command.equals("!cd"))
				{
					Excalibur crntdir = new Excalibur("cd", path);
					crntdir.startthings();
					System.out.println(crntdir.getError());
					System.out.println(crntdir.getOutput());
				}
				else if(Command.length() > 4 && Command.substring(0, 3).equals("!cd"))
				{
					StringTokenizer ns = new StringTokenizer(Command);
					int tot = ns.countTokens();
					ns.nextToken();
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
					File temp = new File(path.getAbsolutePath() + "/" + tea);
					if(temp.exists())
					{
						path = new File(path.getAbsolutePath() + "/" + tea);
					}
					else
					{
						//Do nothing
						System.out.println("Directory is unchanged");
					}
					Command = "cd";
					Excalibur nexus = new Excalibur(Command, path);
					nexus.startthings();//Interrupted Exception is because of this
					System.out.println(nexus.getError());
					System.out.println(nexus.getOutput());
				}
				else if(Command.length() > 4 && Command.toLowerCase().substring(0, 4).equals("copy"))
				{
					dout.writeUTF(Command);
					StringTokenizer ea = new StringTokenizer(Command);
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
					String err = din.readUTF();
					if(err.equals("true"))
					{
						//means the file exists so start recieving
						fsleep obb = new fsleep(1500);
						Recv reckless = new Recv(tea, soc.getInetAddress(), path);
						reckless.reciever();
					}
					else
					{
						//means the file did'nt existed so don't recieve that file
						System.out.println("The file is not copied because the file does not exist");
					}
					
				}
				else if(Command.length() > 5 && Command.toLowerCase().substring(0, 5).equals("rcopy"))
				{
					//first tell the remote host that you are going to send the file
					//then the file will be checked if that it exists or not
					//then call the send class
					//send data that you have opened the send.soc
					//at remote site, open the port and recieve file. after that confirm that the file was copied
					dout.writeUTF(Command);
					Excalibur sword = new Excalibur(Command.substring(1) ,path);
					sword.startthings();
					dout.writeUTF(sword.getError());
					dout.writeUTF(sword.getOutput());
					if(sword.getError().equals("true") && sword.getOutput().equals("true"))
					{
						sword.sendnow();//true means that the file exists and is ready to send
					}
					else 
					{
						System.out.println("Sending to remote host failed, file may not exist");
					}
				}
				else if(Command.length() > 2 && Command.charAt(0) == '!')
				{
					Excalibur ink = new Excalibur(Command.substring(1), path);
					ink.startthings();
				}
				else
				{
					dout.writeUTF(Command);
					String err = din.readUTF();
					System.out.print(err);
					String outp = din.readUTF();
					System.out.print(outp);
				}
				
			}
			while(online == 1);
		}
		else
		{
			System.out.println("Your password or username is incorrect");
		}
		soc.close();
	}
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