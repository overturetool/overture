package org.overturetool.VDM2JavaCG.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import jp.co.csk.vdm.toolbox.VDM.CGException;

import org.overturetool.VDM2JavaCG.VDM2Java.BackEnd;
import org.overturetool.VDM2JavaCG.VDM2Java.Parser;
import org.overturetool.VDM2JavaCG.VDM2Java.vdm2java;
import org.overturetool.vdmj.definitions.ClassList;

public class TestMain {

	public static void main(String[] args) throws FileNotFoundException, CGException, IOException, ParseException

	{ 
		if (args.length == 0) {
			System.out.println("Please enter the location of the file and output directory :-)");
		}
		else {
			File file = new File(args[0]);
			File file1 = new File(args[1]);
			TestMain.toJava(file1, file);
			System.out.println("A Java source file has been generated from VDM++ file '"+args[0]+"' in directory '"+args[1]+"'");
		}		
	}
	
	public static void toJava(File outputDirectory, File file) throws FileNotFoundException, CGException, IOException, ParseException
	{

	Parser ps = new Parser();
	ClassList classes = ps.startParseFile(file);
	
	    vdm2java vj = new vdm2java();
		BackEnd java = new BackEnd();
		
		if (!outputDirectory.isDirectory())
			try {
				throw new Exception("Output directory not valid: "
						+ outputDirectory.getAbsolutePath());
			} catch (Exception e) {
				 //TODO Auto-generated catch block
				e.printStackTrace();
			}

		String outputPath = outputDirectory.getAbsolutePath();
		if (!outputPath.endsWith(new Character(File.separatorChar).toString()))
			outputPath += File.separatorChar;
		java.Save(outputPath, vj.Transform(classes), vj.GetLog());
	}


	public static List<File> getFilesFromVDMToolsProjectFile(File projectFile)
	throws Exception
	{
	FileReader inputFileReader = new FileReader(projectFile.getAbsoluteFile());
	BufferedReader inputStream = new BufferedReader(inputFileReader);

	String result = "";
	String inLine = null;
	while ((inLine = inputStream.readLine()) != null)
	result += inLine;
	inputStream.close();
	List<File> files = new Vector<File>();
	final String VDM_TOOLS_PROJECT_FILE_INFO = "e2,m4,filem";
	String[] tmp = result.substring(result.indexOf(VDM_TOOLS_PROJECT_FILE_INFO))
		.split(VDM_TOOLS_PROJECT_FILE_INFO);

	for (int i = 1; i < tmp.length; i++)
	{

	String[] fileData = tmp[i].split(",");
	Integer length = Integer.parseInt(fileData[0]);
	String filePath = fileData[1].substring(0, length);

	if (!filePath.contains("./") && new File(filePath).exists())
		files.add(new File(filePath));
	else
	{
		filePath = filePath.replace("../", ":");
		int countUp = filePath.split(":").length;
		filePath = filePath.substring(filePath.lastIndexOf(":") + 1);
		String f = filePath.replace('/', File.separatorChar);

		File parentFile = projectFile.getParentFile();
		for (int j = 0; j < countUp - 1; j++)
		{
			parentFile = parentFile.getParentFile();
		}

		files.add(new File(parentFile, f));
	}

	}
	return files;

	}
}