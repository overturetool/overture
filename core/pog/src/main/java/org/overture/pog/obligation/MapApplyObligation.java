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
import org.overture.ast.expressions.AInSetBinaryExp;
import org.overture.ast.expressions.AMapDomainUnaryExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.factory.AstExpressionFactory;
import org.overture.ast.statements.PStateDesignator;
import org.overture.ast.types.AUnknownType;
import org.overture.pog.pub.IPOContextStack;
import org.overture.pog.pub.IPogAssistantFactory;
import org.overture.pog.pub.POType;
import org.overture.pog.visitors.StateDesignatorToExpVisitor;

public class MapApplyObligation extends ProofObligation
{
	private static final long serialVersionUID = -1385749421110721860L;

	public MapApplyObligation(PExp root, PExp arg, IPOContextStack ctxt,
			IPogAssistantFactory af) throws AnalysisException
	{
		super(root, POType.MAP_APPLY, ctxt, root.getLocation(), af);

		/* <arg> in set dom <root> */
		AMapDomainUnaryExp dom_exp = new AMapDomainUnaryExp();
		dom_exp.setExp(root.clone());
		// We don't care about type of the exp. This is just for the CGP to not crash.
		dom_exp.setType(new AUnknownType());
		
		AInSetBinaryExp inSetExp = AstExpressionFactory.newAInSetBinaryExp(arg.clone(), dom_exp);

		stitch = inSetExp;
		valuetree.setPredicate(ctxt.getPredWithContext(inSetExp));
	}

	public MapApplyObligation(PStateDesignator root, PExp arg,
			IPOContextStack ctxt, IPogAssistantFactory af)
			throws AnalysisException
	{
		super(root, POType.MAP_APPLY, ctxt, root.getLocation(), af);
		AMapDomainUnaryExp dom_exp = new AMapDomainUnaryExp();
		dom_exp.setExp(root.clone().apply(new StateDesignatorToExpVisitor()));
		// We don't care about type of the exp. This is just for the CGP to not crash.
		dom_exp.setType(new AUnknownType());

		AInSetBinaryExp inSetExp = AstExpressionFactory.newAInSetBinaryExp(arg.clone(), dom_exp);

		stitch = inSetExp;
		valuetree.setPredicate(ctxt.getPredWithContext(inSetExp));
	}
}
