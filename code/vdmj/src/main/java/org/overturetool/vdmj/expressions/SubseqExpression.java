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

import org.overturetool.vdmj.pog.POContextStack;
import org.overturetool.vdmj.pog.ProofObligationList;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.TypeList;
import org.overturetool.vdmj.values.SeqValue;
import org.overturetool.vdmj.values.Value;
import org.overturetool.vdmj.values.ValueList;

public class SubseqExpression extends Expression
{
	private static final long serialVersionUID = 1L;
	public final Expression seq;
	public final Expression from;
	public final Expression to;

	public Type ftype = null;
	public Type ttype = null;

	public SubseqExpression(Expression seq, Expression from, Expression to)
	{
		super(seq);
		this.seq = seq;
		this.from = from;
		this.to = to;
	}

	@Override
	public String toString()
	{
		return "(" + seq + "(" + from + ", ... ," + to + "))";
	}

	@Override
	public Type typeCheck(Environment env, TypeList qualifiers, NameScope scope)
	{
		Type stype = seq.typeCheck(env, null, scope);
		ftype = from.typeCheck(env, null, scope);
		ttype = to.typeCheck(env, null, scope);

		if (!stype.isSeq())
		{
			report(3174, "Subsequence is not of a sequence type");
		}

		if (!ftype.isNumeric())
		{
			report(3175, "Subsequence range start is not a number");
		}

		if (!ttype.isNumeric())
		{
			report(3176, "Subsequence range end is not a number");
		}

		return stype;
	}

	@Override
	public Value eval(Context ctxt)
	{
		breakpoint.check(location, ctxt);

		try
		{
    		ValueList list = seq.eval(ctxt).seqValue(ctxt);
    		double fr = from.eval(ctxt).realValue(ctxt);
    		double tr = to.eval(ctxt).realValue(ctxt);
    		int fi = (int)Math.ceil(fr);
    		int ti = (int)Math.floor(tr);

    		if (fi < 1)
    		{
    			fi = 1;
    		}

    		if (ti > list.size())
    		{
    			ti = list.size();
    		}

    		ValueList result = new ValueList();

    		if (fi <= ti)
    		{
        		result.addAll(list.subList(fi-1, ti));
    		}

    		return new SeqValue(result);
		}
		catch (ValueException e)
		{
			return abort(e);
		}
	}

	@Override
	public Expression findExpression(int lineno)
	{
		Expression found = super.findExpression(lineno);
		if (found != null) return found;

		found = seq.findExpression(lineno);
		if (found != null) return found;

		found = from.findExpression(lineno);
		if (found != null) return found;

		found = to.findExpression(lineno);
		if (found != null) return found;

		return null;
	}

	@Override
	public ProofObligationList getProofObligations(POContextStack ctxt)
	{
		ProofObligationList list = seq.getProofObligations(ctxt);
		list.addAll(from.getProofObligations(ctxt));
		list.addAll(to.getProofObligations(ctxt));
		return list;
	}

	@Override
	public String kind()
	{
		return "subsequence";
	}
}
