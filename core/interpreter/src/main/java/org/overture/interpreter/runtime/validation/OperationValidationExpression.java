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

import org.overture.interpreter.messages.rtlog.RTMessage.MessageType;

public class OperationValidationExpression implements IValidationExpression
{

	private String className;
	private String opName;
	private MessageType type;

	// TODO: The names should probably be changed to LexNameToken
	public OperationValidationExpression(String opName, String className,
			MessageType type)
	{
		this.className = className;
		this.opName = opName;
		this.type = type;
	}

	public boolean evaluate()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String toString()
	{
		StringBuffer s = new StringBuffer();

		switch (type)
		{
			case Activate:
				s.append("#act");
				break;
			case Completed:
				s.append("#fin");
				break;
			case Request:
				s.append("#req");
				break;
			default:
				break;
		}
		s.append("(");
		s.append(className);
		s.append("`");
		s.append(opName);
		s.append(")");

		return s.toString();
	}

	public boolean isAssociatedWith(String opname, String classdef)
	{
		return opname.equals(this.opName) && classdef.equals(this.className);
	}

	public boolean matches(String opname, String classname, MessageType kind)
	{
		return opname.equals(this.opName) && classname.equals(this.className)
				&& kind.equals(this.type);
	}

}
