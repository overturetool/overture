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
package org.overture.ide.debug.core.dbgp.internal.breakpoints;

import org.overture.ide.debug.core.dbgp.breakpoints.IDbgpBreakpoint;

public class DbgpBreakpoint implements IDbgpBreakpoint
{
	private final String id;

	private final boolean enabled;

	// Number of breakpoint hits
	private final int hitCount;

	// Hit value for hit condition
	private final int hitValue;

	// Hit condition
	private final int hitCondition;

	protected int convertHitCondition(String s)
	{
		if (">=".equals(s)) { //$NON-NLS-1$
			return HIT_CONDITION_GREATER_OR_EQUAL;
		} else if ("==".equals(s)) { //$NON-NLS-1$
			return HIT_CONDITION_EQUAL;
		} else if ("%".equals(s)) { //$NON-NLS-1$
			return HIT_CONDITION_MULTIPLE;
		} else if ("".equals(s)) { //$NON-NLS-1$
			return HIT_NOT_SET;
		}

		throw new IllegalArgumentException("Invalid Hit Condition Value");
	}

	public DbgpBreakpoint(String id, boolean enabled, int hitValue,
			int hitCount, String hitCondition)
	{
		this.id = id;
		this.enabled = enabled;
		this.hitValue = hitValue;
		this.hitCount = hitCount;
		this.hitCondition = convertHitCondition(hitCondition);
	}

	public int getHitCondition()
	{
		return hitCondition;
	}

	public int getHitCount()
	{
		return hitCount;
	}

	public int getHitValue()
	{
		return hitValue;
	}

	public String getId()
	{
		return id;
	}

	public boolean isEnabled()
	{
		return enabled;
	}
}
