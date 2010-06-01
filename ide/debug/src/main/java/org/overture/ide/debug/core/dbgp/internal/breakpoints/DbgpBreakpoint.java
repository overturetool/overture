/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.overture.ide.debug.core.dbgp.internal.breakpoints;

import org.overture.ide.debug.core.dbgp.breakpoints.IDbgpBreakpoint;

public class DbgpBreakpoint implements IDbgpBreakpoint {
	private final String id;

	private final boolean enabled;
	
	// Number of breakpoint hits
	private final int hitCount;	

	// Hit value for hit condition
	private final int hitValue;

	// Hit condition
	private final int hitCondition;

	protected int convertHitCondition(String s) {
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
			int hitCount, String hitCondition) {
		this.id = id;
		this.enabled = enabled;
		this.hitValue = hitValue;
		this.hitCount = hitCount;
		this.hitCondition = convertHitCondition(hitCondition);
	}

	public int getHitCondition() {
		return hitCondition;
	}

	public int getHitCount() {
		return hitCount;
	}

	public int getHitValue() {
		return hitValue;
	}

	public String getId() {
		return id;
	}

	public boolean isEnabled() {
		return enabled;
	}
}
