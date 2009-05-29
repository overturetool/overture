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

import org.overturetool.vdmj.lex.LexException;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.syntax.ParserException;

/**
 * A breakpoint where something is displayed.
 */

public class Tracepoint extends Breakpoint
{
	private static final long serialVersionUID = 1L;

	public Tracepoint(LexLocation location, int number, String trace)
		throws ParserException, LexException
	{
		super(location, number, trace);
	}

	@Override
	public void check(LexLocation execl, Context ctxt)
	{
		location.hit();
		hits++;

		handleInterrupt(execl, ctxt);

		if (parsed == null)
		{
			println("Reached [" + number + "]" );
		}
		else
		{
			println(trace + " = " + parsed.eval(ctxt) + " at [" + number + "]" );
		}
	}

	@Override
	public String toString()
	{
		return "trace [" + number + "] " +
				(trace == null ? "" : "show \"" + trace + "\" ") +
				super.toString();
	}
}
