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

package org.overture.interpreter.values;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.overture.ast.types.AClassType;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.ContextException;
import org.overture.interpreter.scheduler.BUSResource;
import org.overture.interpreter.scheduler.BusThread;
import org.overture.interpreter.scheduler.CPUResource;
import org.overture.interpreter.scheduler.FCFSPolicy;
import org.overture.interpreter.scheduler.MessageRequest;
import org.overture.interpreter.scheduler.MessageResponse;
import org.overture.interpreter.scheduler.ResourceScheduler;
import org.overture.interpreter.scheduler.SchedulingPolicy;

public class BUSValue extends ObjectValue
{
	private static final long serialVersionUID = 1L;
	private static List<BUSValue> busses = new LinkedList<BUSValue>();
	private static BUSValue[][] cpumap = null;

	public static BUSValue vBUS = null;
	public final BUSResource resource;

	public BUSValue(AClassType classtype, NameValuePairMap map,
			ValueList argvals)
	{
		super(classtype, map, new ArrayList<ObjectValue>(), null, null);

		QuoteValue parg = (QuoteValue) argvals.get(0);
		SchedulingPolicy policy = SchedulingPolicy.factory(parg.value.toUpperCase());

		RealValue sarg = (RealValue) argvals.get(1);
		double speed = sarg.value;

		SetValue set = (SetValue) argvals.get(2);
		List<CPUResource> cpulist = new ArrayList<CPUResource>();

		for (Value v : set.values)
		{
			CPUValue cpuv = (CPUValue) v.deref();
			cpulist.add(cpuv.resource);
		}

		resource = new BUSResource(false, policy, speed, cpulist);
		busses.add(this);
	}

	public BUSValue(AClassType type, ValueSet cpus)
	{
		super(type, new NameValuePairMap(), new ArrayList<ObjectValue>(), null, null);
		List<CPUResource> cpulist = new ArrayList<CPUResource>();

		for (Value v : cpus)
		{
			CPUValue cpuv = (CPUValue) v.deref();
			cpulist.add(cpuv.resource);
		}

		resource = new BUSResource(true, new FCFSPolicy(), 0, cpulist);
		vBUS = this;
		busses.add(this);
	}

	public void setup(ResourceScheduler scheduler, String name)
	{
		resource.setName(name);
		scheduler.register(resource);
	}

	public void transmit(MessageRequest request)
	{
		resource.transmit(request);
	}

	public void reply(MessageResponse response)
	{
		resource.reply(response);
	}

	@Override
	public String toString()
	{
		return resource.toString();
	}

	public boolean isVirtual()
	{
		return resource.isVirtual();
	}

	public static void init()
	{
		BUSResource.init();
		busses.clear();
		// Can't create the vBUS until we know all the CPUs.
	}

	public static void start()
	{
		for (BUSValue bus : busses)
		{
			new BusThread(bus.resource, 0).start();
		}
	}

	public String getName()
	{
		return resource.getName();
	}

	public int getNumber()
	{
		return resource.getNumber();
	}

	public static void createMap(Context ctxt, ValueSet allCPUs)
	{
		int max = allCPUs.size() + 1; // vCPU missing
		cpumap = new BUSValue[max][max];

		for (int i = 0; i < max; i++)
		{
			cpumap[i][0] = vBUS;
			cpumap[0][i] = vBUS;
		}

		for (Value fv : allCPUs)
		{
			CPUValue from = (CPUValue) fv;

			for (Value tv : allCPUs)
			{
				CPUValue to = (CPUValue) tv;

				if (from == to)
				{
					continue;
				}

				BUSValue bus = findRealBUS(from, to);

				if (bus == null)
				{
					continue; // May be OK - separated island CPUs
				}

				int nf = from.getNumber();
				int nt = to.getNumber();

				if (cpumap[nf][nt] == null)
				{
					cpumap[nf][nt] = bus;
				} else if (cpumap[nf][nt] != bus)
				{
					throw new ContextException(4139, "CPUs " + from.getName()
							+ " and " + to.getName() + " connected by "
							+ bus.getName() + " and "
							+ cpumap[nf][nt].getName(), ctxt.location, ctxt);
				}
			}
		}
	}

	private static BUSValue findRealBUS(CPUValue from, CPUValue to)
	{
		for (BUSValue bus : busses)
		{
			if (bus != vBUS && bus.resource.links(from.resource, to.resource))
			{
				return bus;
			}
		}

		return null;
	}

	public static BUSValue lookupBUS(CPUValue from, CPUValue to)
	{
		return cpumap[from.getNumber()][to.getNumber()];
	}
}
