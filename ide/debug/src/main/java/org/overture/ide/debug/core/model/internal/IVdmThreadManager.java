package org.overture.ide.debug.core.model.internal;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.ISuspendResume;
import org.eclipse.debug.core.model.ITerminate;
import org.overture.ide.debug.core.dbgp.IDbgpThreadAcceptor;
import org.overture.ide.debug.core.model.IVdmDebugThreadConfigurator;
import org.overture.ide.debug.core.model.IVdmThread;
import org.overture.ide.debug.core.model.internal.operations.DbgpDebugger;

public interface IVdmThreadManager extends IDbgpThreadAcceptor, ITerminate,
		ISuspendResume {

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

	

	void handleCustomTerminationCommands();
}
