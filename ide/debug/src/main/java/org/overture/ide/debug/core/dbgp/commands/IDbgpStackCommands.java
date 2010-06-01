/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.overture.ide.debug.core.dbgp.commands;

import org.overture.ide.debug.core.dbgp.IDbgpStackLevel;
import org.overture.ide.debug.core.dbgp.exceptions.DbgpException;

public interface IDbgpStackCommands {
	int getStackDepth() throws DbgpException;

	IDbgpStackLevel[] getStackLevels() throws DbgpException;

	IDbgpStackLevel getStackLevel(int stackDepth) throws DbgpException;
}
