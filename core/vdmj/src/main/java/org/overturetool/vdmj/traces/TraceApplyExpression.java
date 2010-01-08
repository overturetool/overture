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

import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.expressions.Expression;
import org.overturetool.vdmj.expressions.ExpressionList;
import org.overturetool.vdmj.lex.LexException;
import org.overturetool.vdmj.lex.LexTokenReader;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.statements.CallObjectStatement;
import org.overturetool.vdmj.statements.CallStatement;
import org.overturetool.vdmj.statements.Statement;
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
	public final Statement callStatement;
	public final String currentModule;

	public TraceApplyExpression(Statement stmt, String currentModule)
	{
		super(stmt.location);
		this.callStatement = stmt;
		this.currentModule = currentModule;
	}

	@Override
	public String toString()
	{
		return callStatement.toString();
	}

	@Override
	public void typeCheck(Environment env, NameScope scope)
	{
		callStatement.typeCheck(env, scope);
	}

	@Override
	public TraceNode expand(Context ctxt)
	{
		ExpressionList newargs = new ExpressionList();
		ExpressionList args = null;

		if (callStatement instanceof CallStatement)
		{
			CallStatement stmt = (CallStatement)callStatement;
			args = stmt.args;
		}
		else
		{
			CallObjectStatement stmt = (CallObjectStatement)callStatement;
			args = stmt.args;
		}

		for (Expression arg: args)
		{
			Value v = arg.eval(ctxt).deref();

			if (v instanceof ObjectValue)
			{
				newargs.add(arg);
			}
			else
			{
    			String value = v.toString();
    			LexTokenReader ltr = new LexTokenReader(value, Settings.dialect);
    			ExpressionReader er = new ExpressionReader(ltr);
    			er.setCurrentModule(currentModule);

    			try
    			{
    				newargs.add(er.readExpression());
    			}
    			catch (ParserException e)
    			{
    				newargs.add(arg);		// Give up!
    			}
    			catch (LexException e)
    			{
    				newargs.add(arg);		// Give up!
    			}
			}
		}

		Statement newStatement = null;

		if (callStatement instanceof CallStatement)
		{
			CallStatement stmt = (CallStatement)callStatement;
			newStatement = new CallStatement(stmt.name, newargs);
		}
		else
		{
			CallObjectStatement stmt = (CallObjectStatement)callStatement;
			newStatement = new CallObjectStatement(
				stmt.designator, stmt.classname, stmt.fieldname, newargs);
		}

		return new StatementTraceNode(newStatement);
	}
}
