package org.overturetool.tools.packworkspace.testing;

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
import java.util.zip.CRC32;
import java.util.zip.Checksum;

public class CrcTable
{
	File checkSumFile;
	HashMap<String, String> classCheckSum = new HashMap<String, String>();
	boolean changeDetected = false;

	public CrcTable(File base) {
		checkSumFile = new File(base.getAbsolutePath(), ".crc");
		loadCheckSums();
	}

	public CrcTable(File base, boolean load) {
		checkSumFile = new File(base.getAbsolutePath(), ".crc");
		if (load)
			loadCheckSums();
	}

	private void loadCheckSums()
	{
		FileReader inputFileReader;
		try
		{
			if (!checkSumFile.exists())
				return;
			inputFileReader = new FileReader(checkSumFile);

			// Create Buffered/PrintWriter Objects
			BufferedReader inputStream = new BufferedReader(inputFileReader);
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

	public void add(String filePath)
	{
		File file = new File(filePath);
		if (file.exists())
		{
			String checkSum = getCheckSum(filePath);
			classCheckSum.put(filePath, checkSum);
			changeDetected = true;
		}
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

	public boolean contains(String fileName, String crc)
	{
		return classCheckSum.containsKey(fileName)
				&& classCheckSum.get(fileName).equals(crc);
	}

	public int size()
	{
		return classCheckSum.size();
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof CrcTable)
		{
			CrcTable other = (CrcTable) obj;
			if (this.size() == other.size())
			{
				for (String fileName : classCheckSum.keySet())
				{
					if (!other.contains(fileName, classCheckSum.get(fileName)))
						return false;
				}
				return true;
			} else
				return false;

		} else
			return super.equals(obj);
	}

}
