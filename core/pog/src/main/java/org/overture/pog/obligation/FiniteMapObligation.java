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

import org.overture.ast.expressions.AApplyExp;
import org.overture.ast.expressions.AExistsExp;
import org.overture.ast.expressions.AForAllExp;
import org.overture.ast.expressions.AImpliesBooleanBinaryExp;
import org.overture.ast.expressions.AMapCompMapExp;
import org.overture.ast.expressions.AMapDomainUnaryExp;
import org.overture.ast.expressions.AMapEnumMapExp;
import org.overture.ast.expressions.AMapletExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.lex.LexKeywordToken;
import org.overture.ast.lex.VDMToken;
import org.overture.ast.patterns.PMultipleBind;
import org.overture.ast.types.AMapMapType;
import org.overture.ast.types.ANatNumericBasicType;
import org.overture.ast.types.PType;
import org.overture.pog.pub.IPOContextStack;


public class FiniteMapObligation extends ProofObligation
{
	private static final long serialVersionUID = -2891663568497319141L;

	public FiniteMapObligation(AMapCompMapExp exp, PType mapType, IPOContextStack ctxt)
	{
		super(exp, POType.FINITE_MAP, ctxt, exp.getLocation());

		ILexNameToken finmap = getUnique("finmap");
		ILexNameToken findex = getUnique("findex");
		
		//	eg. { a |-> b | a:A, b:B & p(a,b) }, gives...
		//
		//	exists m:map nat to map A to B &
		//		forall a:A, b:B &
		//			p(a,b) => exists idx in set dom m &
		//				m(idx) = { a |-> b }
		
		AExistsExp existsExp = new AExistsExp();
		AMapMapType natmaptype = new AMapMapType();
		natmaptype.setFrom(new ANatNumericBasicType());
		natmaptype.setTo(mapType.clone());
		
		existsExp.setBindList(getMultipleTypeBindList(natmaptype, finmap));
		existsExp.setPredicate(getForallExp(exp.clone(), finmap, findex));
		
		valuetree.setPredicate(ctxt.getPredWithContext(existsExp));
	}

	/**
	 *	forall a:A, b:B &
	 *		p(a,b) => exists idx in set dom m &
	 *			m(idx) = { a |-> b }
	 * 
	 */
	private PExp getForallExp(AMapCompMapExp exp, ILexNameToken finmap, ILexNameToken findex)
	{
		AForAllExp forallExp = new AForAllExp();
		forallExp.setBindList(exp.clone().getBindings());
		forallExp.setPredicate(getImpliesExpression(exp, finmap, findex));
		return forallExp;
	}

	/**
	 *	p(a,b) => exists idx in set dom m &
	 *		m(idx) = { a |-> b }
	 */
	private PExp getImpliesExpression(AMapCompMapExp exp, ILexNameToken finmap, ILexNameToken findex)
	{
		if (exp.getPredicate() == null)		// Map comprehension has no predicate
		{
			return getImpliesExists(exp, finmap, findex);
		}
		else
		{
			AImpliesBooleanBinaryExp implies = new AImpliesBooleanBinaryExp();
			implies.setLeft(exp.getPredicate());
			implies.setOp(new LexKeywordToken(VDMToken.IMPLIES, exp.getLocation()));
			implies.setRight(getImpliesExists(exp, finmap, findex));
			return implies;
		}
	}
	
	/**
	 *	exists idx in set dom m &
	 *		m(idx) = { a |-> b }
	 */
	private PExp getImpliesExists(AMapCompMapExp exp, ILexNameToken finmap, ILexNameToken findex)
	{
		AExistsExp exists = new AExistsExp();
		exists.setBindList(getSetBindList(finmap, findex));
		exists.setPredicate(getExistsPredicate(exp, finmap, findex));
		return exists;
	}

	/**
	 * idx in set dom m
	 */
	private List<PMultipleBind> getSetBindList(ILexNameToken finmap, ILexNameToken findex)
	{
		AMapDomainUnaryExp domExp = new AMapDomainUnaryExp();
		domExp.setExp(getVarExp(finmap));
		return getMultipleSetBindList(domExp, findex);
	}

	/**
	 *	m(idx) = { a |-> b }
	 */
	private PExp getExistsPredicate(AMapCompMapExp exp, ILexNameToken finmap, ILexNameToken findex)
	{
		AApplyExp apply = getApplyExp(getVarExp(finmap), getVarExp(findex));
		
		AMapEnumMapExp setEnum = new AMapEnumMapExp();
		List<AMapletExp> members = new Vector<AMapletExp>();
		members.add(exp.getFirst().clone());
		setEnum.setMembers(members);
		
		return getEqualsExp(apply, setEnum);
	}
}
