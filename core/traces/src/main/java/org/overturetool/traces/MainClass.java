package org.overturetool.traces;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.overturetool.traces.utility.CmdTrace;
import org.overturetool.traces.utility.TraceXmlWrapper;
import org.overturetool.traces.vdmj.TraceInterpreter;
import org.overturetool.vdmj.runtime.ContextException;
import org.overturetool.vdmj.types.ParameterType;

public class MainClass
{
	private static String[] paramterTypes = new String[] { "-outputPath", "-c",
			"-max", "-toolbox", "-VDMToolsPath", "-help", "-vdmjOnly",
			"-projectRoot","-wait" };

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception
	{
		// try {

		// System.out.println("OS detected: " +
		// System.getProperty("os.name"));
		// String tmp = "";
		// for (int i = 0; i < args.length; i++) {
		// tmp += " " + args[i];
		// }
		Object[] tmp = ExstractParameters(args);
		Hashtable<String, String> par = (Hashtable<String, String>) tmp[0];
		String[] files = (String[]) tmp[1];
		
		if(par.containsKey("-wait"))
			Thread.sleep(Integer.parseInt(par.get("-wait")));

		if (args.length == 0 || args[0].replace("--", "-").startsWith("-help")
				|| args[0].startsWith("?") || args[0].startsWith("-?")
				|| args[0].startsWith("/?"))
		{
			PrintHelp();
			return;
			// } else if (args[0].equals("-GUI")) {
			// RunGUI();
			// return;
		} else if (par.containsKey("-outputPath")
				&& par.containsKey("-vdmjOnly") && par.containsKey("-c")
				&& par.containsKey("-max"))
		{
			String projectRoot = null;
			if (par.containsKey("-projectRoot"))
				projectRoot = par.get("-projectRoot");

			RunVdmjOnly(
					par.get("-outputPath"),
					par.get("-c"),
					par.get("-max"),
					files,
					projectRoot);
			return;

		} else if (par.containsKey("-outputPath") && par.containsKey("-c")
				&& par.containsKey("-max") && par.containsKey("-toolbox"))
		{
			RunCmd(
					par.get("-outputPath"),
					par.get("-c"),
					par.get("-max"),
					files,
					par.get("-toolbox"),
					par.containsKey("-VDMToolsPath") ? par.get("-VDMToolsPath")
							: "");
			return;
		}

		// } catch (Exception e) {
		// PrintHelp();
		// }

	}

