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

package org.overture.ast.messages;

import java.util.Formatter;

@SuppressWarnings("serial")
public class InternalException extends RuntimeException
{
	public final int number;

	public InternalException(int number, String message)
	{
		super(message);
		this.number = number;
	}

	public InternalException(int number, String message, Throwable cause)
	{
		super(message,cause);
		this.number = number;
	}

	@Override
	public String toString()
	{
		Formatter f = new Formatter();
		f.format("Internal %04d: %s", number, getMessage());
		String tmp = f.out().toString();
		f.close();
		return tmp;
	}
}
