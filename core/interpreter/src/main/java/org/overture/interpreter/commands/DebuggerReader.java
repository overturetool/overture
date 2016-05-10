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

import java.io.File;
import java.util.List;
import java.util.Vector;
import java.util.ArrayList;

import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.messages.InternalException;
import org.overture.config.Settings;
import org.overture.interpreter.messages.Console;
import org.overture.interpreter.runtime.Breakpoint;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.ContextException;
import org.overture.interpreter.runtime.DebuggerException;
import org.overture.interpreter.runtime.Interpreter;
import org.overture.interpreter.runtime.ModuleInterpreter;
import org.overture.interpreter.scheduler.BasicSchedulableThread;
import org.overture.interpreter.util.ExitStatus;
import org.overture.parser.syntax.ParserException;

/**
 * A class to read and perform breakpoint commands from standard input.
 */

public class DebuggerReader extends CommandReader
{
	/** The breakpoint which caused us to stop. */
	private final Breakpoint breakpoint;
	/** The context that was active when the breakpoint stopped. */
	private final Context ctxt;
	/** The before/after printing of the source command. */
	private static final int SOURCE_LINES = 5;

	/** The number of stack levels moved down. */
	private int frame = 0;

	public DebuggerReader(Interpreter interpreter, Breakpoint breakpoint,
			Context ctxt)
	{
		super(interpreter, "["
				+ BasicSchedulableThread.getThreadName(Thread.currentThread())
				+ "]> ");
		this.breakpoint = breakpoint;
		this.ctxt = ctxt;
	}

	/**
	 * This first prints out the current breakpoint source location before calling the superclass' run method. The
	 * synchronization prevents more than one debugging session on the console.
	 * 
	 * @return the exit status
	 */

	public ExitStatus run()
	{
		synchronized (DebuggerReader.class) // Only one console!
		{
			println("Stopped " + breakpoint);
			println(interpreter.getSourceLine(breakpoint.location));

			try
			{
				interpreter.setDefaultName(breakpoint.location.getModule());
			} catch (Exception e)
			{
				throw new InternalException(52, "Cannot set default name at breakpoint");
			}

			ExitStatus status = super.run(new ArrayList<File>());

			return status;
		}
	}

	@Override
	protected boolean doException(Exception e)
	{
		if (e instanceof DebuggerException)
		{
			throw (DebuggerException) e;
		} else
		{
			return super.doException(e);
		}
	}

	/**
	 * Evaluate an expression in the breakpoint's context. This is similar to the superclass method, except that the
	 * context is the one taken from the breakpoint, and the execution is not timed.
	 * 
	 * @see org.overture.interpreter.commands.CommandReader#doEvaluate(java.lang.String)
	 */

	@Override
	protected boolean doEvaluate(String line)
	{
		line = line.substring(line.indexOf(' ') + 1);

		try
		{
			println(line + " = " + interpreter.evaluate(line, getFrame()));
		} catch (ParserException e)
		{
			println("Syntax: " + e);
		} catch (ContextException e)
		{
			println("Runtime: " + e.getMessage());
		} catch (RuntimeException e)
		{
			println("Runtime: " + e.getMessage());
		} catch (Exception e)
		{
			println("Error: " + e.getMessage());
		}

		return true;
	}

	@Override
	protected boolean doStep(String line)
	{
		ctxt.threadState.setBreaks(breakpoint.location, null, null);
		return false;
	}

	@Override
	protected boolean doNext(String line)
	{
		ctxt.threadState.setBreaks(breakpoint.location, ctxt.getRoot(), null);
		return false;
	}

	@Override
	protected boolean doOut(String line)
	{
		ctxt.threadState.setBreaks(breakpoint.location, null, ctxt.getRoot().outer);
		return false;
	}

	@Override
	protected boolean doContinue(String line)
	{
		ctxt.threadState.setBreaks(null, null, null);
		return false;
	}

	private Context getFrame()
	{
		Context fp = ctxt;
		int c = frame;

		while (c > 0 && fp.outer != null)
		{
			fp = fp.outer;
			c--;
		}

		return fp;
	}

	@Override
	protected boolean doStack(String line)
	{
		println(breakpoint.stoppedAtString());
		getFrame().printStackTrace(Console.out, true);
		return true;
	}

	@Override
	protected boolean doUp(String line)
	{
		if (frame == 0)
		{
			println("Already at first frame");
		} else
		{
			frame--;
			Context fp = getFrame();
			println("In context of " + fp.title + " " + fp.location);
		}

		return true;
	}

	@Override
	protected boolean doDown(String line)
	{
		Context fp = getFrame();

		if (fp.outer == null)
		{
			println("Already at last frame");
		} else
		{
			frame++;
			fp = getFrame();
			println("In context of " + fp.title + " " + fp.location);
		}

		return true;
	}

	@Override
	protected boolean doSource(String line)
	{
		File file = breakpoint.location.getFile();
		int current = breakpoint.location.getStartLine();

		int start = current - SOURCE_LINES;
		if (start < 1)
		{
			start = 1;
		}
		int end = start + SOURCE_LINES * 2 + 1;

		for (int src = start; src < end; src++)
		{
			println(interpreter.getSourceLine(file, src, src == current ? ":>>"
					: ":  "));
		}

		return true;
	}

	@Override
	protected boolean doState(String line)
	{
		if (interpreter instanceof ModuleInterpreter)
		{
			ModuleInterpreter minterpreter = (ModuleInterpreter) interpreter;
			Context c = minterpreter.getStateContext();
			print(c == null ? "(no state)\n" : c.toString());
		} else
		{
			println("Not in a module context");
		}

		return true;
	}

	@Override
	protected boolean doStop(String line)
	{
		throw new DebuggerException("terminated");
	}

	@Override
	protected boolean doInit(String line)
	{
		return notAvailable(line);
	}

	@Override
	protected boolean doAssert(String line)
	{
		return notAvailable(line);
	}

	@Override
	protected boolean doReLoad(String line)
	{
		return notAvailable(line);
	}

	@Override
	protected boolean doLoad(String line, List<File> filenames)
	{
		return notAvailable(line);
	}

	@Override
	protected boolean doFiles()
	{
		return notAvailable("");
	}

	@Override
	protected boolean doSet(String line)
	{
		return notAvailable("");
	}

	@Override
	protected boolean doCoverage(String line)
	{
		return notAvailable(line);
	}

	@Override
	protected boolean doQuit(String line)
	{
		throw new DebuggerException("terminated");
	}

	@Override
	protected void doHelp(String line)
	{
		println("step - step one expression/statement");
		println("next - step over functions or operations");
		println("out - run to the return of functions or operations");
		println("continue - resume execution");
		println("stack - display the current stack frame context");
		println("up - move the stack frame context up one frame");
		println("down - move the stack frame context down one frame");
		println("source - list VDM source code around the current breakpoint");
		println("stop - terminate the execution immediately");
		println("threads - list active threads");
		super.doHelp(line);
	}

	@Override
	protected boolean notAvailable(String line)
	{
		println("Command not available in the debugger context");
		return true;
	}

	public static void stopped(Context ctxt, ILexLocation location)
	{
		if (Settings.usingCmdLine)
		{
			try
			{
				Breakpoint bp = new Breakpoint(location);
				new DebuggerReader(Interpreter.getInstance(), bp, ctxt).run();
			} catch (Exception e)
			{
				Console.out.println("Exception: " + e.getMessage());
			}
		}
	}
}
