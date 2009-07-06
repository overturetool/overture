/*******************************************************************************
 *
 *	Copyright (c) 2009 Fujitsu Services Ltd.
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

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import org.overturetool.vdmj.messages.Console;
import org.overturetool.vdmj.values.CPUValue;
import org.overturetool.vdmj.values.ObjectValue;

public class FCFSPolicy extends SchedulingPolicy
{
	private final CPUValue cpu;
	private final Queue<Thread> waiters;
	private Thread running = null;

	public FCFSPolicy(CPUValue cpu)
	{
		this.cpu = cpu;
		this.waiters = new LinkedBlockingQueue<Thread>();
	}

	@Override
	public synchronized void acquire(ObjectValue self)
	{
		if (running == null)
		{
			Console.out.println(
				"ThreadSwapIn -> id: " + Thread.currentThread().getId() +
				" objref: " + self.objectReference +
				" clnm: \"" + self.type.name.name + "\"" +
				" cpunum: " + cpu.cpuNumber +
				" overhead: " + 0 +
				" time: " + VDMThreadSet.getWallTime());

			running = Thread.currentThread();
    		notifyAll();	// For yielders
		}
		else
		{
    		// Put me on the run queue, signal all waiters (head can proceed)

    		waiters.add(Thread.currentThread());
    		notifyAll();

    		while (waiters.peek() != Thread.currentThread() ||
    			   running != null)
    		{
    			try
    			{
    				wait();
    			}
    			catch (InterruptedException e)
    			{
    				// Ignore
    			}
    		}

    		running = waiters.poll();

    		Console.out.println(
				"ThreadSwapIn -> id: " + running.getId() +
				" objref: " + self.objectReference +
				" clnm: \"" + self.type.name.name + "\"" +
				" cpunum: " + cpu.cpuNumber +
				" overhead: " + 0 +
				" time: " + VDMThreadSet.getWallTime());
		}
	}

	@Override
	public synchronized void release(ObjectValue self)
	{
		Console.out.println(
			"ThreadSwapOut -> id: " + Thread.currentThread().getId() +
			" objref: " + self.objectReference +
			" clnm: \"" + self.type.name.name + "\"" +
			" cpunum: " + cpu.cpuNumber +
			" overhead: " + 0 +
			" time: " + VDMThreadSet.getWallTime());

		running = null;
		notifyAll();
	}

	@Override
	public synchronized void yield(ObjectValue self)
	{
		// Only called when we *know* another thread can run...

		release(self);

		while (running == null)
		{
			try
			{
				wait();
			}
			catch (InterruptedException e)
			{
				// Ignore
			}
		}

		acquire(self);		// OK, someone else has set running
	}
}
