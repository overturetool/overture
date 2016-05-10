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
import java.util.List;
import java.util.Vector;

import org.overture.tools.packworkspace.rss.RssItem;
import org.overture.tools.packworkspace.testing.LatexBuilder;
import org.overture.tools.packworkspace.testing.ProjectTester;
import org.overturetool.vdmj.lex.Dialect;

public class Main
{

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception
	{
		Boolean override = false;
		Dialect dialect = Dialect.VDM_PP;
		if (args.length < 1 || !new File(args[1]).exists())
		{
			System.out.println("Please supply a vaild path to the examples root.");
			System.out.println("	e.g. C:\\overture\\overturesvn\\documentation\\examples");
			return;
		}
		File inputRootFolder = new File(args[1]);

		if (args.length > 3 && args[3].equals("-f"))
		{
			ProjectTester.FORCE_RERUN = true;
		}

		if (args.length > 0)
		{
			switch (args[0].toLowerCase().toCharArray()[1])
			{
				case 's':
					dialect = Dialect.VDM_SL;
					break;
				case 'p':
					dialect = Dialect.VDM_PP;
					break;
				case 'r':
					dialect = Dialect.VDM_RT;
					break;
				case 'c':
					runCompleteTest(new File(args[1]));
					return;

			}
		}
		if (args.length > 2 && args[2].equals("-override"))
			override = true;

		File tmpFolder = new File("examples");
		if (tmpFolder.exists())
		{
			System.out.println("The folder \""
					+ tmpFolder.getName()
					+ "\" already exists, it is used as the temp folder for packing and added to be deleted. Delete ok. ( y / n):");
			if (override || System.in.read() == (int) 'y')
				Controller.delete(tmpFolder);
			else
				return;
		}
		List<Controller> controllers = new ArrayList<Controller>();

		Controller controller = runController(dialect, inputRootFolder, tmpFolder);
		controllers.add(controller);

		List<RssItem> items = new ArrayList<RssItem>();
		for (Controller c : controllers)
		{
			items.addAll(c.getRssItems());
		}
		Controller.createRss(new File("rss.xml"), items);

		Controller.createOverviewPage(controllers);

		System.out.println("Wating for latex");
		Thread.sleep(5000);
		LatexBuilder.destroy();

		System.out.println("Done.");
		System.exit(0);
	}

	private static Controller runController(Dialect dialect,
			File inputRootFolder, File tmpFolder) throws IOException
	{
		Controller controller = new Controller(dialect, inputRootFolder);

		Controller.webDir.mkdirs();

		File zipFile = new File(Controller.webDir, "Examples"
				+ dialect.toString().toUpperCase() + ".zip");
		controller.packExamples(tmpFolder, zipFile);
		
		zipFiles.add(zipFile);

		controller.testProjects();

		return controller;
	}

static	List<File> zipFiles = new Vector<File>();

	public static void runCompleteTest(File root) throws Exception
	{
		if (!root.getName().toLowerCase().equals("examples"))
			throw new Exception("Illegal root");

		File tmpFolder = new File("tmp");

		List<Controller> controllers = new ArrayList<Controller>();

		Controller controller = runController(Dialect.VDM_SL, new File(root, "VDMSL"), tmpFolder);
		controllers.add(controller);
		Controller.delete(tmpFolder);

		controller = runController(Dialect.VDM_PP, new File(root, "VDM++"), tmpFolder);
		controllers.add(controller);
		Controller.delete(tmpFolder);

		controller = runController(Dialect.VDM_RT, new File(root, "VDMRT"), tmpFolder);
		controllers.add(controller);

		List<RssItem> items = new ArrayList<RssItem>();
		for (Controller c : controllers)
		{
			c.createWebSite();
			items.addAll(c.getRssItems());
		}
		
		Controller.createWebOverviewPage(controllers,zipFiles);
		
		Controller.createRss(new File("rss.xml"), items);

		Controller.createOverviewPage(controllers);

		Controller.delete(tmpFolder);

		System.out.println("\nWating for latex");
		Thread.sleep(5000);
		LatexBuilder.destroy();

		System.out.println("Done.");

		System.exit(0);

	}

}
