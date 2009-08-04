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
		this.queue = new MessageQueue<MessageRequest>();

		cpu.addThread(this, self, operation);
	}

	@Override
	public void run()
	{
		MessageRequest request = queue.take();
		cpu.sleep();

		ValueList arglist = request.args;
		MessageResponse response = null;

		try
		{
    		RootContext global = ClassInterpreter.getInstance().initialContext;
    		Context ctxt = new ObjectContext(operation.name.location, "async", global, self);
    		ctxt.setThreadState(null, cpu);

    		Value rv = operation.localEval(arglist, ctxt);
   			response = new MessageResponse(rv, request);
		}
		catch (ValueException e)
		{
			response = new MessageResponse(e, request);
		}

		if (request.replyTo != null)
		{
			request.bus.reply(response);
		}

		cpu.removeThread(this);
	}

	public void send(MessageRequest request)
	{
		cpu.setState(this, RunState.RUNNABLE);
		queue.add(request);
	}

	@Override
	public int hashCode()
	{
		return (int)getId();
	}
}
