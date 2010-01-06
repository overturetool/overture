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

import org.overturetool.vdmj.statements.CallStatement;
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

	public String toShape()
	{
		StringBuilder sb = new StringBuilder();
		String sep = "";

		for (Statement stmt: this)
		{
    		if (stmt instanceof TraceVariableStatement)
    		{
       			sb.append(sep);
       			sb.append(stmt.toString());
       			sep = "; ";
     		}
    		else if (stmt instanceof CallStatement)
    		{
    			CallStatement cs = (CallStatement)stmt;
       			sb.append(sep);
       			sb.append(cs.name.toString());
       			sep = "; ";
     		}
		}

		return sb.toString();
	}

	public boolean compareStem(CallSequence other, int upto)
	{
		// Note that the upto count does not include the variable statements
		// that may be in the sequences, but those variables do need to be
		// included in the stem match. "count" is the position ignoring any
		// variable statements.

		int i = 0;

		for (int count=0; count<upto;)
		{
			if (!compareItem(other, i))
			{
				return false;
			}

			if (!(get(i) instanceof TraceVariableStatement))
			{
				count++;	// Only increment for non-variable statements
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
