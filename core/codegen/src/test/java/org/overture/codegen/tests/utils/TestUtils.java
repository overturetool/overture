package org.overture.codegen.tests.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

public class TestUtils
{	
	public static List<File> getResultFiles(File file)
	{
		List<File> files = new Vector<File>();
		for (File f : file.listFiles())
		{
			if (f.isDirectory())
				files.addAll(getResultFiles(f));
			else if (f.getName().toLowerCase().endsWith(".result"))
				files.add(f);
		}
		return files;
	}
	
	public static String getJavaModuleName(StringBuffer moduleContent)
	{
		int moduleIdx = moduleContent.indexOf("class");
		
		if(moduleIdx == -1)
			moduleIdx = moduleContent.indexOf("interface");
		
		int startClassIdx = moduleContent.indexOf(" ", moduleIdx);
		int endClassIdx = moduleContent.indexOf(" ", 1+startClassIdx);
		String className = moduleContent.substring(1+startClassIdx, endClassIdx);
		
		return className;
	}
	
	public static String readJavaExpFromResultFile(File file) throws IOException
	{
		FileInputStream input = new FileInputStream(file);
		
		String data = "";
		
		int c = 0;
		while ((c = input.read()) != -1)
			data += (char) c;
		
		input.close();
		
		return data;
	}
	
	public static List<StringBuffer> readJavaModulesFromResultFile(File file) throws IOException
	{
		final char DELIMITER_CHAR = '#';
		
		FileInputStream input = new FileInputStream(file);

		List<StringBuffer> classes = new LinkedList<StringBuffer>();
		
		StringBuffer data = new StringBuffer();
		int c = 0;
		while ((c = input.read()) != -1)
		{
			if(c == DELIMITER_CHAR)
			{
				while(input.read() == DELIMITER_CHAR);
				classes.add(data);
				data = new StringBuffer();
			}
			else
			{
				data.append((char) c);
			}
		}
		input.close();

		return classes;
	}
}
