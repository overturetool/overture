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

/**
 * The root of all reported messages.
 */

public class VDMMessage
{
	public final int number;
	public final String message;
	public final LexLocation location;

	public VDMMessage(int number)
	{
		this.number = number;
		this.message = "";
		this.location = null;
	}

	public VDMMessage(int number, String message, LexLocation location)
	{
		this.number = number;
		this.message = message;
		this.location = location;
	}

	@Override
	public String toString()
	{
		String padded = "0000" + number;
		padded = padded.substring(padded.length() - 4);
		return padded + ": " + message + " " + location;
	}

	@Override
	public boolean equals(Object other)
	{
		if (other instanceof VDMMessage)
		{
			// Just compare numbers
			return ((VDMMessage)other).number == number;
		}

		return false;
	}

	@Override
	public int hashCode()
	{
		return number;
	}
}
