package org.overturetool.ct.ctruntime;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import org.overture.ast.definitions.ANamedTraceDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.modules.AModuleModules;
import org.overture.config.Settings;
import org.overture.interpreter.assistant.definition.ANamedTraceDefinitionAssistantInterpreter;
import org.overture.interpreter.runtime.ClassInterpreter;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.ContextException;
import org.overture.interpreter.runtime.Interpreter;
import org.overture.interpreter.runtime.ModuleInterpreter;
import org.overture.interpreter.runtime.ValueException;
import org.overture.interpreter.traces.CallSequence;
import org.overture.interpreter.traces.TestSequence;
import org.overture.interpreter.traces.TraceReductionType;
import org.overture.interpreter.traces.TypeCheckedTestSequence;
import org.overture.interpreter.traces.Verdict;
import org.overture.typechecker.assistant.definition.PDefinitionAssistantTC;
import org.overturetool.ct.utils.TraceXmlWrapper;

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

	public TraceInterpreter(IProgressMonitor monitor)
	{
		this.monitor = monitor;
	}

	public TraceInterpreter(IProgressMonitor monitor, float subset,
			TraceReductionType traceReductionType, long seed)
	{
		this(monitor);
		this.reduce = true;
		this.seed = seed;
		this.traceReductionType = traceReductionType;
		this.subset = subset;
	}

	public void run(String moduleName, String traceName,
			Interpreter interpreter, TraceXmlWrapper store) throws IOException
	{
		this.interpreter = interpreter;

		List<PDefinition> definitions = null;

		if (interpreter instanceof ModuleInterpreter)
		{
			for (AModuleModules module : ((ModuleInterpreter) interpreter).modules)
			{
				if (module.getName().name.equals(moduleName))
				{
					definitions = module.getDefs();
				}
			}
		} else
		{
			for (SClassDefinition classDefinition : ((ClassInterpreter) interpreter).getClasses())
			{
				if (classDefinition.getName().name.equals(moduleName))
				{
					definitions = PDefinitionAssistantTC.getDefinitions(classDefinition);
					
				}				
			}
		}

		processTraces(definitions, moduleName, traceName, store);
	}

	private void processTraces(List<PDefinition> definitions, String className,
			String traceName, TraceXmlWrapper storage) throws IOException
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

			for (Object definition : definitions)
			{
				if (definition instanceof ANamedTraceDefinition)
				{
					if (traceName == null
							|| ((ANamedTraceDefinition) definition).getName().name.equals(traceName))
					{
						interpreter.init(null);
						Context ctxt = interpreter.getInitialTraceContext((ANamedTraceDefinition) definition, false);

						evaluateTests(className, storage, definition, ctxt);
					}
				}
			}

			completed();
			System.out.println("Completed");
		} catch (ContextException e)
		{
			// e.printStackTrace();
			error(e.getMessage());
			// throw e;
		} catch (ValueException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally
		{
			if (storage != null)
			{
				storage.Stop();
			}
		}
	}

	private void evaluateTests(String className, TraceXmlWrapper storage,
			Object traceDefinition, Context ctxt) throws Exception
	{
		ANamedTraceDefinition mtd = (ANamedTraceDefinition) traceDefinition;
		TypeCheckedTestSequence tests = null;
		if (!reduce)
		{
			subset = 1.0F;
			traceReductionType = TraceReductionType.NONE;
			seed = 999;
		}

		TestSequence tests1 = ANamedTraceDefinitionAssistantInterpreter.getTests(mtd,ctxt, subset, traceReductionType, seed);
		if(tests1 instanceof TypeCheckedTestSequence)
		{
			tests = (TypeCheckedTestSequence) tests1;
		}else
		{
			throw new Exception("Failed to get tests");
		}

		processingTrace(className, mtd.getName().name, tests.size());
		if (storage != null)
		{
			storage.StartTrace(mtd.getName().name, mtd.getLocation().file.getName(), mtd.getLocation().startLine, mtd.getLocation().startPos, tests.getTests().size(), new Float(subset), TraceReductionType.valueOf(traceReductionType.toString()), new Long(seed));
		}

		int n = 1;

		int faildCount = 0;
		int inconclusiveCount = 0;
		int skippedCount = 0;

		for (CallSequence test : tests.getTests())
		{
			processingTest(className, mtd.getName().name, n, tests.size());
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
				skippedCount++;
				testFiltered(n, test.getFilter(), test);
				if (storage != null)
				{
					storage.AddSkippedResult(new Integer(n).toString());
				}
			} else
			{
				List<Object> result = null;
				
				if(tests.isTypeCorrect(test))
				{
					interpreter.init(null); // Initialize completely between
					// every
					// run...
					result = interpreter.runOneTrace(mtd, test, false);
	
					tests.filter(result, test, n);
				}else
				{
					result = new Vector<Object>();
					result.add(tests.getTypeCheckError(test));
					result.add(Verdict.FAILED);
				}

				if (result.get(result.size() - 1) == Verdict.FAILED)
				{
					faildCount++;
				}

				else if (result.get(result.size() - 1) == Verdict.INCONCLUSIVE)
				{
					inconclusiveCount++;
				}

				for (int i = 0; i < result.size(); i++)
				{
					if (result.get(i) instanceof Verdict)
					{
						result.set(i, Verdict.valueOf(result.get(i).toString()));
					}

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

			storage.AddTraceStatus(Verdict.valueOf(worstVerdict.toString()), tests.getTests().size(), skippedCount, faildCount, inconclusiveCount);
			storage.StopElement();
		}

		processingTraceFinished(className, mtd.getName().name, tests.getTests().size(), faildCount, inconclusiveCount, skippedCount);
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
			Integer pct = new Double(((double) testNumber / (double) total) * 100).intValue();
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

	protected void error(String message)
	{
		System.err.println(message);
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
