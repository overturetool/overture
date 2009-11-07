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

import org.overturetool.vdmj.lex.LexLocation;

public class VoidType extends Type
{
	private static final long serialVersionUID = 1L;

	public VoidType(LexLocation location)
	{
		super(location);
	}

	@Override
	public boolean equals(Object other)
	{
		while (other instanceof BracketType)
		{
			other = ((BracketType)other).type;
		}

		return (other instanceof VoidType);
	}

	@Override
	public String toDisplay()
	{
		return "()";
	}
}
