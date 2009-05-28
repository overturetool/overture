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

import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.definitions.ClassDefinition;
import org.overturetool.vdmj.definitions.Definition;
import org.overturetool.vdmj.expressions.Expression;
import org.overturetool.vdmj.expressions.ExpressionList;
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.pog.POContextStack;
import org.overturetool.vdmj.pog.ProofObligationList;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.typechecker.PrivateClassEnvironment;
import org.overturetool.vdmj.typechecker.PublicClassEnvironment;
import org.overturetool.vdmj.typechecker.TypeComparator;
import org.overturetool.vdmj.types.ClassType;
import org.overturetool.vdmj.types.FunctionType;
import org.overturetool.vdmj.types.OperationType;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.TypeList;
import org.overturetool.vdmj.types.TypeSet;
import org.overturetool.vdmj.types.UnknownType;
import org.overturetool.vdmj.types.VoidType;
import org.overturetool.vdmj.util.Utils;
import org.overturetool.vdmj.values.FunctionValue;
import org.overturetool.vdmj.values.ObjectValue;
import org.overturetool.vdmj.values.OperationValue;
import org.overturetool.vdmj.values.Value;
import org.overturetool.vdmj.values.ValueList;

public class CallObjectStatement extends Statement
{
	private static final long serialVersionUID = 1L;
	public final ObjectDesignator designator;
	public String classname;
	public final String fieldname;
	public final ExpressionList args;
	public final boolean explicit;

	private LexNameToken field;

	public CallObjectStatement(
		ObjectDesignator designator, String classname, String fieldname, ExpressionList args)
	{
		super(designator.location);

		this.designator = designator;
		this.classname = classname;
		this.fieldname = fieldname;
		this.args = args;
		this.explicit = (classname != null);
	}

	@Override
	public String toString()
	{
		return designator + "." +
			(explicit ? classname + "`" : "") + fieldname +
			"(" + Utils.listToString(args) + ")";
	}

	@Override
	public String kind()
	{
		return "object call";
	}

	@Override
	public Type typeCheck(Environment env, NameScope scope)
	{
		Type dtype = designator.typeCheck(env, null);

		if (!dtype.isClass())
		{
			report(3207, "Object designator is not an object type");
			return new UnknownType(location);
		}

		ClassType ctype = dtype.getClassType();

		if (classname == null)
		{
			classname = ctype.name.name;
		}

		ClassDefinition classdef = ctype.classdef;
		ClassDefinition self = env.findClassDefinition();
		Environment classenv = null;

		if (self == classdef || self.hasSupertype(classdef.getType()))
		{
			// All fields visible. Note that protected fields are inherited
			// into "locals" so they are effectively private
			classenv = new PrivateClassEnvironment(self);
		}
		else
		{
			// Only public fields externally visible
			classenv = new PublicClassEnvironment(classdef);
		}

		field = new LexNameToken(classname, fieldname, location);
		TypeList atypes = getArgTypes(env, scope);
		field.setTypeQualifier(atypes);
		Definition fdef = classenv.findName(field, scope);

		// Special code for the deploy method of CPU

		if (Settings.dialect == Dialect.VDM_RT &&
			classname.equals("CPU") && field.name.equals("deploy"))
		{
			if (!(atypes.get(0) instanceof ClassType))
			{
				args.get(0).report(3280, "Argument to deploy must be an object");
			}

			return new VoidType(location);
		}
		else if (Settings.dialect == Dialect.VDM_RT &&
			classname.equals("CPU") && field.name.equals("setPriority"))
		{
			if (!(atypes.get(0) instanceof OperationType))
			{
				args.get(0).report(3290, "Argument to setPriority must be an operation");
			}
			else
			{
//    			OperationType op = (OperationType)(atypes.get(0));
//
//    			if (it's a constructor)	// How?
//    			{
//    				args.get(0).report(3291, "Argument to setPriority cannot be a constructor");
//    			}
			}

			return new VoidType(location);
		}
		else if (fdef == null)
		{
			report(3209, "Member " + field + " is not in scope");
			return new UnknownType(location);
		}
		else if (fdef.isStatic() && !env.isStatic())
		{
			// warning(5005, "Should invoke member " + field + " from a static context");
		}

		Type type = fdef.getType();

		if (type.isOperation())
		{
			OperationType optype = type.getOperation();
			optype.typeResolve(env, null);
			field.setTypeQualifier(optype.parameters);
			checkArgTypes(optype.parameters, atypes);	// Not necessary?
			return optype.result;
		}
		else if (type.isFunction())
		{
			// This is the case where a function is called as an operation without
			// a "return" statement.

			FunctionType ftype = type.getFunction();
			ftype.typeResolve(env, null);
			field.setTypeQualifier(ftype.parameters);
			checkArgTypes(ftype.parameters, atypes);	// Not necessary?
			return ftype.result;
		}
		else
		{
			report(3210, "Object member is neither a function nor an operation");
			return new UnknownType(location);
		}
	}

	@Override
	public TypeSet exitCheck()
	{
		// We don't know what an operation call will raise
		return new TypeSet(new UnknownType(location));
	}

	private TypeList getArgTypes(Environment env, NameScope scope)
	{
		TypeList types = new TypeList();

		for (Expression a: args)
		{
			types.add(a.typeCheck(env, null, scope));
		}

		return types;
	}

	private void checkArgTypes(TypeList ptypes, TypeList atypes)
	{
		if (ptypes.size() != atypes.size())
		{
			report(3211, "Expecting " + ptypes.size() + " arguments");
		}
		else
		{
			int i=0;

			for (Type atype: atypes)
			{
				Type ptype = ptypes.get(i++);

				if (!TypeComparator.compatible(ptype, atype))
				{
					atype.report(3212, "Unexpected type for argument " + i);
					detail2("Expected", ptype, "Actual", atype);
				}
			}
		}
	}

	@Override
	public Value eval(Context ctxt)
	{
		breakpoint.check(location, ctxt);

		try
		{
			ObjectValue obj = designator.eval(ctxt).objectValue(ctxt);
			Value v = obj.get(field, explicit);

			if (v == null)
			{
    			field.abort(4035, "Object has no field: " + field.name, ctxt);
			}

			v = v.deref();

			if (v instanceof OperationValue)
			{
    			OperationValue op = v.operationValue(ctxt);
    			ValueList argValues = new ValueList();

    			for (Expression arg: args)
    			{
    				argValues.add(arg.eval(ctxt));
    			}

    			return op.eval(argValues, ctxt);
			}
			else
			{
    			FunctionValue op = v.functionValue(ctxt);
    			ValueList argValues = new ValueList();

    			for (Expression arg: args)
    			{
    				argValues.add(arg.eval(ctxt));
    			}

    			return op.eval(location, argValues, ctxt);
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
		ProofObligationList obligations = new ProofObligationList();

		for (Expression exp: args)
		{
			obligations.addAll(exp.getProofObligations(ctxt));
		}

		return obligations;
	}
}
