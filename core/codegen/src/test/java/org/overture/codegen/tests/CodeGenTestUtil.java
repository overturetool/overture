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
				
		return content.toString().trim();
	}

}
