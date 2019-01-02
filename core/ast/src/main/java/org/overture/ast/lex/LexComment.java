/*******************************************************************************
 *
 *	Copyright (c) 2018 Nick Battle.
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

package org.overture.ast.lex;

import org.overture.ast.intf.lex.ILexComment;
import org.overture.ast.intf.lex.ILexLocation;

/**
 * A comment within the spec. This isn't part of the grammar, as such.
 */
public class LexComment implements ILexComment
{
	private final ILexLocation location;
	private final String comment;
	private final boolean block;

	public LexComment(ILexLocation location, String comment, boolean block)
	{
		super();
		
		this.location = location;
		this.comment = comment;
		this.block = block;
	}
	
	@Override
	public String toString()
	{
		return block ? "/*" + comment + "*/" : "--" + comment;
	}

	@Override
	public ILexLocation getLocation()
	{
		return location;
	}

	@Override
	public String getComment()
	{
		return comment;
	}

	@Override
	public boolean isBlock()
	{
		return block;
	}
}
