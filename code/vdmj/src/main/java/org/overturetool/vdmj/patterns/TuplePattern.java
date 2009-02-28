/*******************************************************************************
 *
 *	Copyright (c) 2008 Fujitsu Services Ltd.
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

package org.overturetool.vdmj.patterns;

import java.util.Iterator;
import java.util.ListIterator;

import org.overturetool.vdmj.definitions.DefinitionList;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.lex.LexNameList;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.PatternMatchException;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.typechecker.TypeCheckException;
import org.overturetool.vdmj.types.ProductType;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.TypeList;
import org.overturetool.vdmj.util.Utils;
import org.overturetool.vdmj.values.NameValuePair;
import org.overturetool.vdmj.values.NameValuePairList;
import org.overturetool.vdmj.values.NameValuePairMap;
import org.overturetool.vdmj.values.Value;
import org.overturetool.vdmj.values.ValueList;


public class TuplePattern extends Pattern
{
	public final PatternList plist;

	public TuplePattern(LexLocation location, PatternList list)
	{
		super(location);
		this.plist = list;
	}

	@Override
	public String toString()
	{
		return "mk_" + "(" + Utils.listToString(plist) + ")";
	}

	@Override
	public String getMatchingValue()
	{
		return "mk_" + "(" + plist.getMatchingValues() + ")";
	}

	@Override
	public int getLength()
	{
		return plist.size();
	}

	@Override
	public void unResolve()
	{
		plist.unResolve();
		resolved = false;
	}

	@Override
	public void typeResolve(Environment env)
	{
		if (resolved) return; else { resolved = true; }

		try
		{
			plist.typeResolve(env);
		}
		catch (TypeCheckException e)
		{
			unResolve();
			throw e;
		}
	}

	@Override
	public DefinitionList getDefinitions(Type type, NameScope scope)
	{
		DefinitionList defs = new DefinitionList();

		if (!type.isProduct(plist.size()))
		{
			report(3205, "Matching expression is not a product of cardinality " + plist.size());
			detail("Actual", type);
			return defs;
		}

		ProductType product = type.getProduct(plist.size());
		Iterator<Type> ti = product.types.iterator();

		for (Pattern p: plist)
		{
			defs.addAll(p.getDefinitions(ti.next(), scope));
		}

		return defs;
	}

	@Override
	public NameValuePairList getNamedValues(Value expval, Context ctxt)
		throws PatternMatchException
	{
		ValueList values = null;

		try
		{
			values = expval.tupleValue(ctxt);
		}
		catch (ValueException e)
		{
			patternFail(e);
		}

		if (values.size() != plist.size())
		{
			patternFail(4123, "Tuple expression does not match pattern");
		}

		ListIterator<Value> iter = values.listIterator();
		NameValuePairMap results = new NameValuePairMap();

		for (Pattern p: plist)
		{
			for (NameValuePair nvp: p.getNamedValues(iter.next(), ctxt))
			{
				Value v = results.get(nvp.name);

				if (v == null)
				{
					results.put(nvp);
				}
				else	// Names match, so values must also
				{
					if (!v.equals(nvp.value))
					{
						patternFail(4124, "Values do not match tuple pattern");
					}
				}
			}
		}

		return results.asList();
	}

	@Override
	public LexNameList getVariableNames()
	{
		LexNameList list = new LexNameList();

		for (Pattern p: plist)
		{
			list.addAll(p.getVariableNames());
		}

		return list;
	}

	@Override
	public Type getPossibleType()
	{
		TypeList list = new TypeList();

		for (Pattern p: plist)
		{
			list.add(p.getPossibleType());
		}

		return list.getType(location);
	}
}
