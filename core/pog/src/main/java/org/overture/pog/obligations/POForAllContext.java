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

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.overture.ast.definitions.assistants.PMultipleBindAssistant;
import org.overture.ast.expressions.AExists1Exp;
import org.overture.ast.expressions.AExistsExp;
import org.overture.ast.expressions.AForAllExp;
import org.overture.ast.expressions.AIotaExp;
import org.overture.ast.expressions.ALambdaExp;
import org.overture.ast.expressions.ALetBeStExp;
import org.overture.ast.expressions.AMapCompMapExp;
import org.overture.ast.expressions.ASeqCompSeqExp;
import org.overture.ast.expressions.ASetCompSetExp;
import org.overture.ast.patterns.ATypeBind;
import org.overture.ast.patterns.ATypeMultipleBind;
import org.overture.ast.patterns.PMultipleBind;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.patterns.assistants.ASetBindAssistant;
import org.overture.ast.patterns.assistants.PBindAssistant;

public class POForAllContext extends POContext
{
	public final List<PMultipleBind> bindings;

	public POForAllContext(AMapCompMapExp exp)
	{
		this.bindings = exp.getBindings();
	}

	public POForAllContext(ASetCompSetExp exp)
	{
		this.bindings = exp.getBindings();
	}

	public POForAllContext(ASeqCompSeqExp exp)
	{
		this.bindings = ASetBindAssistant.getMultipleBindList(exp.getSetBind());
	}

	public POForAllContext(AForAllExp exp)
	{
		this.bindings = exp.getBindList();
	}

	public POForAllContext(AExistsExp exp)
	{
		this.bindings = exp.getBindList();
	}

	public POForAllContext(AExists1Exp exp)
	{
		this.bindings = PBindAssistant.getMultipleBindList(exp.getBind());
	}

	public POForAllContext(AIotaExp exp)
	{
		this.bindings = PBindAssistant.getMultipleBindList(exp.getBind());
	}

	public POForAllContext(ALambdaExp exp)
	{
		this.bindings = new Vector<PMultipleBind>();

		for (ATypeBind tb: exp.getBindList())
		{
			List<PPattern> pl = new ArrayList<PPattern>();
			pl.add(tb.getPattern());
			ATypeMultipleBind mtb = new ATypeMultipleBind(pl.get(0).getLocation(), pl, tb.getType());
			bindings.add(mtb);
		}
	}

	public POForAllContext(ALetBeStExp exp)
	{
		this.bindings = PMultipleBindAssistant.getMultipleBindList( exp.getBind());
	}

	@Override
	public boolean isScopeBoundary()
	{
		return true;
	}

	@Override
	public String getContext()
	{
		StringBuilder sb = new StringBuilder();

		sb.append("forall ");
		String prefix = "";

		for (PMultipleBind mb: bindings)
		{
			sb.append(prefix);
			sb.append(mb);
			prefix = ", ";
		}

		sb.append(" &");

		return sb.toString();
	}
}
