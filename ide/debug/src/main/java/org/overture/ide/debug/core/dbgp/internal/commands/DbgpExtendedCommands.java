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
import org.overture.ide.debug.core.dbgp.IDbgpProperty;
import org.overture.ide.debug.core.dbgp.commands.IDbgpExtendedCommands;
import org.overture.ide.debug.core.dbgp.exceptions.DbgpException;
import org.overture.ide.debug.core.dbgp.internal.utils.DbgpXmlEntityParser;
import org.overture.ide.debug.core.dbgp.internal.utils.DbgpXmlParser;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class DbgpExtendedCommands extends DbgpBaseCommands implements
		IDbgpExtendedCommands
{

	private IDbgpProperty parseResponse(Element response)
	{
		if (DbgpXmlParser.parseSuccess(response))
		{
			NodeList list = response.getElementsByTagName(DbgpXmlEntityParser.TAG_PROPERTY);
			if(list.getLength() > 0)
			{
			return DbgpXmlEntityParser.parseProperty((Element) list.item(0));
			}
		}
		return null;
	}

	public DbgpExtendedCommands(IDbgpCommunicator communicator)
			throws DbgpException
	{
		super(communicator);
	}

	public boolean makeBreak() throws DbgpException
	{
		return DbgpXmlParser.parseSuccess(communicate(createAsyncRequest(BREAK_COMMAND)));
	}

	public boolean configureStdin(int value) throws DbgpException
	{
		DbgpRequest request = createRequest(STDIN_COMMAND);
		request.addOption("-c", value); //$NON-NLS-1$
		return DbgpXmlParser.parseSuccess(communicate(request));
	}

	public boolean sendStdin(String data) throws DbgpException
	{
		DbgpRequest request = createRequest(STDIN_COMMAND);
		request.setData(data);
		return DbgpXmlParser.parseSuccess(communicate(request));
	}

	public IDbgpProperty evaluate(String snippet) throws DbgpException
	{
		DbgpRequest request = createRequest(EVAL_COMMAND);
		request.setData(snippet);
		return parseResponse(communicate(request));
	}

	public IDbgpProperty execute(String code) throws DbgpException
	{
		DbgpRequest request = createRequest(EXEC_COMMAND);
		request.setData(code);
		return parseResponse(communicate(request));
	}
}
