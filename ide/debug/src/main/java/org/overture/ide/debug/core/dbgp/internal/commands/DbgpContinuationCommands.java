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
package org.overture.ide.debug.core.dbgp.internal.commands;

import org.overture.ide.debug.core.dbgp.DbgpBaseCommands;
import org.overture.ide.debug.core.dbgp.IDbgpCommunicator;
import org.overture.ide.debug.core.dbgp.IDbgpStatus;
import org.overture.ide.debug.core.dbgp.commands.IDbgpContinuationCommands;
import org.overture.ide.debug.core.dbgp.exceptions.DbgpException;
import org.overture.ide.debug.core.dbgp.internal.utils.DbgpXmlEntityParser;

public class DbgpContinuationCommands extends DbgpBaseCommands implements
		IDbgpContinuationCommands
{
	private static final String RUN_COMMAND = "run"; //$NON-NLS-1$

	private static final String STEP_INTO_COMMAND = "step_into"; //$NON-NLS-1$

	private static final String STEP_OVER_COMMAND = "step_over"; //$NON-NLS-1$

	private static final String STEP_OUT_COMMAND = "step_out"; //$NON-NLS-1$

	private static final String STOP_COMMAND = "stop"; //$NON-NLS-1$

	private static final String DETACH_COMMAND = "detach"; //$NON-NLS-1$

	protected IDbgpStatus execCommand(String command) throws DbgpException
	{
		return DbgpXmlEntityParser.parseStatus(communicate(createRequest(command)));
	}

	public DbgpContinuationCommands(IDbgpCommunicator communicator)
	{
		super(communicator);
	}

	public IDbgpStatus run() throws DbgpException
	{
		return execCommand(RUN_COMMAND);
	}

	public IDbgpStatus stepInto() throws DbgpException
	{
		return execCommand(STEP_INTO_COMMAND);
	}

	public IDbgpStatus stepOut() throws DbgpException
	{
		return execCommand(STEP_OUT_COMMAND);
	}

	public IDbgpStatus stepOver() throws DbgpException
	{
		return execCommand(STEP_OVER_COMMAND);
	}

	public IDbgpStatus stop() throws DbgpException
	{
		return execCommand(STOP_COMMAND);
	}

	public IDbgpStatus detach() throws DbgpException
	{
		return execCommand(DETACH_COMMAND);
	}
}
