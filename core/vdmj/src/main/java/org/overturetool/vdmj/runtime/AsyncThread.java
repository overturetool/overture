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

import org.overturetool.vdmj.messages.RTLogger;
import org.overturetool.vdmj.values.CPUValue;
import org.overturetool.vdmj.values.ObjectValue;
import org.overturetool.vdmj.values.OperationValue;
import org.overturetool.vdmj.values.Value;
import org.overturetool.vdmj.values.ValueList;

public class AsyncThread extends Thread
{
	private MessageQueue<MessageRequest> queue;
	public final ObjectValue self;
	public final OperationValue operation;
	public final CPUValue cpu;

	public AsyncThread(ObjectValue self, OperationValue operation)
	{
		setName("Async Thread " + getId());

		this.self = self;
		this.operation = operation;
		this.cpu = self.getCPU();
		this.queue = new MessageQueue<MessageRequest>(cpu);

		cpu.addThread(this, self);

		RTLogger.log(
			"ThreadCreate -> id: " + getId() +
			" period: " + false +
			" objref: " + self.objectReference +
			" clnm: " + self.type.name +
			" cpunm: " + cpu.cpuNumber +
			" time: " + VDMThreadSet.getWallTime());
	}

	@Override
	public void run()
	{
		cpu.reschedule(RunState.WAITING);

		MessageRequest request = queue.take(self);		// Blocking on CPU
		ValueList arglist = request.args;
		MessageResponse response = null;

		try
		{
    		RootContext global = ClassInterpreter.getInstance().initialContext;
    		Context ctxt = new ObjectContext(operation.name.location, "async", global, self);
    		ctxt.setThreadState(null, cpu);

    		Value rv = operation.localEval(arglist, ctxt);

    		if (!rv.isVoid())
    		{
    			response = new MessageResponse(rv, request);
    		}
		}
		catch (ValueException e)
		{
			response = new MessageResponse(e, request);
		}

		if (response != null && request.bus != null)
		{
			request.bus.reply(response);
		}

		RTLogger.log(
			"ThreadKill -> id: " + Thread.currentThread().getId() +
			" cpunm: " + cpu.cpuNumber +
			" time: " + VDMThreadSet.getWallTime());

		cpu.removeThread(this);
	}

	public void send(MessageRequest request)
	{
		queue.add(request);
		cpu.setState(this, RunState.RUNNABLE);
	}

	@Override
	public int hashCode()
	{
		return (int)getId();
	}
}
