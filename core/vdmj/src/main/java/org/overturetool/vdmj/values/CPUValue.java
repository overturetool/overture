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

import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.definitions.CPUClassDefinition;
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.messages.RTLogger;
import org.overturetool.vdmj.runtime.CPUPolicy;
import org.overturetool.vdmj.runtime.RTException;
import org.overturetool.vdmj.runtime.RunState;
import org.overturetool.vdmj.runtime.SchedulingPolicy;
import org.overturetool.vdmj.runtime.FPPolicy;
import org.overturetool.vdmj.runtime.SystemClock;
import org.overturetool.vdmj.types.ClassType;
import org.overturetool.vdmj.types.Type;

public class CPUValue extends ObjectValue
{
	private static final long serialVersionUID = 1L;
	public static int nextCPU = 1;
	public static List<CPUValue> allCPUs = new Vector<CPUValue>();

	public final int cpuNumber;
	public final SchedulingPolicy policy;
	public final double cyclesPerSec;
	public final List<ObjectValue> deployed;
	public final Map<Thread, ObjectValue> objects;

	public String name;
	public Thread runningThread;
	public int switches;

	public static void init()
	{
		nextCPU = 1;
		allCPUs.clear();
	}

	public static void abortAll()
	{
		for (CPUValue cpu: allCPUs)
		{
			cpu.abort();
		}
	}

	private void abort()
	{
		for (Thread th: objects.keySet())
		{
			th.interrupt();
		}
	}

	public static void resetAll()
	{
		if (Settings.dialect == Dialect.VDM_RT)
		{
			Thread.interrupted();		// Clears interrupted flag

    		for (CPUValue cpu: allCPUs)
    		{
    			cpu.reset();
    		}

    		SystemClock.reset();

    		// Show the main thread creation...

    		CPUValue vCPU = CPUClassDefinition.virtualCPU;
    		Thread main = Thread.currentThread();

    		vCPU.addThread(main);

    		RTLogger.log(
    			"ThreadSwapIn -> id: " + main.getId() +
    			" objref: nil" +
    			" clnm: nil" +
    			" cpunm: 0" +
    			" overhead: 0" +
    			" time: 0");

    		vCPU.policy.setState(main, RunState.RUNNABLE);
    		SystemClock.cpuRunning(vCPU.cpuNumber, true);
		}
	}

	private void reset()
	{
		policy.reset();
		objects.clear();

		runningThread = null;
		switches = 0;
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
		this.switches = 0;

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
		this.switches = 0;

		allCPUs.add(this);
	}

	public void addDeployed(ObjectValue obj)
	{
		deployed.add(obj);
	}

	public boolean setPriority(String opname, long priority)
	{
		if (!(policy instanceof FPPolicy))
		{
			return false;
		}

		boolean found = false;

		for (ObjectValue obj: deployed)
		{
			for (LexNameToken m: obj.members.keySet())
			{
				// Set priority for all overloads of opname

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
		return name == null ? ("CPU:" + cpuNumber) : name;
	}

	public String declString(String sysname, boolean explicit)
	{
		return
			"CPUdecl -> id: " + cpuNumber +
			" expl: " + explicit +
			" sys: \"" + sysname + "\"" +
			" name: \"" + name + "\"";
	}

	private String objRefString(ObjectValue object)
	{
		return
			" objref: " + (object == null ? "nil" : object.objectReference) +
			" clnm: " + (object == null ? "nil" : ("\"" + object.type + "\""));
	}

	public synchronized void addThread(
		Thread thread, ObjectValue object, OperationValue op, boolean periodic)
	{
		policy.addThread(thread, op.getPriority());
		objects.put(thread, object);

		RTLogger.log(
			"ThreadCreate -> id: " + thread.getId() +
			" period: " + periodic +
			objRefString(object) +
			" cpunm: " + cpuNumber +
			" time: " + SystemClock.getWallTime());
	}

	private void addThread(Thread thread)
	{
		policy.addThread(thread, 0);	// Default priority
		objects.put(thread, null);

		RTLogger.log(
			"ThreadCreate -> id: " + thread.getId() +
			" period: false " +
			objRefString(null) +
			" cpunm: " + cpuNumber +
			" time: " + SystemClock.getWallTime());
	}

	public synchronized void removeThread()
	{
		Thread current = Thread.currentThread();
		ObjectValue object = objects.get(current);

		RTLogger.log(
			"ThreadSwapOut -> id: " + current.getId() +
			objRefString(object) +
			" cpunm: " + cpuNumber +
			" overhead: " + 0 +
			" time: " + SystemClock.getWallTime());

		policy.removeThread(current);
		objects.remove(current);

		RTLogger.log(
			"ThreadKill -> id: " + current.getId() +
			" cpunm: " + cpuNumber +
			" time: " + SystemClock.getWallTime());

		policy.reschedule();
		runningThread = policy.getThread();
		notifyAll();
	}

	public void duration(long step)		// NB. Not synchronized
    {
		long end = SystemClock.getWallTime() + step;
		SystemClock.cpuRunning(cpuNumber, false);

		do
    	{
    		SystemClock.timeStep(cpuNumber, step);
    		step = end - SystemClock.getWallTime();
    	}
		while (step > 0);

    	SystemClock.cpuRunning(cpuNumber, true);
    }

	public synchronized void yield(Thread thread, RunState newstate)
	{
		policy.setState(thread, newstate);
		yield();
	}

	public synchronized void yield(RunState newstate)
	{
		policy.setState(Thread.currentThread(), newstate);
		yield();
	}

	public synchronized long reschedule()
	{
		yield();
		return policy.getTimeslice();
	}

	public synchronized void yield()
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
    			objRefString(object) +
    			" cpunm: " + cpuNumber +
    			" overhead: " + 0 +
    			" time: " + SystemClock.getWallTime());

    		boolean idle = (runningThread == null);

    		if (idle)
    		{
    			SystemClock.cpuRunning(cpuNumber, false);
    		}

    		while (runningThread != current)
    		{
    			try
    			{
    				wait();
    			}
    			catch (InterruptedException e)
    			{
    				throw new RTException("Thread stopped");
    			}
    		}

    		if (idle)	// ie. was idle
    		{
    			SystemClock.cpuRunning(cpuNumber, true);
    		}

    		switches++;

    		RTLogger.log(
    			"ThreadSwapIn -> id: " + current.getId() +
    			objRefString(object) +
    			" cpunm: " + cpuNumber +
    			" overhead: " + 0 +
    			" time: " + SystemClock.getWallTime());
		}
	}

	public synchronized void startThread()
	{
		Thread current = Thread.currentThread();
		policy.setState(current, RunState.RUNNABLE);
		wakeUp();

    	while (runningThread != current)
    	{
    		try
    		{
    			wait();
    		}
    		catch (InterruptedException e)
    		{
    			throw new RTException("Thread stopped");
    		}
    	}

		ObjectValue object = objects.get(current);
    	switches++;

    	RTLogger.log(
    		"ThreadSwapIn -> id: " + current.getId() +
    		objRefString(object) +
    		" cpunm: " + cpuNumber +
    		" overhead: " + 0 +
    		" time: " + SystemClock.getWallTime());

    	SystemClock.cpuRunning(cpuNumber, true);
    }

	public void setState(Thread thread, RunState newstate)
	{
		policy.setState(thread, newstate);
	}

	public synchronized void wakeUp()	// Wake up idle CPUs
	{
		if (runningThread == null)
		{
			policy.reschedule();
			runningThread = policy.getThread();
			notifyAll();
		}
	}
}
