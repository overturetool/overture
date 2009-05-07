package org.overturetool.tex;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import java.io.Writer;

@SuppressWarnings("unused")
public class ClassExstractorFromTexFiles
{
	private static final String CLASS_START = "class ";//"\\begin{vdm_al}";
	private static final String CLASS_END =  "end ";//"\\end{vdm_al}";
	
	private static final String VDM_START = "\\begin{vdm_al}";
	private static final String VDM_END =  "\\end{vdm_al}";

	public static String[] exstract(String[] files, String outputDir) throws IOException
	{
		File outputDirrectory = new File(outputDir);
		if(outputDir.length()==0  )
			return null;
		
		if(!outputDirrectory.exists())
			outputDirrectory.mkdir();
		
		
		String[] newFiles = new String[files.length];
		for (int i = 0; i < files.length; i++)
		{
			//if (!files[i].endsWith(".tex"))
			//{
				newFiles[i] = files[i];
			//	continue;
			//}
				File f = new File(files[i]);
				
			newFiles[i] = outputDirrectory.getAbsolutePath() + File.separatorChar + f.getName();
			if(newFiles[i].endsWith(".tex"))
				newFiles[i] = newFiles[i]+".vpp";
			
			System.out.println(newFiles[i]);

			FileReader inputFileReader = new FileReader(files[i]);
			FileWriter outputFileReader = new FileWriter(newFiles[i]);

			// Create Buffered/PrintWriter Objects
			BufferedReader inputStream = new BufferedReader(inputFileReader);
			PrintWriter outputStream = new PrintWriter(outputFileReader);

			String inLine = null;
			Boolean skip = true;
			String currentClass = "";
			Boolean enabled = false;
			Boolean texTagsFound= false;
			while ((inLine = inputStream.readLine()) != null)
			{
				if (inLine.trim().startsWith(CLASS_START))
				{
					String classString = inLine.trim().substring(CLASS_START.length()).trim();
					int indexOfInh = classString.indexOf(':');
					int indexOfSpace = classString.indexOf(' ');
					if(indexOfInh >= 0 || indexOfSpace >=0)
					{
						
						if(indexOfInh >= 0)
							currentClass = classString.substring(0,indexOfInh);
						else
							currentClass = classString.substring(0,indexOfSpace);
					
					}else
						currentClass = classString;
					
					enabled = true;
					texTagsFound= true;
					//continue;
				}
				else if (inLine.trim().startsWith(CLASS_END+currentClass))
				{
					outputStream.println(inLine);
					enabled = false;
					continue;
					
				}
				if (inLine.trim().startsWith(VDM_START))
				{
				    enabled = true;
				    outputStream.println("");
				    continue;
				    
				}
				if (inLine.trim().startsWith(VDM_END))
				{
					enabled = false;
					outputStream.println("");
				continue;
				
				}
				
			
				if (enabled)
					outputStream.println(inLine);
				else
					outputStream.println("");
			}
			outputStream.close();
			inputStream.close();
			if(!texTagsFound)
				new File(newFiles[i]).delete();

		}
		return newFiles;
	}

	public static String exstractAsString(String file) throws IOException
	{
			FileReader inputFileReader = new FileReader(file);
			// Create Buffered/PrintWriter Objects
			BufferedReader inputStream = new BufferedReader(inputFileReader);
		
			StringBuilder outputStream =new StringBuilder();

			String inLine = null;
			Boolean skip = true;
			String currentClass = "";
			Boolean enabled = false;
			Boolean texTagsFound= false;
			while ((inLine = inputStream.readLine()) != null)
			{
				if (inLine.trim().startsWith(CLASS_START))
				{
					String classString = inLine.trim().substring(CLASS_START.length()).trim();
					int indexOfInh = classString.indexOf(':');
					int indexOfSpace = classString.indexOf(' ');
					if(indexOfInh >= 0 || indexOfSpace >=0)
					{
						
						if(indexOfInh >= 0)
							currentClass = classString.substring(0,indexOfInh);
						else
							currentClass = classString.substring(0,indexOfSpace);
					
					}else
						currentClass = classString;
					
					enabled = true;
					texTagsFound= true;
					//continue;
				}
				else if (inLine.trim().startsWith(CLASS_END+currentClass))
				{
					outputStream.append("\n"+inLine);
					enabled = false;
					continue;
					
				}
				if (inLine.trim().startsWith(VDM_START))
				{
				    enabled = true;
				    outputStream.append("\n"+"");
				    continue;
				    
				}
				if (inLine.trim().startsWith(VDM_END))
				{
					enabled = false;
					outputStream.append("\n"+"");
				continue;
				
				}
				
			
				if (enabled)
					outputStream.append("\n"+inLine);
				else
					outputStream.append("\n"+"");
			}
			inputStream.close();
			
		return outputStream.toString();
	}
}
