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

package org.overturetool.vdmj.syntax;

import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.messages.LocatedException;

/**
 * A syntax analyser exception.
 */

@SuppressWarnings("serial")
public class ParserException extends LocatedException
{
	private int depth;

	public ParserException(int number, String message, LexLocation location, int depth)
	{
		super(number, message, location);
		this.depth = depth;
	}

	public int getDepth()
	{
		return depth;		// Tokens read since start, or last push
	}

	public void adjustDepth(int adj)
	{
		depth += adj;
	}

	public boolean deeperThan(ParserException other)
	{
		return depth > other.depth;
	}
}
