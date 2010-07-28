package org.overture.ide.plugins.traces.internal;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.overture.ide.core.ast.NotAllowedException;
import org.overture.ide.core.resources.IVdmProject;
import org.overture.ide.plugins.traces.TracesXmlStoreReader;
import org.overture.ide.plugins.traces.TracesXmlStoreReader.TraceInfo;
import org.overture.ide.plugins.traces.TracesXmlStoreReader.TraceStatusXml;
import org.overture.ide.ui.utility.VdmTypeCheckerUi;
import org.overture.ide.vdmpp.core.IVdmPpCoreConstants;
import org.overturetool.traces.utility.ITracesHelper;
import org.overturetool.traces.utility.TraceHelperNotInitializedException;
import org.overturetool.traces.utility.TraceTestResult;
import org.overturetool.traces.utility.TraceTestStatus;
import org.overturetool.traces.utility.TraceXmlWrapper;
import org.overturetool.traces.vdmj.TraceInterpreter;
import org.overturetool.vdmj.definitions.ClassDefinition;
import org.overturetool.vdmj.definitions.NamedTraceDefinition;
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.modules.Module;
import org.overturetool.vdmj.traces.TraceReductionType;
import org.xml.sax.SAXException;

public class VdmjTracesHelper implements ITracesHelper
{
	boolean initialized = false;
	// ClassList classes;
	// ClassInterpreter ci;
	String projectName;
	final String TRACE_STORE_DIR_NAME = "generated/traces";
	public final IVdmProject project;
	String nature = IVdmPpCoreConstants.NATURE;
	Map<String, TracesXmlStoreReader> classTraceReaders = new HashMap<String, TracesXmlStoreReader>();
	File projectDir;
	Dialect dialect = Dialect.VDM_PP;
	String contentTypeId = IVdmPpCoreConstants.CONTENT_TYPE;
	Shell shell;

