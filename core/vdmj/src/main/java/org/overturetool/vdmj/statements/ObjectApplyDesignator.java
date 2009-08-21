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

import org.overturetool.vdmj.expressions.Expression;
import org.overturetool.vdmj.expressions.ExpressionList;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.typechecker.TypeComparator;
import org.overturetool.vdmj.types.FunctionType;
import org.overturetool.vdmj.types.MapType;
import org.overturetool.vdmj.types.OperationType;
import org.overturetool.vdmj.types.SeqType;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.TypeList;
import org.overturetool.vdmj.types.TypeSet;
import org.overturetool.vdmj.types.UnknownType;
import org.overturetool.vdmj.util.Utils;
import org.overturetool.vdmj.values.FunctionValue;
import org.overturetool.vdmj.values.MapValue;
import org.overturetool.vdmj.values.OperationValue;
import org.overturetool.vdmj.values.SeqValue;
import org.overturetool.vdmj.values.UpdatableValue;
import org.overturetool.vdmj.values.Value;
import org.overturetool.vdmj.values.ValueList;
import org.overturetool.vdmj.values.ValueMap;

public class ObjectApplyDesignator extends ObjectDesignator
{
	private static final long serialVersionUID = 1L;
	public final ObjectDesignator object;
	public final ExpressionList args;

	public ObjectApplyDesignator(ObjectDesignator object, ExpressionList args)
	{
		super(object.location);
		this.object = object;
		this.args = args;
	}

	@Override
	public String toString()
	{
		return "(" + object + "(" + Utils.listToString(args) + "))";
	}

	@Override
	public Type typeCheck(Environment env, TypeList qualifiers)
	{
		TypeList argtypes = new TypeList();

		for (Expression a: args)
		{
			argtypes.add(a.typeCheck(env, null, NameScope.NAMESANDSTATE));
		}

		Type type = object.typeCheck(env, argtypes);
		boolean unique = !type.isUnion();
		TypeSet result = new TypeSet();

		if (type.isMap())
		{
			MapType map = type.getMap();
			result.add(mapApply(map, env, NameScope.NAMESANDSTATE, unique));
		}

		if (type.isSeq())
		{
			SeqType seq = type.getSeq();
			result.add(seqApply(seq, env, NameScope.NAMESANDSTATE, unique));
		}

		if (type.isFunction())
		{
			FunctionType ft = type.getFunction();
			ft.typeResolve(env, null);
			result.add(functionApply(ft, env, NameScope.NAMESANDSTATE, unique));
		}

		if (type.isOperation())
		{
			OperationType ot = type.getOperation();
			ot.typeResolve(env, null);
			result.add(operationApply(ot, env, NameScope.NAMESANDSTATE, unique));
		}

		if (result.isEmpty())
		{
			report(3249, "Object designator is not a map, sequence, function or operation");
			detail2("Designator", object, "Type", type);
			return new UnknownType(location);
		}

		return result.getType(location);
	}

	@Override
	public Value eval(Context ctxt)
	{
		try
		{
			Value v = object.eval(ctxt).deref();

			if (v instanceof MapValue)
			{
				ValueMap mv = v.mapValue(ctxt);
				Value a = args.get(0).eval(ctxt);
				Value rv = mv.get(a);

				if (rv == null)		// Not already in map
				{
					rv = UpdatableValue.factory(null);
					mv.put(a, rv);
				}

				return rv;
			}
			else if (v instanceof SeqValue)
			{
				ValueList seq = v.seqValue(ctxt);
				Value a = args.get(0).eval(ctxt);
				int i = (int)a.intValue(ctxt)-1;

				if (!seq.inbounds(i))
				{
					abort(4042, "Sequence does not contain key: " + a, ctxt);
				}

				return seq.get(i);
			}
			else if (v instanceof FunctionValue)
			{
				ValueList argvals = new ValueList();

				for (Expression arg: args)
				{
					argvals.add(arg.eval(ctxt));
				}

				FunctionValue fv = v.functionValue(ctxt);
				return fv.eval(location, argvals, ctxt);
			}

			else if (v instanceof OperationValue)
			{
				ValueList argvals = new ValueList();

				for (Expression arg: args)
				{
					argvals.add(arg.eval(ctxt));
				}

				OperationValue ov = v.operationValue(ctxt);
				return ov.eval(argvals, ctxt);
			}
			else
			{
				return abort(4043,
					"Object designator is not a map, sequence, operation or function", ctxt);
			}
		}
		catch (ValueException e)
		{
			return abort(e);
		}
	}

	private Type mapApply(
		MapType map, Environment env, NameScope scope, boolean unique)
	{
		if (args.size() != 1)
		{
			concern(unique, 3250, "Map application must have one argument");
			return new UnknownType(location);
		}

		Type argtype = args.get(0).typeCheck(env, null, scope);

		if (!TypeComparator.compatible(map.from, argtype))
		{
			concern(unique, 3251, "Map application argument is incompatible type");
			detail2(unique, "Map domain", map.from, "Argument", argtype);
		}

		return map.to;
	}

	private Type seqApply(
		SeqType seq, Environment env, NameScope scope, boolean unique)
	{
		if (args.size() != 1)
		{
			concern(unique, 3252, "Sequence application must have one argument");
			return new UnknownType(location);
		}

		Type argtype = args.get(0).typeCheck(env, null, scope);

		if (!argtype.isNumeric())
		{
			concern(unique, 3253, "Sequence argument is not numeric");
			detail(unique, "Type", argtype);
		}

		return seq.seqof;
	}

	private Type functionApply(
		FunctionType ftype, Environment env, NameScope scope, boolean unique)
	{
		TypeList ptypes = ftype.parameters;

		if (args.size() > ptypes.size())
		{
			concern(unique, 3254, "Too many arguments");
			detail2(unique, "Args", args, "Params", ptypes);
			return ftype.result;
		}
		else if (args.size() < ptypes.size())
		{
			concern(unique, 3255, "Too few arguments");
			detail2(unique, "Args", args, "Params", ptypes);
			return ftype.result;
		}

		int i=0;

		for (Expression a: args)
		{
			Type at = a.typeCheck(env, null, scope);
			Type pt = ptypes.get(i++);

			if (!TypeComparator.compatible(pt, at))
			{
				concern(unique, 3256, "Inappropriate type for argument " + i);
				detail2(unique, "Expect", pt, "Actual", at);
			}
		}

		return ftype.result;
	}

	private Type operationApply(
		OperationType optype, Environment env, NameScope scope, boolean unique)
	{
		TypeList ptypes = optype.parameters;

		if (args.size() > ptypes.size())
		{
			concern(unique, 3257, "Too many arguments");
			detail2(unique, "Args", args, "Params", ptypes);
			return optype.result;
		}
		else if (args.size() < ptypes.size())
		{
			concern(unique, 3258, "Too few arguments");
			detail2(unique, "Args", args, "Params", ptypes);
			return optype.result;
		}

		int i=0;

		for (Expression a: args)
		{
			Type at = a.typeCheck(env, null, scope);
			Type pt = ptypes.get(i++);

			if (!TypeComparator.compatible(pt, at))
			{
				concern(unique, 3259, "Inappropriate type for argument " + i);
				detail2(unique, "Expect", pt, "Actual", at);
			}
		}

		return optype.result;
	}
}
