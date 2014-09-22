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
package org.overture.ide.debug.core.dbgp.internal;

import java.io.IOException;

import org.overture.ide.debug.core.dbgp.DbgpRequest;
import org.overture.ide.debug.core.dbgp.IDbgpRawListener;
import org.overture.ide.debug.core.dbgp.internal.packets.DbgpNotifyPacket;
import org.overture.ide.debug.core.dbgp.internal.packets.DbgpResponsePacket;
import org.overture.ide.debug.core.dbgp.internal.packets.DbgpStreamPacket;

public interface IDbgpDebugingEngine extends IDbgpTermination
{
	// Non-blocking method
	void sendCommand(DbgpRequest command) throws IOException;

	// Blocking methods
	DbgpResponsePacket getResponsePacket(int transactionId, int timeout)
			throws IOException, InterruptedException;

	DbgpNotifyPacket getNotifyPacket() throws IOException, InterruptedException;

	DbgpStreamPacket getStreamPacket() throws IOException, InterruptedException;

	// Listeners
	void addRawListener(IDbgpRawListener listener);

	void removeRawListenr(IDbgpRawListener listener);
}
