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

import org.overturetool.vdmj.definitions.ClassDefinition;
import org.overturetool.vdmj.definitions.Definition;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.pog.POContextStack;
import org.overturetool.vdmj.pog.ProofObligationList;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.types.BooleanType;
import org.overturetool.vdmj.types.ClassType;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.TypeList;
import org.overturetool.vdmj.values.BooleanValue;
import org.overturetool.vdmj.values.ObjectValue;
import org.overturetool.vdmj.values.Value;
import org.overturetool.vdmj.values.ValueList;

public class IsOfClassExpression extends Expression
{
	private static final long serialVersionUID = 1L;
	public final LexNameToken classname;
	public final Expression exp;

	private ClassType classType;

	public IsOfClassExpression(
		LexLocation start, LexNameToken classname, Expression exp)
	{
		super(start);

		this.classname = classname.getExplicit(false);
		this.exp = exp;
	}

	@Override
	public Value eval(Context ctxt)
	{
		breakpoint.check(location, ctxt);
		classname.location.hit();

		try
		{
			Value v = exp.eval(ctxt).deref();

			if (!(v instanceof ObjectValue))
			{
				return new BooleanValue(false);
			}

			ObjectValue ov = v.objectValue(ctxt);
			return new BooleanValue(isOfClass(ov, classname.name));
		}
		catch (ValueException e)
		{
			return abort(e);
		}
	}

	private boolean isOfClass(ObjectValue obj, String name)
	{
		if (obj.type.name.name.equals(name))
		{
			return true;
		}
		else
		{
			for (ObjectValue objval: obj.superobjects)
			{
				if (isOfClass(objval, name))
				{
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public Expression findExpression(int lineno)
	{
		Expression found = super.findExpression(lineno);
		if (found != null) return found;

		return exp.findExpression(lineno);
	}

	@Override
	public ProofObligationList getProofObligations(POContextStack ctxt)
	{
		ctxt.noteType(exp, classType);
		return exp.getProofObligations(ctxt);
	}

	@Override
	public String kind()
	{
		return "isofclass";
	}

	@Override
	public String toString()
	{
		return "isofclass(" + classname + "," + exp + ")";
	}

	@Override
	public Type typeCheck(Environment env, TypeList qualifiers, NameScope scope)
	{
		Definition cls = env.findType(classname);

		if (cls == null || !(cls instanceof ClassDefinition))
		{
			report(3115, "Undefined class type: " + classname.name);
		}
		else
		{
			classType = (ClassType)cls.getType();
		}

		Type rt = exp.typeCheck(env, null, scope);

		if (!rt.isClass())
		{
			exp.report(3266, "Argument is not an object");
		}

		return new BooleanType(location);
	}

	@Override
	public ValueList getValues(Context ctxt)
	{
		return exp.getValues(ctxt);
	}
}
