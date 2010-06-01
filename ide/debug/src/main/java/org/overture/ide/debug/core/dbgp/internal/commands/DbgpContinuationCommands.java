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
import org.overture.ide.debug.core.dbgp.commands.IDbgpContinuationCommands;
import org.overture.ide.debug.core.dbgp.exceptions.DbgpException;
import org.overture.ide.debug.core.dbgp.internal.utils.DbgpXmlEntityParser;

public class DbgpContinuationCommands extends DbgpBaseCommands implements
		IDbgpContinuationCommands {
	private static final String RUN_COMMAND = "run"; //$NON-NLS-1$

	private static final String STEP_INTO_COMMAND = "step_into"; //$NON-NLS-1$

	private static final String STEP_OVER_COMMAND = "step_over"; //$NON-NLS-1$

	private static final String STEP_OUT_COMMAND = "step_out"; //$NON-NLS-1$

	private static final String STOP_COMMAND = "stop"; //$NON-NLS-1$

	private static final String DETACH_COMMAND = "detach"; //$NON-NLS-1$

	protected IDbgpStatus execCommand(String command) throws DbgpException {
		return DbgpXmlEntityParser
				.parseStatus(communicate(createRequest(command)));
	}

	public DbgpContinuationCommands(IDbgpCommunicator communicator) {
		super(communicator);
	}

	public IDbgpStatus run() throws DbgpException {
		return execCommand(RUN_COMMAND);
	}

	public IDbgpStatus stepInto() throws DbgpException {
		return execCommand(STEP_INTO_COMMAND);
	}

	public IDbgpStatus stepOut() throws DbgpException {
		return execCommand(STEP_OUT_COMMAND);
	}

	public IDbgpStatus stepOver() throws DbgpException {
		return execCommand(STEP_OVER_COMMAND);
	}

	public IDbgpStatus stop() throws DbgpException {
		return execCommand(STOP_COMMAND);
	}

	public IDbgpStatus detach() throws DbgpException {
		return execCommand(DETACH_COMMAND);
	}
}
