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

package org.overturetool.vdmj.runtime;

import org.overturetool.vdmj.lex.LexLocation;

@SuppressWarnings("serial")
public class ContextException extends RuntimeException
{
	public final LexLocation location;
	public final Context ctxt;
	public final int number;

	public ContextException(int number, String msg, LexLocation location, Context ctxt)
	{
		super("Error " + number + ": " + msg + " " + location);
		this.location = location;
		this.number = number;
		this.ctxt = ctxt;
	}

	public ContextException(ValueException ve, LexLocation location)
	{
		this(ve.number, ve.getMessage(), location, ve.ctxt);
	}

	@Override
	public String toString()
	{
		return getMessage();
	}
}
