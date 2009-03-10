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
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.lex.LexNameList;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.PatternMatchException;
import org.overturetool.vdmj.runtime.ValueException;
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
	public int getLength()
	{
		return plist.size();
	}

	@Override
	public DefinitionList getDefinitions(Type type, NameScope scope)
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
        			defs.addAll(p.getDefinitions(set.setof, scope));
        		}
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

		// Since the left and right may have specific set members, we
		// have to permute through the various set orderings to see
		// whether there are any which match both sides...
		// TODO There may be a more efficient way to do this!

		List<ValueSet> allSets = values.permutedSets();

		for (ValueSet setPerm: allSets)
		{
			Iterator<Value> iter = setPerm.iterator();

			try
			{
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
								patternFail(4120, "Values do not match set pattern");
							}
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

		patternFail(4121, "Cannot match set pattern");
		return null;
	}

	@Override
	public Type getPossibleType()
	{
		return new SetType(location, new UnknownType(location));
	}
}
