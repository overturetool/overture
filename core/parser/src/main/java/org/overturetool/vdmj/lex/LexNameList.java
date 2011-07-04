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

package org.overturetool.vdmj.lex;

import java.util.Vector;

import org.overturetool.vdmj.util.Utils;


@SuppressWarnings("serial")
public class LexNameList extends Vector<LexNameToken>
{
	public LexNameList()
	{
		super();
	}

	public LexNameList(LexNameToken name)
	{
		super();
		add(name);
	}

	@Override
	public String toString()
	{
		return Utils.listToString(this);
	}

	public boolean hasDuplicates()
	{
		int len = size();

		for (int i=0; i<len; i++)
		{
			LexNameToken name = get(i);

			for (int j=i+1; j<len; j++)
			{
				if (get(j).equals(name))
				{
					return true;
				}
			}
		}

		return false;
	}
}
