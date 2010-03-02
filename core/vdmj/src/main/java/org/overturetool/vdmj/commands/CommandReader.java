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

package org.overturetool.vdmj.commands;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.overturetool.vdmj.ExitStatus;
import org.overturetool.vdmj.VDMJ;
import org.overturetool.vdmj.expressions.Expression;
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.lex.LexIdentifierToken;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.lex.LexToken;
import org.overturetool.vdmj.lex.LexTokenReader;
import org.overturetool.vdmj.lex.Token;
import org.overturetool.vdmj.messages.Console;
import org.overturetool.vdmj.messages.RTLogger;
import org.overturetool.vdmj.messages.VDMErrorsException;
import org.overturetool.vdmj.pog.ProofObligation;
import org.overturetool.vdmj.pog.ProofObligationList;
import org.overturetool.vdmj.runtime.Breakpoint;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.ContextException;
import org.overturetool.vdmj.runtime.DebuggerException;
import org.overturetool.vdmj.runtime.Interpreter;
import org.overturetool.vdmj.runtime.RTException;
import org.overturetool.vdmj.runtime.SourceFile;
import org.overturetool.vdmj.runtime.StopException;
import org.overturetool.vdmj.runtime.VDMThreadSet;
import org.overturetool.vdmj.statements.Statement;
import org.overturetool.vdmj.syntax.ParserException;
import org.overturetool.vdmj.values.BooleanValue;
import org.overturetool.vdmj.values.FunctionValue;
import org.overturetool.vdmj.values.OperationValue;
import org.overturetool.vdmj.values.Value;


/**
 * A class to read and perform commands from standard input.
 */

abstract public class CommandReader
{
	/** The interpreter to use for the execution of commands. */
	protected final Interpreter interpreter;

	/** The prompt for the user. */
	protected final String prompt;

	/**
	 * Create a command reader with the given interpreter and prompt.
	 *
	 * @param interpreter The interpreter instance to use.
	 * @param prompt The user prompt.
	 */

	public CommandReader(Interpreter interpreter, String prompt)
	{
		this.interpreter = interpreter;
		this.prompt = prompt;
	}

	/**
	 * Read and execute commands from standard input. The prompt passed
	 * to the constructor is used to prompt the user, and the interpreter
	 * passed is used to execute commands. Each command is directed through
	 * the corresponding "do" method in this class, the defaults for which
	 * print that the command is not available from the current context.
	 * Subclasses of CommandReader implement the "do" methods that apply
	 * to them. The only methods implemented at this level are the ones
	 * which are globally applicable.
	 * <p>
	 * The "do" methods return a boolean which indicates whether the command
	 * reader should loop and read/dispatch more commands, or exit.
	 */

