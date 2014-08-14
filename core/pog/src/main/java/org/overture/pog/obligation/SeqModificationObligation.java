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
import org.overture.ast.expressions.AIndicesUnaryExp;
import org.overture.ast.expressions.AMapDomainUnaryExp;
import org.overture.ast.expressions.APlusPlusBinaryExp;
import org.overture.ast.expressions.ASubsetBinaryExp;
import org.overture.ast.lex.LexKeywordToken;
import org.overture.ast.lex.VDMToken;
import org.overture.pog.pub.IPOContextStack;
import org.overture.pog.pub.IPogAssistantFactory;
import org.overture.pog.pub.POType;

public class SeqModificationObligation extends ProofObligation
{
	private static final long serialVersionUID = 2541416807923302230L;

	public SeqModificationObligation(APlusPlusBinaryExp arg,
			IPOContextStack ctxt, IPogAssistantFactory af)
			throws AnalysisException
	{
		super(arg, POType.SEQ_MODIFICATION, ctxt, arg.getLocation(), af);

		/**
		 * "seq ++ map" produces "dom map subset inds seq"
		 */
		ASubsetBinaryExp subsetExp = new ASubsetBinaryExp();
		subsetExp.setOp(new LexKeywordToken(VDMToken.SUBSET, null));
		AMapDomainUnaryExp domExp = new AMapDomainUnaryExp();
		domExp.setExp(arg.getRight().clone());
		AIndicesUnaryExp indsExp = new AIndicesUnaryExp();
		indsExp.setExp(arg.getLeft().clone());
		subsetExp.setLeft(domExp);
		subsetExp.setRight(indsExp);

		stitch = subsetExp;
		valuetree.setPredicate(ctxt.getPredWithContext(stitch));
	}
}
