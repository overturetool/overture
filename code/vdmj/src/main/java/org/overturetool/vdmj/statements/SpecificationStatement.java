/*******************************************************************************
 *
 *	Copyright (C) 2008 Fujitsu Services Ltd.
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

import org.overturetool.vdmj.definitions.DefinitionList;
import org.overturetool.vdmj.definitions.LocalDefinition;
import org.overturetool.vdmj.expressions.Expression;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.pog.POContextStack;
import org.overturetool.vdmj.pog.ProofObligationList;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.FlatEnvironment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.types.BooleanType;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.VoidType;
import org.overturetool.vdmj.values.Value;


public class SpecificationStatement extends Statement
{
	public final List<ExternalClause> externals;
	public final Expression precondition;
	public final Expression postcondition;
	public final List<ErrorCase> errors;

	public SpecificationStatement(LexLocation location,
		List<ExternalClause> externals,	Expression precondition,
		Expression postcondition, List<ErrorCase> errors)
	{
		super(location);

		this.externals = externals;
		this.precondition = precondition;
		this.postcondition = postcondition;
		this.errors = errors;
	}

	@Override
	public String toString()
	{
		return "[" +
    		(externals == null ? "" : "\n\text " + externals) +
    		(precondition == null ? "" : "\n\tpre " + precondition) +
    		(postcondition == null ? "" : "\n\tpost " + postcondition) +
    		(errors == null ? "" : "\n\terrs " + errors) + "]";
	}

	@Override
	public String kind()
	{
		return "specification";
	}

	@Override
	public Type typeCheck(Environment base, NameScope scope)
	{
		DefinitionList defs = new DefinitionList();

		// Now we build local definitions for each of the externals, so
		// that they can be added to the local environment, while the
		// global state is made inaccessible.

		if (externals != null)
		{
    		for (ExternalClause clause: externals)
    		{
    			for (LexNameToken name: clause.identifiers)
    			{
    				if (base.findName(name, NameScope.STATE) == null)
    				{
    					name.report(3274, "External variable is not in scope: " + name);
    				}
    				else
    				{
    					defs.add(new LocalDefinition(
    						name.location, name, NameScope.STATE, clause.type));
    				}
    			}
    		}
		}

		if (errors != null)
		{
			for (ErrorCase err: errors)
			{
				Type lt = err.left.typeCheck(base, null, NameScope.NAMESANDSTATE);
				Type rt = err.right.typeCheck(base, null, NameScope.NAMESANDSTATE);

				if (!lt.isType(BooleanType.class))
				{
					err.left.report(3275, "Error clause must be a boolean");
				}

				if (!rt.isType(BooleanType.class))
				{
					err.right.report(3275, "Error clause must be a boolean");
				}
			}
		}

		defs.typeCheck(base, scope);
		Environment local = new FlatEnvironment(defs, base);	// NB. No check

		if (precondition != null &&
			!precondition.typeCheck(local, null, NameScope.NAMESANDSTATE).isType(BooleanType.class))
		{
			precondition.report(3233, "Precondition is not a boolean expression");
		}

		if (postcondition != null &&
			!postcondition.typeCheck(local, null, NameScope.NAMESANDANYSTATE).isType(BooleanType.class))
		{
			postcondition.report(3234, "Postcondition is not a boolean expression");
		}

		return new VoidType(location);
	}

	@Override
	public Value eval(Context ctxt)
	{
		breakpoint.check(location, ctxt);
		return abort(4047, "Cannot execute specification statement", ctxt);
	}

	@Override
	public ProofObligationList getProofObligations(POContextStack ctxt)
	{
		ProofObligationList obligations = new ProofObligationList();

		if (errors != null)
		{
			for (ErrorCase err: errors)
			{
				obligations.addAll(err.left.getProofObligations(ctxt));
				obligations.addAll(err.right.getProofObligations(ctxt));
			}
		}

		if (precondition != null)
		{
			obligations.addAll(precondition.getProofObligations(ctxt));
		}

		if (postcondition != null)
		{
			obligations.addAll(postcondition.getProofObligations(ctxt));
		}

		return obligations;
	}
}
