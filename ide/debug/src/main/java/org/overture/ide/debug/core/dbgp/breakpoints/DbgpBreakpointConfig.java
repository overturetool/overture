/*
 * #%~
 * org.overture.ide.debug
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
package org.overture.ide.debug.core.dbgp.breakpoints;

public class DbgpBreakpointConfig
{
	private static final String TEMPORARY_TRUE = "1"; //$NON-NLS-1$
	private static final String TEMPORARY_FALSE = "0"; //$NON-NLS-1$

	private static final String STATE_ENABLED = "enabled"; //$NON-NLS-1$
	private static final String STATE_DISABLED = "disabled"; //$NON-NLS-1$

	private static final String HIT_CONDITION_GREATER = ">="; //$NON-NLS-1$
	private static final String HIT_CONDITION_EQUAL = "=="; //$NON-NLS-1$
	private static final String HIT_CONDITION_MULTIPLE = "%"; //$NON-NLS-1$

	private boolean enabled;

	private boolean temporary;

	private int hitValue;

	private int hitCondition;

	private String expression;
	private int lineno;

	// Constructors
	public DbgpBreakpointConfig()
	{
		this(true);
	}

	public DbgpBreakpointConfig(boolean enabled)
	{
		this(enabled, -1, -1, null);
	}

	public DbgpBreakpointConfig(boolean enabled, int hitValue,
			int hitCondition, String expression)
	{
		this(enabled, hitValue, hitCondition, false, expression, -1);
	}

	public DbgpBreakpointConfig(boolean enabled, int hitValue,
			int hitCondition, boolean temporary, String expression, int lineno)
	{
		this.enabled = enabled;
		this.hitValue = hitValue;
		this.hitCondition = hitCondition;
		this.temporary = temporary;
		this.expression = expression;
		this.lineno = lineno;
	}

	// Enabled
	public boolean isEnabled()
	{
		return enabled;
	}

	public void setEnabled(boolean value)
	{
		this.enabled = value;
	}

	// Temporary
	public boolean isTemporary()
	{
		return temporary;
	}

	public void setTemporary(boolean value)
	{
		this.temporary = value;
	}

	// Hit value
	public int getHitValue()
	{
		return hitValue;
	}

	public void setHitValue(int hitValue)
	{
		this.hitValue = hitValue;
	}

	// Hit condition
	public int getHitCondition()
	{
		return hitCondition;
	}

	public void setHitCondition(int hitCondition)
	{
		this.hitCondition = hitCondition;
	}

	// Expression
	public String getExpression()
	{
		return expression;
	}

	public void setExpression(String expression)
	{
		this.expression = expression;
	}

	// Strings
	public String getTemporaryString()
	{
		return temporary ? TEMPORARY_TRUE : TEMPORARY_FALSE;
	}

	public String getStateString()
	{
		return enabled ? STATE_ENABLED : STATE_DISABLED;
	}

	public String getHitConditionString()
	{
		if (hitCondition == IDbgpBreakpoint.HIT_CONDITION_EQUAL)
		{
			return HIT_CONDITION_EQUAL;
		} else if (hitCondition == IDbgpBreakpoint.HIT_CONDITION_GREATER_OR_EQUAL)
		{
			return HIT_CONDITION_GREATER;
		} else if (hitCondition == IDbgpBreakpoint.HIT_CONDITION_MULTIPLE)
		{
			return HIT_CONDITION_MULTIPLE;
		}

		return null;
	}

	public int getLineNo()
	{
		return lineno;
	}

	public void setLineNo(int value)
	{
		lineno = value;
	}
}
