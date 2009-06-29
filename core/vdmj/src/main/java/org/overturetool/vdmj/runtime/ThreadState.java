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

import org.overturetool.vdmj.debug.DBGPReader;

/**
 * A class to hold some runtime information for each VDM thread.
 */

public class ThreadState
{
	public final long threadId;

	public InterruptAction action;
	public int stepline;
	public RootContext nextctxt;
	public Context outctxt;
	public DBGPReader dbgp;
	private long timestep;

	public ThreadState(DBGPReader dbgp)
	{
		this.dbgp = dbgp;
		this.threadId = Thread.currentThread().getId();
		init();
	}

	public void init()
	{
		this.action = InterruptAction.RUNNING;
		this.setTimestep(0);
		set(0, null, null);
	}

	public synchronized void set(
		int stepline, RootContext nextctxt, Context outctxt)
	{
		this.stepline = stepline;
		this.nextctxt = nextctxt;
		this.outctxt = outctxt;
	}

	public synchronized void setTimestep(long timestep)
	{
		this.timestep = timestep;
	}

	public synchronized long getTimestep()
	{
		return timestep;
	}
}
