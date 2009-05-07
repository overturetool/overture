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

import org.overturetool.vdmj.expressions.Expression;
import org.overturetool.vdmj.pog.POContextStack;
import org.overturetool.vdmj.pog.ProofObligationList;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.typechecker.TypeComparator;
import org.overturetool.vdmj.types.SetType;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.UnknownType;
import org.overturetool.vdmj.values.SetValue;
import org.overturetool.vdmj.values.Value;
import org.overturetool.vdmj.values.ValueList;
import org.overturetool.vdmj.values.ValueSet;

public class MultipleSetBind extends MultipleBind
{
	private static final long serialVersionUID = 1L;
	public final Expression set;

	public MultipleSetBind(PatternList plist, Expression set)
	{
		super(plist);
		this.set = set;
	}

	@Override
	public String toString()
	{
		return plist + " in set " + set;
	}

	@Override
	public Type typeCheck(Environment base, NameScope scope)
	{
		plist.typeResolve(base);
		Type type = set.typeCheck(base, null, scope);
		Type result = new UnknownType(location);

		if (!type.isSet())
		{
			set.report(3197, "Expression matching set bind is not a set");
			set.detail("Actual type", type);
		}
		else
		{
			SetType st = type.getSet();

			if (!st.empty)
			{
				result = st.setof;
				Type ptype = getPossibleType();

				if (!TypeComparator.compatible(ptype, result))
				{
					set.report(3264, "At least one bind cannot match set");
					set.detail2("Binds", ptype, "Set of", st);
				}
			}
			else
			{
				set.warning(3264, "Empty set used in bind");
			}
		}

		return result;
	}

	@Override
	public ValueList getBindValues(Context ctxt)
	{
		try
		{
			ValueList vl = new ValueList();
			ValueSet vs = set.eval(ctxt).setValue(ctxt).sorted();

			for (Value v: vs)
			{
				v = v.deref();

				if (v instanceof SetValue)
				{
					SetValue sv = (SetValue)v;
					vl.addAll(sv.permutedSets());
				}
				else
				{
					vl.add(v);
				}
			}

			return vl;
		}
		catch (ValueException e)
		{
			abort(e);
			return null;
		}
	}

	@Override
	public ProofObligationList getProofObligations(POContextStack ctxt)
	{
		return set.getProofObligations(ctxt);
	}
}
