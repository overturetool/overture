/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package org.overture.ide.debug.core.model;

import java.net.URI;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IDebugTarget;
import org.overture.ide.core.resources.IVdmProject;
import org.overture.ide.debug.core.IDebugOptions;
import org.overture.ide.debug.core.dbgp.IDbgpSession;
import org.overture.ide.debug.core.model.internal.IVdmStreamProxy;
import org.overture.ide.debug.logging.LogItem;

public interface IVdmDebugTarget extends IDebugTarget {
	boolean isInitialized();

	// 
	String getSessionId();

	// Listener
	void addListener(IVdmDebugTargetListener listener);

	void removeListener(IVdmDebugTargetListener listener);

	// Request timeout
	void setRequestTimeout(int timeout);

	int getRequestTimeout();

	// Stream proxy management
	void setStreamProxy(IVdmStreamProxy proxy);

	IVdmStreamProxy getStreamProxy();

	// Run to line
	void runToLine(URI uri, int lineNumber) throws DebugException;

	void setFilters(String[] activeFilters);

	String[] getFilters();

	void setUseStepFilters(boolean useStepFilters);

	boolean isUseStepFilters();

	//IDLTKLanguageToolkit getLanguageToolkit();

	/**
	 * Returns <code>true</code> if the thread should break on the first
	 * executable line of code, <code>false</code> otherwise.
	 */
	boolean breakOnFirstLineEnabled();

	void toggleGlobalVariables(boolean enabled);

	void toggleClassVariables(boolean enabled);

	void toggleLocalVariables(boolean enabled);

	boolean retrieveGlobalVariables();

	boolean retrieveClassVariables();

	boolean retrieveLocalVariables();

	String getConsoleEncoding();

	IDebugOptions getOptions();

	IDbgpSession[] getSessions();

	IVdmBreakpointPathMapper getPathMapper();

	/**
	 * @since 2.0
	 */
	boolean isRemote();

	IVdmProject getVdmProject();
	
	public void printLog(LogItem item);
}
