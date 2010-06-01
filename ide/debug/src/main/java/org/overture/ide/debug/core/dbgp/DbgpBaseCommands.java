/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.overture.ide.debug.core.dbgp;

import org.overture.ide.debug.core.dbgp.exceptions.DbgpException;
import org.overture.ide.debug.core.dbgp.internal.DbgpTransactionManager;
import org.w3c.dom.Element;

public class DbgpBaseCommands {

	public static final String ID_OPTION = "-i"; //$NON-NLS-1$

	private final IDbgpCommunicator communicator;

	public DbgpBaseCommands(IDbgpCommunicator communicator) {
		this.communicator = communicator;
	}

	public static DbgpRequest createRequest(String command) {
		DbgpRequest request = new DbgpRequest(command);
		request.addOption(ID_OPTION, generateRequestId());
		return request;
	}

	public static DbgpRequest createAsyncRequest(String command) {
		DbgpRequest request = new DbgpRequest(command, true);
		request.addOption(ID_OPTION, generateRequestId());
		return request;
	}

	private static int generateRequestId() {
		return DbgpTransactionManager.getInstance().generateId();
	}

	protected Element communicate(DbgpRequest request) throws DbgpException {
		return communicator.communicate(request);
	}

	protected void send(DbgpRequest request) throws DbgpException {
		communicator.send(request);
	}
}
