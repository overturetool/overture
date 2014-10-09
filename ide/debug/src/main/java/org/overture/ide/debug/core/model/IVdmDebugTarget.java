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
package org.overture.ide.debug.core.model;

import java.net.URI;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IDebugTarget;
import org.overture.ide.core.resources.IVdmProject;
import org.overture.ide.debug.core.IDebugOptions;
import org.overture.ide.debug.core.dbgp.IDbgpSession;
import org.overture.ide.debug.core.model.internal.IVdmStreamProxy;
import org.overture.ide.debug.logging.LogItem;

public interface IVdmDebugTarget extends IDebugTarget
{
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

	// IDLTKLanguageToolkit getLanguageToolkit();

	/**
	 * Returns <code>true</code> if the thread should break on the first executable line of code, <code>false</code>
	 * otherwise.
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
