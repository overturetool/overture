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

package org.overturetool.vdmj.expressions;

import org.overturetool.vdmj.Release;
import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.definitions.Definition;
import org.overturetool.vdmj.definitions.ExplicitFunctionDefinition;
import org.overturetool.vdmj.definitions.ImplicitFunctionDefinition;
import org.overturetool.vdmj.definitions.PerSyncDefinition;
import org.overturetool.vdmj.pog.FunctionApplyObligation;
import org.overturetool.vdmj.pog.MapApplyObligation;
import org.overturetool.vdmj.pog.POContextStack;
import org.overturetool.vdmj.pog.ProofObligationList;
import org.overturetool.vdmj.pog.RecursiveObligation;
import org.overturetool.vdmj.pog.SeqApplyObligation;
import org.overturetool.vdmj.pog.SubTypeObligation;
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
import org.overturetool.vdmj.values.Value;
import org.overturetool.vdmj.values.ValueList;

public class ApplyExpression extends Expression
{
	private static final long serialVersionUID = 1L;

	public final Expression root;
	public final ExpressionList args;

	private Type type;
	private TypeList argtypes;
	private Definition recursive = null;

	public ApplyExpression(Expression root)
	{
		super(root);
		this.root = root;
		this.args = new ExpressionList();	// ie. "()"
	}

	public ApplyExpression(Expression root, ExpressionList args)
	{
		super(root);
		this.root = root;
		this.args = args;
	}

	@Override
	public String toString()
	{
		return root + "("+ Utils.listToString(args) + ")";
	}

	@Override
	public Type typeCheck(Environment env, TypeList qualifiers, NameScope scope)
	{
		argtypes = new TypeList();

		for (Expression a: args)
		{
			argtypes.add(a.typeCheck(env, null, scope));
		}

		type = root.typeCheck(env, argtypes, scope);

		if (type.isUnknown())
		{
			return type;
		}

		Definition func = env.getEnclosingDefinition();

		boolean inFunction =
			(func instanceof ExplicitFunctionDefinition ||
			 func instanceof ImplicitFunctionDefinition ||
			 func instanceof PerSyncDefinition);

		if (inFunction && root instanceof VariableExpression)
		{
			VariableExpression var = (VariableExpression)root;

			if (func instanceof ExplicitFunctionDefinition)
			{
				ExplicitFunctionDefinition def = (ExplicitFunctionDefinition)func;

    			if (var.name.equals(def.name))
    			{
    				recursive = def;
    				def.recursive = true;
    			}
			}
			else if (func instanceof ImplicitFunctionDefinition)
			{
				ImplicitFunctionDefinition def = (ImplicitFunctionDefinition)func;

    			if (var.name.equals(def.name))
    			{
    				recursive = def;
    				def.recursive = true;
    			}
			}
		}

		boolean isSimple = !type.isUnion();
		TypeSet results = new TypeSet();

		if (type.isFunction())
		{
			FunctionType ft = type.getFunction();
			ft.typeResolve(env, null);
			results.add(functionApply(isSimple, ft));
		}

		if (type.isOperation())
		{
			OperationType ot = type.getOperation();
			ot.typeResolve(env, null);

			if (inFunction && Settings.release == Release.VDM_10)
			{
				report(3300, "Operation " + ot + " cannot be called from a function");
				results.add(new UnknownType(location));
			}
			else
			{
    			results.add(operationApply(isSimple, ot));
			}
		}

		if (type.isSeq())
		{
			SeqType seq = type.getSeq();
			results.add(sequenceApply(isSimple, seq));
		}

		if (type.isMap())
		{
			MapType map = type.getMap();
			results.add(mapApply(isSimple, map));
		}

		if (results.isEmpty())
		{
			report(3054, "Type " + type + " cannot be applied");
			return new UnknownType(location);
		}

		return results.getType(location);	// Union of possible applications
	}

	private Type sequenceApply(boolean isSimple, SeqType seq)
	{
		if (args.size() != 1)
		{
			concern(isSimple, 3055, "Sequence selector must have one argument");
		}
		else if (!argtypes.get(0).isNumeric())
		{
			concern(isSimple, 3056, "Sequence application argument must be numeric");
		}
		else if (seq.empty)
		{
			concern(isSimple, 3268, "Empty sequence cannot be applied");
		}

		return seq.seqof;
	}

	private Type mapApply(boolean isSimple, MapType map)
	{
		if (args.size() != 1)
		{
			concern(isSimple, 3057, "Map application must have one argument");
		}
		else if (map.empty)
		{
			concern(isSimple, 3267, "Empty map cannot be applied");
		}

		Type argtype = argtypes.get(0);

		if (!TypeComparator.compatible(map.from, argtype))
		{
			concern(isSimple, 3058, "Map application argument is incompatible type");
			detail2(isSimple, "Map domain", map.from, "Argument", argtype);
		}

		return map.to;
	}