	private static void RunVdmjOnly(String outputPath, String classes,
			String maxString, String[] files, String projectRoot)
	{
		// TODO Auto-generated method stub
		Integer max = Integer.parseInt(maxString);

		List<String> cls = new ArrayList<String>();
		for (String c : classes.split(","))
		{
			cls.add(c);
		}

		TraceInterpreter ti = new TraceInterpreter();

		List<File> specFiles = new Vector<File>();

		for (String file : files)
		{
			File f = new File(file);
			if (f.exists())
				specFiles.add(f);
		}

		if (projectRoot != null)
		{
			File projectRootFile = new File(projectRoot);
			if (projectRootFile.exists())
				for (File file : GetFiles(projectRootFile))
				{
					if (file.getName().endsWith(".vpp"))
						specFiles.add(file);
				}
		}

		try
		{

			TraceXmlWrapper txw = new TraceXmlWrapper(outputPath
					+ File.separatorChar + cls.get(0) + ".xml");

			ti.processTraces(specFiles, cls.get(0), txw);
			txw.Stop();
		}catch(ContextException e)
		{
		System.out.println(e.toString());
		}catch(ClassNotFoundException e)
		{
			
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static ArrayList<File> GetFiles(File file)
	{
		ArrayList<File> files = new ArrayList<File>();
		// if(file.getName().contains(".svn"))
		// return files;
		if (file.isDirectory())
			for (File currentFile : file.listFiles())
			{
				if (currentFile.isDirectory())
				{
					for (File file2 : GetFiles(currentFile))
					{
						files.add(file2);
					}

					int ki = 0;
				} else if (currentFile.isFile())
					files.add(currentFile);
			}
		else
			files.add(file);
		return files;
	}

	private static void RunCmd(String outputPath, String classes, String max,
			String[] files, String toolbox, String VDMToolsPath)
			throws Exception
	{

		ArrayList<String> cls = new ArrayList<String>();
		for (String c : classes.split(","))
		{
			cls.add(c);
		}
		int maximum = Integer.parseInt(max);
		ArrayList<String> specFiles = new ArrayList<String>();
		for (String f : files)
		{
			specFiles.add(f);
		}
		CmdTrace.ToolBoxType tb = CmdTrace.ToolBoxType.VDMJ;
		if (toolbox.equals("VDMTools"))
			tb = CmdTrace.ToolBoxType.VDMTools;
		else if (toolbox.equals("VDMJ"))
			tb = CmdTrace.ToolBoxType.VDMJ;

		CmdTrace.RunCmd(outputPath, cls, maximum, specFiles, tb, VDMToolsPath);
	}

	// private static void RunGUI() throws Exception {
	// SwingUtilities.invokeLater(new Runnable() {
	// public void run() {
	// MainFrame thisClass = new MainFrame();
	// thisClass.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	// thisClass.setVisible(true);
	// }
	// });
	// }

	private static String[] SplitInputFiles(String files, String splitter)
	{
		if (files.contains(splitter))
			return files.split(splitter);
		else
			return new String[] { files };

	}

	private static void PrintHelp()
	{
		System.out.println("Overture VDM Traces test");
		System.out.println("Usage: org.overture.traces.jar [options] [specfile{,specfile}]");
		// System.out.println("If no options are entered the GUI will show.");
		System.out.println();
		System.out.println("OPTIONS for command line usage:");
		System.out.println(" -outputPath Path to a folder where results will be stored.");
		System.out.println(" -c          Class names to be concidered {,classname}.");
		System.out.println(" -max        Maximum used in expansion of statements.");
		System.out.println(" -toolbox    The type of toolbox which should be used.[VDMTools | VDMJ]");
		System.out.println("     VDMTools: Requires VDMTools to be installed and an additional option");
		System.out.println("               -VDMToolsPath to be set to the specific file.");
		System.out.println("     VDMJ:     Requires VDMJ to be in the class path.");
		System.out.println();
		System.out.println("Example of usege:");
		System.out.println("org.overture.traces.jar Will result in a GUI being shown.");
		System.out.println();
		System.out.println("org.overture.traces.jar -outputPath c:\\ -c A,B -max 3 -toolbox VDMJ a.vpp,b.vpp");
		System.out.println("  Will result in the classes A and B being tested with VDMJ");
		System.out.println();
		System.out.println("org.overture.traces.jar -outputPath c:\\ -c A,B -max 3 -toolbox VDMTools -VDMToolsPath vppgde.exe a.vpp,b.vpp");
		System.out.println("  Will result in the classes A and B being tested with VDM Tools");

	}

	private static Object[] ExstractParameters(String[] parameters)
	{

		Hashtable<String, String> pars = new Hashtable<String, String>();
		String[] specFiles = null;
		try
		{

			int lastFoundParameter = 0;
			for (int i = 0; i < parameters.length; i++)
			{
				for (String parType : paramterTypes)
				{
					if (parameters[i].equals(parType)
							&& parameters.length > i + 1)
					{
						pars.put(parType, parameters[i + 1]);
						lastFoundParameter = i;
					}
				}
			}

			ArrayList<String> specF = new ArrayList<String>();
			if (lastFoundParameter + 2 < parameters.length)
				for (int i = lastFoundParameter + 2; i < parameters.length; i++)
				{
					specF.add(parameters[i]);
				}

			specFiles = new String[specF.size()];
			specF.toArray(specFiles);

			// int highestIndex = 0;
			// String value = "";
			//
			// for (int i = 0; i < paramterTypes.length; i++) {
			// String parType = paramterTypes[i];
			//
			// int index = parameters.indexOf(parType);
			// if (index >= 0) {
			// index += parType.length();
			// String tmp = parameters.substring(index).trim();
			// int indexOfParSplit = tmp.indexOf('-');
			// value = tmp;
			// if (indexOfParSplit > 0) {
			// int nextSplit = tmp.indexOf('-');
			// value = tmp.substring(0, nextSplit);
			// } else {
			// int nextSplit = tmp.indexOf(' ');
			// value = tmp.substring(0, nextSplit);
			// }
			// pars.put(parType, value.trim());
			// }
			// if (index > highestIndex)
			// highestIndex = index + value.length() + 1;
			// }
			// if (highestIndex < parameters.length())
			// pars.put("files", parameters.substring(highestIndex).trim());
		} catch (Exception e)
		{
			// TODO: handle exception
		}
		return new Object[] { pars, specFiles };
	}
}
