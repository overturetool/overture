/*******************************************************************************
 * Copyright (c) 2008 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.overture.ide.debug.core;

import org.overture.ide.debug.core.IDebugOptions.BooleanOption;

public class DebugOption {

	/**
	 * All commands should be treated as asynchronous
	 */
	public static final BooleanOption DBGP_ASYNC = new BooleanOption(
			"DBGP_ASYNC", true); //$NON-NLS-1$

	/**
	 * <code>breakpoint_update</code> command can update line number
	 */
	public static final BooleanOption DBGP_BREAKPOINT_UPDATE_LINE_NUMBER = new BooleanOption(
			"DBGP_BREAKPOINT_UPDATE_LINE_NUMBER", true); //$NON-NLS-1$

	/**
	 * Debugging engine supports different data types. Should be
	 * <code>false</code> for TCL.
	 */
	public static final BooleanOption ENGINE_SUPPORT_DATATYPES = new BooleanOption(
			"ENGINE_SUPPORT_DATATYPES", true); //$NON-NLS-1$

	/**
	 * Debugging engine initially stops before first line. Should be
	 * <code>false</code> for TCL.
	 */
	public static final BooleanOption ENGINE_STOP_BEFORE_CODE = new BooleanOption(
			"ENGINE_STOP_BEFORE_CODE", true); //$NON-NLS-1$

	/**
	 * Should test if thread has valid current stack on initial connect. Should
	 * be <code>true</code> for TCL.
	 */
	public static final BooleanOption ENGINE_VALIDATE_STACK = new BooleanOption(
			"ENGINE_VALIDATE_STACK", false); //$NON-NLS-1$
}
