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
import org.overturetool.vdmj.expressions.MapEnumExpression;
import org.overturetool.vdmj.expressions.MapletExpression;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.lex.LexNameList;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.PatternMatchException;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.traces.Permutor;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.typechecker.TypeCheckException;
import org.overturetool.vdmj.types.MapType;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.UnknownType;
import org.overturetool.vdmj.util.Utils;
import org.overturetool.vdmj.values.NameValuePair;
import org.overturetool.vdmj.values.NameValuePairList;
import org.overturetool.vdmj.values.NameValuePairMap;
import org.overturetool.vdmj.values.Value;
import org.overturetool.vdmj.values.ValueMap;


public class MapPattern extends Pattern
{
	private static final long serialVersionUID = 1L;
	public final List<MapletPattern> maplets;

	public MapPattern(LexLocation location, List<MapletPattern> maplets)
	{
		super(location);
		this.maplets = maplets;
	}

	@Override
	public void unResolve()
	{
		for (MapletPattern mp: maplets)
		{
			mp.unResolve();
		}

		resolved = false;
	}

	@Override
	public void typeResolve(Environment env)
	{
		if (resolved) return; else { resolved = true; }

		try
		{
			for (MapletPattern mp: maplets)
			{
				mp.unResolve();
			}
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
		return Utils.listToString("{", maplets, ", ", "}");
	}

	@Override
	public Expression getMatchingExpression()
	{
		List<MapletExpression> list = new Vector<MapletExpression>();

		for (MapletPattern p: maplets)
		{
			list.add(p.getMapletExpression());
		}

		return new MapEnumExpression(location, list);
	}

	@Override
	public int getLength()
	{
		return maplets.size();
	}

	@Override
	public DefinitionList getAllDefinitions(Type type, NameScope scope)
	{
		DefinitionList defs = new DefinitionList();

		if (!type.isMap())
		{
			report(3314, "Map pattern is not matched against map type");
			detail("Actual type", type);
		}
		else
		{
			MapType map = type.getMap();

			if (!map.empty)
			{
        		for (MapletPattern p: maplets)
        		{
        			defs.addAll(p.getDefinitions(map, scope));
        		}
			}
		}

		return defs;
	}

	@Override
	public LexNameList getAllVariableNames()
	{
		LexNameList list = new LexNameList();

		for (MapletPattern p: maplets)
		{
			list.addAll(p.getVariableNames());
		}

		return list;
	}

	@Override
	protected List<NameValuePairList> getAllNamedValues(Value expval, Context ctxt)
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

		if (values.size() != maplets.size())
		{
			patternFail(4152, "Wrong number of elements for map pattern");
		}

		// Since the member patterns may indicate specific map members, we
		// have to permute through the various map orderings to see
		// whether there are any which match both sides. If the members
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

		List<NameValuePairList> finalResults = new Vector<NameValuePairList>();
		int psize = maplets.size();

		if (maplets.isEmpty())
		{
			finalResults.add(new NameValuePairList());
			return finalResults;
		}

		for (ValueMap mapPerm: allMaps)
		{
			Iterator<Entry<Value, Value>> iter = mapPerm.entrySet().iterator();

			List<List<NameValuePairList>> nvplists = new Vector<List<NameValuePairList>>();
			int[] counts = new int[psize];
			int i = 0;

			try
			{
				for (MapletPattern p: maplets)
				{
					List<NameValuePairList> pnvps = p.getAllNamedValues(iter.next(), ctxt);
					nvplists.add(pnvps);
					counts[i++] = pnvps.size();
				}
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
									patternFail(4153, "Values do not match map pattern");
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

		if (finalResults.isEmpty())
		{
			patternFail(4154, "Cannot match map pattern");
		}

		return finalResults;
	}

	@Override
	public Type getPossibleType()
	{
		return new MapType(location, new UnknownType(location), new UnknownType(location));
	}

	@Override
	public boolean isConstrained()
	{
		for (MapletPattern p: maplets)
		{
			if (p.isConstrained()) return true;
		}

		return false;
	}

	@Override
	public List<IdentifierPattern> findIdentifiers()
	{
		List<IdentifierPattern> list = new Vector<IdentifierPattern>();

		for (MapletPattern p: maplets)
		{
			list.addAll(p.findIdentifiers());
		}

		return list;
	}
}
