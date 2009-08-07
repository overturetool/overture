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
	private MessageRequest request = null;

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

	public AsyncThread(ObjectValue target, OperationValue operation,
		MessageRequest request)
	{
		this(target, operation);
		this.request = request;
	}

	@Override
	public void run()
	{
		try
		{
			if (request == null)
			{
				request = queue.take();
				cpu.sleep();
			}

    		if (request.bus != null)
    		{
        		RTLogger.log(
        			"MessageRequest -> busid: " + request.bus.busNumber +
        			" fromcpu: " + request.from.cpuNumber +
        			" tocpu: " + request.to.cpuNumber +
        			" msgid: " + request.msgId +
        			" callthr: " + request.thread.getId() +
        			" opname: " + "\"" + operation.name + "\"" +
        			" objref: " + self.objectReference +
        			" size: " + request.args.toString().length() +
        			" time: " + SystemClock.getWallTime());

        		RTLogger.log(
        			"MessageActivate -> msgid: " + request.msgId +
        			" time: " + SystemClock.getWallTime());

        		if (request.bus.busNumber > 0)
        		{
        			long pause = request.args.toString().length();
        			cpu.duration(pause);
        		}

        		RTLogger.log(
        			"MessageCompleted -> msgid: " + request.msgId +
        			" time: " + SystemClock.getWallTime());
    		}

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
    			RTLogger.log(
    				"ReplyRequest -> busid: " + request.bus.busNumber +
    				" fromcpu: " + response.from.cpuNumber +
    				" tocpu: " + response.to.cpuNumber +
    				" msgid: " + response.msgId +
    				" origmsgid: " + response.request.msgId +
    				" callthr: " + response.request.thread.getId() +
    				" calleethr: " + response.thread.getId() +
    				" size: " + response.toString().length() +
    				" time: " + SystemClock.getWallTime());

    			if (request.bus.busNumber > 0)
    			{
    				long pause = response.result.toString().length();
    				cpu.duration(pause);
    			}

    			RTLogger.log(
    				"MessageActivate -> msgid: " + response.msgId +
    				" time: " + SystemClock.getWallTime());

    			RTLogger.log(
    				"MessageCompleted -> msgid: " + response.msgId +
    				" time: " + SystemClock.getWallTime());

    			request.bus.reply(response);
    		}

    		cpu.removeThread(this);
		}
		catch (ContextException e)
		{
			Interpreter.stop(e, e.ctxt);
		}
		catch (RTException e)
		{
			CPUValue.abortAll();	// Thread stopped
		}
	}

	public void send(MessageRequest req)
	{
		cpu.setState(this, RunState.RUNNABLE);
		queue.add(req);
	}

	@Override
	public int hashCode()
	{
		return (int)getId();
	}
}
