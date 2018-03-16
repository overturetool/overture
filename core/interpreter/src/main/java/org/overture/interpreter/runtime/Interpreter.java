/*******************************************************************************
 *
 *	Copyright (C) 2008 Fujitsu Services Ltd.
 *
 *	Author: Nick Battle
 *
 *	This file is part of VDMJ.
 *
 *	VDMJ is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *	the Free Software Foundation, either version 3 of the License, or
 *	(at your option) any later version.
 *
 *	VDMJ is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with VDMJ.  If not, see <http://www.gnu.org/licenses/>.
 *
 ******************************************************************************/

package org.overture.interpreter.runtime;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.ANamedTraceDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.lex.Dialect;
import org.overture.ast.lex.LexIdentifierToken;
import org.overture.ast.lex.LexNameToken;
import org.overture.ast.lex.LexToken;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.node.INode;
import org.overture.ast.statements.PStm;
import org.overture.ast.typechecker.NameScope;
import org.overture.ast.types.PType;
import org.overture.ast.util.modules.CombinedDefaultModule;
import org.overture.config.Settings;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.debug.BreakpointManager;
import org.overture.interpreter.debug.DBGPReader;
import org.overture.interpreter.messages.Console;
import org.overture.interpreter.scheduler.BasicSchedulableThread;
import org.overture.interpreter.scheduler.ResourceScheduler;
import org.overture.interpreter.traces.CallSequence;
import org.overture.interpreter.traces.TestSequence;
import org.overture.interpreter.traces.TraceReductionType;
import org.overture.interpreter.traces.TraceVariableStatement;
import org.overture.interpreter.traces.Verdict;
import org.overture.interpreter.values.Value;
import org.overture.parser.lex.LexException;
import org.overture.parser.lex.LexTokenReader;
import org.overture.parser.messages.VDMErrorsException;
import org.overture.parser.syntax.ParserException;
import org.overture.pog.pub.IProofObligationList;
import org.overture.typechecker.Environment;
import org.overture.typechecker.FlatEnvironment;
import org.overture.typechecker.ModuleEnvironment;
import org.overture.typechecker.PrivateClassEnvironment;
import org.overture.typechecker.TypeCheckInfo;
import org.overture.typechecker.TypeChecker;
import org.overture.typechecker.visitor.TypeCheckVisitor;

/**
 * An abstract VDM interpreter.
 */

abstract public class Interpreter
{
	/** the assistant factory used by this interpreter for e.g. FunctionValues */
	protected final IInterpreterAssistantFactory assistantFactory;

	/** The main thread scheduler */
	public ResourceScheduler scheduler;

	/** The initial execution context. */
	public RootContext initialContext;

	/** A list of breakpoints created. */
	public Map<Integer, Breakpoint> breakpoints;

	/** A list of source files loaded. */
	public Map<File, SourceFile> sourceFiles;

	/** The number of the next breakpoint to be created. */
	public int nextbreakpoint = 0;

	/** A static instance pointer to the interpreter. */
	protected static Interpreter instance = null;

	/**
	 * Create an Interpreter.
	 * 
	 * @param assistantFactory
	 *            the assistant factory to be used by the interpreter
	 */

	public Interpreter(IInterpreterAssistantFactory assistantFactory)
	{
		scheduler = new ResourceScheduler();
		breakpoints = new TreeMap<Integer, Breakpoint>();
		sourceFiles = new HashMap<File, SourceFile>();
		this.assistantFactory = assistantFactory;
		instance = this;
	}

	/**
	 * Gets the current assistant factory
	 * 
	 * @return
	 */
	public IInterpreterAssistantFactory getAssistantFactory()
	{
		return assistantFactory;
	}

	/**
	 * Get a string version of the environment.
	 * 
	 * @return
	 */

	public String getInitialContext()
	{
		return initialContext.toString();
	}

	/**
	 * Get the global environment.
	 * 
	 * @return
	 */

	abstract public Environment getGlobalEnvironment();
	
	/**
	 * Get the global environment for some module
	 * 
	 * @return
	 */
	abstract public Environment getGlobalEnvironment(String module);

