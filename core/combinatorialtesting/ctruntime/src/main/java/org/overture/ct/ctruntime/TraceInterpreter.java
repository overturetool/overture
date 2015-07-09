/*
 * #%~
 * Combinatorial Testing Runtime
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
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
import org.overture.ast.node.INode;
import org.overture.ast.statements.PStm;
import org.overture.ast.typechecker.NameScope;
import org.overture.ast.util.modules.CombinedDefaultModule;
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
import org.overture.typechecker.ModuleEnvironment;
import org.overture.typechecker.PrivateClassEnvironment;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class TraceInterpreter
{
	private final static boolean DEBUG = false;

	private static class StopWatch
	{
		static long before = 0;

		public static void set()
		{
			before = System.currentTimeMillis();
		}

		public static void stop(String msg)
		{
			if (DEBUG)
			{
				long after = System.currentTimeMillis();
				long duration = after - before;
				System.out.println("Completed '" + msg + "' in "
						+ (double) duration / 1000 + " secs. ");
			}
		}

	}

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
			infoProcessingClass(className, numberOfTraces);

			List<ANamedTraceDefinition> traceDefs = getAllTraceDefinitions(definitions, traceName);

			for (ANamedTraceDefinition def : traceDefs)
			{
				evaluateTraceDefinition(className, storage, def);
			}

			infoCompleted();

			if (DEBUG)
			{
				System.out.println("Completed");
			}
		} catch (ContextException e)
		{
			error(e.getMessage());
			throw e;
		} catch (ValueException e)
		{
			error(e.getMessage());
			throw e;
		} catch (Exception e)
		{
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
	 * @return a list of named trace definitions
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

		int size = tests.size();

		infoProcessingTrace(className, mtd.getName().getName(), size);
		if (storage != null)
		{
			storage.StartTrace(mtd.getName().getName(), mtd.getLocation().getFile().getName(), mtd.getLocation().getStartLine(), mtd.getLocation().getStartPos(), size, new Float(subset), TraceReductionType.valueOf(traceReductionType.toString()), new Long(seed));
		}

		INode traceContainer = null;
		Environment rootEnv = null;
		if (interpreter instanceof ClassInterpreter)
		{
			traceContainer = mtd.getClassDefinition();
			rootEnv = new PrivateClassEnvironment(interpreter.getAssistantFactory(), mtd.getClassDefinition(), interpreter.getGlobalEnvironment());
			;
		} else
		{
			traceContainer = mtd.parent();
			if(((AModuleModules)traceContainer).getIsFlat())
			{
				//search for the combined module
				for(AModuleModules m : ((ModuleInterpreter)interpreter).modules)
				{
					if(m instanceof CombinedDefaultModule)
					{
						traceContainer = m;
						break;
					}
				}
			}
			rootEnv = new ModuleEnvironment(interpreter.getAssistantFactory(), (AModuleModules) traceContainer);
		}

		int n = 1;

		int faildCount = 0;
		int inconclusiveCount = 0;
		int skippedCount = 0;

		StopWatch.set();

		for (CallSequence test : tests)
		{
			StopWatch.stop("Getting test");

			infoProcessingTest(className, mtd.getName().getName(), n, size);

			List<Object> result = null;
			Verdict verdict = null;

			// type check
			boolean typeOk = false;
			try
			{
				if (interpreter instanceof ClassInterpreter)
				{
					typeCheck(traceContainer, interpreter, test, rootEnv);
				} else
				{
					typeCheck(traceContainer, interpreter, test, rootEnv);
				}
				typeOk = true;
			} catch (Exception e)
			{
				result = new Vector<Object>();
				result.add(e);
				verdict = Verdict.FAILED;
				result.add(verdict);

			}

			// interpret
			if (typeOk)
			{
				StopWatch.set();
				result = evaluateCallSequence(mtd, test);
				StopWatch.stop("Executing   ");
				StopWatch.set();

				verdict = (Verdict) result.get(result.size() - 1);

				if (verdict == Verdict.ERROR)
				{
				} else
				{
					tests.filter(result, test, n);
				}
			}

			switch (verdict)
			{
				case FAILED:
					faildCount++;
					break;
				case INCONCLUSIVE:
					inconclusiveCount++;
					break;
				default:
					break;
			}

			if (storage != null)
			{/*
			 * Bodge until we figure out how to not have explicit op names.
			 */
				String clean = test.toString().replaceAll("\\.\\w+`", ".");
				storage.StartTest(new Integer(n).toString(), clean);
				storage.StopElement();
			}

			if (test.getFilter() > 0)
			{
				skippedCount++;
				infoTestFiltered(n, test.getFilter(), test);
				if (storage != null)
				{
					storage.AddSkippedResult(new Integer(n).toString());
				}
			} else
			{

				if (verdict == Verdict.ERROR)
				{
					if (storage != null)
					{
						storage.AddResults(new Integer(n).toString(), result);
						storage.AddTraceStatus(Verdict.valueOf(Verdict.FAILED.toString()), size, skippedCount, faildCount, inconclusiveCount);
						storage.StopElement();
					}

					Exception e = (Exception) result.get(result.size() - 2);
					result.remove(result.size() - 2);

					throw e;
				}

				if (storage != null)
				{
					storage.AddResults(new Integer(n).toString(), result);
				}
			}

			n++;
			StopWatch.stop("store&filter");
			StopWatch.set();
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

			storage.AddTraceStatus(Verdict.valueOf(worstVerdict.toString()), size, skippedCount, faildCount, inconclusiveCount);
			storage.StopElement();
		}

		infoProcessingTraceFinished(className, mtd.getName().getName(), size, faildCount, inconclusiveCount, skippedCount);
	}

	/**
	 * type check a test
	 * 
	 * @param classdef
	 * @param interpreter
	 * @param test
	 * @throws AnalysisException
	 * @throws Exception
	 */
	protected void typeCheck(INode classdef, Interpreter interpreter,
			CallSequence test, Environment outer) throws AnalysisException,
			Exception
	{
		FlatEnvironment env = null;

		if (classdef instanceof SClassDefinition)
		{

			env = new FlatEnvironment(interpreter.getAssistantFactory(), classdef.apply(interpreter.getAssistantFactory().getSelfDefinitionFinder()), outer);
		} else
		{

			env = new FlatEnvironment(interpreter.getAssistantFactory(), new Vector<PDefinition>(), outer);
		}

		for (int i = 0; i < test.size(); i++)
		{
			PStm statement = test.get(i);

			if (statement instanceof TraceVariableStatement)
			{
				((TraceVariableStatement) statement).typeCheck(env, NameScope.NAMESANDSTATE);
			} else
			{
				statement = statement.clone();
				test.set(i, statement);
				interpreter.typeCheck(statement, env);
			}

		}
	}

	/**
	 * interpret a test
	 * 
	 * @param mtd
	 * @param test
	 * @return list of results or list of errors
	 */
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

	protected void infoProcessingTraceFinished(String className, String name,
			int size, int faildCount, int inconclusiveCount, int skippedCount)
	{
	}

	private void infoProcessingClass(String className, Integer traceCount)
	{
		preProcessingClass(className, traceCount);
	}

	protected void preProcessingClass(String className, Integer traceCount)
	{

	}

	protected void infoProcessingTrace(String className, String traceName,
			Integer testCount) throws IOException
	{
		if (monitor != null)
		{
			monitor.progressStartTrace(className + "`" + traceName);
			currentPct = 0;
		}

		preProcessingTrace(className, traceName, testCount);
	}

	protected void preProcessingTrace(String className, String traceName,
			Integer testCount)
	{

	}

	protected void infoProcessingTest(String className, String traceName,
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

		if (DEBUG)
		{
			System.out.println(testNumber);
		}
	}

	protected void infoCompleted() throws IOException
	{
		if (monitor != null)
		{
			monitor.progressCompleted();
		}
		infoPrintTraceStatus();

		infoPreCompleted();
	}

	protected void infoPreCompleted()
	{

	}

	protected void infoTestFiltered(Integer number, Integer filteredBy,
			CallSequence test)
	{
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

	private void infoPrintTraceStatus()
	{
		infoPrePrintTraceStatus();
	}

	protected void infoPrePrintTraceStatus()
	{

	}

	public void setCoverageDir(File coverageDir)
	{
		this.coverage = coverageDir;
	}

}
