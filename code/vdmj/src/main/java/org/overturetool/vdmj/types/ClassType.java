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

import org.overturetool.vdmj.definitions.ClassDefinition;
import org.overturetool.vdmj.definitions.Definition;
import org.overturetool.vdmj.definitions.TypeDefinition;
import org.overturetool.vdmj.lex.LexIdentifierToken;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.typechecker.PrivateClassEnvironment;
import org.overturetool.vdmj.typechecker.TypeCheckException;

public class ClassType extends Type
{
	public final ClassDefinition classdef;
	public final LexNameToken name;		// "CLASS`<name>"

	public ClassType(LexLocation location, ClassDefinition classdef)
	{
		super(location);

		this.classdef = classdef;
		this.name = classdef.name;
	}

	public LexNameToken getMemberName(LexIdentifierToken id)
	{
		// Note: not explicit
		return new LexNameToken(name.name, id.name, id.location, false, false);
	}

	@Override
	public void unResolve()
	{
		if (resolved)
		{
    		resolved = false;

    		for (Definition d: classdef.getDefinitions())
    		{
    			d.getType().unResolve();
    		}
		}
	}

	@Override
	public Type typeResolve(Environment env, TypeDefinition root)
	{
		if (resolved) return this; else resolved = true;

		try
		{
			// We have to add a private class environment here because the
			// one passed in may be from a class that contains a reference
			// to this class. We need the private environment to see all
			// the definitions that are available to us while resolving...

			Environment self = new PrivateClassEnvironment(classdef, env);

			for (Definition d: classdef.getDefinitions())
			{
				d.getType().typeResolve(self, root);
			}

			return this;
		}
		catch (TypeCheckException e)
		{
			unResolve();
			throw e;
		}
	}

	public Definition findName(LexNameToken tag)
	{
		return classdef.findName(tag, NameScope.NAMESANDSTATE);
	}

	@Override
	public boolean isClass()
	{
		return true;
	}

	@Override
	public ClassType getClassType()
	{
		return this;
	}

	public boolean hasSupertype(Type other)
	{
		return classdef.hasSupertype(other);
	}

	@Override
	protected String toDisplay()
	{
		return classdef.name.name;
	}

	@Override
	public boolean equals(Object other)
	{
		while (other instanceof BracketType)
		{
			other = ((BracketType)other).type;
		}

		if (other instanceof ClassType)
		{
			ClassType oc = (ClassType)other;
			return name.equals(oc.name);		// NB. name only
		}

		return false;
	}

	@Override
	public int hashCode()
	{
		return name.hashCode();
	}
}
