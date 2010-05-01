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

package org.overturetool.vdmj.types;

import java.io.Serializable;

import org.overturetool.vdmj.ast.IAstNode;
import org.overturetool.vdmj.definitions.AccessSpecifier;
import org.overturetool.vdmj.definitions.TypeDefinition;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.typechecker.Environment;

public class Field implements Serializable, IAstNode
{
	private static final long serialVersionUID = 1L;

	public final AccessSpecifier accessibility;
	public final LexNameToken tagname;
	public final String tag;
	public Type type;
	public final boolean equalityAbstration;

	public Field(LexNameToken tagname, String tag, Type type, boolean equalityAbstration)
	{
		this.accessibility = null;
		this.tagname = tagname;
		this.tag = tag;
		this.type = type;
		this.equalityAbstration = equalityAbstration;
	}

	public Field(AccessSpecifier accessibility, LexNameToken tagname, String tag, Type type)
	{
		this.accessibility = accessibility;
		this.tagname = tagname;
		this.tag = tag;
		this.type = type;
		this.equalityAbstration = false;
	}

	public void unResolve()
	{
		type.unResolve();
	}

	public void typeResolve(Environment env, TypeDefinition root)
	{
		// Recursion defence done by the type
		type = type.typeResolve(env, root);

		if (env.isVDMPP())
		{
			if (type instanceof FunctionType)
			{
    			tagname.setTypeQualifier(((FunctionType)type).parameters);
			}
			else if (type instanceof OperationType)
    		{
    			tagname.setTypeQualifier(((OperationType)type).parameters);
    		}
		}
	}

	@Override
	public String toString()
	{
		return tagname + (equalityAbstration ? ":-" : ":") + type;
	}

	@Override
	public boolean equals(Object other)
	{
		if (other instanceof Field)
		{
			Field fother = (Field)other;
			return tag.equals(fother.tag) && type.equals(fother.type);
		}

		return false;
	}
	
	public String getName()
	{
		return tagname.getName();
	}
	
	public LexLocation getLocation()
	{
		return tagname.location;
	}
}
