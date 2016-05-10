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

import java.util.List;
import java.util.Vector;

import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.factory.AstFactoryTC;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.types.AClassType;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.scheduler.CPUResource;
import org.overture.interpreter.scheduler.FCFSPolicy;
import org.overture.interpreter.scheduler.ResourceScheduler;
import org.overture.interpreter.scheduler.SchedulingPolicy;

public class CPUValue extends ObjectValue
{
	private static final long serialVersionUID = 1L;
	public final CPUResource resource;
	private final List<ObjectValue> deployed;
	public static CPUValue vCPU;

	public CPUValue(AClassType aClassType, NameValuePairMap map,
			ValueList argvals)
	{
		super(aClassType, map, new Vector<>(), null, null);

		QuoteValue parg = (QuoteValue) argvals.get(0);
		SchedulingPolicy cpup = SchedulingPolicy.factory(parg.value.toUpperCase());
		RealValue sarg = (RealValue) argvals.get(1);

		resource = new CPUResource(cpup, sarg.value);
		deployed = new Vector<>();

	}

	public CPUValue(AClassType classtype) // for virtual CPUs
	{
		super(classtype, new NameValuePairMap(), new Vector<>(), null, null);
		resource = new CPUResource(new FCFSPolicy(), 0);
		deployed = new Vector<>();
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

		for (ObjectValue obj : deployed)
		{
			for (ILexNameToken m : obj.members.keySet())
			{
				// Set priority for all overloads of opname

				if (m.getExplicit(true).getFullName().equals(opname))
				{
					OperationValue op = (OperationValue) obj.members.get(m);
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
	
	transient static AClassType cpuType = null;

	public static void init(ResourceScheduler scheduler,
			IInterpreterAssistantFactory assistantFactory)
	{
		try
		{
			CPUResource.init();
			if(cpuType==null)
			{
				SClassDefinition cpu = AstFactoryTC.newACpuClassDefinition(assistantFactory);
				cpuType = (AClassType) assistantFactory.createSClassDefinitionAssistant().getType(cpu);
			}
			vCPU = new CPUValue(cpuType);
			vCPU.setup(scheduler, "vCPU");
		} catch (Exception e)
		{
			// Parse/lex of built-in ops. Can't happen.
		}
	}
}
