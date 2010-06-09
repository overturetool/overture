package org.overturetool.traces;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.overturetool.traces.utility.CmdTrace;
import org.overturetool.traces.utility.TraceXmlWrapper;
import org.overturetool.traces.vdmj.TraceInterpreter;
import org.overturetool.vdmj.Release;
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.runtime.ContextException;
import org.overturetool.vdmj.traces.TraceReductionType;

public class MainClass
{

	private static String[] paramterTypes = new String[] { "-outputPath", "-c",
			 "-toolbox", "-VDMToolsPath", "-help", "-vdmjOnly",
			"-projectRoot", "-wait", "-d", "-r", "-subset", "-seed",
			"-reduction", "-log", "-store" };

	private static boolean log = false;
	private static boolean storeOutput = true;

	/**
	 * @param args
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
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

		if (par.containsKey("-wait"))
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
				)
		{
			Dialect dialect = Dialect.VDM_PP;

			if (par.containsKey("-d"))
			{
				if (par.get("-d").toLowerCase().equals("vdmsl"))
					dialect = Dialect.VDM_SL;
				else if (par.get("-d").toLowerCase().equals("vdmpp"))
					dialect = Dialect.VDM_PP;
				else if (par.get("-d").toLowerCase().equals("vdmrt"))
					dialect = Dialect.VDM_RT;
			}

			Release release = Release.DEFAULT;
			if (par.containsKey("-r"))
				release = Release.lookup(par.get("-r"));
			String projectRoot = null;
			if (par.containsKey("-projectRoot"))
				projectRoot = par.get("-projectRoot");

			Float subset = null;
			if (par.containsKey("-subset"))
				subset = Float.parseFloat(par.get("-subset"));
			Long seed = null;
			if (par.containsKey("-seed"))
				seed = Long.parseLong(par.get("-seed"));
			TraceReductionType traceReductionType = null;
			if (par.containsKey("-reduction"))
				traceReductionType = TraceReductionType.valueOf(par.get("-reduction"));

			if (par.containsKey("-log"))
				log = Boolean.parseBoolean(par.get("-log"));
			if (par.containsKey("-store"))
				storeOutput = Boolean.parseBoolean(par.get("-store"));
			// if (par.containsKey("-logAppend"))
			// logAppend = Boolean.parseBoolean(par.get("-logAppend"));

			RunVdmjOnly(par.get("-outputPath"), par.get("-c"),files, projectRoot, dialect, release, subset, seed, traceReductionType);
			return;

		} else if (par.containsKey("-outputPath") && par.containsKey("-c")
				&& par.containsKey("-max") && par.containsKey("-toolbox"))
		{
			RunCmd(par.get("-outputPath"), par.get("-c"), par.get("-max"), files, par.get("-toolbox"), par.containsKey("-VDMToolsPath") ? par.get("-VDMToolsPath")
					: "");
			return;
		}

		// } catch (Exception e) {
		// PrintHelp();
		// }

	}

	private static void RunVdmjOnly(String outputPath, String classes,
			String[] files, String projectRoot,
			Dialect dialect, Release release, Float subset, Long seed,
			TraceReductionType traceReductionType)
	{
		List<String> cls = new ArrayList<String>();
		for (String c : classes.split(","))
		{
			cls.add(c);
		}

		TraceInterpreterCsv ti = null;

		if (subset == null || seed == null || traceReductionType == null)
		{
			ti = new TraceInterpreterCsv();
		} else
		{
			ti = new TraceInterpreterCsv(subset, traceReductionType, seed);
		}
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
			{
				ti.projectName = projectRootFile.getName();
				for (File file : GetFiles(projectRootFile))
				{
					if (isSpecFile(file))
						specFiles.add(file);
				}
			}
		}

		try
		{

			TraceXmlWrapper txw = null;
			if (storeOutput)
			{
				txw = new TraceXmlWrapper(outputPath + File.separatorChar
						+ cls.get(0) + ".xml");
			}
			ti.processTraces(specFiles, cls.get(0), txw, true, dialect, release);
			if (txw != null)
			{
				txw.Stop();
			}
			if (log)
			{
				boolean logAppend = new File("log.txt").exists();
				FileWriter outFile = new FileWriter("log.txt", logAppend);
				PrintWriter out = new PrintWriter(outFile);
				out.println(ti.getCsv(!logAppend));
				out.close();
			}

		} catch (ClassNotFoundException e)
		{

		} catch (ContextException e)
		{
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static class TraceInterpreterCsv extends TraceInterpreter
	{
		public String projectName = "";
		//
		String[] header = {/* 0 */"Project", /* 1 */"Class", /* 2 */"Trace",
		/* 3 */"Seed", /* 4 */"Subset", /* 5 */"reduction", /* 6 */"size",
		/* 7 */"faild",/* 8 */"skipped", /* 9 */"inconclusive",/* 10 */"passed",/* 11 */
		"speed",/* 12 */"total speed" };
		List<String[]> csv = new Vector<String[]>();

		String[] currect;
		String[] currectClass;

		public TraceInterpreterCsv()
		{
			super();
		}

		public TraceInterpreterCsv(float subset,
				TraceReductionType traceReductionType, long seed)
		{
			super(subset, traceReductionType, seed);
		}

		@Override
		protected void preProcessingClass(String className, Integer traceCount)
		{
			super.preProcessingClass(className, traceCount);

			currectClass = new String[header.length];
			currectClass[0] = projectName;
			currectClass[1] = className;
			// csv.add(currectClass);

		}

		@Override
		protected void processingTrace(String className, String traceName,
				Integer testCount)
		{
			super.processingTrace(className, traceName, testCount);
			currect = new String[header.length];
			csv.add(currect);
			currect[0] = currectClass[0];
			currect[1] = currectClass[1];
			currect[2] = traceName;
			currect[3] = String.valueOf(seed);
			currect[4] = String.valueOf(subset);
			currect[5] = traceReductionType.toString();
			currect[6] = testCount.toString();
		}

		@Override
		protected void processingTraceFinished(String className, String name,
				int size, int faildCount, int inconclusiveCount,
				int skippedCount)
		{
			super.processingTraceFinished(className, name, size, faildCount, inconclusiveCount, skippedCount);
			currect[7] = String.valueOf(faildCount);
			currect[8] = String.valueOf(skippedCount);
			currect[9] = String.valueOf(inconclusiveCount);
			currect[10] = String.valueOf((size - (faildCount
					+ inconclusiveCount + skippedCount)));
			long endTrace = System.currentTimeMillis();
			currect[11] = String.valueOf((double) (endTrace - beginTrace) / 1000);
		}

		@Override
		protected void completed()
		{
			long endClass = System.currentTimeMillis();
			currectClass[12] = String.valueOf((double) (endClass - beginClass) / 1000);
			if(currect!=null)
			{
			currect[12] = String.valueOf((double) (endClass - beginClass) / 1000);
			}
			super.completed();
		}

		public String getCsv(boolean withHeader)
		{
			StringBuilder sb = new StringBuilder();
			final String divider = "\t;\t";
			if (withHeader)
			{
				for (String heading : header)
				{
					sb.append(heading + divider);
				}
				sb.append("\n");
				sb.append("\n");
			}

			for (String[] line : csv)
			{
				for (String string : line)
				{
					sb.append((string == null ? "" : string) + divider);
				}
				sb.append("\n");
			}

			return sb.toString();

		}

	}

	private static boolean isSpecFile(File file)
	{
		return file.getName().endsWith(".vpp")
				|| file.getName().endsWith(".vdmpp")
				|| file.getName().endsWith(".vdmrt")
				|| file.getName().endsWith(".vdm")
				|| file.getName().endsWith(".vdmsl");
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

//	private static String[] SplitInputFiles(String files, String splitter)
//	{
//		if (files.contains(splitter))
//			return files.split(splitter);
//		else
//			return new String[] { files };
//
//	}

	private static void PrintHelp()
	{
		System.out.println("Overture VDM Traces test");
		System.out.println("Usage: org.overture.traces.jar [options] [specfile{,specfile}]");
		// System.out.println("If no options are entered the GUI will show.");
		System.out.println();
		System.out.println("OPTIONS for command line usage:");
		System.out.println(" -outputPath Path to a folder where results will be stored.");
		System.out.println(" -c          Class names to be concidered {,classname}.");
		System.out.println(" -r          Release version of vdm e.g. vdm10, classic");
		System.out.println(" -d          Dialect e.g. vdmsl, vdmpp, vdmrt");
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
