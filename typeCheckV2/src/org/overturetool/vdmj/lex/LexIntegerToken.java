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

public class LexIntegerToken extends LexToken
{
	private static final long serialVersionUID = 1L;
	public final long value;

	public LexIntegerToken(long value, LexLocation location)
	{
		super(location, Token.NUMBER);
		this.value = value;
	}

	public LexIntegerToken(String value, LexLocation location)
	{
		super(location, Token.NUMBER);
		this.value = Long.parseLong(value);
	}

	@Override
	public String toString()
	{
		return Long.toString(value);
	}
}
