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

package org.overturetool.vdmj.expressions;

import org.overturetool.vdmj.definitions.ClassDefinition;
import org.overturetool.vdmj.definitions.Definition;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.lex.LexNameList;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.lex.Token;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.ObjectContext;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.types.IntegerType;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.TypeList;
import org.overturetool.vdmj.values.IntegerValue;
import org.overturetool.vdmj.values.ObjectValue;
import org.overturetool.vdmj.values.OperationValue;
import org.overturetool.vdmj.values.Value;
import org.overturetool.vdmj.values.ValueList;

public class HistoryExpression extends Expression
{
	public final Token hop;
	public final LexNameList opnames;

	public HistoryExpression(
		LexLocation location, Token hop, LexNameList opnames)
	{
		super(location);
		this.hop = hop;
		this.opnames = opnames;
	}

	@Override
	public Value eval(Context ctxt)
	{
		try
		{
			// TODO Not very efficient to do this every time. But we can't
			// save the list because the same HistoryExpression is called from
			// different object instance contexts, and each instance has its
			// own operation history counters...

			ValueList operations = new ValueList();
			ObjectValue self = ((ObjectContext)ctxt).self;

			for (LexNameToken opname: opnames)
			{
				operations.addAll(self.getOverloads(opname));
			}

			int result = 0;

    		for (Value v: operations)
    		{
    			OperationValue ov = v.operationValue(ctxt);

    			switch (hop)
    			{
    				case ACT:
    					result += ov.hashAct;
    					break;

    				case FIN:
       					result += ov.hashFin;
    					break;

    				case REQ:
       					result += ov.hashReq;
    					break;

    				case ACTIVE:
       					result += ov.hashAct - ov.hashFin;
    					break;

    				case WAITING:
       					result += ov.hashReq - ov.hashAct;
    					break;

    				default:
    					abort(4011, "Illegal history operator: " + hop, ctxt);

    			}
    		}

    		return new IntegerValue(result);
		}
		catch (ValueException e)
		{
			return abort(e);
		}
	}

	@Override
	public String kind()
	{
		return toString();
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();

		sb.append("#");
		sb.append(hop.toString().toLowerCase());
		sb.append("(");
		String sep = "";

		for (LexNameToken opname: opnames)
		{
			sb.append(sep);
			sep = ", ";
			sb.append(opname.name);
		}

		sb.append(")");
		return sb.toString();
	}

	@Override
	public Type typeCheck(Environment env, TypeList qualifiers, NameScope scope)
	{
		ClassDefinition classdef = env.findClassDefinition();

		for (LexNameToken opname: opnames)
		{
    		int found = 0;

    		for (Definition def: classdef.getDefinitions())
    		{
    			if (def.name != null && def.name.matches(opname))
    			{
    				found++;

    				if (!def.isCallableOperation())
    				{
    					opname.report(3105, opname + " is not an explicit operation");
    				}
    			}
    		}

    		if (found == 0)
    		{
    			opname.report(3106, opname + " is not in scope");
    		}
    		else if (found > 1)
    		{
    			opname.warning(5004, "History expression of overloaded operation");
    		}

    		if (opname.name.equals(classdef.name.name))
    		{
    			opname.report(3107, "Cannot use history of a constructor");
    		}
		}

		return new IntegerType(location);
	}
}
