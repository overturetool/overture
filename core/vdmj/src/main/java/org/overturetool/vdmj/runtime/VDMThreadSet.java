/*******************************************************************************
 *
 *	Copyright (C) 2008 Fujitsu Services Ltd.
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

package org.overturetool.vdmj.runtime;

import java.util.HashSet;
import java.util.Set;

/**
 * A class containing all active VDM threads.
 */

public class VDMThreadSet
{
	private static Set<VDMThread> threads = null;
	private static int debugStopped = 0;

	private static int timeSteppers = 0;
	private static long minimumStep = 0;
	private static long wallTime = 0;

	public static synchronized void init()
	{
		threads = new HashSet<VDMThread>();
		debugStopped = 0;

		timeSteppers = 0;
		minimumStep = 0;
		wallTime = 0;
	}

	public static synchronized void add(VDMThread th)
	{
		threads.add(th);
	}

	public static synchronized void remove(VDMThread th)
	{
		threads.remove(th);
	}

	public static synchronized void blockAll()
	{
		for (VDMThread th: threads)
		{
			th.block();
		}
	}

	public static synchronized void unblockAll()
	{
		for (VDMThread th: threads)
		{
			th.unblock();
		}
	}

	public static synchronized void abortAll()
	{
		for (VDMThread th: threads)
		{
			th.abort();
		}
	}

	public static synchronized boolean abortAll(int secs)
	{
		abortAll();

		for (int loop=0; loop < secs; loop++)
		{
			if (threads.isEmpty())
			{
				return true;
			}

			try { Thread.sleep(1000); } catch (Exception e) { /**/ }
		}

		return threads.isEmpty();
	}

	public static synchronized String getStatus()
	{
		StringBuilder sb = new StringBuilder();

		for (VDMThread th: threads)
		{
			sb.append(th.toString());
			sb.append("\n");
		}

		return sb.toString();
	}

	public static synchronized String dump()
	{
		StringBuilder sb = new StringBuilder();

		for (VDMThread th: threads)
		{
			sb.append(th.title);
			sb.append("\n");
		}

		return sb.toString();
	}

	public static synchronized boolean isDebugStopped()
	{
		return debugStopped > 0;
	}

	public static synchronized void incDebugStopped()
	{
		debugStopped++;
	}

	public static synchronized void decDebugStopped()
	{
		debugStopped--;
	}

	public static synchronized long getWallTime()
	{
		return wallTime;
	}

	public static synchronized void timeStep(long interval)
	{
		long endTime = wallTime + interval;

		while (wallTime < endTime)
		{
    		timeSteppers++;

    		if (timeSteppers == threads.size() + 1)	// Plus main thread
    		{
    			// Calculate the minimum time step, then wake everyone
    			minimumStep = Interpreter.mainContext.threadState.getTimestep();

    			for (VDMThread th: threads)
    			{
    				if (th.ctxt.threadState.getTimestep() < minimumStep)
    				{
    					minimumStep = th.ctxt.threadState.getTimestep();
    				}
    			}

    			wallTime += minimumStep;
    			notifyWaiters();
    		}
    		else
    		{
    			waitNotify();	// Wait until all threads ready to step
    		}

    		timeSteppers--;

    		// Wait until everyone is ready to continue before letting
    		// anyone continue - otherwise we'll start the next step before
    		// everyone has checked their duration.

    		if (timeSteppers == 0)
    		{
    			notifyWaiters();
    		}
    		else
    		{
    			waitNotify();	// Wait until everyone can continue
    		}
		}
	}

	private static void notifyWaiters()
	{
		VDMThreadSet.class.notifyAll();
	}

	private static void waitNotify()
	{
		while (true)
		{
			try
			{
				VDMThreadSet.class.wait();
				break;
			}
			catch (InterruptedException e)
			{
				// Ignore
			}
		}
	}
}
