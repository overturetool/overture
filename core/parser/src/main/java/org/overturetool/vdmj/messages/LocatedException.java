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

package org.overturetool.vdmj.messages;

import org.overturetool.vdmj.lex.LexLocation;

@SuppressWarnings("serial")
abstract public class LocatedException extends NumberedException
{
	public final LexLocation location;

	public LocatedException(int number, String msg, LexLocation location)
	{
		super(number, msg);
		this.location = location;
	}

	@Override
	public String toString()
	{
		return "Error " + number + ": " + getMessage() + " " + location;
	}
}
