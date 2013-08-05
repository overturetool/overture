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

import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Vector;

import org.overturetool.vdmj.definitions.DefinitionList;
import org.overturetool.vdmj.expressions.Expression;
import org.overturetool.vdmj.expressions.MapUnionExpression;
import org.overturetool.vdmj.lex.LexKeywordToken;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.lex.LexNameList;
import org.overturetool.vdmj.lex.LexToken;
import org.overturetool.vdmj.lex.Token;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.PatternMatchException;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.traces.Permutor;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.typechecker.TypeCheckException;
import org.overturetool.vdmj.types.MapType;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.TypeSet;
import org.overturetool.vdmj.types.UnknownType;
import org.overturetool.vdmj.values.MapValue;
import org.overturetool.vdmj.values.NameValuePair;
import org.overturetool.vdmj.values.NameValuePairList;
import org.overturetool.vdmj.values.NameValuePairMap;
import org.overturetool.vdmj.values.Value;
import org.overturetool.vdmj.values.ValueMap;


public class MapUnionPattern extends Pattern
{
	private static final long serialVersionUID = 1L;
	public final Pattern left;
	public final Pattern right;

	public MapUnionPattern(Pattern left, LexLocation location, Pattern right)
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
		LexToken op = new LexKeywordToken(Token.MUNION, location);
		return new MapUnionExpression(
			left.getMatchingExpression(), op, right.getMatchingExpression());
	}

	@Override
	public int getLength()
	{
		int llen = left.getLength();
		int rlen = right.getLength();
		return (llen == ANY || rlen == ANY) ? ANY : llen + rlen;
	}

	@Override
	public DefinitionList getAllDefinitions(Type type, NameScope scope)
	{
		DefinitionList defs = new DefinitionList();

		if (!type.isMap())
		{
			report(3315, "Matching expression is not a map type");
		}

		defs.addAll(left.getAllDefinitions(type, scope));
		defs.addAll(right.getAllDefinitions(type, scope));

		return defs;
	}

	@Override
	public LexNameList getAllVariableNames()
	{
		LexNameList list = new LexNameList();

		list.addAll(left.getAllVariableNames());
		list.addAll(right.getAllVariableNames());

		return list;
	}

	@Override
	public List<NameValuePairList> getAllNamedValues(Value expval, Context ctxt)
		throws PatternMatchException
	{
		ValueMap values = null;

		try
		{
			values = expval.mapValue(ctxt);
		}
		catch (ValueException e)
		{
			patternFail(e);
		}

		int llen = left.getLength();
		int rlen = right.getLength();
		int size = values.size();

		if ((llen == ANY && rlen > size) ||
			(rlen == ANY && llen > size) ||
			(rlen != ANY && llen != ANY && size != llen + rlen))
		{
			patternFail(4155, "Map union pattern does not match expression");
		}

		// If the left and right sizes are zero (ie. flexible) then we have to
		// generate a set of splits of the values, and offer these to sub-matches
		// to see whether they fit. Otherwise, there is just one split at this level.

		List<Integer> leftSizes = new Vector<Integer>();

		if (llen == ANY)
		{
			if (rlen == ANY)
			{
				if (size == 0)
				{
					// Can't match a munion b with {|->}
				}
				else if (size % 2 == 1)
				{
					// Odd => add the middle, then those either side
					int half = size/2 + 1;
					if (half > 0) leftSizes.add(half);

					for (int delta=1; half - delta > 0; delta++)
					{
						leftSizes.add(half + delta);
						leftSizes.add(half - delta);
					}

					leftSizes.add(0);
				}
				else
				{
					// Even => add those either side of the middle
					int half = size/2;
					if (half > 0) leftSizes.add(half);

					for (int delta=1; half - delta > 0; delta++)
					{
						leftSizes.add(half + delta);
						leftSizes.add(half - delta);
					}
					
					leftSizes.add(size);
					leftSizes.add(0);
				}
			}
			else
			{
				leftSizes.add(size - rlen);
			}
		}
		else
		{
			leftSizes.add(llen);
		}

		// Since the left and right may have specific element members, we
		// have to permute through the various map orderings to see
		// whether there are any which match both sides. If the patterns
		// are not constrained however, the initial ordering will be
		// fine.

		List<ValueMap> allMaps;

		if (isConstrained())
		{
			allMaps = values.permutedMaps();
		}
		else
		{
			allMaps = new Vector<ValueMap>();
			allMaps.add(values);
		}

		// Now loop through the various splits and attempt to match the l/r
		// sub-patterns to the split map value.

		List<NameValuePairList> finalResults = new Vector<NameValuePairList>();

		for (Integer lsize: leftSizes)
		{
			for (ValueMap setPerm: allMaps)
			{
				Iterator<Entry<Value, Value>> iter = setPerm.entrySet().iterator();
				ValueMap first = new ValueMap();

				for (int i=0; i<lsize; i++)
				{
					Entry<Value, Value> e = iter.next();
					first.put(e.getKey(), e.getValue());
				}

				ValueMap second = new ValueMap();

				while (iter.hasNext())	// Everything else in second
				{
					Entry<Value, Value> e = iter.next();
					second.put(e.getKey(), e.getValue());
				}

				List<List<NameValuePairList>> nvplists = new Vector<List<NameValuePairList>>();
				int psize = 2;
				int[] counts = new int[psize];

				try
				{
					List<NameValuePairList> lnvps = left.getAllNamedValues(new MapValue(first), ctxt);
					nvplists.add(lnvps);
					counts[0] = lnvps.size();

					List<NameValuePairList> rnvps = right.getAllNamedValues(new MapValue(second), ctxt);
					nvplists.add(rnvps);
					counts[1] = rnvps.size();
				}
				catch (Exception e)
				{
					continue;
				}

				Permutor permutor = new Permutor(counts);

				while (permutor.hasNext())
				{
					try
					{
						NameValuePairMap results = new NameValuePairMap();
						int[] selection = permutor.next();

						for (int p=0; p<psize; p++)
						{
							for (NameValuePair nvp: nvplists.get(p).get(selection[p]))
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
						}

						finalResults.add(results.asList());
					}
					catch (PatternMatchException pme)
					{
						// Try next perm then...
					}
				}
			}
		}

		if (finalResults.isEmpty())
		{
			patternFail(4156, "Cannot match map pattern");
		}

		return finalResults;
	}

	@Override
	public Type getPossibleType()
	{
		TypeSet list = new TypeSet();

		list.add(left.getPossibleType());
		list.add(right.getPossibleType());

		Type s = list.getType(location);

		return s.isUnknown() ?
			new MapType(location, new UnknownType(location), new UnknownType(location)) : s;
	}

	@Override
	public boolean isConstrained()
	{
		return left.isConstrained() || right.isConstrained();
	}

	@Override
	public boolean isSimple()
	{
		return left.isSimple() && right.isSimple();
	}

	@Override
	public List<IdentifierPattern> findIdentifiers()
	{
		List<IdentifierPattern> list = new Vector<IdentifierPattern>();
		list.addAll(left.findIdentifiers());
		list.addAll(right.findIdentifiers());
		return list;
	}
}
