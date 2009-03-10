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

import java.util.List;

import org.overturetool.vdmj.definitions.Definition;
import org.overturetool.vdmj.definitions.MultiBindListDefinition;
import org.overturetool.vdmj.patterns.MultipleBind;
import org.overturetool.vdmj.patterns.MultipleTypeBind;
import org.overturetool.vdmj.patterns.Pattern;
import org.overturetool.vdmj.pog.FiniteSetObligation;
import org.overturetool.vdmj.pog.POForAllPredicateContext;
import org.overturetool.vdmj.pog.POForAllContext;
import org.overturetool.vdmj.pog.POContextStack;
import org.overturetool.vdmj.pog.ProofObligationList;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.FlatCheckedEnvironment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.types.BooleanType;
import org.overturetool.vdmj.types.SetType;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.TypeList;
import org.overturetool.vdmj.values.NameValuePair;
import org.overturetool.vdmj.values.NameValuePairList;
import org.overturetool.vdmj.values.Quantifier;
import org.overturetool.vdmj.values.QuantifierList;
import org.overturetool.vdmj.values.SetValue;
import org.overturetool.vdmj.values.Value;
import org.overturetool.vdmj.values.ValueList;
import org.overturetool.vdmj.values.ValueSet;


public class SetCompExpression extends SetExpression
{
	private static final long serialVersionUID = 1L;
	public final Expression first;
	public final List<MultipleBind> bindings;
	public final Expression predicate;
	private SetType settype;

	public SetCompExpression(Expression first, List<MultipleBind> bindings,
		Expression predicate)
	{
		super(first);
		this.first = first;
		this.bindings = bindings;
		this.predicate = predicate;
	}

	@Override
	public String toString()
	{
		return "{" + first + " | " + bindings +
			(predicate == null ? "}" : " & " + predicate + "}");
	}

	@Override
	public Type typeCheck(Environment base, TypeList qualifiers, NameScope scope)
	{
		Definition def = new MultiBindListDefinition(first.location, bindings);
		def.typeCheck(base, scope);
		Environment local = new FlatCheckedEnvironment(def, base);

		Type etype = first.typeCheck(local, null, scope);

		if (predicate != null)
		{
			if (!predicate.typeCheck(local, null, scope).isType(BooleanType.class))
			{
				predicate.report(3159, "Predicate is not boolean");
			}
		}

		local.unusedCheck();
		return settype = new SetType(location, etype);
	}

	@Override
	public Value eval(Context ctxt)
	{
		breakpoint.check(location, ctxt);

		QuantifierList quantifiers = new QuantifierList();

		for (MultipleBind mb: bindings)
		{
			ValueList bvals = mb.getBindValues(ctxt);

			for (Pattern p: mb.plist)
			{
				Quantifier q = new Quantifier(p, bvals);
				quantifiers.add(q);
			}
		}

		ValueSet set = new ValueSet();
		quantifiers.init();

		try
		{
			while (quantifiers.hasNext(ctxt))
			{
				Context evalContext = new Context(location, "set comprehension", ctxt);
				NameValuePairList nvpl = quantifiers.next();
				boolean matches = true;

				for (NameValuePair nvp: nvpl)
				{
					Value v = evalContext.get(nvp.name);

					if (v == null)
					{
						evalContext.put(nvp.name, nvp.value);
					}
					else
					{
						if (!v.equals(nvp.value))
						{
							matches = false;
							break;	// This quantifier set does not match
						}
					}
				}

				if (matches &&
					(predicate == null ||
					 predicate.eval(evalContext).boolValue(ctxt)))
				{
					set.add(first.eval(evalContext));
				}
			}
		}
		catch (ValueException e)
		{
			return abort(e);
		}

		return new SetValue(set);
	}

	@Override
	public Expression findExpression(int lineno)
	{
		Expression found = super.findExpression(lineno);
		if (found != null) return found;

		found = first.findExpression(lineno);
		if (found != null) return found;

		return predicate == null ? null : predicate.findExpression(lineno);
	}

	@Override
	public ProofObligationList getProofObligations(POContextStack ctxt)
	{
		ProofObligationList obligations = new ProofObligationList();

		ctxt.push(new POForAllPredicateContext(this));
		obligations.addAll(first.getProofObligations(ctxt));
		ctxt.pop();

		boolean finiteTest = false;

		for (MultipleBind mb: bindings)
		{
			obligations.addAll(mb.getProofObligations(ctxt));

			if (mb instanceof MultipleTypeBind)
			{
				finiteTest = true;
			}
		}

		if (finiteTest)
		{
			obligations.add(new FiniteSetObligation(this, settype, ctxt));
		}

		if (predicate != null)
		{
    		ctxt.push(new POForAllContext(this));
    		obligations.addAll(predicate.getProofObligations(ctxt));
    		ctxt.pop();
		}

		return obligations;
	}

	@Override
	public String kind()
	{
		return "set comprehension";
	}
}
