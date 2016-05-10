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

package org.overture.interpreter.commands;

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
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.expressions.PExp;
import org.overture.ast.lex.Dialect;
import org.overture.ast.lex.LexIdentifierToken;
import org.overture.ast.lex.LexLocation;
import org.overture.ast.lex.LexNameToken;
import org.overture.ast.lex.LexToken;
import org.overture.ast.lex.VDMToken;
import org.overture.ast.statements.PStm;
import org.overture.config.Settings;
import org.overture.interpreter.VDMJ;
import org.overture.interpreter.debug.BreakpointManager;
import org.overture.interpreter.debug.DBGPReader;
import org.overture.interpreter.messages.Console;
import org.overture.interpreter.messages.rtlog.RTLogger;
import org.overture.interpreter.runtime.Breakpoint;
import org.overture.interpreter.runtime.ContextException;
import org.overture.interpreter.runtime.DebuggerException;
import org.overture.interpreter.runtime.Interpreter;
import org.overture.interpreter.runtime.LatexSourceFile;
import org.overture.interpreter.runtime.SourceFile;
import org.overture.interpreter.traces.TraceReductionType;
import org.overture.interpreter.util.ExitStatus;
import org.overture.interpreter.values.BooleanValue;
import org.overture.interpreter.values.FunctionValue;
import org.overture.interpreter.values.OperationValue;
import org.overture.interpreter.values.Value;
import org.overture.parser.lex.LexTokenReader;
import org.overture.parser.messages.VDMErrorsException;
import org.overture.parser.syntax.ParserException;
import org.overture.pog.obligation.ProofObligationList;
import org.overture.pog.pub.IProofObligation;
import org.overture.pog.pub.IProofObligationList;

/**
 * A class to read and perform commands from standard input.
 */

abstract public class CommandReader
{
	/** The interpreter to use for the execution of commands. */
	protected final Interpreter interpreter;

	/** The prompt for the user. */
	protected final String prompt;

	/** The degree of trace reduction. */
	private float reduction = 1.0F;

	/** The type of trace reduction. */
	private TraceReductionType reductionType = TraceReductionType.RANDOM;

	/** The IDE DBGPReader, if any */
	private DBGPReader dbgp = null;

	private final boolean singlePass;

	/**
	 * Create a command reader with the given interpreter and prompt.
	 * 
	 * @param interpreter
	 *            The interpreter instance to use.
	 * @param prompt
	 *            The user prompt.
	 */

	public CommandReader(Interpreter interpreter, String prompt)
	{
		this.interpreter = interpreter;
		this.prompt = prompt;
		this.singlePass = false;
	}

	/**
	 * Create a command reader with the given interpreter and prompt.
	 * 
	 * @param interpreter
	 *            The interpreter instance to use.
	 * @param prompt
	 *            The user prompt.
	 * @param singlePass
	 *            True if the reader should read more than one command
	 */

	public CommandReader(Interpreter interpreter, String prompt,
			boolean singlePass)
	{
		this.interpreter = interpreter;
		this.prompt = prompt;
		this.singlePass = singlePass;
	}

	/**
	 * Read and execute commands from standard input. The prompt passed to the constructor is used to prompt the user,
	 * and the interpreter passed is used to execute commands. Each command is directed through the corresponding "do"
	 * method in this class, the defaults for which print that the command is not available from the current context.
	 * Subclasses of CommandReader implement the "do" methods that apply to them. The only methods implemented at this
	 * level are the ones which are globally applicable.
	 * <p>
	 * The "do" methods return a boolean which indicates whether the command reader should loop and read/dispatch more
	 * commands, or exit.
	 * 
	 * @param filenames
	 *            the files
	 * @return
	 */

