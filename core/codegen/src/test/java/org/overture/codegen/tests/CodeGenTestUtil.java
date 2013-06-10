package org.overture.codegen.tests;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.overture.codegen.constants.IText;

public class CodeGenTestUtil
{
	
	public static String getFileContent(File file)
	{
		
		StringBuilder content = new StringBuilder();
		try
		{
			
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line = "";
			while ((line = reader.readLine()) != null)
			{				
				content.append(line + IText.NEW_LINE);
			}
			reader.close();
			
		} catch (Exception e)
		{
			return null;
		}
		
		int lastIndex = content.lastIndexOf(IText.NEW_LINE);
		
		if(lastIndex >= 0)
			content.replace(lastIndex, lastIndex + IText.NEW_LINE.length(), "");
		
		return content.toString();
	}

}
