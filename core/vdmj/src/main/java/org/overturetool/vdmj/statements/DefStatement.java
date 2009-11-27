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

import org.overturetool.vdmj.definitions.Definition;
import org.overturetool.vdmj.definitions.DefinitionList;
import org.overturetool.vdmj.expressions.Expression;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.pog.POContextStack;
import org.overturetool.vdmj.pog.POScopeContext;
import org.overturetool.vdmj.pog.ProofObligationList;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.util.Utils;
import org.overturetool.vdmj.values.Value;

public class DefStatement extends LetDefStatement
{
	private static final long serialVersionUID = 1L;

	public DefStatement(LexLocation location, DefinitionList equalsDefs, Statement statement)
	{
		super(location, equalsDefs, statement);
	}

	@Override
	public String toString()
	{
		return "def " + Utils.listToString(localDefs) + " in " + statement;
	}

	@Override
	public String kind()
	{
		return "def";
	}

	@Override
	public Value eval(Context ctxt)
	{
		breakpoint.check(location, ctxt);

		Context evalContext = new Context(location, "def statement", ctxt);

		for (Definition d: localDefs)
		{
			evalContext.putList(d.getNamedValues(evalContext));
		}

		return statement.eval(evalContext);
	}

	@Override
	public Statement findStatement(int lineno)
	{
		return statement.findStatement(lineno);
	}

	@Override
	public Expression findExpression(int lineno)
	{
		return statement.findExpression(lineno);
	}

	@Override
	public ProofObligationList getProofObligations(POContextStack ctxt)
	{
		ProofObligationList obligations = localDefs.getProofObligations(ctxt);

		ctxt.push(new POScopeContext());
		obligations.addAll(statement.getProofObligations(ctxt));
		ctxt.pop();

		return obligations;
	}
}
