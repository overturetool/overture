package org.overturetool.traces;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.overturetool.eclipse.plugins.traces.TracesXmlStoreReader;
import org.overturetool.eclipse.plugins.traces.TracesXmlStoreReader.TraceStatusXml;
import org.overturetool.traces.utility.ITracesHelper;
import org.overturetool.traces.utility.TraceTestResult;
import org.overturetool.traces.utility.TraceTestStatus;
import org.overturetool.traces.utility.TraceXmlWrapper;
import org.overturetool.traces.vdmj.TraceInterpreter;
import org.overturetool.vdmj.definitions.ClassDefinition;
import org.overturetool.vdmj.definitions.ClassList;
import org.overturetool.vdmj.definitions.NamedTraceDefinition;
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.lex.LexTokenReader;
import org.overturetool.vdmj.runtime.ClassInterpreter;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.ObjectContext;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.statements.Statement;
import org.overturetool.vdmj.syntax.ClassReader;
import org.overturetool.vdmj.traces.CallSequence;
import org.overturetool.vdmj.traces.TestSequence;
import org.overturetool.vdmj.traces.Verdict;
import org.overturetool.vdmj.typechecker.ClassTypeChecker;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.FlatEnvironment;
import org.overturetool.vdmj.typechecker.PrivateClassEnvironment;
import org.overturetool.vdmj.typechecker.TypeChecker;
import org.overturetool.vdmj.values.ObjectValue;
import org.overturetool.vdmj.values.TupleValue;
import org.xml.sax.SAXException;

public class VdmjTracesHelper implements ITracesHelper
{

	ClassList classes;
	ClassInterpreter ci;
	//HashMap<NamedTraceDefinition, List<TraceTestResult>> traceResults = new HashMap<NamedTraceDefinition, List<TraceTestResult>>();

	HashMap<String, TracesXmlStoreReader> classTraceReaders = new HashMap<String, TracesXmlStoreReader>();
	File projectDir;

	public VdmjTracesHelper(File projectDir, File[] specFiles, int max)
			throws Exception
	{
		this.projectDir = new File(projectDir.getAbsolutePath()
				+ File.separatorChar + "Traces");
		if (!this.projectDir.exists())
			this.projectDir.mkdirs();
		classes = new ClassList();
		int parsErrors = 0;
		for (File file : specFiles)
		{
			LexTokenReader ltr;

			ltr = new LexTokenReader(file, Dialect.VDM_PP);

			ClassReader mr = new ClassReader(ltr);
			parsErrors += mr.getErrorCount();
			classes.addAll(mr.readClasses());
		}

		if (parsErrors == 0)
		{
			TypeChecker tc = new ClassTypeChecker(classes);
			tc.typeCheck();
			if (TypeChecker.getErrorCount() == 0)
			{
				ci = new ClassInterpreter(classes);

			} else
			{
				TypeChecker.printErrors(new PrintWriter(System.out));
			}
		}

		org.overturetool.vdmj.Settings.prechecks = true;
		org.overturetool.vdmj.Settings.postchecks = true;
		org.overturetool.vdmj.Settings.dynamictypechecks = true;
		try{
		ci.init(null);
		}catch(Exception ex)
		{
			ConsolePrint(ex.getMessage());
		}
	//	traceResults = new HashMap<NamedTraceDefinition, List<TraceTestResult>>();
	}

	public List<String> GetClassNamesWithTraces() throws IOException
	{

		List<String> classNames = new ArrayList<String>();

		for (ClassDefinition classdef : classes)
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

		return classNames;
	}

	public File GetFile(String className)
	{
		// TODO Auto-generated method stub
		ClassDefinition classdef = ci.findClass(className);
		return classdef.location.file;
	}

	public TraceTestResult GetResult(String className, String trace, Integer num)
			throws IOException, SAXException
	{
		
		return classTraceReaders.get(className).GetTraceTestResults(
				trace,
				num,
				num).get(0);

	}

	public int GetSkippedCount(String className, String traceName)

	{
		if (classTraceReaders.containsKey(className))
		{
			HashMap<String, TraceStatusXml> traceStatus = classTraceReaders.get(
					className).GetTraceStatus();
			if (traceStatus != null && traceStatus.containsKey(traceName))
			{
				return traceStatus.get(traceName).getSkippedTestCount();
			}
		}

		// TODO Auto-generated method stub
		return 0;
	}

	public TraceTestStatus GetStatus(String className, String trace, Integer num)

	{
//		ClassDefinition classdef = ci.findClass(className);
//
//		for (Object string : classdef.definitions)
//		{
//			if (string instanceof NamedTraceDefinition)
//			{
//				NamedTraceDefinition mtd = (NamedTraceDefinition) string;
//
//				if (!mtd.name.name.equals(trace))
//					continue;
//				if (traceResults.containsKey(mtd))
//					for (TraceTestStatus t : traceResults.get(mtd))
//					{
//						if (t.getNumber().equals(num))
//							return t;
//					}
//
//			}
//		}
		return null;
	}

	public void processClassTraces(String className, Object monitor)
			throws Exception
	{
		TraceInterpreter interpeter = null;
		if (monitor instanceof IProgressMonitor)
			interpeter = new ObservableTraceInterpeter(
					(IProgressMonitor) monitor, this);
		else
			interpeter = new TraceInterpreter();

		TraceXmlWrapper storage = new TraceXmlWrapper(
				projectDir.getAbsolutePath() + File.separatorChar + className
						+ ".xml");

		interpeter.processTraces(classes, className, storage);

	}

