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

import java.net.URI;

import org.overture.ide.debug.core.dbgp.IDbgpSpawnpoint;
import org.overture.ide.debug.core.dbgp.exceptions.DbgpException;

public interface IDbgpSpawnpointCommands
{

	/**
	 * Sets the spawn point at the specified location. Returns the id of created spawn point.
	 * 
	 * @param uri
	 * @param lineNumber
	 * @param enabled
	 * @return
	 * @throws DbgpException
	 */
	IDbgpSpawnpoint setSpawnpoint(URI uri, int lineNumber, boolean enabled)
			throws DbgpException;

	/**
	 * Retrieves the information about the specified spawn point
	 * 
	 * @param spawnpointId
	 * @return
	 * @throws DbgpException
	 */
	IDbgpSpawnpoint getSpawnpoint(String spawnpointId) throws DbgpException;

	/**
	 * Updates the specified spawn point
	 * 
	 * @param spawnpointId
	 * @param enabled
	 * @return
	 * @throws DbgpException
	 */
	void updateSpawnpoint(String spawnpointId, boolean enabled)
			throws DbgpException;

	/**
	 * Removes the specified spawn point
	 * 
	 * @param spawnpointId
	 * @throws DbgpException
	 */
	void removeSpawnpoint(String spawnpointId) throws DbgpException;

	/**
	 * Retrieves all spawn points. If there are no spawn points empty array is returned.
	 * 
	 * @return
	 * @throws DbgpException
	 */
	IDbgpSpawnpoint[] listSpawnpoints() throws DbgpException;

}
