/*******************************************************************************
 *
 *	Copyright (C) 2008 Fujitsu Services Ltd.
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

package org.overturetool.vdmj.traces;

import org.overturetool.vdmj.expressions.Expression;
import org.overturetool.vdmj.expressions.ExpressionList;
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.lex.LexException;
import org.overturetool.vdmj.lex.LexTokenReader;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.ContextException;
import org.overturetool.vdmj.statements.CallObjectStatement;
import org.overturetool.vdmj.syntax.ExpressionReader;
import org.overturetool.vdmj.syntax.ParserException;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.values.ObjectValue;
import org.overturetool.vdmj.values.Value;

/**
 * A class representing a trace apply expression.
 */

public class TraceApplyExpression extends TraceCoreDefinition
{
    private static final long serialVersionUID = 1L;
	public final CallObjectStatement statement;

	public TraceApplyExpression(CallObjectStatement statement)
	{
		super(statement.location);
		this.statement = statement;
	}

	@Override
	public String toString()
	{
		return statement.toString();
	}

	@Override
	public void typeCheck(Environment env, NameScope scope)
	{
		statement.typeCheck(env, scope);
	}

	@Override
	public TraceNode expand(Context ctxt)
	{
		ExpressionList newargs = new ExpressionList();
		int hash = 0;

		for (Expression arg: statement.args)
		{
			Value v = arg.eval(ctxt).deref();

			if (v instanceof ObjectValue)
			{
				// We can't save the value of an object, so we use its hash.
				newargs.add(arg);
				hash += v.hashCode();
			}
			else
			{
    			String value = v.toString();
    			LexTokenReader ltr = new LexTokenReader(value, Dialect.VDM_PP);
    			ExpressionReader er = new ExpressionReader(ltr);

    			try
    			{
    				newargs.add(er.readExpression());
    			}
    			catch (ParserException e)
    			{
    				throw new ContextException(
    					e.number, e.getMessage(), e.location, ctxt);
    			}
    			catch (LexException e)
    			{
    				throw new ContextException(
    					e.number, e.getMessage(), e.location, ctxt);
    			}
			}
		}

		CallObjectStatement cos = new CallObjectStatement(
			statement.designator, statement.classname, statement.fieldname,
			newargs);

		return new StatementTraceNode(cos, hash, ctxt);
	}
}
