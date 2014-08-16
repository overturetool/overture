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

import org.eclipse.core.runtime.ListenerList;

public abstract class DbgpTermination implements IDbgpTermination
{
	private final ListenerList listeners = new ListenerList();

	protected void fireObjectTerminated(final Exception e)
	{
		Thread thread = new Thread(new Runnable()
		{
			public void run()
			{
				Object[] list = listeners.getListeners();
				for (int i = 0; i < list.length; ++i)
				{
					((IDbgpTerminationListener) list[i]).objectTerminated(DbgpTermination.this, e);
				}
			}
		});

		thread.start();
	}

	public void addTerminationListener(IDbgpTerminationListener listener)
	{
		listeners.add(listener);

	}

	public void removeTerminationListener(IDbgpTerminationListener listener)
	{
		listeners.remove(listener);
	}

}
