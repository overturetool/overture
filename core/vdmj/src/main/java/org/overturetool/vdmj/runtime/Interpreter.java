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

package org.overturetool.vdmj.runtime;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.debug.DBGPReader;
import org.overturetool.vdmj.definitions.ClassDefinition;
import org.overturetool.vdmj.definitions.NamedTraceDefinition;
import org.overturetool.vdmj.expressions.Expression;
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.lex.LexException;
import org.overturetool.vdmj.lex.LexIdentifierToken;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.lex.LexToken;
import org.overturetool.vdmj.lex.LexTokenReader;
import org.overturetool.vdmj.messages.Console;
import org.overturetool.vdmj.messages.VDMErrorsException;
import org.overturetool.vdmj.modules.Module;
import org.overturetool.vdmj.pog.ProofObligationList;
import org.overturetool.vdmj.scheduler.ResourceScheduler;
import org.overturetool.vdmj.statements.Statement;
import org.overturetool.vdmj.syntax.ParserException;
import org.overturetool.vdmj.traces.CallSequence;
import org.overturetool.vdmj.traces.TestSequence;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.typechecker.TypeChecker;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.values.Value;


/**
 * An abstract VDM interpreter.
 */

abstract public class Interpreter
{
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
	 */

	public Interpreter()
	{
		scheduler = new ResourceScheduler();
		breakpoints = new TreeMap<Integer, Breakpoint>();
		sourceFiles = new HashMap<File, SourceFile>();
		instance = this;
	}

	/**
	 * Get a string version of the environment.
	 */

	public String getInitialContext()
	{
		return initialContext.toString();
	}

	/**
	 * Get the global environment.
	 */

	abstract public Environment getGlobalEnvironment();

	/**
	 * @return The Interpreter instance.
	 */

	public static Interpreter getInstance()
	{
		return instance;	// NB. last one created
	}

	/**
	 * Get the name of the default module or class. Symbols in the default
	 * module or class do not have to have their names qualified when being
	 * referred to on the command line.
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
	 * @param name The default name.
	 * @throws Exception
	 */

	abstract public void setDefaultName(String name) throws Exception;

	/**
	 * Initialize the initial context. This means that all definition
	 * initializers are re-run to put the global environment back into its
	 * original state. This is run implicitly when the interpreter starts,
	 * but it can also be invoked explicitly via the "init" command.
	 *
	 * @throws Exception
	 */

	abstract public void init(DBGPReader dbgp);

	/**
	 * Initialize the context between trace sequences. This is less
	 * thorough than the full init, since it does not reset the scheduler
	 * for example.
	 */

	abstract public void traceInit(DBGPReader dbgp);

	/**
	 * Parse the line passed, type check it and evaluate it as an expression
	 * in the initial context.
	 *
	 * @param line A VDM expression.
	 * @param dbgp The DBGPReader, if any
	 * @return The value of the expression.
	 * @throws Exception Parser, type checking or runtime errors.
	 */

	abstract public Value execute(String line, DBGPReader dbgp) throws Exception;

	/**
	 * Parse the content of the file passed, type check it and evaluate it as an
	 * expression in the initial context.
	 *
	 * @param file A file containing a VDM expression.
	 * @return The value of the expression.
	 * @throws Exception Parser, type checking or runtime errors.
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

		return execute(sb.toString(), null);
	}

	/**
	 * Parse the line passed, and evaluate it as an expression in the context
	 * passed. Note that this does not type check the expression.
	 *
	 * @param line A VDM expression.
	 * @param ctxt The context in which to evaluate the expression.
	 * @return The value of the expression.
	 * @throws Exception Parser or runtime errors.
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
	 */

	public String getSourceLine(LexLocation src)
	{
		return getSourceLine(src.file, src.startLine);
	}

	/**
	 * Get a line of a source file by its location.
	 */

	public String getSourceLine(File file, int line)
	{
		return getSourceLine(file, line, ":  ");
	}

