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

import org.overturetool.vdmj.expressions.EqualsExpression;
import org.overturetool.vdmj.expressions.Expression;
import org.overturetool.vdmj.expressions.HistoryExpression;
import org.overturetool.vdmj.expressions.IntegerLiteralExpression;
import org.overturetool.vdmj.lex.LexIntegerToken;
import org.overturetool.vdmj.lex.LexKeywordToken;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.lex.LexNameList;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.lex.Token;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.typechecker.Pass;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.UnknownType;
import org.overturetool.vdmj.util.Utils;

public class MutexSyncDefinition extends Definition
{
	private static final long serialVersionUID = 1L;
	public final LexNameList operations;

	public MutexSyncDefinition(LexLocation location, LexNameList operations)
	{
		super(Pass.DEFS, location, null, NameScope.GLOBAL);
		this.operations = operations;
	}

	@Override
	public DefinitionList getDefinitions()
	{
		return new DefinitionList();
	}

	@Override
	public Type getType()
	{
		return new UnknownType(location);
	}

	@Override
	public LexNameList getVariableNames()
	{
		return new LexNameList();
	}

	@Override
	public String kind()
	{
		return "mutex predicate";
	}

	@Override
	public String toString()
	{
		return "mutex(" +
			(operations.isEmpty() ? "all)" :
				Utils.listToString("", operations, ", ", ")"));
	}
	
	@Override
	public boolean equals(Object other)
	{
		if (other instanceof MutexSyncDefinition)
		{
			return toString().equals(other.toString());
		}
		
		return false;
	}
	
	@Override
	public int hashCode()
	{
		return toString().hashCode();
	}

	@Override
	public Definition findName(LexNameToken sought, NameScope scope)
	{
		return null;
	}

	@Override
	public void typeCheck(Environment base, NameScope scope)
	{
		ClassDefinition classdef = base.findClassDefinition();

		if (operations.isEmpty())
		{
			// Add all locally visibly callable operations for mutex(all)

			for (Definition def: classdef.getLocalDefinitions())
			{
				if (def.isCallableOperation() &&
					!def.name.name.equals(classdef.name.name))
				{
					operations.add(def.name);
				}
			}
		}

		for (LexNameToken opname: operations)
		{
			int found = 0;

			for (Definition def: classdef.getDefinitions())
			{
				if (def.name != null && def.name.matches(opname))
				{
					found++;

					if (!def.isCallableOperation())
					{
						opname.report(3038, opname + " is not an explicit operation");
					}
				}
			}

    		if (found == 0)
    		{
    			opname.report(3039, opname + " is not in scope");
    		}
    		else if (found > 1)
    		{
    			opname.warning(5002, "Mutex of overloaded operation");
    		}

    		if (opname.name.equals(classdef.name.name))
    		{
    			opname.report(3040, "Cannot put mutex on a constructor");
    		}

    		for (LexNameToken other: operations)
    		{
    			if (opname != other && opname.equals(other))
    			{
    				opname.report(3041, "Duplicate mutex name");
    			}
    		}
		}
	}

	public Expression getExpression(LexNameToken excluding)
	{
		LexNameList list = null;

		if (operations.size() == 1)
		{
			list = operations;
		}
		else
		{
			list = new LexNameList();
			list.addAll(operations);
			list.remove(excluding);
		}

		return new EqualsExpression(
			new HistoryExpression(location, Token.ACTIVE, list),
    		new LexKeywordToken(Token.EQUALS, location),
    		new IntegerLiteralExpression(new LexIntegerToken(0, location)));
	}
}
