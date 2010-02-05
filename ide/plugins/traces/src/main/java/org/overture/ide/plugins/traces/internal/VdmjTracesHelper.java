package org.overture.ide.plugins.traces.internal;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.overture.ide.ast.AstManager;
import org.overture.ide.ast.NotAllowedException;
import org.overture.ide.ast.RootNode;
import org.overture.ide.builders.builder.AbstractBuilder;
import org.overture.ide.plugins.traces.TracesXmlStoreReader;
import org.overture.ide.plugins.traces.TracesXmlStoreReader.TraceStatusXml;
import org.overture.ide.utility.IVdmProject;
import org.overture.ide.utility.VdmProject;
import org.overture.ide.vdmpp.core.VdmPpCorePluginConstants;
import org.overture.ide.vdmpp.core.VdmPpProjectNature;
import org.overture.ide.vdmrt.core.VdmRtCorePluginConstants;
import org.overture.ide.vdmrt.core.VdmRtProjectNature;
import org.overture.ide.vdmsl.core.VdmSlCorePluginConstants;
import org.overture.ide.vdmsl.core.VdmSlProjectNature;
import org.overturetool.traces.utility.ITracesHelper;
import org.overturetool.traces.utility.TraceHelperNotInitializedException;
import org.overturetool.traces.utility.TraceTestResult;
import org.overturetool.traces.utility.TraceTestStatus;
import org.overturetool.traces.utility.TraceXmlWrapper;
import org.overturetool.traces.vdmj.TraceInterpreter;
import org.overturetool.vdmj.definitions.ClassDefinition;
import org.overturetool.vdmj.definitions.ClassList;
import org.overturetool.vdmj.definitions.NamedTraceDefinition;
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.modules.Module;
import org.overturetool.vdmj.modules.ModuleList;
import org.overturetool.vdmj.traces.TraceReductionType;
import org.xml.sax.SAXException;

public class VdmjTracesHelper implements ITracesHelper {
	boolean initialized = false;
	// ClassList classes;
	//ClassInterpreter ci;
	String projectName;
	final String TRACE_STORE_DIR_NAME = "generated/traces";
	IVdmProject project;
	String nature = VdmPpProjectNature.VDM_PP_NATURE;
	HashMap<String, TracesXmlStoreReader> classTraceReaders = new HashMap<String, TracesXmlStoreReader>();
	File projectDir;
	Dialect dialect = Dialect.VDM_PP;
	String contentTypeId = VdmPpCorePluginConstants.CONTENT_TYPE;

	public VdmjTracesHelper(IProject project, int max) throws Exception {
		this.project = new VdmProject(project);
		
		if(project.hasNature(VdmPpProjectNature.VDM_PP_NATURE))
		{
			 nature = VdmPpProjectNature.VDM_PP_NATURE;
			 contentTypeId = VdmPpCorePluginConstants.CONTENT_TYPE;
			 dialect= Dialect.VDM_PP;
		}else if(project.hasNature(VdmRtProjectNature.VDM_RT_NATURE))
		{
			 nature = VdmRtProjectNature.VDM_RT_NATURE;
			 contentTypeId = VdmRtCorePluginConstants.CONTENT_TYPE;
			 dialect= Dialect.VDM_RT;
		}else if(project.hasNature(VdmSlProjectNature.VDM_SL_NATURE))
		{
			 nature = VdmSlProjectNature.VDM_SL_NATURE;
			 contentTypeId = VdmSlCorePluginConstants.CONTENT_TYPE;
			 dialect= Dialect.VDM_SL;
		}
		
		this.projectDir = new File(project.getLocation().toFile(), TRACE_STORE_DIR_NAME.replace('/', File.separatorChar));
		if (!this.projectDir.exists())
			this.projectDir.mkdirs();

		AbstractBuilder.parseMissingFiles(project, nature, contentTypeId ,null);

		org.overturetool.vdmj.Settings.prechecks = true;
		org.overturetool.vdmj.Settings.postchecks = true;
		org.overturetool.vdmj.Settings.dynamictypechecks = true;
		org.overturetool.vdmj.Settings.dialect=dialect;
		try {
//			ci = new ClassInterpreter(getClasses());
//			ci.init(null);
			initialized = true;
		} catch (Exception ex) {
			ConsolePrint(ex.getMessage());
		}

	}

