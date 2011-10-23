/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.overture.ide.debug.core.dbgp.internal.commands;

import java.io.IOException;
import java.util.IdentityHashMap;
import java.util.Map;

import org.overture.ide.debug.core.DebugOption;
import org.overture.ide.debug.core.IDebugOptions;
import org.overture.ide.debug.core.dbgp.DbgpBaseCommands;
import org.overture.ide.debug.core.dbgp.DbgpRequest;
import org.overture.ide.debug.core.dbgp.IDbgpCommunicator;
import org.overture.ide.debug.core.dbgp.exceptions.DbgpException;
import org.overture.ide.debug.core.dbgp.exceptions.DbgpIOException;
import org.overture.ide.debug.core.dbgp.exceptions.DbgpOpertionCanceledException;
import org.overture.ide.debug.core.dbgp.exceptions.DbgpTimeoutException;
import org.overture.ide.debug.core.dbgp.internal.IDbgpDebugingEngine;
import org.overture.ide.debug.core.dbgp.internal.packets.DbgpResponsePacket;
import org.overture.ide.debug.core.dbgp.internal.utils.DbgpXmlParser;
import org.w3c.dom.Element;

public class DbgpDebuggingEngineCommunicator implements IDbgpCommunicator {
	private final int timeout;

	private final IDbgpDebugingEngine engine;
	private IDebugOptions options;

	private void sendRequest(DbgpRequest command) throws IOException {
		engine.sendCommand(command);
	}

	private DbgpResponsePacket receiveResponse(int transactionId)
			throws IOException, InterruptedException {
		return engine.getResponsePacket(transactionId, timeout);
	}

	public DbgpDebuggingEngineCommunicator(IDbgpDebugingEngine engine,
			IDebugOptions options) {
		if (engine == null) {
			throw new IllegalArgumentException();
		}

		this.engine = engine;
		this.options = options;

		timeout = 0;
//		timeout = VdmDebugPlugin.getDefault().getPluginPreferences().getInt(
//				IDebugConstants.PREF_DBGP_RESPONSE_TIMEOUT);
	}

	private final Map<DbgpRequest,DbgpRequest> activeRequests = new IdentityHashMap<DbgpRequest,DbgpRequest>();

	public Element communicate(DbgpRequest request) throws DbgpException {
		try {
			final DbgpResponsePacket packet;
			final int requestId = Integer.parseInt(request
					.getOption(DbgpBaseCommands.ID_OPTION));
			if (options.get(DebugOption.DBGP_ASYNC) || request.isAsync()) {
				sendRequest(request);
				packet = receiveResponse(requestId);
			} else {
				final long startTime = DEBUG ? System.currentTimeMillis() : 0;
				beginSyncRequest(request);
				if (DEBUG) {
					final long waited = System.currentTimeMillis() - startTime;
					if (waited > 0) {
						System.out.println("Waited " + waited + " ms"); //$NON-NLS-1$ //$NON-NLS-2$
					}
				}
				try {
					sendRequest(request);
					packet = receiveResponse(requestId);
				} finally {
					endSyncRequest(request);
				}
			}

			if (packet == null) {
				throw new DbgpTimeoutException(request);
			}

			Element response = packet.getContent();

			DbgpException e = DbgpXmlParser.checkError(response);
			if (e != null) {
				throw e;
			}

			return response;
		} catch (InterruptedException e) {
			throw new DbgpOpertionCanceledException(e);
		} catch (IOException e) {
			throw new DbgpIOException(e);
		}
	}

	private void endSyncRequest(DbgpRequest request) {
		synchronized (activeRequests) {
			activeRequests.remove(request);
			activeRequests.notifyAll();
		}
	}

	private void beginSyncRequest(DbgpRequest request)
			throws InterruptedException {
		synchronized (activeRequests) {
			while (!activeRequests.isEmpty()) {
				activeRequests.wait();
			}
			activeRequests.put(request, request);
		}
	}

	public void send(DbgpRequest request) throws DbgpException {
		try {
			sendRequest(request);
		} catch (IOException e) {
			throw new DbgpIOException(e);
		}
	}

	private static final boolean DEBUG = false;

	public IDebugOptions getDebugOptions() {
		return options;
	}

	public void configure(IDebugOptions debugOptions) {
		this.options = debugOptions;
	}
}
