/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.overture.ide.debug.core.dbgp.internal;

import org.eclipse.core.runtime.ListenerList;

public abstract class DbgpTermination implements IDbgpTermination {
	private final ListenerList listeners = new ListenerList();

	protected void fireObjectTerminated(final Exception e) {
		Thread thread = new Thread(new Runnable() {
			public void run() {
				Object[] list = listeners.getListeners();
				for (int i = 0; i < list.length; ++i) {
					((IDbgpTerminationListener) list[i]).objectTerminated(
							DbgpTermination.this, e);
				}
			}
		});

		thread.start();
	}

	public void addTerminationListener(IDbgpTerminationListener listener) {
		listeners.add(listener);

	}

	public void removeTerminationListener(IDbgpTerminationListener listener) {
		listeners.remove(listener);
	}
}
