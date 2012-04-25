/*******************************************************************************
 *
 *	Copyright (c) 2009 Fujitsu Services Ltd.
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

import java.io.PrintWriter;

import org.overturetool.vdmj.definitions.ClassDefinition;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.values.Value;

@SuppressWarnings("serial")
public class ClassContext extends RootContext
{
	public final ClassDefinition classdef;

	public ClassContext(LexLocation location,
		String title, Context freeVariables, Context outer,
		ClassDefinition classdef)
	{
		super(location, title, freeVariables, outer);
		this.classdef = classdef;
	}

	public ClassContext(LexLocation location,
		String title, Context outer, ClassDefinition classdef)
	{
		this(location, title, null, outer, classdef);
	}

	/**
	 * Check for the name in the current context and classdef, and if
	 * not present search the global context. Note that the context
	 * chain is not followed.
	 *
	 * @see org.overturetool.vdmj.runtime.Context#check(org.overturetool.vdmj.lex.LexNameToken)
	 */

	@Override
	public Value check(LexNameToken name)
	{
		// A RootContext stops the name search from continuing down the
		// context chain. It first checks any local context, then it
		// checks the "class" context, then it goes down to the global level.

		Value v = get(name);		// Local variables

		if (v != null)
		{
			return v;
		}

		if (freeVariables != null)
		{
			v = freeVariables.get(name);

			if (v != null)
			{
				return v;
			}
		}

		v = classdef.getStatic(name);

		if (v != null)
		{
			return v;
		}

		Context g = getGlobal();

		if (g != this)
		{
			return g.check(name);
		}

		return v;
	}

	@Override
	public String toString()
	{
		return super.toString();	// Self there anyway ...+ self.toString();
	}

	@Override
	public void printStackTrace(PrintWriter out, boolean variables)
	{
		if (outer == null)		// Don't expand initial context
		{
			out.println("In class context of " + title);
		}
		else
		{
			if (variables)
			{
    			out.print(this.format("\t", this));
			}

			out.println("In class context of " + title + " " + location);
			outer.printStackTrace(out, false);
		}
	}
}