	public ExitStatus run(List<File> filenames)
	{
		String line = "";
		String lastline = "";
		boolean carryOn = true;
		long timestamp = System.currentTimeMillis();

		while (carryOn)
		{
			for (File file: filenames)
			{
				if (file.lastModified() > timestamp)
				{
					println("File " + file + " has changed");
				}
			}

			try
			{
				print(prompt);
				line = Console.in.readLine();

				if (line == null)
				{
					carryOn = doQuit(line);
					continue;
				}

				if (line.equals("."))
				{
					line = lastline;
					println(prompt + line);
				}

				if (line.equals("") || line.startsWith("--"))
				{
					continue;
				}

				lastline = line;

				if (line.equals("quit") || line.equals("q"))
				{
					carryOn = doQuit(line);
				}
				else if (line.startsWith("reload"))
				{
					carryOn = doReLoad(line);

					if (!carryOn)
					{
						return ExitStatus.RELOAD;
					}
				}
				else if (line.startsWith("load"))
				{
					carryOn = doLoad(line, filenames);

					if (!carryOn)
					{
						return ExitStatus.RELOAD;
					}
				}
				else if (line.equals("files"))
				{
					carryOn = doFiles();
				}
				else if (line.equals("stop"))
				{
					carryOn = doStop(line);
				}
				else if (line.equals("help") || line.equals("?"))
				{
					doHelp(line);
				}
				else if (line.startsWith("assert"))
				{
					carryOn = doAssert(line);
				}
				else if(line.equals("continue") || line.equals("c"))
				{
					carryOn = doContinue(line);
				}
				else if(line.equals("stack"))
				{
					carryOn = doStack(line);
				}
				else if(line.equals("up"))
				{
					carryOn = doUp(line);
				}
				else if(line.equals("down"))
				{
					carryOn = doDown(line);
				}
				else if(line.equals("step") || line.equals("s"))
				{
					carryOn = doStep(line);
				}
				else if(line.equals("next") || line.equals("n"))
				{
					carryOn = doNext(line);
				}
				else if(line.equals("out") || line.equals("o"))
				{
					carryOn = doOut(line);
				}
				else if(line.startsWith("trace"))
				{
					carryOn = doTrace(line);
				}
				else if(line.startsWith("break"))
				{
					carryOn = doBreak(line);
				}
				else if(line.equals("list"))
				{
					carryOn = doList(line);
				}
				else if(line.equals("threads"))
				{
					carryOn = doThreads(line);
				}
				else if(line.equals("source"))
				{
					carryOn = doSource(line);
				}
				else if(line.startsWith("coverage"))
				{
					carryOn = doCoverage(line);
				}
				else if(line.startsWith("latexdoc"))
				{
					carryOn = doLatex(line, true);
				}
				else if(line.startsWith("latex"))
				{
					carryOn = doLatex(line, false);
				}
				else if(line.startsWith("remove"))
				{
					carryOn = doRemove(line);
				}
				else if(line.equals("init"))
				{
					carryOn = doInit(line);
				}
				else if(line.equals("env"))
				{
					carryOn = doEnv(line);
				}
				else if(line.equals("state"))
				{
					carryOn = doState(line);
				}
				else if(line.startsWith("default"))
				{
					carryOn = doDefault(line);
				}
				else if(line.equals("classes"))
				{
					carryOn = doClasses(line);
				}
				else if(line.startsWith("create"))
				{
					carryOn = doCreate(line);
				}
				else if(line.equals("modules"))
				{
					carryOn = doModules(line);
				}
				else if(line.startsWith("pog"))
				{
					carryOn = doPog(line);
				}
				else if(line.startsWith("log"))
				{
					carryOn = doLog(line);
				}
				else if (line.startsWith("print") || line.startsWith("p "))
				{
					carryOn = doEvaluate(line);
				}
				else
				{
					println("Bad command. Try 'help'");
				}
			}
			catch (Exception e)
			{
				carryOn = doException(e);
			}
		}

		return ExitStatus.EXIT_OK;
	}

	protected boolean doException(Exception e)
	{
		println("Exception: " + e.getMessage());
		return true;
	}

	protected boolean doEvaluate(String line)
	{
		line = line.substring(line.indexOf(' ') + 1);

		try
		{
   			long before = System.currentTimeMillis();
   			println("= " + interpreter.execute(line, null));
   			long after = System.currentTimeMillis();
			println("Executed in " + (double)(after-before)/1000 + " secs. ");

			if (RTLogger.getLogSize() > 0)
			{
				println("Dumped RT events");
				RTLogger.dump(false);
			}
		}
		catch (ParserException e)
		{
			println("Syntax: " + e.getMessage());
		}
		catch (ContextException e)
		{
			println("Runtime: " + e.getMessage());
			Breakpoint bp = new Breakpoint(e.location);
			new DebuggerReader(interpreter, bp, e.ctxt).run();
		}
		catch (StopException e)
		{
			println("Stopped: " + Interpreter.stoppedException.getMessage());
			Breakpoint bp = new Breakpoint(Interpreter.stoppedLocation);
			Context cx = Interpreter.stoppedContext;
			Interpreter.stopped();
			new DebuggerReader(interpreter, bp, cx).run();
		}
		catch (RTException e)
		{
			while (VDMThreadSet.isDebugStopped())
			{
				try
				{
					Thread.sleep(1000);
				}
				catch (InterruptedException x)
				{
					// ZZZzzz...
				}
			}
		}
		catch (DebuggerException e)
		{
			println("Debug: " + e.getMessage());
		}
		catch (RuntimeException e)
		{
			println("Runtime: " + e);
		}
		catch (VDMErrorsException e)
		{
			println(e.toString());
		}
		catch (Exception e)
		{
			println("Error: " + e.getMessage());
		}

		return true;
	}