	/**
	 * Get a line of a source file by its location.
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
			}
			catch (IOException e)
			{
				return "Cannot open source file: " + file;
			}
		}

		return line + sep + source.getLine(line);
	}

	/**
	 * Get an entire source file object.
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
	 */

	abstract public Set<File> getSourceFiles();


	/**
	 * Get a list of proof obligations for the loaded specification.
	 *
	 * @return A list of POs.
	 */

	abstract public ProofObligationList getProofObligations();

	/**
	 * Find a statement by file name and line number.
	 *
	 * @param file The name of the class/module
	 * @param lineno The line number
	 * @return A Statement object if found, else null.
	 */

	abstract public Statement findStatement(File file, int lineno);

	/**
	 * Find an expression by file name and line number.
	 *
	 * @param file The name of the file
	 * @param lineno The line number
	 * @return An Expression object if found, else null.
	 */

	abstract public Expression findExpression(File file, int lineno);

	/**
	 * Find a global environment value by name.
	 *
	 * @param name The name of the variable
	 * @return A Value object if found, else null.
	 */

	public Value findGlobal(LexNameToken name)
	{
		return initialContext.check(name);
	}

	/**
	 * Set a statement tracepoint. A tracepoint does not stop execution, but
	 * evaluates and displays an expression before continuing.
	 *
	 * @param stmt The statement to trace.
	 * @param trace The expression to evaluate.
	 * @return The Breakpoint object created.
	 *
	 * @throws Exception Expression is not valid.
	 */

	public Breakpoint setTracepoint(Statement stmt, String trace) throws Exception
	{
		stmt.breakpoint = new Tracepoint(stmt.location, ++nextbreakpoint, trace);
		breakpoints.put(nextbreakpoint, stmt.breakpoint);
		return stmt.breakpoint;
	}

	/**
	 * Set an expression tracepoint. A tracepoint does not stop execution, but
	 * evaluates an expression before continuing.
	 *
	 * @param exp The expression to trace.
	 * @param trace The expression to evaluate.
	 * @return The Breakpoint object created.
	 *
	 * @throws LexException
	 * @throws ParserException
	 */

	public Breakpoint setTracepoint(Expression exp, String trace)
		throws ParserException, LexException
	{
		exp.breakpoint = new Tracepoint(exp.location, ++nextbreakpoint, trace);
		breakpoints.put(nextbreakpoint, exp.breakpoint);
		return exp.breakpoint;
	}

	/**
	 * Set a statement breakpoint. A breakpoint stops execution and allows
	 * the user to query the environment.
	 *
	 * @param stmt The statement at which to stop.
	 * @param condition The condition when to stop.
	 * @return The Breakpoint object created.
	 *
	 * @throws LexException
	 * @throws ParserException
	 */

	public Breakpoint setBreakpoint(Statement stmt, String condition)
		throws ParserException, LexException
	{
		stmt.breakpoint = new Stoppoint(stmt.location, ++nextbreakpoint, condition);
		breakpoints.put(nextbreakpoint, stmt.breakpoint);
		return stmt.breakpoint;
	}

	/**
	 * Set an expression breakpoint. A breakpoint stops execution and allows
	 * the user to query the environment.
	 *
	 * @param exp The expression at which to stop.
	 * @param condition The condition when to stop.
	 * @return The Breakpoint object created.
	 * @throws LexException
	 * @throws ParserException
	 *
	 */

	public Breakpoint setBreakpoint(Expression exp, String condition)
		throws ParserException, LexException
	{
		exp.breakpoint = new Stoppoint(exp.location, ++nextbreakpoint, condition);
		breakpoints.put(nextbreakpoint, exp.breakpoint);
		return exp.breakpoint;
	}

	/**
	 * Clear the breakpoint given by the number.
	 *
	 * @param bpno The breakpoint number to remove.
	 * @return The breakpoint object removed, or null.
	 */

