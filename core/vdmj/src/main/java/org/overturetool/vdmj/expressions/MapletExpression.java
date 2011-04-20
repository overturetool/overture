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

package org.overturetool.vdmj.expressions;

import java.io.Serializable;

import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.lex.LexToken;
import org.overturetool.vdmj.pog.POContextStack;
import org.overturetool.vdmj.pog.ProofObligationList;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.types.MapType;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.values.ValueList;

public class MapletExpression implements Serializable
{
	private static final long serialVersionUID = 1L;

	public final LexLocation location;
	public final Expression left;
	public final Expression right;

	public MapletExpression(Expression left, LexToken op, Expression right)
	{
		this.location = op.location;
		this.left = left;
		this.right = right;
	}

	public Type typeCheck(Environment env, NameScope scope)
	{
		Type ltype = left.typeCheck(env, null, scope);
		Type rtype = right.typeCheck(env, null, scope);

		return new MapType(location, ltype, rtype);
	}

	@Override
	public String toString()
	{
		return left + " |-> " + right;
	}

	public Expression findExpression(int lineno)
	{
		Expression found = left.findExpression(lineno);
		return (found == null) ? right.findExpression(lineno) : found;
	}

	public ProofObligationList getProofObligations(POContextStack ctxt)
	{
		ProofObligationList list = left.getProofObligations(ctxt);
		list.addAll(right.getProofObligations(ctxt));
		return list;
	}

	public ValueList getValues(Context ctxt)
	{
		ValueList list = left.getValues(ctxt);
		list.addAll(right.getValues(ctxt));
		return list;
	}
}
