/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.overture.ide.debug.core.dbgp;

import org.overture.ide.debug.core.IDebugConfigurable;
import org.overture.ide.debug.core.dbgp.commands.IDbgpCommands;
import org.overture.ide.debug.core.dbgp.commands.IDbgpOvertureCommands;
import org.overture.ide.debug.core.dbgp.internal.IDbgpTermination;
import org.overture.ide.debug.core.dbgp.internal.managers.IDbgpStreamManager;

public interface IDbgpSession extends IDbgpCommands, IDbgpTermination,
		IDebugConfigurable {
	IDbgpSessionInfo getInfo();

	IDbgpStreamManager getStreamManager();

	IDbgpNotificationManager getNotificationManager();

	// Listeners
	void addRawListener(IDbgpRawListener listener);

	void removeRawListenr(IDbgpRawListener listener);

	IDbgpCommunicator getCommunicator();
	
	IDbgpOvertureCommands getOvertureCommands();
}
