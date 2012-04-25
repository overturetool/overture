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

import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.commands.DebuggerReader;
import org.overturetool.vdmj.lex.LexException;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.scheduler.BasicSchedulableThread;
import org.overturetool.vdmj.scheduler.ISchedulableThread;
import org.overturetool.vdmj.syntax.ParserException;

/**
 * A breakpoint where execution must stop.
 */

public class Stoppoint extends Breakpoint
{
	private static final long serialVersionUID = 1L;

	public Stoppoint(LexLocation location, int number, String trace)
		throws ParserException, LexException
	{
		super(location, number, trace);
	}

	@Override
	public void check(LexLocation execl, Context ctxt)
	{
		//skips if breakpoint is disabled
		if(!enabled){
			return;
		}
		
		location.hit();
		hits++;

		try
		{
			boolean shouldBreak = parsed == null;

			if (!shouldBreak)
			{
				//Clear thread state while evaluating the expression and set
				//the state back after. Done to prevent the debugger from stopping
				// in the expression
				Context outctxt = ctxt.threadState.outctxt;
				RootContext rootContext = ctxt.threadState.nextctxt;
				LexLocation stepline = ctxt.threadState.stepline;
				ctxt.threadState.init();
				shouldBreak = parsed.eval(ctxt).boolValue(ctxt);
				ctxt.threadState.setBreaks(stepline, rootContext, outctxt);
			}

			if (shouldBreak)
			{
				ISchedulableThread th = BasicSchedulableThread.getThread(Thread.currentThread());

				if (th !=null)
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
		}
		catch (DebuggerException e)
		{
			throw e;
		}
		catch (ValueException e)
		{
			println("Breakpoint [" + number + "]: " + e.getMessage() + " \"" + trace + "\"");
		}
	}

	@Override
	public String toString()
	{
		if (number == 0)
		{
			return super.toString();
		}
		else
		{
			return "break [" + number + "] " +
				(trace == null ? "" : "when \"" + trace + "\" ") +
				super.toString();
		}
	}
}
