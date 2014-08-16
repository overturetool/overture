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
package org.overture.ide.debug.core.dbgp.internal.managers;

import org.eclipse.core.runtime.ListenerList;
import org.overture.ide.debug.core.dbgp.IDbgpNotification;
import org.overture.ide.debug.core.dbgp.IDbgpNotificationListener;
import org.overture.ide.debug.core.dbgp.IDbgpNotificationManager;
import org.overture.ide.debug.core.dbgp.internal.DbgpNotification;
import org.overture.ide.debug.core.dbgp.internal.DbgpWorkingThread;
import org.overture.ide.debug.core.dbgp.internal.IDbgpDebugingEngine;
import org.overture.ide.debug.core.dbgp.internal.packets.DbgpNotifyPacket;

public class DbgpNotificationManager extends DbgpWorkingThread implements
		IDbgpNotificationManager
{
	private final ListenerList listeners = new ListenerList();

	private final IDbgpDebugingEngine engine;

	protected void fireDbgpNotify(IDbgpNotification notification)
	{
		Object[] list = listeners.getListeners();
		for (int i = 0; i < list.length; ++i)
		{
			((IDbgpNotificationListener) list[i]).dbgpNotify(notification);
		}
	}

	protected void workingCycle() throws Exception
	{
		try
		{
			while (!Thread.interrupted())
			{
				DbgpNotifyPacket packet = engine.getNotifyPacket();

				fireDbgpNotify(new DbgpNotification(packet.getName(), packet.getContent()));
			}
		} catch (InterruptedException e)
		{
			// OK, interrupted
		}
	}

	public DbgpNotificationManager(IDbgpDebugingEngine engine)
	{
		super("DBGP - Notification Manager"); //$NON-NLS-1$
		if (engine == null)
		{
			throw new IllegalArgumentException();
		}

		this.engine = engine;
	}

	public void addNotificationListener(IDbgpNotificationListener listener)
	{
		listeners.add(listener);
	}

	public void removeNotificationListener(IDbgpNotificationListener listener)
	{
		listeners.remove(listener);
	}
}
