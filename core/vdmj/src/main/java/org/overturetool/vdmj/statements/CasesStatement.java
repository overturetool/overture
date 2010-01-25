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

package org.overturetool.vdmj.statements;

import java.util.List;

import org.overturetool.vdmj.expressions.Expression;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.patterns.IgnorePattern;
import org.overturetool.vdmj.pog.POContextStack;
import org.overturetool.vdmj.pog.ProofObligationList;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.TypeSet;
import org.overturetool.vdmj.values.Value;
import org.overturetool.vdmj.values.VoidValue;


public class CasesStatement extends Statement
{
	private static final long serialVersionUID = 1L;
	public final Expression exp;
	public final List<CaseStmtAlternative> cases;
	public final Statement others;

	public Type expType = null;

	public CasesStatement(LexLocation location,
		Expression exp, List<CaseStmtAlternative> cases, Statement others)
	{
		super(location);
		this.exp = exp;
		this.cases = cases;
		this.others = others;
	}

	@Override
	public String kind()
	{
		return "cases";
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("cases " + exp + " :\n");

		for (CaseStmtAlternative csa: cases)
		{
			sb.append("  ");
			sb.append(csa.toString());
		}

		if (others != null)
		{
			sb.append("  others -> ");
			sb.append(others.toString());
		}

		sb.append("esac");
		return sb.toString();
	}

	@Override
	public Type typeCheck(Environment env, NameScope scope)
	{
		expType = exp.typeCheck(env, null, scope);
		TypeSet rtypes = new TypeSet();

		for (CaseStmtAlternative c: cases)
		{
			rtypes.add(c.typeCheck(env, scope, expType));
		}

		if (others != null)
		{
			rtypes.add(others.typeCheck(env, scope));
		}

		return rtypes.getType(location);
	}


	@Override
	public TypeSet exitCheck()
	{
		TypeSet types = new TypeSet();

		for (CaseStmtAlternative c: cases)
		{
			types.addAll(c.exitCheck());
		}

		return types;
	}

	@Override
	public Statement findStatement(int lineno)
	{
		Statement found = super.findStatement(lineno);
		if (found != null) return found;

		for (CaseStmtAlternative stmt: cases)
		{
			found = stmt.statement.findStatement(lineno);
			if (found != null) break;
		}

		return found;
	}

	@Override
	public Expression findExpression(int lineno)
	{
		Expression found = null;

		for (CaseStmtAlternative stmt: cases)
		{
			found = stmt.statement.findExpression(lineno);
			if (found != null) break;
		}

		return found;
	}

	@Override
	public Value eval(Context ctxt)
	{
		breakpoint.check(location, ctxt);

		Value val = exp.eval(ctxt);

		for (CaseStmtAlternative c: cases)
		{
			Value rv = c.eval(val, ctxt);
			if (rv != null) return rv;
		}

		if (others != null)
		{
			return others.eval(ctxt);
		}

		return new VoidValue();
	}

	@Override
	public ProofObligationList getProofObligations(POContextStack ctxt)
	{
		ProofObligationList obligations = new ProofObligationList();
		boolean hasIgnore = false;

		for (CaseStmtAlternative alt: cases)
		{
			if (alt.pattern instanceof IgnorePattern)
			{
				hasIgnore = true;
			}

			obligations.addAll(alt.getProofObligations(ctxt));
		}

		if (others != null && !hasIgnore)
		{
			obligations.addAll(others.getProofObligations(ctxt));
		}

		return obligations;
	}
}
