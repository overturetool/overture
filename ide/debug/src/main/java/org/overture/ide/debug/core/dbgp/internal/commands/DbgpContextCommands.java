/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
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
		IDbgpContextCommands {
	private static final String CONTEXT_NAMES_COMMAND = "context_names"; //$NON-NLS-1$

	private static final String CONTEXT_GET = "context_get"; //$NON-NLS-1$

	private static final String TAG_CONTEXT = "context"; //$NON-NLS-1$

	private static final String ATTR_NAME = "name"; //$NON-NLS-1$

	private static final String ATTR_ID = "id"; //$NON-NLS-1$

	public DbgpContextCommands(IDbgpCommunicator communicator) {
		super(communicator);
	}

	protected Map parseContextNamesResponse(Element response)
			throws DbgpException {
		Map map = new HashMap();

		NodeList contexts = response.getElementsByTagName(TAG_CONTEXT);
		for (int i = 0; i < contexts.getLength(); ++i) {
			Element context = (Element) contexts.item(i);
			String name = context.getAttribute(ATTR_NAME);
			Integer id = new Integer(context.getAttribute(ATTR_ID));
			map.put(id, name);
		}

		return map;
	}

	protected IDbgpProperty[] parseContextPropertiesResponse(Element response)
			throws DbgpException {
		NodeList properties = response.getChildNodes();

		List list = new ArrayList();
		for (int i = 0; i < properties.getLength(); ++i) {

			Node item = properties.item(i);
			if (item instanceof Element) {
				if (item.getNodeName().equals(DbgpXmlEntityParser.TAG_PROPERTY)) {
					list.add(DbgpXmlEntityParser.parseProperty((Element) item));
				}
			}
		}

		return (IDbgpProperty[]) list.toArray(new IDbgpProperty[list.size()]);
	}

	public Map getContextNames(int stackDepth) throws DbgpException {
		DbgpRequest request = createRequest(CONTEXT_NAMES_COMMAND);
		request.addOption("-d", stackDepth); //$NON-NLS-1$
		return parseContextNamesResponse(communicate(request));
	}

	public IDbgpProperty[] getContextProperties(int stackDepth)
			throws DbgpException {
		DbgpRequest request = createRequest(CONTEXT_GET);
		request.addOption("-d", stackDepth); //$NON-NLS-1$
		return parseContextPropertiesResponse(communicate(request));
	}

	public IDbgpProperty[] getContextProperties(int stackDepth, int contextId)
			throws DbgpException {
		DbgpRequest request = createRequest(CONTEXT_GET);
		request.addOption("-d", stackDepth); //$NON-NLS-1$
		request.addOption("-c", contextId); //$NON-NLS-1$
		return parseContextPropertiesResponse(communicate(request));
	}
}
