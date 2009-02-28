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
import org.overturetool.vdmj.definitions.MultiBindListDefinition;
import org.overturetool.vdmj.expressions.Expression;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.patterns.Bind;
import org.overturetool.vdmj.pog.LetBeExistsObligation;
import org.overturetool.vdmj.pog.POContextStack;
import org.overturetool.vdmj.pog.ProofObligationList;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.PatternMatchException;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.FlatCheckedEnvironment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.types.BooleanType;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.TypeSet;
import org.overturetool.vdmj.values.Value;
import org.overturetool.vdmj.values.ValueList;

public class LetBeStStatement extends Statement
{
	public final Bind bind;
	public final Expression suchThat;
	public final Statement statement;

	public LetBeStStatement(LexLocation location,
		Bind bind, Expression suchThat, Statement statement)
	{
		super(location);
		this.bind = bind;
		this.suchThat = suchThat;
		this.statement = statement;
	}

	@Override
	public String toString()
	{
		return "let " + bind +
			(suchThat == null ? "" : " be st " + suchThat) + " in " + statement;
	}

	@Override
	public String kind()
	{
		return "let be st";
	}

	@Override
	public Type typeCheck(Environment base, NameScope scope)
	{
		Definition def = new MultiBindListDefinition(location, bind.getMultipleBindList());
		def.typeCheck(base, scope);
		Environment local = new FlatCheckedEnvironment(def, base);

		if (suchThat != null && !suchThat.typeCheck(local, null, scope).isType(BooleanType.class))
		{
			report(3225, "Such that clause is not boolean");
		}

		Type r = statement.typeCheck(local, scope);
		local.unusedCheck();
		return r;
	}

	@Override
	public TypeSet exitCheck()
	{
		return statement.exitCheck();
	}

	@Override
	public Statement findStatement(int lineno)
	{
		Statement found = super.findStatement(lineno);
		if (found != null) return found;
		return statement.findStatement(lineno);
	}

	@Override
	public Value eval(Context ctxt)
	{
		breakpoint.check(location, ctxt);
		ValueList allValues = bind.getBindValues(ctxt);

		for (Value val: allValues)
		{
			try
			{
				Context evalContext = new Context(location, "let be st", ctxt);
				evalContext.put(bind.pattern.getNamedValues(val, ctxt));

				if (suchThat == null || suchThat.eval(evalContext).boolValue(ctxt))
				{
					return statement.eval(evalContext);
				}
			}
	        catch (ValueException e)
	        {
	        	abort(e);
	        }
			catch (PatternMatchException e)
			{
				// Silently continue...
			}
		}

		return abort(4040, "Let be st found no applicable bindings", ctxt);
	}

	@Override
	public ProofObligationList getProofObligations(POContextStack ctxt)
	{
		ProofObligationList obligations = new ProofObligationList();
		obligations.add(new LetBeExistsObligation(this, ctxt));
		obligations.addAll(bind.getProofObligations(ctxt));

		if (suchThat != null)
		{
			obligations.addAll(suchThat.getProofObligations(ctxt));
		}

		obligations.addAll(statement.getProofObligations(ctxt));

		return obligations;
	}
}
