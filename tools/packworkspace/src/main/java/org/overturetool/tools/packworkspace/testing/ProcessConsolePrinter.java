package org.overturetool.tools.packworkspace.testing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ProcessConsolePrinter extends Thread
{
	File file = null;
	InputStream stream = null;
	String headerMessage = null;

	public ProcessConsolePrinter(File file, InputStream inputStream) {
		this.file = file;
		this.stream = inputStream;
	}

	public ProcessConsolePrinter(File file, InputStream inputStream,
			String headerMessage) {
		this.file = file;
		this.stream = inputStream;
		this.headerMessage = headerMessage;
	}

	@Override
	public void run()
	{

		String line = null;
		BufferedReader input = new BufferedReader(new InputStreamReader(stream));
		FileWriter outputFileReader;
		try
		{
			outputFileReader = new FileWriter(file, true);
			BufferedWriter outputStream = new BufferedWriter(outputFileReader);
			if (headerMessage != null)
			{
				outputStream.write("\n" + headerMessage + "\n");
				outputStream.flush();
			}
			while ((line = input.readLine()) != null)
			{
				outputStream.write("\n" + line);
				outputStream.flush();
			}
			outputStream.close();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}