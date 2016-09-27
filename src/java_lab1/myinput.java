package java_lab1;

import java.io.*;

public class myinput 
{
	public static String read_string()
	{
		BufferedReader br=new BufferedReader( new InputStreamReader(System.in),1);
		String string="";
		try {
			string = br.readLine();
		} catch (IOException ex) 
		{
			System.out.println(ex);
		}
		return string;
	}
	
	public static int read_int()
	{
		return Integer.parseInt(read_string());
	}
}
