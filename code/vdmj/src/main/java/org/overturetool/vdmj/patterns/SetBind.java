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
import java.util.Vector;

import org.overturetool.vdmj.expressions.Expression;
import org.overturetool.vdmj.pog.POContextStack;
import org.overturetool.vdmj.pog.ProofObligationList;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.values.SetValue;
import org.overturetool.vdmj.values.Value;
import org.overturetool.vdmj.values.ValueList;
import org.overturetool.vdmj.values.ValueSet;


public class SetBind extends Bind
{
	public final Expression set;

	public SetBind(Pattern pattern, Expression set)
	{
		super(pattern.location, pattern);
		this.set = set;
	}

	@Override
	public List<MultipleBind> getMultipleBindList()
	{
		PatternList plist = new PatternList();
		plist.add(pattern);
		List<MultipleBind> mblist = new Vector<MultipleBind>();
		mblist.add(new MultipleSetBind(plist, set));
		return mblist;
	}

	@Override
	public String toString()
	{
		return pattern + " in set " + set;
	}

	@Override
	public ValueList getBindValues(Context ctxt)
	{
		try
		{
			ValueList results = new ValueList();
			ValueSet elements = set.eval(ctxt).setValue(ctxt).sorted();

			for (Value e: elements)
			{
				e = e.deref();

				if (e instanceof SetValue)
				{
					SetValue sv = (SetValue)e;
					results.addAll(sv.permutedSets());
				}
				else
				{
					results.add(e);
				}
			}

			return results;
		}
		catch (ValueException ex)
		{
			abort(ex);
			return null;
		}
	}

	@Override
	public ProofObligationList getProofObligations(POContextStack ctxt)
	{
		return set.getProofObligations(ctxt);
	}
}
