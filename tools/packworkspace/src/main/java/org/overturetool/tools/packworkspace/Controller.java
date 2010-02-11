package org.overturetool.tools.packworkspace;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import org.overturetool.tools.packworkspace.testing.HtmlPage;
import org.overturetool.tools.packworkspace.testing.HtmlTable;
import org.overturetool.tools.packworkspace.testing.ProjectTester;
import org.overturetool.vdmj.lex.Dialect;

public class Controller
{
	List<ProjectPacker> projects = new Vector<ProjectPacker>();
	List<ProjectTester> testProjects = new Vector<ProjectTester>();
	Dialect dialect;
	File inputRootFolder;
	static final File reportDir = new File("Reports");

	public Controller(Dialect dialect, File inputRootFolder) {
		this.dialect = dialect;
		this.inputRootFolder = inputRootFolder;

		reportDir.mkdirs();
	}

	public String getName()
	{
		return inputRootFolder.getName();
	}

	public void testProjects() throws IOException
	{
		System.out.println("Testing...");

		File logOutput = new File(reportDir, inputRootFolder.getName());

		StringBuilder sb = new StringBuilder();
		sb.append(HtmlTable.makeRow(HtmlTable.makeCellHeaderss(new String[] {
				"Project Name", "Syntax check", "Type check",
				"Interpretation test" })));
		Collections.sort(projects);
		for (ProjectPacker p : projects)
		{
			ProjectTester pTest = new ProjectTester(logOutput);
			sb.append(pTest.test(p));
			testProjects.add(pTest);

		}

		String page = HtmlPage.makePage(HtmlPage.makeH1(dialect + ": "
				+ inputRootFolder.getName())
				+ HtmlTable.makeTable(sb.toString()));
		writeFile(new File(logOutput, "index.html"), page);
writeFile(new File(logOutput,"style.css"), HtmlPage.makeStyleCss());
	}

	public void packExamples(File outputFolder, String outputName)
	{

		// if (outputFolder.exists())
		// delete(outputFolder);
		outputFolder.mkdirs();

		System.out.println("PACKING: " + inputRootFolder.getName());
		for (File exampleFolder : inputRootFolder.listFiles())
		{
			if (exampleFolder.getName().equals(".svn"))
				continue;

			ProjectPacker p = new ProjectPacker(exampleFolder, dialect);
			p.packTo(outputFolder);
			projects.add(p);
		}
		String zipName = outputName + ".zip";
		if(new File(zipName).exists())
			new File(zipName).delete();
		
		FolderZiper.zipFolder(outputFolder.getName(), zipName);
		// GZIPfile.getInterface().zip(outputFolder, new
		// File(outputName+".zip"));
		System.out.println("Folder zipped: " + outputName);
		// while (outputFolder.exists())
		// delete(outputFolder);

	}

	public static void delete(File tmpFolder)
	{
		System.out.println("Trying to delete: " + tmpFolder);
		try
		{
			if (tmpFolder != null && tmpFolder.isFile())
				tmpFolder.delete();
			else
			{
				for (File file : tmpFolder.listFiles())
				{
					delete(file);
				}
				tmpFolder.delete();
			}
		} catch (Exception e)
		{

		}
	}

	public String getOverview()
	{
		Integer count = testProjects.size();
		Integer synErrors = 0;
		Integer typeErrors = 0;
		Integer interpretationErrors = 0;
		for (ProjectTester t : testProjects)
		{
			if (!t.isSyntaxCorrect())
				synErrors++;
			if (!t.isTypeCorrect())
				typeErrors++;
			if (!t.isInterpretationSuccessfull())
				interpretationErrors++;
		}
		return makeCell(synErrors+typeErrors+interpretationErrors,HtmlPage.makeLink( getName(),getName()+"/index.html"))+
		
		HtmlTable.makeCell(count.toString())+makeCell(synErrors)+makeCell(typeErrors)+makeCell(interpretationErrors);
	}
	
	private static String makeCell(Integer status)
	{
		return makeCell(status,status.toString());
		
	}
	private static String makeCell(Integer status,String text)
	{
		if(status>0)
			return HtmlTable.makeCell(text, HtmlTable.STYLE_CLASS_FAILD);
		else
			return HtmlTable.makeCell(text, HtmlTable.STYLE_CLASS_OK);
		
	}

	public static void createOverviewPage(List<Controller> controllers) throws IOException
	{
		StringBuilder sb = new StringBuilder();
		sb.append(HtmlTable.makeRow(HtmlTable.makeCellHeaderss(new String[] {
				"Test set","Project count", "Syntax check", "Type check",
				"Interpretation test" })));
		for (Controller c : controllers)
		{
			sb.append(HtmlTable.makeRow( c.getOverview()));
		}

		String page = HtmlPage.makePage(HtmlPage.makeH1("Test Overview")
				+ HtmlTable.makeTable(sb.toString()));
		writeFile(new File(reportDir,
				"index.html"),page);
		writeFile(new File(reportDir,
		"style.css"),HtmlPage.makeStyleCss());
	}

	private static void writeFile(File file, String content) throws IOException
	{
		FileWriter outputFileWriter = new FileWriter(file);
		BufferedWriter outputStream = new BufferedWriter(outputFileWriter);
		outputStream.write(content);
		outputStream.close();
		outputFileWriter.close();
	}
}
