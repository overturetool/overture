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

import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.statements.Statement;
import org.overturetool.vdmj.util.Utils;

@SuppressWarnings("serial")
public class CallSequence extends Vector<Statement>
{
	public Context ctxt = null;
	private int filtered = 0;

	public void setContext(Context ctxt)
	{
		this.ctxt = ctxt;
	}

	@Override
	public String toString()
	{
		return Utils.listToString(this, "; ");
	}

	public String toString(int upto)
	{
		return Utils.listToString(this.subList(0, upto), "; ");
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
