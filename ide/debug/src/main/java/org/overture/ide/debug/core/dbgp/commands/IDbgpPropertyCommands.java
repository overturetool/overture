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
package org.overture.ide.debug.core.dbgp.commands;

import org.overture.ide.debug.core.dbgp.IDbgpProperty;
import org.overture.ide.debug.core.dbgp.exceptions.DbgpException;

public interface IDbgpPropertyCommands
{
	/*
	 * -d stack depth (optional, debugger engine should assume zero if not provided) -c context id (optional, retrieved
	 * by context-names, debugger engine should assume zero if not provided) -n property long name (required) -m max
	 * data size to retrieve (optional) -t data type (optional) -p data page (optional, for arrays, hashes, objects,
	 * etc.) -k property key as retrieved in a property element, optional, used for property_get of children and
	 * property_value, required if it was provided by the debugger engine. -a property address as retrieved in a
	 * property element, optional, used for property_set/value, required if it was provided by the debugger engine.
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
