/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.overture.ide.debug.core.model;

import org.eclipse.core.runtime.CoreException;

/**
 * A method entry breakpoint suspends execution on the first executable line of
 * a method when entered.
 */
public interface IVdmMethodEntryBreakpoint extends IVdmLineBreakpoint {
	// Method name
	String getMethodName() throws CoreException;

	// Break on entry
	boolean breakOnEntry() throws CoreException;
	
	void setBreakOnEntry(boolean value) throws CoreException;

	// Break on exit
	boolean breakOnExit() throws CoreException;
	
	void setBreakOnExit(boolean value) throws CoreException;

	// Entry breakpoint id
	String getEntryBreakpointId() throws CoreException;

	void setEntryBreakpointId(String id) throws CoreException;
	
	// Exit breakpoint id
	String getExitBreakpointId() throws CoreException;

	void setExitBreakpointId(String id) throws CoreException;
}
