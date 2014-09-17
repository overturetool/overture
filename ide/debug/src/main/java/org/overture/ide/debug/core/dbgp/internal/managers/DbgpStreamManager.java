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
import org.overture.ide.debug.core.dbgp.IDbgpStreamListener;
import org.overture.ide.debug.core.dbgp.internal.DbgpWorkingThread;
import org.overture.ide.debug.core.dbgp.internal.IDbgpDebugingEngine;
import org.overture.ide.debug.core.dbgp.internal.packets.DbgpStreamPacket;

public class DbgpStreamManager extends DbgpWorkingThread implements
		IDbgpStreamManager
{
	private final ListenerList listeners = new ListenerList();

	private final IDbgpDebugingEngine engine;

	protected void fireStderrReceived(String data)
	{
		if (data == null || data.length() == 0)
		{
			return;
		}
		Object[] list = listeners.getListeners();
		for (int i = 0; i < list.length; ++i)
		{
			((IDbgpStreamListener) list[i]).stderrReceived(data);
		}
	}

	protected void fireStdoutReceived(String data)
	{
		if (data == null || data.length() == 0)
		{
			return;
		}
		Object[] list = listeners.getListeners();
		for (int i = 0; i < list.length; ++i)
		{
			((IDbgpStreamListener) list[i]).stdoutReceived(data);
		}
	}

	protected void workingCycle() throws Exception
	{
		try
		{
			while (!Thread.interrupted())
			{
				final DbgpStreamPacket packet = engine.getStreamPacket();

				if (packet.isStderr())
				{
					fireStderrReceived(packet.getTextContent());
				} else if (packet.isStdout())
				{
					fireStdoutReceived(packet.getTextContent());
				}
			}
		} catch (InterruptedException e)
		{
			// OK, interrupted
		}
	}

	public DbgpStreamManager(IDbgpDebugingEngine engine, String name)
	{
		super(name);

		if (engine == null)
		{
			throw new IllegalArgumentException();
		}

		this.engine = engine;
	}

	public void addListener(IDbgpStreamListener listener)
	{
		listeners.add(listener);
	}

	public void removeListener(IDbgpStreamListener listener)
	{
		listeners.remove(listener);
	}

}
