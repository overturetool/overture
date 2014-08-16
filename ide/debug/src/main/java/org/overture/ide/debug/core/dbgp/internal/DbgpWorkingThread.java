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

import org.overture.ide.debug.core.VdmDebugPlugin;

public abstract class DbgpWorkingThread extends DbgpTermination
{
	private Thread thread;
	private final String name;

	public DbgpWorkingThread(String name)
	{
		this.name = name;
	}

	public void start()
	{
		if (thread == null || !thread.isAlive())
		{
			thread = new Thread(new Runnable()
			{
				public void run()
				{
					try
					{
						workingCycle();
					} catch (Exception e)
					{
						if (isLoggable(e))
						{
							VdmDebugPlugin.logError("workingCycleError", e);
						}
						fireObjectTerminated(e);
						return;
					}

					fireObjectTerminated(null);
				}
			}, name);

			thread.start();
		} else
		{
			throw new IllegalStateException("threadAlreadyStarted");
		}
	}

	public void requestTermination()
	{
		if (thread != null && thread.isAlive())
		{
			thread.interrupt();
		}
	}

	public void waitTerminated() throws InterruptedException
	{
		if (thread != null)
		{
			thread.join();
		}
	}

	/**
	 * Tests if this exception should be logged. The rationale here is IOExceptions/SocketExceptions occurs always after
	 * socket is closed, so there is no point to log it.
	 * 
	 * @param e
	 * @return
	 */
	protected boolean isLoggable(Exception e)
	{
		return !(e instanceof IOException);
	}

	// Working cycle
	protected abstract void workingCycle() throws Exception;
}
