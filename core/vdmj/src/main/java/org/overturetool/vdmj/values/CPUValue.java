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

package org.overturetool.vdmj.values;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.messages.RTLogger;
import org.overturetool.vdmj.runtime.CPUPolicy;
import org.overturetool.vdmj.runtime.RunState;
import org.overturetool.vdmj.runtime.SchedulingPolicy;
import org.overturetool.vdmj.runtime.SystemClock;
import org.overturetool.vdmj.types.ClassType;
import org.overturetool.vdmj.types.Type;

public class CPUValue extends ObjectValue
{
	private static final long serialVersionUID = 1L;
	public static int nextCPU = 1;
	public static List<CPUValue> allCPUs;

	public final int cpuNumber;
	public final SchedulingPolicy policy;
	public final double cyclesPerSec;
	public final List<ObjectValue> deployed;
	public final Map<Thread, ObjectValue> objects;
	public Map<Thread, Long> durations;

	public String name;
	public Thread runningThread;
	public int switches;
	private long minTimeStep;

	public static void init()
	{
		nextCPU = 1;
		allCPUs = new Vector<CPUValue>();
	}

	public CPUValue(Type classtype, NameValuePairMap map, ValueList argvals)
	{
		super((ClassType)classtype, map, new Vector<ObjectValue>());

		this.cpuNumber = nextCPU++;

		QuoteValue parg = (QuoteValue)argvals.get(0);
		CPUPolicy cpup = CPUPolicy.valueOf(parg.value.toUpperCase());
		policy = cpup.factory();

		RealValue sarg = (RealValue)argvals.get(1);
		this.cyclesPerSec = sarg.value;

		this.deployed = new Vector<ObjectValue>();
		this.objects = new HashMap<Thread, ObjectValue>();
		this.durations = new HashMap<Thread, Long>();
		this.switches = 0;
		this.minTimeStep = Long.MAX_VALUE;

		allCPUs.add(this);
	}

	public CPUValue(
		int number, Type classtype, NameValuePairMap map, ValueList argvals)
	{
		super((ClassType)classtype, map, new Vector<ObjectValue>());

		this.cpuNumber = number;

		QuoteValue parg = (QuoteValue)argvals.get(0);
		CPUPolicy cpup = CPUPolicy.valueOf(parg.value.toUpperCase());
		policy = cpup.factory();

		RealValue sarg = (RealValue)argvals.get(1);
		this.cyclesPerSec = sarg.value;

		this.deployed = new Vector<ObjectValue>();
		this.objects = new HashMap<Thread, ObjectValue>();
		this.durations = new HashMap<Thread, Long>();
		this.minTimeStep = Long.MAX_VALUE;
		this.switches = 0;

		allCPUs.add(this);
	}

	public void addDeployed(ObjectValue obj)
	{
		deployed.add(obj);
	}

	public boolean setPriority(String opname, long priority)
	{
		boolean found = false;

		for (ObjectValue obj: deployed)
		{
			for (LexNameToken m: obj.members.keySet())
			{
				if (m.getExplicit(true).getName().equals(opname))
				{
					OperationValue op = (OperationValue)obj.members.get(m);
					op.setPriority(priority);
					found = true;
				}
			}
		}

		return found;
	}

	public long getDuration(long cycles)
	{
		return (long)(cycles/cyclesPerSec * 1000);		// millisecs
	}

	public void setName(String name)
	{
		this.name = name;
	}

	@Override
	public String toString()
	{
		return name;
	}

	public String declString(String sysname, boolean explicit)
	{
		return
			"CPUdecl -> id: " + cpuNumber +
			" expl: " + explicit +
			" sys: \"" + sysname + "\"" +
			" name: \"" + name + "\"";
	}

	public synchronized void addThread(Thread thread, ObjectValue object)
	{
		policy.addThread(thread);
		objects.put(thread, object);
		durations.put(thread, -1L);

		RTLogger.log(
			"ThreadCreate -> id: " + thread.getId() +
			" period: false " +
			" objref: " + object.objectReference +
			" clnm: \"" + object.type + "\"" +
			" cpunm: " + cpuNumber +
			" time: " + SystemClock.getWallTime());
	}

