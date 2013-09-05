package org.overture.tools.maven.astcreator.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.channels.FileChannel;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

public class Util
{
	public static Long getCheckSum(String filePath)
	{
		if (new File(filePath).exists())
		{
			long cs = getChecksumValue(new CRC32(), filePath);
			return new Long(cs);
		} else
			return new Long(0);
	}

	public static long getChecksumValue(Checksum checksum, String fname)
	{
		try
		{
			BufferedInputStream is = new BufferedInputStream(new FileInputStream(fname));
			byte[] bytes = new byte[1024];
			int len = 0;

			while ((len = is.read(bytes)) >= 0)
			{
				checksum.update(bytes, 0, len);
			}
			is.close();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		return checksum.getValue();
	}

	public static void writeFile(File file, String content) throws IOException
	{
		FileWriter outFile = new FileWriter(file, false);
		PrintWriter out = new PrintWriter(outFile);
		try
		{
			out.write(content);
		} finally
		{
			out.close();
		}
	}

	public static String readFile(File file) throws IOException
	{
		String content = "";
		FileReader inputFileReader;
		BufferedReader inputStream = null;
		try
		{
			if (!file.exists())
				return "";
			inputFileReader = new FileReader(file);

			// Create Buffered/PrintWriter Objects
			inputStream = new BufferedReader(inputFileReader);
			String inLine = null;

			while ((inLine = inputStream.readLine()) != null)
			{
				content += inLine;

			}
			return content;

		} finally
		{
			if (inputStream != null)
			{
				inputStream.close();
			}
		}
	}
	
	
	public static void copyFile(File sourceFile, File destFile) throws IOException {
	    if(!destFile.exists()) {
	        destFile.createNewFile();
	    }

	    FileChannel source = null;
	    FileChannel destination = null;

	    try {
	        source = new FileInputStream(sourceFile).getChannel();
	        destination = new FileOutputStream(destFile).getChannel();
	        destination.transferFrom(source, 0, source.size());
	    }
	    finally {
	        if(source != null) {
	            source.close();
	        }
	        if(destination != null) {
	            destination.close();
	        }
	    }
	}
}
