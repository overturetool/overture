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
import org.overturetool.vdmj.pog.POContextStack;
import org.overturetool.vdmj.pog.ProofObligationList;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.types.BooleanType;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.TypeSet;
import org.overturetool.vdmj.types.VoidType;
import org.overturetool.vdmj.values.Value;
import org.overturetool.vdmj.values.VoidValue;


public class IfStatement extends Statement
{
	private static final long serialVersionUID = 1L;
	public final Expression ifExp;
	public final Statement thenStmt;
	public final List<ElseIfStatement> elseList;
	public final Statement elseStmt;

	public IfStatement(LexLocation location,
		Expression ifExp, Statement thenStmt,
		List<ElseIfStatement> elseList, Statement elseStmt)
	{
		super(location);
		this.ifExp = ifExp;
		this.thenStmt = thenStmt;
		this.elseList = elseList;
		this.elseStmt = elseStmt;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("if " + ifExp + "\nthen\n" + thenStmt);

		for (ElseIfStatement s: elseList)
		{
			sb.append(s.toString());
		}

		if (elseStmt != null)
		{
			sb.append("else\n");
			sb.append(elseStmt.toString());
		}

		return sb.toString();
	}

	@Override
	public String kind()
	{
		return "if";
	}

	@Override
	public Type typeCheck(Environment env, NameScope scope)
	{
		Type test = ifExp.typeCheck(env, null, scope);

		if (!test.isType(BooleanType.class))
		{
			ifExp.report(3224, "If expression is not boolean");
		}

		TypeSet rtypes = new TypeSet();
		rtypes.add(thenStmt.typeCheck(env, scope));

		if (elseList != null)
		{
			for (ElseIfStatement stmt: elseList)
			{
				rtypes.add(stmt.typeCheck(env, scope));
			}
		}

		if (elseStmt != null)
		{
			rtypes.add(elseStmt.typeCheck(env, scope));
		}
		else
		{
			rtypes.add(new VoidType(location));
		}

		return rtypes.getType(location);
	}

	@Override
	public TypeSet exitCheck()
	{
		TypeSet types = new TypeSet();
		types.addAll(thenStmt.exitCheck());

		for (ElseIfStatement stmt: elseList)
		{
			types.addAll(stmt.exitCheck());
		}

		if (elseStmt != null)
		{
			types.addAll(elseStmt.exitCheck());
		}

		return types;
	}

	@Override
	public Statement findStatement(int lineno)
	{
		Statement found = super.findStatement(lineno);
		if (found != null) return found;
		found = thenStmt.findStatement(lineno);
		if (found != null) return found;

		for (ElseIfStatement stmt: elseList)
		{
			found = stmt.findStatement(lineno);
			if (found != null) return found;
		}

		if (elseStmt != null)
		{
			found = elseStmt.findStatement(lineno);
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
    			return thenStmt.eval(ctxt);
    		}
    		else
    		{
    			for (ElseIfStatement elseif: elseList)
    			{
    				Value r = elseif.eval(ctxt);
    				if (r != null) return r;
    			}

    			if (elseStmt != null)
    			{
    				return elseStmt.eval(ctxt);
    			}

    			return new VoidValue();
    		}
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
		obligations.addAll(thenStmt.getProofObligations(ctxt));

		for (ElseIfStatement stmt: elseList)
		{
			obligations.addAll(stmt.getProofObligations(ctxt));
		}

		if (elseStmt != null)
		{
			obligations.addAll(elseStmt.getProofObligations(ctxt));
		}

		return obligations;
	}
}
