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

import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AImplicitFunctionDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.ast.patterns.AIdentifierPattern;
import org.overture.ast.patterns.APatternTypePair;
import org.overture.ast.types.AFunctionType;
import org.overturetool.vdmj.lex.LexNameToken;

public class POFunctionResultContext extends POContext {
	public final LexNameToken name;
	public final AFunctionType deftype;
	public final PExp precondition;
	public final PExp body;
	public final APatternTypePair result;
	public final boolean implicit;

	public POFunctionResultContext(AExplicitFunctionDefinition definition) {
		this.name = definition.getName();
		this.deftype = definition.getType();
		this.precondition = definition.getPrecondition();
		this.body = definition.getBody();
		this.implicit = false;
		this.result = new APatternTypePair(false, new AIdentifierPattern(
				definition.getLocation(), null, false, new LexNameToken(
						definition.getName().module, "RESULT",
						definition.getLocation())), definition.getType()
				.getResult());

	}

	public POFunctionResultContext(AImplicitFunctionDefinition definition) {
		this.name = definition.getName();
		this.deftype = definition.getType();
		this.precondition = definition.getPrecondition();
		this.body = definition.getBody();
		this.implicit = true;
		this.result = definition.getResult();
	}

	@Override
	public String getContext() {
		StringBuilder sb = new StringBuilder();

		if (precondition != null) {
			sb.append(precondition);
			sb.append(" => ");
		}

		if (implicit) {
			sb.append("forall ");
			sb.append(result);
			sb.append(" & ");
		} else {
			sb.append("let ");
			sb.append(result);
			sb.append(" = ");
			sb.append(body);
			sb.append(" in ");
		}

		return sb.toString();
	}
}
