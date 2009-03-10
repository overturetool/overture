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
import org.overturetool.vdmj.pog.PONotImpliesContext;
import org.overturetool.vdmj.pog.POContextStack;
import org.overturetool.vdmj.pog.POImpliesContext;
import org.overturetool.vdmj.pog.ProofObligationList;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.types.BooleanType;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.TypeList;
import org.overturetool.vdmj.types.TypeSet;
import org.overturetool.vdmj.values.Value;


public class IfExpression extends Expression
{
	private static final long serialVersionUID = 1L;
	public final Expression ifExp;
	public final Expression thenExp;
	public final List<ElseIfExpression> elseList;
	public final Expression elseExp;

	public IfExpression(LexLocation location,
		Expression ifExp, Expression thenExp, List<ElseIfExpression> elseList,
		Expression elseExp)
	{
		super(location);
		this.ifExp = ifExp;
		this.thenExp = thenExp;
		this.elseList = elseList;
		this.elseExp = elseExp;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("(if " + ifExp + "\nthen " + thenExp);

		for (ElseIfExpression s: elseList)
		{
			sb.append("\n");
			sb.append(s.toString());
		}

		if (elseExp != null)
		{
			sb.append("\nelse ");
			sb.append(elseExp.toString());
		}

		sb.append(")");

		return sb.toString();
	}

	@Override
	public Type typeCheck(Environment env, TypeList qualifiers, NameScope scope)
	{
		if (!ifExp.typeCheck(env, null, scope).isType(BooleanType.class))
		{
			report(3108, "If expression is not a boolean");
		}

		TypeSet rtypes = new TypeSet();
		rtypes.add(thenExp.typeCheck(env, null, scope));

		for (ElseIfExpression eie: elseList)
		{
			rtypes.add(eie.typeCheck(env, null, scope));
		}

		rtypes.add(elseExp.typeCheck(env, null, scope));

		return rtypes.getType(location);
	}

	@Override
	public Expression findExpression(int lineno)
	{
		Expression found = super.findExpression(lineno);
		if (found != null) return found;
		found = thenExp.findExpression(lineno);
		if (found != null) return found;

		for (ElseIfExpression stmt: elseList)
		{
			found = stmt.findExpression(lineno);
			if (found != null) return found;
		}

		if (elseExp != null)
		{
			found = elseExp.findExpression(lineno);
		}

		return found;
	}

	@Override
	public Value eval(Context ctxt)
	{
		breakpoint.check(location, ctxt);

		try
		{
    		if (ifExp.eval(ctxt).boolValue(ctxt))
    		{
    			return thenExp.eval(ctxt);
    		}

    		for (ElseIfExpression elseif: elseList)
			{
				Value r = elseif.eval(ctxt);
				if (r != null) return r;
			}

			return elseExp.eval(ctxt);
        }
        catch (ValueException e)
        {
        	return abort(e);
        }
	}

	@Override
	public ProofObligationList getProofObligations(POContextStack ctxt)
	{
		ProofObligationList obligations = ifExp.getProofObligations(ctxt);

		ctxt.push(new POImpliesContext(ifExp));
		obligations.addAll(thenExp.getProofObligations(ctxt));
		ctxt.pop();

		ctxt.push(new PONotImpliesContext(ifExp));	// not (ifExp) =>

		for (ElseIfExpression exp: elseList)
		{
			obligations.addAll(exp.getProofObligations(ctxt));
			ctxt.push(new PONotImpliesContext(exp.elseIfExp));
		}

		obligations.addAll(elseExp.getProofObligations(ctxt));

		for (int i=0; i<elseList.size(); i++)
		{
			ctxt.pop();
		}

		ctxt.pop();

		return obligations;
	}

	@Override
	public String kind()
	{
		return "if";
	}
}