	private ClassList getClasses() throws NotAllowedException {
		RootNode root = AstManager.instance().getRootNode(project.getProject(), nature);
		return root.getClassList();
	}
	
	private ModuleList getModules() throws NotAllowedException {
		RootNode root = AstManager.instance().getRootNode(project.getProject(), nature);
		return root.getModuleList();
	}	

	public List<String> GetClassNamesWithTraces() throws IOException {

		List<String> classNames = new ArrayList<String>();

		try {
			if(dialect == Dialect.VDM_SL)
			{
				for (Module classdef : getModules()) {
					for (Object string : classdef.defs) {
						if (string instanceof NamedTraceDefinition) {

							classNames.add(classdef.name.name);
							break;
						}
					}
				}
				
			}
		else
			{
			for (ClassDefinition classdef : getClasses()) {
				for (Object string : classdef.definitions) {
					if (string instanceof NamedTraceDefinition) {

						classNames.add(classdef.name.name);
						break;
					}
				}
			}}
			
		} catch (NotAllowedException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return classNames;
		}

		return classNames;
	}

	public File GetFile(String className)
			throws TraceHelperNotInitializedException, ClassNotFoundException {
		CheckInitialization();
		
		if(dialect == Dialect.VDM_SL)
		{
			Module module = findModule(className);
			if (module == null)
				throw new ClassNotFoundException("Module: "+className);

			return module.files.get(0);
		}else
		{
		ClassDefinition classdef = findClass(className);
		if (classdef == null)
			throw new ClassNotFoundException(className);

		return classdef.location.file;
		}
	}

	public TraceTestResult GetResult(String className, String trace, Integer num)
			throws IOException, SAXException {

		return classTraceReaders.get(className).GetTraceTestResults(trace, num,
				num).get(0);

	}

	public int GetSkippedCount(String className, String traceName)

	{
		if (classTraceReaders.containsKey(className)) {
			HashMap<String, TraceStatusXml> traceStatus = classTraceReaders.get(
					className).GetTraceStatus();
			if (traceStatus != null && traceStatus.containsKey(traceName)) {
				return traceStatus.get(traceName).getSkippedTestCount();
			}
		}

		return 0;
	}

	public TraceTestStatus GetStatus(String className, String trace, Integer num)

	{

		return null;
	}

	public void processClassTraces(String className, Object monitor)
			throws ClassNotFoundException, TraceHelperNotInitializedException,
			IOException, Exception {
		TraceInterpreter interpeter = null;
		if (monitor instanceof IProgressMonitor)
			interpeter = new ObservableTraceInterpeter((IProgressMonitor) monitor, this);
		else
			interpeter = new TraceInterpreter();

		TraceXmlWrapper storage = new TraceXmlWrapper(projectDir.getAbsolutePath()
				+ File.separatorChar + className + ".xml");

		
		buildProjectIfRequired();
		
		if(dialect== Dialect.VDM_SL)
		interpeter.processTrace(getModules(), className, false,storage,dialect,project.getLanguageVersion());
		else
			interpeter.processTrace(getClasses(), className, false,storage,dialect,project.getLanguageVersion());
	}
	
	public void processClassTraces(String className, float subset,
			TraceReductionType traceReductionType, long seed, Object monitor)
			throws ClassNotFoundException, TraceHelperNotInitializedException,
			IOException, Exception
	{
		TraceInterpreter interpeter = null;
		if (monitor instanceof IProgressMonitor)
			interpeter = new ObservableTraceInterpeter((IProgressMonitor) monitor, this);
		else
			interpeter = new TraceInterpreter();

		TraceXmlWrapper storage = new TraceXmlWrapper(projectDir.getAbsolutePath()
				+ File.separatorChar + className + ".xml");

		
		buildProjectIfRequired();
		
		if(dialect== Dialect.VDM_SL)
		interpeter.processTrace(getModules(), className, false,storage,dialect,project.getLanguageVersion(),subset,traceReductionType,seed);
		else
			interpeter.processTrace(getClasses(), className, false,storage,dialect,project.getLanguageVersion(),subset,traceReductionType,seed);
		
	}
	
	
	void buildProjectIfRequired()
	{
		RootNode root = AstManager.instance().getRootNode(project.getProject(), nature);
		if(root!=null && root.isChecked())
			return;
		else
			try {
				IProgressMonitor progressMonitor = null;

				project.getProject().build(IncrementalProjectBuilder.FULL_BUILD,progressMonitor);
			} catch (CoreException e) {
				System.out.println("Error forcing build from traces");
				e.printStackTrace();
			}
	}

