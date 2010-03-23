package org.overture.ide.debug.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.overture.ide.ui.internal.util.ConsoleWriter;



public class ProcessConsolePrinter extends Thread
{
	ConsoleWriter cw = null;
	InputStream stream = null;
	String name = null;

	public ProcessConsolePrinter(String name ,ConsoleWriter cw, InputStream inputStream) {
		this.cw = cw;
		this.stream = inputStream;
		this.name = name;
	}

	@Override
	public void run()
	{

		String line = null;
		BufferedReader input = new BufferedReader(new InputStreamReader(stream));
		try
		{
			while ((line = input.readLine()) != null)
			{
				if (cw != null)
					cw.println(name + ": " +line);
				else
					System.out.println(name + ": " + line);
			}
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
