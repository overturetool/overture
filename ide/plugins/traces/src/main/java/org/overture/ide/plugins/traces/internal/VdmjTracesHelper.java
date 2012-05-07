package org.overture.ide.plugins.traces.internal;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.overture.ast.definitions.ANamedTraceDefinition;
import org.overture.ast.node.INode;
import org.overture.ide.core.resources.IVdmProject;
import org.overture.ide.core.resources.IVdmSourceUnit;
import org.overture.ide.plugins.traces.TracesXmlStoreReader;
import org.overture.ide.plugins.traces.views.TraceAstUtility;
import org.overturetool.ct.utils.TraceHelperNotInitializedException;
import org.overturetool.ct.utils.TraceReductionType;

public class VdmjTracesHelper // implements ITracesHelper
{
	String projectName;
	// final String TRACE_STORE_DIR_NAME = "generated/traces";
	final String COVERAGE_DIR_NAME = "generated/coverage";
	public final IVdmProject project;
	Map<String, TracesXmlStoreReader> classTraceReaders = new HashMap<String, TracesXmlStoreReader>();
	File projectDir;
	Shell shell;

	private File coverageBaseDir;

	public VdmjTracesHelper(Shell shell, IVdmProject vdmProject)
			throws Exception
	{
		this.project = vdmProject;
		this.shell = shell;
		// IProject project = (IProject) vdmProject.getAdapter(IProject.class);
		// Assert.isNotNull(project, "Project could not be adapted");

		// project.refreshLocal(IResource.DEPTH_INFINITE, null);

		// this.projectDir = new File(project.getLocation().toFile(), TRACE_STORE_DIR_NAME.replace('/',
		// File.separatorChar));
		// if (!this.projectDir.exists())
		// this.projectDir.mkdirs();

		// this.coverageBaseDir = new File(project.getLocation().toFile(), COVERAGE_DIR_NAME.replace('/',
		// File.separatorChar));
		// if (!this.coverageBaseDir.exists())
		// this.coverageBaseDir.mkdirs();

		// vdmProject.getModel().refresh(false, null);
		// AbstractBuilder.parseMissingFiles(project, nature, contentTypeId,
		// null);

	}

	// private ClassList getClasses() throws NotAllowedException
	// {
	// RootNode root = AstManager.instance().getRootNode(project.getProject(),
	// nature);
	// if (root != null)
	// return root.getClassList();
	// else
	// return new ClassList();
	// }
	//
	// private ModuleList getModules() throws NotAllowedException
	// {
	// RootNode root = AstManager.instance().getRootNode(project.getProject(),
	// nature);
	// if (root != null)
	// return root.getModuleList();
	// else
	// return new ModuleList();
	// }

	public File getCTRunCoverageDir()
	{
		DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
		File coverageDir = new File(coverageBaseDir, "CT_"
				+ dateFormat.format(new Date()));

		if (!coverageDir.exists())
			coverageDir.mkdirs();

		return coverageDir;
	}

	// public List<String> getClassNamesWithTraces() throws IOException
	// {
	//
	// List<String> classNames = new ArrayList<String>();
	//
	// try
	// {
	// if (project.getDialect() == Dialect.VDM_SL)
	// {
	// for (AModuleModules classdef : project.getModel().getModuleList())
	// {
	// for (Object string : classdef.getDefs())
	// {
	// if (string instanceof ANamedTraceDefinition)
	// {
	//
	// classNames.add(classdef.getName().name);
	// break;
	// }
	// }
	// }
	//
	// } else
	// {
	// for (SClassDefinition classdef : project.getModel().getClassList())
	// {
	// for (Object string : classdef.getDefinitions())
	// {
	// if (string instanceof ANamedTraceDefinition)
	// {
	//
	// classNames.add(classdef.getName().name);
	// break;
	// }
	// }
	// }
	// }
	//
	// } catch (NotAllowedException e)
	// {
	// // TODO Auto-generated catch block
	// // e.printStackTrace();
	// return classNames;
	// }
	//
	// return classNames;
	// }

	// public File getFile(String className)
	// throws TraceHelperNotInitializedException, ClassNotFoundException
	// {
	// checkInitialization();
	//
	// if (project.getDialect()== Dialect.VDM_SL)
	// {
	// AModuleModules module = findModule(className);
	// if (module == null)
	// throw new ClassNotFoundException("Module: " + className);
	//
	// return module.getFiles().get(0);
	// } else
	// {
	// SClassDefinition classdef = findClass(className);
	// if (classdef == null)
	// throw new ClassNotFoundException(className);
	//
	// return classdef.getLocation().file;
	// }
	// }

