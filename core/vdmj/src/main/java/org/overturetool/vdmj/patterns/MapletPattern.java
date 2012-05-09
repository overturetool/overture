/*******************************************************************************
 *
 *	Copyright (c) 2012 Fujitsu Services Ltd.
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

import java.io.Serializable;
import java.util.List;
import java.util.Map.Entry;
import java.util.Vector;

import org.overturetool.vdmj.definitions.DefinitionList;
import org.overturetool.vdmj.expressions.MapletExpression;
import org.overturetool.vdmj.lex.LexKeywordToken;
import org.overturetool.vdmj.lex.LexNameList;
import org.overturetool.vdmj.lex.LexToken;
import org.overturetool.vdmj.lex.Token;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.PatternMatchException;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.typechecker.TypeCheckException;
import org.overturetool.vdmj.types.MapType;
import org.overturetool.vdmj.values.NameValuePairList;
import org.overturetool.vdmj.values.Value;


public class MapletPattern implements Serializable
{
	private static final long serialVersionUID = 1L;
	public final Pattern from;
	public final Pattern to;
	private boolean resolved = false;

	public MapletPattern(Pattern from, Pattern to)
	{
		this.from = from;
		this.to = to;
	}

	public void unResolve()
	{
		from.unResolve();
		to.unResolve();
		resolved = false;
	}

	public void typeResolve(Environment env)
	{
		if (resolved) return; else { resolved = true; }

		try
		{
			from.typeResolve(env);
			to.typeResolve(env);
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
		return from + " |-> " + to;
	}

	public MapletExpression getMapletExpression()
	{
		LexToken op = new LexKeywordToken(Token.MAPLET, from.location);
		return new MapletExpression(from.getMatchingExpression(), op, to.getMatchingExpression());
	}

	public DefinitionList getDefinitions(MapType map, NameScope scope)
	{
		DefinitionList list = new DefinitionList();
		list.addAll(from.getAllDefinitions(map.from, scope));
		list.addAll(to.getAllDefinitions(map.to, scope));
		return list;
	}

	public LexNameList getVariableNames()
	{
		LexNameList list = new LexNameList();

		list.addAll(from.getAllVariableNames());
		list.addAll(to.getAllVariableNames());

		return list;
	}

	public List<IdentifierPattern> findIdentifiers()
	{
		List<IdentifierPattern> list = new Vector<IdentifierPattern>();

		list.addAll(from.findIdentifiers());
		list.addAll(to.findIdentifiers());

		return list;
	}

	public List<NameValuePairList> getAllNamedValues(Entry<Value, Value> maplet, Context ctxt)
		throws PatternMatchException
	{
		List<NameValuePairList> flist = from.getAllNamedValues(maplet.getKey(), ctxt);
		List<NameValuePairList> tlist = to.getAllNamedValues(maplet.getValue(), ctxt);
		List<NameValuePairList> results = new Vector<NameValuePairList>();

		for (NameValuePairList f: flist)
		{
			for (NameValuePairList t: tlist)
			{
				NameValuePairList both = new NameValuePairList();
				both.addAll(f);
				both.addAll(t);
				results.add(both);	// Every combination of from/to mappings
			}
		}

		return results;
	}

	public boolean isConstrained()
	{
		if (from.isConstrained() || to.isConstrained())
		{
			return true;
		}

		return (from.getPossibleType().isUnion() || to.getPossibleType().isUnion());
	}
}
