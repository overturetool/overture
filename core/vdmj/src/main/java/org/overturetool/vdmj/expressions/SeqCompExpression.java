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

import java.util.Collections;
import org.overturetool.vdmj.definitions.Definition;
import org.overturetool.vdmj.definitions.MultiBindListDefinition;
import org.overturetool.vdmj.patterns.SetBind;
import org.overturetool.vdmj.pog.POForAllPredicateContext;
import org.overturetool.vdmj.pog.POForAllContext;
import org.overturetool.vdmj.pog.POContextStack;
import org.overturetool.vdmj.pog.ProofObligationList;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.PatternMatchException;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.FlatCheckedEnvironment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.types.BooleanType;
import org.overturetool.vdmj.types.SeqType;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.TypeList;
import org.overturetool.vdmj.values.SeqValue;
import org.overturetool.vdmj.values.Value;
import org.overturetool.vdmj.values.ValueList;


public class SeqCompExpression extends SeqExpression
{
	private static final long serialVersionUID = 1L;
	public final Expression first;
	public final SetBind setbind;
	public final Expression predicate;

	public SeqCompExpression(Expression first, SetBind setbind, Expression predicate)
	{
		super(first);
		this.first = first;
		this.setbind = setbind;
		this.predicate = predicate;
	}

	@Override
	public String toString()
	{
		return "[" + first + " | " + setbind +
			(predicate == null ? "]" : " & " + predicate + "]");
	}

	@Override
	public Type typeCheck(Environment base, TypeList qualifiers, NameScope scope)
	{
		Definition def = new MultiBindListDefinition(location, setbind.getMultipleBindList());
		def.typeCheck(base, scope);

		if (!def.getType().isNumeric())
		{
			report(3155, "List comprehension must define one numeric bind variable");
		}

		Environment local = new FlatCheckedEnvironment(def, base);
		Type etype = first.typeCheck(local, null, scope);

		if (predicate != null)
		{
			if (!predicate.typeCheck(local, null, scope).isType(BooleanType.class))
			{
				predicate.report(3156, "Predicate is not boolean");
			}
		}

		local.unusedCheck();
		return new SeqType(location, etype);
	}

	@Override
	public Value eval(Context ctxt)
	{
		breakpoint.check(location, ctxt);

		if (setbind.pattern.getVariableNames().size() != 1)
		{
			abort(4028, "Sequence comprehension pattern has multiple variables", ctxt);
		}

		ValueList allValues = setbind.getBindValues(ctxt);

		for (Value v: allValues)
		{
			if (!v.isNumeric())
			{
				abort(4029, "Sequence comprehension bindings must be numeric", ctxt);
			}
		}

		Collections.sort(allValues);	// Using compareTo
		ValueList set = new ValueList();

		for (Value val: allValues)
		{
			try
			{
				Context evalContext = new Context(location, "seq comprehension", ctxt);
				evalContext.putList(setbind.pattern.getNamedValues(val, ctxt));

				if (predicate == null || predicate.eval(evalContext).boolValue(ctxt))
				{
					set.add(first.eval(evalContext));
				}
			}
			catch (ValueException e)
			{
				abort(e);
			}
			catch (PatternMatchException e)
			{
				// Ignore mismatches
			}
		}

		return new SeqValue(set);
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

		obligations.addAll(setbind.getProofObligations(ctxt));

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
		return "seq comprehension";
	}
}
