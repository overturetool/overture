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
import java.util.ListIterator;
import java.util.Vector;

import org.overturetool.vdmj.definitions.DefinitionList;
import org.overturetool.vdmj.expressions.Expression;
import org.overturetool.vdmj.expressions.ExpressionList;
import org.overturetool.vdmj.expressions.TupleExpression;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.lex.LexNameList;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.PatternMatchException;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.traces.Permutor;
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
	private static final long serialVersionUID = 1L;
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
	public Expression getMatchingExpression()
	{
		ExpressionList list = new ExpressionList();

		for (Pattern p: plist)
		{
			list.add(p.getMatchingExpression());
		}

		return new TupleExpression(location, list);
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
	public DefinitionList getAllDefinitions(Type type, NameScope scope)
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
			defs.addAll(p.getAllDefinitions(ti.next(), scope));
		}

		return defs;
	}

	@Override
	public List<NameValuePairList> getAllNamedValues(Value expval, Context ctxt)
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
		List<List<NameValuePairList>> nvplists = new Vector<List<NameValuePairList>>();
		int psize = plist.size();
		int[] counts = new int[psize];
		int i = 0;

		for (Pattern p: plist)
		{
			List<NameValuePairList> pnvps = p.getAllNamedValues(iter.next(), ctxt);
			nvplists.add(pnvps);
			counts[i++] = pnvps.size();
		}

		Permutor permutor = new Permutor(counts);
		List<NameValuePairList> finalResults = new Vector<NameValuePairList>();

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
								patternFail(4124, "Values do not match tuple pattern");
							}
						}
					}
				}

				finalResults.add(results.asList());		// Consistent set of nvps
			}
			catch (PatternMatchException pme)
			{
				// try next perm
			}
		}

		if (finalResults.isEmpty())
		{
			patternFail(4124, "Values do not match tuple pattern");
		}

		return finalResults;
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
	public Type getPossibleType()
	{
		TypeList list = new TypeList();

		for (Pattern p: plist)
		{
			list.add(p.getPossibleType());
		}

		return list.getType(location);
	}

	@Override
	public boolean isConstrained()
	{
		return plist.isConstrained();
	}

	@Override
	public boolean isSimple()
	{
		return plist.isSimple();
	}

	@Override
	public boolean alwaysMatches()
	{
		return plist.alwaysMatches();
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
