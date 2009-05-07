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
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.patterns.PatternBind;
import org.overturetool.vdmj.patterns.SetBind;
import org.overturetool.vdmj.patterns.TypeBind;
import org.overturetool.vdmj.pog.POContextStack;
import org.overturetool.vdmj.pog.ProofObligationList;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.PatternMatchException;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.FlatCheckedEnvironment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.TypeSet;
import org.overturetool.vdmj.values.Value;
import org.overturetool.vdmj.values.ValueSet;

public class TixeStmtAlternative
{
	public final PatternBind patternBind;
	public final Statement statement;

	public TixeStmtAlternative(PatternBind patternBind, Statement stmt)
	{
		this.patternBind = patternBind;
		this.statement = stmt;
	}

	@Override
	public String toString()
	{
		return patternBind + " |-> " + statement;
	}

	public void typeCheck(Environment base, NameScope scope, Type ext)
	{
		patternBind.typeCheck(base, scope, ext);
		DefinitionList defs = patternBind.getDefinitions();
		defs.typeCheck(base, scope);
		Environment local = new FlatCheckedEnvironment(defs, base);
		statement.typeCheck(local, scope);
		local.unusedCheck();
	}

	public TypeSet exitCheck()
	{
		return statement.exitCheck();
	}

	public Value eval(LexLocation location, Value exval, Context ctxt)
	{
		Context evalContext = null;

		try
		{
			if (patternBind.pattern != null)
			{
				evalContext = new Context(location, "tixe pattern", ctxt);
				evalContext.put(patternBind.pattern.getNamedValues(exval, ctxt));
			}
			else if (patternBind.bind instanceof SetBind)
			{
				SetBind setbind = (SetBind)patternBind.bind;
				ValueSet set = setbind.set.eval(ctxt).setValue(ctxt);

				if (set.contains(exval))
				{
					evalContext = new Context(location, "tixe set", ctxt);
					evalContext.put(setbind.pattern.getNamedValues(exval, ctxt));
				}
				else
				{
					setbind.abort(4049, "Value " + exval + " is not in set bind", ctxt);
				}
			}
			else
			{
				TypeBind typebind = (TypeBind)patternBind.bind;
				// Note we always perform DTC checks here...
				Value converted = exval.convertValueTo(typebind.type, ctxt);
				evalContext = new Context(location, "tixe type", ctxt);
				evalContext.put(typebind.pattern.getNamedValues(converted, ctxt));
			}
		}
		catch (ValueException ve)	// Type bind convert failure
		{
			evalContext = null;
		}
		catch (PatternMatchException e)
		{
			evalContext = null;
		}

		return evalContext == null ? null : statement.eval(evalContext);
	}

	public ProofObligationList getProofObligations(POContextStack ctxt)
	{
		ProofObligationList list = new ProofObligationList();

		if (patternBind.pattern != null)
		{
			// Nothing to do
		}
		else if (patternBind.bind instanceof TypeBind)
		{
			// Nothing to do
		}
		else if (patternBind.bind instanceof SetBind)
		{
			SetBind bind = (SetBind)patternBind.bind;
			list.addAll(bind.set.getProofObligations(ctxt));
		}

		list.addAll(statement.getProofObligations(ctxt));
		return list;
	}
}
