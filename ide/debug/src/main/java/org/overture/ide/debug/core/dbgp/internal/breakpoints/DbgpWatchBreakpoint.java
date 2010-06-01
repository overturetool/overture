/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.overture.ide.debug.core.dbgp.internal.breakpoints;

import org.overture.ide.debug.core.dbgp.breakpoints.IDbgpWatchBreakpoint;

public class DbgpWatchBreakpoint extends DbgpBreakpoint implements
		IDbgpWatchBreakpoint {

	private final String expression;

	public DbgpWatchBreakpoint(String id, boolean enabled, int hitValue,
			int hitCount, String hitCondition, String expression) {
		super(id, enabled, hitValue, hitCount, hitCondition);
		this.expression = expression;
	}

	public String getExpression() {
		return expression;
	}
}
