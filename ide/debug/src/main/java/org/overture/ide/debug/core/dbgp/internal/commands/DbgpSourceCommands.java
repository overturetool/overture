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

import java.net.URI;

import org.overture.ide.debug.core.dbgp.DbgpBaseCommands;
import org.overture.ide.debug.core.dbgp.DbgpRequest;
import org.overture.ide.debug.core.dbgp.IDbgpCommunicator;
import org.overture.ide.debug.core.dbgp.commands.IDbgpSourceCommands;
import org.overture.ide.debug.core.dbgp.exceptions.DbgpException;
import org.overture.ide.debug.core.dbgp.internal.utils.DbgpXmlParser;
import org.w3c.dom.Element;

public class DbgpSourceCommands extends DbgpBaseCommands implements
		IDbgpSourceCommands
{

	private static final String SOURCE_COMMAND = "source"; //$NON-NLS-1$

	public DbgpSourceCommands(IDbgpCommunicator communicator)
	{
		super(communicator);
	}

	protected String parseResponseXml(Element response) throws DbgpException
	{
		boolean success = DbgpXmlParser.parseSuccess(response);

		if (success)
		{
			return DbgpXmlParser.parseBase64Content(response);
		}

		return null;
	}

	protected String getSource(URI uri, Integer beginLine, Integer endLine)
			throws DbgpException
	{
		DbgpRequest request = createRequest(SOURCE_COMMAND);

		if (beginLine != null)
		{
			request.addOption("-b", beginLine); //$NON-NLS-1$
		}
		if (endLine != null)
		{
			request.addOption("-e", endLine); //$NON-NLS-1$
		}

		request.addOption("-f", uri.toString()); //$NON-NLS-1$

		return parseResponseXml(communicate(request));
	}

	public String getSource(URI uri) throws DbgpException
	{
		return getSource(uri, null, null);
	}

	public String getSource(URI uri, int beginLine) throws DbgpException
	{
		return getSource(uri, new Integer(beginLine), null);
	}

	public String getSource(URI uri, int beginLine, int endLine)
			throws DbgpException
	{
		return getSource(uri, new Integer(beginLine), new Integer(endLine));
	}
}
