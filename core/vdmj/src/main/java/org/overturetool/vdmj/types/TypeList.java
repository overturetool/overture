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

package org.overturetool.vdmj.types;

import java.util.Vector;

import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.patterns.IdentifierPattern;
import org.overturetool.vdmj.patterns.Pattern;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.util.Utils;
import org.overturetool.vdmj.values.NameValuePair;
import org.overturetool.vdmj.values.NameValuePairList;
import org.overturetool.vdmj.values.Quantifier;
import org.overturetool.vdmj.values.QuantifierList;
import org.overturetool.vdmj.values.TupleValue;
import org.overturetool.vdmj.values.ValueList;


@SuppressWarnings("serial")
public class TypeList extends Vector<Type>
{
	public TypeList()
	{
		super();
	}

	public TypeList(Type act)
	{
		add(act);
	}

	@Override
	public boolean add(Type t)
	{
		return super.add(t);
	}

	public Type getType(LexLocation location)
	{
		Type result = null;

		if (this.size() == 1)
		{
			result = iterator().next();
		}
		else
		{
			result = new ProductType(location, this);
		}

		return result;
	}

	@Override
	public String toString()
	{
		return "(" + Utils.listToString(this) + ")";
	}
	
	public ValueList getAllValues(Context ctxt) throws ValueException
	{
		QuantifierList quantifiers = new QuantifierList();
		int n = 0;

		for (Type t: this)
		{
			LexNameToken name = new LexNameToken("#", String.valueOf(n), t.location);
			Pattern p = new IdentifierPattern(name);
			Quantifier q = new Quantifier(p, t.getAllValues(ctxt));
			quantifiers.add(q);
		}

		quantifiers.init();
		ValueList results = new ValueList();

		while (quantifiers.hasNext(ctxt))
		{
			NameValuePairList nvpl = quantifiers.next();
			ValueList list = new ValueList();

			for (NameValuePair nvp: nvpl)
			{
				list.add(nvp.value);
			}
			
			results.add(new TupleValue(list));
		}
		
		return results;
	}
}
