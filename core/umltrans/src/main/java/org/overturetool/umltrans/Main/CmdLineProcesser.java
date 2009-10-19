package org.overturetool.umltrans.Main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import jp.co.csk.vdm.toolbox.VDM.CGException;

import org.overturetool.tex.ClassExstractorFromTexFiles;
import org.overturetool.umltrans.xml.XmlParser;

public class CmdLineProcesser extends CmdLineHelper
{
	final String CMD_REMOVE_TEX = "-r";
	final String OUTPUT_PATH = "-o";
	final String OUTPUT_FILE = "-ofile";
	final String CMD_TO_UML = "-uml";
	final String CMD_TO_VPP = "-vpp";
	final String CMD_PRINT_XML = "-x";

	public CmdLineProcesser()
	{
		super.paramterTypes = new String[] { OUTPUT_PATH, OUTPUT_FILE,
				CMD_REMOVE_TEX, CMD_TO_UML, CMD_TO_VPP, CMD_PRINT_XML };
	}

	@Override
	public void printHelp()
	{
		// System.out.println("Overture VDM Traces test");
		// System.out.println("Usage: org.overture.traces.jar [options] [specfile{,specfile}]");
		// //
		// System.out.println("If no options are entered the GUI will show.");
		// System.out.println();
		// System.out.println("OPTIONS for command line usage:");
		// System.out.println(" -outputPath Path to a folder where results will be stored.");
		// System.out.println(" -c          Class names to be concidered {,classname}.");
		// System.out.println(" -max        Maximum used in expansion of statements.");
		// System.out.println(" -toolbox    The type of toolbox which should be used.[VDMTools | VDMJ]");
		// System.out.println("     VDMTools: Requires VDMTools to be installed and an additional option");
		// System.out.println("               -VDMToolsPath to be set to the specific file.");
		// System.out.println("     VDMJ:     Requires VDMJ to be in the class path.");
		// System.out.println();
		// System.out.println("Example of usege:");
		// System.out.println("org.overture.traces.jar Will result in a GUI being shown.");
		// System.out.println();
		// System.out.println("org.overture.traces.jar -outputPath c:\\ -c A,B -max 3 -toolbox VDMJ a.vpp,b.vpp");
		// System.out.println("  Will result in the classes A and B being tested with VDMJ");
		// System.out.println();
		// System.out.println("org.overture.traces.jar -outputPath c:\\ -c A,B -max 3 -toolbox VDMTools -VDMToolsPath vppgde.exe a.vpp,b.vpp");
		// System.out.println("  Will result in the classes A and B being tested with VDM Tools");

		System.out.print("UMLTRANS: You must specify direction\n");
		System.out.print("\nUsage: org.overture.umltrans.jar (-r | -v | -u) option file1 ; file2 ; ...\n\n");
		System.out.print(CMD_REMOVE_TEX
				+ " create vpp files from VDM tex files ("+CMD_REMOVE_TEX+" "+OUTPUT_FILE+")\n");
		System.out.print(CMD_TO_VPP + " create VDM vpp file from UML model ("+CMD_TO_UML+" "+OUTPUT_PATH+")\n");
		System.out.print(CMD_TO_UML
				+ " create UML model from VDM vpp files ("+CMD_TO_VPP+" "+OUTPUT_PATH+")\n\n");
		System.out.print(CMD_PRINT_XML
				+ " Print XML fils as VDM XML Doc operation\n\n");
		// System.out.print("\n Example: org.overture.umltrans.jar -u file1.vpp;file2.vpp\n");
	}

	@Override
	protected void handleCommand(Hashtable<String, String> parameters,
			List<File> files) throws Exception
	{
		Long beginTime = System.currentTimeMillis();
		if (containsKeys(
				parameters,
				new String[] { OUTPUT_PATH, CMD_REMOVE_TEX }))
			removeTex(parameters.get(OUTPUT_PATH), files);
		if (containsKeys(parameters, new String[] { OUTPUT_FILE, CMD_TO_UML }))
			toUml(new File(parameters.get(OUTPUT_FILE)), files);
		if (containsKeys(parameters, new String[] { OUTPUT_PATH, CMD_TO_VPP }))
			toVpp(new File(parameters.get(OUTPUT_PATH)), files);
		if (containsKeys(parameters, new String[] { OUTPUT_PATH, CMD_PRINT_XML }))
			printXmlDoc(files.get(0));
		
		System.out.println("Command completed in "+(double) (System.currentTimeMillis() - beginTime) / 1000 + " secs");
	}

	public static void removeTex(String outputDirectory, List<File> files)
			throws IOException
	{
		File output = new File(outputDirectory + File.separatorChar + "tmp");
		output.mkdirs();
		ClassExstractorFromTexFiles.exstract(files, output);
	}

	public static void toUml(File outputFile, List<File> files)
			throws FileNotFoundException, CGException, IOException
	{
		// for (String string : files)
		// {
		// System.out.println("Input file: " + string);
		// }

		for (int i = 0; i < files.size(); i++)
		{
			if (files.get(i).getName().endsWith(".prj"))
			{
				// VDM Tools project detected
				try
				{
					List<File> list = GetFilesFromVDMToolsProjectFile(
							files.get(i));
					
//					for (int j = 0; j < list.size(); j++)
//					{
//						if(list.get(j).contains("org\\overturetool\\umltrans\\test"))
//							list.remove(j);
//					}
					
					files.addAll(list);
					break;
				} catch (Exception e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		Translator.TransLateTexVdmToUml(files, outputFile);
		 
		

	}

	public static void toVpp(File outputDirectory, List<File> files)
			throws Exception
	{

		for (File file : files)
		{
			Translator.TransLateUmlToVdm(file, outputDirectory);
		}

		System.out.println("Done: " + outputDirectory);

	}

	public static void printXmlDoc(File string) throws Exception
	{

		XmlParser.Parse(string.getAbsolutePath(), true);

	}

	public static List<File> GetFilesFromVDMToolsProjectFile(File projectFile)
			throws Exception
	{
		FileReader inputFileReader = new FileReader(
				projectFile.getAbsoluteFile());
		BufferedReader inputStream = new BufferedReader(inputFileReader);

		String result = "";
		String inLine = null;
		while ((inLine = inputStream.readLine()) != null)
			result += inLine;
		inputStream.close();
		List<File> files = new Vector<File>();
		final String VDM_TOOLS_PROJECT_FILE_INFO = "e2,m4,filem";
		String[] tmp = result.substring(
				result.indexOf(VDM_TOOLS_PROJECT_FILE_INFO)).split(
				VDM_TOOLS_PROJECT_FILE_INFO);

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
				filePath = filePath.substring(filePath.lastIndexOf(":")+1);
				String f = filePath.replace('/', File.separatorChar);

				File parentFile = projectFile.getParentFile();
				for (int j = 0; j < countUp-1; j++)
				{
					parentFile = parentFile.getParentFile();
				}
				
				files.add(new File(parentFile, f));
			}

		}
		return files;

	}

}
