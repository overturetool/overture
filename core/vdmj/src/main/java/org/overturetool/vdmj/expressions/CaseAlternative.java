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

import java.io.Serializable;

import org.overturetool.vdmj.definitions.DefinitionList;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.patterns.ExpressionPattern;
import org.overturetool.vdmj.patterns.Pattern;
import org.overturetool.vdmj.pog.POCaseContext;
import org.overturetool.vdmj.pog.POContextStack;
import org.overturetool.vdmj.pog.PONotCaseContext;
import org.overturetool.vdmj.pog.ProofObligationList;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.PatternMatchException;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.FlatCheckedEnvironment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.values.Value;

public class CaseAlternative implements Serializable
{
	private static final long serialVersionUID = 1L;

	public final LexLocation location;
	public final Expression cexp;
	public final Pattern pattern;
	public final Expression result;

	private DefinitionList defs = null;

	public CaseAlternative(Expression cexp, Pattern pattern, Expression result)
	{
		this.location = pattern.location;
		this.cexp = cexp;
		this.pattern = pattern;
		this.result = result;
	}

	@Override
	public String toString()
	{
		return "case " + pattern + " -> " + result;
	}

	public Type typeCheck(Environment base, NameScope scope, Type expType)
	{
		if (defs == null)
		{
			defs = new DefinitionList();
			pattern.typeResolve(base);

			if (pattern instanceof ExpressionPattern)
			{
				// Only expression patterns need type checking...
				ExpressionPattern ep = (ExpressionPattern)pattern;
				ep.exp.typeCheck(base, null, scope);
			}

			pattern.typeResolve(base);
			defs.addAll(pattern.getDefinitions(expType, NameScope.LOCAL));
		}

		defs.typeCheck(base, scope);
		Environment local = new FlatCheckedEnvironment(defs, base);
		Type r = result.typeCheck(local, null, scope);
		local.unusedCheck();
		return r;
	}

	public Value eval(Value val, Context ctxt)
	{
		Context evalContext = new Context(location, "case alternative", ctxt);

		try
		{
			evalContext.putList(pattern.getNamedValues(val, ctxt));
			return result.eval(evalContext);
		}
		catch (PatternMatchException e)
		{
			// Silently fail (CasesExpression will try the others)
		}

		return null;
	}

	public ProofObligationList getProofObligations(POContextStack ctxt, Type type)
	{
		ProofObligationList obligations = new ProofObligationList();

		ctxt.push(new POCaseContext(pattern, type, cexp));
		obligations.addAll(result.getProofObligations(ctxt));
		ctxt.pop();
		ctxt.push(new PONotCaseContext(pattern, type, cexp));

		return obligations;
	}
}
