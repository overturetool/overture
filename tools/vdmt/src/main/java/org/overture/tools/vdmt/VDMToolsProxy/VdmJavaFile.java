package org.overture.tools.vdmt.VDMToolsProxy;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class VdmJavaFile
{
	final String IMPORT_TAG_START = "// ***** VDMTOOLS START Name=imports";
	final String IMPORT_TAG_END = "// ***** VDMTOOLS END Name=imports";
	final String UTIL_RUNTIME_ERROR = "UTIL.RunTime(\"Run-Time Error:Can not evaluate an error statement\");";
	final String THROW_CGEXCEPTION = "throw new CGException();";
	final String ELSE = "else {";

	/*
	 * 
	 * // VDMTOOLS START Name=imports KEEP=NO
	 * 
	 * import jp.co.csk.vdm.toolbox.VDM.; import java.util.; // VDMTOOLS END
	 * Name=imports
	 */
	public VdmJavaFile(File file)
	{
		this.file = file;
	}

	private File file;

	/*
	 * Add packages to the code generated file.
	 */
	public void addPackages(List<String> packages)
	{

		ArrayList<String> packagesInFile = new ArrayList<String>();
		try
		{
			FileReader inputFileReader = new FileReader(file);

			// Create Buffered/PrintWriter Objects
			BufferedReader inputStream = new BufferedReader(inputFileReader);
			StringBuilder sb = new StringBuilder();

			String inLine = null;
			Boolean tagFound = false;
			Boolean textFound = false;
			while ((inLine = inputStream.readLine()) != null)
			{
				if (inLine.length() == 0 && !textFound)
					continue;
				textFound = true;
				if (inLine.trim().startsWith(IMPORT_TAG_START))
				{
					sb.append("\n" + inLine);
					tagFound = true;
					continue;

				} else if (inLine.trim().startsWith(IMPORT_TAG_END))
				{

					for (String packageName : packages)
					{
						if (!packagesInFile.contains(packageName))
						{
							sb.append("\n" + createImportLine(packageName));
						}
					}
					sb.append("\n" + inLine);
					tagFound = false;
					continue;
				}
				if (tagFound && inLine.length() > 0)
				{
					packagesInFile.add(parseImport(inLine));
				}

				sb.append("\n" + inLine);

			}
			inputStream.close();
			FileWriter outputFileReader = new FileWriter(file);
			BufferedWriter outputStream = new BufferedWriter(outputFileReader);
			outputStream.write(sb.toString());
			outputStream.close();
		} catch (IOException e)
		{
			e.printStackTrace();
		}

	}

	private String parseImport(String line)
	{
		String packageName = line.replace("import ", "").trim();
		int lastLetter = 0;
		for (int i = packageName.length() - 1; i > 0; i--)
		{
			if (Character.isLetter(packageName.charAt(i)))
			{
				lastLetter = i + 1;
				break;
			}
		}
		packageName = packageName.substring(0, lastLetter);
		return packageName;
	}

	private String createImportLine(String packageName)
	{
		return "import " + packageName.trim() + ".*;";
	}

	/**
	 * Replace VDM error / undefined with a CGException instead of return. This
	 * is useful if there is used error /undefined in a cases statement together
	 * with interface types since a function them may return an instance of an
	 * interface which is illegal in Java.
	 */
	public void replaceUtilRuntimeErrorWithCGException()
	{

		try
		{
			FileReader inputFileReader = new FileReader(file);

			// Create Buffered/PrintWriter Objects
			BufferedReader inputStream = new BufferedReader(inputFileReader);
			StringBuilder sb = new StringBuilder();

			String inLine = null;
			String previousInLine = "";
			String previousInLine2 = "";
			Boolean tagFound = false;

			while ((inLine = inputStream.readLine()) != null)
			{
				if (tagFound)
				{
					sb.append("\n" + THROW_CGEXCEPTION + " // " + inLine);
					tagFound = false;

				} else if (inLine.trim().contains(UTIL_RUNTIME_ERROR)
						&& previousInLine2.contains(ELSE))
				{
					sb.append("\n" + inLine);
					tagFound = true;

				} else
					sb.append("\n" + inLine);

				previousInLine2 = previousInLine;
				previousInLine = inLine;

			}
			inputStream.close();
			FileWriter outputFileReader = new FileWriter(file);
			BufferedWriter outputStream = new BufferedWriter(outputFileReader);
			outputStream.write(sb.toString());
			outputStream.close();
		} catch (IOException e)
		{
			e.printStackTrace();
		}

	}
}
