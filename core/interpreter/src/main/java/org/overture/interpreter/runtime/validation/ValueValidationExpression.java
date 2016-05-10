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

import java.util.ArrayList;
import java.util.List;

import org.overture.interpreter.values.UpdatableValue;
import org.overture.interpreter.values.Value;
import org.overture.interpreter.values.ValueListenerList;

public class ValueValidationExpression implements IValidationExpression
{

	public enum BinaryOps
	{
		GREATER(">"), LESS("<"), EQ("="), GREATEREQ(">="), LESSEQ("<=");

		public final String syntax;

		private BinaryOps(String syntax)
		{
			this.syntax = syntax;
		}
	}

	private String[] leftName;
	private String[] rightName;
	private BinaryOps binaryOp;
	public ValueObserver leftValue;
	public ValueObserver rightValue;
	private ConjectureDefinition conjecture;

	public ValueValidationExpression(String[] leftName, BinaryOps binaryOp,
			String[] rightName)
	{
		this.leftName = leftName;
		this.binaryOp = binaryOp;
		this.rightName = rightName;

	}

	public void setConjecture(ConjectureDefinition c)
	{
		this.conjecture = c;
	}

	@Override
	public String toString()
	{
		StringBuffer s = new StringBuffer();

		s.append("ValueValidationExpression: ");
		s.append(printValueName(this.leftName));
		s.append(" ");
		s.append(this.binaryOp);
		s.append(" ");
		s.append(printValueName(this.rightName));

		return s.toString();
	}

	private String printValueName(String[] name)
	{
		if (name.length == 2)
		{
			return name[0] + "`" + name[1];
		} else
		{
			return name[0] + "`" + name[1] + "." + name[2];
		}
	}

	public List<String[]> getMonitoredValues()
	{
		ArrayList<String[]> res = new ArrayList<>();
		res.add(leftName);
		res.add(rightName);
		return res;

	}

	public boolean isTrue()
	{
		// System.out.println("Checking: " + this.toString());
		boolean result = false;

		switch (binaryOp)
		{
			case EQ:
				result = leftValue.getValue() == rightValue.getValue();
				break;
			case LESS:
				result = leftValue.getValue() < rightValue.getValue();
				break;
			case LESSEQ:
				result = leftValue.getValue() <= rightValue.getValue();
				break;
			case GREATER:
				result = leftValue.getValue() > rightValue.getValue();
				break;
			case GREATEREQ:
				result = leftValue.getValue() >= rightValue.getValue();
				break;
			default:
				break;
		}

		return result;
	}

	public void associateVariable(String[] strings, Value v)
	{
		if (isStringsEqual(strings, leftName))
		{
			leftValue = new ValueObserver(strings, v, this);
			if (v instanceof UpdatableValue)
			{
				UpdatableValue uv = (UpdatableValue) v;
				if (uv.listeners != null)
				{
					uv.listeners.add(leftValue);
				} else
				{
					uv.listeners = new ValueListenerList(leftValue);
				}
			}

		} else
		{
			rightValue = new ValueObserver(strings, v, this);
			if (v instanceof UpdatableValue)
			{
				UpdatableValue uv = (UpdatableValue) v;
				if (uv.listeners != null)
				{
					uv.listeners.add(rightValue);
				} else
				{
					uv.listeners = new ValueListenerList(rightValue);
				}
			}

		}

	}

	public boolean isValueMonitored(String[] strings)
	{
		return isStringsEqual(strings, leftName)
				|| isStringsEqual(strings, rightName);
	}

	private boolean isStringsEqual(String[] strings1, String[] strings2)
	{
		if (strings1.length == strings2.length)
		{
			for (int i = 0; i < strings2.length; i++)
			{
				if (!strings1[i].equals(strings2[i]))
				{
					return false;
				}
			}
			return true;
		} else
		{
			return false;
		}

	}

	public void valueChanged(ValueObserver valueObserver)
	{
		// System.out.println("Value Validation Expression: after value change: " + valueObserver.v.toString());
		conjecture.valueChanged(this);
	}
}