	public synchronized void addThread(Thread thread)
	{
		policy.addThread(thread);
		objects.put(thread, null);
		durations.put(thread, -1L);

		RTLogger.log(
			"ThreadCreate -> id: " + thread.getId() +
			" period: false " +
			" objref: nil " +
			" clnm: nil " +
			" cpunm: " + cpuNumber +
			" time: " + SystemClock.getWallTime());
	}

	public synchronized void removeThread(Thread thread)
	{
		boolean kickMe = false;

		if (runningThread == thread)
		{
			ObjectValue object = objects.get(thread);

			RTLogger.log(
				"ThreadSwapOut -> id: " + thread.getId() +
				" objref: " + object.objectReference +
				" clnm: \"" + object.type.name.name + "\"" +
				" cpunum: " + cpuNumber +
				" overhead: " + 0 +
				" time: " + SystemClock.getWallTime());

			kickMe = true;
		}

		policy.removeThread(thread);
		objects.remove(thread);
		durations.remove(thread);

		RTLogger.log(
			"ThreadKill -> id: " + thread.getId() +
			" cpunm: " + cpuNumber +
			" time: " + SystemClock.getWallTime());

		if (kickMe)
		{
			kick();
		}
	}

	public synchronized void reschedule()
	{
		Thread current = Thread.currentThread();
		policy.reschedule();
		runningThread = policy.getThread();

		if (runningThread != current)
		{
			notifyAll();

			ObjectValue object = objects.get(current);

			RTLogger.log(
				"ThreadSwapOut -> id: " + current.getId() +
				" objref: " + object.objectReference +
				" clnm: \"" + object.type.name.name + "\"" +
				" cpunum: " + cpuNumber +
				" overhead: " + 0 +
				" time: " + SystemClock.getWallTime());

			sleep();
			switches++;
		}
	}

	public synchronized void sleep()
	{
		Thread current = Thread.currentThread();
		ObjectValue object = objects.get(current);

		while (runningThread != current)
		{
			try
			{
				wait();
			}
			catch (InterruptedException e)
			{
				// So?
			}
		}

		RTLogger.log(
			"ThreadSwapIn -> id: " + current.getId() +
			" objref: " + object.objectReference +
			" clnm: \"" + object.type.name.name + "\"" +
			" cpunum: " + cpuNumber +
			" overhead: " + 0 +
			" time: " + SystemClock.getWallTime());
	}

	public synchronized void setState(Thread thread, RunState newstate)
	{
		policy.setState(thread, newstate);
		kick();
	}

	public synchronized void setState(RunState newstate)
	{
		policy.setState(Thread.currentThread(), newstate);
		kick();
	}

	public synchronized void duration(long step)
	{
		long end = SystemClock.getWallTime() + step;

		policy.setState(Thread.currentThread(), RunState.TIMESTEP);
		durations.put(Thread.currentThread(), step);

		if (step < minTimeStep)
		{
			minTimeStep = step;
		}

		if (policy.canTimeStep())
		{
			SystemClock.timeStep(minTimeStep);
		}

		while (SystemClock.getWallTime() < end)
		{
			reschedule();
		}
	}

	public void timeStep(long step)
	{
		minTimeStep = Long.MAX_VALUE;
		Map<Thread, Long> remaining = new HashMap<Thread, Long>();

		for (Thread th: durations.keySet())
		{
			long duration = durations.get(th);

			if (duration > step)
			{
				long reduced = duration - step;
				remaining.put(th, reduced);

				if (reduced < minTimeStep)
				{
					minTimeStep = reduced;
				}
			}
			else
			{
				policy.setState(th, RunState.RUNNABLE);
			}
		}

		durations = remaining;
	}

	private void kick()
	{
		policy.reschedule();
		runningThread = policy.getThread();
		notifyAll();
	}
}
