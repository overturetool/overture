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
import org.overturetool.vdmj.values.ObjectValue;

/**
 * A Context class, specialized to represent points in a context chain where
 * name resolution does not proceed further down the chain, but rather jumps to
 * the outermost level.
 */

@SuppressWarnings("serial")
public abstract class RootContext extends Context
{
	protected final Context freeVariables;

	public RootContext(
		LexLocation location, String title, Context freeVariables, Context outer)
	{
		super(location, title, outer);
		this.freeVariables = freeVariables;
	}

	@Override
	public Context getVisibleVariables()
	{
		Context visible = new Context(location, title, null);
		visible.putAll(this);

		if (freeVariables != null)
		{
			visible.putAll(freeVariables);
		}

		return visible;
	}

	@Override
	public RootContext getRoot()
	{
		return this;
	}

	@Override
	public final int getDepth()
	{
		return outer == null ? 1 : outer.getDepth() + 1;
	}

	@Override
	public final Context getFrame(int depth)
	{
		return depth == 0 ? this :
				outer == null ? this :
					outer.getFrame(depth - 1);
	}

	@Override
	public ObjectValue getSelf()
	{
		return null;	// Overridden in ObjectContext
	}
}