	public ExitStatus run(List<File> filenames)
	{
		String line;
		String lastline = "";
		boolean carryOn = true;
		long timestamp = System.currentTimeMillis();

		while (carryOn)
		{
			for (File file : filenames)
			{
				if (file.lastModified() > timestamp)
				{
					println("File " + file + " has changed");
				}
			}

			try
			{
				print(prompt);
				line = getStdin().readLine();

				if (line == null)
				{
					carryOn = doQuit("");
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
				} else if (line.startsWith("reload"))
				{
					carryOn = doReLoad(line);

					if (!carryOn)
					{
						return ExitStatus.RELOAD;
					}
				} else if (line.startsWith("load"))
				{
					carryOn = doLoad(line, filenames);

					if (!carryOn)
					{
						return ExitStatus.RELOAD;
					}
				} else if (line.startsWith("save"))
				{
					carryOn = doSave(line);
				} else if (line.equals("files"))
				{
					carryOn = doFiles();
				} else if (line.startsWith("set"))
				{
					carryOn = doSet(line);
				} else if (line.equals("stop"))
				{
					carryOn = doStop(line);
				} else if (line.equals("help") || line.equals("?"))
				{
					doHelp(line);
				} else if (line.startsWith("assert"))
				{
					carryOn = doAssert(line);
				} else if (line.equals("continue") || line.equals("c"))
				{
					carryOn = doContinue(line);
				} else if (line.equals("stack"))
				{
					carryOn = doStack(line);
				} else if (line.equals("up"))
				{
					carryOn = doUp(line);
				} else if (line.equals("down"))
				{
					carryOn = doDown(line);
				} else if (line.equals("step") || line.equals("s"))
				{
					carryOn = doStep(line);
				} else if (line.equals("next") || line.equals("n"))
				{
					carryOn = doNext(line);
				} else if (line.equals("out") || line.equals("o"))
				{
					carryOn = doOut(line);
				} else if (line.startsWith("trace"))
				{
					carryOn = doTrace(line);
				} else if (line.startsWith("break"))
				{
					carryOn = doBreak(line);
				} else if (line.equals("list"))
				{
					carryOn = doList(line);
				} else if (line.equals("threads"))
				{
					carryOn = doThreads(line);
				} else if (line.equals("source"))
				{
					carryOn = doSource(line);
				} else if (line.startsWith("coverage"))
				{
					carryOn = doCoverage(line);
				} else if (line.startsWith("latexdoc"))
				{
					carryOn = doLatex(line, true);
				} else if (line.startsWith("latex"))
				{
					carryOn = doLatex(line, false);
				} else if (line.startsWith("word"))
				{
					carryOn = doWord(line);
				} else if (line.startsWith("remove"))
				{
					carryOn = doRemove(line);
				} else if (line.equals("init"))
				{
					carryOn = doInit(line);
				} else if (line.equals("env"))
				{
					carryOn = doEnv(line);
				} else if (line.equals("state"))
				{
					carryOn = doState(line);
				} else if (line.startsWith("default"))
				{
					carryOn = doDefault(line);
				} else if (line.equals("classes"))
				{
					carryOn = doClasses(line);
				} else if (line.startsWith("create"))
				{
					carryOn = doCreate(line);
				} else if (line.equals("modules"))
				{
					carryOn = doModules(line);
				} else if (line.startsWith("pog"))
				{
					carryOn = doPog(line);
				} else if (line.startsWith("log"))
				{
					carryOn = doLog(line);
				} else if (line.startsWith("print ") || line.startsWith("p "))
				{
					carryOn = doEvaluate(line);
				} else if (line.startsWith("runtrace "))
				{
					carryOn = doRuntrace(line, false);
				} else if (line.startsWith("debugtrace "))
				{
					carryOn = doRuntrace(line, true);
				} else if (line.startsWith("filter"))
				{
					carryOn = doFilter(line);
				} else
				{
					println("Bad command. Try 'help'");
				}
			} catch (Exception e)
			{
				carryOn = doException(e);
			}

			if (singlePass)
			{
				break;
			}
		}

		return ExitStatus.EXIT_OK;
	}

	protected PrintWriter getStdout()
	{
		return Console.out;
	}

	protected BufferedReader getStdin()
	{
		return Console.in;
	}

	public void setDebugReader(DBGPReader dbgp)
	{
		this.dbgp = dbgp;
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
			println("= " + interpreter.execute(line, dbgp));
			long after = System.currentTimeMillis();
			println("Executed in " + (double) (after - before) / 1000
					+ " secs. ");

			if (RTLogger.getLogSize() > 0)
			{
				println("Dumped RT events");
				RTLogger.dump(false);
			}
		} catch (ParserException e)
		{
			println("Syntax: " + e.getMessage());
		} catch (DebuggerException e)
		{
			println("Debug: " + e.getMessage());
		} catch (RuntimeException e)
		{
			println("Runtime: " + e);
		} catch (VDMErrorsException e)
		{
			println(e.toString());
		} catch (Exception e)
		{
			println("Error: " + e.getMessage());
		}

