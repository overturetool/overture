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
import org.overturetool.vdmj.runtime.ContextException;

/**
 * A VDM error message.
 */

public class VDMError extends VDMMessage
{
	public VDMError(int number, String message, LexLocation location)
	{
		super(number, message, location);
	}

	public VDMError(LocatedException ne)
	{
		super(ne.number, ne.getMessage(), ne.location);
	}

	public VDMError(ContextException ce)
	{
		super(ce.number, ce.getMessage(), ce.location);
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("Error ");
		sb.append(super.toString());

		for (String d: details)
		{
			sb.append("\n");
			sb.append(d);
		}

		return sb.toString();
	}
}
