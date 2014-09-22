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
package org.overture.ide.debug.core.model.internal.eval;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osgi.util.NLS;
import org.overture.ide.debug.core.dbgp.IDbgpProperty;
import org.overture.ide.debug.core.dbgp.IDbgpSession;
import org.overture.ide.debug.core.dbgp.commands.IDbgpExtendedCommands;
import org.overture.ide.debug.core.model.IVdmDebugTarget;
import org.overture.ide.debug.core.model.IVdmStackFrame;
import org.overture.ide.debug.core.model.IVdmThread;
import org.overture.ide.debug.core.model.IVdmValue;
import org.overture.ide.debug.core.model.eval.IVdmEvaluationEngine;
import org.overture.ide.debug.core.model.eval.IVdmEvaluationListener;
import org.overture.ide.debug.core.model.eval.IVdmEvaluationResult;
import org.overture.ide.debug.core.model.internal.VdmDebugTarget;
import org.overture.ide.debug.core.model.internal.VdmValue;

public class VdmEvaluationEngine implements IVdmEvaluationEngine
{
	private final IVdmThread thread;

	// private int count;
	private final/* WeakHashMap */Object cache;

	protected void putToCache(String snippet, IVdmEvaluationResult result)
	{
		// if (result != null) {
		// cache.put(snippet, result);
		// }
	}

	protected IVdmEvaluationResult getFromCache(String snippet)
	{
		return null;
		// int newCount = thread.getModificationsCount();
		// if (count != newCount) {
		// cache.clear();
		// count = newCount;
		// return null;
		// }
		//
		// return (IVdmEvaluationResult) cache.get(snippet);
	}

	private IVdmEvaluationResult evaluate(String snippet, IVdmStackFrame frame)
	{
		IVdmEvaluationResult result = null;
		try
		{
			final IDbgpSession session = thread.getDbgpSession();

			final IDbgpExtendedCommands extended = session.getExtendedCommands();

			final IDbgpProperty property = extended.evaluate(snippet);

			if (property != null)
			{
				IVdmValue value = VdmValue.createValue(frame, property);
				result = new VdmEvaluationResult(thread, snippet, value);
			} else
			{
				result = new FailedVdmEvaluationResult(thread, snippet, new String[] { "VdmEvaluationEngine_cantEvaluate" });
			}

		} catch (Exception e)
		{
			// TODO: improve
			result = new FailedVdmEvaluationResult(thread, snippet, new String[] { e.getMessage() });
		}

		return result;
	}

	public VdmEvaluationEngine(IVdmThread thread)
	{
		this.thread = thread;
		// this.count = thread.getModificationsCount();
		this.cache = new Object();// new WeakHashMap();
	}

	public IVdmDebugTarget getVdmDebugTarget()
	{
		return (VdmDebugTarget) thread.getDebugTarget();
	}

	public IVdmEvaluationResult syncEvaluate(String snippet,
			IVdmStackFrame frame)
	{
		snippet = snippet.trim();
		synchronized (cache)
		{
			IVdmEvaluationResult result = getFromCache(snippet);

			if (result == null)
			{
				result = evaluate(snippet, frame);
			}

			putToCache(snippet, result);

			return result;
		}
	}

	public void asyncEvaluate(final String snippet, final IVdmStackFrame frame,
			final IVdmEvaluationListener listener)
	{
		Job job = new Job(NLS.bind("VdmEvaluationEngine_evaluationOf", snippet))
		{
			protected IStatus run(IProgressMonitor monitor)
			{
				if (getVdmDebugTarget().isTerminated())
				{
					listener.evaluationComplete(new NoEvaluationResult(snippet, thread));
				} else
				{
					listener.evaluationComplete(syncEvaluate(snippet, frame));
				}
				return Status.OK_STATUS;
			}
		};

		job.setSystem(true);
		job.setUser(false);
		job.schedule();
	}

	public void dispose()
	{
		// TODO Auto-generated method stub
	}
}
