/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.overture.ide.debug.core.model;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IThread;
import org.overture.ide.debug.core.dbgp.IDbgpSession;
import org.overture.ide.debug.core.dbgp.IDbgpStatusInterpreterThreadState;
import org.overture.ide.debug.core.dbgp.breakpoints.IDbgpBreakpoint;
import org.overture.ide.debug.core.model.eval.IVdmEvaluationEngine;
import org.overture.ide.debug.core.model.internal.IVdmStreamProxy;

public interface IVdmThread extends IThread /* , IFilteredStep */{
	int ERR_THREAD_NOT_SUSPENDED = -3;

	IDbgpSession getDbgpSession();

	IDbgpBreakpoint getDbgpBreakpoint(String id);

	IVdmStreamProxy getStreamProxy();

	IVdmEvaluationEngine getEvaluationEngine();

	int getModificationsCount();

	void sendTerminationRequest() throws DebugException;

	int getPropertyPageSize();

	boolean retrieveGlobalVariables();

	boolean retrieveClassVariables();

	boolean retrieveLocalVariables();

	void updateStackFrames();
	
	IDbgpStatusInterpreterThreadState getInterpreterState();
}
