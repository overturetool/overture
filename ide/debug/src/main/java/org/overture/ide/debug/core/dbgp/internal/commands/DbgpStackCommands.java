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

import java.util.Arrays;
import java.util.Comparator;

import org.overture.ide.debug.core.dbgp.DbgpBaseCommands;
import org.overture.ide.debug.core.dbgp.DbgpRequest;
import org.overture.ide.debug.core.dbgp.IDbgpCommunicator;
import org.overture.ide.debug.core.dbgp.IDbgpStackLevel;
import org.overture.ide.debug.core.dbgp.commands.IDbgpStackCommands;
import org.overture.ide.debug.core.dbgp.exceptions.DbgpDebuggingEngineException;
import org.overture.ide.debug.core.dbgp.exceptions.DbgpException;
import org.overture.ide.debug.core.dbgp.internal.utils.DbgpXmlEntityParser;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class DbgpStackCommands extends DbgpBaseCommands implements
		IDbgpStackCommands
{
	private static final String STACK_DEPTH_COMMAND = "stack_depth"; //$NON-NLS-1$

	private static final String STACK_GET_COMMAND = "stack_get"; //$NON-NLS-1$

	private static final String TAG_STACK = "stack"; //$NON-NLS-1$

	private static final String ATTR_DEPTH = "depth"; //$NON-NLS-1$

	protected int parseStackDepthResponse(Element response)
			throws DbgpDebuggingEngineException
	{
		return Integer.parseInt(response.getAttribute(ATTR_DEPTH));
	}

	protected IDbgpStackLevel[] parseStackLevels(Element response)
			throws DbgpException
	{
		NodeList nodes = response.getElementsByTagName(TAG_STACK);
		IDbgpStackLevel[] list = new IDbgpStackLevel[nodes.getLength()];
		for (int i = 0; i < nodes.getLength(); ++i)
		{
			final Element level = (Element) nodes.item(i);
			list[i] = DbgpXmlEntityParser.parseStackLevel(level);
		}
		Arrays.sort(list, STACK_LEVEL_COMPARATOR);
		return list;
	}

	private static final Comparator<Object> STACK_LEVEL_COMPARATOR = new Comparator<Object>()
	{

		public int compare(Object o1, Object o2)
		{
			final IDbgpStackLevel level1 = (IDbgpStackLevel) o1;
			final IDbgpStackLevel level2 = (IDbgpStackLevel) o2;
			return level1.getLevel() - level2.getLevel();
		}

	};

	public DbgpStackCommands(IDbgpCommunicator communicator)
	{
		super(communicator);
	}

	public int getStackDepth() throws DbgpException
	{
		return parseStackDepthResponse(communicate(createRequest(STACK_DEPTH_COMMAND)));
	}

	public IDbgpStackLevel getStackLevel(int stackDepth) throws DbgpException
	{
		DbgpRequest request = createRequest(STACK_GET_COMMAND);
		request.addOption("-d", stackDepth); //$NON-NLS-1$
		IDbgpStackLevel[] levels = parseStackLevels(communicate(request));
		return levels.length == 1 ? levels[0] : null;
	}

	public IDbgpStackLevel[] getStackLevels() throws DbgpException
	{
		return parseStackLevels(communicate(createRequest(STACK_GET_COMMAND)));
	}
}
