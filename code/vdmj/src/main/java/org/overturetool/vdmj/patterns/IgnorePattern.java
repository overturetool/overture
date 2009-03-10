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

import org.overturetool.vdmj.definitions.DefinitionList;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.UnknownType;
import org.overturetool.vdmj.values.NameValuePairList;
import org.overturetool.vdmj.values.Value;

public class IgnorePattern extends Pattern
{
	private static final long serialVersionUID = 1L;
	private static int var = 1;		// Used in getMatchingValue()

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
	public String getMatchingValue()
	{
		return "any" + var++;
	}

	@Override
	public DefinitionList getDefinitions(Type type, NameScope scope)
	{
		return new DefinitionList();
	}

	@Override
	public int getLength()
	{
		return 0;	// Special value meaning "any length"
	}

	@Override
	public NameValuePairList getNamedValues(Value expval, Context ctxt)
	{
		return new NameValuePairList();
	}

	@Override
	public Type getPossibleType()
	{
		return new UnknownType(location);	// As we don't care
	}
}
