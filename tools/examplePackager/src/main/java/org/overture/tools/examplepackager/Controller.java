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
package org.overture.tools.examplepackager;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import org.overture.ast.lex.Dialect;
import org.overture.tools.examplepackager.html.HtmlPage;
import org.overture.tools.examplepackager.html.HtmlTable;
import org.overture.tools.examplepackager.util.FileUtils;
import org.overture.tools.examplepackager.util.FolderZipper;

public class Controller
{
	List<ProjectPacker> projects = new Vector<ProjectPacker>();
	Dialect dialect;
	File inputRootFolder;
	boolean verbose = true;
	public final File webDir;// = new File("Web");

	public Controller(Dialect dialect, File inputRootFolder, File output)
	{
		this.dialect = dialect;
		this.inputRootFolder = inputRootFolder;
		this.webDir = new File(output, "Web");
	}
	
	public Controller(Dialect dialect, File inputRootFolder, File output, boolean verbose)
	{	
		this(dialect, inputRootFolder, output);
		this.verbose = verbose; 
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

	public void packExamples(File outputFolder, File zipName, boolean dryrun)
	{
		if (!dryrun)
		{
			outputFolder.mkdirs();
		}

		if (verbose)
			printSubHeading("PACKING: " + inputRootFolder.getName());
		for (File exampleFolder : inputRootFolder.listFiles())
		{
			if (!exampleFolder.isDirectory()
					|| exampleFolder.getName().equals(".svn"))
				continue;

			ProjectPacker p = new ProjectPacker(exampleFolder, dialect, verbose);
			if (!dryrun)
			{
				p.packTo(outputFolder);
			}
			projects.add(p);
		}
		if (!dryrun)
		{
			if (zipName.exists())
				zipName.delete();

			FolderZipper.zipFolder(outputFolder.getAbsolutePath(), zipName.getAbsolutePath());
			if (verbose)
				printSubHeading("Folder zipped: ".toUpperCase() + zipName.getName());
		}

	}

	public static void delete(File tmpFolder)
	{
		try
		{
			if (tmpFolder != null && tmpFolder.isFile() && tmpFolder.exists())
				tmpFolder.delete();
			else if (tmpFolder.exists())
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

	public void createWebSite()
	{
		webDir.mkdirs();
		printSubHeading("Producing website".toUpperCase());

		File logOutput = new File(webDir, inputRootFolder.getName());
		logOutput.mkdirs();

		String outputFolderName = dialect.toString().replaceAll("_", "");
		File logOuputFiles = new File(logOutput, outputFolderName);
		logOuputFiles.mkdirs();

		StringBuilder sb = new StringBuilder();
		Collections.sort(projects);
		for (ProjectPacker p : projects)
		{
			String name = p.getSettings().getName().substring(0, p.getSettings().getName().length() - 2);
			name = name.substring(0, 1).toUpperCase() + name.substring(1);
			System.out.println("Creating web entry for: " + name);
			sb.append(HtmlPage.makeH(3, name));

			System.out.print(" table...");
			String rows = tableRow("Project Name:", name);
			rows += tableRow("Author:", p.getSettings().getTexAuthor());
			// rows += tableRow("Dialect:", p.getSettings().getDialect().toString());
			rows += tableRow("Language Version:", p.getSettings().getLanguageVersion().toString());
			rows += tableRow("Description:", p.getSettings().getContent());

			String pdfLink = "";

			System.out.print(" zip...");
			File zipFile = new File(logOuputFiles, name + ".zip");
			p.zipTo(zipFile);

			rows += tableRow("Download:", HtmlPage.makeLink("model", outputFolderName
					+ "/" + zipFile.getName())
					+ " " + pdfLink);

			sb.append(HtmlTable.makeTable(rows));
			System.out.print("\n");

		}

		String page = HtmlPage.makePage(HtmlPage.makeH1(inputRootFolder.getName()
				+ ": Examples")
				+ sb.toString());
		FileUtils.writeFile(page, new File(logOutput, "index.html"));
		FileUtils.writeFile(HtmlPage.makeStyleCss(), new File(logOutput, "style.css"));

		// overturetool
		String pageSection = HtmlPage.makeOvertureStyleCss()
				+ "\n"
				+ HtmlPage.makeDiv(sb.toString().replaceAll("href=\"", "href=\""
						+ HtmlPage.overtureExamplesPreLink), "examples");
		FileUtils.writeFile(pageSection, new File(logOutput, "indexOv.html"));

	}

	private String tableRow(String... cells)
	{
		List<String> c = Arrays.asList(cells);
		return HtmlTable.makeRow(HtmlTable.makeCell(cells[0], "first")
				+ HtmlTable.makeCells(c.subList(1, c.size())));
	}

	public void createWebOverviewPage(List<Controller> controllers,
			List<File> zipFiles)
	{
		webDir.mkdirs();
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
