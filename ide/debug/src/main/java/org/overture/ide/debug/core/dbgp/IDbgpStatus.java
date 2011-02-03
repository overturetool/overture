/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.overture.ide.debug.core.dbgp;

public interface IDbgpStatus {
	// State
	boolean isStarting();

	boolean isStopping();

	boolean isStopped();

	boolean isRunning();

	boolean isBreak();

	// Reason
	boolean reasonOk();

	boolean reasonError();

	boolean reasonAborred();

	boolean reasonException();
	
	//additional
	IDbgpStatusInterpreterThreadState getInterpreterThreadState();
}
