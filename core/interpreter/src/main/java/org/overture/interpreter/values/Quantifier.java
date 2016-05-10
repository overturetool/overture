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

package org.overture.interpreter.values;

import java.util.List;
import java.util.Vector;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.patterns.PPattern;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.PatternMatchException;

public class Quantifier
{
	public final PPattern pattern;
	public final ValueList values;
	private List<NameValuePairList> nvlist;

	public Quantifier(PPattern pattern, ValueList values)
	{
		this.pattern = pattern;
		this.values = values;
		this.nvlist = new Vector<>(values.size());
	}

	public int size(Context ctxt, boolean allPossibilities)
			throws AnalysisException
	{
		for (Value value : values)
		{
			try
			{
				if (allPossibilities)
				{
					nvlist.addAll(ctxt.assistantFactory.createPPatternAssistant().getAllNamedValues(pattern, value, ctxt));
				} else
				{
					nvlist.add(ctxt.assistantFactory.createPPatternAssistant().getNamedValues(pattern, value, ctxt));
				}
			} catch (PatternMatchException e)
			{
				// Should never happen
			}
		}

		return nvlist.size();
	}

	public NameValuePairList get(int index) throws PatternMatchException
	{
		if (index >= nvlist.size()) // no values
		{
			return new NameValuePairList();
		}

		return nvlist.get(index);
	}
}
