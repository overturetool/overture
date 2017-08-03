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

import java.io.Serializable;

import org.overture.ast.expressions.PExp;
import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.lex.Dialect;
import org.overture.ast.lex.LexIntegerToken;
import org.overture.ast.lex.LexToken;
import org.overture.ast.lex.VDMToken;
import org.overture.config.Settings;
import org.overture.interpreter.ast.expressions.BreakpointExpression;
import org.overture.interpreter.commands.DebuggerReader;
import org.overture.interpreter.messages.Console;
import org.overture.interpreter.scheduler.BasicSchedulableThread;
import org.overture.interpreter.scheduler.ISchedulableThread;
import org.overture.parser.lex.LexException;
import org.overture.parser.lex.LexTokenReader;
import org.overture.parser.syntax.ExpressionReader;
import org.overture.parser.syntax.ParserException;

/**
 * The root of the breakpoint class hierarchy.
 */

public class Breakpoint implements Serializable
{
	private static final long serialVersionUID = 1L;

	/** The location of the breakpoint. */
	public final ILexLocation location;
	/** The number of the breakpoint. */
	public final int number;
	/** The condition or trace expression, in parsed form. */
	public final PExp parsed;
	/** The condition or trace expression, in raw form. */
	public final String trace;

	/** The number of times this breakpoint has been reached. */
	public long hits = 0;

	/** The condition saying if a the breakpoint is enabled */
	protected boolean enabled = true;

	public Breakpoint(ILexLocation location)
	{
		this.location = location;
		this.number = 0;
		this.trace = null;
		this.parsed = null;
	}

	/**
	 * Create a breakpoint at the given location. The trace string is parsed to an Expression structure for subsequent
	 * evaluation.
	 * 
	 * @param location
	 *            The location of the breakpoint.
	 * @param number
	 *            The number that appears in the "list" command.
	 * @param trace
	 *            Any condition or trace expression.
	 * @throws ParserException
	 * @throws LexException
	 */

	public Breakpoint(ILexLocation location, int number, String trace)
			throws ParserException, LexException
	{
		this.location = location;
		this.number = number;
		this.trace = trace;

		if (trace != null)
		{
			LexTokenReader ltr = new LexTokenReader(trace, Settings.dialect);

			ltr.push();
			LexToken tok = ltr.nextToken();

			switch (tok.type)
			{
				case EQUALS:
					parsed = readHitCondition(ltr, BreakpointCondition.EQ);
					break;

				case GT:
					parsed = readHitCondition(ltr, BreakpointCondition.GT);
					break;

				case GE:
					parsed = readHitCondition(ltr, BreakpointCondition.GE);
					break;

				case MOD:
					parsed = readHitCondition(ltr, BreakpointCondition.MOD);
					break;

				default:
					ltr.pop();
					ExpressionReader reader = new ExpressionReader(ltr);
					reader.setCurrentModule(location.getModule());
					parsed = reader.readExpression();
					break;
			}
		} else
		{
			parsed = null;
		}
	}

	private PExp readHitCondition(LexTokenReader ltr, BreakpointCondition cond)
			throws ParserException, LexException
	{
		LexToken arg = ltr.nextToken();

		if (arg.isNot(VDMToken.NUMBER))
		{
			throw new ParserException(2279, "Invalid breakpoint hit condition", location, 0);
		}

		LexIntegerToken num = (LexIntegerToken) arg;
		return new BreakpointExpression(this, cond, num.value);
	}

	@Override
	public String toString()
	{
		return location.toString();
	}

	public String stoppedAtString()
	{
		return "Stopped ["
				+ BasicSchedulableThread.getThreadName(Thread.currentThread())
				+ "] " + location;
	}

	public void clearHits()
	{
		hits = 0;
	}

	/**
	 * Check whether to stop. The implementation in Breakpoint is used to check for the "step" and "next" commands,
	 * using the stepline, nextctxt and outctxt fields. If the current line is different to the last step line, and the
	 * current context is not "above" the next context or the current context equals the out context or neither the next
	 * or out context are set, a {@link Stoppoint} is created and its check method is called - which starts a
	 * DebuggerReader session.
	 * 
	 * @param execl
	 *            The execution location.
	 * @param ctxt
	 *            The execution context.
	 */

	public void check(ILexLocation execl, Context ctxt)
	{
		// skips if breakpoint is disabled
		// if(!enabled){
		// return;
		// }

		location.hit();
		hits++;

		ThreadState state = ctxt.threadState;

		if (Settings.dialect != Dialect.VDM_SL)
		{
			state.reschedule(ctxt, execl);
		}

		if (state.stepline != null)
		{
			if (execl.getStartLine() != state.stepline.getStartLine()) // NB just line, not pos
			{
				if (state.nextctxt == null && state.outctxt == null
						|| state.nextctxt != null
						&& !isAboveNext(ctxt.getRoot())
						|| state.outctxt != null && isOutOrBelow(ctxt))
				{
					try
					{
						enterDebugger(ctxt);
					}
					catch (DebuggerException e)
					{
						throw e;
					}
				}
			}
		}
	}
	
	/**
	 * Actually stop and enter the debugger. The method returns when the user asks to
	 * continue or step the specification.
	 * 
	 * @param ctxt
	 */
	public void enterDebugger(Context ctxt)
	{
		ISchedulableThread th = BasicSchedulableThread.getThread(Thread.currentThread());

		if (th != null)
		{
			th.suspendOthers();
		}

		if (Settings.usingDBGP)
		{
			ctxt.threadState.dbgp.stopped(ctxt, this);
		}
		else
		{
			new DebuggerReader(Interpreter.getInstance(), this, ctxt).run();
		}
	}
	
	/**
	 * Test for whether an apply expression or operation call ought to catch a breakpoint
	 * after the return from the call. This only happens if we step into the call, so that
	 * when we step out it is clear where we're unwinding too, rather than jumping down
	 * the stack some considerable distance.
	 * 
	 * @param ctxt
	 * @return
	 */
	public boolean catchReturn(Context ctxt)
	{
		ThreadState state = ctxt.threadState;
		return state.stepline != null && state.nextctxt == null && state.outctxt == null;
	}


	/**
	 * True, if the context passed is above nextctxt. That means that the current context must have an "outer" chain
	 * that reaches nextctxt.
	 * 
	 * @param current
	 *            The context to test.
	 * @return True if the current context is above nextctxt.
	 */

	private boolean isAboveNext(Context current)
	{
		Context c = current.outer;

		while (c != null)
		{
			if (c == current.threadState.nextctxt)
			{
				return true;
			}
			c = c.outer;
		}

		return false;
	}

	/**
	 * True, if the context passed is equal to or below outctxt. That means that outctxt must have an "outer" chain that
	 * reaches current context.
	 * 
	 * @param current
	 *            The context to test.
	 * @return True if the current context is at or below outctxt.
	 */

	private boolean isOutOrBelow(Context current)
	{
		Context c = current.threadState.outctxt;

		while (c != null)
		{
			if (c == current)
			{
				return true;
			}
			c = c.outer;
		}

		return false;
	}

	protected void print(String line)
	{
		Console.out.print(line);
		Console.out.flush();
	}

	protected void println(String line)
	{
		Console.out.println(line);
	}

	public void setEnabled(boolean bool)
	{
		this.enabled = bool;
	}

	public boolean isEnabled()
	{
		return this.enabled;
	}
}
