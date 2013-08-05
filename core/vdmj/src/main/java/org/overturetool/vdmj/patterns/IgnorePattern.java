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

package org.overturetool.vdmj.patterns;

import java.util.List;
import java.util.Vector;

import org.overturetool.vdmj.definitions.DefinitionList;
import org.overturetool.vdmj.expressions.Expression;
import org.overturetool.vdmj.expressions.VariableExpression;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.UnknownType;
import org.overturetool.vdmj.values.NameValuePairList;
import org.overturetool.vdmj.values.Value;

public class IgnorePattern extends Pattern
{
	private static final long serialVersionUID = 1L;
	private static int var = 1;		// Used in getMatchingExpression()
	private LexNameToken anyName = null;

	public IgnorePattern(LexLocation location)
	{
		super(location);
	}

	@Override
	public String toString()
	{
		return "-";
	}

	@Override
	public Expression getMatchingExpression()
	{
		// Generate a new "any" name for use during PO generation. The name
		// must be unique for the pattern instance.
		
		if (anyName == null)
		{
			anyName = new LexNameToken("", "any" + var++, location);
		}
		
		return new VariableExpression(anyName);
	}

	@Override
	public DefinitionList getAllDefinitions(Type type, NameScope scope)
	{
		return new DefinitionList();
	}

	@Override
	public int getLength()
	{
		return ANY;	// Special value meaning "any length"
	}

	@Override
	public List<NameValuePairList> getAllNamedValues(Value expval, Context ctxt)
	{
		List<NameValuePairList> result = new Vector<NameValuePairList>();
		result.add(new NameValuePairList());
		return result;
	}

	@Override
	public Type getPossibleType()
	{
		return new UnknownType(location);	// As we don't care
	}

	@Override
	public boolean isConstrained()
	{
		return false;
	}

	@Override
	public boolean isSimple()
	{
		return false;
	}

	@Override
	public boolean alwaysMatches()
	{
		return true;
	}
}
