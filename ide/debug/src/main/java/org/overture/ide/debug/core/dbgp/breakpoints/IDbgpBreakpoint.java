/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.overture.ide.debug.core.dbgp.breakpoints;

public interface IDbgpBreakpoint {
	final int HIT_CONDITION_GREATER_OR_EQUAL = 0;

	final int HIT_CONDITION_EQUAL = 1;

	final int HIT_CONDITION_MULTIPLE = 2;
	
	final int HIT_NOT_SET = -1;

	String getId();

	boolean isEnabled();

	// -1 if not available
	int getHitCount();

	// -1 if not set
	int getHitValue();

	// -1 if not set
	int getHitCondition();
}
