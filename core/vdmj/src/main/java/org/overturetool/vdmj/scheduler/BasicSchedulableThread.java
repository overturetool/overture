/*******************************************************************************
 *
 *	Overture.
 *
 *	Author: Kenneth Lausdahl
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

package org.overturetool.vdmj.scheduler;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.commands.DebuggerReader;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.runtime.Context;

public class BasicSchedulableThread implements Serializable
{
	private static final long serialVersionUID = 1L;

	private static List<ISchedulableThread> allThreads = new LinkedList<ISchedulableThread>();

	private static InitThread initialThread = null;

	public static void add(ISchedulableThread thread)
	{
		synchronized (allThreads)
		{
			allThreads.add(thread);
		}
	}

	public static void setInitialThread(InitThread thread)
	{
		initialThread = thread;
	}

	public static void remove(ISchedulableThread thread)
	{
		synchronized (allThreads)
		{
			allThreads.remove(thread);
		}
	}

	public static String getThreadName(Thread t)
	{
		synchronized (allThreads)
		{
			for (ISchedulableThread thread : allThreads)
			{
				if (thread.getThread() == t)
				{
					return thread.getName();
				}
			}
		}

		if (initialThread != null && initialThread.getThread() == t)
		{
			return initialThread.getName();
		}

		return null;
	}

	public static ISchedulableThread getThread(Thread t)
	{
		synchronized (allThreads)
		{
			for (ISchedulableThread thread : allThreads)
			{
				if (thread.getThread() == t)
				{
					return thread;
				}
			}
		}

		if (initialThread != null && initialThread.getThread() == t)
		{
			return initialThread;
		}

		return null;
	}

	protected void handleSignal(Signal sig, Context ctxt, LexLocation location)
	{
		switch (sig)
		{
			case TERMINATE:
				throw new ThreadDeath();

			case SUSPEND:
			case DEADLOCKED:
				if (ctxt != null)
				{
					if (Settings.usingDBGP)
					{
						ctxt.threadState.dbgp.stopped(ctxt, location);
					}
					else
					{
						DebuggerReader.stopped(ctxt, location);
					}

					if (sig == Signal.DEADLOCKED)
					{
						throw new ThreadDeath();
					}
				}
				break;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.overturetool.vdmj.scheduler.ISchedulableThread#suspendOthers()
	 */
	public static void suspendOthers(ISchedulableThread thread)
	{
		synchronized (allThreads)
		{
			for (ISchedulableThread th : allThreads)
			{
				if (!th.equals(thread))
				{
					th.setSignal(Signal.SUSPEND);
				}
			}
		}
	}

	public static void signalAll(Signal sig)
	{
		synchronized (allThreads)
		{
			for (ISchedulableThread th : allThreads)
			{
				th.setSignal(sig);
			}
		}
	}

	public static void printThreads()
	{
		synchronized (allThreads)
		{
			for (ISchedulableThread t : allThreads)
			{
				System.out.println(t.getName());
			}
		}

	}

	public static synchronized long nextThreadID()
	{
		return ++threadSeqNumber;
	}

	/* For generating thread ID */
	private static long threadSeqNumber = 0;
}
