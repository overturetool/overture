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

import org.overture.interpreter.messages.rtlog.RTMessage.MessageType;
import org.overture.interpreter.scheduler.SystemClock;
import org.overture.interpreter.values.Value;

public abstract class ConjectureDefinition
{

	public String name;
	public OperationValidationExpression opExpr;
	public ValueValidationExpression valueExpr;
	public IValidationExpression endingExpr;
	public int interval;
	private List<ConjectureValue> conjectureValues = new ArrayList<>();
	public boolean startupValue;

	public ConjectureDefinition(String name,
			OperationValidationExpression opExpr,
			IValidationExpression endingExpr, int interval)
	{
		this.name = name;
		this.opExpr = opExpr;
		this.endingExpr = endingExpr;
		this.valueExpr = null;
		this.interval = interval;
	}

	public ConjectureDefinition(String name,
			OperationValidationExpression opExpr,
			ValueValidationExpression valueExpr,
			IValidationExpression endingExpr, int interval)
	{
		this.name = name;
		this.opExpr = opExpr;
		this.endingExpr = endingExpr;
		this.valueExpr = valueExpr;
		this.interval = interval;

		if (valueExpr != null)
		{
			valueExpr.setConjecture(this);
		}

		if (endingExpr instanceof ValueValidationExpression)
		{
			((ValueValidationExpression) endingExpr).setConjecture(this);
		}
	}

	public ConjectureDefinition(ValueValidationExpression valueExpr,
			IValidationExpression endingExpr, int interval)
	{
		this.opExpr = null;
		this.endingExpr = endingExpr;
		this.valueExpr = valueExpr;
		this.interval = interval;
	}

	public ConjectureDefinition(ConjectureDefinition c)
	{
		this.opExpr = c.opExpr;
		this.endingExpr = c.endingExpr;
		this.valueExpr = c.valueExpr;
		this.interval = c.interval;
	}

	abstract public boolean validate(long triggerTime, long endTime);

	public List<String[]> getMonitoredValues()
	{
		List<String[]> res = new ArrayList<>();

		if (this.valueExpr != null)
		{
			res.addAll(this.valueExpr.getMonitoredValues());
		}

		if (this.endingExpr instanceof ValueValidationExpression)
		{
			res.addAll(((ValueValidationExpression) this.endingExpr).getMonitoredValues());
		}

		return res;
	}

	public void process(String opname, String classname, MessageType kind,
			long wallTime, long threadId, int objectReference)
	{

		if (endingExpr instanceof OperationValidationExpression)
		{
			OperationValidationExpression ove = (OperationValidationExpression) endingExpr;

			if (ove.matches(opname, classname, kind))
			{
				for (ConjectureValue conj : conjectureValues)
				{
					if (!conj.isEnded())
					{
						conj.setEnd(wallTime, threadId, objectReference);
					}
				}
			}
		}

		if (opExpr.matches(opname, classname, kind))
		{

			if (valueExpr != null)
			{
				if (valueExpr.isTrue())
				{
					conjectureValues.add(new ConjectureValue(this, wallTime, threadId, objectReference));
				}
			} else
			{
				conjectureValues.add(new ConjectureValue(this, wallTime, threadId, objectReference));
			}

		}

	}

	public boolean associatedWith(String classname, String opname)
	{

		if (this.opExpr != null)
		{
			if (this.opExpr.isAssociatedWith(opname, classname))
			{
				return true;
			}
		}

		if (endingExpr instanceof OperationValidationExpression)
		{
			return ((OperationValidationExpression) this.endingExpr).isAssociatedWith(opname, classname);
		}

		return false;
	}

	@Override
	public String toString()
	{
		StringBuffer s = new StringBuffer();

		if (opExpr != null)
		{
			s.append(opExpr.toString());
			s.append(",");
		}

		if (valueExpr != null)
		{
			s.append(valueExpr.toString());
			s.append(",");
		}

		s.append(endingExpr.toString());
		s.append(",");
		s.append(interval);

		return s.toString();
	}

	public void associateVariable(String[] strings, Value v)
	{
		if (valueExpr != null)
		{
			if (valueExpr.isValueMonitored(strings))
			{
				valueExpr.associateVariable(strings, v);
			}

		}

		if (endingExpr instanceof ValueValidationExpression)
		{
			ValueValidationExpression vve = (ValueValidationExpression) endingExpr;
			if (vve.isValueMonitored(strings))
			{
				vve.associateVariable(strings, v);
			}
		}

	}

	public void valueChanged(ValueValidationExpression valueValidationExpression)
	{
		if (opExpr == null && valueValidationExpression == valueExpr)
		{
			if (valueExpr.isTrue())
			{
				conjectureValues.add(new ConjectureValue(this, SystemClock.getWallTime(), -1, -1));
			}
		} else
		{
			if (endingExpr instanceof ValueValidationExpression)
			{
				if (((ValueValidationExpression) endingExpr).isTrue())
				{
					for (ConjectureValue conj : conjectureValues)
					{
						if (!conj.isEnded())
						{
							conj.setEnd(SystemClock.getWallTime(), -1, -1);
						}
					}
				}
			}
		}

	}

	public boolean isPassed()
	{
		for (ConjectureValue cv : conjectureValues)
		{
			if (!cv.isValidated())
			{
				return false;
			}
		}

		return true;
	}

	public void printLogFormat()
	{
		StringBuffer s = new StringBuffer();
		s.append("\"" + this.name + "\"" + " " + "\"" + this.toString() + "\""
				+ " ");

		if (isPassed())
		{
			s.append("PASS");
			System.out.println(s.toString());
		} else
		{
			for (ConjectureValue cv : conjectureValues)
			{
				if (!cv.isValidated())
				{
					StringBuffer ts = new StringBuffer(s.toString());

					ts.append(cv.triggerTime);
					ts.append(" ");
					ts.append(cv.triggerThreadId);
					ts.append(" ");
					ts.append(cv.endTime);
					ts.append(" ");
					ts.append(cv.endThreadId);
					System.out.println(ts.toString());
				}

			}
		}

	}

	public String getLogFormat()
	{
		StringBuffer s = new StringBuffer();

		if (isPassed())
		{

			s.append("\"" + this.name + "\"" + " " + "\"" + this.toString()
					+ "\"" + " ");
			s.append("PASS");
			s.append("\n");
		} else
		{
			for (ConjectureValue cv : conjectureValues)
			{
				if (!cv.isValidated())
				{
					s.append("\"" + this.name + "\"" + " " + "\""
							+ this.toString() + "\"" + " ");
					s.append(cv.triggerTime);
					s.append(" ");
					s.append(cv.triggerThreadId);
					s.append(" ");
					s.append(cv.endTime);
					s.append(" ");
					s.append(cv.endThreadId);
					s.append("\n");
				}
			}
		}

		return s.toString();
	}

}
