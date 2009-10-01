/*******************************************************************************
 *
 *	Copyright (C) 2008, 2009 Fujitsu Services Ltd.
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

package org.overturetool.vdmj.traces;

import java.util.Vector;
import org.overturetool.vdmj.statements.Statement;

@SuppressWarnings("serial")
public class CallSequence extends Vector<Statement>
{
	private int filtered = 0;

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		String sep = "";

		for (Statement stmt: this)
		{
    		if (!(stmt instanceof TraceVariableStatement))
    		{
       			sb.append(sep);
       			sb.append(stmt.toString());
       			sep = "; ";
     		}
		}

		return sb.toString();
	}

	public boolean compareStem(CallSequence other, int upto)
	{
		int i = 0;

		for (int count=0; count<upto;)
		{
			if (!compareItem(other, i))
			{
				return false;
			}

			if (!(get(i) instanceof TraceVariableStatement))
			{
				count++;
			}

			i++;
		}

		return true;
	}

	private boolean compareItem(CallSequence other, int i)
	{
		return get(i).toString().equals(other.get(i).toString());
	}

	public void setFilter(int n)
	{
		filtered = n;
	}

	public int getFilter()
	{
		return filtered;
	}
}
