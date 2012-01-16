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
import org.overturetool.vdmj.expressions.SeqConcatExpression;
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
import org.overturetool.vdmj.types.SeqType;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.UnknownType;
import org.overturetool.vdmj.values.NameValuePair;
import org.overturetool.vdmj.values.NameValuePairList;
import org.overturetool.vdmj.values.NameValuePairMap;
import org.overturetool.vdmj.values.SeqValue;
import org.overturetool.vdmj.values.Value;
import org.overturetool.vdmj.values.ValueList;


public class ConcatenationPattern extends Pattern
{
	private static final long serialVersionUID = 1L;
	public final Pattern left;
	public final Pattern right;

	public ConcatenationPattern(Pattern left, LexLocation location, Pattern right)
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
		return left + " ^ " + right;
	}

	@Override
	public Expression getMatchingExpression()
	{
		LexToken op = new LexKeywordToken(Token.CONCATENATE, location);
		return new SeqConcatExpression(
			left.getMatchingExpression(), op, right.getMatchingExpression());
	}

	@Override
	public DefinitionList getDefinitions(Type type, NameScope scope)
	{
		DefinitionList list = left.getDefinitions(type, scope);
		list.addAll(right.getDefinitions(type, scope));
		return list;
	}

	@Override
	public int getLength()
	{
		int llen = left.getLength();
		int rlen = right.getLength();
		return (llen == 0 || rlen == 0) ? 0 : llen + rlen;
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
	protected List<NameValuePairList> getAllNamedValues(Value expval, Context ctxt)
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

		int llen = left.getLength();
		int rlen = right.getLength();
		int size = values.size();

		if ((size < 2) ||	// We don't allow empty sequence matches
			(llen == 0 && rlen > size) ||
			(rlen == 0 && llen > size) ||
			(rlen > 0 && llen > 0 && size != llen + rlen))
		{
			patternFail(4108, "Sequence concatenation pattern does not match expression");
		}

		// If the left and right sizes are zero (ie. flexible) then we have to
		// generate a set of splits of the values, and offer these to sub-matches
		// to see whether they fit. Otherwise, there is just one split at this level.

		List<Integer> leftSizes = new Vector<Integer>();

		if (llen == 0)
		{
			if (rlen == 0)
			{
				// Divide size roughly between l/r initially, then diverge
				int half = size/2;
				leftSizes.add(half);

				for (int delta=1; half - delta > 0; delta++)
				{
					leftSizes.add(half + delta);
					leftSizes.add(half - delta);
				}

				if (size % 2 == 1)
				{
					leftSizes.add(size-1);	// Most in left, one in right
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

		// Now loop through the various splits and attempt to match the l/r
		// sub-patterns to the split sequence value.

		List<NameValuePairList> finalResults = new Vector<NameValuePairList>();

		for (Integer lsize: leftSizes)
		{
			Iterator<Value> iter = values.iterator();
			ValueList head = new ValueList();

			for (int i=0; i<lsize; i++)
			{
				head.add(iter.next());
			}

			ValueList tail = new ValueList();

			while (iter.hasNext())	// Everything else in second
			{
				tail.add(iter.next());
			}

			List<List<NameValuePairList>> nvplists = new Vector<List<NameValuePairList>>();
			int psize = 2;
			int[] counts = new int[psize];

			try
			{
				List<NameValuePairList> lnvps = left.getAllNamedValues(new SeqValue(head), ctxt);
				nvplists.add(lnvps);
				counts[0] = lnvps.size();

				List<NameValuePairList> rnvps = right.getAllNamedValues(new SeqValue(tail), ctxt);
				nvplists.add(rnvps);
				counts[1] = rnvps.size();
			}
			catch (PatternMatchException e)
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
									patternFail(4109, "Values do not match concatenation pattern");
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
		}

		if (finalResults.isEmpty())
		{
			patternFail(4109, "Values do not match concatenation pattern");
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
		return left.isConstrained() || right.isConstrained();
	}
}
