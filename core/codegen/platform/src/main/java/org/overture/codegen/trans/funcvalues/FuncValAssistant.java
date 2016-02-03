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
package org.overture.codegen.trans.funcvalues;

import java.util.LinkedList;
import java.util.List;

import org.overture.codegen.ir.declarations.AInterfaceDeclIR;
import org.overture.codegen.ir.types.AMethodTypeIR;

public class FuncValAssistant
{
	private List<AInterfaceDeclIR> funcValInterfaces;

	public FuncValAssistant()
	{
		this.funcValInterfaces = new LinkedList<AInterfaceDeclIR>();
	}

	public List<AInterfaceDeclIR> getFuncValInterfaces()
	{
		return funcValInterfaces;
	}

	public void registerInterface(AInterfaceDeclIR functionValueInterface)
	{
		funcValInterfaces.add(functionValueInterface);
	}

	public AInterfaceDeclIR findInterface(AMethodTypeIR methodType)
	{
		for (AInterfaceDeclIR functionValueInterface : funcValInterfaces)
		{
			if (1 + methodType.getParams().size() == functionValueInterface.getTemplateTypes().size())
			{
				return functionValueInterface;
			}
		}

		return null;
	}
}
