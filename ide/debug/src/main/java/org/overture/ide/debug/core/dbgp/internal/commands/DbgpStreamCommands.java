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
import org.overture.ide.debug.core.dbgp.DbgpRequest;
import org.overture.ide.debug.core.dbgp.IDbgpCommunicator;
import org.overture.ide.debug.core.dbgp.commands.IDbgpStreamCommands;
import org.overture.ide.debug.core.dbgp.exceptions.DbgpException;
import org.overture.ide.debug.core.dbgp.internal.utils.DbgpXmlParser;

public class DbgpStreamCommands extends DbgpBaseCommands implements
		IDbgpStreamCommands
{
	private static final String STDERR_COMMAND = "stderr"; //$NON-NLS-1$

	private static final String STDOUT_COMMAND = "stdout"; //$NON-NLS-1$

	protected boolean execCommand(String command, int value)
			throws DbgpException
	{
		DbgpRequest request = createRequest(command);
		request.addOption("-c", value); //$NON-NLS-1$
		return DbgpXmlParser.parseSuccess(communicate(request));
	}

	public DbgpStreamCommands(IDbgpCommunicator communicator)
	{
		super(communicator);
	}

	public boolean configureStdout(int value) throws DbgpException
	{
		return execCommand(STDOUT_COMMAND, value);
	}

	public boolean configureStderr(int value) throws DbgpException
	{
		return execCommand(STDERR_COMMAND, value);
	}
}
