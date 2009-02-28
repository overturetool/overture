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

package org.overturetool.vdmj.statements;

import org.overturetool.vdmj.definitions.ClassInvariantDefinition;
import org.overturetool.vdmj.definitions.Definition;
import org.overturetool.vdmj.definitions.DefinitionList;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.types.BooleanType;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.values.BooleanValue;
import org.overturetool.vdmj.values.Value;

public class ClassInvariantStatement extends Statement
{
	public final LexNameToken name;
	public final DefinitionList invdefs;

	public ClassInvariantStatement(LexNameToken name, DefinitionList invdefs)
	{
		super(name.location);
		this.name = name;
		this.invdefs = invdefs;
	}

	@Override
	public Type typeCheck(Environment env, NameScope scope)
	{
		// Definitions already checked.
		return new BooleanType(location);
	}

	@Override
	public Value eval(Context ctxt)
	{
		for (Definition d: invdefs)
		{
			ClassInvariantDefinition invdef = (ClassInvariantDefinition)d;

			try
			{
				if (!invdef.expression.eval(ctxt).boolValue(ctxt))
				{
					return new BooleanValue(false);
				}
			}
			catch (ValueException e)
			{
				abort(e);
			}
		}

		return new BooleanValue(true);
	}

	@Override
	public String kind()
	{
		return "instance invariant";
	}

	@Override
	public String toString()
	{
		return "instance invariant " + name;
	}
}
