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

package org.overture.interpreter.values;

import java.io.Serializable;

import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.lex.LexNameList;
import org.overture.typechecker.util.HackLexNameToken;
import org.overture.typechecker.util.LexNameTokenMap;

public class NameValuePairMap extends LexNameTokenMap<Value> implements
		Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3333481593678892977L;

	public void put(NameValuePair nvp)
	{
		put(nvp.name, nvp.value);
	}

	public void putNew(NameValuePair nvp)
	{
		if (get(nvp.name) == null)
		{
			put(nvp.name, nvp.value);
		}
	}

	public void putAll(NameValuePairList list)
	{
		for (NameValuePair nvp : list)
		{
			put(nvp);
		}
	}

	public void putAllNew(NameValuePairList list)
	{
		for (NameValuePair nvp : list)
		{
			putNew(nvp);
		}
	}

	public ValueList getOverloads(ILexNameToken sought)
	{
		ValueList list = new ValueList();

		for (Entry<ILexNameToken, Value> entry : this.entrySet())
		{
			if (entry.getKey().matches(sought)) // All overloaded names
			{
				list.add(entry.getValue());
			}
		}

		return list;
	}
	
	public LexNameList getOverloadNames(ILexNameToken sought)
	{
		LexNameList list = new LexNameList();

		for (Entry<ILexNameToken, Value> entry: this.entrySet())
		{
			if (entry.getKey().matches(sought))		// All overloaded names
			{
				list.add(entry.getKey());
			}
		}

		return list;
	}

	public NameValuePairList asList()
	{
		NameValuePairList list = new NameValuePairList();

		for (Entry<ILexNameToken, Value> entry : this.entrySet())
		{
			list.add(new NameValuePair(entry.getKey(), entry.getValue()));
		}

		return list;
	}

	@Override
	public Object clone()
	{
		NameValuePairMap copy = new NameValuePairMap();

		for (Entry<ILexNameToken, Value> entry : this.entrySet())
		{
			copy.put(entry.getKey(), (Value) entry.getValue().clone());
		}

		return copy;
	}

	@Override
	public Value get(Object name)
	{
		Value rv = super.get(name);

		if (rv == null)
		{
			for (ILexNameToken var : keySet())
			{
				if (HackLexNameToken.isEqual(var, name))
				{
					rv = super.get(var);
					break;
				}
			}
		}

		return rv;
	}

}
