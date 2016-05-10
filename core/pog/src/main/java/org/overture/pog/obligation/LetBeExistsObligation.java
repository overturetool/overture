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
import java.util.ArrayList;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.expressions.ABooleanConstExp;
import org.overture.ast.expressions.AExistsExp;
import org.overture.ast.expressions.ALetBeStExp;
import org.overture.ast.lex.LexBooleanToken;
import org.overture.ast.patterns.PMultipleBind;
import org.overture.ast.statements.ALetBeStStm;
import org.overture.pog.pub.IPOContextStack;
import org.overture.pog.pub.IPogAssistantFactory;
import org.overture.pog.pub.POType;

public class LetBeExistsObligation extends ProofObligation
{
	private static final long serialVersionUID = 4190499967249305830L;

	public LetBeExistsObligation(ALetBeStExp exp, IPOContextStack ctxt,
			IPogAssistantFactory af) throws AnalysisException
	{
		super(exp, POType.LET_BE_EXISTS, ctxt, exp.getBind().getLocation(), af);

		/**
		 * let <binds> be st <predicate> in <exp> produces exists <binds> & <predicate>
		 */

		AExistsExp exists = new AExistsExp();
		List<PMultipleBind> bindList = new ArrayList<PMultipleBind>();
		bindList.add(exp.getBind().clone());
		exists.setBindList(bindList);

		if (exp.getSuchThat() != null)
		{
			exists.setPredicate(exp.getSuchThat().clone());
		} else
		{
			// we just use true since we cannot have
			// exists by itself
			ABooleanConstExp replacementNothing_exp = new ABooleanConstExp();
			replacementNothing_exp.setValue(new LexBooleanToken(true, null));

			exists.setPredicate(replacementNothing_exp);
		}

		stitch = exists;
		valuetree.setPredicate(ctxt.getPredWithContext(exists));
	}

	public LetBeExistsObligation(ALetBeStStm stmt, IPOContextStack ctxt,
			IPogAssistantFactory af) throws AnalysisException
	{
		super(stmt, POType.LET_BE_EXISTS, ctxt, stmt.getBind().getLocation(), af);

		AExistsExp exists = new AExistsExp();
		List<PMultipleBind> bindList = new ArrayList<PMultipleBind>();
		bindList.add(stmt.getBind().clone());
		exists.setBindList(bindList);

		if (stmt.getSuchThat() != null)
		{
			exists.setPredicate(stmt.getSuchThat().clone());
		} else
		{
			// we just use true since we cannot have
			// exists by itself
			ABooleanConstExp replacementNothing_exp = new ABooleanConstExp();
			replacementNothing_exp.setValue(new LexBooleanToken(true, null));

			exists.setPredicate(replacementNothing_exp);
		}
		stitch = exists;
		valuetree.setPredicate(ctxt.getPredWithContext(exists));
	}
}
