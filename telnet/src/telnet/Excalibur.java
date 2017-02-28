package telnet;
//This Excalibur is for the remote host
import java.io.*;
import java.util.StringTokenizer;

public class Excalibur extends Thread
{
	private Runner is;//Creating objects of the runner class
	private Runner es;
	private Process an;//Declaring another process
	private String[] cmd;//CMD command will be used by the process an
	private String alloutputerr;//Storage for the error output
	private String alloutput;//Storage for the standard output
	private File path;//works as a path for the directory of the remote host
	private String temp;
	public Excalibur(String args, File path) throws IOException
	{
		cmd = new String[3];
		cmd[0] = "cmd.exe";
		cmd[1] = "/C";
		cmd[2] = args;
		this.path = path;
	}
	public void startthings() throws IOException
	{//there is no path given
		/**
		 * in actual program runs two times. but once for the getting any error message while the other for the error message
		 */
		Runtime rs = Runtime.getRuntime();
		if(cmd[2].length() > 4 && cmd[2].substring(0,4).toLowerCase().equals("copy"))
		{
			StringTokenizer ns = new StringTokenizer(cmd[2]);
			ns.nextToken();
			String tea = ns.nextToken();
			int tot = ns.countTokens();
			//--------------------------------------------
			//This here is the logic for changing to a directory that contains a space in its name
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
			temp = tea;
			//---------------------------------------------
			boolean check = new File(path, temp).exists();
			if(check == true)
			{
				alloutput = "true";
				alloutputerr = "true";
			}
			else
			{
				//Do nothing
				alloutput = "false";
				alloutputerr = "false";
			}
		}
		else
		{
			an = rs.exec(cmd, null, path);
			is = new Runner(an.getInputStream(),"Output");
			es = new Runner(an.getErrorStream(),"Error");
			alloutputerr = es.startmake();
			alloutput = is.startmake();
		}
	}
	public void sendnow()
	{
		Send rfile = new Send();
		rfile.sendthis(path, temp);
	}
	public String getOutput()
	{
		return alloutput;
	}
	public String getError()
	{
		return alloutputerr;
	}
}
class Runner extends Thread
{
	private InputStream is;
	private String Type;
	private String alloutput;
	Runner(InputStream ls, String Type)
	{
		this.is = ls;
		this.Type = Type;
		alloutput = new String("");
	}
	public String startmake()
	{
		try
		{
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			alloutput = new String("");
			while((line = br.readLine()) != null)
			{
				alloutput += Type + " > " + line + "\n";
			}
			return alloutput;
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace();
		}
		return alloutput;
	}
}