	public List<NamedTraceDefinition> GetTraceDefinitions(String className)
			throws IOException, SAXException, ClassNotFoundException,
			TraceHelperNotInitializedException

	{
		CheckInitialization();
		List<NamedTraceDefinition> traces = new Vector<NamedTraceDefinition>();

		
		if(dialect == Dialect.VDM_SL)
		{
			Module moduledef = findModule(className);
			if (moduledef == null)
				throw new ClassNotFoundException(className);
			for (Object string : moduledef.defs) {
				if (string instanceof NamedTraceDefinition) {
					NamedTraceDefinition mtd = (NamedTraceDefinition) string;
					traces.add(mtd);
				}
			}
		}else{
			
		
		
		
		ClassDefinition classdef = findClass(className);
		if (classdef == null)
			throw new ClassNotFoundException(className);
		for (Object string : classdef.definitions) {
			if (string instanceof NamedTraceDefinition) {
				NamedTraceDefinition mtd = (NamedTraceDefinition) string;
				traces.add(mtd);
			}
		}
		}
		// Look for result file
		File classTraceXmlFile = new File(projectDir.getAbsolutePath()
				+ File.separatorChar + className + ".xml");
		if (classTraceXmlFile.exists()) {
			try {
				// result file exists, create reader
				TracesXmlStoreReader reader = new TracesXmlStoreReader(classTraceXmlFile, className);
				classTraceReaders.put(className, reader);
			} catch (SAXException e) {
				// e.printStackTrace();
				// TODO could not parse file. Posible not found
			}

		}

		return traces;
	}
	
	private ClassDefinition findClass(String className){
		
		try {
			for(ClassDefinition cl : getClasses())
			{
				if(cl.name.name.equals(className))
					return cl;
			}
		} catch (NotAllowedException e) {
			
		}
		return null;
	}
	
	private Module findModule(String moduleName){
		
		try {
			for(Module cl : getModules())
			{
				if(cl.name.name.equals(moduleName))
					return cl;
			}
		} catch (NotAllowedException e) {
			
		}
		return null;
	}

	public List<TraceTestResult> GetTraceTests(String className, String trace)
			throws IOException, SAXException

	{

		List<TraceTestResult> testStatus = classTraceReaders.get(className).GetTraceTestResults(
				trace, 1,
				classTraceReaders.get(className).GetTraceTestCount(trace));

		return testStatus;

	}

	public void processSingleTrace(String className, String traceName,
			Object monitor) throws ClassNotFoundException,
			TraceHelperNotInitializedException

	{

	}

	public void ConsolePrint(String message) {

		MessageConsole myConsole = findConsole("TracesConsole");
		MessageConsoleStream out = myConsole.newMessageStream();
		out.println(message);

	}

	private MessageConsole findConsole(String name) {
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

	public Integer GetTraceTestCount(String className, String trace) {
		if (classTraceReaders.containsKey(className))
			return classTraceReaders.get(className).GetTraceTestCount(trace);
		else
			return 0;

	}

	public List<TraceTestResult> GetTraceTests(String className, String trace,
			Integer startNumber, Integer stopNumber) throws IOException,
			SAXException {

		List<TraceTestResult> list = classTraceReaders.get(className).GetTraceTestResults(
				trace, startNumber, stopNumber);

		return list;
	}

	public boolean Initialized() {
		return initialized;
	}

	private void CheckInitialization()
			throws TraceHelperNotInitializedException {
		try {
			if(dialect== Dialect.VDM_SL && getModules()!=null && getModules().size()>0)
				return;
			else if (getClasses() != null && getClasses().size()>0)
				return;
			
			throw new TraceHelperNotInitializedException(projectName);
		} catch (NotAllowedException e) {
			throw new TraceHelperNotInitializedException(projectName);
		}
	}

	public String GetProjectName() {
		return projectName;
	}



}
