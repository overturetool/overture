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

import org.overture.ast.patterns.PPattern;
import org.overture.interpreter.assistant.pattern.PPatternAssistantInterpreter;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.PatternMatchException;


public class Quantifier
{
	public final PPattern pattern;
	public final ValueList values;
	private NameValuePairList[] nvlist;

	public Quantifier(PPattern pattern, ValueList values)
	{
		this.pattern = pattern;
		this.values = values;
		this.nvlist = new NameValuePairList[values.size()];
	}

	public int size()
	{
		return nvlist.length;
	}

	public NameValuePairList get(int index, Context ctxt)
		throws PatternMatchException
	{
		if (index >= nvlist.length)		// no values
		{
			return new NameValuePairList();
		}

		if (nvlist[index] == null)
		{
			nvlist[index] = PPatternAssistantInterpreter.getNamedValues(pattern, values.get(index), ctxt);
		}

		return nvlist[index];
	}
}
