package org.overturetool.tools.astgen;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Vector;


import org.apache.maven.plugin.logging.Log;
import org.overturetool.tools.astgen.vdm.*;

public class GenerateSource
{
	private static final String AST = ".ast";
	private static final String NEW_LINE = "\n";
	private static final String TMP_FOLDER = "/.tmp";
	private static final String TEX = ".tex";
	private static final String VPP = ".vpp";
	Log log;
	File projectFile;

	public GenerateSource(Log log, File parentFile)
	{
		this.log = log;
		this.projectFile = parentFile;
	}

	@SuppressWarnings("unchecked")
	public void generate(String prefix, String packageName, List<String> top)
	{
		File file = exstractAstFile();
		if (file == null || !file.exists())
		{
			log.error("please supply a file name!");
			return;
		}
		try
		{
			AstParser ast = new AstParser(file.getAbsolutePath());

			log.info("Start parse");
			ast.yyparse();
			log.info("Parsing finished");

			if (prefix != null)
				ast.theAst.setPrefix(prefix);
			if (packageName != null && packageName.length() > 0)
				ast.theAst.setPackage(getPackageFromString(packageName));
			if (top != null && top.size() > 0)
				ast.theAst.setTop(new Vector(top));

			File outputFolder = new File(projectFile, SRC_MAIN_VPP
					+ getPackageFolderPath(packageName)
					+ TMP_FOLDER.replace('/', File.separatorChar));
			outputFolder.mkdirs();
			ast.theAst.setDirectory(outputFolder.getAbsolutePath());

			log.info("AstGen:prefix: " + ast.theAst.getPrefix());
			log.info("AstGen:package: " + ast.theAst.getPackage());
			log.info("AstGen:Directory: " + ast.theAst.getDirectory());
			log.info("AstGen:Top: " + ast.theAst.getTop());

			if (ast.errors == 0)
			{
				wfCheckVisitor wVisit = new wfCheckVisitor();
				ast.theAst.accept(wVisit);
				if (wfCheckVisitor.errors.intValue() == 0)
				{
					codegenVisitor cgVisit = new codegenVisitor();
					cgVisit.setLong();
					ast.theAst.accept(cgVisit);
					copyVppFiles(outputFolder, prefix);
				}
			}

			deleteTmpData(outputFolder);

			log.info("Done");
		} catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	private void deleteTmpData(File outputFolder)
	{
		for (File f : outputFolder.listFiles())
		{
			if (f.isFile())
				f.delete();
		}

		while (outputFolder.listFiles().length > 0)
			for (File f : outputFolder.listFiles())
			{
				deleteTmpData(f);
			}

		outputFolder.delete();

	}

	private void copyVppFiles(File outputFolder, String prefix)
	{
		List<File> interfaceFiles = new Vector<File>();
		List<File> implementation = new Vector<File>();

		File sourceFolder = new File(outputFolder, "src");

		for (File file : sourceFolder.listFiles())
		{
			if (file.getAbsolutePath().endsWith(TEX)
					|| file.getAbsolutePath().endsWith(VPP))
			{
				if (file.getName().startsWith(prefix))
					implementation.add(file);
				else if (file.getName().startsWith("I" + prefix))
					interfaceFiles.add(file);
			}
		}

		mergeFiles(outputFolder.getParentFile(), interfaceFiles, prefix
				+ "Interfaces.vpp");
		mergeFiles(outputFolder.getParentFile(), implementation, prefix
				+ "Implementation.vpp");

	}

	private void mergeFiles(File outputDir, List<File> files, String fileName)
	{
		FileWriter outputFileWriter;
		BufferedWriter outputStream = null;
		try
		{

			outputFileWriter = new FileWriter(new File(outputDir, fileName));

			outputStream = new BufferedWriter(outputFileWriter);

			for (File file : files)
			{
				FileReader inputFileReader = new FileReader(file);

				// Create Buffered/PrintWriter Objects
				BufferedReader inputStream = new BufferedReader(inputFileReader);
				String inLine = null;
				boolean enableSkip = false;
				boolean continuouslySkip = false;
				while ((inLine = inputStream.readLine()) != null)
				{
					if (inLine.trim().equals("class IUmlDocument")
							|| inLine.trim().startsWith("class UmlDocument"))
						enableSkip = true;
					else if (inLine.trim().equals("end IUmlDocument")
							|| inLine.trim().equals("end UmlDocument"))
						enableSkip = false;

					if (enableSkip)
					{
						String fSl = "public toVdmSlValue: () ==> seq of char";
						String fSl1 = "toVdmSlValue () == is subclass responsibility;";
						String fVpp = "public toVdmPpValue: () ==> seq of char";
						String fVpp1 = "toVdmPpValue () == is subclass responsibility;";

						List<String> filter = new Vector<String>();
						filter.add(fSl);
						filter.add(fSl1);
						filter.add(fVpp);
						filter.add(fVpp1);
						String l = inLine.trim();

						if (filter.contains(l))
							continue;

						if (l.equals("toVdmSlValue () ==")
								|| l.equals("toVdmPpValue () =="))
							continuouslySkip = true;
						 if (l.equals("return visitor.result );"))
						 {
							continuouslySkip = false;
							continue;
						 }

						if (continuouslySkip)
							continue;
					}
					
					if(inLine.trim().startsWith("static public IQ") && (file.getName().endsWith(VPP)|| file.getName().endsWith(TEX)))
					{
						outputStream.write(NEW_LINE +"values -- Temp fix static access to instance variables are not allowed"+ NEW_LINE+inLine.replace(":=", "=")+"\ninstance variables\n");
						continue;
					}

					outputStream.write(NEW_LINE + inLine);
				}
				inputStream.close();

				outputStream.write(NEW_LINE);
			}

		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally
		{
			try
			{
				if (outputStream != null)
					outputStream.close();
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}

	}

	private Vector<String> getPackageFromString(String data)
	{
		Vector<String> v = new Vector<String>();
		if (data.contains("."))
		{
			data.replace('.', ':');
			for (String b : data.split(":"))
			{
				v.add(b);
			}
		} else
			v.add(data);
		return v;
	}

	private String getPackageFolderPath(String packageName)
	{
		return "/" + packageName.replace('.', '/');
	}

	private static final String SRC_MAIN_RESOURCES = "/src/main/resources";
	private static final String SRC_MAIN_VPP = "/src/main/vpp";

	private File exstractAstFile()
	{
		File resourcesFolder = getAstLocation(projectFile);
		if (resourcesFolder != null && resourcesFolder.exists())
		{
			for (File f : resourcesFolder.listFiles())
			{
				if (f.getAbsolutePath().toLowerCase().endsWith(AST))
				{
					return f;
				}
			}
		}
		return null;
	}

	/*
	 * Get location of vpp files for the a project from the main folder
	 */
	private File getAstLocation(File mainSource)
	{
		return new File(mainSource.getAbsolutePath()
				+ SRC_MAIN_RESOURCES.replace('/', File.separatorChar));
	}
}
