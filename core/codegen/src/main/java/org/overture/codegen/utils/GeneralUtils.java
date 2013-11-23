package org.overture.codegen.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

public class GeneralUtils
{
	public static StringBuffer readFromFile(String relativepath) throws IOException
	{
		InputStream input = GeneralUtils.class.getResourceAsStream(relativepath);

		if (input == null)
			return null;

		StringBuffer data = new StringBuffer();
		int c = 0;
		while ((c = input.read()) != -1)
		{
			data.append((char) c);
		}
		input.close();

		return data;
	}
	
	public static List<File> getFiles(File folder)
	{
		File[] listOfFiles = folder.listFiles();
		
		List<File> fileList = new LinkedList<File>();

		for (File file : listOfFiles)
			if (file.isFile())
				fileList.add(file);
		
		return fileList;
	}
}
