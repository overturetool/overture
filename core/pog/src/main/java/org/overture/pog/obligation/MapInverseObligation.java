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
import org.overture.ast.expressions.AIsExp;
import org.overture.ast.expressions.AMapInverseUnaryExp;
import org.overture.ast.types.AInMapMapType;
import org.overture.pog.pub.IPOContextStack;
import org.overture.pog.pub.IPogAssistantFactory;
import org.overture.pog.pub.POType;

public class MapInverseObligation extends ProofObligation
{

	private static final long serialVersionUID = -5763771885830801635L;

	/**
	 * is_(exp, inmap expFromType to expToType)
	 * 
	 * @param exp
	 * @param ctxt
	 * @param af
	 * @throws AnalysisException
	 */
	public MapInverseObligation(AMapInverseUnaryExp exp, IPOContextStack ctxt,
			IPogAssistantFactory af) throws AnalysisException
	{
		super(exp, POType.MAP_INVERSE, ctxt, exp.getLocation(), af);

		AIsExp is_Exp = new AIsExp();
		is_Exp.setTest(exp.getExp().clone());

		AInMapMapType inMap_type = new AInMapMapType();
		inMap_type.setFrom(exp.getMapType().getFrom().clone());
		inMap_type.setTo(exp.getMapType().getTo().clone());
		is_Exp.setBasicType(inMap_type);

		stitch = is_Exp;
		valuetree.setPredicate(ctxt.getPredWithContext(is_Exp));

	}

}
