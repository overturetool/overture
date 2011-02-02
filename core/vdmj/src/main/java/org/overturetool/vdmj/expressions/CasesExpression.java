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

import java.util.List;

import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.patterns.IgnorePattern;
import org.overturetool.vdmj.pog.CasesExhaustiveObligation;
import org.overturetool.vdmj.pog.POContextStack;
import org.overturetool.vdmj.pog.ProofObligationList;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.TypeList;
import org.overturetool.vdmj.types.TypeSet;
import org.overturetool.vdmj.util.Utils;
import org.overturetool.vdmj.values.Value;
import org.overturetool.vdmj.values.ValueList;


public class CasesExpression extends Expression
{
	private static final long serialVersionUID = 1L;
	public final Expression exp;
	public final List<CaseAlternative> cases;
	public final Expression others;
	public Type expType = null;

	public CasesExpression(LexLocation location, Expression exp,
					List<CaseAlternative> cases, Expression others)
	{
		super(location);
		this.exp = exp;
		this.cases = cases;
		this.others = others;
	}

	@Override
	public String toString()
	{
		return "(cases " + exp + " :\n" +
			Utils.listToString("", cases, ",\n", "") +
			(others == null ? "\n" : "others " + others + "\n") + "end)";
	}

	@Override
	public Type typeCheck(Environment env, TypeList qualifiers, NameScope scope)
	{
		expType = exp.typeCheck(env, null, scope);
		TypeSet rtypes = new TypeSet();

		for (CaseAlternative c: cases)
		{
			rtypes.add(c.typeCheck(env, scope, expType));
		}

		if (others != null)
		{
			rtypes.add(others.typeCheck(env, null, scope));
		}

		return rtypes.getType(location);
	}

	@Override
	public Expression findExpression(int lineno)
	{
		Expression found = super.findExpression(lineno);
		if (found != null) return found;

		found = exp.findExpression(lineno);
		if (found != null) return found;

		for (CaseAlternative c: cases)
		{
			found = c.result.findExpression(lineno);
			if (found != null) break;
		}

		return found != null ? found :
				others != null ? others.findExpression(lineno) : null;
	}

	@Override
	public Value eval(Context ctxt)
	{
		breakpoint.check(location, ctxt);

		Value val = exp.eval(ctxt);

		for (CaseAlternative c: cases)
		{
			Value rv = c.eval(val, ctxt);
			if (rv != null) return rv;
		}

		if (others != null)
		{
			return others.eval(ctxt);
		}

		return abort(4004, "No cases apply for " + val, ctxt);
	}

	@Override
	public ProofObligationList getProofObligations(POContextStack ctxt)
	{
		ProofObligationList obligations = new ProofObligationList();

		int count = 0;
		boolean hasIgnore = false;

		for (CaseAlternative alt: cases)
		{
			if (alt.pattern instanceof IgnorePattern)
			{
				hasIgnore = true;
			}

			// PONotCaseContext pushed by the CaseAlternative...
			obligations.addAll(alt.getProofObligations(ctxt, expType));
			count++;
		}

		if (others != null)
		{
			obligations.addAll(others.getProofObligations(ctxt));
		}

		for (int i=0; i<count; i++)
		{
			ctxt.pop();
		}

		if (others == null && !hasIgnore)
		{
			obligations.add(new CasesExhaustiveObligation(this, ctxt));
		}

		return obligations;
	}

	@Override
	public String kind()
	{
		return "cases";
	}

	@Override
	public ValueList getValues(Context ctxt)
	{
		ValueList list = exp.getValues(ctxt);

		for (CaseAlternative c: cases)
		{
			list.addAll(c.getValues(ctxt));
		}

		if (others != null)
		{
			list.addAll(others.getValues(ctxt));
		}

		return list;
	}
}
