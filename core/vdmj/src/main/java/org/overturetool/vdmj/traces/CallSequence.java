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

import java.util.List;
import java.util.Vector;

import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.statements.Statement;
import org.overturetool.vdmj.util.Utils;

@SuppressWarnings("serial")
public class CallSequence extends Vector<Statement>
{
	public Context ctxt = null;
	public List<Integer> hashes = null;
	private int filtered = 0;

	public CallSequence()
	{
		hashes = new Vector<Integer>();
	}

	public void setContext(Context ctxt)
	{
		this.ctxt = ctxt;
	}

	@Override
	public String toString()
	{
		return Utils.listToString(this, "; ");
	}

	public boolean compareStem(CallSequence other, int upto)
	{
		for (int i=0; i<upto; i++)
		{
			if (!compareItem(other, i))
			{
				return false;
			}
		}

		return true;
	}

	private boolean compareItem(CallSequence other, int i)
	{
		Statement os = other.get(i);
		int oi = other.hashes.get(i);

		return (hashes.get(i) == oi) &&
			   (get(i).toString().equals(os.toString()));
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
