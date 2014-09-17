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
package org.overture.ide.debug.core.model.internal;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IDebugElement;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IWatchExpressionDelegate;
import org.eclipse.debug.core.model.IWatchExpressionListener;
import org.overture.ide.debug.core.model.IVdmStackFrame;
import org.overture.ide.debug.core.model.IVdmThread;
import org.overture.ide.debug.core.model.eval.IVdmEvaluationEngine;
import org.overture.ide.debug.core.model.eval.IVdmEvaluationListener;
import org.overture.ide.debug.core.model.eval.IVdmEvaluationResult;

public class VdmWatchExpressionDelegate implements IWatchExpressionDelegate
{
	protected static class ListenerAdpater implements IVdmEvaluationListener
	{
		protected final IWatchExpressionListener listener;

		public ListenerAdpater(IWatchExpressionListener listener)
		{
			this.listener = listener;
		}

		public void evaluationComplete(IVdmEvaluationResult result)
		{
			listener.watchEvaluationFinished(new VdmWatchExpressionResult(result));
		}
	}

	protected static IVdmThread getVdmThread(Object context)
	{
		if (context instanceof IVdmThread)
		{
			return (IVdmThread) context;
		} else if (context instanceof IVdmStackFrame)
		{
			return (IVdmThread) ((IVdmStackFrame) context).getThread();
		}

		return null;
	}

	protected static IVdmStackFrame getStackFrame(IDebugElement context)
	{
		try
		{
			if (context instanceof IVdmThread)
			{
				IStackFrame[] frames = ((IVdmThread) context).getStackFrames();
				if (frames.length > 0)
				{
					return (IVdmStackFrame) frames[0];
				}
			} else if (context instanceof IVdmStackFrame)
			{
				return (IVdmStackFrame) context;
			}
		} catch (DebugException e)
		{
		}

		return null;
	}

	public void evaluateExpression(String expression, IDebugElement context,
			IWatchExpressionListener listener)
	{

		IVdmThread thread = getVdmThread(context);
		IVdmStackFrame frame = getStackFrame(context);
		if (thread != null && frame != null)
		{
			IVdmEvaluationEngine engine = thread.getEvaluationEngine();
			if (engine != null)
			{
				engine.asyncEvaluate(prepareExpression(expression), frame, createListener(listener, expression));
				return;
			}
		}
		listener.watchEvaluationFinished(new NoWatchExpressionResult(expression));
	}

	protected String prepareExpression(String expression)
	{
		return expression;
	}

	protected ListenerAdpater createListener(IWatchExpressionListener listener,
			String expression)
	{
		return new ListenerAdpater(listener);
	}
}
