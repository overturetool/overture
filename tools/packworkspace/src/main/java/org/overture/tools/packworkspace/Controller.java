/*******************************************************************************
 * Copyright (c) 2009, 2011 Overture Team and others.
 *
 * Overture is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Overture is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Overture.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * The Overture Tool web-site: http://overturetool.org/
 *******************************************************************************/
package org.overture.tools.packworkspace;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.Vector;

import org.overture.tools.packworkspace.latex.FileUtils;
import org.overture.tools.packworkspace.rss.RssChannel;
import org.overture.tools.packworkspace.rss.RssFeed;
import org.overture.tools.packworkspace.rss.RssItem;
import org.overture.tools.packworkspace.testing.HtmlPage;
import org.overture.tools.packworkspace.testing.HtmlTable;
import org.overture.tools.packworkspace.testing.ProjectTester;
import org.overturetool.vdmj.lex.Dialect;

public class Controller
{
	List<ProjectPacker> projects = new Vector<ProjectPacker>();
	List<ProjectTester> testProjects = new Vector<ProjectTester>();
	Dialect dialect;
	File inputRootFolder;
	static final File reportDir = new File("Reports");
	public static final File webDir = new File("Web");

	public Controller(Dialect dialect, File inputRootFolder)
	{
		this.dialect = dialect;
		this.inputRootFolder = inputRootFolder;

		reportDir.mkdirs();
		webDir.mkdirs();
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
		text = "| " + text;
		while (text.length() < 79)
			text += " ";
		text += "|";

		System.out.println(text);
		System.out.println("|                                                                              |");
	}

	public static void printSubHeading(String text)
	{
		System.out.println("--------------------------------------------------------------------------------");
		text = "| " + text;
		while (text.length() < 79)
			text += " ";
		text += "|";

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
			p.setProjectTester(pTest);

		}

