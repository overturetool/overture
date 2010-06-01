/*******************************************************************************
 * Copyright (c) 2008 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation
 *     xored software, Inc. - remove DLTKDebugPlugin preferences dependency (Alex Panchenko) 
 *******************************************************************************/
package org.overture.ide.debug.core.model.internal;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.overture.ide.debug.core.VdmDebugPlugin;
import org.overture.ide.debug.core.IDbgpService;
import org.overture.ide.debug.core.IDebugPreferenceConstants;
import org.overture.ide.debug.core.dbgp.DbgpServer;
import org.overture.ide.debug.core.dbgp.IDbgpServerListener;
import org.overture.ide.debug.core.dbgp.IDbgpSession;
import org.overture.ide.debug.core.dbgp.IDbgpSessionInfo;
import org.overture.ide.debug.core.dbgp.IDbgpThreadAcceptor;
import org.overture.ide.debug.core.dbgp.internal.IDbgpTerminationListener;

public class DbgpService implements IDbgpService, IDbgpTerminationListener,
		IDbgpServerListener {
	private static final int FROM_PORT = 10000;
	private static final int TO_PORT = 50000;

	protected static final int SERVER_SOCKET_TIMEOUT = 500;
	protected static final int CLIENT_SOCKET_TIMEOUT = 10000000;

	private DbgpServer server;

	private final Map acceptors = Collections.synchronizedMap(new HashMap());

	private int serverPort;

	private void stopServer() {
		if (server != null) {
			try {
				server.removeTerminationListener(this);
				server.setListener(null);
				server.requestTermination();
				try {
					server.waitTerminated();
				} catch (InterruptedException e) {
					VdmDebugPlugin.log(e);
				}
			} finally {
				server = null;
			}
		}
	}

	private void startServer(int port) {
		serverPort = port;

		server = createServer(port);
		server.addTerminationListener(this);
		server.setListener(this);
		server.start();
	}

	protected DbgpServer createServer(int port) {
		return new DbgpServer(port, CLIENT_SOCKET_TIMEOUT);
	}

	private void restartServer(int port) {
		stopServer();
		startServer(port);
	}

	public DbgpService(int port) {
		if (port == IDebugPreferenceConstants.DBGP_AVAILABLE_PORT) {
			port = DbgpServer.findAvailablePort(FROM_PORT, TO_PORT);
		}
		startServer(port);
	}

	public void shutdown() {
		stopServer();
	}

	public int getPort() {
		return serverPort;
	}

	/**
	 * Waits until the socket is actually started using the default timeout.
	 * 
	 * @return <code>true</code> if socket was successfully started and
	 *         <code>false</code> otherwise.
	 */
	public boolean waitStarted() {
		return server != null && server.waitStarted();
	}

	/**
	 * Waits until the socket is actually started using specified timeout.
	 * 
	 * @return <code>true</code> if socket was successfully started and
	 *         <code>false</code> otherwise.
	 */
	public boolean waitStarted(long timeout) {
		return server != null && server.waitStarted(timeout);
	}

	// Acceptors
	public void registerAcceptor(String id, IDbgpThreadAcceptor acceptor) {
		acceptors.put(id, acceptor);
	}

	public IDbgpThreadAcceptor unregisterAcceptor(String id) {
		return (IDbgpThreadAcceptor) acceptors.remove(id);
	}

	public void restart(int newPort) {
		if (newPort != IDebugPreferenceConstants.DBGP_AVAILABLE_PORT) {
			// Only restart if concrete port specified
			restartServer(newPort);
		}
	}

	// IDbgpTerminationListener
	public void objectTerminated(Object object, Exception e) {
		if (e != null) {
			VdmDebugPlugin.log(e);
			final Job job = new Job("ServerRestart") {

				protected IStatus run(IProgressMonitor monitor) {
					restartServer(serverPort);
					return Status.OK_STATUS;
				}

			};
			job.schedule(2000);
		}
	}

	public boolean available() {
		return true;
	}

	// INewDbgpServerListener
	public void clientConnected(IDbgpSession session) {
		final IDbgpSessionInfo info = session.getInfo();
		if (info != null) {
			final IDbgpThreadAcceptor acceptor = (IDbgpThreadAcceptor) acceptors
					.get(info.getIdeKey());
			if (acceptor != null) {
				acceptor.acceptDbgpThread(session, new NullProgressMonitor());
			} else {
				session.requestTermination();
			}
		}
	}
}