	protected boolean doQuit(@SuppressWarnings("unused") String line)
	{
		if (RTLogger.getLogSize() > 0)
		{
			println("Dumping RT events");
			RTLogger.dump(true);
		}

		return false;
	}

	protected boolean doStop(String line)
	{
		return notAvailable(line);
	}

	protected boolean doModules(String line)
	{
		return notAvailable(line);
	}

	protected boolean doClasses(String line)
	{
		return notAvailable(line);
	}

	protected boolean doFiles()
	{
		Set<File> filenames = interpreter.getSourceFiles();

		for (File file: filenames)
		{
			println(file.getPath());
		}

		return true;
	}

	protected boolean doPog(String line)
	{
		ProofObligationList all = interpreter.getProofObligations();
		ProofObligationList list = null;

		if (line.equals("pog"))
		{
			list = all;
		}
		else
		{
    		Pattern p1 = Pattern.compile("^pog (\\w+)$");
    		Matcher m = p1.matcher(line);

    		if (m.matches())
    		{
    			list = new ProofObligationList();
    			String name = m.group(1) + "(";

    			for (ProofObligation po: all)
    			{
    				if (po.name.indexOf(name) >= 0)
    				{
    					list.add(po);
    				}
    			}
    		}
    		else
    		{
    			println("Usage: pog [<fn/op name>]");
    			return true;
    		}
		}

		if (list.isEmpty())
		{
			println("No proof obligations generated");
		}
		else
		{
			println("Generated " +
				plural(list.size(), "proof obligation", "s") + ":\n");
			print(list.toString());
		}

		return true;
	}

	protected boolean doLog(String line)
	{
		return notAvailable(line);
	}

	@SuppressWarnings("unused")
	protected boolean doCreate(String line) throws Exception
	{
		return notAvailable(line);
	}

	@SuppressWarnings("unused")
	protected boolean doDefault(String line) throws Exception
	{
		return notAvailable(line);
	}

	protected boolean doInit(@SuppressWarnings("unused") String line)
	{
		LexLocation.clearLocations();
		println("Cleared all coverage information");
		interpreter.init(null);

		if (!VDMThreadSet.abortAll(3))
		{
			println("Warning: threads remain blocked");
		}

		println("Global context initialized");
		return true;
	}

	protected boolean doEnv(@SuppressWarnings("unused") String line)
	{
		print(interpreter.getInitialContext());
		return true;
	}

	protected boolean doState(String line)
	{
		return notAvailable(line);
	}

	protected boolean doRemove(String line)
	{
		String parts[] = line.split("\\s+");

		if (parts.length != 2)
		{
			println("Usage: remove <breakpoint#>");
			return true;
		}

		int bpno = Integer.parseInt(parts[1]);
		Breakpoint old = interpreter.clearBreakpoint(bpno);

		if (old != null)
		{
			println("Cleared " + old);
			println(interpreter.getSourceLine(old.location));
		}
		else
		{
			println("Breakpoint [" + bpno + "] not set");
		}

		return true;
	}

	protected boolean doList(@SuppressWarnings("unused") String line)
	{
		Map<Integer, Breakpoint> map = interpreter.getBreakpoints();

		for (Integer key: map.keySet())
		{
			Breakpoint bp = map.get(key);
			println(bp.toString());
			println(interpreter.getSourceLine(bp.location));
		}

		return true;
	}

	protected boolean doSource(String line)
	{
		return notAvailable(line);
	}

