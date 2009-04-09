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
import java.util.List;

import org.overturetool.vdmj.definitions.DefinitionList;
import org.overturetool.vdmj.expressions.Expression;
import org.overturetool.vdmj.expressions.SetUnionExpression;
import org.overturetool.vdmj.lex.LexKeywordToken;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.lex.LexNameList;
import org.overturetool.vdmj.lex.LexToken;
import org.overturetool.vdmj.lex.Token;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.PatternMatchException;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.typechecker.TypeCheckException;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.TypeSet;
import org.overturetool.vdmj.values.NameValuePair;
import org.overturetool.vdmj.values.NameValuePairList;
import org.overturetool.vdmj.values.NameValuePairMap;
import org.overturetool.vdmj.values.SetValue;
import org.overturetool.vdmj.values.Value;
import org.overturetool.vdmj.values.ValueSet;


public class UnionPattern extends Pattern
{
	private static final long serialVersionUID = 1L;
	public final Pattern left;
	public final Pattern right;

	public UnionPattern(Pattern left, LexLocation location, Pattern right)
	{
		super(location);
		this.left = left;
		this.right = right;
	}

	@Override
	public void unResolve()
	{
		left.unResolve();
		right.unResolve();
		resolved = false;
	}

	@Override
	public void typeResolve(Environment env)
	{
		if (resolved) return; else { resolved = true; }

		try
		{
			left.typeResolve(env);
			right.typeResolve(env);
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
		return left + " union " + right;
	}

	@Override
	public Expression getMatchingExpression()
	{
		LexToken op = new LexKeywordToken(Token.UNION, location);
		return new SetUnionExpression(
			left.getMatchingExpression(), op, right.getMatchingExpression());
	}

	@Override
	public int getLength()
	{
		return left.getLength() + right.getLength();
	}

	@Override
	public DefinitionList getDefinitions(Type type, NameScope scope)
	{
		DefinitionList defs = new DefinitionList();

		if (!type.isSet())
		{
			report(3206, "Matching expression is not a set type");
		}

		defs.addAll(left.getDefinitions(type, scope));
		defs.addAll(right.getDefinitions(type, scope));

		return defs;
	}

	@Override
	public LexNameList getVariableNames()
	{
		LexNameList list = new LexNameList();

		list.addAll(left.getVariableNames());
		list.addAll(right.getVariableNames());

		return list;
	}

	@Override
	public NameValuePairList getNamedValues(Value expval, Context ctxt)
		throws PatternMatchException
	{
		ValueSet values = null;

		try
		{
			values = expval.setValue(ctxt);
		}
		catch (ValueException e)
		{
			patternFail(e);
		}

		int llen = left.getLength();
		int rlen = right.getLength();
		int size = values.size();

		if ((llen == 0 && rlen > size) ||
			(rlen == 0 && llen > size) ||
			(rlen > 0 && llen > 0 && size != llen + rlen))
		{
			patternFail(4125, "Set union pattern does not match expression");
		}

		if (llen == 0)
		{
			if (rlen == 0)
			{
				// Divide size roughly between l/r
				llen = size/2;
				rlen = size - llen;
			}
			else
			{
				// Take rlen from size and give to llen
				llen = size - rlen;
			}
		}
		else
		{
			if (rlen == 0)
			{
				// Take llen from size and give to rlen
				rlen = size - llen;
			}
		}

		assert llen + rlen == size : "Pattern match internal error";

		// Since the left and right may have specific set members, we
		// have to permute through the various set orderings to see
		// whether there are any which match both sides...
		// TODO There may be a more efficient way to do this!

		List<ValueSet> allSets = values.permutedSets();

		for (ValueSet setPerm: allSets)
		{
			Iterator<Value> iter = setPerm.iterator();
			ValueSet first = new ValueSet();

			for (int i=0; i<llen; i++)
			{
				first.add(iter.next());
			}

			ValueSet second = new ValueSet();

			while (iter.hasNext())	// Everything else in second
			{
				second.add(iter.next());
			}

			try
			{
				NameValuePairList matches = new NameValuePairList();
				matches.addAll(left.getNamedValues(new SetValue(first), ctxt));
				matches.addAll(right.getNamedValues(new SetValue(second), ctxt));
				NameValuePairMap results = new NameValuePairMap();

				for (NameValuePair nvp: matches)
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
							patternFail(4126, "Values do not match union pattern");
						}
					}
				}

				return results.asList();
			}
			catch (PatternMatchException pme)
			{
				// Try next perm then...
			}
		}

		patternFail(4127, "Cannot match set pattern");
		return null;
	}

	@Override
	public Type getPossibleType()
	{
		TypeSet list = new TypeSet();

		list.add(left.getPossibleType());
		list.add(right.getPossibleType());

		return list.getType(location);
	}
}
