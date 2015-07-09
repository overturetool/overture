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
import org.overture.ast.expressions.AApplyExp;
import org.overture.ast.expressions.AExistsExp;
import org.overture.ast.expressions.AForAllExp;
import org.overture.ast.expressions.AMapDomainUnaryExp;
import org.overture.ast.expressions.ASetCompSetExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.factory.AstExpressionFactory;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.patterns.PMultipleBind;
import org.overture.ast.types.ABooleanBasicType;
import org.overture.ast.types.AMapMapType;
import org.overture.ast.types.ANatNumericBasicType;
import org.overture.ast.types.ASetType;
import org.overture.pog.pub.IPOContextStack;
import org.overture.pog.pub.IPogAssistantFactory;
import org.overture.pog.pub.POType;

public class FiniteSetObligation extends ProofObligation
{

	private static final long serialVersionUID = 4471304924561635823L;

	/**
	 * { f(a) | a:A & p(a) } yields exists m:map nat to map A to :f & forall a:A & p(a) => exists idx in set dom m &
	 * m(idx) = f(a)
	 * 
	 * @param exp
	 * @param settype
	 * @param ctxt
	 * @param af
	 * @throws AnalysisException
	 */
	public FiniteSetObligation(ASetCompSetExp exp, ASetType settype,
			IPOContextStack ctxt, IPogAssistantFactory af)
			throws AnalysisException
	{
		super(exp, POType.FINITE_SET, ctxt, exp.getLocation(), af);

		ILexNameToken finmap = getUnique("finmap");
		ILexNameToken findex = getUnique("findex");

		AExistsExp existsExp = new AExistsExp();

		AMapMapType mapType = new AMapMapType();
		mapType.setFrom(new ANatNumericBasicType());
		mapType.setTo(settype.getSetof().clone());

		existsExp.setBindList(getMultipleTypeBindList(mapType, finmap));
		existsExp.setPredicate(getForallExp(exp.clone(), finmap, findex));

		stitch = existsExp.clone();
		valuetree.setPredicate(ctxt.getPredWithContext(existsExp));
	}

	/**
	 * forall a:A & p(a) => exists idx in set dom m & m(idx) = f(a)
	 */
	private PExp getForallExp(ASetCompSetExp exp, ILexNameToken finmap,
			ILexNameToken findex)
	{
		AForAllExp forallExp = new AForAllExp();
		forallExp.setBindList(exp.clone().getBindings());
		forallExp.setPredicate(getImpliesExpression(exp, finmap, findex));
		return forallExp;
	}

	/**
	 * p(a,b) => exists idx in set dom m & m(idx) = f(a)
	 */
	private PExp getImpliesExpression(ASetCompSetExp exp, ILexNameToken finmap,
			ILexNameToken findex)
	{
		if (exp.getPredicate() == null) // set comprehension has no predicate
		{
			return getImpliesExists(exp, finmap, findex);
		} else
		{
			return AstExpressionFactory.newAImpliesBooleanBinaryExp(exp.getPredicate().clone(), getImpliesExists(exp.clone(), finmap, findex));
		}
	}

	/**
	 * exists idx in set dom m & m(idx) =f(a)
	 */
	private PExp getImpliesExists(ASetCompSetExp exp, ILexNameToken finmap,
			ILexNameToken findex)
	{
		AExistsExp exists = new AExistsExp();

		AMapDomainUnaryExp domExp = new AMapDomainUnaryExp();
		domExp.setType(new ABooleanBasicType());
		domExp.setExp(getVarExp(finmap));
		List<PMultipleBind> bindList = getMultipleSetBindList(domExp, findex);
		exists.setBindList(bindList);

		exists.setPredicate(getExistsPredicate(exp, finmap, findex));
		return exists;
	}

	/**
	 * m(idx) = f(a)
	 */
	private PExp getExistsPredicate(ASetCompSetExp exp, ILexNameToken finmap,
			ILexNameToken findex)
	{
		AApplyExp apply = getApplyExp(getVarExp(finmap), getVarExp(findex));

		return getEqualsExp(apply, exp.getFirst());
	}

}
