/*******************************************************************************
 * Copyright (c) 2008 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.overture.ide.debug.core.dbgp.commands;

import java.net.URI;

import org.overture.ide.debug.core.dbgp.IDbgpSpawnpoint;
import org.overture.ide.debug.core.dbgp.exceptions.DbgpException;

public interface IDbgpSpawnpointCommands {

	/**
	 * Sets the spawn point at the specified location. Returns the id of created
	 * spawn point.
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
	 * Retrieves all spawn points. If there are no spawn points empty array is
	 * returned.
	 * 
	 * @return
	 * @throws DbgpException
	 */
	IDbgpSpawnpoint[] listSpawnpoints() throws DbgpException;

}
