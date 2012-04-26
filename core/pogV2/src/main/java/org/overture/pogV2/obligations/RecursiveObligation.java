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

package org.overture.pogV2.obligations;

import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AImplicitFunctionDefinition;
import org.overture.ast.expressions.AApplyExp;
import org.overture.ast.patterns.APatternListTypePair;
import org.overturetool.utilV2.Utils;

public class RecursiveObligation extends ProofObligation
{
	public RecursiveObligation(
		AExplicitFunctionDefinition def, AApplyExp apply, POContextStack ctxt)
	{
		super(def.getLocation(), POType.RECURSIVE, ctxt);
		StringBuilder sb = new StringBuilder();

		sb.append(def.getMeasure().getName());
		sb.append("(");
		sb.append(Utils.listToString(def.getParamPatternList().get(0)));
		sb.append(")");
		sb.append(def.getMeasureLexical() > 0 ? " LEX" + def.getMeasureLexical() + "> " : " > ");
		sb.append(def.getMeasure().getName());
		sb.append("(");
		sb.append(Utils.listToString(apply.getArgs()));
		sb.append(")");

		value = ctxt.getObligation(sb.toString());
	}

	public RecursiveObligation(
		AImplicitFunctionDefinition def, AApplyExp apply, POContextStack ctxt)
	{
		super(def.getLocation(), POType.RECURSIVE, ctxt);
		StringBuilder sb = new StringBuilder();

		sb.append(def.getMeasure());
		sb.append("(");

		for (APatternListTypePair pltp: def.getParamPatterns())
		{
			sb.append(pltp.getPatterns());
		}

		sb.append(")");
		sb.append(def.getMeasureLexical() > 0 ? " LEX" + def.getMeasureLexical() + "> " : " > ");
		sb.append(def.getMeasure());
		sb.append("(");
		sb.append(apply.getArgs());
		sb.append(")");

		value = ctxt.getObligation(sb.toString());
	}
}
