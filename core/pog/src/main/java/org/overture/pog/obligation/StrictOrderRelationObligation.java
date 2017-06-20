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
import org.overture.ast.factory.AstFactory;
import org.overture.ast.types.ABooleanBasicType;
import org.overture.pog.pub.IPOContextStack;
import org.overture.pog.pub.IPogAssistantFactory;
import org.overture.pog.pub.POType;

import java.util.List;
import java.util.Vector;

public class StrictOrderRelationObligation extends ProofObligation
{
	private static final long serialVersionUID = -3771203462569628826L;

	public StrictOrderRelationObligation(ATypeDefinition node,
			IPOContextStack question, IPogAssistantFactory af)
			throws AnalysisException
	{
		super(node, POType.STRICT_ORDER, question, node.getLocation(), af);

		AVariableExp xExp = getVarExp(getUnique("x"));
		AVariableExp yExp = getVarExp(getUnique("y"));
		AVariableExp zExp = getVarExp(getUnique("z"));

		AForAllExp forallExp = makeRelContext(node,xExp,yExp,zExp);
		PExp lExp = node.getOrdRelation().getLhsPattern().apply(af.getPatternToExpVisitor(getUniqueGenerator()));
		PExp rExp = node.getOrdRelation().getRhsPattern().apply(af.getPatternToExpVisitor(getUniqueGenerator()));
		PExp andExp = makeAnd(makeIrreflexive(xExp,node),makeTransitive(xExp,yExp,zExp, node));
		forallExp.setPredicate(andExp);
		valuetree.setPredicate(forallExp);
	}

	private PExp makeIrreflexive(PExp var, ATypeDefinition node){
		ANotUnaryExp notExp = new ANotUnaryExp();
		notExp.setType(new ABooleanBasicType());
		PExp applyExp = makeLessWithApply(var, var, node);
		notExp.setExp(applyExp);
		return notExp;
	}

	private PExp makeLessWithApply(PExp l, PExp r, ATypeDefinition node){
		List<PExp> args = new Vector<>();
		args.add(l.clone());
		args.add(r.clone());
		PExp root = AstFactory.newAVariableExp(node.getName().getOrdName(node.getLocation()).clone());
		return AstFactory.newAApplyExp(root,args);
	}

	private PExp makeTransitive(PExp x, PExp y, AVariableExp z,
			ATypeDefinition node){
		PExp lessXYExp = makeLessWithApply(x,y,node);
		PExp lessYZExp = makeLessWithApply(y,z,node);
		PExp lessXZExp = makeLessWithApply(x,z,node);
		AAndBooleanBinaryExp andExp = AstExpressionFactory.newAAndBooleanBinaryExp(lessXYExp,lessYZExp);
		AImpliesBooleanBinaryExp impExp = AstExpressionFactory.newAImpliesBooleanBinaryExp(andExp,lessXZExp);
		return impExp;
	}

}
