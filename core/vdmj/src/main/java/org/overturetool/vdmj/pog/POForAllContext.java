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

package org.overturetool.vdmj.pog;

import java.util.List;
import java.util.Vector;

import org.overturetool.vdmj.expressions.Exists1Expression;
import org.overturetool.vdmj.expressions.ExistsExpression;
import org.overturetool.vdmj.expressions.ForAllExpression;
import org.overturetool.vdmj.expressions.IotaExpression;
import org.overturetool.vdmj.expressions.LambdaExpression;
import org.overturetool.vdmj.expressions.LetBeStExpression;
import org.overturetool.vdmj.expressions.MapCompExpression;
import org.overturetool.vdmj.expressions.SeqCompExpression;
import org.overturetool.vdmj.expressions.SetCompExpression;
import org.overturetool.vdmj.patterns.MultipleBind;
import org.overturetool.vdmj.patterns.MultipleTypeBind;
import org.overturetool.vdmj.patterns.PatternList;
import org.overturetool.vdmj.patterns.TypeBind;

public class POForAllContext extends POContext
{
	public final List<MultipleBind> bindings;

	public POForAllContext(MapCompExpression exp)
	{
		this.bindings = exp.bindings;
	}

	public POForAllContext(SetCompExpression exp)
	{
		this.bindings = exp.bindings;
	}

	public POForAllContext(SeqCompExpression exp)
	{
		this.bindings = exp.setbind.getMultipleBindList();
	}

	public POForAllContext(ForAllExpression exp)
	{
		this.bindings = exp.bindList;
	}

	public POForAllContext(ExistsExpression exp)
	{
		this.bindings = exp.bindList;
	}

	public POForAllContext(Exists1Expression exp)
	{
		this.bindings = exp.bind.getMultipleBindList();
	}

	public POForAllContext(IotaExpression exp)
	{
		this.bindings = exp.bind.getMultipleBindList();
	}

	public POForAllContext(LambdaExpression exp)
	{
		this.bindings = new Vector<MultipleBind>();

		for (TypeBind tb: exp.bindList)
		{
			PatternList pl = new PatternList();
			pl.add(tb.pattern);
			MultipleTypeBind mtb = new MultipleTypeBind(pl, tb.type);
			bindings.add(mtb);
		}
	}

	public POForAllContext(LetBeStExpression exp)
	{
		this.bindings = exp.bind.getMultipleBindList();
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

		for (MultipleBind mb: bindings)
		{
			sb.append(prefix);
			sb.append(mb);
			prefix = ", ";
		}

		sb.append(" &");

		return sb.toString();
	}
}
