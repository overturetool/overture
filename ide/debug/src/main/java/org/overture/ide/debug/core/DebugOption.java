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
package org.overture.ide.debug.core;

import org.overture.ide.debug.core.IDebugOptions.BooleanOption;

public class DebugOption
{

	/**
	 * All commands should be treated as asynchronous
	 */
	public static final BooleanOption DBGP_ASYNC = new BooleanOption("DBGP_ASYNC", true); //$NON-NLS-1$

	/**
	 * <code>breakpoint_update</code> command can update line number
	 */
	public static final BooleanOption DBGP_BREAKPOINT_UPDATE_LINE_NUMBER = new BooleanOption("DBGP_BREAKPOINT_UPDATE_LINE_NUMBER", true); //$NON-NLS-1$

	/**
	 * Debugging engine supports different data types. Should be <code>false</code> for TCL.
	 */
	public static final BooleanOption ENGINE_SUPPORT_DATATYPES = new BooleanOption("ENGINE_SUPPORT_DATATYPES", true); //$NON-NLS-1$

	/**
	 * Debugging engine initially stops before first line. Should be <code>false</code> for TCL.
	 */
	public static final BooleanOption ENGINE_STOP_BEFORE_CODE = new BooleanOption("ENGINE_STOP_BEFORE_CODE", true); //$NON-NLS-1$

	/**
	 * Should test if thread has valid current stack on initial connect. Should be <code>true</code> for TCL.
	 */
	public static final BooleanOption ENGINE_VALIDATE_STACK = new BooleanOption("ENGINE_VALIDATE_STACK", false); //$NON-NLS-1$
}