	protected boolean doCoverage(String line)
	{
		try
		{
			Set<File> loaded = interpreter.getSourceFiles();

			if (line.equals("coverage"))
			{
				for (File file: interpreter.getSourceFiles())
				{
					doCoverage(file);
				}

				return true;
			}

			String[] parts = line.split("\\s+");

			if (parts.length == 2 && parts[1].equals("clear"))
			{
				LexLocation.clearLocations();
				println("Cleared all coverage information");
				return true;
			}

			if (parts.length == 3 && parts[1].equals("write"))
			{
				writeCoverage(new File(parts[2]));
				return true;
			}

			if (parts.length == 3 && parts[1].equals("merge"))
			{
				mergeCoverage(new File(parts[2]));
				return true;
			}

			for (int p = 1; p < parts.length; p++)
			{
				File f = new File(parts[p]);

    			if (loaded.contains(f))
    			{
    				doCoverage(f);
    			}
    			else
    			{
    				println(f + " is not loaded - try 'files'");
    			}
			}
		}
		catch (Exception e)
		{
			println("Usage: coverage clear|write <dir>|merge <dir>|<filenames>");
		}

		return true;
	}

	private boolean doCoverage(File file)
	{
		try
		{
			SourceFile source = interpreter.getSourceFile(file);

			if (source == null)
			{
				println(file + ": file not found");
			}
			else
			{
				source.printCoverage(Console.out);
			}
		}
		catch (Exception e)
		{
			println("coverage: " + e.getMessage());
		}

		return true;
	}

	protected boolean doLatex(String line, boolean headers)
	{
		try
		{
			Set<File> loaded = interpreter.getSourceFiles();

			if (line.equals("latex") || line.equals("latexdoc"))
			{
				for (File file: interpreter.getSourceFiles())
				{
					doLatex(file, headers);
				}

				return true;
			}

			String[] parts = line.split("\\s+");

			for (int p = 1; p < parts.length; p++)
			{
				File f = new File(parts[p]);

    			if (loaded.contains(f))
    			{
    				doLatex(f, headers);
    			}
    			else
    			{
    				println(f + " is not loaded - try 'files'");
    			}
			}
		}
		catch (Exception e)
		{
			println("Usage: latex|latexdoc <filenames>");
		}

		return true;
	}

	private boolean doLatex(File file, boolean headers)
	{
		try
		{
			SourceFile source = interpreter.getSourceFile(file);

			if (source == null)
			{
				println(file + ": file not found");
			}
			else
			{
				File tex = new File(source.filename.getPath() + ".tex");
				PrintWriter pw = new PrintWriter(tex);
				source.printLatexCoverage(pw, headers);
				pw.close();
				println("Latex coverage written to " + tex);
			}
		}
		catch (Exception e)
		{
			println("latex: " + e.getMessage());
		}

		return true;
	}

	protected boolean doBreak(String line) throws Exception
	{
		Pattern p1 = Pattern.compile("^break ([\\w._/\\\\]++)?:?(\\d+) ?(.+)?$");
		Matcher m = p1.matcher(line);

		if (m.matches())
		{
			String g1 = m.group(1);
			File file = g1 == null ? null : new File(g1);
			setBreakpoint(file, Integer.parseInt(m.group(2)), m.group(3));
		}
		else
		{
			Pattern p2 = Pattern.compile("^break ([\\w`$%']+) ?(.+)?$");
			m = p2.matcher(line);

			if (m.matches())
			{
				setBreakpoint(m.group(1), m.group(2));
			}
			else
			{
	    		println("Usage: break [<file>:]<lineno> [<condition>]");
	    		println("   or: break <function/operation> [<condition>]");
			}
		}

		return true;
	}

	protected boolean doTrace(String line) throws Exception
	{
		Pattern p1 = Pattern.compile("^trace ([\\w._/\\\\]++)?:?(\\d+) ?(.+)?$");
		Matcher m = p1.matcher(line);

		if (m.matches())
		{
			String g1 = m.group(1);
			File file = g1 == null ? null : new File(g1);
			setTracepoint(file, Integer.parseInt(m.group(2)), m.group(3));
		}
		else
		{
			Pattern p2 = Pattern.compile("^trace ([\\w`$%']+) ?(.+)?$");
			m = p2.matcher(line);

			if (m.matches())
			{
				setTracepoint(m.group(1), m.group(2));
			}
			else
			{
	    		println("Usage: trace [<file>:]<lineno> [<expression>]");
	    		println("   or: trace <function/operation> [<expression>]");
			}
		}

		return true;
	}

