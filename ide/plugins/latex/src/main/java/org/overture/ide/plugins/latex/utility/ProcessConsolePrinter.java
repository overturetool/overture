package org.overture.ide.plugins.latex.utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.overture.ide.utility.ConsoleWriter;

public class ProcessConsolePrinter extends Thread
{
	ConsoleWriter cw =null;
	InputStream stream = null;
	public ProcessConsolePrinter(ConsoleWriter cw, InputStream inputStream) {
		this.cw = cw;
		this.stream = inputStream;
	}
	@Override
	public void run()
	{
		
		String line=null;
		BufferedReader input = new BufferedReader(new InputStreamReader(stream));
		try
		{
			while ((line = input.readLine()) != null) {
				if(cw!=null)
				cw.println(line);
				else
					System.out.println(line);
			}
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