	/**
	 * @return The Interpreter instance.
	 */

	public static Interpreter getInstance()
	{
		return instance; // NB. last one created
	}

	/**
	 * Get the name of the default module or class. Symbols in the default module or class do not have to have their
	 * names qualified when being referred to on the command line.
	 * 
	 * @return The default name.
	 */

	abstract public String getDefaultName();

	/**
	 * Get the filename that contains the default module or class.
	 * 
	 * @return The default file name.
	 */

	abstract public File getDefaultFile();

	/**
	 * Set the default module or class name.
	 * 
	 * @param name
	 *            The default name.
	 * @throws Exception
	 */

	abstract public void setDefaultName(String name) throws Exception;

	/**
	 * Initialize the initial context. This means that all definition initializers are re-run to put the global
	 * environment back into its original state. This is run implicitly when the interpreter starts, but it can also be
	 * invoked explicitly via the "init" command.
	 * 
	 * @param dbgp
	 */

	abstract public void init(DBGPReader dbgp);

	/**
	 * Initialize the context between trace sequences. This is less thorough than the full init, since it does not reset
	 * the scheduler for example.
	 * 
	 * @param dbgp
	 */

	abstract public void traceInit(DBGPReader dbgp);

	/**
	 * Parse the line passed, type check it and evaluate it as an expression in the initial context.
	 * 
	 * @param line
	 *            A VDM expression.
	 * @param dbgp
	 *            The DBGPReader, if any
	 * @return The value of the expression.
	 * @throws Exception
	 *             Parser, type checking or runtime errors.
	 */

	abstract public Value execute(String line, DBGPReader dbgp)
			throws Exception;

	/**
	 * Parse the content of the file passed, type check it and evaluate it as an expression in the initial context.
	 * 
	 * @param file
	 *            A file containing a VDM expression.
	 * @return The value of the expression.
	 * @throws Exception
	 *             Parser, type checking or runtime errors.
	 */

	public Value execute(File file) throws Exception
	{
		BufferedReader br = new BufferedReader(new FileReader(file));
		StringBuilder sb = new StringBuilder();

		String line = br.readLine();

		while (line != null)
		{
			sb.append(line);
			line = br.readLine();
		}

		br.close();

		Value result = execute(sb.toString(), null);

		BasicSchedulableThread.terminateAll(); // NB not a session (used for tests)
		return result;
	}

	/**
	 * Parse the line passed, and evaluate it as an expression in the context passed. Note that this does not type check
	 * the expression.
	 * 
	 * @param line
	 *            A VDM expression.
	 * @param ctxt
	 *            The context in which to evaluate the expression.
	 * @return The value of the expression.
	 * @throws Exception
	 *             Parser or runtime errors.
	 */

	abstract public Value evaluate(String line, Context ctxt) throws Exception;

	/**
	 * @return The list of breakpoints currently set.
	 */

	public Map<Integer, Breakpoint> getBreakpoints()
	{
		return breakpoints;
	}

	/**
	 * Get a line of a source file.
	 * 
	 * @param src
	 * @return
	 */

	public String getSourceLine(ILexLocation src)
	{
		return getSourceLine(src.getFile(), src.getStartLine());
	}

	/**
	 * Get a line of a source file by its location.
	 * 
	 * @param file
	 * @param line
	 * @return
	 */

	public String getSourceLine(File file, int line)
	{
		return getSourceLine(file, line, ":  ");
	}

	/**
	 * Get a line of a source file by its location.
	 * 
	 * @param file
	 * @param line
	 * @param sep
	 * @return
	 */

	public String getSourceLine(File file, int line, String sep)
	{
		SourceFile source = sourceFiles.get(file);

		if (source == null)
		{
			try
			{
				source = new SourceFile(file);
				sourceFiles.put(file, source);
			} catch (IOException e)
			{
				return "Cannot open source file: " + file;
			}
		}

		return line + sep + source.getLine(line);
	}

	/**
	 * Get an entire source file object.
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */

