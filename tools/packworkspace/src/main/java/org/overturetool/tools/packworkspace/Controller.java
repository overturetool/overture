package org.overturetool.tools.packworkspace;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import org.overturetool.tools.packworkspace.latex.FileUtils;
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
		printHeading(dialect.toString());
	}

	public String getName()
	{
		return inputRootFolder.getName();
	}
	
	public static void printHeading(String text)
	{
		System.out.println("\n================================================================================");
		System.out.println("|                                                                              |");
		text = "| "+text;
		while(text.length()<79)
			text+=" ";
		text+="|";
		
		System.out.println(text);
		System.out.println("|                                                                              |");
	}
	
	public static void printSubHeading(String text)
	{
		System.out.println("--------------------------------------------------------------------------------");
		text = "| "+text;
		while(text.length()<79)
			text+=" ";
		text+="|";
		
		System.out.println(text);
		System.out.println("|                                                                              |");
	}

	public void testProjects() throws IOException
	{
		printSubHeading("Testing".toUpperCase());

		File logOutput = new File(reportDir, inputRootFolder.getName());

		StringBuilder sb = new StringBuilder();
		sb.append(HtmlTable.makeRow(HtmlTable.makeCellHeaderss(new String[] {
				"Project Name", "Syntax check", "Type check", "PO",
				"Interpretation test", "Doc" })));
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
		FileUtils.writeFile(new File(logOutput, "index.html"), page);
		FileUtils.writeFile(new File(logOutput, "style.css"), HtmlPage.makeStyleCss());
	}

	public void packExamples(File outputFolder, String outputName)
	{

		// if (outputFolder.exists())
		// delete(outputFolder);
		outputFolder.mkdirs();

		printSubHeading("PACKING: " + inputRootFolder.getName());
		for (File exampleFolder : inputRootFolder.listFiles())
		{
			if (exampleFolder.getName().equals(".svn"))
				continue;

			ProjectPacker p = new ProjectPacker(exampleFolder, dialect);
			p.packTo(outputFolder);
			projects.add(p);
		}
		String zipName = outputName + ".zip";
		if (new File(zipName).exists())
			new File(zipName).delete();

		FolderZiper.zipFolder(outputFolder.getName(), zipName);
		// GZIPfile.getInterface().zip(outputFolder, new
		// File(outputName+".zip"));
		printSubHeading("Folder zipped: ".toUpperCase() + outputName);
		// while (outputFolder.exists())
		// delete(outputFolder);

	}

	public static void delete(File tmpFolder)
	{
		//System.out.println("Deleting: " + tmpFolder);

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
				System.err.println("\nFaild to deleting: " + tmpFolder);
			}
			if(tmpFolder.exists())
				System.err.println("\nFaild to deleting - file not closed: " + tmpFolder);
	}

	public Integer count = 0;
	public Integer synErrors = 0;
	public Integer typeErrors = 0;
	public Integer poCount = 0;
	public Integer interpretationErrors = 0;

	public String getOverview()
	{
		count = testProjects.size();
		synErrors = 0;
		typeErrors = 0;
		interpretationErrors = 0;
		for (ProjectTester t : testProjects)
		{
			if (!t.isSyntaxCorrect())
				synErrors++;
			if (!t.isTypeCorrect())
				typeErrors++;
			if (!t.isInterpretationSuccessfull())
				interpretationErrors++;
			poCount += t.getPoCount();
		}

		return makeCell(synErrors + typeErrors + interpretationErrors,
				HtmlPage.makeLink(getName(), getName() + "/index.html"))
				+

				HtmlTable.makeCell(count.toString())
				+ makeCell(synErrors)
				+ makeCell(typeErrors)
				+ HtmlTable.makeCell(poCount.toString())
				+ makeCell(interpretationErrors);
	}

	private static String makeCell(Integer status)
	{
		return makeCell(status, status.toString());

	}

	private static String makeCell(Integer status, String text)
	{
		if (status > 0)
			return HtmlTable.makeCell(text, HtmlTable.STYLE_CLASS_FAILD);
		else
			return HtmlTable.makeCell(text, HtmlTable.STYLE_CLASS_OK);

	}

	public static void createOverviewPage(List<Controller> controllers)
			throws IOException
	{
		Integer totalCount = 0;
		Integer totalSynErrors = 0;
		Integer totalTypeErrors = 0;
		Integer totalPos = 0;
		Integer totalInterpretationErrors = 0;
		StringBuilder sb = new StringBuilder();
		sb.append(HtmlTable.makeRow(HtmlTable.makeCellHeaderss(new String[] {
				"Test set", "Project count", "Syntax check", "Type check",
				"PO", "Interpretation test" })));
		for (Controller c : controllers)
		{
			sb.append(HtmlTable.makeRow(c.getOverview()));
			totalCount += c.count;
			totalSynErrors += c.synErrors;
			totalTypeErrors += c.typeErrors;
			totalPos += c.poCount;
			totalInterpretationErrors += c.interpretationErrors;
		}

		sb.append(HtmlTable.makeRowTotal(HtmlTable.makeCell("Totals"
				+ HtmlTable.makeCell(totalCount.toString())
				+ makeCell(totalSynErrors) + makeCell(totalTypeErrors)
				+ HtmlTable.makeCell(totalPos.toString())
				+ makeCell(totalInterpretationErrors))));

		String page = HtmlPage.makePage(HtmlPage.makeH1("Test Overview")
				+ HtmlTable.makeTable(sb.toString()));
		FileUtils.writeFile(new File(reportDir, "index.html"), page);
		FileUtils.writeFile(new File(reportDir, "style.css"), HtmlPage.makeStyleCss());
	}


}
