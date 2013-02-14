package org.overturetool.umltrans.Main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import java.util.List;
import java.util.Vector;

import jp.co.csk.vdm.toolbox.VDM.CGException; //import junit.framework.Assert;

import org.overturetool.api.xml.XmlDocument;
import org.overturetool.parser.imp.OvertureParser;
import org.overturetool.parser.imp.ParserError;
import org.overturetool.tex.ClassExstractorFromTexFiles;
import org.overturetool.umltrans.StatusLog;
import org.overturetool.umltrans.uml2vdm.Oml2Vpp;
import org.overturetool.umltrans.uml2vdm.Uml2Vdm;
import org.overturetool.umltrans.uml2vdm.Xml2UmlModel;
import org.overturetool.umltrans.vdm2uml.Uml2XmiEAxml;
import org.overturetool.umltrans.vdm2uml.Vdm2Uml;
import org.overturetool.umltrans.xml.XmlParser;

public class CmdLineProcesser
{
	public static void setOutput(PrintWriter outputWriter)
	{
		StatusLog.out = outputWriter;
	}

	public static void removeTex(File outputDirectory, List<File> files)
			throws IOException
	{
		File output = new File(outputDirectory, "tmp");
		output.mkdirs();
		ClassExstractorFromTexFiles.exstract(files, output);
	}

	public static void toUml(File outputFile, List<File> files)
			throws FileNotFoundException, CGException, IOException,
			ParseException
	{

		List<File> selectedFiles = new Vector<File>();
		for (int i = 0; i < files.size(); i++)
		{
			if (files.get(i).isFile()
					&& files.get(i).getName().endsWith(".prj"))
			{
				// VDM Tools project detected
				try
				{
					List<File> list = getFilesFromVDMToolsProjectFile(files.get(i));

					selectedFiles.addAll(list);
					break;
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			} else if (files.get(i).isFile()
					&& !files.get(i).getName().startsWith("."))
				selectedFiles.add(files.get(i));
			else if (files.get(i).isDirectory())
			{
				for (File f : files.get(i).listFiles())
				{
					if (f.isFile())
						selectedFiles.add(f);
					else
						System.out.println("Dir found in file list skipping: "
								+ f.getName());
				}
			}
		}

		// Translator.TransLateTexVdmToUml(selectedFiles, outputFile);
		StringBuilder sb = new StringBuilder();
		List<Integer> fileLineOffset = new Vector<Integer>();

		for (File file : selectedFiles)
		{

			String data = "\n"
					+ ClassExstractorFromTexFiles.exstractAsString(file.getAbsolutePath());

			int lineCount = data.split("\n").length;

			if (fileLineOffset.size() > 0)
				fileLineOffset.add(fileLineOffset.get(fileLineOffset.size() - 1)
						+ lineCount);
			else
				fileLineOffset.add(lineCount);

			if(file.getName().toLowerCase().startsWith("io"))
			{
				
			}
			else
			{
				sb.append(data);
			}
		}

		// return Translator.TranslateVdmToUml(sb.toString(),
		// outputFile.getAbsolutePath());
		// String xmiDocumentFileName = outputFile;// files[0].substring(0,
		String specData = sb.toString();
		OvertureParser op = new OvertureParser(specData);
		op.parseDocument();
		if (op.errors > 0)
		{
			for (ParserError error : op.parseErrors)
			{
				for (int j = 0; j < fileLineOffset.size(); j++)
				{
					Integer line = fileLineOffset.get(j);
					if (error.line <= line)
					{
						if (j != 0)
							error.line -= fileLineOffset.get(j - 1);
						error.file = selectedFiles.get(j);
						break;
					}
				}
			}

			FileWriter outputFileReader = new FileWriter(outputFile);

			BufferedWriter outputStream = new BufferedWriter(outputFileReader);

			outputStream.write(specData);
			outputStream.close();
			throw new ParseException("Parse errors encountered during parse of vdm file: ",
					op.parseErrors);
			// + outputFile,
			// 0);// \n"+ specData
		}

		Vdm2Uml w = new Vdm2Uml();
		Uml2XmiEAxml xmi = new Uml2XmiEAxml();
		xmi.Save(outputFile.getAbsolutePath(),
				w.Init(op.astDocument.getSpecifications()),
				w.GetLog());

		// return xmiDocumentFileName;

	}

	public static void toVpp(File outputDirectory, List<File> files)
			throws Exception
	{

		for (File file : files)
		{
			// Translator.TransLateUmlToVdm(file, outputDirectory);
			XmlDocument doc = XmlParser.Parse(file.getAbsolutePath(), false);

			StatusLog log = new StatusLog();

			Xml2UmlModel xmlUmlModel = new Xml2UmlModel(log);
			xmlUmlModel.VisitXmlDocument(doc);

			Uml2Vdm u = new Uml2Vdm();
			Oml2Vpp vpp = new Oml2Vpp();

			if (!outputDirectory.isDirectory())
				throw new Exception("Output directory not valid: "
						+ outputDirectory.getAbsolutePath());

			String outputPath = outputDirectory.getAbsolutePath();
			if (!outputPath.endsWith(new Character(File.separatorChar).toString()))
				outputPath += File.separatorChar;

			vpp.Save(outputPath, u.Init(xmlUmlModel.result), log);
		}

		System.out.println("Done: " + outputDirectory);

	}

	public static void printXmlDoc(File inputFile, File outputFile)
			throws Exception
	{

		XmlParser.Parse(inputFile.getAbsolutePath(), true);

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

	public static void test()
	{
		List<String> testsResults = new Vector<String>();
		List<String> testsResultsFail = new Vector<String>();
		List<String> testsResultsFailParse = new Vector<String>();
		for (File file : new File("C:\\overture\\overturesvn\\documentation\\examples\\VDM++\\").listFiles())
		{
			if (file.getName().startsWith("."))
				continue;
			System.out.println("---------------------- TEST: " + file.getName()
					+ "--------------------------------------");
			try
			{
				if (execute(file.getName(), file.getAbsolutePath()))
					testsResults.add(file.getName());
				else
					testsResultsFail.add(file.getName());
			} catch (ParseException e)
			{
				testsResultsFailParse.add(file.getName());
			} catch (Exception e)
			{
			}
		}
		System.out.println("--------------------------- RESULT ----------------------------------");
		printList(testsResults, "OK");
		printList(testsResultsFail, "FAIL");
		printList(testsResultsFailParse, "FAIL - Parse");
		System.out.println("Completed: "
				+ (testsResults.size() + testsResultsFail.size() + testsResultsFailParse.size())
				+ " Ok: " + testsResults.size() + " Fail: "
				+ testsResultsFail.size() + " Fail parse: "
				+ testsResultsFailParse.size());
		// Assert.assertEquals(0, testsResultsFail.size()
		// + testsResultsFailParse.size());
	}

	private static void printList(List<String> list, String status)
	{
		for (String string : list)
		{
			System.out.println(fixLength(string) + ": " + status);
		}
	}

	private static String fixLength(String data)
	{
		while (data.length() < 20)
			data += " ";
		return data;
	}

	public static boolean execute(String name, String argument)
			throws ParseException
	{
		ParseException exception = null;
		try
		{
			MainClass.main(new String[] { "-uml", "-output",
					"c:\\tmp\\" + name + ".xml", argument });
		} catch (ParseException e)
		{
			exception = e;
		} catch (Exception e)
		{
			// System.out.print(" - FAIL");
			// Assert.fail(e.getMessage());
			return false;
		}
		if (exception != null)
			throw exception;
		return true;
	}

}
