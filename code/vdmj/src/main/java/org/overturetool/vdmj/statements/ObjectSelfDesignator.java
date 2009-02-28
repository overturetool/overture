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

import org.overturetool.vdmj.definitions.Definition;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.TypeList;
import org.overturetool.vdmj.types.UnknownType;
import org.overturetool.vdmj.values.Value;

public class ObjectSelfDesignator extends ObjectDesignator
{
	public final LexNameToken self;

	public ObjectSelfDesignator(LexLocation location)
	{
		super(location);
		self = new LexNameToken(location.module, "self", location);
	}

	@Override
	public String toString()
	{
		return "self";
	}

	@Override
	public Type typeCheck(Environment env, TypeList qualifiers)
	{
		Definition def = env.findName(self, NameScope.NAMES);

		if (def == null)
		{
			report(3263, "Cannot reference 'self' from here");
			return new UnknownType(location);
		}

		return def.getType();
	}

	@Override
	public Value eval(Context ctxt)
	{
		return ctxt.lookup(self);
	}
}
