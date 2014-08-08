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
import org.overture.ast.expressions.AAndBooleanBinaryExp;
import org.overture.ast.expressions.AGreaterNumericBinaryExp;
import org.overture.ast.expressions.AInSetBinaryExp;
import org.overture.ast.expressions.AIndicesUnaryExp;
import org.overture.ast.expressions.AIntLiteralExp;
import org.overture.ast.expressions.ALenUnaryExp;
import org.overture.ast.expressions.ALessEqualNumericBinaryExp;
import org.overture.ast.expressions.APlusNumericBinaryExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.factory.AstExpressionFactory;
import org.overture.ast.lex.LexIntegerToken;
import org.overture.ast.statements.PStateDesignator;
import org.overture.pog.pub.IPOContextStack;
import org.overture.pog.pub.IPogAssistantFactory;
import org.overture.pog.pub.POType;
import org.overture.pog.visitors.StateDesignatorToExpVisitor;

public class SeqApplyObligation extends ProofObligation
{

	private static final long serialVersionUID = -4022111928534078511L;

	public SeqApplyObligation(PExp root, PExp arg, IPOContextStack ctxt,
			IPogAssistantFactory af) throws AnalysisException
	{
		super(root, POType.SEQ_APPLY, ctxt, root.getLocation(), af);

		AIndicesUnaryExp indsExp = new AIndicesUnaryExp();
		indsExp.setExp(root.clone());

		AInSetBinaryExp inSetExp = AstExpressionFactory.newAInSetBinaryExp(arg.clone(), indsExp);
		stitch = inSetExp;
		valuetree.setPredicate(ctxt.getPredWithContext(stitch));
	}

	public SeqApplyObligation(PStateDesignator root, PExp arg,
			IPOContextStack ctxt, IPogAssistantFactory af)
			throws AnalysisException
	{
		super(root, POType.SEQ_APPLY, ctxt, root.getLocation(), af);
		// arg >0
		AIntLiteralExp zeroExp = new AIntLiteralExp();
		zeroExp.setValue(new LexIntegerToken(0, null));
		AGreaterNumericBinaryExp grExp = AstExpressionFactory.newAGreaterNumericBinaryExp(arg.clone(), zeroExp);

		// len(root)
		ALenUnaryExp lenExp = new ALenUnaryExp();
		PExp stateExp = root.apply(new StateDesignatorToExpVisitor());
		lenExp.setExp(stateExp.clone());

		// len(root)+1
		AIntLiteralExp oneExp = new AIntLiteralExp();
		oneExp.setValue(new LexIntegerToken(1, null));
		APlusNumericBinaryExp plusExp = AstExpressionFactory.newAPlusNumericBinaryExp(lenExp, oneExp);

		// arg <= len(root) +1
		ALessEqualNumericBinaryExp lteExp = AstExpressionFactory.newALessEqualNumericBinaryExp(arg.clone(), plusExp);

		// arg > 0 and arg <= len(root)+1
		AAndBooleanBinaryExp andExp = AstExpressionFactory.newAAndBooleanBinaryExp(grExp, lteExp);

		stitch = andExp;
		valuetree.setPredicate(ctxt.getPredWithContext(stitch));
	}
}
