package org.overturetool.tex;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class ClassExstractorFromTexFiles
{
	private static final String CLASS_START = "\\begin{vdm_al}";
	private static final String CLASS_END = "\\end{vdm_al}";

	public static String[] exstract(String[] files) throws IOException
	{
		String[] newFiles = new String[files.length];
		for (int i = 0; i < files.length; i++)
		{
			if (!files[i].endsWith(".tex"))
			{
				newFiles[i] = files[i];
				continue;
			}
			newFiles[i] = files[i] + ".vpp";

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
					enabled = true;
					texTagsFound= true;
					continue;
				}
				else if (inLine.trim().startsWith(CLASS_END))
				{
					enabled = false;
					continue;
				}
			
				if (enabled)
					outputStream.println(inLine);
			}
			outputStream.close();
			inputStream.close();
			if(!texTagsFound)
				new File(newFiles[i]).delete();

		}
		return newFiles;
	}

}