	// public TraceTestResult getResult(String className, String trace, Integer num)
	// throws IOException, SAXException
	// {
	//
	// return classTraceReaders.get(className).getTraceTestResults(trace, num, num).get(0);
	//
	// }

	// public int getSkippedCount(String className, String traceName)
	//
	// {
	// if (classTraceReaders.containsKey(className))
	// {
	// Map<String, TraceStatusXml> traceStatus = classTraceReaders.get(className).getTraceStatus();
	// if (traceStatus != null && traceStatus.containsKey(traceName))
	// {
	// return traceStatus.get(traceName).getSkippedTestCount();
	// }
	// }
	//
	// return 0;
	// }

	// public TraceTestStatus getStatus(String className, String trace, Integer num)
	//
	// {
	//
	// return null;
	// }

	// public void processClassTraces(String className, Object monitor)
	// throws ClassNotFoundException, TraceHelperNotInitializedException,
	// IOException, Exception
	// {
	// File coverageDir = getCTRunCoverageDir();
	//
	// MessageConsole myConsole = findConsole("TracesConsole");
	// MessageConsoleStream out = myConsole.newMessageStream();
	// // out.println(message);
	// new TraceTestEngine().launch(project, className, null, coverageDir, out);
	//
	// copySourceFilesForCoverage(coverageDir);
	// try
	// {
	// IProject p = (IProject) project.getAdapter(IProject.class);
	// p.refreshLocal(IResource.DEPTH_INFINITE, null);
	// } catch (CoreException e)
	// {
	//
	// }
	// }

	public void processClassTraces(String className, float subset,
			TraceReductionType traceReductionType, long seed, Object monitor)
			throws ClassNotFoundException, TraceHelperNotInitializedException,
			IOException, Exception
	{

		// FIXME: //if (dialect == Dialect.VDM_SL)
		// interpeter.processTrace(project.getModel().getModuleList(), className, false, storage, dialect,
		// project.getLanguageVersion(), subset, traceReductionType, seed);
		// else
		// interpeter.processTrace(project.getModel().getClassList(), className, false, storage, dialect,
		// project.getLanguageVersion(), subset, traceReductionType, seed);

		// copySourceFilesForCoverage(coverageDir);

	}

	private void copySourceFilesForCoverage(File coverageDir)
			throws IOException, CoreException
	{
		for (IVdmSourceUnit source : this.project.getSpecFiles())
		{
			String name = source.getSystemFile().getName();

			writeFile(coverageDir, name + "cov", getContent(source));
		}
	}

	private String getContent(IVdmSourceUnit source) throws CoreException,
			IOException
	{
		InputStreamReader reader = new InputStreamReader(source.getFile().getContents());
		StringBuilder sb = new StringBuilder();

		int inLine;
		while ((inLine = reader.read()) != -1)
		{
			sb.append((char) inLine);
		}
		return sb.toString();
	}

	public static void writeFile(File outputFolder, String fileName,
			String content) throws IOException
	{
		FileWriter outputFileReader = new FileWriter(new File(outputFolder, fileName));
		BufferedWriter outputStream = new BufferedWriter(outputFileReader);
		outputStream.write(content);
		outputStream.close();
	}

	// void buildProjectIfRequired()
	// {
	// shell.getDisplay().syncExec(new Runnable()
	// {
	//
	// public void run()
	// {
	// VdmTypeCheckerUi.typeCheck(shell, project);
	//
	// }
	//
	// });
	//
	// try
	// {
	// IProject aproject = (IProject) project.getAdapter(IProject.class);
	// aproject.refreshLocal(IResource.DEPTH_INFINITE, null);
	// } catch (CoreException e)
	// {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	// }

	// public List<ANamedTraceDefinition> getTraceDefinitions(String className)
	// throws IOException, SAXException, ClassNotFoundException,
	// TraceHelperNotInitializedException
	//
	// {
	// checkInitialization();
	// List<ANamedTraceDefinition> traces = new Vector<ANamedTraceDefinition>();
	//
	// if (project.getDialect() == Dialect.VDM_SL)
	// {
	// AModuleModules moduledef = findModule(className);
	// if (moduledef == null)
	// throw new ClassNotFoundException(className);
	// for (Object string : moduledef.getDefs())
	// {
	// if (string instanceof ANamedTraceDefinition)
	// {
	// ANamedTraceDefinition mtd = (ANamedTraceDefinition) string;
	// traces.add(mtd);
	// }
	// }
	// } else
	// {
	//
	// SClassDefinition classdef = findClass(className);
	// if (classdef == null)
	// throw new ClassNotFoundException(className);
	// for (Object string : classdef.getDefinitions())
	// {
	// if (string instanceof ANamedTraceDefinition)
	// {
	// ANamedTraceDefinition mtd = (ANamedTraceDefinition) string;
	// traces.add(mtd);
	// }
	// }
	// }
	// // Look for result file
	// File classTraceXmlFile = new File(projectDir.getAbsolutePath()
	// + File.separatorChar + className + ".xml");
	// if (classTraceXmlFile.exists())
	// {
	// try
	// {
	// // result file exists, create reader
	// TracesXmlStoreReader reader = new TracesXmlStoreReader(classTraceXmlFile, className);
	// classTraceReaders.put(className, reader);
	// } catch (SAXException e)
	// {
	// // e.printStackTrace();
	// // TODO could not parse file. Posible not found
	// }
	//
	// }
	//
	// return traces;
	// }

