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

import org.overture.ide.debug.core.model.IVdmDebugTarget;
import org.overture.ide.debug.core.model.IVdmStackFrame;
import org.overture.ide.debug.core.model.eval.IVdmEvaluationCommand;
import org.overture.ide.debug.core.model.eval.IVdmEvaluationEngine;
import org.overture.ide.debug.core.model.eval.IVdmEvaluationListener;
import org.overture.ide.debug.core.model.eval.IVdmEvaluationResult;

public class VdmEvaluationCommand implements IVdmEvaluationCommand
{
	private final IVdmEvaluationEngine engine;
	private final String snippet;
	private final IVdmStackFrame frame;

	public VdmEvaluationCommand(IVdmEvaluationEngine engine, String snippet,
			IVdmStackFrame frame)
	{
		this.snippet = snippet;
		this.engine = engine;
		this.frame = frame;
	}

	public IVdmDebugTarget getVdmDebugTarget()
	{
		return engine.getVdmDebugTarget();
	}

	public IVdmEvaluationResult syncEvaluate()
	{
		return engine.syncEvaluate(snippet, frame);
	}

	public void asyncEvaluate(IVdmEvaluationListener listener)
	{
		engine.asyncEvaluate(snippet, frame, listener);
	}

	public void dispose()
	{
		engine.dispose();
	}
}
