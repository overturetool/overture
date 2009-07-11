package org.overturetool.traces.vdmj;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import java.util.ListIterator;

import org.overturetool.traces.utility.TraceXmlWrapper;
import org.overturetool.vdmj.definitions.ClassDefinition;
import org.overturetool.vdmj.definitions.ClassList;
import org.overturetool.vdmj.definitions.NamedTraceDefinition;
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.lex.LexTokenReader;
import org.overturetool.vdmj.runtime.ClassInterpreter;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.ContextException;
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
	protected long beginClass = 0;
	protected long beginTrace = 0;
	protected String activeClass = "";
	protected String activeTrace;

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
			if (mr.getErrorCount() > 0)
			{
				Writer parseErrors = new StringWriter();
				mr.printErrors(new PrintWriter(parseErrors));
				parseError(parseErrors.toString());
			}
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
		try
		{
			TypeChecker tc = new ClassTypeChecker(classes);
			typeCheckStarted();
			tc.typeCheck();
			classes.setLoaded(); //do not type check this classes again
			if (TypeChecker.getErrorCount() == 0)
			{
				ci = new ClassInterpreter(classes);

			} else
			{
				Writer typeErrors = new StringWriter();
				TypeChecker.printErrors(new PrintWriter(typeErrors));
				typeError(typeErrors.toString());
			}

			org.overturetool.vdmj.Settings.prechecks = true;
			org.overturetool.vdmj.Settings.postchecks = true;
			org.overturetool.vdmj.Settings.dynamictypechecks = true;
			ci.init(null);

			ClassDefinition classdef = ci.findClass(className);

			if (classdef == null)
			{
				error("Class not found: " + className);
				throw new ClassNotFoundException(className);
			}

			// PublicClassEnvironment globals = new
			// PublicClassEnvironment(classes);
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
						object = classdef.newInstance(
								null,
								null,
								ci.initialContext);
					} catch (ValueException e)
					{
						error(e.getMessage());
						throw e;
						// list.add(e.getMessage());
						// list.add(Verdict.FAILED);
						// return list;
					}

					Context ctxt = new ObjectContext(classdef.name.location,
							classdef.name.name + "()", ci.initialContext,
							object);

					TestSequence tests = mtd.getTests(ctxt);

					processingTrace(className, mtd.name.name, tests.size());
					if (storage != null)
						storage.StartTrace(
								mtd.name.name,
								mtd.location.file.getName(),
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

					int faildCount = 0;
					int inconclusiveCount = 0;
					int skippedCount = 0;

					for (CallSequence test : tests)
					{
						processingTest(className, mtd.name.name, n);
						// Bodge until we figure out how to not have explicit op
						// names.
						String clean = test.toString().replaceAll(
								"\\.\\w+`",
								".");

						if (storage != null)
						{
							storage.StartTest(new Integer(n).toString(), clean);
							storage.StopElement();
						}

						if (test.getFilter() > 0)
						{
							skippedCount++;
							testFiltered(n, test.getFilter(), test);
							// Console.out.println("Test " + n + " = " + test);
							// Console.out.println("Test " + n +
							// " FILTERED by test "
							// + test.getFilter());

							storage.AddSkippedResult(new Integer(n).toString());
						} else
						{
							ci.init(null); // Initialize completely between
							// every
							// run...
							List<Object> result = ci.runtrace(
									className,
									env,
									test);

							if (result.get(result.size() - 1) == Verdict.FAILED)
							{
								faildCount++;

								// String stem = test.toString(result.size() -
								// 1);
								// ListIterator<CallSequence> it =
								// tests.listIterator(n);
								//
								// while (it.hasNext())
								// {
								// CallSequence other = it.next();
								//
								// if (other.toString().startsWith(stem))
								// {
								// other.setFilter(n);
								// }
								// }

								int stem = result.size() - 1;
								ListIterator<CallSequence> it = tests.listIterator(n);

								while (it.hasNext())
								{
									CallSequence other = it.next();

									if (other.compareStem(test, stem))
									{
										other.setFilter(n);
									}
								}

							} else if (result.get(result.size() - 1) == Verdict.INCONCLUSIVE)
								inconclusiveCount++;

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
					{
						Verdict worstVerdict = Verdict.PASSED;
						if (faildCount > 0)
							worstVerdict = Verdict.FAILED;
						else if (inconclusiveCount > 0)
							worstVerdict = Verdict.INCONCLUSIVE;

						storage.AddTraceStatus(
								worstVerdict,
								tests.size(),
								skippedCount,
								faildCount,
								inconclusiveCount);
						storage.StopElement();
					}
				}

			}

			completed();
		} catch (ContextException e)
		{
			error(e.getMessage());
			throw e;
		} catch (Exception e)
		{

			throw e;
		} finally
		{
			if (storage != null)
				storage.Stop();
		}
	}

	private void processingClass(String className, Integer traceCount)
	{
		beginClass = System.currentTimeMillis();
		activeClass = className;
		System.out.println("Executing: " + className + " - Trace count: "
				+ traceCount);

		preProcessingClass(className, traceCount);
	}

	protected void preProcessingClass(String className, Integer traceCount)
	{

	}

	protected void processingTrace(String className, String traceName,
			Integer testCount)
	{
		printTraceStatus();
		beginTrace = System.currentTimeMillis();
		activeTrace = traceName;
		System.out.println(className + " - " + traceName + " Test count = "
				+ testCount);

		preProcessingTrace(className, traceName, testCount);
	}

	protected void preProcessingTrace(String className, String traceName,
			Integer testCount)
	{

	}

	protected void processingTest(String className, String traceName,
			Integer testNumber)
	{

	}

	protected void completed()
	{
		printTraceStatus();

		long endClass = System.currentTimeMillis();
		System.out.println("Class " + activeClass + " processed in "
				+ (double) (endClass - beginClass) / 1000 + " secs");

		preCompleted();
	}

	protected void preCompleted()
	{

	}

	protected void testFiltered(Integer number, Integer filteredBy,
			CallSequence test)
	{
		// Console.out.println("Test " + number + " = " + test);
		// Console.out.println("Test " + number + " FILTERED by test "
		// + filteredBy);
	}

	protected void error(String message)
	{
		System.out.println(message);
	}

	protected void typeError(String message)
	{
		System.out.println(message);
	}

	protected void parseError(String message)
	{
		System.out.println(message);
	}

	protected void typeCheckStarted()
	{

	}

	private void printTraceStatus()
	{
		if (activeTrace != null && beginTrace != 0)
		{
			long endTrace = System.currentTimeMillis();
			System.out.println("Trace " + activeClass + " - " + activeTrace
					+ " processed in " + (double) (endTrace - beginTrace)
					/ 1000 + " secs");
		}
		prePrintTraceStatus();
	}

	protected void prePrintTraceStatus()
	{

	}
}