	// private SClassDefinition findClass(String className)
	// {
	//
	// try
	// {
	// for (SClassDefinition cl : project.getModel().getClassList())
	// {
	// if (cl.getName().name.equals(className))
	// return cl;
	// }
	// } catch (NotAllowedException e)
	// {
	//
	// }
	// return null;
	// }

	// private AModuleModules findModule(String moduleName)
	// {
	//
	// try
	// {
	// AModuleModules m = new AModuleModules();
	// List<ClonableFile> files = new Vector<ClonableFile>();
	// for (AModuleModules cl : project.getModel().getModuleList())
	// {
	// if (cl.getName().name.equals(moduleName))
	// {
	// m.getDefs().addAll(cl.getDefs());
	// files.addAll(cl.getFiles());
	// }
	// }
	// m.setFiles(files);
	// return m;
	// } catch (NotAllowedException e)
	// {
	//
	// }
	// return null;
	// }

	// public List<TraceTestResult> getTraceTests(String className, String trace)
	// throws IOException, SAXException
	//
	// {
	//
	// List<TraceTestResult> testStatus = classTraceReaders.get(className).getTraceTestResults(trace, 1,
	// classTraceReaders.get(className).getTraceTestCount(trace));
	//
	// return testStatus;
	//
	// }

	// public void processSingleTrace(String className, String traceName,
	// Object monitor) throws ClassNotFoundException,
	// TraceHelperNotInitializedException
	//
	// {
	//
	// }

	public void consolePrint(String message)
	{

		MessageConsole myConsole = findConsole("TracesConsole");
		MessageConsoleStream out = myConsole.newMessageStream();
		out.println(message);

	}

	private MessageConsole findConsole(String name)
	{
		ConsolePlugin plugin = ConsolePlugin.getDefault();
		IConsoleManager conMan = plugin.getConsoleManager();
		IConsole[] existing = conMan.getConsoles();
		for (int i = 0; i < existing.length; i++)
			if (name.equals(existing[i].getName()))
				return (MessageConsole) existing[i];
		// no console found, so create a new one
		MessageConsole myConsole = new MessageConsole(name, null);
		conMan.addConsoles(new IConsole[] { myConsole });
		return myConsole;
	}

	public void evaluateTraces(INode container, ANamedTraceDefinition traceDef,
			IProgressMonitor monitor, ITracesDisplay display)
			throws IOException, CoreException
	{
//		List<TraceExecutionSetup> traceSetups = new Vector<TraceExecutionSetup>();
//
//		if (container != null)
//		{
//			if (traceDef != null)
//			{
//				TraceExecutionSetup texe = new TraceExecutionSetup(project, TraceAstUtility.getContainerName(container), traceDef.getName().name, getCTRunCoverageDir());
//				traceSetups.add(texe);
//			} else
//			{
//				for (ANamedTraceDefinition tDef : TraceAstUtility.getTraceDefinitions(container))
//				{
//					TraceExecutionSetup texe = new TraceExecutionSetup(project, TraceAstUtility.getContainerName(container), tDef.getName().name, getCTRunCoverageDir());
//					traceSetups.add(texe);
//				}
//			}
//		}else
//		{
//			for (INode c : TraceAstUtility.getTraceContainers(project))
//			{
//				if (traceDef != null)
//				{
//					TraceExecutionSetup texe = new TraceExecutionSetup(project, TraceAstUtility.getContainerName(c), traceDef.getName().name, getCTRunCoverageDir());
//					traceSetups.add(texe);
//				} else
//				{
//					for (ANamedTraceDefinition tDef : TraceAstUtility.getTraceDefinitions(c))
//					{
//						TraceExecutionSetup texe = new TraceExecutionSetup(project, TraceAstUtility.getContainerName(c), tDef.getName().name, getCTRunCoverageDir());
//						traceSetups.add(texe);
//					}
//				}
//			}
//		}
//
//		execute(monitor, display, traceSetups);
		evaluateTraces(container, traceDef, 0,null,0,monitor, display, false);
	}

