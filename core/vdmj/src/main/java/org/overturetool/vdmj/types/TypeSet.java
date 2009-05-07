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

package org.overturetool.vdmj.types;

import java.util.Iterator;
import java.util.TreeSet;

import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.util.Utils;


@SuppressWarnings("serial")
public class TypeSet extends TreeSet<Type>
{
	public TypeSet()
	{
		super();
	}

	public TypeSet(Type t)
	{
		add(t);
	}

	public TypeSet(Type t1, Type t2)
	{
		add(t1);
		add(t2);
	}

	@Override
	public boolean add(Type t)
	{
		return super.add(t);
	}

	@Override
	public String toString()
	{
		return Utils.setToString(this, ", ");
	}

	public Type getType(LexLocation location)
	{
		// If there are any Optional(Unknowns) these are the result of
		// nil values, which set the overall type as optional. Other
		// optional types stay.

		Iterator<Type> tit = this.iterator();
		boolean optional = false;

		while (tit.hasNext())
		{
			Type t = tit.next();

			if (t instanceof OptionalType)
			{
				OptionalType ot = (OptionalType)t;

				if (ot.type instanceof UnknownType)
				{
					if (this.size() > 1)
    				{
    					tit.remove();
    					optional = true;
    				}
					else
					{
						optional = false;
					}
				}
			}
		}

		assert this.size() > 0 : "Getting type of empty TypeSet";
		Type result = null;

		if (this.size() == 1)
		{
			result = iterator().next();
		}
		else
		{
			result = new UnionType(location, this);
		}

		return (optional ? new OptionalType(location, result) : result);
	}
}
