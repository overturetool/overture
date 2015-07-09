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

package org.overture.pog.contexts;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.overture.ast.expressions.AExists1Exp;
import org.overture.ast.expressions.AExistsExp;
import org.overture.ast.expressions.AForAllExp;
import org.overture.ast.expressions.AIotaExp;
import org.overture.ast.expressions.ALambdaExp;
import org.overture.ast.expressions.ALetBeStExp;
import org.overture.ast.expressions.AMapCompMapExp;
import org.overture.ast.expressions.ASeqCompSeqExp;
import org.overture.ast.expressions.ASetCompSetExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.patterns.ASetBind;
import org.overture.ast.patterns.ATypeBind;
import org.overture.ast.patterns.ATypeMultipleBind;
import org.overture.ast.patterns.PMultipleBind;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.types.ABooleanBasicType;
import org.overture.pog.pub.IPogAssistantFactory;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

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

	public POForAllContext(ASeqCompSeqExp exp,
			IPogAssistantFactory assistantFactory)
	{
		this.bindings = getMultipleBindList(exp.getSetBind());
	}

	public POForAllContext(AForAllExp exp)
	{
		this.bindings = exp.getBindList();
	}

	public POForAllContext(AExistsExp exp)
	{
		this.bindings = exp.getBindList();
	}

	public POForAllContext(ITypeCheckerAssistantFactory af, AExists1Exp exp)
	{
		this.bindings = af.createPBindAssistant().getMultipleBindList(exp.getBind());
	}

	public POForAllContext(ITypeCheckerAssistantFactory af, AIotaExp exp)
	{
		this.bindings = af.createPBindAssistant().getMultipleBindList(exp.getBind());
	}

	public POForAllContext(ALambdaExp exp)
	{
		this.bindings = new Vector<PMultipleBind>();

		for (ATypeBind tb : exp.getBindList())
		{
			List<PPattern> pl = new ArrayList<PPattern>();
			pl.add(tb.getPattern().clone());
			ATypeMultipleBind mtb = AstFactory.newATypeMultipleBind(pl, tb.getType().clone());
			bindings.add(mtb);
		}
	}

	public POForAllContext(ALetBeStExp exp,
			IPogAssistantFactory assistantFactory)
	{
		this.bindings = cloneBinds(assistantFactory.createPMultipleBindAssistant().getMultipleBindList(exp.getBind()));
	}

	private List<PMultipleBind> cloneBinds(List<PMultipleBind> multipleBindList)
	{
		List<PMultipleBind> r = new LinkedList<PMultipleBind>();
		for (PMultipleBind pmb : multipleBindList)
		{
			r.add(pmb.clone());
		}
		return r;
	}

	@Override
	public PExp getContextNode(PExp stitch)
	{
		return getSuperContext(stitch);
	}

	protected AForAllExp getSuperContext(PExp stitch)
	{
		AForAllExp forAllExp = new AForAllExp();
		forAllExp.setType(new ABooleanBasicType());
		forAllExp.setBindList(cloneBinds(bindings));
		forAllExp.setPredicate(stitch);
		return forAllExp;
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

		for (PMultipleBind mb : bindings)
		{
			sb.append(prefix);
			sb.append(mb);
			prefix = ", ";
		}

		sb.append(" &");

		return sb.toString();
	}
	
	public List<PMultipleBind> getMultipleBindList(ASetBind bind)
	{

		List<PPattern> plist = new ArrayList<PPattern>();
		plist.add(bind.getPattern());
		List<PMultipleBind> mblist = new Vector<PMultipleBind>();
		mblist.add(AstFactory.newASetMultipleBind(plist, bind.getSet()));
		return mblist;
	}
}
