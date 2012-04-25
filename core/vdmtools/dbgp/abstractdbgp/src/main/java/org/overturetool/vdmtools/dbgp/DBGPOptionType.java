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

package org.overturetool.vdmtools.dbgp;

public enum DBGPOptionType
{
	TRANSACTION_ID("-i"),
	N("-n"),
	V("-v"),
	T("-t"),
	S("-s"),
	F("-f"),
	M("-m"),
	O("-o"),
	H("-h"),
	X("-x"),
	R("-r"),
	D("-d"),
	P("-p"),
	K("-k"),
	A("-a"),
	C("-c"),
	B("-b"),
	E("-e"),
	DATA("--");

	public String tag;

	DBGPOptionType(String tag)
	{
		this.tag = tag;
	}

	public static DBGPOptionType lookup(String string) throws DBGPException
	{
		for (DBGPOptionType opt: values())
		{
			if (opt.tag.equals(string))
			{
				return opt;
			}
		}

		throw new DBGPException(DBGPErrorCode.INVALID_OPTIONS, string);
	}

	@Override
	public String toString()
	{
		return tag;
	}
}
