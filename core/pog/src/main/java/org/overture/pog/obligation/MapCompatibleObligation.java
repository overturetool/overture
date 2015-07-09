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

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.expressions.AForAllExp;
import org.overture.ast.expressions.AImpliesBooleanBinaryExp;
import org.overture.ast.expressions.AMapDomainUnaryExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.factory.AstExpressionFactory;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.patterns.PMultipleBind;
import org.overture.pog.pub.IPOContextStack;
import org.overture.pog.pub.IPogAssistantFactory;
import org.overture.pog.pub.POType;

public class MapCompatibleObligation extends ProofObligation
{
	private static final long serialVersionUID = -7453383884893058267L;

	public MapCompatibleObligation(PExp left, PExp right, IPOContextStack ctxt,
			IPogAssistantFactory af) throws AnalysisException
	{
		super(left, POType.MAP_COMPATIBLE, ctxt, left.getLocation(), af);

		/**
		 * This obligation occurs during a map union, and ensures that if there are overlaps in the maps, then they map
		 * to the same thing. So m1 munion m2 produces: forall v1 in set dom m1, v2 in set dom m2 & (v1 = v2) => (m1(v1)
		 * = m2(v2))
		 */

		ILexNameToken ldom = getUnique("ldom");
		ILexNameToken rdom = getUnique("rdom");

		AMapDomainUnaryExp domLeft = new AMapDomainUnaryExp();
		domLeft.setExp(left.clone());
		AMapDomainUnaryExp domRight = new AMapDomainUnaryExp();
		domRight.setExp(right.clone());
		List<PMultipleBind> bindings = getMultipleSetBindList(domLeft, ldom);
		bindings.addAll(getMultipleSetBindList(domRight, rdom));

		AImpliesBooleanBinaryExp implies = AstExpressionFactory.newAImpliesBooleanBinaryExp(getEqualsExp(getVarExp(ldom), getVarExp(rdom)), getEqualsExp(getApplyExp(left, getVarExp(ldom)), getApplyExp(right, getVarExp(rdom))));
	
		AForAllExp forallExp = new AForAllExp();

		forallExp.setBindList(bindings);
		forallExp.setPredicate(implies);

		stitch = forallExp;
		valuetree.setPredicate(ctxt.getPredWithContext(forallExp));
	}
}
