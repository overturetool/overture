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

import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.messages.Console;
import org.overturetool.vdmj.values.ObjectValue;
import org.overturetool.vdmj.values.UpdatableValue;
import org.overturetool.vdmj.values.Value;

/**
 * A root context for object method invocations.
 */

@SuppressWarnings("serial")
public class ObjectContext extends RootContext
{
	public final ObjectValue self;

	/**
	 * Create an ObjectContext from the values passed.
	 *
	 * @param location The location of the context.
	 * @param title The name of the location.
	 * @param outer The context chain (not searched).
	 * @param self The object context.
	 */

	public ObjectContext(
		LexLocation location, String title, Context outer, ObjectValue self)
	{
		super(location, title, outer);
		this.self = self;
	}

	/**
	 * Build a new Context based on the current one, but with only the
	 * updateable values included (eg. "dcl" definitions and state).
	 */

	@Override
	public Context getUpdateable()
	{
		Context outup = (outer == null) ? null : outer.getUpdateable();
		Context result = new ObjectContext(location, title, outup, self);

		for (LexNameToken var: keySet())
		{
			Value v = get(var);

			if (v instanceof UpdatableValue)
			{
				result.put(var, v);
			}
		}

		return result;
	}

	@Override
	public Context copy()
	{
		Context below = null;

		if (outer != null)
		{
			below = outer.copy();
		}

		Context result =
			new ObjectContext(location, title, below, self.deepCopy());

		for (LexNameToken var: keySet())
		{
			Value v = get(var);
			result.put(var, (Value)v.clone());
		}

		return result;
	}

	/**
	 * Check for the name in the current context and self, and if
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
		// checks the "self" context, then it goes down to the global level.

		Value v = get(name);		// Local variables

		if (v != null)
		{
			return v;
		}

		v = self.get(name, name.explicit);

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
	public void printStackTrace(boolean variables)
	{
		if (outer == null)		// Don't expand initial context
		{
			Console.out.println("In object context of " + title);
		}
		else
		{
			if (variables)
			{
    			Console.out.print(this.format("\t", this));
			}

			Console.out.println("In object context of " + title + " " + location);
			outer.printStackTrace(false);
		}
	}
}
