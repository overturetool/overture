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
import org.overturetool.vdmj.lex.LexNameList;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.pog.POContextStack;
import org.overturetool.vdmj.pog.ProofObligationList;
import org.overturetool.vdmj.pog.SubTypeObligation;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.typechecker.Pass;
import org.overturetool.vdmj.typechecker.TypeComparator;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.VoidType;
import org.overturetool.vdmj.values.NameValuePair;
import org.overturetool.vdmj.values.NameValuePairList;
import org.overturetool.vdmj.values.Value;

/**
 * A class to represent assignable variable definitions.
 */

public class AssignmentDefinition extends Definition
{
	private static final long serialVersionUID = 1L;

	public Type type;
	public final Expression expression;
	public Type expType;

	public AssignmentDefinition(LexNameToken name, Type type, Expression expression)
	{
		super(Pass.VALUES, name.location, name, NameScope.STATE);
		this.type = type;
		this.expression = expression;
	}

	@Override
	public String toString()
	{
		return name + ":" + type + " := " + expression;
	}

	@Override
	public NameValuePairList getNamedValues(Context ctxt)
	{
        try
        {
	        Value v = expression.eval(ctxt);

	        if (!v.isUndefined())
	        {
	        	v = v.convertTo(type, ctxt);
	        }

			return new NameValuePairList(new NameValuePair(name, v.getUpdatable(null)));
        }
        catch (ValueException e)
        {
        	abort(e);
        	return null;
        }
 	}

	@Override
	public DefinitionList getDefinitions()
	{
		return new DefinitionList(this);
	}

	@Override
	public LexNameList getVariableNames()
	{
		return new LexNameList(name);
	}

	@Override
	public Expression findExpression(int lineno)
	{
		return expression.findExpression(lineno);
	}

	@Override
	public Type getType()
	{
		return type;
	}

	@Override
	public void typeCheck(Environment base, NameScope scope)
	{
		expType = expression.typeCheck(base, null, scope);
		type = type.typeResolve(base, null);

		if (expType instanceof VoidType)
		{
			expression.report(3048, "Expression does not return a value");
		}

		if (!TypeComparator.compatible(type, expType))
		{
			report(3000, "Expression does not match declared type");
			detail2("Declared", type, "Expression", expType);
		}
	}

	@Override
	public ProofObligationList getProofObligations(POContextStack ctxt)
	{
		ProofObligationList obligations = new ProofObligationList();
		obligations.addAll(expression.getProofObligations(ctxt));

		if (!TypeComparator.isSubType(ctxt.checkType(expression, expType), type))
		{
			obligations.add(
				new SubTypeObligation(expression, type, expType, ctxt));
		}

		return obligations;
	}

	@Override
	public String kind()
	{
		return "assignable variable";
	}

	@Override
	public boolean isUpdatable()
	{
		return true;
	}
}
