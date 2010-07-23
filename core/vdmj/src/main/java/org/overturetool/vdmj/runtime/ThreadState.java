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

import java.io.Serializable;

import org.overturetool.vdmj.debug.DBGPReader;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.scheduler.BasicSchedulableThread;
import org.overturetool.vdmj.scheduler.ISchedulableThread;
import org.overturetool.vdmj.scheduler.InitThread;
import org.overturetool.vdmj.values.CPUValue;

/**
 * A class to hold some runtime information for each thread.
 */

public class ThreadState implements Serializable
{
    private static final long serialVersionUID = 1L;
	public final long threadId;
	public final DBGPReader dbgp;
	public final CPUValue CPU;

	private boolean atomic = false;	// Don't reschedule

	public LexLocation stepline;	// Breakpoint stepping values
	public RootContext nextctxt;
	public Context outctxt;


	public ThreadState(DBGPReader dbgp, CPUValue cpu)
	{
		this.dbgp = dbgp;
		this.threadId = BasicSchedulableThread.getThread(Thread.currentThread()).getId();
		this.CPU = cpu;
		init();
	}

	public void init()
	{
		setBreaks(null, null, null);
	}

	public synchronized void setBreaks(
		LexLocation stepline, RootContext nextctxt, Context outctxt)
	{
		this.stepline = stepline;
		this.nextctxt = nextctxt;
		this.outctxt = outctxt;
	}

	public synchronized boolean isStepping()
	{
		return stepline != null;
	}

	public void reschedule(Context ctxt, LexLocation location)
	{
		if (!atomic)
		{
			// Initialization doesn't occur from SchedulableThreads

			ISchedulableThread s = BasicSchedulableThread.getThread(Thread.currentThread());

			if (s !=null && !(s instanceof InitThread))
			{
				s.step(ctxt, location);
			}
		}
	}

	public synchronized void setAtomic(boolean atomic)
	{
		this.atomic = atomic;
	}
}
