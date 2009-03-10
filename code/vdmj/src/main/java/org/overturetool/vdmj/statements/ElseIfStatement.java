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

import org.overturetool.vdmj.expressions.Expression;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.pog.POContextStack;
import org.overturetool.vdmj.pog.ProofObligationList;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.types.BooleanType;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.TypeSet;
import org.overturetool.vdmj.values.Value;

public class ElseIfStatement extends Statement
{
	private static final long serialVersionUID = 1L;
	public final Expression elseIfExp;
	public final Statement thenStmt;

	public ElseIfStatement(LexLocation location, Expression elseIfExp, Statement thenStmt)
	{
		super(location);
		this.elseIfExp = elseIfExp;
		this.thenStmt = thenStmt;
	}

	@Override
	public String toString()
	{
		return "elseif " + elseIfExp + "\nthen\n" + thenStmt;
	}

	@Override
	public String kind()
	{
		return "elseif";
	}

	@Override
	public Type typeCheck(Environment env, NameScope scope)
	{
		if (!elseIfExp.typeCheck(env, null, scope).isType(BooleanType.class))
		{
			elseIfExp.report(3218, "Expression is not boolean");
		}

		return thenStmt.typeCheck(env, scope);
	}

	@Override
	public TypeSet exitCheck()
	{
		return thenStmt.exitCheck();
	}

	@Override
	public Statement findStatement(int lineno)
	{
		Statement found = super.findStatement(lineno);
		if (found != null) return found;
		return thenStmt.findStatement(lineno);
	}

	@Override
	public Value eval(Context ctxt)
	{
		breakpoint.check(location, ctxt);

		try
		{
			return elseIfExp.eval(ctxt).boolValue(ctxt) ? thenStmt.eval(ctxt) : null;
		}
        catch (ValueException e)
        {
        	return abort(e);
        }
	}

	@Override
	public ProofObligationList getProofObligations(POContextStack ctxt)
	{
		ProofObligationList obligations = elseIfExp.getProofObligations(ctxt);
		obligations.addAll(thenStmt.getProofObligations(ctxt));
		return obligations;
	}
}
