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
import org.overture.ast.expressions.AIntLiteralExp;
import org.overture.ast.expressions.AMapDomainUnaryExp;
import org.overture.ast.expressions.AMapRangeUnaryExp;
import org.overture.ast.expressions.AOrBooleanBinaryExp;
import org.overture.ast.expressions.AStarStarBinaryExp;
import org.overture.ast.expressions.ASubsetBinaryExp;
import org.overture.pog.pub.IPOContextStack;
import org.overture.pog.pub.IPogAssistantFactory;
import org.overture.pog.pub.POType;

public class MapIterationObligation extends ProofObligation
{
	private static final long serialVersionUID = -9122478081832322687L;

	public MapIterationObligation(AStarStarBinaryExp exp, IPOContextStack ctxt,
			IPogAssistantFactory af) throws AnalysisException
	{
		super(exp, POType.MAP_ITERATION, ctxt, exp.getLocation(), af);

		/**
		 * The obligation for m ** e is: e = 0 or e = 1 or rng m subset dom m
		 */

		AOrBooleanBinaryExp orExp = new AOrBooleanBinaryExp();
		AIntLiteralExp zero = getIntLiteral(0);
		AIntLiteralExp one = getIntLiteral(1);

		orExp.setLeft(getEqualsExp(exp.clone(), zero));
		AOrBooleanBinaryExp orExp2 = new AOrBooleanBinaryExp();
		orExp2.setLeft(getEqualsExp(exp.clone(), one));

		AMapRangeUnaryExp rng = new AMapRangeUnaryExp();
		rng.setExp(exp.getLeft().clone());
		AMapDomainUnaryExp dom = new AMapDomainUnaryExp();
		dom.setExp(exp.getLeft().clone());
		ASubsetBinaryExp subset = new ASubsetBinaryExp();
		subset.setLeft(rng);
		subset.setRight(dom);

		orExp2.setRight(subset);
		orExp.setRight(orExp2);

		stitch = orExp;
		valuetree.setPredicate(ctxt.getPredWithContext(orExp));
	}
}
