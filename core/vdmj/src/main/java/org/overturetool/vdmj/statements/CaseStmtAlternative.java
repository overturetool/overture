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
import org.overturetool.vdmj.patterns.PatternList;
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
	public final PatternList plist;
	public final Statement statement;

	private DefinitionList defs = null;

	public CaseStmtAlternative(PatternList plist, Statement stmt)
	{
		this.location = stmt.location;
		this.plist = plist;
		this.statement = stmt;
	}

	@Override
	public String toString()
	{
		return "case " + plist + " -> " + statement;
	}

	public Type typeCheck(Environment base, NameScope scope, Type ctype)
	{
		if (defs == null)
		{
			defs = new DefinitionList();

			for (Pattern p: plist)
			{
				if (p instanceof ExpressionPattern)
				{
					// Only expression patterns need type checking...
					ExpressionPattern ep = (ExpressionPattern)p;
					ep.exp.typeCheck(base, null, scope);
				}

				try
				{
					p.typeResolve(base);
					defs.addAll(p.getDefinitions(ctype, NameScope.LOCAL));
				}
				catch (TypeCheckException e)
				{
					defs = null;
					throw e;
				}
			}
		}

		defs.typeCheck(base, scope);
		Environment local = new FlatCheckedEnvironment(defs, base);
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

		for (Pattern p: plist)
		{
			try
			{
				evalContext.putList(p.getNamedValues(val, ctxt));
				return statement.eval(evalContext);
			}
			catch (PatternMatchException e)
			{
				// Try them all
			}
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
