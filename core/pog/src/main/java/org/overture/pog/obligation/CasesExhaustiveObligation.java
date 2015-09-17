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

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.expressions.ACaseAlternative;
import org.overture.ast.expressions.ACasesExp;
import org.overture.ast.expressions.AEqualsBinaryExp;
import org.overture.ast.expressions.AExistsExp;
import org.overture.ast.expressions.AOrBooleanBinaryExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.factory.AstExpressionFactory;
import org.overture.ast.patterns.ATypeMultipleBind;
import org.overture.ast.patterns.PMultipleBind;
import org.overture.ast.patterns.PPattern;
import org.overture.pog.pub.IPOContextStack;
import org.overture.pog.pub.IPogAssistantFactory;
import org.overture.pog.pub.POType;

public class CasesExhaustiveObligation extends ProofObligation
{
	/**
	 * VDM bit: cases x: a -> .... default -> ... yields PO: x = a or (exists default : X & x = default)
	 */
	private static final long serialVersionUID = -2266396606434510800L;

	// gkanos: Added parameter for the use of assistant.
	public CasesExhaustiveObligation(ACasesExp exp, IPOContextStack ctxt,
			IPogAssistantFactory assistantFactory) throws AnalysisException
	{
		super(exp, POType.CASES_EXHAUSTIVE, ctxt, exp.getLocation(), assistantFactory);

		PExp initialExp = alt2Exp(exp.getCases().getFirst(), exp, assistantFactory);
		List<ACaseAlternative> initialCases = new LinkedList<ACaseAlternative>(exp.getCases());
		initialCases.remove(0);

		PExp pred = recOnExp(exp.clone(), initialCases, initialExp, assistantFactory);

		stitch = pred.clone();
		valuetree.setPredicate(ctxt.getPredWithContext(pred));
	}

	private PExp recOnExp(ACasesExp exp, List<ACaseAlternative> cases, PExp r,
			IPogAssistantFactory assistantFactory) throws AnalysisException
	{
		if (cases.isEmpty())
		{
			return r;
		}

		AOrBooleanBinaryExp orExp = AstExpressionFactory.newAOrBooleanBinaryExp(r, alt2Exp(cases.get(0), exp.clone(), assistantFactory));

		List<ACaseAlternative> newCases = new LinkedList<ACaseAlternative>(cases);
		newCases.remove(0);

		return recOnExp(exp, newCases, orExp, assistantFactory);
	}

	private PExp alt2Exp(ACaseAlternative alt, ACasesExp exp,
			IPogAssistantFactory assistantFactory) throws AnalysisException
	{
		if (assistantFactory.createPPatternAssistant().isSimple(alt.getPattern()))
		{
			AEqualsBinaryExp equalsExp = AstExpressionFactory.newAEqualsBinaryExp(exp.getExpression().clone(), patternToExp(alt.getPattern().clone()));
			return equalsExp;
		} else
		{
			PExp matching =  patternToExp(alt.getPattern().clone());

			AExistsExp existsExp = new AExistsExp();

			ATypeMultipleBind tbind = new ATypeMultipleBind();
			List<PPattern> plist = new LinkedList<PPattern>();
			plist.add(alt.getPattern().clone());
			tbind.setPlist(plist);
			tbind.setType(exp.getExpression().getType().clone());
			List<PMultipleBind> bindList = new LinkedList<PMultipleBind>();
			bindList.add(tbind);
			existsExp.setBindList(bindList);

			AEqualsBinaryExp equalsExp = AstExpressionFactory.newAEqualsBinaryExp(exp.getExpression().clone(), matching);
			existsExp.setPredicate(equalsExp);

			return existsExp;
		}
	}

};
