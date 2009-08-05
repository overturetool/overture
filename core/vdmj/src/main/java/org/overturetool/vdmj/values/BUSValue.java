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

import org.overturetool.vdmj.runtime.AsyncThread;
import org.overturetool.vdmj.runtime.BUSPolicy;
import org.overturetool.vdmj.runtime.MessageRequest;
import org.overturetool.vdmj.runtime.MessageResponse;
import org.overturetool.vdmj.runtime.RunState;
import org.overturetool.vdmj.types.ClassType;
import org.overturetool.vdmj.types.Type;

public class BUSValue extends ObjectValue
{
	private static final long serialVersionUID = 1L;
	private static int nextBUS = 1;
	public static List<BUSValue> allBUSSES;

	public final int busNumber;
	public final BUSPolicy policy;
	public final double speed;
	public final ValueSet cpus;

	public String name;

	public static void init()
	{
		nextBUS = 1;
		allBUSSES = new Vector<BUSValue>();
	}

	public BUSValue(Type classtype, NameValuePairMap map, ValueList argvals)
	{
		super((ClassType)classtype, map, new Vector<ObjectValue>());

		this.busNumber = nextBUS++;

		QuoteValue parg = (QuoteValue)argvals.get(0);
		this.policy = BUSPolicy.valueOf(parg.value.toUpperCase());

		RealValue sarg = (RealValue)argvals.get(1);
		this.speed = sarg.value;

		SetValue set = (SetValue)argvals.get(2);
		this.cpus = set.values;

		allBUSSES.add(this);
	}

	public BUSValue(
		int number, Type classtype, NameValuePairMap map, ValueList argvals)
	{
		super((ClassType)classtype, map, new Vector<ObjectValue>());

		this.busNumber = number;

		QuoteValue parg = (QuoteValue)argvals.get(0);
		this.policy = BUSPolicy.valueOf(parg.value.toUpperCase());

		RealValue sarg = (RealValue)argvals.get(1);
		this.speed = sarg.value;

		SetValue set = (SetValue)argvals.get(2);
		this.cpus = set.values;

		allBUSSES.add(this);
	}

	public void send(MessageRequest request, AsyncThread thread)
	{
		thread.send(request);
	}

	public void reply(MessageResponse response)
	{
		response.request.replyTo.add(response);
		response.request.from.wakeUp(response.request.thread, RunState.RUNNABLE);
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

	public String declString() throws Exception
	{
		ValueSet set = new ValueSet();

		for (Value v: cpus)
		{
			CPUValue cpu = (CPUValue)v.deref();
			set.add(new NaturalValue(cpu.cpuNumber));
		}

		return
			"BUSdecl -> id: " + busNumber +
			" topo: " + cpus +
			" name: \"" + name + "\"";
	}
}
