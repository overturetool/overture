package org.overturetool.tex;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import java.io.Writer;
import java.util.List;
import java.util.Vector;

@SuppressWarnings("unused")
public class ClassExstractorFromTexFiles
{
	private static final String CLASS_START = "class ";// "\\begin{vdm_al}";
	private static final String SYSTEM_START = "system ";
	private static final String CLASS_END = "end ";// "\\end{vdm_al}";

	private static final String VDM_START = "\\begin{vdm_al}";
	private static final String VDM_END = "\\end{vdm_al}";

	/**
	 * Creates a temporary file where all latex tags have been removed. Line
	 * numbers are preserved by insertion of empty lines
	 * 
	 * @param files
	 *            The files where LaTex should be removed from
	 * @param outputDirrectory
	 *            The directory to put the temporary files
	 * @return A list of Files where the LaTex text have been removed from
	 * @throws IOException
	 *             If any file error occures
	 */
	public static List<File> exstract(List<File> files, File outputDirrectory)
			throws IOException
	{
		if (outputDirrectory == null)
			return null;

		if (!outputDirrectory.exists())
			outputDirrectory.mkdir();

		List<File> newFiles = new Vector<File>();
		for (int i = 0; i < files.size(); i++)
		{
			File currentFile = files.get(i);

			currentFile = new File(outputDirrectory, currentFile.getName());
			if (currentFile.getName().endsWith(".tex"))
				currentFile = new File(currentFile.getAbsolutePath() + ".vpp");

			newFiles.add(currentFile);
			System.out.println(currentFile);

			FileReader inputFileReader = new FileReader(files.get(i));
			FileWriter outputFileReader = new FileWriter(currentFile);

			// Create Buffered/PrintWriter Objects
			BufferedReader inputStream = new BufferedReader(inputFileReader);
			PrintWriter outputStream = new PrintWriter(outputFileReader);

			String inLine = null;
			Boolean skip = true;
			String currentClass = "";
			Boolean enabled = false;
			Boolean texTagsFound = false;
			while ((inLine = inputStream.readLine()) != null)
			{
				if (inLine.trim().startsWith(CLASS_START)
						|| inLine.trim().startsWith(SYSTEM_START))
				{
					String classString = "";

					if (inLine.trim().startsWith(CLASS_START))
						classString = inLine.trim()
								.substring(CLASS_START.length())
								.trim();
					else //if (inLine.trim().startsWith(SYSTEM_START))
						classString = inLine.trim()
								.substring(SYSTEM_START.length())
								.trim();

					int indexOfInh = classString.indexOf(':');
					int indexOfSpace = classString.indexOf(' ');
					if (indexOfInh >= 0 || indexOfSpace >= 0)
					{

						if (indexOfInh >= 0)
							currentClass = classString.substring(0, indexOfInh);
						else
							currentClass = classString.substring(0,
									indexOfSpace);

					} else
						currentClass = classString;

					enabled = true;
					texTagsFound = true;
					// continue;
				} else if (inLine.trim().startsWith(CLASS_END + currentClass))
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
			if (!texTagsFound)
				newFiles.get(i).delete();

		}
		return newFiles;
	}

	public static String exstractAsString(String file) throws IOException
	{
		FileReader inputFileReader = new FileReader(file);
		// Create Buffered/PrintWriter Objects
		BufferedReader inputStream = new BufferedReader(inputFileReader);

		StringBuilder outputStream = new StringBuilder();

		String inLine = null;
		Boolean skip = true;
		String currentClass = "";
		Boolean enabled = false;
		Boolean texTagsFound = false;
		while ((inLine = inputStream.readLine()) != null)
		{
			if (inLine.trim().startsWith(CLASS_START)
					|| inLine.trim().startsWith(SYSTEM_START))
			{
				String classString = "";

				if (inLine.trim().startsWith(CLASS_START))
					classString = inLine.trim()
							.substring(CLASS_START.length())
							.trim();
				else //if (inLine.trim().startsWith(SYSTEM_START))
					classString = inLine.trim()
							.substring(SYSTEM_START.length())
							.trim();
				int indexOfInh = classString.indexOf(':');
				int indexOfSpace = classString.indexOf(' ');
				if (indexOfInh >= 0 || indexOfSpace >= 0)
				{

					if (indexOfInh >= 0)
						currentClass = classString.substring(0, indexOfInh);
					else
						currentClass = classString.substring(0, indexOfSpace);

				} else
					currentClass = classString;

				enabled = true;
				texTagsFound = true;
				// continue;
			} else if (inLine.trim().startsWith(CLASS_END + currentClass))
			{
				outputStream.append("\n" + inLine);
				enabled = false;
				continue;

			}
			if (inLine.trim().startsWith(VDM_START))
			{
				enabled = true;
				outputStream.append("\n" + "");
				continue;

			}
			if (inLine.trim().startsWith(VDM_END))
			{
				enabled = false;
				outputStream.append("\n" + "");
				continue;

			}

			if (enabled)
				outputStream.append("\n" + inLine);
			else
				outputStream.append("\n" + "");
		}
		inputStream.close();

		return outputStream.toString();
	}
}
