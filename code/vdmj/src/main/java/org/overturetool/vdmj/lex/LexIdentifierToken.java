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

package org.overturetool.vdmj.lex;

public class LexIdentifierToken extends LexToken
{
	public final String name;
	public final boolean old;

	public LexIdentifierToken(String name, boolean old, LexLocation location)
	{
		super(location, Token.IDENTIFIER);
		this.name = name;
		this.old = old;
	}

	public LexNameToken getClassName()
	{
		return new LexNameToken("CLASS", name, location);
	}

	@Override
	public boolean equals(Object other)
	{
		if (other instanceof LexIdentifierToken)
		{
			LexIdentifierToken tother = (LexIdentifierToken)other;
			return this.name.equals(tother.name) && this.old == tother.old;
		}

		return false;
	}

	@Override
	public int hashCode()
	{
		return name.hashCode() + (old ? 1 : 0);
	}

	@Override
	public String toString()
	{
		return name + (old ? "~" : "");
	}
}
