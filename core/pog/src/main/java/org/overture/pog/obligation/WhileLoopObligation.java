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
import org.overture.ast.expressions.AVariableExp;
import org.overture.ast.lex.LexNameToken;
import org.overture.ast.statements.ASkipStm;
import org.overture.ast.statements.AWhileStm;
import org.overture.pog.pub.IPOContextStack;
import org.overture.pog.pub.IPogAssistantFactory;
import org.overture.pog.pub.POType;

public class WhileLoopObligation extends ProofObligation
{
	private static final long serialVersionUID = -3771203462569628826L;

	public WhileLoopObligation(AWhileStm stmt, IPOContextStack ctxt,
			IPogAssistantFactory af) throws AnalysisException
	{
		super(stmt, POType.WHILE_LOOP, ctxt, stmt.getLocation(), af);
		AWhileStm whileStmt = new AWhileStm();
		whileStmt.setExp(stmt.getExp().clone());
		whileStmt.setStatement(new ASkipStm());

		AVariableExp nyexp = getVarExp(new LexNameToken("", "...", null));
		valuetree.setPredicate(nyexp);

		// valuetree.setPredicate(whileStmt);
		// valuetree.setContext(ctxt.getContextNodeList());
	}
}
