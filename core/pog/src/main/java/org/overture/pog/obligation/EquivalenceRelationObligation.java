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

public class EquivalenceRelationObligation extends ProofObligation
{
	private static final long serialVersionUID = -3771203462569628826L;

	public EquivalenceRelationObligation(ATypeDefinition node,
			IPOContextStack question, IPogAssistantFactory af)
			throws AnalysisException
	{
		super(node, POType.EQUIV_REL, question, node.getLocation(), af);

		AVariableExp xExp = getVarExp(getUnique("x"));
		AVariableExp yExp = getVarExp(getUnique("y"));
		AVariableExp zExp = getVarExp(getUnique("z"));

		AForAllExp forallExp = makeRelContext(node,xExp,yExp,zExp);

		PExp andExp1 = makeAnd(makeReflexive(xExp,node),makeSymmetric(xExp,yExp,node));
		PExp andExp2 = makeAnd(andExp1, makeTransitive(xExp,yExp,zExp,node));
		forallExp.setPredicate(andExp2);
		valuetree.setPredicate(forallExp);
	}

	private PExp makeTransitive(PExp x, PExp y, AVariableExp z,
			ATypeDefinition node)
	{
		PExp xyExp = makeEqWithApply(x,y,node);
		PExp yzExp = makeEqWithApply(y,z,node);
		PExp xzExp = makeEqWithApply(x,z,node);
		AAndBooleanBinaryExp andExp = AstExpressionFactory.newAAndBooleanBinaryExp(xyExp,yzExp);
		AImpliesBooleanBinaryExp impExp = AstExpressionFactory.newAImpliesBooleanBinaryExp(andExp,xzExp);
		return impExp;
	}

	private PExp makeSymmetric(PExp x, PExp y,ATypeDefinition node)
	{
		PExp xyExp = makeEqWithApply(x,y,node);
		PExp yxExp = makeEqWithApply(y,x,node);
		PExp impliesExp = AstExpressionFactory.newAImpliesBooleanBinaryExp(xyExp,yxExp);
		return impliesExp;
	}

	private PExp makeReflexive(PExp xExp, ATypeDefinition node)
	{
		PExp applyExp = makeEqWithApply(xExp, xExp, node);
		return applyExp;
	}

	private PExp makeEqWithApply(PExp l, PExp r, ATypeDefinition node){
		List<PExp> args = new Vector<>();
		args.add(l.clone());
		args.add(r.clone());
		PExp root = AstFactory.newAVariableExp(node.getName().getEqName(node.getLocation()).clone());
		return AstFactory.newAApplyExp(root,args);
	}
}
