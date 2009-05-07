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

import java.util.List;

import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.pog.POContextStack;
import org.overturetool.vdmj.pog.ProofObligationList;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.ExitException;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.TypeSet;
import org.overturetool.vdmj.values.Value;


public class TixeStatement extends Statement
{
	private static final long serialVersionUID = 1L;
	public final List<TixeStmtAlternative> traps;
	public final Statement body;

	public TixeStatement(LexLocation location,
		List<TixeStmtAlternative> traps, Statement body)
	{
		super(location);
		this.traps = traps;
		this.body = body;
	}

	@Override
	public String toString()
	{
		return "tixe {" + traps + "} in " + body;
	}

	@Override
	public String kind()
	{
		return "tixe";
	}

	@Override
	public Type typeCheck(Environment env, NameScope scope)
	{
		Type rt = body.typeCheck(env, scope);
		TypeSet extypes = body.exitCheck();

		if (!extypes.isEmpty())
		{
			Type union = extypes.getType(location);

    		for (TixeStmtAlternative tsa: traps)
    		{
    			tsa.typeCheck(env, scope, union);
    		}
		}

		return rt;
	}

	@Override
	public TypeSet exitCheck()
	{
		TypeSet types = new TypeSet();
		types.addAll(body.exitCheck());

		for (TixeStmtAlternative tsa: traps)
		{
			types.addAll(tsa.exitCheck());
		}

		return types;
	}

	@Override
	public Statement findStatement(int lineno)
	{
		Statement found = super.findStatement(lineno);
		if (found != null) return found;
		found = body.findStatement(lineno);
		if (found != null) return found;

		for (TixeStmtAlternative tsa: traps)
		{
			found = tsa.statement.findStatement(lineno);
			if (found != null) break;
		}

		return found;
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
		catch (ExitException original)
		{
			ExitException last = original;

			while (true)
			{
				Value exval = last.value;

				try
    			{
    				for (TixeStmtAlternative tsa: traps)
    				{
    					rv = tsa.eval(location, exval, ctxt);

    					if (rv != null)  // Statement was executed
    					{
    						return rv;
    					}
    				}
    			}
    			catch (ExitException ex)
    			{
    				last = ex;
    				continue;
    			}

				throw last;
			}
		}

		return rv;
	}

	@Override
	public ProofObligationList getProofObligations(POContextStack ctxt)
	{
		ProofObligationList obligations = new ProofObligationList();

		for (TixeStmtAlternative alt: traps)
		{
			obligations.addAll(alt.getProofObligations(ctxt));
		}

		obligations.addAll(body.getProofObligations(ctxt));
		return obligations;
	}
}
