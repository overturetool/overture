package org.overturetool.tools.packworkspace.testing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.overturetool.vdmj.ExitStatus;

public class TestStatus
{
	File file;

	public ExitStatus statusParse = null;
	public ExitStatus statusTypeCheck = null;
	public ExitStatus statusPo = null;
	public ExitStatus statusInterpreter = null;
	public Integer poCount = 0;
	public boolean isFaild = false;

	public TestStatus(File base, boolean load) {
		file = new File(base.getAbsolutePath(), ".status");
		if (load)
			load();

	}

	private void load()
	{
		FileReader inputFileReader;
		try
		{
			if (!file.exists())
				return;
			inputFileReader = new FileReader(file);

			// Create Buffered/PrintWriter Objects
			BufferedReader inputStream = new BufferedReader(inputFileReader);

			statusParse = getStatus(getLine(inputStream));
			statusTypeCheck = getStatus(getLine(inputStream));
			statusPo = getStatus(getLine(inputStream));
			statusInterpreter = getStatus(getLine(inputStream));
			poCount = Integer.parseInt(getLine(inputStream));
			isFaild = Boolean.parseBoolean(getLine(inputStream));
			inputStream.close();
		} catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private ExitStatus getStatus(String line)
	{
		if(line.equals("null"))
			return null;
		
		return ExitStatus.valueOf(line);
	}

	private String getLine(BufferedReader inputStream) throws IOException
	{
		String inLine = null;
		inLine = inputStream.readLine();
		return inLine;
	}

	public void save()
	{
		FileWriter outputFileReader;
		try
		{
			if (file.exists())
				file.delete();
			outputFileReader = new FileWriter(file);
			StringBuilder sb = new StringBuilder();

			sb.append(statusParse + "\n");
			sb.append(statusTypeCheck + "\n");
			sb.append(statusPo + "\n");
			sb.append(statusInterpreter + "\n");
			sb.append(poCount + "\n");
			sb.append(isFaild + "\n");

			BufferedWriter outputStream = new BufferedWriter(outputFileReader);
			outputStream.write(sb.toString());
			outputStream.close();
		} catch (IOException e)
		{

		}
	}
}
