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

import org.overturetool.vdmj.definitions.Definition;
import org.overturetool.vdmj.definitions.LocalDefinition;
import org.overturetool.vdmj.expressions.Expression;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.pog.POContextStack;
import org.overturetool.vdmj.pog.POScopeContext;
import org.overturetool.vdmj.pog.ProofObligationList;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.FlatCheckedEnvironment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.TypeSet;
import org.overturetool.vdmj.values.IntegerValue;
import org.overturetool.vdmj.values.Value;
import org.overturetool.vdmj.values.VoidValue;

public class ForIndexStatement extends Statement
{
	private static final long serialVersionUID = 1L;
	public final LexNameToken var;
	public final Expression from;
	public final Expression to;
	public final Expression by;
	public final Statement statement;

	public ForIndexStatement(LexLocation location,
		LexNameToken var, Expression from, Expression to, Expression by, Statement body)
	{
		super(location);
		this.var = var;
		this.from = from;
		this.to = to;
		this.by = by;
		this.statement = body;
	}

	@Override
	public String toString()
	{
		return "for " + var + " = " + from + " to " + to +
					(by == null ? "" : " by " + by) + "\n" + statement;
	}

	@Override
	public String kind()
	{
		return "for";
	}

	@Override
	public Type typeCheck(Environment env, NameScope scope)
	{
		Type ft = from.typeCheck(env, null, scope);
		Type tt = to.typeCheck(env, null, scope);

		if (!ft.isNumeric())
		{
			report(3220, "From type is not numeric");
		}

		if (!tt.isNumeric())
		{
			report(3221, "To type is not numeric");
		}

		if (by != null)
		{
			Type bt = by.typeCheck(env, null, scope);

			if (!bt.isNumeric())
			{
				report(3222, "By type is not numeric");
			}
		}

		Definition vardef = new LocalDefinition(var.location, var, NameScope.LOCAL, ft);
		Environment local = new FlatCheckedEnvironment(vardef, env, scope);
		Type rt = statement.typeCheck(local, scope);
		local.unusedCheck();
		return rt;
	}

	@Override
	public TypeSet exitCheck()
	{
		return statement.exitCheck();
	}

	@Override
	public Statement findStatement(int lineno)
	{
		Statement found = super.findStatement(lineno);
		if (found != null) return found;
		return statement.findStatement(lineno);
	}

	@Override
	public Expression findExpression(int lineno)
	{
		Expression found = from.findExpression(lineno);
		if (found != null) return found;
		found = to.findExpression(lineno);
		if (found != null) return found;
		found = by.findExpression(lineno);
		if (found != null) return found;
		return statement.findExpression(lineno);
	}

	@Override
	public Value eval(Context ctxt)
	{
		breakpoint.check(location, ctxt);

		try
		{
			long fval = from.eval(ctxt).intValue(ctxt);
			long tval = to.eval(ctxt).intValue(ctxt);
			long bval = (by == null) ? 1 : by.eval(ctxt).intValue(ctxt);

			if (bval == 0)
			{
				abort(4038, "Loop, from " + fval + " to " + tval + " by " + bval +
						" will never terminate", ctxt);
			}

			for (long value = fval;
				 (bval > 0 && value <= tval) || (bval < 0 && value >= tval);
				 value += bval)
			{
				Context evalContext = new Context(location, "for index", ctxt);
				evalContext.put(var, new IntegerValue(value));
				Value rv = statement.eval(evalContext);

				if (!rv.isVoid())
				{
					return rv;
				}
			}
		}
		catch (ValueException e)
		{
			abort(e);
		}

		return new VoidValue();
	}

	@Override
	public ProofObligationList getProofObligations(POContextStack ctxt)
	{
		ProofObligationList obligations = from.getProofObligations(ctxt);
		obligations.addAll(to.getProofObligations(ctxt));

		if (by != null)
		{
			obligations.addAll(by.getProofObligations(ctxt));
		}

		ctxt.push(new POScopeContext());
		obligations.addAll(statement.getProofObligations(ctxt));
		ctxt.pop();

		return obligations;
	}
}
