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

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import org.overturetool.vdmj.messages.RTLogger;
import org.overturetool.vdmj.values.CPUValue;
import org.overturetool.vdmj.values.ObjectValue;

public class FCFSPolicy extends SchedulingPolicy
{
	private final CPUValue cpu;
	private final Queue<Thread> waiters;
	private final Map<Thread, ObjectValue> objects;
	private final Map<Thread, RunState> state;

	private Thread bestThread = null;
	private ObjectValue bestObject = null;
	private long switches = 0;

	public FCFSPolicy(CPUValue cpu)
	{
		this.cpu = cpu;
		this.waiters = new LinkedBlockingQueue<Thread>();
		this.objects = new HashMap<Thread, ObjectValue>();
		this.state = new HashMap<Thread, RunState>();
		this.switches = 0;
	}

	@Override
	public synchronized void addThread(Thread thread, ObjectValue object)
	{
		waiters.add(thread);
		objects.put(thread, object);
		state.put(thread, RunState.CREATED);
	}

	@Override
	public synchronized void removeThread(Thread thread)
	{
		waiters.remove(thread);
		objects.remove(thread);
		state.remove(thread);

		if (bestThread == thread)
		{
			bestThread = null;
			bestObject = null;
		}
	}

	@Override
	public synchronized ObjectValue getObject()
	{
		return bestObject;
	}

	@Override
	public synchronized Thread getThread()
	{
		return bestThread;
	}

	@Override
	public synchronized void setState(Thread thread, RunState newstate)
	{
		state.put(thread, newstate);
	}

	@Override
	public synchronized boolean reschedule()
	{
		if (!waiters.isEmpty())
		{
			Thread original = bestThread;
    		ObjectValue object = bestObject;

    		do
    		{
    			bestThread = waiters.poll();
    			waiters.add(bestThread);
    		}
    		while (state.get(bestThread) != RunState.RUNNABLE);

    		if (bestThread != original)
    		{
    			if (original != null)
    			{
    				RTLogger.log(
    					"ThreadSwapOut -> id: " + original.getId() +
    					" objref: " + object.objectReference +
    					" clnm: \"" + object.type.name.name + "\"" +
    					" cpunum: " + cpu.cpuNumber +
    					" overhead: " + 0 +
    					" time: " + VDMThreadSet.getWallTime());
    			}

    			bestObject = objects.get(bestThread);

    			RTLogger.log(
    				"ThreadSwapIn -> id: " + bestThread.getId() +
    				" objref: " + bestObject.objectReference +
    				" clnm: \"" + bestObject.type.name.name + "\"" +
    				" cpunum: " + cpu.cpuNumber +
    				" overhead: " + 0 +
    				" time: " + VDMThreadSet.getWallTime());

    			switches++;
    			return true;
    		}
    		else
    		{
    			return false;
    		}
		}
		else
		{
			return false;
		}
	}
}
