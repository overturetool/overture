package org.overture.ide.debug.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.eclipse.swt.SWT;
import org.overture.ide.ui.internal.util.ConsoleWriter;



public class ProcessConsolePrinter extends Thread
{
	ConsoleWriter cw = null;
	InputStream stream = null;
	boolean error = false;

	public ProcessConsolePrinter(Boolean error ,ConsoleWriter cw, InputStream inputStream) {
		this.cw = cw;
		this.stream = inputStream;
		this.error = error;
		setDaemon(true);
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
				{
					if(error){
						cw.ConsolePrint(line, SWT.COLOR_RED);
					}
					else{
						cw.println(line);
					}
					
				}
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
