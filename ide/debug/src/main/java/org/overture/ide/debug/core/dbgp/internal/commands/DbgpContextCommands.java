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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.overture.ide.debug.core.dbgp.DbgpBaseCommands;
import org.overture.ide.debug.core.dbgp.DbgpRequest;
import org.overture.ide.debug.core.dbgp.IDbgpCommunicator;
import org.overture.ide.debug.core.dbgp.IDbgpProperty;
import org.overture.ide.debug.core.dbgp.commands.IDbgpContextCommands;
import org.overture.ide.debug.core.dbgp.exceptions.DbgpException;
import org.overture.ide.debug.core.dbgp.internal.utils.DbgpXmlEntityParser;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DbgpContextCommands extends DbgpBaseCommands implements
		IDbgpContextCommands
{
	private static final String CONTEXT_NAMES_COMMAND = "context_names"; //$NON-NLS-1$

	private static final String CONTEXT_GET = "context_get"; //$NON-NLS-1$

	private static final String TAG_CONTEXT = "context"; //$NON-NLS-1$

	private static final String ATTR_NAME = "name"; //$NON-NLS-1$

	private static final String ATTR_ID = "id"; //$NON-NLS-1$

	public DbgpContextCommands(IDbgpCommunicator communicator)
	{
		super(communicator);
	}

	protected Map<Integer, String> parseContextNamesResponse(Element response)
			throws DbgpException
	{
		Map<Integer, String> map = new HashMap<Integer, String>();

		NodeList contexts = response.getElementsByTagName(TAG_CONTEXT);
		for (int i = 0; i < contexts.getLength(); ++i)
		{
			Element context = (Element) contexts.item(i);
			String name = context.getAttribute(ATTR_NAME);
			Integer id = new Integer(context.getAttribute(ATTR_ID));
			map.put(id, name);
		}

		return map;
	}

	protected IDbgpProperty[] parseContextPropertiesResponse(Element response)
			throws DbgpException
	{
		NodeList properties = response.getChildNodes();

		List<IDbgpProperty> list = new ArrayList<IDbgpProperty>();
		for (int i = 0; i < properties.getLength(); ++i)
		{

			Node item = properties.item(i);
			if (item instanceof Element)
			{
				if (item.getNodeName().equals(DbgpXmlEntityParser.TAG_PROPERTY))
				{
					list.add(DbgpXmlEntityParser.parseProperty((Element) item));
				}
			}
		}

		return (IDbgpProperty[]) list.toArray(new IDbgpProperty[list.size()]);
	}

	public Map<Integer, String> getContextNames(int stackDepth)
			throws DbgpException
	{
		DbgpRequest request = createRequest(CONTEXT_NAMES_COMMAND);
		request.addOption("-d", stackDepth); //$NON-NLS-1$
		return parseContextNamesResponse(communicate(request));
	}

	public IDbgpProperty[] getContextProperties(int stackDepth)
			throws DbgpException
	{
		DbgpRequest request = createRequest(CONTEXT_GET);
		request.addOption("-d", stackDepth); //$NON-NLS-1$
		return parseContextPropertiesResponse(communicate(request));
	}

	public IDbgpProperty[] getContextProperties(int stackDepth, int contextId)
			throws DbgpException
	{
		DbgpRequest request = createRequest(CONTEXT_GET);
		request.addOption("-d", stackDepth); //$NON-NLS-1$
		request.addOption("-c", contextId); //$NON-NLS-1$
		return parseContextPropertiesResponse(communicate(request));
	}
}
