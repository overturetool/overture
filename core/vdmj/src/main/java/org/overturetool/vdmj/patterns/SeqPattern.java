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

import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

import org.overturetool.vdmj.definitions.DefinitionList;
import org.overturetool.vdmj.expressions.Expression;
import org.overturetool.vdmj.expressions.ExpressionList;
import org.overturetool.vdmj.expressions.SeqEnumExpression;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.lex.LexNameList;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.PatternMatchException;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.traces.Permutor;
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
	public DefinitionList getAllDefinitions(Type type, NameScope scope)
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
    			defs.addAll(p.getAllDefinitions(elem, scope));
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

		if (plist.isEmpty())
		{
			finalResults.add(new NameValuePairList());
			return finalResults;
		}

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
								patternFail(4118, "Values do not match sequence pattern");
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
			patternFail(4118, "Values do not match sequence pattern");
		}

		return finalResults;
	}

	@Override
	public Type getPossibleType()
	{
		return new SeqType(location, new UnknownType(location));
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
