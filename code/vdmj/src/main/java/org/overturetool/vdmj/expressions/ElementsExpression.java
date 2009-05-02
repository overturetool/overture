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

package org.overturetool.vdmj.expressions;

import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.pog.POContextStack;
import org.overturetool.vdmj.pog.ProofObligationList;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.types.SeqType;
import org.overturetool.vdmj.types.SetType;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.TypeList;
import org.overturetool.vdmj.types.UnknownType;
import org.overturetool.vdmj.values.SetValue;
import org.overturetool.vdmj.values.Value;
import org.overturetool.vdmj.values.ValueList;
import org.overturetool.vdmj.values.ValueSet;

public class ElementsExpression extends SetExpression
{
	private static final long serialVersionUID = 1L;
	public final Expression exp;

	public ElementsExpression(LexLocation location, Expression exp)
	{
		super(location);
		this.exp = exp;
	}

	@Override
	public String toString()
	{
		return "(elems " + exp + ")";
	}

	@Override
	public Type typeCheck(Environment env, TypeList qualifiers, NameScope scope)
	{
		Type arg = exp.typeCheck(env, null, scope);

		if (!arg.isSeq())
		{
			report(3085, "Argument of 'elems' is not a sequence");
			return new SetType(location, new UnknownType(location));
		}

		SeqType seq = arg.getSeq();
		return seq.empty ? new SetType(location) : new SetType(location, seq.seqof);
	}

	@Override
	public Expression findExpression(int lineno)
	{
		Expression found = super.findExpression(lineno);
		if (found != null) return found;

		return exp.findExpression(lineno);
	}

	@Override
	public Value eval(Context ctxt)
	{
		breakpoint.check(location, ctxt);

		try
		{
    		ValueList seq = exp.eval(ctxt).seqValue(ctxt);
    		ValueSet set = new ValueSet();
    		set.addAll(seq);
    		return new SetValue(set);
        }
        catch (ValueException e)
        {
        	return abort(e);
        }
	}

	@Override
	public ProofObligationList getProofObligations(POContextStack ctxt)
	{
		return exp.getProofObligations(ctxt);
	}

	@Override
	public String kind()
	{
		return "elems";
	}
}
