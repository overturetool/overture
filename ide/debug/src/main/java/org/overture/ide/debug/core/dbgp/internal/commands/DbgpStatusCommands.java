/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.overture.ide.debug.core.dbgp.internal.commands;

import org.overture.ide.debug.core.dbgp.DbgpBaseCommands;
import org.overture.ide.debug.core.dbgp.IDbgpCommunicator;
import org.overture.ide.debug.core.dbgp.IDbgpStatus;
import org.overture.ide.debug.core.dbgp.commands.IDbgpStatusCommands;
import org.overture.ide.debug.core.dbgp.exceptions.DbgpException;
import org.overture.ide.debug.core.dbgp.internal.utils.DbgpXmlEntityParser;

public class DbgpStatusCommands extends DbgpBaseCommands implements
		IDbgpStatusCommands {
	private static final String STATUS_COMMAND = "status"; //$NON-NLS-1$

	public DbgpStatusCommands(IDbgpCommunicator communicator) {
		super(communicator);
	}

	public IDbgpStatus getStatus() throws DbgpException {
		return DbgpXmlEntityParser
				.parseStatus(communicate(createAsyncRequest(STATUS_COMMAND)));
	}
}
