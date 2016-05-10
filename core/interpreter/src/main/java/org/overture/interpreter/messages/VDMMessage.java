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

package org.overture.interpreter.messages;

import java.util.List;
import java.util.Vector;

import org.overture.ast.intf.lex.ILexLocation;

/**
 * The root of all reported messages.
 */

public class VDMMessage
{
	public final int number;
	public final String message;
	public final ILexLocation location;

	protected List<String> details = new Vector<>();

	public VDMMessage(int number)
	{
		this.number = number;
		this.message = "";
		this.location = null;
	}

	public VDMMessage(int number, String message, ILexLocation location)
	{
		this.number = number;
		this.message = message;
		this.location = location;
	}

	public void add(String det)
	{
		details.add(det);
	}

	@Override
	public String toString()
	{
		return String.format("%04d: %s %s", number, message, location);
	}

	// This one is used by the GUI to generate strings for the problem
	// view, with details but without the file location and number.

	public String toProblemString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(message);

		sb.append(".");
		for (String d : details)
		{
			sb.append(" ");
			sb.append(d);
		}

		return sb.toString();
	}

	@Override
	public boolean equals(Object other)
	{
		if (other instanceof VDMMessage)
		{
			VDMMessage omsg = (VDMMessage) other;
			return omsg.number == number && omsg.message.equals(message)
					&& omsg.location.equals(location);
		}

		return false;
	}

	@Override
	public int hashCode()
	{
		return number;
	}
}
