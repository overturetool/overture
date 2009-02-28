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
import org.overturetool.vdmj.definitions.ExplicitFunctionDefinition;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.pog.POContextStack;
import org.overturetool.vdmj.pog.ProofObligationList;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.FlatCheckedEnvironment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.util.Utils;
import org.overturetool.vdmj.values.Value;

public class DefStatement extends Statement
{
	public final DefinitionList equalsDefs;
	public final Statement statement;

	public DefStatement(LexLocation location, DefinitionList equalsDefs, Statement statement)
	{
		super(location);
		this.equalsDefs = equalsDefs;
		this.statement = statement;
	}

	@Override
	public String toString()
	{
		return "def " + Utils.listToString(equalsDefs) + " in " + statement;
	}

	@Override
	public String kind()
	{
		return "def";
	}

	@Override
	public Type typeCheck(Environment base, NameScope scope)
	{
		// Each local definition is in scope for later local definitions...

		Environment local = base;

		for (Definition d: equalsDefs)
		{
			if (d instanceof ExplicitFunctionDefinition)
			{
				// Functions' names are in scope in their bodies, whereas
				// simple variable declarations aren't

				local = new FlatCheckedEnvironment(d, local);	// cumulative
				d.implicitDefinitions(local);
				d.typeCheck(local, scope);
			}
			else
			{
				d.implicitDefinitions(local);
				d.typeCheck(local, scope);
				local = new FlatCheckedEnvironment(d, local);	// cumulative
			}
		}

		local = new FlatCheckedEnvironment(equalsDefs, base);
		Type rt = statement.typeCheck(local, scope);
		local.unusedCheck();
		return rt;
	}

	@Override
	public Value eval(Context ctxt)
	{
		breakpoint.check(location, ctxt);

		Context evalContext = new Context(location, "def statement", ctxt);

		for (Definition d: equalsDefs)
		{
			evalContext.put(d.getNamedValues(evalContext));
		}

		return statement.eval(evalContext);
	}

	@Override
	public ProofObligationList getProofObligations(POContextStack ctxt)
	{
		ProofObligationList obligations = equalsDefs.getProofObligations(ctxt);
		obligations.addAll(statement.getProofObligations(ctxt));
		return obligations;
	}
}
