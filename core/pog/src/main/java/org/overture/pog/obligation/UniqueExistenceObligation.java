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
import org.overture.ast.expressions.AExists1Exp;
import org.overture.ast.expressions.AIotaExp;
import org.overture.pog.pub.IPOContextStack;
import org.overture.pog.pub.IPogAssistantFactory;
import org.overture.pog.pub.POType;

public class UniqueExistenceObligation extends ProofObligation
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6089416321651268903L;

	public UniqueExistenceObligation(AIotaExp iota, IPOContextStack ctxt,
			IPogAssistantFactory af) throws AnalysisException
	{
		super(iota, POType.UNIQUE_EXISTENCE, ctxt, iota.getLocation(), af);

		// exists1 iotaBinds & iotaPredicate

		AExists1Exp exists1Exp = new AExists1Exp();
		exists1Exp.setBind(iota.getBind().clone());
		exists1Exp.setPredicate(iota.getPredicate().clone());

		// valuetree.setContext(ctxt.getContextNodeList());
		stitch = exists1Exp;
		valuetree.setPredicate(ctxt.getPredWithContext(exists1Exp));
	}
}
