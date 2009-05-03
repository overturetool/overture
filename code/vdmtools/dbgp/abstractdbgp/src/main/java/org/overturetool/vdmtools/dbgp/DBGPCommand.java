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

import java.util.List;


public class DBGPCommand
{
	public final DBGPCommandType type;
	public final List<DBGPOption> options;
	public final String data;

	
	public DBGPCommand(
		DBGPCommandType type, List<DBGPOption> options, String base64)
		throws Exception
	{
		this.type = type;
		this.options = options;

		if (base64 != null)
		{
			this.data = new String(Base64.decode(base64.getBytes()), "UTF-8");
		}
		else
		{
			this.data = null;
		}
	}

	@Override
	public String toString()
	{
		return type +
			(options.isEmpty() ? "" : " " + Utils.listToString(options, " ")) +
			(data == null ? "" : " -- " + data);
	}

	public DBGPOption getOption(DBGPOptionType sought)
	{
		for (DBGPOption opt: options)
		{
			if (opt.type == sought)
			{
				return opt;
			}
		}
		return null;
	}
	
}
