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

package org.overturetool.vdmj.expressions;

import org.overturetool.vdmj.definitions.StateDefinition;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.ObjectContext;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.types.Field;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.TypeList;
import org.overturetool.vdmj.values.ObjectValue;
import org.overturetool.vdmj.values.RecordValue;
import org.overturetool.vdmj.values.Value;

public class PreOpExpression extends Expression
{
	public final LexNameToken opname;
	public final Expression expression;
	public final StateDefinition state;

	public PreOpExpression(
		LexNameToken opname, Expression expression, StateDefinition state)
	{
		super(expression);
		this.opname = opname;
		this.expression = expression;
		this.state = state;
	}

	@Override
	public Value eval(Context ctxt)
	{
    	try
    	{
    		breakpoint.check(location, ctxt);

    		// The precondition function arguments are the function args,
    		// plus the state (if any). These all exist in ctxt. We find the
    		// Sigma record and expand its contents to give additional
    		// values in ctxt for each field.

    		if (state != null)
    		{
    			try
    			{
    				RecordValue sigma = ctxt.lookup(state.name).recordValue(ctxt);

    				for (Field field: state.fields)
    				{
    					ctxt.put(field.tagname, sigma.fieldmap.get(field.tag));
    				}
    			}
    			catch (ValueException e)
    			{
    				abort(e);
    			}
    		}
    		else if (ctxt instanceof ObjectContext)
    		{
    			ObjectContext octxt = (ObjectContext)ctxt;
    			LexNameToken selfname = opname.getSelfName();
    			ObjectValue self = octxt.lookup(selfname).objectValue(ctxt);

    			// If the opname was defined in a superclass of "self", we have
    			// to discover the subobject to populate its state variables.

    			ObjectValue subself = findObject(opname.module, self);

    			if (subself == null)
    			{
    				abort(4027, "Cannot create pre_op environment", ctxt);
    			}

    			// Create an object context using the "self" passed in, rather
    			// than the self that we're being called from.

    			ObjectContext selfctxt = new ObjectContext(
    				ctxt.location, "precondition's object", ctxt, subself);

    			selfctxt.putAll(ctxt);	// To add "RESULT" and args.
    			ctxt = selfctxt;
    		}

    		return expression.eval(ctxt);
    	}
    	catch (ValueException e)
    	{
    		return abort(e);
    	}
	}

	private ObjectValue findObject(String classname, ObjectValue object)
	{
		if (object.type.name.name.equals(classname))
		{
			return object;
		}

		ObjectValue found = null;

		for (ObjectValue ov: object.superobjects)
		{
			found = findObject(classname, ov);

			if (found != null)
			{
				break;
			}
		}

		return found;
	}

	@Override
	public String toString()
	{
		return expression.toString();
	}

	@Override
	public Type typeCheck(Environment env, TypeList qualifiers, NameScope scope)
	{
		return expression.typeCheck(env, null, scope);
	}

	@Override
	public String kind()
	{
		return "pre_op";
	}
}
