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

package org.overturetool.vdmj.values;

import java.util.List;
import java.util.Vector;

import org.overturetool.vdmj.patterns.Pattern;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.PatternMatchException;

public class Quantifier
{
	public final Pattern pattern;
	public final ValueList values;
	private List<NameValuePairList> nvlist;

	public Quantifier(Pattern pattern, ValueList values)
	{
		this.pattern = pattern;
		this.values = values;
		this.nvlist = new Vector<NameValuePairList>(values.size());
	}

	public int size(Context ctxt, boolean allPossibilities)
	{
		for (Value value: values)
		{
			try
			{
				if (allPossibilities)
				{
					nvlist.addAll(pattern.getAllNamedValues(value, ctxt));
				}
				else
				{
					nvlist.add(pattern.getNamedValues(value, ctxt));
				}
			}
			catch (PatternMatchException e)
			{
				// Should never happen
			}
		}
		
		return nvlist.size();
	}

	public NameValuePairList get(int index)
		throws PatternMatchException
	{
		if (index >= nvlist.size())		// no values
		{
			return new NameValuePairList();
		}

		return nvlist.get(index);
	}
}