	protected boolean doStep(String line)
	{
		return notAvailable(line);
	}

	protected boolean doNext(String line)
	{
		return notAvailable(line);
	}

	protected boolean doOut(String line)
	{
		return notAvailable(line);
	}

	protected boolean doStack(String line)
	{
		return notAvailable(line);
	}

	protected boolean doUp(String line)
	{
		return notAvailable(line);
	}

	protected boolean doDown(String line)
	{
		return notAvailable(line);
	}

	protected boolean doContinue(String line)
	{
		return notAvailable(line);
	}

	protected boolean doThreads(String line)
	{
		return notAvailable(line);
	}

	protected boolean doAssert(String line)
	{
		File filename = null;

		try
		{
			String[] parts = line.split("\\s+");
			filename = new File(parts[1]);

			if (!filename.canRead())
			{
				println("File '" + filename + "' not accessible");
				return true;
			}
		}
		catch (Exception e)
		{
			println("Usage: assert <filename>");
			return true;
		}

		assertFile(filename);
		return true;
	}

	protected boolean doReLoad(String line)
	{
		if (!line.equals("reload"))
		{
			println("Usage: reload");
			return true;
		}

		return false;
	}

	protected boolean doLoad(String line, List<File> filenames)
	{
		if (line.indexOf(' ') < 0)
		{
			println("Usage: load <files>");
			return true;
		}

		filenames.clear();

		for (String s: line.split("\\s+"))
		{
			filenames.add(new File(s));
		}

		filenames.remove(0);	// which is "load" :-)
		return false;
	}

	public boolean assertFile(File filename)
	{
		BufferedReader input = null;

		try
		{
			input = new BufferedReader(
				new InputStreamReader(
					new FileInputStream(filename), VDMJ.filecharset));
		}
		catch (FileNotFoundException e)
		{
			println("File '" + filename + "' not found");
			return false;
		}
		catch (UnsupportedEncodingException e)
		{
			println("File encoding exception: " + e);
			return false;
		}

		int assertErrors = 0;
		int assertPasses = 0;

		while (true)
		{
			String assertion = null;

			try
			{
				assertion = input.readLine();

				if (assertion == null)
				{
					break;	 // EOF
				}

				if (assertion.equals("") || assertion.startsWith("--"))
				{
					continue;
				}

	   			Value result = interpreter.execute(assertion, null);

	   			if (!(result instanceof BooleanValue) || !result.boolValue(null))
   				{
   					println("FAILED: " + assertion);
   					assertErrors++;
   				}
   				else
   				{
   					assertPasses++;
   				}
			}
			catch (ParserException e)
			{
				println("FAILED: " + assertion);
				println("Syntax: " + e);
				assertErrors++;
				break;
			}
			catch (ContextException e)
			{
				println("FAILED: " + assertion);
				println("Runtime: " + e.getMessage());
				e.ctxt.printStackTrace(Console.out, true);
				assertErrors++;
				break;
			}
			catch (RuntimeException e)
			{
				println("FAILED: " + assertion);
				println("Runtime: " + e.getMessage());
				assertErrors++;
				break;
			}
			catch (Exception e)
			{
				println("FAILED: " + assertion);
				println("Exception: " + e);
				assertErrors++;
				break;
			}
		}

		if (assertErrors == 0)
		{
			println("PASSED all " + assertPasses +
				" assertions from " + filename);
		}
		else
		{
			println("FAILED " + assertErrors +
				" and passed " + assertPasses + " assertions from " + filename);
		}

		try { input.close(); } catch (Exception e) {/* */}
		return assertErrors == 0;
	}

