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

import java.util.LinkedList;
import java.util.List;

import org.overture.ast.expressions.AForAllExp;
import org.overture.ast.expressions.AImpliesBooleanBinaryExp;
import org.overture.ast.expressions.AMapCompMapExp;
import org.overture.ast.expressions.AMapDomainUnaryExp;
import org.overture.ast.expressions.ASetCompSetExp;
import org.overture.ast.expressions.ASetEnumSetExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.factory.AstExpressionFactory;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.patterns.AIdentifierPattern;
import org.overture.ast.patterns.ASetMultipleBind;
import org.overture.ast.patterns.PMultipleBind;
import org.overture.ast.patterns.PPattern;
import org.overture.pog.pub.IPOContextStack;
import org.overture.pog.pub.POType;

public class MapSetOfCompatibleObligation extends ProofObligation
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6082219504509442557L;

	public MapSetOfCompatibleObligation(PExp exp, IPOContextStack ctxt)
	{
		super(exp, POType.MAP_SET_OF_COMPATIBLE, ctxt, exp.getLocation());

		PExp predicate = buildPredicate(exp.clone());

		// valuetree.setContext(ctxt.getContextNodeList());
		valuetree.setPredicate(ctxt.getPredWithContext(predicate));
	}

	public MapSetOfCompatibleObligation(AMapCompMapExp exp, IPOContextStack ctxt)
	{
		super(exp, POType.MAP_SET_OF_COMPATIBLE, ctxt, exp.getLocation());

		PExp predicate = buildPredicate(exp.clone());

		// valuetree.setContext(ctxt.getContextNodeList());
		valuetree.setPredicate(ctxt.getPredWithContext(predicate));
	}

	private PPattern makePattern(ILexNameToken name)
	{
		AIdentifierPattern pattern = new AIdentifierPattern();
		pattern.setName(name);
		return pattern;
	}

	// private PExp mapCompAsSet(AMapCompMapExp exp){
	//
	// ASetEnumSetExp setOfMaplets = new ASetEnumSetExp();
	// List<AMapEnumMapExp> singleMaplets = new Vector<AMapEnumMapExp>();
	//
	// for (AMapletExp maplet: exp.getMembers())
	// {
	// AMapEnumMapExp mapOfOne = new AMapEnumMapExp();
	// List<AMapletExp> members = new Vector<AMapletExp>();
	// members.add(maplet);
	// mapOfOne.setMembers(members);
	//
	// singleMaplets.add(mapOfOne);
	// }
	//
	// setOfMaplets.setMembers(singleMaplets);
	//
	//
	// return "{{" + exp.getFirst() + "} | " + Utils.listToString(exp.getBindings()) +
	// (exp.getPredicate() == null ? "}" : " & " + exp.getPredicate() + "}");
	// }

	private PExp buildPredicate(PExp mapExp)
	{

		/*
		 * forall m1, m2 in set exp & -- set of maps forall d1 in set dom m1, d2 in set dom m2 & (d1 = d2) => (m1(d1) =
		 * m2(d2))
		 */

		ILexNameToken m1 = getUnique("m");
		ILexNameToken m2 = getUnique("m");

		PPattern p1 = makePattern(m1);
		PPattern p2 = makePattern(m2);

		ASetMultipleBind setBind = new ASetMultipleBind();
		if (mapExp instanceof AMapCompMapExp)
		{
			AMapCompMapExp mapCompExp = (AMapCompMapExp) mapExp;
			ASetCompSetExp setExp = new ASetCompSetExp();
			ASetEnumSetExp setEnumExp = new ASetEnumSetExp();
			List<PExp> member = new LinkedList<PExp>();
			member.add(mapCompExp.getFirst().clone());
			setEnumExp.setMembers(member);
			setExp.setFirst(setEnumExp);
			setExp.setBindings(cloneListMultipleBind(mapCompExp.getBindings()));
			if (mapCompExp.getPredicate() != null)
			{
				setExp.setPredicate(mapCompExp.getPredicate().clone());
			}
			setBind.setSet(setExp);
		} else
		{
			setBind.setSet(mapExp.clone());
		}
		List<PPattern> patternList = new LinkedList<PPattern>();
		patternList.add(p1);
		patternList.add(p2);
		setBind.setPlist(patternList);

		AForAllExp domForallExp = new AForAllExp();
		ILexNameToken d1 = getUnique("d");
		ILexNameToken d2 = getUnique("d");

		AMapDomainUnaryExp domM1 = new AMapDomainUnaryExp();
		domM1.setExp(getVarExp(m1));
		AMapDomainUnaryExp domM2 = new AMapDomainUnaryExp();
		domM2.setExp(getVarExp(m2));

		AImpliesBooleanBinaryExp implies = AstExpressionFactory.newAImpliesBooleanBinaryExp(getEqualsExp(getVarExp(d1), getVarExp(d2)), getEqualsExp(getApplyExp(getVarExp(m1), getVarExp(d1)), getApplyExp(getVarExp(m2), getVarExp(d2))));

		List<PMultipleBind> domBinding = getMultipleSetBindList(domM1, d1);
		domBinding.addAll(getMultipleSetBindList(domM2, d2));
		domForallExp.setBindList(domBinding);
		domForallExp.setPredicate(implies);

		AForAllExp forallExp = new AForAllExp();
		List<PMultipleBind> setBindList = new LinkedList<PMultipleBind>();
		setBindList.add(setBind);
		forallExp.setBindList(setBindList);
		forallExp.setPredicate(domForallExp);

		return forallExp;

	}

}