	private void SaveTraceTestResult(NamedTraceDefinition mtd,
			TraceTestResult traceTestResult)
	{
//		if (traceResults.containsKey(mtd))
//		{
//			traceResults.get(mtd).add(traceTestResult);
//		} else
//		{
//			List<TraceTestResult> tmp = new Vector<TraceTestResult>();
//			tmp.add(traceTestResult);
//			traceResults.put(mtd, tmp);
//		}
	}

	private TraceTestResult ExstractResult(CallSequence callSequence,
			List<Object> res, Integer number)
	{
		TraceTestResult traceResult = new TraceTestResult();
		traceResult.setNumber(number);
		
		for (Statement callObjectStatement : callSequence)
		{
			traceResult.addArgument(callObjectStatement.toString());
		}

		for (Object object2 : res)
		{
			if (object2 instanceof Verdict)
			{
				
					traceResult.setStatus((Verdict)object2);
				
			} else if (object2 instanceof TupleValue)
			{
				TupleValue v = (TupleValue) object2;
				for (Object object : v.values)
				{
					traceResult.addResult(object.toString());
				}

			} else if (object2 instanceof String)
				traceResult.addResult(object2.toString());

		}

		return traceResult;
	}

	public List<NamedTraceDefinition> GetTraceDefinitions(String className)
			throws IOException, SAXException

	{

		List<NamedTraceDefinition> traces = new Vector<NamedTraceDefinition>();

		ClassDefinition classdef = ci.findClass(className);
		for (Object string : classdef.definitions)
		{
			if (string instanceof NamedTraceDefinition)
			{
				NamedTraceDefinition mtd = (NamedTraceDefinition) string;
				traces.add(mtd);
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
		// else
		// throw new FileNotFoundException(
		// "Class Trace result XML file not found");

		return traces;
	}

	public List<TraceTestResult> GetTraceTests(String className, String trace)
			throws IOException, SAXException

	{
	
		List<TraceTestResult> testStatus = classTraceReaders.get(className).GetTraceTestResults(
				trace,
				1,
				classTraceReaders.get(className).GetTraceTestCount(trace));

		return testStatus;

	}

	public void processSingleTrace(String className, String traceName,
			Object monitor) throws Exception

	{

//		List<TraceTestStatus> returnValue = new Vector<TraceTestStatus>();
//
//		ClassDefinition classdef = ci.findClass(className);
//
//		for (Object string : classdef.definitions)
//		{
//			if (string instanceof NamedTraceDefinition)
//			{
//				NamedTraceDefinition mtd = (NamedTraceDefinition) string;
//
//				if (!mtd.name.name.equals(traceName))
//					continue;
//
//				ObjectValue object = null;
//
//				try
//				{
//					object = classdef.newInstance(null, null, ci.initialContext);
//				} catch (ValueException e)
//				{
//					// list.add(e.getMessage());
//					// list.add(Verdict.FAILED);
//					// return list;
//				}
//
//				Context ctxt = new ObjectContext(classdef.name.location,
//						classdef.name.name + "()", ci.initialContext, object);
//
//				TestSequence tests = mtd.getTests(ctxt);
//
//				Environment env = new FlatEnvironment(
//						classdef.getSelfDefinition(),
//						new PrivateClassEnvironment(classdef,
//								ci.getGlobalEnvironment()));
//
//				int n = 0;
//
//				for (CallSequence test : tests)
//				{
//
//					if (test.getFilter() > 0)
//					{
//						ConsolePrint("Test " + n + " = " + test);
//						ConsolePrint("Test " + n + " FILTERED by test "
//								+ test.getFilter());
//					} else
//					{
//						ci.init(null); // Initialize completely between every
//						// run...
//						List<Object> result = ci.runtrace(env, test);
//
//						if (result.get(result.size() - 1) == Verdict.FAILED)
//						{
//							String stem = test.toString(result.size() - 1);
//							ListIterator<CallSequence> it = tests.listIterator(n);
//
//							while (it.hasNext())
//							{
//								CallSequence other = it.next();
//
//								if (other.toString().startsWith(stem))
//								{
//									other.setFilter(n);
//								}
//							}
//						}
//
//						// Bodge until we figure out how to not have explicit op
//						// names.
//						TraceTestResult traceResult = ExstractResult(
//								test,
//								result,
//								n);
//
//						SaveTraceTestResult(mtd, traceResult);
//						returnValue.add(traceResult);
//						n++;
//					}
//				}
//				break;
//			}
//		}

	}

	public void ConsolePrint(String message)
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

	public Integer GetTraceTestCount(String className, String trace)
	{
		if (classTraceReaders.containsKey(className))
			return classTraceReaders.get(className).GetTraceTestCount(trace);
		else
			return 0;

	}

	public List<TraceTestResult> GetTraceTests(String className, String trace,
			Integer startNumber, Integer stopNumber) throws IOException,
			SAXException
	{
		
		List<TraceTestResult> list = classTraceReaders.get(className).GetTraceTestResults(
				trace,
				startNumber,
				stopNumber);

		return list;
	}

}
