/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
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
		IDbgpSourceCommands {

	private static final String SOURCE_COMMAND = "source"; //$NON-NLS-1$

	public DbgpSourceCommands(IDbgpCommunicator communicator) {
		super(communicator);
	}

	protected String parseResponseXml(Element response) throws DbgpException {
		boolean success = DbgpXmlParser.parseSuccess(response);

		if (success) {
			return DbgpXmlParser.parseBase64Content(response);
		}

		return null;
	}

	protected String getSource(URI uri, Integer beginLine, Integer endLine)
			throws DbgpException {
		DbgpRequest request = createRequest(SOURCE_COMMAND);

		if (beginLine != null) {
			request.addOption("-b", beginLine); //$NON-NLS-1$
		}
		if (endLine != null) {
			request.addOption("-e", endLine); //$NON-NLS-1$
		}

		request.addOption("-f", uri.toString()); //$NON-NLS-1$

		return parseResponseXml(communicate(request));
	}

	public String getSource(URI uri) throws DbgpException {
		return getSource(uri, null, null);
	}

	public String getSource(URI uri, int beginLine) throws DbgpException {
		return getSource(uri, new Integer(beginLine), null);
	}

	public String getSource(URI uri, int beginLine, int endLine)
			throws DbgpException {
		return getSource(uri, new Integer(beginLine), new Integer(endLine));
	}
}
