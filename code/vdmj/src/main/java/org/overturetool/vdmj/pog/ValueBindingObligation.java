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

package org.overturetool.vdmj.pog;

import org.overturetool.vdmj.definitions.EqualsDefinition;
import org.overturetool.vdmj.definitions.ValueDefinition;
import org.overturetool.vdmj.expressions.Expression;
import org.overturetool.vdmj.patterns.Pattern;
import org.overturetool.vdmj.types.Type;

public class ValueBindingObligation extends ProofObligation
{
	public ValueBindingObligation(ValueDefinition def, POContextStack ctxt)
	{
		super(def.location, POType.VALUE_BINDING, ctxt);
		StringBuilder sb = new StringBuilder();

		sb.append("exists ");
		sb.append(def.pattern);
		sb.append(":");
		sb.append(def.type);
		sb.append(" & ");
		sb.append(def.pattern);
		sb.append(" = ");
		sb.append(def.exp);

		value = ctxt.getObligation(sb.toString());
	}

	public ValueBindingObligation(EqualsDefinition def, POContextStack ctxt)
	{
		super(def.location, POType.VALUE_BINDING, ctxt);
		StringBuilder sb = new StringBuilder();

		sb.append("exists ");
		sb.append(def.pattern);
		sb.append(":");
		sb.append(def.expType);
		sb.append(" & ");
		sb.append(def.pattern);
		sb.append(" = ");
		sb.append(def.test);

		value = ctxt.getObligation(sb.toString());
	}

	public ValueBindingObligation(
		Pattern p, Type t, Expression e, POContextStack ctxt)
	{
		super(p.location, POType.VALUE_BINDING, ctxt);
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
