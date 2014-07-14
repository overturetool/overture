package org.overture.codegen.tests.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

public class TestUtils
{	
	public static List<File> getFiles(File file, String extension)
	{
		List<File> files = new Vector<File>();
		for (File f : file.listFiles())
		{
			if (f.isDirectory())
				files.addAll(getFiles(f, extension));
			else if(f.getName().toLowerCase().endsWith(extension))
				files.add(f);
		}
		
		Collections.sort(files, new FileComparator());
		
		return files;
	}
	
	public static List<File> getTestInputFiles(File file)
	{
		List<File> files = new Vector<File>();
		for (File f : file.listFiles())
		{
			Collections.sort(files, new FileComparator());if (f.isDirectory())
				files.addAll(getTestInputFiles(f));
			else if(!f.getName().contains("."))
				files.add(f);
		}
		
		Collections.sort(files, new FileComparator());
		
		return files;
	}
	
	public static String getJavaModuleName(StringBuffer moduleContent)
	{
		int moduleIdx = moduleContent.indexOf("class");
		
		if(moduleIdx == -1)
			moduleIdx = moduleContent.indexOf("interface");
		
		int startClassIdx = moduleContent.indexOf(" ", moduleIdx);
		
		
		int endTemplateClassIdx = moduleContent.indexOf("<", 1+startClassIdx);
		int endClassIdx = moduleContent.indexOf(" ", 1+startClassIdx);
		
		if(endTemplateClassIdx > 0 && endTemplateClassIdx < endClassIdx)
			endClassIdx = endTemplateClassIdx;
		
		String className = moduleContent.substring(1+startClassIdx, endClassIdx);
		
		return className;
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
				
				if(!data.toString().trim().startsWith("*Name Violations*"))
				{
					classes.add(data);
				}
				
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
