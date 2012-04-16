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

import java.util.List;

import org.overturetool.vdmj.definitions.StateDefinition;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.lex.LexNameList;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.ObjectContext;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.statements.ErrorCase;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.types.Field;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.TypeList;
import org.overturetool.vdmj.values.BooleanValue;
import org.overturetool.vdmj.values.FunctionValue;
import org.overturetool.vdmj.values.ObjectValue;
import org.overturetool.vdmj.values.OperationValue;
import org.overturetool.vdmj.values.RecordValue;
import org.overturetool.vdmj.values.Value;
import org.overturetool.vdmj.values.ValueMap;

public class PostOpExpression extends Expression
{
	private static final long serialVersionUID = 1L;
	public final LexNameToken opname;
	public final Expression preexpression;
	public final Expression postexpression;
	public final List<ErrorCase> errors;
	public final StateDefinition state;

	private LexLocation errorLocation;

	public PostOpExpression(
		LexNameToken opname, Expression preexpression, Expression postexpression,
		List<ErrorCase> errors, StateDefinition state)
	{
		super(postexpression.location);
		this.opname = opname;
		this.preexpression = preexpression;
		this.postexpression = postexpression;
		this.errors = errors;
		this.state = state;
	}

	@Override
	public Value eval(Context ctxt)
	{
		// No break check here, as we want to start in the expression

		// The postcondition function arguments are the function args, the
		// result, the old/new state (if any). These all exist in ctxt.
		// We find the Sigma record and expand its contents to give additional
		// values in ctxt for each field. Ditto with Sigma~.

		try
		{
    		if (state != null)
    		{
    			RecordValue sigma = ctxt.lookup(state.name).recordValue(ctxt);

    			for (Field field: state.fields)
    			{
    				ctxt.put(field.tagname, sigma.fieldmap.get(field.tag));
    			}

    			RecordValue oldsigma = ctxt.lookup(state.name.getOldName()).recordValue(ctxt);

    			for (Field field: state.fields)
    			{
    				ctxt.put(field.tagname.getOldName(), oldsigma.fieldmap.get(field.tag));
    			}
    		}
    		else if (ctxt instanceof ObjectContext)
    		{
    			ObjectContext octxt = (ObjectContext)ctxt;
    			LexNameToken selfname = opname.getSelfName();
    			LexNameToken oldselfname = selfname.getOldName();

    			ObjectValue self = octxt.lookup(selfname).objectValue(ctxt);
    			ValueMap oldvalues = octxt.lookup(oldselfname).mapValue(ctxt);

    			// If the opname was defined in a superclass of "self", we have
    			// to discover the subobject to populate its state variables.

    			ObjectValue subself = findObject(opname.module, self);

    			if (subself == null)
    			{
    				abort(4026, "Cannot create post_op environment", ctxt);
    			}

    			// Create an object context using the "self" passed in, rather
    			// than the self that we're being called from, assuming they
    			// are different.

    			if (subself != octxt.self)
    			{
        			ObjectContext selfctxt = new ObjectContext(
        				ctxt.location, "postcondition's object", ctxt, subself);

        			selfctxt.putAll(ctxt);	// To add "RESULT" and args.
        			ctxt = selfctxt;
    			}

    			populate(ctxt, subself.type.name.name, oldvalues);		// To add old "~" values
    		}


    		// If there are errs clauses, and there is a precondition defined, then
    		// we evaluate that as well as the postcondition.

    		boolean result =
    			(errors == null || preexpression == null || preexpression.eval(ctxt).boolValue(ctxt)) &&
    			postexpression.eval(ctxt).boolValue(ctxt);

    		errorLocation = location;

    		if (errors != null)
    		{
    			for (ErrorCase err: errors)
    			{
    				boolean left  = err.left.eval(ctxt).boolValue(ctxt);
    				boolean right = err.right.eval(ctxt).boolValue(ctxt);

    				if (left && !right)
    				{
    					errorLocation = err.left.location;
    				}

    				result = result || (left && right);
    			}
    		}

    		return new BooleanValue(result);
		}
		catch (ValueException e)
		{
			return abort(e);
		}
	}

	@Override
	public LexLocation getLocation()
	{
		return errorLocation == null ? location : errorLocation;
	}

	private void populate(Context ctxt, String classname, ValueMap oldvalues) throws ValueException
	{
		for (Value var: oldvalues.keySet())
		{
			String name = var.stringValue(ctxt);
			Value val = oldvalues.get(var);

			if (!(val instanceof FunctionValue) &&
				!(val instanceof OperationValue))
			{
				LexNameToken oldname = new LexNameToken(classname, name, location, true, false);
				ctxt.put(oldname, val);
			}
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
		return postexpression.toString();
	}

	@Override
	public Type typeCheck(Environment env, TypeList qualifiers, NameScope scope)
	{
		return postexpression.typeCheck(env, null, scope);
	}

	@Override
	public Expression findExpression(int lineno)
	{
		return postexpression.findExpression(lineno);
	}

	@Override
	public LexNameList getOldNames()
	{
		return postexpression.getOldNames();
	}

	@Override
	public String kind()
	{
		return "post_op";
	}
}
