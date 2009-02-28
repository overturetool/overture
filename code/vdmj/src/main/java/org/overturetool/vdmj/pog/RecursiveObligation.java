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

import org.overturetool.vdmj.definitions.ExplicitFunctionDefinition;
import org.overturetool.vdmj.definitions.ImplicitFunctionDefinition;
import org.overturetool.vdmj.expressions.ApplyExpression;
import org.overturetool.vdmj.types.PatternListTypePair;

public class RecursiveObligation extends ProofObligation
{
	public RecursiveObligation(
		ExplicitFunctionDefinition def, ApplyExpression apply, POContextStack ctxt)
	{
		super(def.location, POType.RECURSIVE, ctxt);
		StringBuilder sb = new StringBuilder();

		sb.append(def.measure);
		sb.append("(");
		sb.append(def.paramPatternList.get(0));
		sb.append(")");
		sb.append(def.measureLexical > 0 ? " LEX" + def.measureLexical + "> " : " > ");
		sb.append(def.measure);
		sb.append(apply.args);

		value = ctxt.getObligation(sb.toString());
	}

	public RecursiveObligation(
		ImplicitFunctionDefinition def, ApplyExpression apply, POContextStack ctxt)
	{
		super(def.location, POType.RECURSIVE, ctxt);
		StringBuilder sb = new StringBuilder();

		sb.append(def.measure);
		sb.append("(");

		for (PatternListTypePair pltp: def.parameterPatterns)
		{
			sb.append(pltp.patterns);
		}

		sb.append(")");
		sb.append(def.measureLexical > 0 ? " LEX" + def.measureLexical + "> " : " > ");
		sb.append(def.measure);
		sb.append(apply.args);

		value = ctxt.getObligation(sb.toString());
	}
}
