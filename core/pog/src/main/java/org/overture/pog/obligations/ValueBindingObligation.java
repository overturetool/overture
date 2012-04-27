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

package org.overture.pog.obligations;

import org.overture.ast.definitions.AEqualsDefinition;
import org.overture.ast.definitions.AValueDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.types.PType;


public class ValueBindingObligation extends ProofObligation
{
	public ValueBindingObligation(AValueDefinition def, POContextStack ctxt)
	{
		super(def.getLocation(), POType.VALUE_BINDING, ctxt);
		StringBuilder sb = new StringBuilder();

		sb.append("exists ");
		sb.append(def.getPattern());
		sb.append(":");
		sb.append(def.getType());
		sb.append(" & ");
		sb.append(def.getPattern());
		sb.append(" = ");
		sb.append(def.getExpression());

		value = ctxt.getObligation(sb.toString());
	}

	public ValueBindingObligation(AEqualsDefinition def, POContextStack ctxt)
	{
		super(def.getLocation(), POType.VALUE_BINDING, ctxt);
		StringBuilder sb = new StringBuilder();

		sb.append("exists ");
		sb.append(def.getPattern());
		sb.append(":");
		sb.append(def.getExpType());
		sb.append(" & ");
		sb.append(def.getPattern());
		sb.append(" = ");
		sb.append(def.getTest());

		value = ctxt.getObligation(sb.toString());
	}

	public ValueBindingObligation(
		PPattern p, PType t, PExp e, POContextStack ctxt)
	{
		super(p.getLocation(), POType.VALUE_BINDING, ctxt);
		StringBuilder sb = new StringBuilder();

		sb.append("exists ");
		sb.append(p);
		sb.append(":");
		sb.append(t);
		sb.append(" & ");
		sb.append(p);
		sb.append(" = ");
		sb.append(e);

		value = ctxt.getObligation(sb.toString());
	}
}