	protected void doHelp(@SuppressWarnings("unused") String line)
	{
		println("print <expression> - evaluate expression");
		println("assert <file> - run assertions from a file");
		println("init - re-initialize the global environment");
		println("env - list the global symbols in the default environment");
		println("pog [<function/operation>] - generate proof obligations");
		println("break [<file>:]<line#> [<condition>] - create a breakpoint");
		println("break <function/operation> [<condition>] - create a breakpoint");
		println("trace [<file>:]<line#> [<exp>] - create a tracepoint");
		println("trace <function/operation> [<exp>] - create a tracepoint");
		println("remove <breakpoint#> - remove a trace/breakpoint");
		println("list - list breakpoints");
		println("coverage clear|write <dir>|merge <dir>|<filenames> - handle line coverage");
		println("latex|latexdoc [<files>] - generate LaTeX line coverage files");
		println("files - list files in the current specification");
		println("reload - reload the current specification files");
		println("load <files> - replace current loaded specification files");
		println("quit - leave the interpreter");
	}

	/**
	 * Callable by "do" methods which want to make the command unavailable.
	 * The method just prints "Command not available in this context" and
	 * returns true.
	 */

	protected boolean notAvailable(@SuppressWarnings("unused") String line)
	{
		println("Command not available in this context");
		return true;
	}

	/**
	 * Set a breakpoint at the given file and line with a condition.
	 *
	 * @param file The file name
	 * @param line The line number
	 * @param condition Any condition for the breakpoint, or null
	 * @throws Exception Problems parsing condition.
	 */

	private void setBreakpoint(File file, int line, String condition)
		throws Exception
	{
		if (file == null)
		{
			file = interpreter.getDefaultFile();
		}

		Statement stmt = interpreter.findStatement(file, line);

		if (stmt == null)
		{
			Expression exp = interpreter.findExpression(file, line);

			if (exp == null)
			{
				println("No breakable expressions or statements at " + file + ":" + line);
			}
			else
			{
				interpreter.clearBreakpoint(exp.breakpoint.number);
				Breakpoint bp = interpreter.setBreakpoint(exp, condition);
				println("Created " + bp);
				println(interpreter.getSourceLine(bp.location));
			}
		}
		else
		{
			interpreter.clearBreakpoint(stmt.breakpoint.number);
			Breakpoint bp = interpreter.setBreakpoint(stmt, condition);
			println("Created " + bp);
			println(interpreter.getSourceLine(bp.location));
		}
	}

	/**
	 * Set a breakpoint at the given function or operation name with
	 * a condition.
	 *
	 * @param name The function or operation name.
	 * @param condition Any condition for the breakpoint, or null.
	 * @throws Exception Problems parsing condition.
	 */

	private void setBreakpoint(String name, String condition)
		throws Exception
	{
		LexTokenReader ltr = new LexTokenReader(name, Dialect.VDM_SL);
		LexToken token = ltr.nextToken();
		ltr.close();

		Value v = null;

		if (token.is(Token.IDENTIFIER))
		{
			LexIdentifierToken id = (LexIdentifierToken)token;
			LexNameToken lnt = new LexNameToken(interpreter.getDefaultName(), id);
			v = interpreter.findGlobal(lnt);
		}
		else if (token.is(Token.NAME))
		{
			v = interpreter.findGlobal((LexNameToken)token);
		}

		if (v instanceof FunctionValue)
		{
			FunctionValue fv = (FunctionValue)v;
			Expression exp = fv.body;
			interpreter.clearBreakpoint(exp.breakpoint.number);
			Breakpoint bp = interpreter.setBreakpoint(exp, condition);
			println("Created " + bp);
			println(interpreter.getSourceLine(bp.location));
		}
		else if (v instanceof OperationValue)
		{
			OperationValue ov = (OperationValue)v;
			Statement stmt = ov.body;
			interpreter.clearBreakpoint(stmt.breakpoint.number);
			Breakpoint bp = interpreter.setBreakpoint(stmt, condition);
			println("Created " + bp);
			println(interpreter.getSourceLine(bp.location));
		}
		else if (v == null)
		{
			println(name + " is not visible or not found");
		}
		else
		{
			println(name + " is not a function or operation");
		}
	}

