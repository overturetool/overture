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

import org.overturetool.vdmj.definitions.MultiBindListDefinition;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.patterns.MultipleBind;
import org.overturetool.vdmj.patterns.Pattern;
import org.overturetool.vdmj.pog.LetBeExistsObligation;
import org.overturetool.vdmj.pog.POForAllContext;
import org.overturetool.vdmj.pog.POForAllPredicateContext;
import org.overturetool.vdmj.pog.POContextStack;
import org.overturetool.vdmj.pog.ProofObligationList;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.FlatCheckedEnvironment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.types.BooleanType;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.TypeList;
import org.overturetool.vdmj.values.NameValuePair;
import org.overturetool.vdmj.values.NameValuePairList;
import org.overturetool.vdmj.values.Quantifier;
import org.overturetool.vdmj.values.QuantifierList;
import org.overturetool.vdmj.values.Value;
import org.overturetool.vdmj.values.ValueList;

public class LetBeStExpression extends Expression
{
	private static final long serialVersionUID = 1L;
	public final MultipleBind bind;
	public final Expression suchThat;
	public final Expression value;
	private MultiBindListDefinition def = null;

	public LetBeStExpression(LexLocation location,
				MultipleBind bind, Expression suchThat, Expression value)
	{
		super(location);
		this.bind = bind;
		this.suchThat = suchThat;
		this.value = value;
	}

	@Override
	public String toString()
	{
		return "let " + bind +
			(suchThat == null ? "" : " be st " + suchThat) + " in " + value;
	}

	@Override
	public Type typeCheck(Environment base, TypeList qualifiers, NameScope scope)
	{
		def = new MultiBindListDefinition(location, bind.getMultipleBindList());
		def.typeCheck(base, scope);
		Environment local = new FlatCheckedEnvironment(def, base, scope);

		if (suchThat != null &&
			!suchThat.typeCheck(local, null, scope).isType(BooleanType.class))
		{
			report(3117, "Such that clause is not boolean");
		}

		Type r = value.typeCheck(local, null, scope);
		local.unusedCheck();
		return r;
	}

	@Override
	public Expression findExpression(int lineno)
	{
		Expression found = super.findExpression(lineno);
		if (found != null) return found;

		found = suchThat.findExpression(lineno);
		if (found != null) return found;

		return value.findExpression(lineno);
	}

	@Override
	public Value eval(Context ctxt)
	{
		breakpoint.check(location, ctxt);

		QuantifierList quantifiers = new QuantifierList();

		for (MultipleBind mb: def.bindings)
		{
			ValueList bvals = mb.getBindValues(ctxt);

			for (Pattern p: mb.plist)
			{
				Quantifier q = new Quantifier(p, bvals);
				quantifiers.add(q);
			}
		}

		quantifiers.init();

		try
		{
			while (quantifiers.hasNext(ctxt))
			{
				Context evalContext = new Context(location, "let be st expression", ctxt);
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
					(suchThat == null || suchThat.eval(evalContext).boolValue(ctxt)))
				{
					return value.eval(evalContext);
				}
			}
		}
        catch (ValueException e)
        {
        	abort(e);
        }

		return abort(4015, "Let be st found no applicable bindings", ctxt);
	}

	@Override
	public ProofObligationList getProofObligations(POContextStack ctxt)
	{
		ProofObligationList obligations = new ProofObligationList();
		obligations.add(new LetBeExistsObligation(this, ctxt));
		obligations.addAll(bind.getProofObligations(ctxt));

		if (suchThat != null)
		{
			ctxt.push(new POForAllContext(this));
			obligations.addAll(suchThat.getProofObligations(ctxt));
			ctxt.pop();
		}

		ctxt.push(new POForAllPredicateContext(this));
		obligations.addAll(value.getProofObligations(ctxt));
		ctxt.pop();

		return obligations;
	}

	@Override
	public String kind()
	{
		return "let be st";
	}
}
