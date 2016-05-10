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

package org.overture.interpreter.scheduler;

import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.lex.LexLocation;
import org.overture.config.Settings;
import org.overture.interpreter.commands.DebuggerReader;
import org.overture.interpreter.debug.DBGPReader;
import org.overture.interpreter.debug.DBGPReason;
import org.overture.interpreter.runtime.ClassInterpreter;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.ContextException;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.runtime.RootContext;
import org.overture.interpreter.runtime.ValueException;
import org.overture.interpreter.values.CPUValue;
import org.overture.interpreter.values.ObjectValue;
import org.overture.interpreter.values.OperationValue;
import org.overture.interpreter.values.TransactionValue;
import org.overture.interpreter.values.Value;
import org.overture.interpreter.values.ValueList;

public class AsyncThread extends SchedulablePoolThread
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
		super(request.target.getCPU().resource, request.target, request.operation.getPriority(), false, 0);

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
		ILexLocation from = self.type.getClassdef().getLocation();
		Context ctxt = new ObjectContext(global.assistantFactory, from, "async", global, self);

		if (Settings.usingDBGP)
		{
			DBGPReader reader = ctxt.threadState.dbgp.newThread(cpu);
			ctxt.setThreadState(reader, cpu);
			runDBGP(ctxt);
		} else
		{
			ctxt.setThreadState(null, cpu);
			runCmd(ctxt);
		}
	}

	private void runDBGP(Context ctxt)
	{
		try
		{
			MessageResponse response;

			try
			{
				if (breakAtStart)
				{
					// Step at the first location you check (start of body)
					ctxt.threadState.setBreaks(new LexLocation(), null, null);
				}

				Value rv = operation.localEval(operation.name.getLocation(), args, ctxt, false);
				response = new MessageResponse(rv, request);

				ctxt.threadState.dbgp.complete(DBGPReason.OK, null);
			} catch (ValueException e)
			{
				ctxt.threadState.dbgp.complete(DBGPReason.OK, new ContextException(e, operation.name.getLocation()));

				response = new MessageResponse(e, request);
			}

			if (request.replyTo != null)
			{
				request.bus.reply(response);
			}

		} catch (ContextException e)
		{
			suspendOthers();
			ResourceScheduler.setException(e);
			ctxt.threadState.dbgp.stopped(e.ctxt, e.location);

		} catch (Exception e)
		{
			ResourceScheduler.setException(e);
			BasicSchedulableThread.signalAll(Signal.SUSPEND);
		} finally
		{
			TransactionValue.commitAll();
		}
	}

	private void runCmd(Context ctxt)
	{
		try
		{
			if (breakAtStart)
			{
				// Step at the first location you check (start of body)
				ctxt.threadState.setBreaks(new LexLocation(), null, null);
			}

			Value result = operation.localEval(operation.name.getLocation(), args, ctxt, false);

			if (request.replyTo != null)
			{
				request.bus.reply(new MessageResponse(result, request));
			}
		} catch (ValueException e)
		{
			suspendOthers();
			ResourceScheduler.setException(e);
			DebuggerReader.stopped(e.ctxt, operation.name.getLocation());
		} catch (ContextException e)
		{
			suspendOthers();
			ResourceScheduler.setException(e);
			DebuggerReader.stopped(e.ctxt, operation.name.getLocation());
		} catch (Exception e)
		{
			ResourceScheduler.setException(e);
			BasicSchedulableThread.signalAll(Signal.SUSPEND);
		} finally
		{
			TransactionValue.commitAll();
		}
	}

}
