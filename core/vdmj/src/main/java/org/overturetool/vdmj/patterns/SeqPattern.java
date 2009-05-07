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

import java.util.ListIterator;

import org.overturetool.vdmj.definitions.DefinitionList;
import org.overturetool.vdmj.expressions.Expression;
import org.overturetool.vdmj.expressions.ExpressionList;
import org.overturetool.vdmj.expressions.SeqEnumExpression;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.lex.LexNameList;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.PatternMatchException;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.typechecker.TypeCheckException;
import org.overturetool.vdmj.types.SeqType;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.UnknownType;
import org.overturetool.vdmj.values.NameValuePair;
import org.overturetool.vdmj.values.NameValuePairList;
import org.overturetool.vdmj.values.NameValuePairMap;
import org.overturetool.vdmj.values.Value;
import org.overturetool.vdmj.values.ValueList;


public class SeqPattern extends Pattern
{
	private static final long serialVersionUID = 1L;
	public final PatternList plist;

	public SeqPattern(LexLocation location, PatternList list)
	{
		super(location);
		this.plist = list;
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
	public String toString()
	{
		return "[" + plist.toString() + "]";
	}

	@Override
	public Expression getMatchingExpression()
	{
		ExpressionList list = new ExpressionList();
		
		for (Pattern p: plist)
		{
			list.add(p.getMatchingExpression());
		}
		
		return new SeqEnumExpression(location, list);
	}

	@Override
	public int getLength()
	{
		return plist.size();
	}

	@Override
	public DefinitionList getDefinitions(Type type, NameScope scope)
	{
		DefinitionList defs = new DefinitionList();

		if (!type.isSeq())
		{
			report(3203, "Sequence pattern is matched against " + type);
		}
		else
		{
			Type elem = type.getSeq().seqof;

    		for (Pattern p: plist)
    		{
    			defs.addAll(p.getDefinitions(elem, scope));
    		}
		}

		return defs;
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
	public NameValuePairList getNamedValues(Value expval, Context ctxt)
		throws PatternMatchException
	{
		ValueList values = null;

		try
		{
			values = expval.seqValue(ctxt);
		}
		catch (ValueException e)
		{
			patternFail(e);
		}

		if (values.size() != plist.size())
		{
			patternFail(4117, "Wrong number of elements for sequence pattern");
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
						patternFail(4118, "Values do not match sequence pattern");
					}
				}
			}
		}

		return results.asList();
	}

	@Override
	public Type getPossibleType()
	{
		return new SeqType(location, new UnknownType(location));
	}
}
