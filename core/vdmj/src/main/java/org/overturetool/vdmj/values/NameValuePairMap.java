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

import java.util.HashMap;

import org.overturetool.vdmj.lex.LexNameToken;

@SuppressWarnings("serial")
public class NameValuePairMap extends HashMap<LexNameToken, Value>
{
	public void put(NameValuePair nvp)
	{
		put(nvp.name, nvp.value);
	}

	public void putAll(NameValuePairList list)
	{
		for (NameValuePair nvp: list)
		{
			put(nvp);
		}
	}

	public ValueList getOverloads(LexNameToken sought)
	{
		ValueList list = new ValueList();

		for (LexNameToken name: this.keySet())
		{
			if (name.matches(sought))	// All overloaded names
			{
				list.add(get(name));
			}
		}

		return list;
	}

	public NameValuePairList asList()
	{
		NameValuePairList list = new NameValuePairList();

		for (LexNameToken name: this.keySet())
		{
			list.add(new NameValuePair(name, get(name)));
		}

		return list;
	}

	@Override
	public Object clone()
	{
		NameValuePairMap copy = new NameValuePairMap();

		for (LexNameToken name: keySet())
		{
			copy.put(name, (Value)get(name).clone());
		}

		return copy;
	}
}
