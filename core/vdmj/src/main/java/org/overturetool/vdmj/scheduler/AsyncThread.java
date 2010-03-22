/*******************************************************************************
 *
 *	Copyright (c) 2010 Fujitsu Services Ltd.
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

package org.overturetool.vdmj.scheduler;

import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.commands.DebuggerReader;
import org.overturetool.vdmj.debug.DBGPReader;
import org.overturetool.vdmj.debug.DBGPReason;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.runtime.ClassInterpreter;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.ContextException;
import org.overturetool.vdmj.runtime.ObjectContext;
import org.overturetool.vdmj.runtime.RootContext;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.values.CPUValue;
import org.overturetool.vdmj.values.ObjectValue;
import org.overturetool.vdmj.values.OperationValue;
import org.overturetool.vdmj.values.TransactionValue;
import org.overturetool.vdmj.values.Value;
import org.overturetool.vdmj.values.ValueList;

public class AsyncThread extends SchedulableThread
{
	private static final long serialVersionUID = 1L;
	public final MessageRequest request;

	public final ObjectValue self;
	public final OperationValue operation;
	public final ValueList args;
	public final CPUValue cpu;
	public final boolean breakAtStart;

	public AsyncThread(MessageRequest request)
	{
		super(
			request.target.getCPU().resource,
			request.target,
			request.operation.getPriority(),
			false, 0);

		setName("AsyncThread-" + getId());

		this.self = request.target;
		this.operation = request.operation;
		this.args = request.args;
		this.cpu = self.getCPU();
		this.breakAtStart = request.breakAtStart;
		this.request = request;
	}

	@Override
	protected void body()
	{
		RootContext global = ClassInterpreter.getInstance().initialContext;
		LexLocation from = self.type.classdef.location;
		Context ctxt = new ObjectContext(from, "async", global, self);

		if (Settings.usingDBGP)
		{
			runDBGP(ctxt);
		}
		else
		{
			runCmd(ctxt);
		}
	}

	private void runDBGP(Context ctxt)
	{
		DBGPReader reader = null;

		try
		{
    		MessageResponse response = null;

    		try
    		{
    			reader = request.dbgp.newThread(cpu);
    			ctxt.setThreadState(reader, cpu);

    			if (breakAtStart)
    			{
    				// Step at the first location you check (start of body)
    				ctxt.threadState.setBreaks(new LexLocation(), null, null);
    			}

        		Value rv = operation.localEval(operation.name.location, args, ctxt, false);
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
			suspendOthers();
			ResourceScheduler.setException(e);
			reader.stopped(e.ctxt, e.location);
		}
		catch (Exception e)
		{
			ResourceScheduler.setException(e);
			SchedulableThread.signalAll(Signal.SUSPEND);
		}
		finally
		{
			TransactionValue.commitAll();
		}
	}

	private void runCmd(Context ctxt)
	{
		try
		{
    		ctxt.setThreadState(null, cpu);

			if (breakAtStart)
			{
				// Step at the first location you check (start of body)
				ctxt.threadState.setBreaks(new LexLocation(), null, null);
			}

    		Value result = operation.localEval(
    			operation.name.location, args, ctxt, false);

			if (request.replyTo != null)
			{
				request.bus.reply(new MessageResponse(result, request));
			}
		}
		catch (ValueException e)
		{
			suspendOthers();
			ResourceScheduler.setException(e);
			DebuggerReader.stopped(e.ctxt, operation.name.location);
		}
		catch (ContextException e)
		{
			suspendOthers();
			ResourceScheduler.setException(e);
			DebuggerReader.stopped(e.ctxt, operation.name.location);
		}
		catch (Exception e)
		{
			ResourceScheduler.setException(e);
			SchedulableThread.signalAll(Signal.SUSPEND);
		}
		finally
		{
			TransactionValue.commitAll();
		}
	}
}
