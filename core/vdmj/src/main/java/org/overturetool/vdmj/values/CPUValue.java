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

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.runtime.AsyncThread;
import org.overturetool.vdmj.runtime.CPUPolicy;
import org.overturetool.vdmj.types.ClassType;
import org.overturetool.vdmj.types.Type;

public class CPUValue extends ObjectValue
{
	private static final long serialVersionUID = 1L;
	private static int nextCPU = 1;
	public static List<CPUValue> allCPUs;

	public final int cpuNumber;
	public final CPUPolicy policy;
	public final double cyclesPerSec;
	public final List<ObjectValue> deployed;
	public final Set<AsyncThread> threads;

	public String name;
	private boolean active = false;

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
		this.policy = CPUPolicy.valueOf(parg.value.toUpperCase());

		RealValue sarg = (RealValue)argvals.get(1);
		this.cyclesPerSec = sarg.value;

		deployed = new Vector<ObjectValue>();
		threads = new HashSet<AsyncThread>();

		allCPUs.add(this);
	}

	public CPUValue(
		int number, Type classtype, NameValuePairMap map, ValueList argvals)
	{
		super((ClassType)classtype, map, new Vector<ObjectValue>());

		this.cpuNumber = number;

		QuoteValue parg = (QuoteValue)argvals.get(0);
		this.policy = CPUPolicy.valueOf(parg.value.toUpperCase());

		RealValue sarg = (RealValue)argvals.get(1);
		this.cyclesPerSec = sarg.value;

		deployed = new Vector<ObjectValue>();
		threads = new HashSet<AsyncThread>();

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

	public synchronized void activate()
	{
		while (active)
		{
			try
			{
				wait();
			}
			catch (InterruptedException e)
			{
				// continue;
			}
		}

		active = true;
		notify();	// For any yielders
	}

	public synchronized void passivate()
	{
		active = false;
		notify();
	}

	public synchronized void yield()
	{
		passivate();

		while (!active && threads.size() > 1)	// Wait for someone else...
		{
			try
			{
				wait();
			}
			catch (InterruptedException e)
			{
				// continue;
			}
		}

		activate();
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

	public synchronized void addThread(AsyncThread thread)
	{
		threads.add(thread);
	}

	public synchronized void removeThread(AsyncThread thread)
	{
		threads.remove(thread);
		notify();	// Yield may be waiting for no-one, otherwise
	}
}
