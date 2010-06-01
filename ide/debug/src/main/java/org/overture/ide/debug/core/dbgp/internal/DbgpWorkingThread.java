/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.overture.ide.debug.core.dbgp.internal;

import java.io.IOException;

import org.overture.ide.debug.core.VdmDebugPlugin;

public abstract class DbgpWorkingThread extends DbgpTermination {
	private Thread thread;
	private final String name;

	public DbgpWorkingThread(String name) {
		this.name = name;
	}

	public void start() {
		if (thread == null || !thread.isAlive()) {
			thread = new Thread(new Runnable() {
				public void run() {
					try {
						workingCycle();
					} catch (Exception e) {
						if (isLoggable(e)) {
							VdmDebugPlugin
									.logError(
											"workingCycleError",
											e);
						}
						fireObjectTerminated(e);
						return;
					}

					fireObjectTerminated(null);
				}
			}, name);

			thread.start();
		} else {
			throw new IllegalStateException(
					"threadAlreadyStarted");
		}
	}

	public void requestTermination() {
		if (thread != null && thread.isAlive()) {
			thread.interrupt();
		}
	}

	public void waitTerminated() throws InterruptedException {
		if (thread != null)
			thread.join();
	}

	/**
	 * Tests if this exception should be logged. The rationale here is
	 * IOExceptions/SocketExceptions occurs always after socket is closed, so
	 * there is no point to log it.
	 * 
	 * @param e
	 * @return
	 */
	protected boolean isLoggable(Exception e) {
		return !(e instanceof IOException);
	}

	// Working cycle
	protected abstract void workingCycle() throws Exception;
}
