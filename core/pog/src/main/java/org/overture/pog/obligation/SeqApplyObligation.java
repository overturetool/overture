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
import org.overture.ast.lex.LexIntegerToken;
import org.overture.ast.statements.PStateDesignator;
import org.overture.pog.pub.IPOContextStack;
import org.overture.pog.pub.POType;
import org.overture.pog.utility.StateDesignatorToExpVisitor;


public class SeqApplyObligation extends ProofObligation
{
	
	private static final long serialVersionUID = -4022111928534078511L;

	public SeqApplyObligation(PExp root, PExp arg, IPOContextStack ctxt)
	{
		super(root, POType.SEQ_APPLY, ctxt);
		AInSetBinaryExp inSetExp = new AInSetBinaryExp();
		inSetExp.setLeft(arg);
		AIndicesUnaryExp indsExp = new AIndicesUnaryExp();
		indsExp.setExp(root);
		inSetExp.setRight(indsExp);
		valuetree.setPredicate(ctxt.getPredWithContext(inSetExp));
	}
	


	public SeqApplyObligation(PStateDesignator root,
		PExp arg, IPOContextStack ctxt) throws AnalysisException
	{
		super(root, POType.SEQ_APPLY, ctxt);
		//arg >0
		AGreaterNumericBinaryExp grExp = new AGreaterNumericBinaryExp();
		grExp.setLeft(arg);
		AIntLiteralExp zeroExp = new AIntLiteralExp();
		zeroExp.setValue(new LexIntegerToken(0, null));
		grExp.setRight(zeroExp);
		
		
		// len(root)
		ALenUnaryExp lenExp = new ALenUnaryExp();
		PExp stateExp = root.apply(new StateDesignatorToExpVisitor());
		lenExp.setExp(stateExp);
		
		// len(root)+1
		APlusNumericBinaryExp plusExp = new APlusNumericBinaryExp();
		plusExp.setLeft(lenExp);
		AIntLiteralExp oneExp = new AIntLiteralExp();
		oneExp.setValue(new LexIntegerToken(1, null));
		plusExp.setRight(oneExp);
		
		//arg <= len(root) +1
		ALessEqualNumericBinaryExp lteExp = new ALessEqualNumericBinaryExp();
		lteExp.setLeft(arg);
		lteExp.setRight(plusExp);
		
		//arg > 0 and arg <= len(root)+1
		AAndBooleanBinaryExp andExp = new AAndBooleanBinaryExp();
		andExp.setLeft(grExp);
		andExp.setRight(lteExp);


//		valuetree.setContext(ctxt.getContextNodeList());
		valuetree.setPredicate(ctxt.getPredWithContext(lteExp));
	}
}
