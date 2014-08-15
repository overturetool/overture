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
package org.overture.ide.debug.core.dbgp;

import org.overture.ide.debug.core.dbgp.exceptions.DbgpException;
import org.overture.ide.debug.core.dbgp.internal.DbgpTransactionManager;
import org.w3c.dom.Element;

public class DbgpBaseCommands
{

	public static final String ID_OPTION = "-i"; //$NON-NLS-1$

	private final IDbgpCommunicator communicator;

	public DbgpBaseCommands(IDbgpCommunicator communicator)
	{
		this.communicator = communicator;
	}

	public static DbgpRequest createRequest(String command)
	{
		DbgpRequest request = new DbgpRequest(command);
		request.addOption(ID_OPTION, generateRequestId());
		return request;
	}

	public static DbgpRequest createAsyncRequest(String command)
	{
		DbgpRequest request = new DbgpRequest(command, true);
		request.addOption(ID_OPTION, generateRequestId());
		return request;
	}

	private static int generateRequestId()
	{
		return DbgpTransactionManager.getInstance().generateId();
	}

	protected Element communicate(DbgpRequest request) throws DbgpException
	{
		return communicator.communicate(request);
	}

	protected void send(DbgpRequest request) throws DbgpException
	{
		communicator.send(request);
	}
}