	/**
	 * Set a tracepoint at the given file and line. Tracepoints without
	 * a condition just print "Reached [n]", where [n] is the breakpoint
	 * number.
	 *
	 * @param file The file name
	 * @param line The line number
	 * @param trace Any expression to evaluate at the tracepoint, or null
	 * @throws Exception Problems parsing condition.
	 */

	private void setTracepoint(File file, int line, String trace)
		throws Exception
	{
		if (file == null)
		{
			file = interpreter.getDefaultFile();
		}

		Statement stmt = interpreter.findStatement(file, line);

		if (stmt == null)
		{
			Expression exp = interpreter.findExpression(file, line);

			if (exp == null)
			{
				println("No breakable expressions or statements at " + file + ":" + line);
			}
			else
			{
				interpreter.clearBreakpoint(exp.breakpoint.number);
				Breakpoint bp = interpreter.setTracepoint(exp, trace);
				println("Created " + bp);
				println(interpreter.getSourceLine(bp.location));
			}
		}
		else
		{
			interpreter.clearBreakpoint(stmt.breakpoint.number);
			Breakpoint bp = interpreter.setTracepoint(stmt, trace);
			println("Created " + bp);
			println(interpreter.getSourceLine(bp.location));
		}
	}

	/**
	 * Set a tracepoint at the given function or operation name. Tracepoints
	 * without a condition just print "Reached [n]", where [n] is the
	 * breakpoint number.
	 *
	 * @param name The function or operation name.
	 * @param trace Any trace for the tracepoint
	 * @throws Exception Problems parsing condition.
	 */

	private void setTracepoint(String name, String trace)
		throws Exception
	{
		LexTokenReader ltr = new LexTokenReader(name, Dialect.VDM_SL);
		LexToken token = ltr.nextToken();
		ltr.close();

		Value v = null;

		if (token.is(Token.IDENTIFIER))
		{
			LexIdentifierToken id = (LexIdentifierToken)token;
			LexNameToken lnt = new LexNameToken(interpreter.getDefaultName(), id);
			v = interpreter.findGlobal(lnt);
		}
		else if (token.is(Token.NAME))
		{
			v = interpreter.findGlobal((LexNameToken)token);
		}

		if (v instanceof FunctionValue)
		{
			FunctionValue fv = (FunctionValue)v;
			Expression exp = fv.body;
			interpreter.clearBreakpoint(exp.breakpoint.number);
			Breakpoint bp = interpreter.setTracepoint(exp, trace);
			println("Created " + bp);
			println(interpreter.getSourceLine(bp.location));
		}
		else if (v instanceof OperationValue)
		{
			OperationValue ov = (OperationValue)v;
			Statement stmt = ov.body;
			interpreter.clearBreakpoint(stmt.breakpoint.number);
			Breakpoint bp = interpreter.setTracepoint(stmt, trace);
			println("Created " + bp);
			println(interpreter.getSourceLine(bp.location));
		}
		else
		{
			println(name + " is not a function or operation");
		}
	}

	protected static void print(String m)
	{
		Console.out.print(m);
		Console.out.flush();	// As it's not a complete line
	}

	protected static void println(String m)
	{
		Console.out.println(m);
	}

	protected String plural(int n, String s, String pl)
	{
		return n + " " + (n != 1 ? s + pl : s);
	}

	private void writeCoverage(File dir)
		throws IOException
    {
    	for (File f: interpreter.getSourceFiles())
    	{
    		SourceFile source = interpreter.getSourceFile(f);

    		File cov = new File(dir.getPath() + File.separator + f.getName() + ".cov");
    		PrintWriter pw = new PrintWriter(cov);
    		source.writeCoverage(pw);
    		pw.close();
    		println("Written coverage for " + f);
    	}
    }

	private void mergeCoverage(File dir)
		throws IOException
    {
    	for (File f: interpreter.getSourceFiles())
    	{
    		File cov = new File(dir.getPath() + File.separator + f.getName() + ".cov");
    		LexLocation.mergeHits(f, cov);
    		println("Merged coverage for " + f);
    	}
    }
}
