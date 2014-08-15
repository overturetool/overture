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

import org.eclipse.debug.core.DebugException;
import org.overture.ide.debug.core.model.IVdmThread;
import org.overture.ide.debug.core.model.IVdmValue;
import org.overture.ide.debug.core.model.eval.IVdmEvaluationResult;
import org.overture.ide.debug.utils.CharOperation;

public class VdmEvaluationResult implements IVdmEvaluationResult
{
	private final IVdmThread thread;
	private final String snippet;
	private final IVdmValue value;

	public VdmEvaluationResult(IVdmThread thread, String snippet,
			IVdmValue value)
	{
		this.thread = thread;
		this.value = value;
		this.snippet = snippet;
	}

	public String getSnippet()
	{
		return snippet;
	}

	public IVdmValue getValue()
	{
		return value;
	}

	public IVdmThread getThread()
	{
		return thread;
	}

	public String[] getErrorMessages()
	{
		return CharOperation.NO_STRINGS;
	}

	public DebugException getException()
	{
		return null;
	}

	public boolean hasErrors()
	{
		return false;
	}
}
