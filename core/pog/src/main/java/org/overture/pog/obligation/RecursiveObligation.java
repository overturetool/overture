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

package org.overture.pog.obligation;

import java.util.List;

import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AImplicitFunctionDefinition;
import org.overture.ast.expressions.AApplyExp;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.patterns.APatternListTypePair;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.util.Utils;
import org.overture.typechecker.assistant.expression.AApplyExpAssistantTC;

public class RecursiveObligation extends ProofObligation
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6975984943449362262L;

	public RecursiveObligation(
		AExplicitFunctionDefinition def, AApplyExp apply, POContextStack ctxt)
	{
		super(apply.getLocation(), POType.RECURSIVE, ctxt);
		StringBuilder sb = new StringBuilder();

		sb.append(def.getMeasure().getName());
			
		if (def.getTypeParams() != null && !def.getTypeParams().isEmpty())
		{
			sb.append("[");
			
			for (ILexNameToken type: def.getTypeParams())
			{
				sb.append("@");
				sb.append(type);
			}
			
			sb.append("]");
		}
		
		String sep = "";
		sb.append("(");
		
		for (List<PPattern> plist: def.getParamPatternList())
		{
			 sb.append(sep);
			 sb.append(Utils.listToString(plist));
			 sep = ", ";
		}

		sb.append(")");
		sb.append(def.getMeasureLexical() > 0 ? " LEX" + def.getMeasureLexical() + "> " : " > ");
		sb.append(AApplyExpAssistantTC.getMeasureApply(apply, def.getMeasure()));

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
