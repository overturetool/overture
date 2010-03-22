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

import java.util.List;
import java.util.Vector;

import org.overturetool.vdmj.definitions.CPUClassDefinition;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.scheduler.FCFSPolicy;
import org.overturetool.vdmj.scheduler.ResourceScheduler;
import org.overturetool.vdmj.scheduler.SchedulingPolicy;
import org.overturetool.vdmj.scheduler.CPUResource;
import org.overturetool.vdmj.types.ClassType;

public class CPUValue extends ObjectValue
{
	private static final long serialVersionUID = 1L;
	public final CPUResource resource;
	private final List<ObjectValue> deployed;
	public static CPUValue vCPU;

	public CPUValue(ClassType classtype, NameValuePairMap map, ValueList argvals)
	{
		super(classtype, map, new Vector<ObjectValue>(), null);

		QuoteValue parg = (QuoteValue)argvals.get(0);
		SchedulingPolicy cpup = SchedulingPolicy.factory(parg.value.toUpperCase());
		RealValue sarg = (RealValue)argvals.get(1);

		resource = new CPUResource(cpup, sarg.value);
		deployed = new Vector<ObjectValue>();
	}

	public CPUValue(ClassType classtype)	// for virtual CPUs
	{
		super(classtype, new NameValuePairMap(), new Vector<ObjectValue>(), null);
		resource = new CPUResource(new FCFSPolicy(), 0);
		deployed = new Vector<ObjectValue>();
	}

	public void setup(ResourceScheduler scheduler, String name)
	{
		scheduler.register(resource);
		resource.setName(name);
	}

	public void deploy(ObjectValue obj)
	{
		resource.deploy(obj);
		deployed.add(obj);
	}

	public void setPriority(String opname, long priority) throws Exception
	{
		if (!resource.policy.hasPriorities())
		{
			throw new Exception("CPUs policy does not support priorities");
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

		if (!found)
		{
			throw new Exception("Operation name not found");
		}
	}

	public long getDuration(long cycles)
	{
		return resource.getCyclesDuration(cycles);
	}

	@Override
	public String toString()
	{
		return resource.toString();
	}

	public String getName()
	{
		return resource.getName();
	}

	public int getNumber()
	{
		return resource.getNumber();
	}

	public boolean isVirtual()
	{
		return resource.isVirtual();
	}

	public static void init(ResourceScheduler scheduler)
	{
		try
		{
			CPUResource.init();
			CPUClassDefinition def = new CPUClassDefinition();
			vCPU = new CPUValue((ClassType)def.getType());
			vCPU.setup(scheduler, "vCPU");
		}
		catch (Exception e)
		{
			// Parse/lex of built-in ops. Can't happen.
		}
	}
}