	public Breakpoint clearBreakpoint(int bpno)
	{
		Breakpoint old = breakpoints.remove(bpno);

		if (old != null)
		{
			Statement stmt = findStatement(old.location.file, old.location.startLine);

			if (stmt != null)
			{
				stmt.breakpoint = new Breakpoint(stmt.location);
			}
			else
			{
				Expression exp = findExpression(old.location.file, old.location.startLine);
				assert (exp != null) : "Cannot locate old breakpoint?";
				exp.breakpoint = new Breakpoint(exp.location);
			}
		}

		return old;		// null if not found
	}

	public void clearBreakpointHits()
	{
		for (Entry<Integer, Breakpoint> e: breakpoints.entrySet())
		{
			e.getValue().clearHits();
		}
	}

	abstract protected Expression parseExpression(String line, String module)
		throws Exception;

	public Type typeCheck(Expression expr, Environment env)
		throws Exception
	{
		TypeChecker.clearErrors();
		Type type = expr.typeCheck(env, null, NameScope.NAMESANDSTATE);

		if (TypeChecker.getErrorCount() > 0)
		{
			throw new VDMErrorsException(TypeChecker.getErrors());
		}

		return type;
	}

	public Type typeCheck(Statement stmt, Environment env)
		throws Exception
	{
		TypeChecker.clearErrors();
		Type type = stmt.typeCheck(env, NameScope.NAMESANDSTATE);

		if (TypeChecker.getErrorCount() > 0)
		{
			throw new VDMErrorsException(TypeChecker.getErrors());
		}

		return type;
	}

	public Type typeCheck(String line) throws Exception
	{
		Expression expr = parseExpression(line, getDefaultName());
		return typeCheck(expr, getGlobalEnvironment());
	}

	public ClassDefinition findClass(@SuppressWarnings("unused") String classname)
	{
		assert false : "findClass cannot be called for modules";
		return null;
	}

	public Module findModule(@SuppressWarnings("unused") String module)
	{
		assert false : "findModule cannot be called for classes";
		return null;
	}

	abstract public List<Object> runtrace(ClassDefinition def, CallSequence statements);


	private static PrintWriter writer = null;

	public static void setTraceOutput(PrintWriter pw)
	{
		writer = pw;
	}

	abstract protected NamedTraceDefinition findTraceDefinition(LexNameToken name);

	public void runtrace(String strname, int testNo, boolean debug)
		throws Exception
	{
		LexTokenReader ltr = new LexTokenReader(strname, Dialect.VDM_SL);
		LexToken token = ltr.nextToken();
		LexNameToken name = null;

		switch (token.type)
		{
			case NAME:
				name = (LexNameToken)token;
				break;

			case IDENTIFIER:
				name = new LexNameToken(getDefaultName(), (LexIdentifierToken)token);
				break;

			default:
				throw new Exception("Expecting trace name");
		}

		NamedTraceDefinition tracedef = findTraceDefinition(name);

		if (tracedef == null)
		{
			throw new Exception("Trace " + name + " not found");
		}

		TestSequence tests = tracedef.getTests(initialContext);

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
			throw new Exception("Trace " + name +
				" only has " + tests.size() + " tests");
		}

		int n = 1;

		for (CallSequence test: tests)
		{
			if (testNo > 0 && n != testNo)
			{
				n++;
				continue;
			}

			// Bodge until we figure out how to not have explicit op names.
			String clean = test.toString().replaceAll("\\.\\w+`", ".");

			if (test.getFilter() > 0)
			{
    			writer.println("Test " + n + " = " + clean);
				writer.println(
					"Test " + n + " FILTERED by test " + test.getFilter());
			}
			else
			{
				// Initialize completely between every run...
    			traceInit(null);
    			List<Object> result = runOneTrace(tracedef.classDefinition, test, debug);
    			tests.filter(result, test, n);

    			writer.println("Test " + n + " = " + clean);
    			writer.println("Result = " + result);
			}

			n++;
		}

		Settings.usingCmdLine = wasCMD;
		Settings.usingDBGP = wasDBGP;
	}

	abstract public List<Object> runOneTrace(
		ClassDefinition classDefinition, CallSequence test, boolean debug);
}