	public VdmjTracesHelper(Shell shell, IVdmProject project, int max)
			throws Exception
	{
		this.project = project;
		this.shell = shell;
		// if (project.hasNature(VdmPpProjectNature.VDM_PP_NATURE))
		// {
		// nature = VdmPpProjectNature.VDM_PP_NATURE;
		// contentTypeId = VdmPpCorePluginConstants.CONTENT_TYPE;
		// dialect = Dialect.VDM_PP;
		// } else if (project.hasNature(VdmRtProjectNature.VDM_RT_NATURE))
		// {
		// nature = VdmRtProjectNature.VDM_RT_NATURE;
		// contentTypeId = VdmRtCorePluginConstants.CONTENT_TYPE;
		// dialect = Dialect.VDM_RT;
		// } else if (project.hasNature(VdmSlProjectNature.VDM_SL_NATURE))
		// {
		// nature = VdmSlProjectNature.VDM_SL_NATURE;
		// contentTypeId = VdmSlCorePluginConstants.CONTENT_TYPE;
		// dialect = Dialect.VDM_SL;
		// }

		project.refreshLocal(IResource.DEPTH_INFINITE, null);

		nature = this.project.getVdmNature();
		contentTypeId = this.project.getContentTypeIds().get(0);
		dialect = this.project.getDialect();

		this.projectDir = new File(project.getLocation().toFile(),
				TRACE_STORE_DIR_NAME.replace('/', File.separatorChar));
		if (!this.projectDir.exists())
			this.projectDir.mkdirs();

		project.getModel().refresh(false, null);
		// AbstractBuilder.parseMissingFiles(project, nature, contentTypeId,
		// null);

		org.overturetool.vdmj.Settings.prechecks = true;
		org.overturetool.vdmj.Settings.postchecks = true;
		org.overturetool.vdmj.Settings.dynamictypechecks = true;
		org.overturetool.vdmj.Settings.dialect = dialect;
		try
		{
			// ci = new ClassInterpreter(getClasses());
			// ci.init(null);
			initialized = true;
		} catch (Exception ex)
		{
			consolePrint(ex.getMessage());
		}

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

	public List<String> getClassNamesWithTraces() throws IOException
	{

		List<String> classNames = new ArrayList<String>();

		try
		{
			if (dialect == Dialect.VDM_SL)
			{
				for (Module classdef : project.getModel().getModuleList())
				{
					for (Object string : classdef.defs)
					{
						if (string instanceof NamedTraceDefinition)
						{

							classNames.add(classdef.name.name);
							break;
						}
					}
				}

			} else
			{
				for (ClassDefinition classdef : project.getModel()
						.getClassList())
				{
					for (Object string : classdef.definitions)
					{
						if (string instanceof NamedTraceDefinition)
						{

							classNames.add(classdef.name.name);
							break;
						}
					}
				}
			}

		} catch (NotAllowedException e)
		{
			// TODO Auto-generated catch block
			// e.printStackTrace();
			return classNames;
		}

		return classNames;
	}

	public File getFile(String className)
			throws TraceHelperNotInitializedException, ClassNotFoundException
	{
		checkInitialization();

		if (dialect == Dialect.VDM_SL)
		{
			Module module = findModule(className);
			if (module == null)
				throw new ClassNotFoundException("Module: " + className);

			return module.files.get(0);
		} else
		{
			ClassDefinition classdef = findClass(className);
			if (classdef == null)
				throw new ClassNotFoundException(className);

			return classdef.location.file;
		}
	}

	public TraceTestResult getResult(String className, String trace, Integer num)
			throws IOException, SAXException
	{

		return classTraceReaders.get(className).getTraceTestResults(trace, num,
				num).get(0);

	}

	public int getSkippedCount(String className, String traceName)

	{
		if (classTraceReaders.containsKey(className))
		{
			Map<String, TraceStatusXml> traceStatus = classTraceReaders.get(
					className).getTraceStatus();
			if (traceStatus != null && traceStatus.containsKey(traceName))
			{
				return traceStatus.get(traceName).getSkippedTestCount();
			}
		}

		return 0;
	}

	public TraceTestStatus getStatus(String className, String trace, Integer num)

	{

		return null;
	}

	public void processClassTraces(String className, Object monitor)
			throws ClassNotFoundException, TraceHelperNotInitializedException,
			IOException, Exception
	{
		TraceInterpreter interpeter = null;
		if (monitor instanceof IProgressMonitor)
			interpeter = new ObservableTraceInterpeter(
					(IProgressMonitor) monitor, this);
		else
			interpeter = new TraceInterpreter();

		TraceXmlWrapper storage = new TraceXmlWrapper(projectDir
				.getAbsolutePath()
				+ File.separatorChar + className + ".xml");

		buildProjectIfRequired();

		// Thread.sleep(1000);

		if (dialect == Dialect.VDM_SL)
		{
			interpeter.processTrace(project.getModel().getModuleList(),
					className, false, storage, dialect, project
							.getLanguageVersion());
		} else
		{
			interpeter.processTrace(project.getModel().getClassList(),
					className, false, storage, dialect, project
							.getLanguageVersion());
		}
	}

	public void processClassTraces(String className, float subset,
			TraceReductionType traceReductionType, long seed, Object monitor)
			throws ClassNotFoundException, TraceHelperNotInitializedException,
			IOException, Exception
	{
		TraceInterpreter interpeter = null;
		if (monitor instanceof IProgressMonitor)
			interpeter = new ObservableTraceInterpeter(
					(IProgressMonitor) monitor, this);
		else
			interpeter = new TraceInterpreter();

		TraceXmlWrapper storage = new TraceXmlWrapper(projectDir
				.getAbsolutePath()
				+ File.separatorChar + className + ".xml");

		buildProjectIfRequired();

		if (dialect == Dialect.VDM_SL)
			interpeter.processTrace(project.getModel().getModuleList(),
					className, false, storage, dialect, project
							.getLanguageVersion(), subset, traceReductionType,
					seed);
		else
			interpeter.processTrace(project.getModel().getClassList(),
					className, false, storage, dialect, project
							.getLanguageVersion(), subset, traceReductionType,
					seed);

	}

	void buildProjectIfRequired()
	{
		project.getModel().clean();
		shell.getDisplay().syncExec(new Runnable()
		{

			public void run()
			{
				VdmTypeCheckerUi.typeCheck(shell, project);

			}

		});

		try
		{
			project.refreshLocal(IResource.DEPTH_INFINITE, null);
		} catch (CoreException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// RootNode root =
		// AstManager.instance().getRootNode(project.getProject(),
		// nature);
		// if (root != null && root.isChecked())
		// return;
		// else
		// try
		// {
		// IProgressMonitor progressMonitor = null;
		//
		// project.getProject()
		// .build(IncrementalProjectBuilder.FULL_BUILD,
		// progressMonitor);
		// } catch (CoreException e)
		// {
		// System.out.println("Error forcing build from traces");
		// e.printStackTrace();
		// }
	}

	public List<NamedTraceDefinition> getTraceDefinitions(String className)
			throws IOException, SAXException, ClassNotFoundException,
			TraceHelperNotInitializedException

	{
		checkInitialization();
		List<NamedTraceDefinition> traces = new Vector<NamedTraceDefinition>();

		if (dialect == Dialect.VDM_SL)
		{
			Module moduledef = findModule(className);
			if (moduledef == null)
				throw new ClassNotFoundException(className);
			for (Object string : moduledef.defs)
			{
				if (string instanceof NamedTraceDefinition)
				{
					NamedTraceDefinition mtd = (NamedTraceDefinition) string;
					traces.add(mtd);
				}
			}
		} else
		{

			ClassDefinition classdef = findClass(className);
			if (classdef == null)
				throw new ClassNotFoundException(className);
			for (Object string : classdef.definitions)
			{
				if (string instanceof NamedTraceDefinition)
				{
					NamedTraceDefinition mtd = (NamedTraceDefinition) string;
					traces.add(mtd);
				}
			}
		}
		// Look for result file
		File classTraceXmlFile = new File(projectDir.getAbsolutePath()
				+ File.separatorChar + className + ".xml");
		if (classTraceXmlFile.exists())
		{
			try
			{
				// result file exists, create reader
				TracesXmlStoreReader reader = new TracesXmlStoreReader(
						classTraceXmlFile, className);
				classTraceReaders.put(className, reader);
			} catch (SAXException e)
			{
				// e.printStackTrace();
				// TODO could not parse file. Posible not found
			}

		}

		return traces;
	}

	private ClassDefinition findClass(String className)
	{

		try
		{
			for (ClassDefinition cl : project.getModel().getClassList())
			{
				if (cl.name.name.equals(className))
					return cl;
			}
		} catch (NotAllowedException e)
		{

		}
		return null;
	}

	private Module findModule(String moduleName)
	{

		try
		{
			Module m = new Module();

			for (Module cl : project.getModel().getModuleList())
			{
				if (cl.name.name.equals(moduleName))
				{
					m.defs.addAll(cl.defs);
					m.files.addAll(cl.files);
				}
			}
			return m;
		} catch (NotAllowedException e)
		{

		}
		return null;
	}

	public List<TraceTestResult> getTraceTests(String className, String trace)
			throws IOException, SAXException

	{

		List<TraceTestResult> testStatus = classTraceReaders.get(className)
				.getTraceTestResults(
						trace,
						1,
						classTraceReaders.get(className).getTraceTestCount(
								trace));

		return testStatus;

	}

	public void processSingleTrace(String className, String traceName,
			Object monitor) throws ClassNotFoundException,
			TraceHelperNotInitializedException

	{

	}

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

	public Integer getTraceTestCount(String className, String trace)
	{
		if (classTraceReaders.containsKey(className))
			return classTraceReaders.get(className).getTraceTestCount(trace);
		else
			return 0;

	}

	public TraceInfo getTraceInfo(String className, String trace)
	{
		if (classTraceReaders.containsKey(className))
			return classTraceReaders.get(className).getTraceInfo(trace);

		return null;
	}

	public List<TraceTestResult> getTraceTests(String className, String trace,
			Integer startNumber, Integer stopNumber) throws IOException,
			SAXException
	{

		List<TraceTestResult> list = classTraceReaders.get(className)
				.getTraceTestResults(trace, startNumber, stopNumber);

		return list;
	}

	public boolean initialized()
	{
		return initialized;
	}

	private void checkInitialization()
			throws TraceHelperNotInitializedException
	{
		try
		{
			if (dialect == Dialect.VDM_SL
					&& project.getModel().getModuleList() != null
					&& project.getModel().getModuleList().size() > 0)
				return;
			else if (project.getModel().getClassList() != null
					&& project.getModel().getClassList().size() > 0)
				return;

			throw new TraceHelperNotInitializedException(projectName);
		} catch (NotAllowedException e)
		{
			throw new TraceHelperNotInitializedException(projectName);
		}
	}

	public String getProjectName()
	{
		return projectName;
	}

}
