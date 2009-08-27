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

import org.overturetool.vdmj.definitions.Definition;
import org.overturetool.vdmj.definitions.MultiBindListDefinition;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.patterns.Bind;
import org.overturetool.vdmj.patterns.SetBind;
import org.overturetool.vdmj.patterns.TypeBind;
import org.overturetool.vdmj.pog.POForAllContext;
import org.overturetool.vdmj.pog.POContextStack;
import org.overturetool.vdmj.pog.ProofObligationList;
import org.overturetool.vdmj.pog.UniqueExistenceObligation;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.PatternMatchException;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.FlatCheckedEnvironment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.TypeList;
import org.overturetool.vdmj.values.Value;
import org.overturetool.vdmj.values.ValueList;

public class IotaExpression extends Expression
{
	private static final long serialVersionUID = 1L;
	public final Bind bind;
	public final Expression predicate;

	public IotaExpression(LexLocation location, Bind bind, Expression predicate)
	{
		super(location);
		this.bind = bind;
		this.predicate = predicate;
	}

	@Override
	public String toString()
	{
		return "(iota " + bind + " & " + predicate + ")";
	}

	@Override
	public Type typeCheck(Environment base, TypeList qualifiers, NameScope scope)
	{
		Definition def = new MultiBindListDefinition(location, bind.getMultipleBindList());
		def.typeCheck(base, scope);
		Type rt = null;

		if (bind instanceof SetBind)
		{
			SetBind sb = (SetBind)bind;
			rt = sb.set.typeCheck(base, null, scope);

			if (rt.isSet())
			{
				rt = rt.getSet().setof;
			}
			else
			{
				report(3112, "Iota set bind is not a set");
			}
		}
		else
		{
			TypeBind tb = (TypeBind)bind;
			rt = tb.type;
		}

		Environment local = new FlatCheckedEnvironment(def, base);
		predicate.typeCheck(local, null, scope);
		local.unusedCheck();
		return rt;
	}

	@Override
	public Value eval(Context ctxt)
	{
		breakpoint.check(location, ctxt);

		ValueList allValues = bind.getBindValues(ctxt);
		Value result = null;

		for (Value val: allValues)
		{
			try
			{
				Context evalContext = new Context(location, "iota", ctxt);
				evalContext.putList(bind.pattern.getNamedValues(val, ctxt));

				if (predicate.eval(evalContext).boolValue(ctxt))
				{
					if (result != null && !result.equals(val))
					{
						abort(4013, "Iota selects more than one result", ctxt);
					}

					result = val;
				}
			}
	        catch (ValueException e)
	        {
	        	abort(e);
	        }
			catch (PatternMatchException e)
			{
				// Ignore pattern mismatches
			}
	    }

		if (result != null)
		{
			return result;
		}

		return abort(4014, "Iota does not select a result", ctxt);
	}

	@Override
	public Expression findExpression(int lineno)
	{
		Expression found = super.findExpression(lineno);
		if (found != null) return found;

		return predicate.findExpression(lineno);
	}

	@Override
	public ProofObligationList getProofObligations(POContextStack ctxt)
	{
		ProofObligationList obligations = bind.getProofObligations(ctxt);
		obligations.add(new UniqueExistenceObligation(this, ctxt));

		ctxt.push(new POForAllContext(this));
		obligations.addAll(predicate.getProofObligations(ctxt));
		ctxt.pop();

		return obligations;
	}

	@Override
	public String kind()
	{
		return "iota";
	}
}
