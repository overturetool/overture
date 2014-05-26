package org.overture.codegen.tests;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

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
				content.append(line + "\n");
			}
			reader.close();
			
		} catch (Exception e)
		{
			return null;
		}
				
		return content.toString().trim();
	}

}
