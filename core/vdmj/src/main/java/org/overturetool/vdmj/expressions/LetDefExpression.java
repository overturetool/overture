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

import org.overturetool.vdmj.definitions.ClassDefinition;
import org.overturetool.vdmj.definitions.Definition;
import org.overturetool.vdmj.definitions.DefinitionList;
import org.overturetool.vdmj.definitions.ExplicitFunctionDefinition;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.pog.POLetDefContext;
import org.overturetool.vdmj.pog.POContextStack;
import org.overturetool.vdmj.pog.ProofObligationList;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.FlatCheckedEnvironment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.TypeList;
import org.overturetool.vdmj.util.Utils;
import org.overturetool.vdmj.values.FunctionValue;
import org.overturetool.vdmj.values.NameValuePair;
import org.overturetool.vdmj.values.NameValuePairList;
import org.overturetool.vdmj.values.ObjectValue;
import org.overturetool.vdmj.values.Value;

public class LetDefExpression extends Expression
{
	private static final long serialVersionUID = 1L;
	public final DefinitionList localDefs;
	public final Expression expression;

	public LetDefExpression(LexLocation location,
				DefinitionList localDefs, Expression expression)
	{
		super(location);
		this.localDefs = localDefs;
		this.expression = expression;
	}

	@Override
	public String toString()
	{
		return "let " + Utils.listToString(localDefs) + " in " + expression;
	}

	@Override
	public Type typeCheck(Environment env, TypeList qualifiers, NameScope scope)
	{
		// Each local definition is in scope for later local definitions...

		Environment local = env;

		for (Definition d: localDefs)
		{
			if (d instanceof ExplicitFunctionDefinition)
			{
				// Functions' names are in scope in their bodies, whereas
				// simple variable declarations aren't

				local = new FlatCheckedEnvironment(d, local);	// cumulative
				d.implicitDefinitions(local);
				d.typeResolve(local);

				if (env.isVDMPP())
				{
					ClassDefinition cdef = env.findClassDefinition();
					d.setClassDefinition(cdef);
					d.setAccessSpecifier(d.accessSpecifier.getStatic(true));
				}

				d.typeCheck(local, scope);
			}
			else
			{
				d.implicitDefinitions(local);
				d.typeResolve(local);
				d.typeCheck(local, scope);
				local = new FlatCheckedEnvironment(d, local);	// cumulative
			}
		}

		Type r = expression.typeCheck(local, null, scope);
		local.unusedCheck(env);
		return r;
	}

	@Override
	public Expression findExpression(int lineno)
	{
		Expression found = super.findExpression(lineno);
		if (found != null) return found;

		found = localDefs.findExpression(lineno);
		if (found != null) return found;

		return expression.findExpression(lineno);
	}

	@Override
	public Value eval(Context ctxt)
	{
		breakpoint.check(location, ctxt);

		Context evalContext = new Context(location, "let expression", ctxt);

		LexNameToken sname = new LexNameToken(location.module, "self", location);
		ObjectValue self = (ObjectValue)ctxt.check(sname);

		for (Definition d: localDefs)
		{
			NameValuePairList values = d.getNamedValues(evalContext);

			if (self != null && d instanceof ExplicitFunctionDefinition)
			{
				for (NameValuePair nvp: values)
				{
					if (nvp.value instanceof FunctionValue)
					{
						FunctionValue fv = (FunctionValue)nvp.value;
						fv.setSelf(self);
					}
				}
			}

			evalContext.putList(values);
		}

		return expression.eval(evalContext);
	}

	@Override
	public ProofObligationList getProofObligations(POContextStack ctxt)
	{
		ProofObligationList obligations = localDefs.getProofObligations(ctxt);

		ctxt.push(new POLetDefContext(this));
		obligations.addAll(expression.getProofObligations(ctxt));
		ctxt.pop();

		return obligations;
	}

	@Override
	public String kind()
	{
		return "let def";
	}
}