	public SourceFile getSourceFile(File file) throws IOException
	{
		SourceFile source = sourceFiles.get(file);

		if (source == null)
		{
			source = new SourceFile(file);
			sourceFiles.put(file, source);
		}

		return source;
	}

	/**
	 * Get a list of all source files.
	 * 
	 * @return
	 */

	abstract public Set<File> getSourceFiles();

	/**
	 * Get a list of proof obligations for the loaded specification.
	 * 
	 * @return A list of POs.
	 * @throws AnalysisException
	 */

	abstract public IProofObligationList getProofObligations()
			throws AnalysisException;

	/**
	 * Find a statement by file name and line number.
	 * 
	 * @param file
	 *            The name of the class/module
	 * @param lineno
	 *            The line number
	 * @return A Statement object if found, else null.
	 */

	abstract public PStm findStatement(File file, int lineno);

	/**
	 * Find an expression by file name and line number.
	 * 
	 * @param file
	 *            The name of the file
	 * @param lineno
	 *            The line number
	 * @return An Expression object if found, else null.
	 */

	abstract public PExp findExpression(File file, int lineno);

	/**
	 * Find a global environment value by name.
	 * 
	 * @param name
	 *            The name of the variable
	 * @return A Value object if found, else null.
	 */

	public Value findGlobal(LexNameToken name)
	{
		return initialContext.check(name);
	}

	/**
	 * Set a statement tracepoint. A tracepoint does not stop execution, but evaluates and displays an expression before
	 * continuing.
	 * 
	 * @param stmt
	 *            The statement to trace.
	 * @param trace
	 *            The expression to evaluate.
	 * @return The Breakpoint object created.
	 * @throws Exception
	 *             Expression is not valid.
	 */

	public Breakpoint setTracepoint(PStm stmt, String trace) throws Exception
	{
		BreakpointManager.setBreakpoint(stmt, new Tracepoint(stmt.getLocation(), ++nextbreakpoint, trace));
		breakpoints.put(nextbreakpoint, BreakpointManager.getBreakpoint(stmt));
		return BreakpointManager.getBreakpoint(stmt);
	}

	/**
	 * Set an expression tracepoint. A tracepoint does not stop execution, but evaluates an expression before
	 * continuing.
	 * 
	 * @param exp
	 *            The expression to trace.
	 * @param trace
	 *            The expression to evaluate.
	 * @return The Breakpoint object created.
	 * @throws LexException
	 * @throws ParserException
	 */

	public Breakpoint setTracepoint(PExp exp, String trace)
			throws ParserException, LexException
	{
		BreakpointManager.setBreakpoint(exp, new Tracepoint(exp.getLocation(), ++nextbreakpoint, trace));
		breakpoints.put(nextbreakpoint, BreakpointManager.getBreakpoint(exp));
		return BreakpointManager.getBreakpoint(exp);
	}

	/**
	 * Set a statement breakpoint. A breakpoint stops execution and allows the user to query the environment.
	 * 
	 * @param stmt
	 *            The statement at which to stop.
	 * @param condition
	 *            The condition when to stop.
	 * @return The Breakpoint object created.
	 * @throws LexException
	 * @throws ParserException
	 */

	public Breakpoint setBreakpoint(PStm stmt, String condition)
			throws ParserException, LexException
	{
		BreakpointManager.setBreakpoint(stmt, new Stoppoint(stmt.getLocation(), ++nextbreakpoint, condition));
		breakpoints.put(nextbreakpoint, BreakpointManager.getBreakpoint(stmt));
		return BreakpointManager.getBreakpoint(stmt);
	}

	/**
	 * Set an expression breakpoint. A breakpoint stops execution and allows the user to query the environment.
	 * 
	 * @param exp
	 *            The expression at which to stop.
	 * @param condition
	 *            The condition when to stop.
	 * @return The Breakpoint object created.
	 * @throws LexException
	 * @throws ParserException
	 */

	public Breakpoint setBreakpoint(PExp exp, String condition)
			throws ParserException, LexException
	{
		BreakpointManager.setBreakpoint(exp, new Stoppoint(exp.getLocation(), ++nextbreakpoint, condition));
		breakpoints.put(nextbreakpoint, BreakpointManager.getBreakpoint(exp));
		return BreakpointManager.getBreakpoint(exp);
	}

