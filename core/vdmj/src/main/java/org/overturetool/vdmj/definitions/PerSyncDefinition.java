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

package org.overturetool.vdmj.definitions;

import org.overturetool.vdmj.expressions.Expression;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.lex.LexNameList;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.FlatEnvironment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.typechecker.Pass;
import org.overturetool.vdmj.types.BooleanType;
import org.overturetool.vdmj.types.Type;

public class PerSyncDefinition extends Definition
{
	private static final long serialVersionUID = 1L;
	public final LexNameToken opname;
	public final Expression guard;

	public PerSyncDefinition(LexLocation location,
		LexNameToken opname, Expression guard)
	{
		super(Pass.DEFS, location, opname.getPerName(location), NameScope.GLOBAL);
		this.opname = opname;
		this.guard = guard;
	}

	@Override
	public DefinitionList getDefinitions()
	{
		return new DefinitionList(this);
	}

	@Override
	public Type getType()
	{
		return new BooleanType(location);
	}

	@Override
	public LexNameList getVariableNames()
	{
		return new LexNameList();
	}

	@Override
	public String kind()
	{
		return "permission predicate";
	}

	@Override
	public String toString()
	{
		return "per " + opname + " => " + guard;
	}

	@Override
	public Definition findName(LexNameToken sought, NameScope scope)
	{
		return null;
	}

	@Override
	public Expression findExpression(int lineno)
	{
		return guard.findExpression(lineno);
	}

	@Override
	public void typeCheck(Environment base, NameScope scope)
	{
		ClassDefinition classdef = base.findClassDefinition();
		int opfound = 0;
		int perfound = 0;

		for (Definition def: classdef.getDefinitions())
		{
			if (def.name != null && def.name.matches(opname))
			{
				opfound++;

				if (!def.isCallableOperation())
				{
					opname.report(3042, opname + " is not an explicit operation");
				}
			}

			if (def instanceof PerSyncDefinition)
			{
				PerSyncDefinition psd = (PerSyncDefinition)def;

				if (psd.opname.equals(opname))
				{
					perfound++;
				}
			}
		}

		if (opfound == 0)
		{
			opname.report(3043, opname + " is not in scope");
		}
		else if (opfound > 1)
		{
			opname.warning(5003, "Permission guard of overloaded operation");
		}

		if (perfound != 1)
		{
			opname.report(3044, "Duplicate permission guard found for " + opname);
		}

		if (opname.name.equals(classdef.name.name))
		{
			opname.report(3045, "Cannot put guard on a constructor");
		}

		Environment local = new FlatEnvironment(this, base);
		local.setEnclosingDefinition(this);	// Prevent op calls
		Type rt = guard.typeCheck(local, null, NameScope.NAMESANDSTATE);

		if (!rt.isType(BooleanType.class))
		{
			guard.report(3046, "Guard is not a boolean expression");
		}
	}

	public Expression getExpression()
	{
		return guard;
	}
}
