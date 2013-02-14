package org.overturetool.umltrans.Main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class FileCompare
{
	public static boolean compare(File file1, File file2) throws IOException
	{
		
		
		if(!file1.exists())
			return false;
		FileReader f1Reader = new FileReader(file1);
		BufferedReader f1Stream = new BufferedReader(f1Reader);
		
		
		FileReader f2Reader = new FileReader(file2);
		BufferedReader f2Stream = new BufferedReader(f2Reader);
		boolean equal = true;
		
		try
		{
		String inLine1 = null;
		String inLine2 = null;
		int lines = 0;
		while ((inLine1 = f1Stream.readLine()) != null)
		{
			lines++;
			if(inLine1.trim().length()==0)
				continue;
			while((inLine2 = f2Stream.readLine()) != null && inLine2.trim().length()==0)
			{
				
			}
			
			if(inLine2==null)
				return false;
		
			if(inLine2!=null &&  inLine1.equals(inLine2))
				continue;
			else
				equal=false;
		
		}
		if(lines==0)
			return false;
		}finally{
			f1Stream.close();
			f2Stream.close();
		}
		
		return equal;
	}
	
	public boolean compareVpp(String file1, String file2)
	{
		return false;
	}
}
