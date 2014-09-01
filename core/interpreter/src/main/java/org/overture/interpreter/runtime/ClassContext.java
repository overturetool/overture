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

package org.overture.interpreter.runtime;

import java.io.PrintWriter;

import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.values.Value;

@SuppressWarnings("serial")
public class ClassContext extends RootContext
{
	public final SClassDefinition classdef;

	public ClassContext(IInterpreterAssistantFactory af, ILexLocation location,
			String title, Context freeVariables, Context outer,
			SClassDefinition classdef)
	{
		super(af, location, title, freeVariables, outer);
		this.classdef = classdef;
	}

	public ClassContext(IInterpreterAssistantFactory af, ILexLocation location,
			String title, Context outer, SClassDefinition classdef)
	{
		this(af, location, title, null, outer, classdef);
	}

	/**
	 * Check for the name in the current context and classdef, and if not present search the global context. Note that
	 * the context chain is not followed.
	 * 
	 * @see Context#check(ILexNameToken)
	 */

	@Override
	public Value check(ILexNameToken name)
	{
		// A RootContext stops the name search from continuing down the
		// context chain. It first checks any local context, then it
		// checks the "class" context, then it goes down to the global level.

		Value v = get(name); // Local variables

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

		v = assistantFactory.createSClassDefinitionAssistant().getStatic(classdef, name);

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
		return super.toString(); // Self there anyway ...+ self.toString();
	}

	@Override
	public void printStackTrace(PrintWriter out, boolean variables)
	{
		if (outer == null) // Don't expand initial context
		{
			out.println("In class context of " + title);
		} else
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
