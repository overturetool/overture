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

package org.overture.interpreter.values;

import java.util.Set;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.PType;
import org.overture.interpreter.assistant.type.PTypeAssistantInterpreter;
import org.overture.interpreter.runtime.Context;

public class CompFunctionValue extends FunctionValue
{
	private static final long serialVersionUID = 1L;
	public final FunctionValue ff1;
	public final FunctionValue ff2;

	public CompFunctionValue(FunctionValue f1, FunctionValue f2)
	{
		super(f1.location, AstFactory.newAFunctionType(f1.location, f1.type.getPartial()
				|| f2.type.getPartial(), f2.type.getParameters(), f1.type.getResult()), "comp");
		this.ff1 = f1;
		this.ff2 = f2;
	}

	@Override
	public String toString()
	{
		return ff2.type.getParameters() + " -> " + ff1.type.getResult();
	}

	@Override
	public Value eval(ILexLocation from, ValueList argValues, Context ctxt)
			throws AnalysisException
	{
		ValueList f1arg = new ValueList();
		f1arg.add(ff2.eval(from, argValues, ctxt));
		return ff1.eval(from, f1arg, ctxt);
	}
	
	@Override
	protected Value convertValueTo(PType to, Context ctxt, Set<PType> done)
			throws AnalysisException
	{
		PTypeAssistantInterpreter assistant = ctxt.assistantFactory.createPTypeAssistant();

		if (assistant.isFunction(to))
		{
			if (type.equals(to) || assistant.isUnknown(to))
			{
				return this;
			}
			else
			{
				AFunctionType restrictedType = assistant.getFunction(to);
				
				if (type.equals(restrictedType))
				{
					return this;
				}
				else if (ctxt.assistantFactory.getTypeComparator().isSubType(type, restrictedType))
				{
					// The new function amends the parameters of ff2 and the result of ff1
					
					FunctionValue leftValue = (FunctionValue)ff1.clone();
					leftValue.type = AstFactory.newAFunctionType(ff1.location,
						ff1.type.getPartial(), ff1.type.getParameters(), restrictedType.getResult());
					
					FunctionValue rightValue = (FunctionValue)ff2.clone();
					rightValue.type = AstFactory.newAFunctionType(ff2.location,
						ff2.type.getPartial(), restrictedType.getParameters(), ff2.type.getResult());
					
					return new CompFunctionValue(leftValue, rightValue);
				}
				else
				{
					return abort(4165, "Cannot convert " + this + " to " + restrictedType, ctxt);
				}
			}
		}
		else
		{
			return super.convertValueTo(to, ctxt, done);
		}
	}


	@Override
	public boolean equals(Object other)
	{
		if (other instanceof Value)
		{
			Value val = ((Value) other).deref();

			if (val instanceof CompFunctionValue)
			{
				CompFunctionValue ov = (CompFunctionValue) val;
				return ov.ff1.equals(ff1) && ov.ff2.equals(ff2);
			}
		}

		return false;
	}

	@Override
	public int hashCode()
	{
		return ff1.hashCode() + ff2.hashCode();
	}

	@Override
	public String kind()
	{
		return "comp";
	}
	
	@Override
	public Object clone()
	{
		return new CompFunctionValue((FunctionValue)ff1.clone(), (FunctionValue)ff2.clone());
	}
}
