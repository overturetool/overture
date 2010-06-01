/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.overture.ide.debug.core.dbgp.internal.packets;

import java.io.IOException;
import java.io.OutputStream;

import org.overture.ide.debug.core.dbgp.DbgpRequest;

public class DbgpPacketSender {
	private final Object lock = new Object();

	private final OutputStream output;

	private IDbgpRawLogger logger;

	public DbgpPacketSender(OutputStream output) {
		if (output == null) {
			throw new IllegalArgumentException();
		}

		this.output = output;
	}

	public void setLogger(IDbgpRawLogger logger) {
		this.logger = logger;
	}

	public void sendCommand(DbgpRequest command) throws IOException {
		if (logger != null) {
			logger.log(command);
		}

		synchronized (lock) {
			command.writeTo(output);
			output.write(0);
			output.flush();
		}
	}
}
