/*******************************************************************************
 *
 *	Copyright (c) 2009 Fujitsu Services Ltd.
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

package org.overturetool.vdmj.debug;

public enum DBGPContextType
{
	LOCAL("local", 0),
	CLASS("class", 1),
	GLOBAL("global", 2);

	public String value;
	public int code;

	DBGPContextType(String value, int code)
	{
		this.value = value;
		this.code = code;
	}

	public static DBGPContextType lookup(String string) throws DBGPException
	{
		for (DBGPContextType type: values())
		{
			if (type.value.equals(string))
			{
				return type;
			}
		}

		throw new DBGPException(DBGPErrorCode.PARSE, string);
	}

	public static DBGPContextType lookup(int code) throws DBGPException
	{
		for (DBGPContextType type: values())
		{
			if (type.code == code)
			{
				return type;
			}
		}

		throw new DBGPException(DBGPErrorCode.PARSE, "" + code);
	}

	@Override
	public String toString()
	{
		return value;
	}
}
