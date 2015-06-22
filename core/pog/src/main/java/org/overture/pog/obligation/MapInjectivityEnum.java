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

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.expressions.AForAllExp;
import org.overture.ast.expressions.AImpliesBooleanBinaryExp;
import org.overture.ast.expressions.AMapDomainUnaryExp;
import org.overture.ast.expressions.AMapEnumMapExp;
import org.overture.ast.expressions.AMapletExp;
import org.overture.ast.expressions.ASetEnumSetExp;
import org.overture.ast.factory.AstExpressionFactory;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.patterns.PMultipleBind;
import org.overture.ast.types.ABooleanBasicType;
import org.overture.pog.pub.IPOContextStack;
import org.overture.pog.pub.IPogAssistantFactory;
import org.overture.pog.pub.POType;

public class MapInjectivityEnum extends ProofObligation
{
	private static final long serialVersionUID = 2042036674338877124L;

	public MapInjectivityEnum(AMapEnumMapExp exp, IPOContextStack ctxt,
			IPogAssistantFactory af) throws AnalysisException
	{
		super(exp, POType.MAP_INJ_ENUM, ctxt, exp.getLocation(), af);

		/**
		 * This obligation applies to a map enumeration. Given a map enum of the form { <maplet>, <maplet>, ... }, the
		 * obligation becomes: forall m1, m2 in set { {<maplet>}, {<maplet>}, ... } & -- set of maps forall d1 in set
		 * dom m1, d2 in set dom m2 & (d1 = d2) => (m1(d1) = m2(d2)) The obligation means that there are no
		 * contradictory maplets, where the same domain key maps to different range values.
		 */

		ILexNameToken m1 = getUnique("m");
		ILexNameToken m2 = getUnique("m");

		ASetEnumSetExp setOfMaplets = new ASetEnumSetExp();
		List<AMapEnumMapExp> singleMaplets = new Vector<AMapEnumMapExp>();

		for (AMapletExp maplet : exp.getMembers())
		{
			AMapEnumMapExp mapOfOne = new AMapEnumMapExp();
			List<AMapletExp> members = new Vector<AMapletExp>();
			members.add(maplet.clone());
			mapOfOne.setMembers(members);

			singleMaplets.add(mapOfOne);
		}

		setOfMaplets.setMembers(singleMaplets);
		List<PMultipleBind> m1m2binding = getMultipleSetBindList(setOfMaplets, m1, m2);

		AForAllExp domForallExp = new AForAllExp();
		ILexNameToken d1 = getUnique("d");
		ILexNameToken d2 = getUnique("d");

		AMapDomainUnaryExp domM1 = new AMapDomainUnaryExp();
		domM1.setExp(getVarExp(m1));
		AMapDomainUnaryExp domM2 = new AMapDomainUnaryExp();
		domM2.setExp(getVarExp(m2));

		AImpliesBooleanBinaryExp implies = AstExpressionFactory.newAImpliesBooleanBinaryExp(getEqualsExp(getVarExp(d1), getVarExp(d2)), getEqualsExp(getApplyExp(getVarExp(m1), getVarExp(d1)), getApplyExp(getVarExp(m2), getVarExp(d2))));
		implies.setType(new ABooleanBasicType());
		
		List<PMultipleBind> domBinding = getMultipleSetBindList(domM1, d1);
		domBinding.addAll(getMultipleSetBindList(domM2, d2));

		domForallExp.setBindList(domBinding);
		domForallExp.setPredicate(implies);

		AForAllExp forallExp = new AForAllExp();
		forallExp.setBindList(m1m2binding);
		forallExp.setPredicate(domForallExp);

		stitch = forallExp;
		valuetree.setPredicate(ctxt.getPredWithContext(forallExp));
	}
}
