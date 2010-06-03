/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.overture.ide.debug.core.dbgp.commands;

import org.overture.ide.debug.core.dbgp.IDbgpProperty;
import org.overture.ide.debug.core.dbgp.exceptions.DbgpException;

public interface IDbgpPropertyCommands {
	/*
	 * -d stack depth (optional, debugger engine should assume zero if not
	 * provided) -c context id (optional, retrieved by context-names, debugger
	 * engine should assume zero if not provided) -n property long name
	 * (required) -m max data size to retrieve (optional) -t data type
	 * (optional) -p data page (optional, for arrays, hashes, objects, etc.) -k
	 * property key as retrieved in a property element, optional, used for
	 * property_get of children and property_value, required if it was provided
	 * by the debugger engine. -a property address as retrieved in a property
	 * element, optional, used for property_set/value, required if it was
	 * provided by the debugger engine.
	 */

	IDbgpProperty getPropertyByKey(Integer page, String name,
			Integer stackDepth, String key) throws DbgpException;

	IDbgpProperty getProperty(String name) throws DbgpException;

	IDbgpProperty getProperty(String name, int stackDepth) throws DbgpException;

	IDbgpProperty getProperty(String name, int stackDepth, int contextId)
			throws DbgpException;

	IDbgpProperty getProperty(int page, String name, int stackDepth)
			throws DbgpException;

	boolean setProperty(IDbgpProperty property) throws DbgpException;

	boolean setProperty(String name, int stackDepth, String value)
			throws DbgpException;

	boolean setProperty(String longName, String key, String newValue)
			throws DbgpException;
}
