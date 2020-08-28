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
package org.overture.ide.debug.core.model.internal;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.ISuspendResume;
import org.eclipse.debug.core.model.ITerminate;
import org.overture.ide.debug.core.dbgp.IDbgpThreadAcceptor;
import org.overture.ide.debug.core.model.IVdmDebugThreadConfigurator;
import org.overture.ide.debug.core.model.IVdmThread;
import org.overture.ide.debug.core.model.internal.operations.DbgpDebugger;

public interface IVdmThreadManager extends IDbgpThreadAcceptor, ITerminate,
		ISuspendResume
{

	// Listener
	void addListener(IVdmThreadManagerListener listener);

	void removeListener(IVdmThreadManagerListener listener);

	// Thread management
	boolean hasThreads();

	IVdmThread[] getThreads();

	void terminateThread(IVdmThread thread);

	boolean isWaitingForThreads();

	void sendTerminationRequest() throws DebugException;

	public void refreshThreads();

	/**
	 * Used to configure thread with additional DBGp features, etc.
	 */
	void configureThread(DbgpDebugger engine, VdmThread scriptThread);

	public void setVdmThreadConfigurator(
			IVdmDebugThreadConfigurator configurator);

	void stepInto() throws DebugException;

	void stepOver() throws DebugException;

	void stepReturn() throws DebugException;

	Boolean handleCustomTerminationCommands();
}
