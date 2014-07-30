package org.overture.ct.ctruntime;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.ANamedTraceDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.statements.PStm;
import org.overture.ast.typechecker.NameScope;
import org.overture.config.Settings;
import org.overture.ct.utils.TraceXmlWrapper;
import org.overture.interpreter.runtime.ClassInterpreter;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.ContextException;
import org.overture.interpreter.runtime.Interpreter;
import org.overture.interpreter.runtime.ModuleInterpreter;
import org.overture.interpreter.runtime.ValueException;
import org.overture.interpreter.traces.CallSequence;
import org.overture.interpreter.traces.TestSequence;
import org.overture.interpreter.traces.TraceReductionType;
import org.overture.interpreter.traces.TraceVariableStatement;
import org.overture.interpreter.traces.Verdict;
import org.overture.typechecker.Environment;
import org.overture.typechecker.FlatEnvironment;
import org.overture.typechecker.PrivateClassEnvironment;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class TraceInterpreter
{
	protected long beginClass = 0;
	protected long beginTrace = 0;
	protected String activeClass = "";
	protected String activeTrace;

	Interpreter interpreter;
	protected File coverage;
	boolean reduce = false;
	protected float subset;
	protected long seed = 999;
	protected TraceReductionType traceReductionType = TraceReductionType.NONE;
	IProgressMonitor monitor = null;

	private Integer currentPct = 0;

	public final ITypeCheckerAssistantFactory assistantFactory;

	public TraceInterpreter(IProgressMonitor monitor,
			ITypeCheckerAssistantFactory af)
	{
		this.monitor = monitor;
		this.assistantFactory = af;
	}

	public TraceInterpreter(IProgressMonitor monitor, float subset,
			TraceReductionType traceReductionType, long seed,
			ITypeCheckerAssistantFactory af)
	{
		this(monitor, af);
		this.reduce = true;
		this.seed = seed;
		this.traceReductionType = traceReductionType;
		this.subset = subset;
	}

	public void run(String moduleName, String traceName,
			Interpreter interpreter, TraceXmlWrapper store) throws Exception
	{
		this.interpreter = interpreter;

		List<PDefinition> definitions = null;

		if (interpreter instanceof ModuleInterpreter)
		{
			for (AModuleModules module : ((ModuleInterpreter) interpreter).modules)
			{
				if (module.getName().getName().equals(moduleName))
				{
					definitions = module.getDefs();
				}
			}
		} else
		{
			for (SClassDefinition classDefinition : ((ClassInterpreter) interpreter).getClasses())
			{
				if (classDefinition.getName().getName().equals(moduleName))
				{
					definitions = assistantFactory.createPDefinitionAssistant().getDefinitions(classDefinition);

				}
			}
		}

		processTraces(definitions, moduleName, traceName, store);
	}

	private void processTraces(List<PDefinition> definitions, String className,
			String traceName, TraceXmlWrapper storage) throws Exception
	{
		try
		{
			Settings.prechecks = true;
			Settings.postchecks = true;
			Settings.dynamictypechecks = true;

			if (storage != null)
			{
				storage.StartClass(className);
			}

			Integer numberOfTraces = 0;

			for (Object string : definitions)
			{
				if (string instanceof ANamedTraceDefinition)
				{
					numberOfTraces++;
				}

			}
			processingClass(className, numberOfTraces);

			List<ANamedTraceDefinition> traceDefs = getAllTraceDefinitions(definitions, traceName);

			for (ANamedTraceDefinition def : traceDefs)
			{
				evaluateTraceDefinition(className, storage, def);
			}

			completed();
			System.out.println("Completed");
		} catch (ContextException e)
		{
			// e.printStackTrace();
			error(e.getMessage());
			throw e;
		} catch (ValueException e)
		{
			// TODO Auto-generated catch block
			// e.printStackTrace();
			error(e.getMessage());
			throw e;
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			// e.printStackTrace();
			error(e.getMessage());
			throw e;
		} finally
		{
			if (storage != null)
			{
				storage.Stop();
			}
		}
	}

	/**
	 * obtain all trace definitions, or just the one named
	 * 
	 * @param definitions
	 * @param traceName
	 *            null or a name of a trace
	 * @return
	 */
	private List<ANamedTraceDefinition> getAllTraceDefinitions(
			List<PDefinition> definitions, String traceName)
	{
		List<ANamedTraceDefinition> traceDefs = new Vector<ANamedTraceDefinition>();

		for (Object definition : definitions)
		{
			if (definition instanceof ANamedTraceDefinition)
			{
				if (traceName == null
						|| ((ANamedTraceDefinition) definition).getName().getName().equals(traceName))
				{
					traceDefs.add((ANamedTraceDefinition) definition);
				}
			}
		}
		return traceDefs;
	}

	protected void evaluateTraceDefinition(String className,
			TraceXmlWrapper storage, Object definition) throws ValueException,
			AnalysisException, Exception
	{
		interpreter.init(null);
		Context ctxt = interpreter.getInitialTraceContext((ANamedTraceDefinition) definition, false);

		evaluateTests(className, storage, definition, ctxt);
	}

	private void evaluateTests(String className, TraceXmlWrapper storage,
			Object traceDefinition, Context ctxt) throws Exception
	{
		ANamedTraceDefinition mtd = (ANamedTraceDefinition) traceDefinition;
		TestSequence tests = null;
		if (!reduce)
		{
			subset = 1.0F;
			traceReductionType = TraceReductionType.NONE;
			seed = 999;
		}

		tests = ctxt.assistantFactory.createANamedTraceDefinitionAssistant().getTests(mtd, ctxt, subset, traceReductionType, seed);

		processingTrace(className, mtd.getName().getName(), tests.size());
		if (storage != null)
		{
			storage.StartTrace(mtd.getName().getName(), mtd.getLocation().getFile().getName(), mtd.getLocation().getStartLine(), mtd.getLocation().getStartPos(), tests.size(), new Float(subset), TraceReductionType.valueOf(traceReductionType.toString()), new Long(seed));
		}

		int n = 1;

		int faildCount = 0;
		int inconclusiveCount = 0;
		int skippedCount = 0;

		for (CallSequence test : tests)
		{
			processingTest(className, mtd.getName().getName(), n, tests.size());
			/*
			 * Bodge until we figure out how to not have explicit op names.
			 */
			String clean = test.toString().replaceAll("\\.\\w+`", ".");

			if (storage != null)
			{
				storage.StartTest(new Integer(n).toString(), clean);
				storage.StopElement();
			}

			if (test.getFilter() > 0)
			{
				skippedCount++;
				testFiltered(n, test.getFilter(), test);
				if (storage != null)
				{
					storage.AddSkippedResult(new Integer(n).toString());
				}
			} else
			{
				List<Object> result = null;

				// type check
				boolean typeOk = false;
				try
				{
					typeCheck(mtd.getClassDefinition(), interpreter, test);
					typeOk = true;
				} catch (Exception e)
				{
					result = new Vector<Object>();
					result.add(e);
					result.add(Verdict.FAILED);
				}

				// interpret
				if (typeOk)
				{
					result = evaluateCallSequence(mtd, test);

					if (result.get(result.size() - 1) == Verdict.ERROR)
					{
						if (storage != null)
						{
							storage.AddResults(new Integer(n).toString(), result);
							storage.AddTraceStatus(Verdict.valueOf(Verdict.FAILED.toString()), tests.size(), skippedCount, faildCount, inconclusiveCount);
							storage.StopElement();
						}

						Exception e = (Exception) result.get(result.size() - 2);
						result.remove(result.size() - 2);

						throw e;
					}

					tests.filter(result, test, n);
				}

				if (result.get(result.size() - 1) == Verdict.FAILED)
				{
					faildCount++;
				}

				else if (result.get(result.size() - 1) == Verdict.INCONCLUSIVE)
				{
					inconclusiveCount++;
				}

				if (storage != null)
				{
					storage.AddResults(new Integer(n).toString(), result);
				}

			}

			n++;
		}

		if (storage != null)
		{
			Verdict worstVerdict = Verdict.PASSED;
			if (faildCount > 0)
			{
				worstVerdict = Verdict.FAILED;
			} else if (inconclusiveCount > 0)
			{
				worstVerdict = Verdict.INCONCLUSIVE;
			}

			storage.AddTraceStatus(Verdict.valueOf(worstVerdict.toString()), tests.size(), skippedCount, faildCount, inconclusiveCount);
			storage.StopElement();
		}

		processingTraceFinished(className, mtd.getName().getName(), tests.size(), faildCount, inconclusiveCount, skippedCount);
	}

	protected void typeCheck(SClassDefinition classdef,
			Interpreter interpreter, CallSequence test)
			throws AnalysisException, Exception
	{
		Environment env = null;

		if (interpreter instanceof ClassInterpreter)
		{
			env = new FlatEnvironment(interpreter.getAssistantFactory(), classdef.apply(interpreter.getAssistantFactory().getSelfDefinitionFinder()), new PrivateClassEnvironment(interpreter.getAssistantFactory(), classdef, interpreter.getGlobalEnvironment()));
		} else
		{
			env = new FlatEnvironment(interpreter.getAssistantFactory(), new Vector<PDefinition>(), interpreter.getGlobalEnvironment());
		}

		for (PStm statement : test)
		{
			if (statement instanceof TraceVariableStatement)
			{
				((TraceVariableStatement) statement).typeCheck(env, NameScope.NAMESANDSTATE);
			} else
			{
				interpreter.typeCheck(statement, env);
			}

		}
	}

	protected List<Object> evaluateCallSequence(ANamedTraceDefinition mtd,
			CallSequence test)
	{
		List<Object> result;
		try
		{
			interpreter.init(null); /* Initialize completely between every run... */
			result = interpreter.runOneTrace(mtd, test, false);
		} catch (Exception e)
		{
			result = new Vector<Object>();
			result.add(e.getMessage());
			result.add(e);
			result.add(Verdict.ERROR);

		}
		return result;
	}

	protected void processingTraceFinished(String className, String name,
			int size, int faildCount, int inconclusiveCount, int skippedCount)
	{
		// System.out.println("Finished " + className + "`" + name + ":"
		// + "faild=" + faildCount + " inc=" + inconclusiveCount
		// + " skipped=" + skippedCount + " ok="
		// + (size - (faildCount + inconclusiveCount + skippedCount)));

	}

	private void processingClass(String className, Integer traceCount)
	{
		// beginClass = System.currentTimeMillis();
		// activeClass = className;
		// System.out.println("Executing: " + className + " - Trace count: "
		// + traceCount);

		preProcessingClass(className, traceCount);
	}

	protected void preProcessingClass(String className, Integer traceCount)
	{

	}

	protected void processingTrace(String className, String traceName,
			Integer testCount) throws IOException
	{
		if (monitor != null)
		{
			monitor.progressStartTrace(className + "`" + traceName);
			currentPct = 0;
		}
		// printTraceStatus();
		// beginTrace = System.currentTimeMillis();
		// activeTrace = traceName;
		// System.out.println(className + " - " + traceName + " Test count = "
		// + testCount + " Reduction: seed=" + seed + " subset=" + subset
		// + " type=" + traceReductionType);

		preProcessingTrace(className, traceName, testCount);
	}

	protected void preProcessingTrace(String className, String traceName,
			Integer testCount)
	{

	}

	protected void processingTest(String className, String traceName,
			Integer testNumber, Integer total) throws IOException
	{
		if (monitor != null)
		{
			Integer pct = new Double((double) testNumber / (double) total * 100).intValue();
			if (currentPct + 10 <= pct)
			{
				monitor.progress(pct);
				currentPct = pct;
			}

		}
	}

	protected void completed() throws IOException
	{
		if (monitor != null)
		{
			monitor.progressCompleted();
		}
		printTraceStatus();

		// long endClass = System.currentTimeMillis();
		// System.out.println("Class " + activeClass + " processed in "
		// + (double) (endClass - beginClass) / 1000 + " secs");

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

	protected void error(String message) throws IOException
	{
		System.err.println(message);
		if (this.monitor != null)
		{
			this.monitor.progressError(message);
		}
	}

	protected void typeError(String message)
	{
		System.err.println(message);
	}

	protected void parseError(String message)
	{
		System.err.println(message);
	}

	protected void typeCheckStarted()
	{

	}

	private void printTraceStatus()
	{
		// if (activeTrace != null && beginTrace != 0)
		// {
		// long endTrace = System.currentTimeMillis();
		// System.out.println("Trace " + activeClass + " - " + activeTrace
		// + " processed in " + (double) (endTrace - beginTrace)
		// / 1000 + " secs");
		// }
		prePrintTraceStatus();
	}

	protected void prePrintTraceStatus()
	{

	}

	public void setCoverageDir(File coverageDir)
	{
		this.coverage = coverageDir;
	}

	// private static void writeCoverage(Interpreter interpreter, File coverage)
	// throws IOException
	// {
	// for (File f : interpreter.getSourceFiles())
	// {
	// SourceFile source = interpreter.getSourceFile(f);
	//
	// File data = new File(coverage.getPath() + File.separator
	// + f.getName() + ".covtbl");
	// PrintWriter pw = new PrintWriter(data);
	// source.writeCoverage(pw);
	// pw.close();
	// }
	// }
}
