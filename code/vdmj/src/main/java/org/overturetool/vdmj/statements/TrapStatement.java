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
import org.overturetool.vdmj.runtime.ExitException;
import org.overturetool.vdmj.runtime.PatternMatchException;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.FlatCheckedEnvironment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.TypeSet;
import org.overturetool.vdmj.types.UnknownType;
import org.overturetool.vdmj.values.Value;
import org.overturetool.vdmj.values.ValueSet;

public class TrapStatement extends Statement
{
	private static final long serialVersionUID = 1L;
	public final PatternBind patternBind;
	public final Statement with;
	public final Statement body;

	public TrapStatement(LexLocation location,
		PatternBind patternBind, Statement with, Statement body)
	{
		super(location);
		this.patternBind = patternBind;
		this.with = with;
		this.body = body;
	}

	@Override
	public String toString()
	{
		return "trap " + patternBind + " with " + with + " in " + body;
	}

	@Override
	public String kind()
	{
		return "trap";
	}

	@Override
	public Type typeCheck(Environment base, NameScope scope)
	{
		TypeSet rtypes = new TypeSet();

		Type bt = body.typeCheck(base, scope);
		rtypes.add(bt);

		TypeSet extype = body.exitCheck();
		Type ptype = null;

		if (extype.isEmpty())
		{
			report(3241, "Body of trap statement does not throw exceptions");
			ptype = new UnknownType(body.location);
		}
		else
		{
			ptype = extype.getType(body.location);
		}

		patternBind.typeCheck(base, scope, ptype);
		DefinitionList defs = patternBind.getDefinitions();
		defs.typeCheck(base, scope);
		Environment local = new FlatCheckedEnvironment(defs, base);
		rtypes.add(with.typeCheck(local, scope));

		return rtypes.getType(location);
	}

	@Override
	public TypeSet exitCheck()
	{
		TypeSet types = new TypeSet();
		types.addAll(body.exitCheck());
		types.addAll(with.exitCheck());
		return types;
	}

	@Override
	public Statement findStatement(int lineno)
	{
		Statement found = super.findStatement(lineno);
		if (found != null) return found;
		found = body.findStatement(lineno);
		if (found != null) return found;
		return with.findStatement(lineno);
	}

	@Override
	public Value eval(Context ctxt)
	{
		breakpoint.check(location, ctxt);
		Value rv = null;

		try
		{
			rv = body.eval(ctxt);
		}
		catch (ExitException e)
		{
			Value exval = e.value;

			try
			{
    			if (patternBind.pattern != null)
    			{
    				Context evalContext = new Context(location, "trap pattern", ctxt);
    				evalContext.put(patternBind.pattern.getNamedValues(exval, ctxt));
    				rv = with.eval(evalContext);
    			}
    			else if (patternBind.bind instanceof SetBind)
    			{
    				SetBind setbind = (SetBind)patternBind.bind;
    				ValueSet set = setbind.set.eval(ctxt).setValue(ctxt);

    				if (set.contains(exval))
    				{
    					Context evalContext = new Context(location, "trap set", ctxt);
    					evalContext.put(setbind.pattern.getNamedValues(exval, ctxt));
    					rv = with.eval(evalContext);
    				}
    				else
    				{
    					abort(4050, "Value " + exval + " is not in set bind", ctxt);
    				}
    			}
    			else
    			{
    				TypeBind typebind = (TypeBind)patternBind.bind;
    				Value converted = exval.convertTo(typebind.type, ctxt);
    				Context evalContext = new Context(location, "trap type", ctxt);
    				evalContext.put(typebind.pattern.getNamedValues(converted, ctxt));
    				rv = with.eval(evalContext);
    			}
			}
			catch (ValueException ve)
			{
				abort(ve);
			}
			catch (PatternMatchException pe)
			{
				throw e;
			}
		}

		return rv;
	}

	@Override
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

		list.addAll(with.getProofObligations(ctxt));
		list.addAll(body.getProofObligations(ctxt));
		return list;
	}
}
