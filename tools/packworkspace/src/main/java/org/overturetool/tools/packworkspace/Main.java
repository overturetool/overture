package org.overturetool.tools.packworkspace;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import org.overturetool.tools.packworkspace.testing.LatexBuilder;
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
		if (args.length > 1 && args[2].equals("-override"))
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
		List<Controller> controllers = new Vector<Controller>();

		Controller controller = runController(dialect,
				inputRootFolder,
				tmpFolder);
		controllers.add(controller);
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
	

		controller.packExamples(tmpFolder, "Examples"
				+ dialect.toString().toUpperCase());

		controller.testProjects();
		return controller;
	}
	
	public static void runCompleteTest(File root) throws Exception
	{
		if(!root.getName().toLowerCase().equals("examples"))
			throw new Exception("Illegal root");
		
		File tmpFolder = new File("tmp");
		
		
		

		
		List<Controller> controllers = new Vector<Controller>();

		Controller controller = runController(Dialect.VDM_SL,
				new File(root,"VDMSL"),
				tmpFolder);
		controllers.add(controller);
		Controller.delete(tmpFolder);
		
		
		controller = runController(Dialect.VDM_PP,
				new File(root,"VDM++"),
				tmpFolder);
		controllers.add(controller);
		Controller.delete(tmpFolder);
		
		
		
		controller = runController(Dialect.VDM_RT,
				new File(root,"VDMRT"),
				tmpFolder);
		controllers.add(controller);
		
		
		
		Controller.createOverviewPage(controllers);

		Controller.delete(tmpFolder);
		
		System.out.println("\nWating for latex");
		Thread.sleep(5000);
		LatexBuilder.destroy();

		System.out.println("Done.");
		
		System.exit(0);
		
	}

}
