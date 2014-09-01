/*
 * #%~
 * org.overture.ide.debug
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
package org.overture.ide.debug.core.model.internal.operations;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.overture.ide.debug.core.VdmDebugPlugin;
import org.overture.ide.debug.core.dbgp.IDbgpStatus;
import org.overture.ide.debug.core.dbgp.commands.IDbgpCommands;
import org.overture.ide.debug.core.dbgp.commands.IDbgpCoreCommands;
import org.overture.ide.debug.core.dbgp.commands.IDbgpExtendedCommands;
import org.overture.ide.debug.core.dbgp.exceptions.DbgpException;
import org.overture.ide.debug.core.dbgp.exceptions.DbgpOpertionCanceledException;
import org.overture.ide.debug.core.dbgp.exceptions.DbgpTimeoutException;
import org.overture.ide.debug.core.model.IVdmThread;

public abstract class DbgpOperation
{
	private static final boolean DEBUG = false;// VdmDebugPlugin.DEBUG;

	public interface IResultHandler
	{
		void finish(IDbgpStatus status, DbgpException e);
	}

	private final Job job;
	private final IDbgpCommands commands;

	protected IDbgpCoreCommands getCore()
	{
		return commands.getCoreCommands();
	}

	protected IDbgpExtendedCommands getExtended()
	{
		return commands.getExtendedCommands();
	}

	private final IResultHandler resultHandler;

	protected void callFinish(IDbgpStatus status)
	{
		if (DEBUG)
		{
			System.out.println("Status: " + status); //$NON-NLS-1$
		}

		resultHandler.finish(status, null);
	}

	protected DbgpOperation(IVdmThread thread, String name,
			IResultHandler handler)
	{
		this.resultHandler = handler;

		this.commands = thread.getDbgpSession();

		job = new Job(name)
		{
			protected IStatus run(IProgressMonitor monitor)
			{
				// TODO: improve
				try
				{
					process();
				} catch (DbgpOpertionCanceledException e)
				{
					// Operation was canceled cause debugger is shutting down
				} catch (DbgpTimeoutException e)
				{
					if (VdmDebugPlugin.DEBUG)
					{
						e.printStackTrace();
					}
					resultHandler.finish(null, e);
				} catch (DbgpException e)
				{
					if (VdmDebugPlugin.DEBUG)
					{
						System.out.println("Exception: " + e.getMessage()); //$NON-NLS-1$
						System.out.println(e.getClass());
						e.printStackTrace();
					}
					resultHandler.finish(null, e);
				}

				return Status.OK_STATUS;
			}
		};
		job.setSystem(true);
		job.setUser(false);
	}

	public void schedule()
	{
		if (DEBUG)
		{
			System.out.println("Starting operation: " + job.getName()); //$NON-NLS-1$
		}

		job.schedule();
	}

	protected abstract void process() throws DbgpException;
}
