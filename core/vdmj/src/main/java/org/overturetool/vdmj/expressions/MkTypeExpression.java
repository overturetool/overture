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

import java.util.Iterator;

import org.overturetool.vdmj.definitions.Definition;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.pog.POContextStack;
import org.overturetool.vdmj.pog.ProofObligationList;
import org.overturetool.vdmj.pog.SubTypeObligation;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.typechecker.TypeComparator;
import org.overturetool.vdmj.types.Field;
import org.overturetool.vdmj.types.NamedType;
import org.overturetool.vdmj.types.RecordType;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.TypeList;
import org.overturetool.vdmj.types.UnknownType;
import org.overturetool.vdmj.util.Utils;
import org.overturetool.vdmj.values.RecordValue;
import org.overturetool.vdmj.values.Value;
import org.overturetool.vdmj.values.ValueList;


public class MkTypeExpression extends Expression
{
	private static final long serialVersionUID = 1L;
	public final LexNameToken typename;
	public final ExpressionList args;

	private RecordType recordType = null;
	public TypeList argTypes = null;

	public MkTypeExpression(LexNameToken typename, ExpressionList args)
	{
		super(typename.location);
		this.typename = typename;
		this.args = args;
	}

	@Override
	public String toString()
	{
		return "mk_" + typename + "(" + Utils.listToString(args) + ")";
	}

	@Override
	public Type typeCheck(Environment env, TypeList qualifiers, NameScope scope)
	{
		Definition typeDef = env.findType(typename);

		if (typeDef == null)
		{
			report(3126, "Unknown type '" + typename + "' in constructor");
			return new UnknownType(location);
		}

		Type rec = typeDef.getType();

		while (rec instanceof NamedType)
		{
			NamedType nrec = (NamedType)rec;
			rec = nrec.type;
		}

		if (!(rec instanceof RecordType))
		{
			report(3127, "Type '" + typename + "' is not a record type");
			return rec;
		}

		recordType = (RecordType)rec;

		if (typename.explicit)
		{
			// If the type name is explicit, the Type ought to have an explicit
			// name. This only really affects trace expansion.

			recordType =
				new RecordType(recordType.name.getExplicit(true), recordType.fields);
		}

		if (recordType.fields.size() != args.size())
		{
			report(3128, "Record and constructor do not have same number of fields");
			return rec;
		}

		int i=0;
		Iterator<Field> fiter = recordType.fields.iterator();
		argTypes = new TypeList();

		for (Expression arg: args)
		{
			Type fieldType = fiter.next().type;
			Type argType = arg.typeCheck(env, null, scope);
			i++;

			if (!TypeComparator.compatible(fieldType, argType))
			{
				report(3129, "Constructor field " + i + " is of wrong type");
				detail2("Expected", fieldType, "Actual", argType);
			}

			argTypes.add(argType);
		}

		return recordType;
	}

	@Override
	public Value eval(Context ctxt)
	{
		breakpoint.check(location, ctxt);

		ValueList argvals = new ValueList();

		for (Expression e: args)
		{
			argvals.add(e.eval(ctxt));
		}

		try
		{
    		return new RecordValue(recordType, argvals, ctxt);
		}
		catch (ValueException e)
		{
			return abort(e);
		}
	}

	@Override
	public Expression findExpression(int lineno)
	{
		Expression found = super.findExpression(lineno);
		if (found != null) return found;

		return args.findExpression(lineno);
	}

	@Override
	public ProofObligationList getProofObligations(POContextStack ctxt)
	{
		ProofObligationList list = args.getProofObligations(ctxt);
		Iterator<Type> it = argTypes.iterator();
		int i = 0;

		for (Field f: recordType.fields)
		{
			Type atype = it.next();

			if (!TypeComparator.isSubType(
				ctxt.checkType(args.get(i), atype), f.type))
			{
				list.add(new SubTypeObligation(args.get(i), f.type, atype, ctxt));
			}

			i++;
		}

		if (recordType.invdef != null)
		{
			list.add(new SubTypeObligation(this, recordType, recordType, ctxt));
		}

		return list;
	}

	@Override
	public String kind()
	{
		return "mk_";
	}

	@Override
	public ValueList getValues(Context ctxt)
	{
		return args.getValues(ctxt);
	}
}
