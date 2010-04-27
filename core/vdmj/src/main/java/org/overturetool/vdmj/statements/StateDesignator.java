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

import org.overturetool.vdmj.ast.IAstNode;
import org.overturetool.vdmj.definitions.Definition;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.pog.POContextStack;
import org.overturetool.vdmj.pog.ProofObligationList;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.ContextException;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.TypeChecker;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.values.Value;

/**
 * The root of the state designator hierarchy.
 */

public abstract class StateDesignator implements Serializable,IAstNode
{
	private static final long serialVersionUID = 1L;

	public final LexLocation location;

	public StateDesignator(LexLocation location)
	{
		this.location = location;
	}

	@Override
	abstract public String toString();

	abstract public Type typeCheck(Environment env);

	public Definition targetDefinition(
		@SuppressWarnings("unused") Environment env)
	{
		return null;
	}

	abstract public Value eval(Context ctxt);

	public ProofObligationList getProofObligations(
		@SuppressWarnings("unused") POContextStack ctxt)
	{
		return new ProofObligationList();
	}

	public void abort(int number, String msg, Context ctxt)
	{
		throw new ContextException(number, msg, location, ctxt);
	}

	public Value abort(ValueException ve)
	{
		throw new ContextException(ve, location);
	}

	public void report(int number, String msg)
	{
		TypeChecker.report(number, msg, location);
	}

	public void concern(boolean serious, int number, String msg)
	{
		if (serious)
		{
			TypeChecker.report(number, msg, location);
		}
		else
		{
			TypeChecker.warning(number, msg, location);
		}
	}

	public void detail(String tag, Object obj)
	{
		TypeChecker.detail(tag, obj);
	}

	public void detail2(String tag1, Object obj1, String tag2, Object obj2)
	{
		TypeChecker.detail2(tag1, obj1, tag2, obj2);
	}
	
	public String getName()
	{
		return location.module;
	}
	
	public LexLocation getLocation()
	{
		return location;
	}
}
