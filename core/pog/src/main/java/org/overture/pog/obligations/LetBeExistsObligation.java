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

package org.overture.pog.obligations;

import org.overture.ast.expressions.ALetBeStExp;
import org.overture.ast.statements.ALetBeStStm;


public class LetBeExistsObligation extends ProofObligation
{
	public LetBeExistsObligation(ALetBeStExp exp, POContextStack ctxt)
	{
		super(exp.getBind().getLocation(), POType.LET_BE_EXISTS, ctxt);
		StringBuilder sb = new StringBuilder();

		sb.append("exists ");
		sb.append(exp.getBind());

		if (exp.getSuchThat() != null)
		{
			sb.append(" & ");
			sb.append(exp.getSuchThat());
		}

		value = ctxt.getObligation(sb.toString());
	}

	public LetBeExistsObligation(ALetBeStStm stmt, POContextStack ctxt)
	{
		super(stmt.getBind().getLocation(), POType.LET_BE_EXISTS, ctxt);
		StringBuilder sb = new StringBuilder();

		sb.append("exists ");
		sb.append(stmt.getBind());

		if (stmt.getSuchThat() != null)
		{
			sb.append(" & ");
			sb.append(stmt.getSuchThat());
		}

		value = ctxt.getObligation(sb.toString());
	}
}
