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

import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.messages.RTLogger;
import org.overturetool.vdmj.runtime.AsyncThread;
import org.overturetool.vdmj.runtime.BUSPolicy;
import org.overturetool.vdmj.runtime.ControlQueue;
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
	public ControlQueue cq;

	public static void init()
	{
		nextBUS = 1;
		allBUSSES = new Vector<BUSValue>();
	}

	public static void resetAll()
	{
		if (Settings.dialect == Dialect.VDM_RT)
		{
    		for (BUSValue bus: allBUSSES)
    		{
    			bus.reset();
    		}
		}
	}

	public void reset()
	{
		cq = new ControlQueue();
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

	public void transmit(MessageRequest request)
	{
		cq.join(request.from);

		RTLogger.log(
			"MessageRequest -> busid: " + request.bus.busNumber +
			" fromcpu: " + request.from.cpuNumber +
			" tocpu: " + request.to.cpuNumber +
			" msgid: " + request.msgId +
			" callthr: " + request.thread.getId() +
			" opname: " + "\"" + request.operation.name + "\"" +
			" objref: " + request.target.objectReference +
			" size: " + request.getSize());

		RTLogger.log(
			"MessageActivate -> msgid: " + request.msgId);

		if (request.bus.busNumber > 0)
		{
			long pause = request.args.toString().length();
			request.from.duration(pause);
		}

		RTLogger.log(
			"MessageCompleted -> msgid: " + request.msgId);

		AsyncThread thread = new AsyncThread(request);
		thread.start();

		cq.leave();
	}

	public void reply(MessageResponse response)
	{
		cq.join(response.from);

		RTLogger.log(
			"ReplyRequest -> busid: " + response.bus.busNumber +
			" fromcpu: " + response.from.cpuNumber +
			" tocpu: " + response.to.cpuNumber +
			" msgid: " + response.msgId +
			" origmsgid: " + response.originalId +
			" callthr: " + response.caller.getId() +
			" calleethr: " + response.thread.getId() +
			" size: " + response.getSize());

		RTLogger.log(
			"MessageActivate -> msgid: " + response.msgId);

		if (response.bus.busNumber > 0)
		{
			long pause = response.getSize();
			response.from.duration(pause);
		}

		RTLogger.log(
			"MessageCompleted -> msgid: " + response.msgId);

		response.replyTo.set(response);
		response.to.setState(response.caller, RunState.RUNNABLE);
		response.to.wakeUp();

		cq.leave();
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
			" topo: " + set +
			" name: \"" + name + "\"";
	}
}