	/**
	 * Clear the breakpoint given by the number.
	 * 
	 * @param bpno
	 *            The breakpoint number to remove.
	 * @return The breakpoint object removed, or null.
	 */

	public Breakpoint clearBreakpoint(int bpno)
	{
		Breakpoint old = breakpoints.remove(bpno);

		if (old != null)
		{
			PStm stmt = findStatement(old.location.getFile(), old.location.getStartLine());

			if (stmt != null)
			{
				BreakpointManager.setBreakpoint(stmt, new Breakpoint(stmt.getLocation()));
			} else
			{
				PExp exp = findExpression(old.location.getFile(), old.location.getStartLine());
				assert exp != null : "Cannot locate old breakpoint?";
				BreakpointManager.setBreakpoint(exp, new Breakpoint(exp.getLocation()));
			}
		}

		return old; // null if not found
	}

	public void clearBreakpointHits()
	{
		for (Entry<Integer, Breakpoint> e : breakpoints.entrySet())
		{
			e.getValue().clearHits();
		}
	}

	abstract protected PExp parseExpression(String line, String module)
			throws Exception;

	public PType typeCheck(PExp expr, Environment env) throws Exception
	{
		TypeChecker.clearErrors();

		try
		{
			PType type = expr.apply(new TypeCheckVisitor(), new TypeCheckInfo(assistantFactory, env, NameScope.NAMESANDSTATE));

			if (TypeChecker.getErrorCount() > 0)
			{
				throw new VDMErrorsException(TypeChecker.getErrors());
			}

			return type;
		} catch (Exception e)
		{
			throw e;
		} catch (Throwable e)
		{
			e.printStackTrace();
		}

		return null;

	}

	public PType typeCheck(PStm stmt, Environment env) throws Exception
	{
		TypeChecker.clearErrors();
		try
		{
			PType type = stmt.apply(new TypeCheckVisitor(), new TypeCheckInfo(assistantFactory, env, NameScope.NAMESANDSTATE));

			if (TypeChecker.getErrorCount() > 0)
			{
				throw new VDMErrorsException(TypeChecker.getErrors());
			}

			return type;
		} catch (Exception e)
		{
			throw e;
		} catch (Throwable e)
		{
			e.printStackTrace();
		}

		return null;
	}

	public PType typeCheck(String line) throws Exception
	{
		PExp expr = parseExpression(line, getDefaultName());
		return typeCheck(expr, getGlobalEnvironment());
	}

	/**
	 * @param classname
	 *            Unused.
	 * @return the class found or null
	 */
	public SClassDefinition findClass(String classname)
	{
		assert false : "findClass cannot be called for modules";
		return null;
	}

	public abstract PType findType(String typename);

	/**
	 * @param module
	 *            Unused.
	 * @return the module found or null
	 */
	public AModuleModules findModule(String module)
	{
		assert false : "findModule cannot be called for classes";
		return null;
	}

	private static PrintWriter writer = null;

	public static void setTraceOutput(PrintWriter pw)
	{
		writer = pw;
	}

	abstract protected ANamedTraceDefinition findTraceDefinition(
			LexNameToken name);

	public void runtrace(String name, int testNo, boolean debug)
			throws Exception
	{
		runtrace(name, testNo, debug, 1.0F, TraceReductionType.NONE, 1234);
	}

