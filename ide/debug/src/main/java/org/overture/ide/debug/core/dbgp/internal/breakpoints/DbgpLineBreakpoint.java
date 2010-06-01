/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.overture.ide.debug.core.dbgp.internal.breakpoints;

import org.overture.ide.debug.core.dbgp.breakpoints.IDbgpLineBreakpoint;

public class DbgpLineBreakpoint extends DbgpBreakpoint implements
		IDbgpLineBreakpoint {
	private final String fileName;

	private final int lineNumber;

	public DbgpLineBreakpoint(String id, boolean enabled, int hitValue,
			int hitCount, String hitCondition, String fileName, int lineNumber) {
		super(id, enabled, hitValue, hitCount, hitCondition);

		this.fileName = fileName;
		this.lineNumber = lineNumber;
	}

	public String getFilename() {
		return fileName;
	}

	public int getLineNumber() {
		return lineNumber;
	}
}
