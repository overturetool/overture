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

import org.overturetool.vdmj.definitions.DefinitionList;
import org.overturetool.vdmj.expressions.Expression;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.patterns.Pattern;
import org.overturetool.vdmj.pog.POContextStack;
import org.overturetool.vdmj.pog.ProofObligationList;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.PatternMatchException;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.FlatCheckedEnvironment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.types.SetType;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.TypeSet;
import org.overturetool.vdmj.types.UnknownType;
import org.overturetool.vdmj.values.Value;
import org.overturetool.vdmj.values.ValueSet;
import org.overturetool.vdmj.values.VoidValue;

public class ForAllStatement extends Statement
{
	public final Pattern pattern;
	public final Expression set;
	public final Statement statement;

	private Type setType;

	public ForAllStatement(LexLocation location,
		Pattern pattern, Expression set, Statement stmt)
	{
		super(location);
		this.pattern = pattern;
		this.set = set;
		this.statement = stmt;
	}

	@Override
	public String toString()
	{
		return "for all " + pattern + " in set " + set + " do\n" + statement;
	}

	@Override
	public String kind()
	{
		return "for all";
	}

	@Override
	public Type typeCheck(Environment base, NameScope scope)
	{
		setType = set.typeCheck(base, null, scope);
		pattern.typeResolve(base);

		if (setType.isSet())
		{
			SetType st = setType.getSet();
			DefinitionList defs = pattern.getDefinitions(st.setof, NameScope.LOCAL);

			Environment local = new FlatCheckedEnvironment(defs, base);
			Type rt = statement.typeCheck(local, scope);
			local.unusedCheck();
			return rt;
		}
		else
		{
			report(3219, "For all statement does not contain a set type");
			return new UnknownType(location);
		}
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

		try
		{
			ValueSet values = set.eval(ctxt).setValue(ctxt);

			for (Value val: values)
			{
				try
				{
					Context evalContext = new Context(location, "for all", ctxt);
					evalContext.put(pattern.getNamedValues(val, ctxt));
					Value rv = statement.eval(evalContext);

					if (!rv.isVoid())
					{
						return rv;
					}
				}
				catch (PatternMatchException e)
				{
					// Ignore and try others
				}
			}
		}
		catch (ValueException e)
		{
			abort(e);
		}

		return new VoidValue();
	}

	@Override
	public ProofObligationList getProofObligations(POContextStack ctxt)
	{
		ProofObligationList obligations = set.getProofObligations(ctxt);
		obligations.addAll(statement.getProofObligations(ctxt));
		return obligations;
	}
}
