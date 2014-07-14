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
import org.overture.ast.expressions.ACompBinaryExp;
import org.overture.ast.expressions.AMapDomainUnaryExp;
import org.overture.ast.expressions.AMapRangeUnaryExp;
import org.overture.ast.expressions.ASubsetBinaryExp;
import org.overture.pog.pub.IPOContextStack;
import org.overture.pog.pub.IPogAssistantFactory;
import org.overture.pog.pub.POType;

public class MapComposeObligation extends ProofObligation
{
	private static final long serialVersionUID = -3501039332724576068L;

	public MapComposeObligation(ACompBinaryExp exp, IPOContextStack ctxt,
			IPogAssistantFactory af) throws AnalysisException
	{
		super(exp, POType.MAP_COMPOSE, ctxt, exp.getLocation(), af);

		/**
		 * The obligation for m1 comp m2 is: rng m2 subset dom m1
		 */
		AMapRangeUnaryExp rng = new AMapRangeUnaryExp();
		rng.setExp(exp.getLeft().clone());
		AMapDomainUnaryExp dom = new AMapDomainUnaryExp();
		dom.setExp(exp.getRight().clone());

		ASubsetBinaryExp subset = new ASubsetBinaryExp();
		subset.setLeft(rng);
		subset.setRight(dom);

		stitch = subset;
		valuetree.setPredicate(ctxt.getPredWithContext(subset));
	}
}
