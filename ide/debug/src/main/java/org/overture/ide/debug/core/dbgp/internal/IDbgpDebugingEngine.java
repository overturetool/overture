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

import org.overture.ide.debug.core.dbgp.DbgpRequest;
import org.overture.ide.debug.core.dbgp.IDbgpRawListener;
import org.overture.ide.debug.core.dbgp.internal.packets.DbgpNotifyPacket;
import org.overture.ide.debug.core.dbgp.internal.packets.DbgpResponsePacket;
import org.overture.ide.debug.core.dbgp.internal.packets.DbgpStreamPacket;

public interface IDbgpDebugingEngine extends IDbgpTermination {
	// Non-blocking method
	void sendCommand(DbgpRequest command) throws IOException;

	// Blocking methods
	DbgpResponsePacket getResponsePacket(int transactionId, int timeout)
			throws IOException, InterruptedException;

	DbgpNotifyPacket getNotifyPacket() throws IOException, InterruptedException;

	DbgpStreamPacket getStreamPacket() throws IOException, InterruptedException;

	// Listeners
	void addRawListener(IDbgpRawListener listener);

	void removeRawListenr(IDbgpRawListener listener);
}
