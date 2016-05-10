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
import java.util.Vector;
import java.util.ArrayList;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.AEqualsDefinition;
import org.overture.ast.definitions.AValueDefinition;
import org.overture.ast.expressions.AEqualsBinaryExp;
import org.overture.ast.expressions.AExistsExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.factory.AstExpressionFactory;
import org.overture.ast.patterns.ATypeMultipleBind;
import org.overture.ast.patterns.PMultipleBind;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.types.PType;
import org.overture.pog.pub.IPOContextStack;
import org.overture.pog.pub.IPogAssistantFactory;
import org.overture.pog.pub.POType;

public class ValueBindingObligation extends ProofObligation
{
	private static final long serialVersionUID = -7549866948129324892L;

	public ValueBindingObligation(AValueDefinition def, IPOContextStack ctxt,
			IPogAssistantFactory af) throws AnalysisException
	{
		this(def.getPattern(), def.getType(), def.getExpression(), ctxt, af);
	}

	public ValueBindingObligation(AEqualsDefinition def, IPOContextStack ctxt,
			IPogAssistantFactory af) throws AnalysisException
	{
		this(def.getPattern(), def.getType(), def.getTest(), ctxt, af);
	}

	public ValueBindingObligation(PPattern pattern, PType type, PExp exp,
			IPOContextStack ctxt, IPogAssistantFactory af)
			throws AnalysisException
	{
		super(pattern, POType.VALUE_BINDING, ctxt, pattern.getLocation(), af);
		AExistsExp existsExp = new AExistsExp();

		List<PPattern> patternList = new ArrayList<PPattern>();
		patternList.add(pattern.clone());
		ATypeMultipleBind typeBind = new ATypeMultipleBind();
		typeBind.setPlist(patternList);
		typeBind.setType(type.clone());
		List<PMultipleBind> bindList = new ArrayList<PMultipleBind>();
		bindList.add(typeBind);
		existsExp.setBindList(bindList);

		AEqualsBinaryExp equals = AstExpressionFactory.newAEqualsBinaryExp(patternToExp(pattern.clone()), exp.clone());
		existsExp.setPredicate(equals);

		stitch = existsExp;
		valuetree.setPredicate(ctxt.getPredWithContext(existsExp));
		// valuetree.setContext(ctxt.getContextNodeList());
	}
}
