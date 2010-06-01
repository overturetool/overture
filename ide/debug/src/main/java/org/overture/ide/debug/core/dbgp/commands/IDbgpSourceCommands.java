/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.overture.ide.debug.core.dbgp.commands;

import java.net.URI;

import org.overture.ide.debug.core.dbgp.exceptions.DbgpException;

public interface IDbgpSourceCommands {
	String getSource(URI uri) throws DbgpException;

	String getSource(URI uri, int beginLine) throws DbgpException;

	String getSource(URI uri, int beginLine, int endLine) throws DbgpException;
}
