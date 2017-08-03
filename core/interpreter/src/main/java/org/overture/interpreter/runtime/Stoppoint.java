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

import org.overture.ast.intf.lex.ILexLocation;
import org.overture.interpreter.debug.BreakpointManager;
import org.overture.parser.lex.LexException;
import org.overture.parser.syntax.ParserException;

/**
 * A breakpoint where execution must stop.
 */

public class Stoppoint extends Breakpoint
{
	private static final long serialVersionUID = 1L;

	public Stoppoint(ILexLocation location, int number, String trace)
			throws ParserException, LexException
	{
		super(location, number, trace);
	}

	@Override
	public void check(ILexLocation execl, Context ctxt)
	{
		// skips if breakpoint is disabled
		if (!enabled)
		{
			return;
		}

		location.hit();
		hits++;

		try
		{
			boolean shouldBreak = parsed == null;

			if (!shouldBreak)
			{
				try
				{
					// Clear thread state while evaluating the expression and set
					// the state back after. Done to prevent the debugger from stopping
					// in the expression
					Context outctxt = ctxt.threadState.outctxt;
					RootContext rootContext = ctxt.threadState.nextctxt;
					ILexLocation stepline = ctxt.threadState.stepline;
					ctxt.threadState.init();
					ctxt.threadState.setAtomic(true);
					shouldBreak = BreakpointManager.shouldStop(parsed, ctxt);
					ctxt.threadState.setBreaks(stepline, rootContext, outctxt);
					
				} catch (Exception e)
				{
					String message = e.getMessage();
					
					if(message == null)
					{
						message = "breakpoint condition could not be evaluated";
					}
					
					println("Breakpoint [" + number + "]: " + message
							+ " \"" + trace + "\"");
				}
				finally
				{
					ctxt.threadState.setAtomic(false);
				}
			}

			if (shouldBreak)
			{
				enterDebugger(ctxt);
			}
		}
		catch (DebuggerException e)
		{
			throw e;
		}
	}

	@Override
	public String toString()
	{
		if (number == 0)
		{
			return super.toString();
		} else
		{
			return "break [" + number + "] "
					+ (trace == null ? "" : "when \"" + trace + "\" ")
					+ super.toString();
		}
	}
}
