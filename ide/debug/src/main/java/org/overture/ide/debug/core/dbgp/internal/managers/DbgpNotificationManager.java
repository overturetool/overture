/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
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
		IDbgpNotificationManager {
	private final ListenerList listeners = new ListenerList();

	private final IDbgpDebugingEngine engine;

	protected void fireDbgpNotify(IDbgpNotification notification) {
		Object[] list = listeners.getListeners();
		for (int i = 0; i < list.length; ++i) {
			((IDbgpNotificationListener) list[i]).dbgpNotify(notification);
		}
	}

	protected void workingCycle() throws Exception {
		try {
			while (!Thread.interrupted()) {
				DbgpNotifyPacket packet = engine.getNotifyPacket();

				fireDbgpNotify(new DbgpNotification(packet.getName(), packet
						.getContent()));
			}
		} catch (InterruptedException e) {
			// OK, interrupted
		}
	}

	public DbgpNotificationManager(IDbgpDebugingEngine engine) {
		super("DBGP - Notification Manager"); //$NON-NLS-1$
		if (engine == null) {
			throw new IllegalArgumentException();
		}

		this.engine = engine;
	}

	public void addNotificationListener(IDbgpNotificationListener listener) {
		listeners.add(listener);
	}

	public void removeNotificationListener(IDbgpNotificationListener listener) {
		listeners.remove(listener);
	}
}
