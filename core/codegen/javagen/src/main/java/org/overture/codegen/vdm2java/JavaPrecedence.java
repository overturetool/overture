/*
 * #%~
 * VDM Code Generator
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
package org.overture.codegen.vdm2java;

import org.overture.codegen.ir.IROperatorInfo;
import org.overture.codegen.ir.IROperatorLookup;
import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.expressions.ADivideNumericBinaryExpIR;
import org.overture.codegen.ir.expressions.ASubtractNumericBinaryExpIR;

public class JavaPrecedence
{
	public IROperatorLookup opLookup;

	public JavaPrecedence()
	{
		this.opLookup = new IROperatorLookup();
	}

	public boolean mustIsolate(SExpIR parentExp, SExpIR exp, boolean leftChild)
	{
		IROperatorInfo parentOpInfo = opLookup.find(parentExp.getClass());

		if (parentOpInfo == null)
		{
			return false;
		}

		IROperatorInfo expOpInfo = opLookup.find(exp.getClass());

		if (expOpInfo == null)
		{
			return false;
		}

		// Case 1: Protect against cases like "1 / (2*3*4)"
		// Don't care about left children, i.e. "(2*3*4)/1 = 2*3*4/1"

		// Similar for subtract: "1 - (1+2+3)" and "1+2+3-3"

		// We don't need to consider 'mod' and 'rem' operators since these are constructed
		// using other operators and isolated if needed using the isolation expression
		boolean case1 = !leftChild
				&& (parentExp instanceof ADivideNumericBinaryExpIR
						|| parentExp instanceof ASubtractNumericBinaryExpIR)
				&& parentOpInfo.getPrecedence() >= expOpInfo.getPrecedence();

		if (case1)
		{
			return true;
		}

		// Case 2: Protect against case like 1 / (1+2+3)
		boolean case2 = parentOpInfo.getPrecedence() > expOpInfo.getPrecedence();

		return case2;
	}
}
