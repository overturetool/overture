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
	public boolean copyContext = false;
	private List<Integer> hashes = null;
	private int filtered = 0;

	public CallSequence()
	{
		hashes = new Vector<Integer>();
	}

	public void setContext(CallSequence other)
	{
		this.ctxt = other.ctxt;
		this.copyContext = this.copyContext || other.copyContext;
	}

	public void setContext(Context ctxt, boolean copy)
	{
		this.ctxt = ctxt;
		this.copyContext = this.copyContext || copy;
	}

	public void addHash(int n)
	{
		hashes.add(n);
	}

	public void addHashes(List<Integer> list)
	{
		for (Integer i: list)
		{
			addHash(i);
		}
	}

	public List<Integer> getHashes()
	{
		return hashes;
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
		return (hashes.get(i) == other.hashes.get(i)) &&
			   (get(i).toString().equals(other.get(i).toString()));
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
