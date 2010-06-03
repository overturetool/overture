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
import org.overture.ide.debug.core.dbgp.DbgpRequest;
import org.overture.ide.debug.core.dbgp.IDbgpCommunicator;
import org.overture.ide.debug.core.dbgp.IDbgpProperty;
import org.overture.ide.debug.core.dbgp.commands.IDbgpPropertyCommands;
import org.overture.ide.debug.core.dbgp.exceptions.DbgpException;
import org.overture.ide.debug.core.dbgp.internal.utils.DbgpXmlEntityParser;
import org.overture.ide.debug.core.dbgp.internal.utils.DbgpXmlParser;
import org.overturetool.vdmj.util.Base64;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class DbgpPropertyCommands extends DbgpBaseCommands implements
		IDbgpPropertyCommands {
	private static final String PROPERTY_GET_COMMAND = "property_get"; //$NON-NLS-1$

	private static final String PROPERTY_SET_COMMAND = "property_set"; //$NON-NLS-1$

	protected IDbgpProperty parsePropertyResponse(Element response)
			throws DbgpException {
		// TODO: check length!!!
		NodeList properties = response
				.getElementsByTagName(DbgpXmlEntityParser.TAG_PROPERTY);
		return DbgpXmlEntityParser.parseProperty((Element) properties.item(0));
	}

	public DbgpPropertyCommands(IDbgpCommunicator communicator) {
		super(communicator);
	}

	protected IDbgpProperty getProperty(String name, Integer stackDepth,
			Integer contextId) throws DbgpException {
		return getProperty(null, name, stackDepth, contextId);
	}

	protected IDbgpProperty getProperty(Integer page, String name,
			Integer stackDepth, Integer contextId) throws DbgpException {
		DbgpRequest request = createRequest(PROPERTY_GET_COMMAND);
		request.addOption("-n", name); //$NON-NLS-1$

		if (stackDepth != null) {
			request.addOption("-d", stackDepth); //$NON-NLS-1$
		}

		if (contextId != null) {
			request.addOption("-c", contextId); //$NON-NLS-1$
		}

		if (page != null) {
			request.addOption("-p", page); //$NON-NLS-1$
		}
		return parsePropertyResponse(communicate(request));
	}

	public IDbgpProperty getPropertyByKey(Integer page, String name,
			Integer stackDepth, String key)
			throws DbgpException {
		DbgpRequest request = createRequest(PROPERTY_GET_COMMAND);
		request.addOption("-n", name); //$NON-NLS-1$

		if (stackDepth != null) {
			request.addOption("-d", stackDepth); //$NON-NLS-1$
		}
//
//		if (contextId != null) {
//			request.addOption("-c", contextId); //$NON-NLS-1$
//		}

		if (page != null) {
			request.addOption("-p", page); //$NON-NLS-1$
		}

		if (key != null) {
			request.addOption("-k", key); //$NON-NLS-1$
		}

		return parsePropertyResponse(communicate(request));
	}

	public IDbgpProperty getProperty(String name) throws DbgpException {
		return getProperty(name, null, null);
	}

	public IDbgpProperty getProperty(String name, int stackDepth)
			throws DbgpException {
		return getProperty(name, new Integer(stackDepth), null);
	}

	public IDbgpProperty getProperty(String name, int stackDepth, int contextId)
			throws DbgpException {
		return getProperty(name, new Integer(stackDepth),
				new Integer(contextId));
	}

	public IDbgpProperty getProperty(int page, String name, int stackDepth)
			throws DbgpException {
		return getProperty(new Integer(page), name, new Integer(stackDepth),
				null);
	}

	public boolean setProperty(IDbgpProperty property) throws DbgpException {
		DbgpRequest request = createRequest(PROPERTY_SET_COMMAND);
		request.addOption("-n", property.getName()); //$NON-NLS-1$
		request.setData(property.getValue());
		return DbgpXmlParser.parseSuccess(communicate(request));
	}

	public boolean setProperty(String name, int stackDepth, String value)
			throws DbgpException {
		DbgpRequest request = createRequest(PROPERTY_SET_COMMAND);
		request.addOption("-n", name); //$NON-NLS-1$
		request.addOption("-d", stackDepth); //$NON-NLS-1$
		request.setData(value);
		return DbgpXmlParser.parseSuccess(communicate(request));
	}
	
	public boolean setProperty(String longName, String key, String newValue) throws DbgpException{
		DbgpRequest request = createRequest(PROPERTY_SET_COMMAND);
		String encodedData = Base64.encode(newValue.getBytes()).toString();
		request.addOption("-n", longName);
		request.addOption("-k", key);
		request.addOption("-l", encodedData.length());
		request.setData(newValue);
		return DbgpXmlParser.parseSuccess(communicate(request));
	}

	

}
