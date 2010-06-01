/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
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

public class VdmWatchExpressionDelegate implements IWatchExpressionDelegate {
	protected static class ListenerAdpater implements IVdmEvaluationListener {
		protected final IWatchExpressionListener listener;

		public ListenerAdpater(IWatchExpressionListener listener) {
			this.listener = listener;
		}

		public void evaluationComplete(IVdmEvaluationResult result) {
			listener.watchEvaluationFinished(new VdmWatchExpressionResult(
					result));
		}
	}

	protected static IVdmThread getVdmThread(Object context) {
		if (context instanceof IVdmThread) {
			return (IVdmThread) context;
		} else if (context instanceof IVdmStackFrame) {
			return (IVdmThread) ((IVdmStackFrame) context).getThread();
		}

		return null;
	}

	protected static IVdmStackFrame getStackFrame(IDebugElement context) {
		try {
			if (context instanceof IVdmThread) {
				IStackFrame[] frames = ((IVdmThread) context)
						.getStackFrames();
				if (frames.length > 0)
					return (IVdmStackFrame) frames[0];
			} else if (context instanceof IVdmStackFrame) {
				return (IVdmStackFrame) context;
			}
		} catch (DebugException e) {
		}

		return null;
	}

	public void evaluateExpression(String expression, IDebugElement context,
			IWatchExpressionListener listener) {

		IVdmThread thread = getVdmThread(context);
		IVdmStackFrame frame = getStackFrame(context);
		if (thread != null && frame != null) {
			IVdmEvaluationEngine engine = thread.getEvaluationEngine();
			if (engine != null) {
				engine.asyncEvaluate(prepareExpression(expression), frame,
						createListener(listener, expression));
				return;
			}
		}
		listener
				.watchEvaluationFinished(new NoWatchExpressionResult(expression));
	}

	protected String prepareExpression(String expression) {
		return expression;
	}

	protected ListenerAdpater createListener(IWatchExpressionListener listener,
			String expression) {
		return new ListenerAdpater(listener);
	}
}
