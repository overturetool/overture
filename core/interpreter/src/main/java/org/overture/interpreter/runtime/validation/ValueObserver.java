/***************************************************************************
 *
 *	Copyright (c) 2009 IHA
 *
 *	Author: Kenneth Lausdahl and Augusto Ribeiro
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
 **************************************************************************/

package org.overture.interpreter.runtime.validation;

import org.overture.ast.intf.lex.ILexLocation;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.ValueException;
import org.overture.interpreter.values.Value;
import org.overture.interpreter.values.ValueListener;

public class ValueObserver implements ValueListener
{

	public String[] name;
	public Value v;
	private ValueValidationExpression expr;

	public ValueObserver(String[] name, Value v, ValueValidationExpression expr)
	{
		this.name = name;
		this.v = v;
		this.expr = expr;
	}

	public void changedValue(ILexLocation location, Value value, Context ctxt)
	{
		// System.out.println("Value " + printValueName() + " has changed to " + value.toString());
		this.expr.valueChanged(this);
	}

	// private String printValueName()
	// {
	// if(name.length == 2)
	// {
	// return name[0] + "`" + name[1];
	// }
	// else
	// {
	// return name[0] + "`" + name[1] + "." + name[2];
	// }
	// }

	public double getValue()
	{
		try
		{
			return v.realValue(null);
		} catch (ValueException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

}
