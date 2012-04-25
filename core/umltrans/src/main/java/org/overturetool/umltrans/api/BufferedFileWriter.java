package org.overturetool.umltrans.api;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class BufferedFileWriter
{
	 BufferedWriter outputStream;
	 String file;

	 public void CreateFile(final String fName)
	{
		file = fName;
		new File(file).getParentFile().mkdirs();
		write("", false);
	}

	 public void CloseFile()
	{
		if (outputStream != null)
			try
			{
				outputStream.close();
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

	public void write(String data, boolean append)
	{
		if (!append)
		{
			FileWriter outputFileReader;
			try
			{
				outputFileReader = new FileWriter(file);

				outputStream = new BufferedWriter(outputFileReader);

			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else
		try
		{
			outputStream.write(data.toString());

		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