	private Type functionApply(boolean isSimple, FunctionType ftype)
	{
		TypeList ptypes = ftype.parameters;

		if (args.size() > ptypes.size())
		{
			concern(isSimple, 3059, "Too many arguments");
			detail2(isSimple, "Args", args, "Params", ptypes);
			return ftype.result;
		}
		else if (args.size() < ptypes.size())
		{
			concern(isSimple, 3060, "Too few arguments");
			detail2(isSimple, "Args", args, "Params", ptypes);
			return ftype.result;
		}

		int i=0;

		for (Type at: argtypes)
		{
			Type pt = ptypes.get(i++);

			if (!TypeComparator.compatible(pt, at))
			{
				concern(isSimple, 3061, "Inappropriate type for argument " + i);
				detail2(isSimple, "Expect", pt, "Actual", at);
			}
		}

		return ftype.result;
	}

	private Type operationApply(boolean isSimple, OperationType optype)
	{
		TypeList ptypes = optype.parameters;

		if (args.size() > ptypes.size())
		{
			concern(isSimple, 3062, "Too many arguments");
			detail2(isSimple, "Args", args, "Params", ptypes);
			return optype.result;
		}
		else if (args.size() < ptypes.size())
		{
			concern(isSimple, 3063, "Too few arguments");
			detail2(isSimple, "Args", args, "Params", ptypes);
			return optype.result;
		}

		int i=0;

		for (Type at: argtypes)
		{
			Type pt = ptypes.get(i++);

			if (!TypeComparator.compatible(pt, at))
			{
				concern(isSimple, 3064, "Inappropriate type for argument " + i);
				detail2(isSimple, "Expect", pt, "Actual", at);
			}
		}

		return optype.result;
	}

	@Override
	public Expression findExpression(int lineno)
	{
		Expression found = super.findExpression(lineno);
		if (found != null) return found;

		found = root.findExpression(lineno);
		if (found != null) return found;

		return args.findExpression(lineno);
	}

	@Override
	public Value eval(Context ctxt)
	{
		breakpoint.check(location, ctxt);

    	try
    	{
    		Value object = root.eval(ctxt).deref();

			if (object instanceof FunctionValue)
    		{
        		ValueList argvals = new ValueList();

         		for (Expression arg: args)
        		{
        			argvals.add(arg.eval(ctxt));
        		}

           		FunctionValue fv = object.functionValue(ctxt);
           		return fv.eval(location, argvals, ctxt);
    		}
			else if (object instanceof OperationValue)
    		{
        		ValueList argvals = new ValueList();

         		for (Expression arg: args)
        		{
        			argvals.add(arg.eval(ctxt));
        		}

         		OperationValue ov = object.operationValue(ctxt);
           		return ov.eval(location, argvals, ctxt);
    		}
			else if (object instanceof SeqValue)
    		{
    			Value arg = args.get(0).eval(ctxt);
    			SeqValue sv = (SeqValue)object;
    			return sv.get(arg, ctxt);
    		}
			else if (object instanceof MapValue)
    		{
    			Value arg = args.get(0).eval(ctxt);
    			MapValue mv = (MapValue)object;
    			return mv.lookup(arg, ctxt);
    		}
			else
			{
    			return abort(4003, "Value " + object + " cannot be applied", ctxt);
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

		if (type.isMap())
		{
			MapType m = type.getMap();
			obligations.add(new MapApplyObligation(root, args.get(0), ctxt));
			Type atype = ctxt.checkType(args.get(0), argtypes.get(0));

			if (!TypeComparator.isSubType(atype, m.from))
			{
				obligations.add(new SubTypeObligation(args.get(0), m.from, atype, ctxt));
			}
		}

		if (type.isFunction())
		{
			FunctionType f = type.getFunction();
			String prename = root.getPreName();

			if (prename == null || !prename.equals(""))
			{
				obligations.add(new FunctionApplyObligation(root, args, prename, ctxt));
			}

			int i=0;

			for (Type at: argtypes)
			{
				at = ctxt.checkType(args.get(i), at);
				Type pt = f.parameters.get(i);

				if (!TypeComparator.isSubType(at, pt))
				{
					obligations.add(new SubTypeObligation(args.get(i), pt, at, ctxt));
				}

				i++;
			}

			if (recursive != null)
			{
				if (recursive instanceof ExplicitFunctionDefinition)
				{
					ExplicitFunctionDefinition def = (ExplicitFunctionDefinition)recursive;

					if (def.measure != null)
					{
						obligations.add(new RecursiveObligation(def, this, ctxt));
					}
				}
				else if (recursive instanceof ImplicitFunctionDefinition)
				{
					ImplicitFunctionDefinition def = (ImplicitFunctionDefinition)recursive;

					if (def.measure != null)
					{
						obligations.add(new RecursiveObligation(def, this, ctxt));
					}
				}
			}
		}

		if (type.isSeq())
		{
			obligations.add(new SeqApplyObligation(root, args.get(0), ctxt));
		}

		obligations.addAll(root.getProofObligations(ctxt));

		for (Expression arg: args)
		{
			obligations.addAll(arg.getProofObligations(ctxt));
		}

		return obligations;
	}

	@Override
	public String kind()
	{
		return "apply";
	}
}