	public boolean runtrace(String name, int testNo, boolean debug,
			float subset, TraceReductionType type, long seed) throws Exception
	{
		LexTokenReader ltr = new LexTokenReader(name, Dialect.VDM_SL);
		LexToken token = ltr.nextToken();
		ltr.close();
		LexNameToken lexname = null;

		switch (token.type)
		{
			case NAME:
				lexname = (LexNameToken) token;

				if (Settings.dialect == Dialect.VDM_SL
						&& !lexname.module.equals(getDefaultName()))
				{
					setDefaultName(lexname.module);
				}
				break;

			case IDENTIFIER:
				lexname = new LexNameToken(getDefaultName(), (LexIdentifierToken) token);
				break;

			default:
				throw new Exception("Expecting trace name");
		}

		ANamedTraceDefinition tracedef = findTraceDefinition(lexname);

		if (tracedef == null)
		{
			throw new Exception("Trace " + lexname + " not found");
		}

		TestSequence tests = null;

		Context ctxt = null;

		ctxt = getInitialTraceContext(tracedef, debug);

		tests = ctxt.assistantFactory.createANamedTraceDefinitionAssistant().getTests(tracedef, ctxt, subset, type, seed);

		boolean wasDBGP = Settings.usingDBGP;
		boolean wasCMD = Settings.usingCmdLine;

		if (!debug)
		{
			Settings.usingCmdLine = false;
			Settings.usingDBGP = false;
		}

		if (writer == null)
		{
			writer = Console.out;
		}

		if (testNo > tests.size())
		{
			throw new Exception("Trace " + lexname + " only has "
					+ tests.size() + " tests");
		}

		int n = 1;
		boolean failed = false;
		writer.println("Generated " + tests.size() + " tests");

		for (CallSequence test : tests)
		{
			if (testNo > 0 && n != testNo)
			{
				n++;
				continue;
			}
			
			INode traceContainer;
			Environment rootEnv;
			if (this instanceof ClassInterpreter)
			{
				traceContainer = tracedef.getClassDefinition();
				rootEnv = new PrivateClassEnvironment(getAssistantFactory(), tracedef.getClassDefinition(), getGlobalEnvironment());
			} else
			{
				traceContainer = tracedef.parent();
				
				if(((AModuleModules)traceContainer).getIsFlat())
				{
					//search for the combined module
					for(AModuleModules m : ((ModuleInterpreter)this).modules)
					{
						if(m instanceof CombinedDefaultModule)
						{
							traceContainer = m;
							break;
						}
					}
				}
				rootEnv = new ModuleEnvironment(getAssistantFactory(), (AModuleModules) traceContainer);
			}

			List<Object> result = new Vector<>();
			try
			{
				typeCheck(traceContainer, this, test, rootEnv);
			}
			catch (Exception e)
			{
				result.add(e);
				result.add(Verdict.FAILED);
				failed = true;
			}
			
			// Bodge until we figure out how to not have explicit op names.
			String clean = test.toString().replaceAll("\\.\\w+`", ".");

			if (test.getFilter() > 0)
			{
				writer.println("Test " + n + " = " + clean);
				writer.println("Test " + n + " FILTERED by test "
						+ test.getFilter());
			} else
			{
				// Initialize completely between every run...
				init(ctxt.threadState.dbgp);
				result = runOneTrace(tracedef, test, debug);

				tests.filter(result, test, n);
				writer.println("Test " + n + " = " + clean);
				writer.println("Result = " + result);

				if (result.lastIndexOf(Verdict.PASSED) == -1)
				{
					failed = true; // Not passed => failed.
				}
			}

			n++;
		}

		init(null);
		Settings.usingCmdLine = wasCMD;
		Settings.usingDBGP = wasDBGP;

		return !failed;
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
	public static void typeCheck(INode classdef, Interpreter interpreter,
			CallSequence test, Environment outer) throws AnalysisException,
			Exception
	{
		FlatEnvironment env = null;

		if (classdef instanceof SClassDefinition)
		{
			env = new FlatEnvironment(interpreter.getAssistantFactory(), classdef.apply(interpreter.getAssistantFactory().getSelfDefinitionFinder()), outer);
		} else
		{
			List<PDefinition> defs = new Vector<>();
			
			if(classdef instanceof AModuleModules)
			{
				defs.addAll(((AModuleModules) classdef).getDefs());
			}
			
			env = new FlatEnvironment(interpreter.getAssistantFactory(), defs, outer);
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

	abstract public List<Object> runOneTrace(ANamedTraceDefinition tracedef,
			CallSequence test, boolean debug) throws AnalysisException;

	abstract public Context getInitialTraceContext(
			ANamedTraceDefinition tracedef, boolean debug)
			throws ValueException, AnalysisException;
}