	public void evaluateTraces(INode container, ANamedTraceDefinition traceDef,
			float subset, TraceReductionType traceReductionType, long seed,
			IProgressMonitor monitor, ITracesDisplay display) throws IOException, CoreException
	{
		evaluateTraces(container, traceDef, subset,traceReductionType,seed,monitor, display, true);
	}
	
	private void evaluateTraces(INode container, ANamedTraceDefinition traceDef,
			float subset, TraceReductionType traceReductionType, long seed,
			IProgressMonitor monitor, ITracesDisplay display, boolean useReduction) throws IOException, CoreException
	{
		List<TraceExecutionSetup> traceSetups = new Vector<TraceExecutionSetup>();

		if (container != null)
		{
			if (traceDef != null)
			{
				TraceExecutionSetup texe = new TraceExecutionSetup(project, TraceAstUtility.getContainerName(container), traceDef.getName().name, getCTRunCoverageDir(),subset, traceReductionType,seed,useReduction);
				traceSetups.add(texe);
			} else
			{
				for (ANamedTraceDefinition tDef : TraceAstUtility.getTraceDefinitions(container))
				{
					TraceExecutionSetup texe = new TraceExecutionSetup(project, TraceAstUtility.getContainerName(container), tDef.getName().name, getCTRunCoverageDir(),subset, traceReductionType,seed,useReduction);
					traceSetups.add(texe);
				}
			}
		}else
		{
			for (INode c : TraceAstUtility.getTraceContainers(project))
			{
				if (traceDef != null)
				{
					TraceExecutionSetup texe = new TraceExecutionSetup(project, TraceAstUtility.getContainerName(c), traceDef.getName().name, getCTRunCoverageDir(),subset, traceReductionType,seed,useReduction);
					traceSetups.add(texe);
				} else
				{
					for (ANamedTraceDefinition tDef : TraceAstUtility.getTraceDefinitions(c))
					{
						TraceExecutionSetup texe = new TraceExecutionSetup(project, TraceAstUtility.getContainerName(c), tDef.getName().name, getCTRunCoverageDir(),subset, traceReductionType,seed,useReduction);
						traceSetups.add(texe);
					}
				}
			}
		}

		execute(monitor, display, traceSetups);
	}

	private void execute(IProgressMonitor monitor, ITracesDisplay display,
			List<TraceExecutionSetup> texe) throws IOException, CoreException
	{
		MessageConsole myConsole = findConsole("TracesConsole");
		MessageConsoleStream out = myConsole.newMessageStream();

		for (TraceExecutionSetup traceExecutionSetup : texe)
		{
			new TraceTestEngine().launch(traceExecutionSetup, out, display);
			copySourceFilesForCoverage(traceExecutionSetup.coverageFolder);
		}

		try
		{
			IProject p = (IProject) project.getAdapter(IProject.class);
			p.refreshLocal(IResource.DEPTH_INFINITE, null);
		} catch (CoreException e)
		{

		}
	}

	// public Integer getTraceTestCount(String className, String trace)
	// {
	// if (classTraceReaders.containsKey(className))
	// return classTraceReaders.get(className).getTraceTestCount(trace);
	// else
	// return 0;
	//
	// }
	//
	// public TraceInfo getTraceInfo(String className, String trace)
	// {
	// if (classTraceReaders.containsKey(className))
	// return classTraceReaders.get(className).getTraceInfo(trace);
	//
	// return null;
	// }

	// public List<TraceTestResult> getTraceTests(String className, String trace,
	// Integer startNumber, Integer stopNumber) throws IOException,
	// SAXException
	// {
	//
	// List<TraceTestResult> list = classTraceReaders.get(className).getTraceTestResults(trace, startNumber,
	// stopNumber);
	//
	// return list;
	// }

	// private void checkInitialization()
	// throws TraceHelperNotInitializedException
	// {
	// try
	// {
	// if (project.getDialect() == Dialect.VDM_SL
	// && project.getModel().getModuleList() != null
	// && project.getModel().getModuleList().size() > 0)
	// return;
	// else if (project.getModel().getClassList() != null
	// && project.getModel().getClassList().size() > 0)
	// return;
	//
	// throw new TraceHelperNotInitializedException(projectName);
	// } catch (NotAllowedException e)
	// {
	// throw new TraceHelperNotInitializedException(projectName);
	// }
	// }

	// public String getProjectName()
	// {
	// return projectName;
	// }

}
