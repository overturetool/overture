/*******************************************************************************
 *
 *	Copyright (c) 2008 Fujitsu Services Ltd.
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

package org.overturetool.vdmj.statements;

import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.TypeSet;
import org.overturetool.vdmj.types.UnionType;
import org.overturetool.vdmj.types.VoidReturnType;
import org.overturetool.vdmj.types.VoidType;
import org.overturetool.vdmj.values.Value;

public class NonDeterministicStatement extends SimpleBlockStatement
{
	private static final long serialVersionUID = 1L;

	public NonDeterministicStatement(LexLocation location)
	{
		super(location);
	}

	@Override
	public Type typeCheck(Environment env, NameScope scope)
	{
		TypeSet rtypes = new TypeSet();
		int rcount = 0;

		for (Statement stmt: statements)
		{
			Type stype = stmt.typeCheck(env, scope);

			if (stype instanceof UnionType)
			{
				UnionType ust = (UnionType)stype;

				for (Type t: ust.types)
				{
					if (addOne(rtypes, t)) rcount++;
				}
			}
			else
			{
				if (addOne(rtypes, stype)) rcount++;
			}
		}
		
		if (rcount > 1)
		{
			warning(5016, "Some statements will not be reached");
		}

		return rtypes.isEmpty() ?
			new VoidType(location) : rtypes.getType(location);
	}

	private boolean addOne(TypeSet rtypes, Type add)
	{
		if (add instanceof VoidReturnType)
		{
			rtypes.add(new VoidType(add.location));
			return true;
		}
		else if (!(add instanceof VoidType))
		{
			rtypes.add(add);
			return true;
		}
		else
		{
			rtypes.add(add);
			return false;
		}
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("||(\n");
		sb.append(super.toString());
		sb.append(")");
		return sb.toString();
	}

	@Override
	public String kind()
	{
		return "non-deterministic";
	}

	@Override
	public Value eval(Context ctxt)
	{
		breakpoint.check(location, ctxt);
		return evalBlock(ctxt);
	}
}
