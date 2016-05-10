/*******************************************************************************
 *
 *	Copyright (c) 2010 Fujitsu Services Ltd.
 *
 *	Author: Nick Battle
 *
 *	This file is part of VDMJ.
 *
 *	VDMJ is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *	the Free Software Foundation, either version 3 of the License, or
 *	(at your option) any later version.
 *
 *	VDMJ is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with VDMJ.  If not, see <http://www.gnu.org/licenses/>.
 *
 ******************************************************************************/

package org.overture.interpreter.scheduler;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import org.overture.config.Settings;
import org.overture.interpreter.messages.Console;
import org.overture.interpreter.messages.rtlog.RTLogger;

public class ResourceScheduler implements Serializable
{
	private static final long serialVersionUID = 1L;

	public String name = "scheduler";
	protected List<Resource> resources = new LinkedList<>();
	protected static boolean stopping = false;
	protected static MainThread mainThread = null;

	public void init()
	{
		resources.clear();
		stopping = false;
	}

	public void reset()
	{
		for (Resource r : resources)
		{
			r.reset();
		}
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public void register(Resource resource)
	{
		resources.add(resource);
		resource.setScheduler(this);
	}

	public void unregister(Resource resource)
	{
		resources.remove(resource);
		resource.setScheduler(null);
	}

	public void start(MainThread main)
	{
		mainThread = main;
		// BUSValue.start(); // Start BUS threads first...

		boolean idle = true;
		stopping = false;

		do
		{
			long minstep = Long.MAX_VALUE;
			idle = true;

			for (Resource resource : resources)
			{
				if (resource.reschedule())
				{
					idle = false;
				} else
				{
					long d = resource.getMinimumTimestep();

					if (d < minstep)
					{
						minstep = d;
					}
				}
			}

			if (idle && minstep >= 0 && minstep < Long.MAX_VALUE)
			{
				SystemClock.advance(minstep);

				for (Resource resource : resources)
				{
					resource.advance();
				}

				idle = false;
			}
		} while (!idle && main.getRunState() != RunState.COMPLETE
				&& main.getExceptions().isEmpty());

		stopping = true;

		if (main.getRunState() != RunState.COMPLETE)
		{
			for (Resource resource : resources)
			{
				if (resource.hasActive())
				{
					Console.err.println("DEADLOCK detected");
					BasicSchedulableThread.signalAll(Signal.DEADLOCKED);

					RTLogger.dump(true);

					while (main.isAlive() && Settings.usingDBGP)
					{
						try
						{
							Thread.sleep(100);
						} catch (InterruptedException e)
						{
							// ?
						}
					}

					break;
				}
			}
		}

		// BasicSchedulableThread.signalAll(Signal.TERMINATE);
	}

	public String getStatus()
	{
		StringBuilder sb = new StringBuilder();
		String sep = "";

		for (Resource r : resources)
		{
			sb.append(sep);
			String s = r.getStatus();

			if (!s.isEmpty())
			{
				sb.append(s);
				sep = "\n";
			} else
			{
				sep = "";
			}
		}

		return sb.toString();
	}

	public static boolean isStopping()
	{
		return stopping;
	}

	public static void setException(Exception e)
	{
		mainThread.setException(e);
	}
}
