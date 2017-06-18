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

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.expressions.*;
import org.overture.ast.factory.AstExpressionFactory;
import org.overture.ast.lex.LexNameToken;
import org.overture.ast.patterns.ATypeMultipleBind;
import org.overture.ast.patterns.PMultipleBind;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.types.ABooleanBasicType;
import org.overture.pog.pub.IPOContextStack;
import org.overture.pog.pub.IPogAssistantFactory;
import org.overture.pog.pub.POType;

import java.util.LinkedList;
import java.util.List;

public class StrictOrderRelationObligation extends ProofObligation
{
	private static final long serialVersionUID = -3771203462569628826L;

	public StrictOrderRelationObligation(ATypeDefinition node,
			IPOContextStack question, IPogAssistantFactory af)
			throws AnalysisException
	{
		super(node, POType.STRICT_ORDER, question, node.getLocation(), af);
		AForAllExp forallExp = makeContext(node);
		PExp lExp = node.getOrdRelation().getLhsPattern().apply(af.getPatternToExpVisitor(getUniqueGenerator()));
		PExp rExp = node.getOrdRelation().getRhsPattern().apply(af.getPatternToExpVisitor(getUniqueGenerator()));
		PExp andExp = makeAnd(makeIrreflexive(lExp),makeTransitive(lExp,rExp));
		forallExp.setPredicate(andExp);
		valuetree.setPredicate(forallExp);
	}

	private AForAllExp makeContext(ATypeDefinition node){

		AForAllExp forall_exp = new AForAllExp();
		forall_exp.setType(new ABooleanBasicType());

		ATypeMultipleBind tmb = new ATypeMultipleBind();
		List<PPattern> pats = new LinkedList<>();
		pats.add(node.getOrdRelation().getLhsPattern().clone());
		pats.add(node.getOrdRelation().getRhsPattern().clone());
		tmb.setPlist(pats);
		tmb.setType(node.getType().clone());
		List<PMultipleBind> binds = new LinkedList<>();
		binds.add(tmb);
		forall_exp.setBindList(binds);
		return forall_exp;
	}

	private PExp makeIrreflexive(PExp var){
		ANotUnaryExp notExp = new ANotUnaryExp();
		notExp.setType(new ABooleanBasicType());
		ALessNumericBinaryExp lEq = AstExpressionFactory.newALessNumericBinaryExp(var.clone(),var.clone());
		notExp.setExp(lEq);
		return notExp;
	}

	private PExp makeTransitive(PExp l, PExp r){
		AVariableExp x = getVarExp(getUnique("x"));
		ALessNumericBinaryExp lessLeftRightExp = AstExpressionFactory.newALessNumericBinaryExp(l.clone(),r.clone());
		ALessNumericBinaryExp lessRightXExp = AstExpressionFactory.newALessNumericBinaryExp(r.clone(),x.clone());
		ALessNumericBinaryExp lessLeftXExp = AstExpressionFactory.newALessNumericBinaryExp(l.clone(),x.clone());
		AAndBooleanBinaryExp andExp = AstExpressionFactory.newAAndBooleanBinaryExp(lessLeftRightExp,lessRightXExp);
		AImpliesBooleanBinaryExp impExp = AstExpressionFactory.newAImpliesBooleanBinaryExp(andExp,lessLeftXExp);
		return impExp;
	}

}
