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

public class DeadlineMet extends ConjectureDefinition
{

	public DeadlineMet(String name, OperationValidationExpression opExpr,
			ValueValidationExpression valueExpr,
			IValidationExpression endingExpr, int interval)
	{
		super(name, opExpr, valueExpr, endingExpr, interval);
		startupValue = false;
	}

	@Override
	public boolean validate(long triggerTime, long endTime)
	{
		return endTime - triggerTime <= this.interval;
	}

	@Override
	public String toString()
	{
		StringBuffer s = new StringBuffer();
		s.append("deadlineMet(");
		s.append(super.toString());
		s.append(")");
		return s.toString();
	}

}
