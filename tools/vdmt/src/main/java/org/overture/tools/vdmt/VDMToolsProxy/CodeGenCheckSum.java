package org.overture.tools.vdmt.VDMToolsProxy;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

import org.netbeans.lib.cvsclient.commandLine.command.checkout;

public class CodeGenCheckSum
{
	File checkSumFile;
	HashMap<String, String> classCheckSum = new HashMap<String, String>();
	boolean changeDetected = false;

	public CodeGenCheckSum(File base)
	{
		checkSumFile = new File(base.getAbsolutePath() + File.separatorChar
				+ "target" + File.separatorChar + "vdmtCheckSum.crc");
		loadCheckSums();
	}

	private void loadCheckSums()
	{
		FileReader inputFileReader;
		try
		{
			inputFileReader = new FileReader(checkSumFile);

			// Create Buffered/PrintWriter Objects
			BufferedReader inputStream = new BufferedReader(inputFileReader);
			StringBuilder sb = new StringBuilder();

			String inLine = null;

			while ((inLine = inputStream.readLine()) != null)
			{
				String[] data = inLine.split(";");
				classCheckSum.put(data[0], data[1]);

			}
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

	public List<String> filter(List<String> classes, List<File> files)
	{
		for (File file : files)
		{
			
			for (String className  : getClassesFromFile(file))
			{
				
			
			if (classCheckSum.containsKey(className))
			{
				if (classCheckSum.get(className).equals(
						getCheckSum(file.getAbsolutePath())))
				{
					classes.remove(className);
				}
				else
				{
					String f = "";
				}
			}
			}
		}
		return classes;
	}

	public void saveCheckSums()
	{
		if (!changeDetected)
			return;
		else
			changeDetected = false;
		FileWriter outputFileReader;
		try
		{
			if (checkSumFile.exists())
				checkSumFile.delete();
			outputFileReader = new FileWriter(checkSumFile);
			StringBuilder sb = new StringBuilder();

			for (String key : classCheckSum.keySet())
			{
				sb.append(key + ";" + classCheckSum.get(key) + "\n");
			}

			BufferedWriter outputStream = new BufferedWriter(outputFileReader);
			outputStream.write(sb.toString());
			outputStream.close();
		} catch (IOException e)
		{

		}
	}

	public void addCheckSum(String filePath)
	{
		File file = new File(filePath);
		String className = getClassName(file);
		if (file.exists())
		{

			String checkSum =getCheckSum(filePath);
			for (String name : getClassesFromFile(file))
			{
				classCheckSum.put(name,checkSum );
			}
			
			changeDetected = true;
		}
	}

	private String getClassName(File file)
	{
		return file.getName().substring(0, file.getName().length() - 4);
	}
	
	private List<String> getClassesFromFile(File file)
	{
		List<String> classes = new Vector<String>();
		FileReader inputFileReader;
		try
		{
			inputFileReader = new FileReader(file);

			// Create Buffered/PrintWriter Objects
			BufferedReader inputStream = new BufferedReader(inputFileReader);
			StringBuilder sb = new StringBuilder();

			String inLine = null;

			while ((inLine = inputStream.readLine()) != null)
			{
				String className = null;
				if(inLine.trim().startsWith("class "))
				{
					className = inLine.trim().substring(6);
					if(className.trim().contains(" "))
					className = className.substring(0,className.indexOf(" "));
					
					classes.add(className);
				}

			}
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
		String className = getClassName(file);
		if(!classes.contains(className))
			classes.add(className);
		
		return classes;
	}

	private String getCheckSum(String filePath)
	{
		if (new File(filePath).exists())
		{
			long cs = getChecksumValue(new CRC32(), filePath);
			return new Long(cs).toString();
		} else
			return "";
	}

	public static long getChecksumValue(Checksum checksum, String fname)
	{
		try
		{
			BufferedInputStream is = new BufferedInputStream(
					new FileInputStream(fname));
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

}
