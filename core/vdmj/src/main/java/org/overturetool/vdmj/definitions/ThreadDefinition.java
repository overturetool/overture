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

package org.overturetool.vdmj.definitions;

import org.overturetool.vdmj.expressions.Expression;
import org.overturetool.vdmj.expressions.ExpressionList;
import org.overturetool.vdmj.lex.LexNameList;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.lex.Token;
import org.overturetool.vdmj.patterns.PatternList;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.statements.PeriodicStatement;
import org.overturetool.vdmj.statements.Statement;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.typechecker.Pass;
import org.overturetool.vdmj.types.OperationType;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.UnknownType;
import org.overturetool.vdmj.types.VoidType;
import org.overturetool.vdmj.values.NameValuePairList;

public class ThreadDefinition extends Definition
{
	private static final long serialVersionUID = 1L;
	public final Statement statement;
	private LexNameToken operationName = null;
	private ExplicitOperationDefinition operationDef = null;

	public ThreadDefinition(Statement statement)
	{
		super(Pass.DEFS, statement.location, null, NameScope.GLOBAL);

		this.statement = statement;
		this.operationName = LexNameToken.getThreadName(statement.location);

		setAccessSpecifier(new AccessSpecifier(false, false, Token.PROTECTED));
	}

	public ThreadDefinition(LexNameToken opname, ExpressionList args)
	{
		this(new PeriodicStatement(opname, args));
	}

	@Override
	public void implicitDefinitions(Environment base)
	{
		operationDef = getThreadDefinition();
	}

	@Override
	public Definition findName(LexNameToken sought, NameScope scope)
	{
		return operationDef.findName(sought, scope);
	}

	@Override
	public Expression findExpression(int lineno)
	{
		return statement.findExpression(lineno);
	}

	@Override
	public Statement findStatement(int lineno)
	{
		return statement.findStatement(lineno);
	}

	@Override
	public DefinitionList getDefinitions()
	{
		return new DefinitionList(operationDef);
	}

	@Override
	public NameValuePairList getNamedValues(Context ctxt)
	{
		return operationDef.getNamedValues(ctxt);
	}

	@Override
	public boolean isOperation()
	{
		return true;	// Because we define an operation called "thread"
	}

	@Override
	public Type getType()
	{
		return new UnknownType(location);
	}

	@Override
	public LexNameList getVariableNames()
	{
		return new LexNameList(operationDef.name);
	}

	@Override
	public String kind()
	{
		return "thread";
	}

	@Override
	public String toString()
	{
		return "thread " + statement.toString();
	}
	
	@Override
	public boolean equals(Object other)
	{
		if (other instanceof ThreadDefinition)
		{
			ThreadDefinition tho = (ThreadDefinition)other;
			return tho.operationName.equals(operationName);
		}
		
		return false;
	}
	
	@Override
	public int hashCode()
	{
		return operationName.hashCode();
	}

	@Override
	public void typeCheck(Environment base, NameScope scope)
	{
		Type rt = statement.typeCheck(base, NameScope.NAMESANDSTATE);

		if (!(rt instanceof VoidType) && !(rt instanceof UnknownType))
		{
			report(3049, "Thread statement/operation must not return a value");
		}
	}

	private ExplicitOperationDefinition getThreadDefinition()
	{
		OperationType type = new OperationType(location);	// () ==> ()

		ExplicitOperationDefinition def = new ExplicitOperationDefinition(
			operationName, type, new PatternList(), null, null, statement);

		def.setAccessSpecifier(accessSpecifier);
		def.classDefinition = classDefinition;
		return def;
	}
}
