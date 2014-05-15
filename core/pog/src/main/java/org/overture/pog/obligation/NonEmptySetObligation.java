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

import org.overture.ast.expressions.ANotEqualBinaryExp;
import org.overture.ast.expressions.ASetEnumSetExp;
import org.overture.ast.expressions.PExp;
import org.overture.pog.pub.IPOContextStack;


public class NonEmptySetObligation extends ProofObligation
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6816002531259689986L;

	public NonEmptySetObligation(PExp exp, IPOContextStack ctxt)
	{
		super(exp, POType.NON_EMPTY_SET, ctxt, exp.getLocation());
		
		// exp <> {}
		
		ANotEqualBinaryExp notEqualsExp = new ANotEqualBinaryExp();
		notEqualsExp.setLeft(exp.clone());
		
		ASetEnumSetExp setExp = new ASetEnumSetExp();
		setExp.setMembers(new LinkedList<PExp>()); // empty list
		
		notEqualsExp.setRight(setExp);
	
		valuetree.setPredicate(ctxt.getPredWithContext(notEqualsExp));
//		valuetree.setContext(ctxt.getContextNodeList());
	}
}
