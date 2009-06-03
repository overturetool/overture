package org.overturetool.traces.vdmj;

import java.io.File;
import java.io.PrintWriter;
import java.util.List;
import java.util.ListIterator;

import org.overturetool.traces.utility.TraceXmlWrapper;
import org.overturetool.vdmj.definitions.ClassDefinition;
import org.overturetool.vdmj.definitions.ClassList;
import org.overturetool.vdmj.definitions.NamedTraceDefinition;
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.lex.LexTokenReader;
import org.overturetool.vdmj.messages.Console;
import org.overturetool.vdmj.runtime.ClassInterpreter;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.ObjectContext;
import org.overturetool.vdmj.runtime.ValueException;
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

public class TraceInterpreter
{

	
	ClassInterpreter ci;

	public void processTraces(List<File> specFiles, String className,
			TraceXmlWrapper storage) throws Exception
	{

		ClassList classes = new ClassList();
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
			processTraces(classes, className, storage);
		}
	}

	public void processTraces(ClassList classes, String className,
			TraceXmlWrapper storage) throws Exception
	{

		TypeChecker tc = new ClassTypeChecker(classes);
		tc.typeCheck();
		if (TypeChecker.getErrorCount() == 0)
		{
			ci = new ClassInterpreter(classes);

		} else
		{
			String typeErrors = "";
			TypeChecker.printErrors(new PrintWriter(typeErrors));
			typeError(typeErrors);
		}

		org.overturetool.vdmj.Settings.prechecks = true;
		org.overturetool.vdmj.Settings.postchecks = true;
		org.overturetool.vdmj.Settings.dynamictypechecks = true;
		ci.init(null);

		ClassDefinition classdef = ci.findClass(className);

		// PublicClassEnvironment globals = new PublicClassEnvironment(classes);
		// Environment env = new PrivateClassEnvironment(me, globals);

		if (storage != null)
			storage.StartClass(className);

		Integer numberOfTraces = 0;
		for (Object string : classdef.definitions)
		{
			if (string instanceof NamedTraceDefinition)
				numberOfTraces++;

		}
		processingClass(className, numberOfTraces);

		for (Object string : classdef.definitions)
		{
			if (string instanceof NamedTraceDefinition)
			{
				NamedTraceDefinition mtd = (NamedTraceDefinition) string;
				ObjectValue object = null;

				try
				{
					object = classdef.newInstance(null, null, ci.initialContext);
				} catch (ValueException e)
				{
					error(e.getMessage());
					throw e;
					// list.add(e.getMessage());
					// list.add(Verdict.FAILED);
					// return list;
				}

				Context ctxt = new ObjectContext(classdef.name.location,
						classdef.name.name + "()", ci.initialContext, object);

				TestSequence tests = mtd.getTests(ctxt);

				processingTrace(className, mtd.name.name, tests.size());
				if (storage != null)
					storage.StartTrace(
							mtd.name.name,
							mtd.location.startLine,
							mtd.location.startPos,
							tests.size());
				// List<List<Object>> results = new Vector<List<Object>>();
				// List<String> testSatements = new Vector<String>();
				// for (CallSequence callSequence : ts) {
				// List<Object> dd = interpreter.runtrace(me, env,
				// callSequence);
				// results.add(dd);
				// int yy = 8;
				// }
				Environment env = new FlatEnvironment(
						classdef.getSelfDefinition(),
						new PrivateClassEnvironment(classdef,
								ci.getGlobalEnvironment()));

				int n = 1;

				for (CallSequence test : tests)
				{
					processingTest(className, mtd.name.name, n);
					// Bodge until we figure out how to not have explicit op
					// names.
					String clean = test.toString().replaceAll("\\.\\w+`", ".");

					if (storage != null)
					{
						storage.StartTest(new Integer(n).toString(), clean);
						storage.StopElement();
					}

					if (test.getFilter() > 0)
					{
						testFiltered(n,test.getFilter(),test);
//						Console.out.println("Test " + n + " = " + test);
//						Console.out.println("Test " + n + " FILTERED by test "
//								+ test.getFilter());

						storage.AddSkippedResult(new Integer(n).toString());
					} else
					{
						ci.init(null); // Initialize completely between every
						// run...
						List<Object> result = ci.runtrace(env, test);

						if (result.get(result.size() - 1) == Verdict.FAILED)
						{
							String stem = test.toString(result.size() - 1);
							ListIterator<CallSequence> it = tests.listIterator(n);

							while (it.hasNext())
							{
								CallSequence other = it.next();

								if (other.toString().startsWith(stem))
								{
									other.setFilter(n);
								}
							}
						}

						if (storage != null)
						{
							// storage.StartTest(new
							// Integer(n).toString(),clean);
							// storage.StopElement();

							storage.AddResults(
									new Integer(n).toString(),
									result);
						}

						// Console.out.println("Test " + n + " = " + clean);
						// testSatements.add(clean);
						// Console.out.println("Result = " + result);
						// results.add(result);
					}

					n++;
				}

				if (storage != null)
					storage.StopElement();
			}

		}
		if (storage != null)
			storage.Stop();
		
		completed();
	}

	protected void processingClass(String className, Integer traceCount)
	{

	}

	protected void processingTrace(String className, String traceName,
			Integer testCount)
	{

	}

	protected void processingTest(String className, String traceName,
			Integer testNumber)
	{

	}
	
	protected void completed()
	{
		
	}
	
	protected void testFiltered(Integer number,Integer filteredBy,CallSequence test)
	{
//		Console.out.println("Test " + number + " = " + test);
//		Console.out.println("Test " + number + " FILTERED by test "
//				+ filteredBy);
	}
	
	protected void error(String message)
	{
		

	}
	
	protected void typeError(String message)
	{
		

	}
}