		return true;
	}

	protected boolean doFilter(String line)
	{
		String[] parts = line.split("\\s+");

		if (parts.length != 2)
		{
			println("Usage: filter %age | RANDOM | SHAPES_NOVARS | SHAPES_VARNAMES | SHAPES_VARVALUES");
		} else
		{
			try
			{
				reduction = Float.parseFloat(parts[1]) / 100.0F;

				if (reduction > 1 || reduction < 0)
				{
					println("Usage: filter %age (1-100)");
				}
			} catch (NumberFormatException e)
			{
				try
				{
					reductionType = TraceReductionType.valueOf(parts[1].toUpperCase());
				} catch (Exception e1)
				{
					println("Usage: filter %age | RANDOM | SHAPES_NOVARS | SHAPES_VARNAMES | SHAPES_VARVALUES");
				}
			}
		}

		println("Trace filter currently " + reduction * 100 + "% "
				+ reductionType);
		return true;
	}

	protected boolean doRuntrace(String line, boolean debug)
	{
		String[] parts = line.split("\\s+");
		int testNo = 0;

		if (parts.length == 3)
		{
			try
			{
				testNo = Integer.parseInt(parts[2]);
			} catch (NumberFormatException e)
			{
				println(parts[0] + " <name> [test number]");
				return true;
			}
		}

		line = parts[1];

		try
		{
			long before = System.currentTimeMillis();
			boolean passed = interpreter.runtrace(line, testNo, debug, reduction, reductionType, 0);
			long after = System.currentTimeMillis();
			println("Executed in " + (double) (after - before) / 1000
					+ " secs. ");

			if (passed)
			{
				println("All tests passed");
			} else
			{
				println("Some tests failed or indeterminate");
			}

			if (RTLogger.getLogSize() > 0)
			{
				println("Dumped RT events");
				RTLogger.dump(false);
			}
		} catch (ParserException e)
		{
			println("Syntax: " + e.getMessage());
		} catch (DebuggerException e)
		{
			println("Debug: " + e.getMessage());
		} catch (RuntimeException e)
		{
			println("Runtime: " + e);
		} catch (VDMErrorsException e)
		{
			println(e.toString());
		} catch (Exception e)
		{
			println("Error: " + e.getMessage());
		}

		return true;
	}

	protected boolean doQuit(String line)
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

		for (File file : filenames)
		{
			println(file.getPath());
		}

		return true;
	}

	private void isEnabled(String name, boolean flag)
	{
		print(name);
		println(flag ? " are enabled" : " are disabled");
	}

	protected boolean doSet(String line)
	{
		if (line.equals("set"))
		{
			isEnabled("Preconditions", Settings.prechecks);
			isEnabled("Postconditions", Settings.postchecks);
			isEnabled("Invariants", Settings.invchecks);
			isEnabled("Dynamic type checks", Settings.dynamictypechecks);
			isEnabled("Measure checks", Settings.measureChecks);
		} else
		{
			String[] parts = line.split("\\s+");

			if (parts.length == 3
					&& (parts[2].equalsIgnoreCase("on") || parts[2].equalsIgnoreCase("off")))
			{
				boolean setting = parts[2].equalsIgnoreCase("on");

				if (parts[1].equals("pre"))
				{
					Settings.prechecks = setting;
				} else if (parts[1].equals("post"))
				{
					Settings.postchecks = setting;
				} else if (parts[1].equals("inv"))
				{
					Settings.invchecks = setting;
				} else if (parts[1].equals("dtc"))
				{
					// NB. Do both
					Settings.invchecks = setting;
					Settings.dynamictypechecks = setting;
				} else if (parts[1].equals("measures"))
				{
					Settings.measureChecks = setting;
				} else
				{
					println("Usage: set [<pre|post|inv|dtc|measures> <on|off>]");
				}
			} else
			{
				println("Usage: set [<pre|post|inv|dtc|measures> <on|off>]");
			}
		}

		return true;
	}

	protected boolean doPog(String line) throws AnalysisException
	{
		IProofObligationList all = interpreter.getProofObligations();
		IProofObligationList list;

		if (line.equals("pog"))
		{
			list = all;
		} else
		{
			Pattern p1 = Pattern.compile("^pog (\\w+)$");
			Matcher m = p1.matcher(line);

			if (m.matches())
			{
				list = new ProofObligationList();
				String name = m.group(1)
						+ (Settings.dialect == Dialect.VDM_SL ? "" : "(");

				for (IProofObligation po : all)
				{
					if (po.getName().indexOf(name) >= 0)
					{
						list.add(po);
					}
				}
			} else
			{
				println("Usage: pog [<fn/op name>]");
				return true;
			}
		}

		if (list.isEmpty())
		{
			println("No proof obligations generated");
		} else
		{
			println("Generated " + plural(list.size(), "proof obligation", "s")
					+ ":\n");
			print(list.toString());
		}

		return true;
	}

	protected boolean doLog(String line)
	{
		return notAvailable(line);
	}

	/**
	 * @throws Exception
	 */
	protected boolean doCreate(String line) throws Exception
	{
		return notAvailable(line);
	}

	/**
	 * @throws Exception
	 */
	protected boolean doDefault(String line) throws Exception
	{
		return notAvailable(line);
	}

	protected boolean doInit(String line)
	{
		LexLocation.clearLocations();
		println("Cleared all coverage information");
		interpreter.init(dbgp);
		println("Global context initialized");
		return true;
	}

	protected boolean doEnv(String line)
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
		} else
		{
			println("Breakpoint [" + bpno + "] not set");
		}

		return true;
	}

	protected boolean doList(String line)
	{
		Map<Integer, Breakpoint> map = interpreter.getBreakpoints();

		for (Entry<Integer, Breakpoint> entry : map.entrySet())
		{
			Breakpoint bp = entry.getValue();
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
				for (File file : interpreter.getSourceFiles())
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
				} else
				{
					println(f + " is not loaded - try 'files'");
				}
			}
		} catch (Exception e)
		{
			println("Usage: coverage clear|write <dir>|merge <dir>|<filenames>");
		}

		return true;
	}

	protected boolean doCoverage(File file)
	{
		try
		{
			SourceFile source = interpreter.getSourceFile(file);

			if (source == null)
			{
				println(file + ": file not found");
			} else
			{
				source.printCoverage(getStdout());
			}
		} catch (Exception e)
		{
			println("coverage: " + e.getMessage());
		}

		return true;
	}

	protected boolean doWord(String line)
	{
		try
		{
			Set<File> loaded = interpreter.getSourceFiles();

			if (line.equals("word"))
			{
				for (File file : interpreter.getSourceFiles())
				{
					doWord(file);
				}

				return true;
			}

			String[] parts = line.split("\\s+");

			for (int p = 1; p < parts.length; p++)
			{
				File f = new File(parts[p]);

				if (loaded.contains(f))
				{
					doWord(f);
				} else
				{
					println(f + " is not loaded - try 'files'");
				}
			}
		} catch (Exception e)
		{
			println("Usage: word [<filenames>]");
		}

		return true;
	}

	protected void doWord(File file)
	{
		try
		{
			SourceFile source = interpreter.getSourceFile(file);

			if (source == null)
			{
				println(file + ": file not found");
			} else
			{
				File html = new File(source.filename.getPath() + ".doc");
				PrintWriter pw = new PrintWriter(html, "UTF-8");
				source.printWordCoverage(pw);
				pw.close();
				println("Word HTML coverage written to " + html);
			}
		} catch (Exception e)
		{
			println("word: " + e.getMessage());
		}
	}

	protected boolean doSave(String line)
	{
		try
		{
			Set<File> loaded = interpreter.getSourceFiles();

			if (line.equals("save"))
			{
				for (File file : interpreter.getSourceFiles())
				{
					doSave(file);
				}

				return true;
			}

			String[] parts = line.split("\\s+");

			for (int p = 1; p < parts.length; p++)
			{
				File f = new File(parts[p]);

				if (loaded.contains(f))
				{
					doSave(f);
				} else
				{
					println(f + " is not loaded - try 'files'");
				}
			}
		} catch (Exception e)
		{
			println("Usage: save [<filenames>]");
		}

		return true;
	}

	protected void doSave(File file)
	{
		try
		{
			String name = file.getName().toLowerCase();

			if (name.endsWith(".doc") || name.endsWith(".docx")
					|| name.endsWith(".odt"))
			{
				SourceFile source = interpreter.getSourceFile(file);

				if (source == null)
				{
					println(file + ": file not found");
				} else
				{
					File vdm = new File(source.filename.getPath() + "."
							+ Settings.dialect.getArgstring().substring(1));
					PrintWriter spw = new PrintWriter(vdm, "UTF-8");
					source.printSource(spw);
					spw.close();
					println("Extracted source written to " + vdm);
				}
			} else
			{
				println("Not a Word or ODF file: " + file);
			}
		} catch (Exception e)
		{
			println("save: " + e.getMessage());
		}
	}

	protected boolean doLatex(String line, boolean headers)
	{
		try
		{
			Set<File> loaded = interpreter.getSourceFiles();

			if (line.equals("latex") || line.equals("latexdoc"))
			{
				for (File file : interpreter.getSourceFiles())
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
				} else
				{
					println(f + " is not loaded - try 'files'");
				}
			}
		} catch (Exception e)
		{
			println("Usage: latex|latexdoc <filenames>");
		}

		return true;
	}

	protected boolean doLatex(File file, boolean headers)
	{
		try
		{
			SourceFile source = interpreter.getSourceFile(file);

			if (source == null)
			{
				println(file + ": file not found");
			} else
			{
				File tex = new File(source.filename.getPath() + ".tex");
				PrintWriter pw = new PrintWriter(tex, "UTF-8");
				new LatexSourceFile(source).printCoverage(pw, headers);
				pw.close();
				println("Latex coverage written to " + tex);
			}
		} catch (Exception e)
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
		} else
		{
			Pattern p2 = Pattern.compile("^break ([\\w`$%']+) ?(.+)?$");
			m = p2.matcher(line);

			if (m.matches())
			{
				setBreakpoint(m.group(1), m.group(2));
			} else
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
		} else
		{
			Pattern p2 = Pattern.compile("^trace ([\\w`$%']+) ?(.+)?$");
			m = p2.matcher(line);

			if (m.matches())
			{
				setTracepoint(m.group(1), m.group(2));
			} else
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
		String threads = interpreter.scheduler.getStatus();

		if (threads.isEmpty())
		{
			println("No threads running");
		} else
		{
			println(threads);
		}

		return true;
	}

	protected boolean doAssert(String line)
	{
		File filename;

		try
		{
			String[] parts = line.split("\\s+");
			filename = new File(parts[1]);

			if (!filename.canRead())
			{
				println("File '" + filename + "' not accessible");
				return true;
			}
		} catch (Exception e)
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
			println("Usage: load <files or dirs>");
			return true;
		}

		filenames.clear();

		for (String s : line.split("\\s+"))
		{
			File dir = new File(s);

			if (dir.isDirectory())
			{
				for (File file : dir.listFiles(Settings.dialect.getFilter()))
				{
					if (file.isFile())
					{
						filenames.add(file);
					}
				}
			} else
			{
				filenames.add(dir);
			}
		}

		filenames.remove(0); // which is "load" :-)
		return false;
	}

	public boolean assertFile(File filename)
	{
		BufferedReader input;

		try
		{
			input = new BufferedReader(new InputStreamReader(new FileInputStream(filename), VDMJ.filecharset));
		} catch (FileNotFoundException e)
		{
			println("File '" + filename + "' not found");
			return false;
		} catch (UnsupportedEncodingException e)
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
					break; // EOF
				}

				if (assertion.equals("") || assertion.startsWith("--"))
				{
					continue;
				}

				Value result = interpreter.execute(assertion, dbgp);

				if (!(result instanceof BooleanValue)
						|| !result.boolValue(null))
				{
					println("FAILED: " + assertion);
					assertErrors++;
				} else
				{
					assertPasses++;
				}
			} catch (ParserException e)
			{
				println("FAILED: " + assertion);
				println("Syntax: " + e);
				assertErrors++;
				break;
			} catch (ContextException e)
			{
				println("FAILED: " + assertion);
				println("Runtime: " + e.getMessage());
				e.ctxt.printStackTrace(getStdout(), true);
				assertErrors++;
				break;
			} catch (RuntimeException e)
			{
				println("FAILED: " + assertion);
				println("Runtime: " + e.getMessage());
				assertErrors++;
				break;
			} catch (Exception e)
			{
				println("FAILED: " + assertion);
				println("Exception: " + e);
				assertErrors++;
				break;
			}
		}

		if (assertErrors == 0)
		{
			println("PASSED all " + assertPasses + " assertions from "
					+ filename);
		} else
		{
			println("FAILED " + assertErrors + " and passed " + assertPasses
					+ " assertions from " + filename);
		}

		try
		{
			input.close();
		} catch (Exception e)
		{/* */
		}
		return assertErrors == 0;
	}

	protected void doHelp(String line)
	{
		println("print <expression> - evaluate expression");
		println("runtrace <name> [test number] - run CT trace(s)");
		println("debugtrace <name> [test number] - debug CT trace(s)");
		println("filter %age | <reduction type> - reduce CT trace(s)");
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
		println("word [<files>] - generate Word HTML line coverage files");
		println("files - list files in the current specification");
		println("set [<pre|post|inv|dtc|measures> <on|off>] - set runtime checks");
		println("reload - reload the current specification files");
		println("load <files or dirs> - replace current loaded specification files");
		println("save [<files>] - generate Word/ODF source extract files");
		println("quit - leave the interpreter");
	}

	/**
	 * Callable by "do" methods which want to make the command unavailable. The method just prints
	 * "Command not available in this context" and returns true.
	 */

	protected boolean notAvailable(String line)
	{
		println("Command not available in this context");
		return true;
	}

	/**
	 * Set a breakpoint at the given file and line with a condition.
	 * 
	 * @param file
	 *            The file name
	 * @param line
	 *            The line number
	 * @param condition
	 *            Any condition for the breakpoint, or null
	 * @throws Exception
	 *             Problems parsing condition.
	 */

	private void setBreakpoint(File file, int line, String condition)
			throws Exception
	{
		if (file == null)
		{
			file = interpreter.getDefaultFile();
		}

		if (file == null || file.getPath().equals("?"))
		{
			Set<File> files = interpreter.getSourceFiles();

			if (files.size() > 1)
			{
				println("Assuming file " + file.getPath());
			} else if (files.isEmpty())
			{
				println("No files defined");
				return;
			}

			file = files.iterator().next();
		}

		PStm stmt = interpreter.findStatement(file, line);

		if (stmt == null)
		{
			PExp exp = interpreter.findExpression(file, line);

			if (exp == null)
			{
				println("No breakable expressions or statements at " + file
						+ ":" + line);
			} else
			{
				interpreter.clearBreakpoint(BreakpointManager.getBreakpoint(exp).number);
				Breakpoint bp = interpreter.setBreakpoint(exp, condition);
				println("Created " + bp);
				println(interpreter.getSourceLine(bp.location));
			}
		} else
		{
			interpreter.clearBreakpoint(BreakpointManager.getBreakpoint(stmt).number);
			Breakpoint bp = interpreter.setBreakpoint(stmt, condition);
			println("Created " + bp);
			println(interpreter.getSourceLine(bp.location));
		}
	}

	/**
	 * Set a breakpoint at the given function or operation name with a condition.
	 * 
	 * @param name
	 *            The function or operation name.
	 * @param condition
	 *            Any condition for the breakpoint, or null.
	 * @throws Exception
	 *             Problems parsing condition.
	 */

	private void setBreakpoint(String name, String condition) throws Exception
	{
		LexTokenReader ltr = new LexTokenReader(name, Dialect.VDM_SL);
		LexToken token = ltr.nextToken();
		ltr.close();

		Value v = null;

		if (token.is(VDMToken.IDENTIFIER))
		{
			LexIdentifierToken id = (LexIdentifierToken) token;
			LexNameToken lnt = new LexNameToken(interpreter.getDefaultName(), id);
			v = interpreter.findGlobal(lnt);
		} else if (token.is(VDMToken.NAME))
		{
			v = interpreter.findGlobal((LexNameToken) token);
		}

		if (v instanceof FunctionValue)
		{
			FunctionValue fv = (FunctionValue) v;
			PExp exp = fv.body;
			interpreter.clearBreakpoint(BreakpointManager.getBreakpoint(exp).number);
			Breakpoint bp = interpreter.setBreakpoint(exp, condition);
			println("Created " + bp);
			println(interpreter.getSourceLine(bp.location));
		} else if (v instanceof OperationValue)
		{
			OperationValue ov = (OperationValue) v;
			PStm stmt = ov.body;
			interpreter.clearBreakpoint(BreakpointManager.getBreakpoint(stmt).number);
			Breakpoint bp = interpreter.setBreakpoint(stmt, condition);
			println("Created " + bp);
			println(interpreter.getSourceLine(bp.location));
		} else if (v == null)
		{
			println(name + " is not visible or not found");
		} else
		{
			println(name + " is not a function or operation");
		}
	}

	/**
	 * Set a tracepoint at the given file and line. Tracepoints without a condition just print "Reached [n]", where [n]
	 * is the breakpoint number.
	 * 
	 * @param file
	 *            The file name
	 * @param line
	 *            The line number
	 * @param trace
	 *            Any expression to evaluate at the tracepoint, or null
	 * @throws Exception
	 *             Problems parsing condition.
	 */

	private void setTracepoint(File file, int line, String trace)
			throws Exception
	{
		if (file == null)
		{
			file = interpreter.getDefaultFile();
		}

		if (file == null || file.getPath().equals("?"))
		{
			Set<File> files = interpreter.getSourceFiles();

			if (files.size() > 1)
			{
				println("Assuming file " + file.getPath());
			} else if (files.isEmpty())
			{
				println("No files defined");
				return;
			}

			file = files.iterator().next();
		}

		PStm stmt = interpreter.findStatement(file, line);

		if (stmt == null)
		{
			PExp exp = interpreter.findExpression(file, line);

			if (exp == null)
			{
				println("No breakable expressions or statements at " + file
						+ ":" + line);
			} else
			{
				interpreter.clearBreakpoint(BreakpointManager.getBreakpoint(exp).number);
				Breakpoint bp = interpreter.setTracepoint(exp, trace);
				println("Created " + bp);
				println(interpreter.getSourceLine(bp.location));
			}
		} else
		{
			interpreter.clearBreakpoint(BreakpointManager.getBreakpoint(stmt).number);
			Breakpoint bp = interpreter.setTracepoint(stmt, trace);
			println("Created " + bp);
			println(interpreter.getSourceLine(bp.location));
		}
	}

	/**
	 * Set a tracepoint at the given function or operation name. Tracepoints without a condition just print
	 * "Reached [n]", where [n] is the breakpoint number.
	 * 
	 * @param name
	 *            The function or operation name.
	 * @param trace
	 *            Any trace for the tracepoint
	 * @throws Exception
	 *             Problems parsing condition.
	 */

	private void setTracepoint(String name, String trace) throws Exception
	{
		LexTokenReader ltr = new LexTokenReader(name, Dialect.VDM_SL);
		LexToken token = ltr.nextToken();
		ltr.close();

		Value v = null;

		if (token.is(VDMToken.IDENTIFIER))
		{
			LexIdentifierToken id = (LexIdentifierToken) token;
			LexNameToken lnt = new LexNameToken(interpreter.getDefaultName(), id);
			v = interpreter.findGlobal(lnt);
		} else if (token.is(VDMToken.NAME))
		{
			v = interpreter.findGlobal((LexNameToken) token);
		}

		if (v instanceof FunctionValue)
		{
			FunctionValue fv = (FunctionValue) v;
			PExp exp = fv.body;
			interpreter.clearBreakpoint(BreakpointManager.getBreakpoint(exp).number);
			Breakpoint bp = interpreter.setTracepoint(exp, trace);
			println("Created " + bp);
			println(interpreter.getSourceLine(bp.location));
		} else if (v instanceof OperationValue)
		{
			OperationValue ov = (OperationValue) v;
			PStm stmt = ov.body;
			interpreter.clearBreakpoint(BreakpointManager.getBreakpoint(stmt).number);
			Breakpoint bp = interpreter.setTracepoint(stmt, trace);
			println("Created " + bp);
			println(interpreter.getSourceLine(bp.location));
		} else
		{
			println(name + " is not a function or operation");
		}
	}

	protected void print(String m)
	{
		getStdout().print(m);
		getStdout().flush(); // As it's not a complete line
	}

	protected void println(String m)
	{
		getStdout().println(m);
	}

	protected String plural(int n, String s, String pl)
	{
		return n + " " + (n != 1 ? s + pl : s);
	}

	private void writeCoverage(File dir) throws IOException
	{
		for (File f : interpreter.getSourceFiles())
		{
			SourceFile source = interpreter.getSourceFile(f);

			File cov = new File(dir.getPath() + File.separator + f.getName()
					+ ".cov");
			PrintWriter pw = new PrintWriter(cov);
			source.writeCoverage(pw);
			pw.close();
			println("Written coverage for " + f);
		}
	}

	private void mergeCoverage(File dir) throws IOException
	{
		for (File f : interpreter.getSourceFiles())
		{
			File cov = new File(dir.getPath() + File.separator + f.getName()
					+ ".cov");
			LexLocation.mergeHits(f, cov);
			println("Merged coverage for " + f);
		}
	}
}