		String page = HtmlPage.makePage(HtmlPage.makeH1(dialect + ": "
				+ inputRootFolder.getName())
				+ HtmlTable.makeTable(sb.toString()));
		FileUtils.writeFile(page, new File(logOutput, "index.html"));
		FileUtils.writeFile(HtmlPage.makeStyleCss(), new File(logOutput, "style.css"));
	}

	public void packExamples(File outputFolder, File zipName)
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
//		String zipName = outputName + ".zip";
		if (zipName.exists())
			zipName.delete();

		FolderZiper.zipFolder(outputFolder.getName(), zipName.getAbsolutePath());
		// GZIPfile.getInterface().zip(outputFolder, new
		// File(outputName+".zip"));
		printSubHeading("Folder zipped: ".toUpperCase() + zipName.getName());
		// while (outputFolder.exists())
		// delete(outputFolder);

	}

	public static void delete(File tmpFolder)
	{
		// System.out.println("Deleting: " + tmpFolder);

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
		if (tmpFolder.exists())
			System.err.println("\nFaild to deleting - file not closed: "
					+ tmpFolder);
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

		return makeCell(synErrors + typeErrors + interpretationErrors, HtmlPage.makeLink(getName(), getName()
				+ "/index.html"))
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
		FileUtils.writeFile(page, new File(reportDir, "index.html"));
		FileUtils.writeFile(HtmlPage.makeStyleCss(), new File(reportDir, "style.css"));
	}

	public static void createRss(File outputFile, List<RssItem> items)
	{
		RssChannel channel = new RssChannel();
		channel.title = "VDM Examples";
		channel.description = "Overture VDM Examples";
		channel.link = "http://overturetool.org";

		channel.items.addAll(items);

		RssFeed feed = new RssFeed();
		feed.channel = channel;

		FileUtils.writeFile(feed.getXml().toString(), outputFile);

	}

	public List<RssItem> getRssItems()
	{
		List<RssItem> items = new ArrayList<RssItem>();

		for (ProjectPacker p : projects)
		{
			VdmReadme r = p.settings;
			RssItem item = new RssItem();

			item.title = r.getName();
			item.author = r.getTexAuthor();
			item.category = r.getDialect().toString();
			item.comments = r.getLanguageVersion().toString();
			item.description = r.getContent().trim();
			item.guid = UUID.randomUUID().toString();
			item.link = "http://overture.sourceforge.net/examples/"
					+ r.getDialect().toString() + "/" + r.getName() + ".zip";

			items.add(item);
		}

		return items;
	}

	public void createWebSite()
	{
		printSubHeading("Producing website".toUpperCase());

		File logOutput = new File(webDir, inputRootFolder.getName());
		logOutput.mkdirs();
		
		String outputFolderName = dialect.toString().replaceAll("_", "");
		File logOuputFiles = new File(logOutput,outputFolderName);
		logOuputFiles.mkdirs();

		StringBuilder sb = new StringBuilder();
		// sb.append(HtmlTable.makeRow(HtmlTable.makeCellHeaderss(new String[] {
		// "Project Name", "Syntax check", "Type check", "PO",
		// "Interpretation test", "Doc" })));
		Collections.sort(projects);
		for (ProjectPacker p : projects)
		{
			String name = p.getSettings().getName().substring(0,p.getSettings().getName().length()-2);
			name =name.substring(0,1).toUpperCase()+ name.substring(1); 
			System.out.println("Creating web entry for: "+name);
			sb.append(HtmlPage.makeH(3, name));

			System.out.print(" table...");
			String rows = tableRow("Project Name:", name);
			rows += tableRow("Author:", p.getSettings().getTexAuthor());
			//rows += tableRow("Dialect:", p.getSettings().getDialect().toString());
			rows += tableRow("Language Version:", p.getSettings().getLanguageVersion().toString());
			rows += tableRow("Description:", p.getSettings().getContent());
			
			
			
			String pdfLink = "";
			
			File pdfFile =p.getProjectTester().getPdf();
			if(pdfFile!=null && pdfFile.exists())
			{
				System.out.print(" pdf...");
				File newPdf =new File(logOuputFiles, name+".pdf");
				ProjectPacker.copyfile(pdfFile.getAbsolutePath(), newPdf.getAbsolutePath());
				pdfLink=HtmlPage.makeLink("pdf",outputFolderName+"/"+ newPdf.getName());
			}
			
			System.out.print(" zip...");
			File zipFile = new File(logOuputFiles,name+".zip");
			p.zipTo(zipFile);
			
			rows += tableRow("Download:", HtmlPage.makeLink("model", outputFolderName+"/"+zipFile.getName())+ " "+pdfLink);

			sb.append(HtmlTable.makeTable(rows));
			System.out.print("\n");

		}

		String page = HtmlPage.makePage(HtmlPage.makeH1( inputRootFolder.getName()+": Examples")
				+ sb.toString());
		FileUtils.writeFile(page, new File(logOutput, "index.html"));
		FileUtils.writeFile(HtmlPage.makeStyleCss(), new File(logOutput, "style.css"));
		
		//overturetool
		String pageSection =HtmlPage.makeOvertureStyleCss()+"\n"+ HtmlPage.makeDiv( sb.toString().replaceAll("href=\"", "href=\""+HtmlPage.overtureExamplesPreLink),"examples");
		FileUtils.writeFile(pageSection, new File(logOutput, "indexOv.html"));
		

	}

	private String tableRow(String... cells)
	{
		List c = Arrays.asList(cells);
		return HtmlTable.makeRow(HtmlTable.makeCell(cells[0], "first")+HtmlTable.makeCells(c.subList(1, c.size())));
	}

	public static void createWebOverviewPage(List<Controller> controllers,
			List<File> zipFiles)
	{
		printSubHeading("Producing main website".toUpperCase());

		StringBuilder sb = new StringBuilder();
		
		sb.append(HtmlPage.makeH1("Overture Examples"));
		
		for (Controller controller : controllers)
		{
			sb.append(HtmlPage.makeLink(HtmlPage.makeH(2, controller.getName()), controller.getName()));
		}
		
		sb.append(HtmlPage.makeBr());
		sb.append(HtmlPage.makeBr());
		
		sb.append(HtmlPage.makeH(2, "Download example collections"));
		for (File file : zipFiles)
		{
			sb.append(HtmlPage.makeLink(file.getName(), file.getName()));
			sb.append(HtmlPage.makeBr());
		}
		
		
		String page = HtmlPage.makePage(sb.toString());
		FileUtils.writeFile(page, new File(webDir, "index.html"));
		FileUtils.writeFile(HtmlPage.makeStyleCss(), new File(webDir, "style.css"));
		
	}

}
