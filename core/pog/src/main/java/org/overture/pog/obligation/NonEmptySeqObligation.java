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

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.expressions.ANotEqualBinaryExp;
import org.overture.ast.expressions.ASeqEnumSeqExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.factory.AstExpressionFactory;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.types.AUnknownType;
import org.overture.pog.pub.IPOContextStack;
import org.overture.pog.pub.IPogAssistantFactory;
import org.overture.pog.pub.POType;

public class NonEmptySeqObligation extends ProofObligation
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8245417295117901422L;

	public NonEmptySeqObligation(PExp exp, IPOContextStack ctxt,
			IPogAssistantFactory af) throws AnalysisException
	{
		super(exp, POType.NON_EMPTY_SEQ, ctxt, exp.getLocation(), af);

		// exp <> []

		ASeqEnumSeqExp seqExp = new ASeqEnumSeqExp();
		seqExp.setMembers(new LinkedList<PExp>()); // empty list
		seqExp.setType(AstFactory.newASeqSeqType(null, new AUnknownType()));

		ANotEqualBinaryExp notEqualsExp = AstExpressionFactory.newANotEqualBinaryExp(exp.clone(), seqExp);

		stitch = notEqualsExp;
		valuetree.setPredicate(ctxt.getPredWithContext(notEqualsExp));
	}
}
