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

import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.debug.DBGPReader;
import org.overturetool.vdmj.debug.DBGPReason;
import org.overturetool.vdmj.values.CPUValue;
import org.overturetool.vdmj.values.ObjectValue;
import org.overturetool.vdmj.values.OperationValue;
import org.overturetool.vdmj.values.Value;
import org.overturetool.vdmj.values.ValueList;

public class AsyncThread extends Thread
{
	private MessageRequest request = null;

	public final ObjectValue self;
	public final OperationValue operation;
	public final ValueList args;
	public final CPUValue cpu;

	public AsyncThread(MessageRequest request)
	{
		this(request.target, request.operation, request.args);
		this.request = request;
	}

	public AsyncThread(ObjectValue self, OperationValue operation, ValueList args)
	{
		setName("Async Thread " + getId());

		this.self = self;
		this.operation = operation;
		this.args = args;
		this.cpu = self.getCPU();
		this.request = new MessageRequest();

		cpu.addThread(this, self, operation);
	}

	@Override
	public void run()
	{
		cpu.startThread();

		if (Settings.usingDBGP)
		{
			runDBGP();
		}
		else
		{
			runCmd();
		}

		cpu.removeThread();
	}

	private void runDBGP()
	{
		DBGPReader reader = null;

		try
		{
    		MessageResponse response = null;

    		try
    		{
        		RootContext global = ClassInterpreter.getInstance().initialContext;
        		Context ctxt = new ObjectContext(operation.name.location, "async", global, self);
    			reader = ctxt.threadState.dbgp.newThread();
    			ctxt.setThreadState(reader, cpu);

        		Value rv = operation.localEval(args, ctxt, false);
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

			reader.complete(DBGPReason.OK, null);
		}
		catch (ContextException e)
		{
			reader.complete(DBGPReason.EXCEPTION, e);
		}
		catch (RTException e)
		{
			CPUValue.abortAll();	// Thread stopped
		}
		catch (Exception e)
		{
			if (reader != null)
			{
				reader.complete(DBGPReason.EXCEPTION, null);
			}
		}
	}

	private void runCmd()
	{
		try
		{
    		MessageResponse response = null;

    		try
    		{
        		RootContext global = ClassInterpreter.getInstance().initialContext;
        		Context ctxt = new ObjectContext(operation.name.location, "async", global, self);
        		ctxt.setThreadState(null, cpu);

        		Value rv = operation.localEval(args, ctxt, false);
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

	@Override
	public int hashCode()
	{
		return (int)getId();
	}
}
