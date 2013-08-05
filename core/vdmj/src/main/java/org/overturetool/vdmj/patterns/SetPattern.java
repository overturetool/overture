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
import java.util.Vector;

import org.overturetool.vdmj.definitions.DefinitionList;
import org.overturetool.vdmj.expressions.Expression;
import org.overturetool.vdmj.expressions.ExpressionList;
import org.overturetool.vdmj.expressions.SetEnumExpression;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.lex.LexNameList;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.PatternMatchException;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.traces.Permutor;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.typechecker.TypeCheckException;
import org.overturetool.vdmj.types.SetType;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.UnknownType;
import org.overturetool.vdmj.values.NameValuePair;
import org.overturetool.vdmj.values.NameValuePairList;
import org.overturetool.vdmj.values.NameValuePairMap;
import org.overturetool.vdmj.values.Value;
import org.overturetool.vdmj.values.ValueSet;


public class SetPattern extends Pattern
{
	private static final long serialVersionUID = 1L;
	public final PatternList plist;

	public SetPattern(LexLocation location, PatternList set)
	{
		super(location);
		this.plist = set;
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
		return "{" + plist.toString() + "}";
	}

	@Override
	public Expression getMatchingExpression()
	{
		ExpressionList list = new ExpressionList();

		for (Pattern p: plist)
		{
			list.add(p.getMatchingExpression());
		}

		return new SetEnumExpression(location, list);
	}

	@Override
	public int getLength()
	{
		return plist.size();
	}

	@Override
	public DefinitionList getAllDefinitions(Type type, NameScope scope)
	{
		DefinitionList defs = new DefinitionList();

		if (!type.isSet())
		{
			report(3204, "Set pattern is not matched against set type");
			detail("Actual type", type);
		}
		else
		{
			SetType set = type.getSet();

			if (!set.empty)
			{
        		for (Pattern p: plist)
        		{
        			defs.addAll(p.getAllDefinitions(set.setof, scope));
        		}
			}
		}

		return defs;
	}

	@Override
	public LexNameList getAllVariableNames()
	{
		LexNameList list = new LexNameList();

		for (Pattern p: plist)
		{
			list.addAll(p.getAllVariableNames());
		}

		return list;
	}

	@Override
	public List<NameValuePairList> getAllNamedValues(Value expval, Context ctxt)
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

		if (values.size() != plist.size())
		{
			patternFail(4119, "Wrong number of elements for set pattern");
		}

		// Since the member patterns may indicate specific set members, we
		// have to permute through the various set orderings to see
		// whether there are any which match both sides. If the members
		// are not constrained however, the initial ordering will be
		// fine.

		List<ValueSet> allSets;

		if (isConstrained())
		{
			allSets = values.permutedSets();
		}
		else
		{
			allSets = new Vector<ValueSet>();
			allSets.add(values);
		}

		List<NameValuePairList> finalResults = new Vector<NameValuePairList>();
		int psize = plist.size();

		if (plist.isEmpty())
		{
			finalResults.add(new NameValuePairList());
			return finalResults;
		}

		for (ValueSet setPerm: allSets)
		{
			Iterator<Value> iter = setPerm.iterator();

			List<List<NameValuePairList>> nvplists = new Vector<List<NameValuePairList>>();
			int[] counts = new int[psize];
			int i = 0;

			try
			{
				for (Pattern p: plist)
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
									patternFail(4120, "Values do not match set pattern");
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
			patternFail(4121, "Cannot match set pattern");
		}

		return finalResults;
	}

	@Override
	public Type getPossibleType()
	{
		return new SetType(location, new UnknownType(location));
	}

	@Override
	public boolean isConstrained()
	{
		if (plist.getPossibleType(location).isUnion())
		{
			return true;	// Set types are various, so we must permute
		}

		return plist.isConstrained();
	}

	@Override
	public boolean isSimple()
	{
		return plist.isSimple();
	}

	@Override
	public List<IdentifierPattern> findIdentifiers()
	{
		List<IdentifierPattern> list = new Vector<IdentifierPattern>();

		for (Pattern p: plist)
		{
			list.addAll(p.findIdentifiers());
		}

		return list;
	}
}
