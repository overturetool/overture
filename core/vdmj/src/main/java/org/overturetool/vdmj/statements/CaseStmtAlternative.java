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

import java.io.Serializable;

import org.overturetool.vdmj.definitions.DefinitionList;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.patterns.ExpressionPattern;
import org.overturetool.vdmj.patterns.Pattern;
import org.overturetool.vdmj.pog.POContextStack;
import org.overturetool.vdmj.pog.ProofObligationList;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.PatternMatchException;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.FlatCheckedEnvironment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.typechecker.TypeCheckException;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.TypeSet;
import org.overturetool.vdmj.values.Value;

public class CaseStmtAlternative implements Serializable
{
	private static final long serialVersionUID = 1L;

	public final LexLocation location;
	public final Pattern pattern;
	public final Statement statement;

	private DefinitionList defs = null;

	public CaseStmtAlternative(Pattern pattern, Statement stmt)
	{
		this.location = stmt.location;
		this.pattern = pattern;
		this.statement = stmt;
	}

	@Override
	public String toString()
	{
		return "case " + pattern + " -> " + statement;
	}

	public Type typeCheck(Environment base, NameScope scope, Type ctype)
	{
		if (defs == null)
		{
			defs = new DefinitionList();

			if (pattern instanceof ExpressionPattern)
			{
				// Only expression patterns need type checking...
				ExpressionPattern ep = (ExpressionPattern)pattern;
				ep.exp.typeCheck(base, null, scope);
			}

			try
			{
				pattern.typeResolve(base);
				defs.addAll(pattern.getDefinitions(ctype, NameScope.LOCAL));
			}
			catch (TypeCheckException e)
			{
				defs = null;
				throw e;
			}
		}

		defs.typeCheck(base, scope);
		Environment local = new FlatCheckedEnvironment(defs, base, scope);
		Type r = statement.typeCheck(local, scope);
		local.unusedCheck();
		return r;
	}

	public TypeSet exitCheck()
	{
		return statement.exitCheck();
	}

	public Value eval(Value val, Context ctxt)
	{
		Context evalContext = new Context(location, "case alternative", ctxt);

		try
		{
			evalContext.putList(pattern.getNamedValues(val, ctxt));
			return statement.eval(evalContext);
		}
		catch (PatternMatchException e)
		{
			// CasesStatement tries the others
		}

		return null;
	}

	public ProofObligationList getProofObligations(POContextStack ctxt)
	{
		ProofObligationList obligations = new ProofObligationList();
		obligations.addAll(statement.getProofObligations(ctxt));
		return obligations;
	}
}
